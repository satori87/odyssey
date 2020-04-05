package com.bg.ody.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.bg.ody.shared.Shared;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Queue;
import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.net.packets.PingPacket;
import com.bg.ody.shared.MapData;
import com.bg.ody.shared.MonsterData;
import com.bg.ody.shared.PMap;
import com.bg.ody.shared.Registrar.AdminCommand;
import com.bg.ody.shared.Registrar.ChangeDirection;
import com.bg.ody.shared.Registrar.ChangeDoor;
import com.bg.ody.shared.Registrar.Chunk;
import com.bg.ody.shared.Registrar.DiscardMap;
import com.bg.ody.shared.Registrar.Exit;
import com.bg.ody.shared.Registrar.JoinGame;
import com.bg.ody.shared.Registrar.MapReceived;
import com.bg.ody.shared.Registrar.MonsterReceived;
import com.bg.ody.shared.Registrar.Move;
import com.bg.ody.shared.Registrar.PlayerData;
import com.bg.ody.shared.Registrar.PlayerJoined;
import com.bg.ody.shared.Registrar.PlayerParted;
import com.bg.ody.shared.Registrar.PlayerSync;
import com.bg.ody.shared.Registrar.SendChat;
import com.bg.ody.shared.Registrar.SyncDirection;

public class Player extends Mobile {

	public boolean joined = false;

	boolean unlock = false;

	byte[] updateBytes = new byte[0];
	public Queue<Integer> mapQueue = new Queue<Integer>();
	int curUpdateMap = 0;
	boolean updating = false;
	byte[][] chunks = new byte[1][1];

	public int strength = 0;
	public int dexterity = 0;
	public int constitution = 0;
	public int intelligence = 0;
	public int wisdom = 0;

	public Player(Game game) {
		super(game);
	}

	public void part() {
		if (map().editor == this) {
			map().editor = null;
		}
		map().part(this);
		joined = false;
		game.sendAllBut(this, new PlayerParted(uid));

	}

	public void move(int d, boolean run) {
		super.move(d, run);
	}

	public void update(long tick) {
		this.tick = tick;
	}

	public void updatePlayerAssets(int[] mapVersions) {
		mapQueue = new Queue<Integer>();
		for (int i = 0; i < Shared.NUM_MAPS; i++) {
			if (World.map[i].version != mapVersions[i]) {
				mapQueue.addLast(i);
			}
		}
		sendNextMap();
	}

	void sendNextMap() {
		if (mapQueue.isEmpty()) {
			updating = false;
			if (playing()) {
				// CommitReceived cr = new CommitReceived();
				// sendTCP(cr);
			} else {
				byte[] str = BearTool.serialize(World.monsterData);
				chunks = BearTool.divideArray(str, Shared.CHUNK_SIZE);
				for (int i = 0; i < chunks.length; i++) {
					sendTCP(new Chunk(chunks[i], 0, i == chunks.length - 1 ? 2 : 0, i));
				}
				join();
			}
		} else {
			updating = true;
			curUpdateMap = mapQueue.removeFirst();
			chunks = BearTool.divideArray(World.map[curUpdateMap].data, Shared.CHUNK_SIZE);
			for (int i = 0; i < chunks.length; i++) {
				sendTCP(new Chunk(chunks[i], curUpdateMap, i == chunks.length - 1 ? 1 : 0, i));
			}
		}
	}

	public void processPacket(Object data) {
		if (data instanceof Move) {
			Move m = (Move) data;
			move(m.dir, m.run);
		} else if (data instanceof PingPacket) {
			PingPacket pp = (PingPacket) data;
			sendTCP(pp);
		} else if (data instanceof Exit) {
			Exit e = (Exit) data;
			if (e.dir >= 0 && e.dir < 4) {
				exit(e.dir, e.run);
			}

		} else if (data instanceof ChangeDirection) {
			ChangeDirection cd = (ChangeDirection) data;
			if (cd.d < 0)
				cd.d = 0;
			if (cd.d > 3) {
				cd.d = 3;
			}
			dir = cd.d;
			SyncDirection sd = new SyncDirection();
			sd.cid = uid;
			sd.d = dir;
			map().sendBut(this, sd);
		} else if (data instanceof AdminCommand) {
			AdminCommand ac = (AdminCommand) data;
			if (access > 0) {
				switch (ac.i) {
				case 1: // editmap
					if (map().editor == this || map().editor == null) {
						map().editor = this;
						sendTCP(ac);
					} else {
						message(3, "That map is locked by " + map().editor.name
								+ ". Request an unlock with /god requestunlock", Color.YELLOW);
					}
					break;
				case 2: // edit monster
					sendTCP(ac);
					break;
				case 100: // warp to coords
					int m = ac.j;
					int x = ac.k;
					int y = ac.l;
					if (m < 0)
						m = 0;
					if (m >= Shared.NUM_MAPS)
						m = Shared.NUM_MAPS - 1;
					if (x < 0)
						x = 0;
					if (y < 0)
						y = 0;
					if (x >= Shared.MAP_WIDTH)
						x = Shared.MAP_WIDTH - 1;
					if (y >= Shared.MAP_WIDTH)
						y = Shared.MAP_WIDTH - 1;
					warp(m, x, y);
					sync();
					break;
				}
			}
		} else if (data instanceof MonsterData) {
			MonsterData md = (MonsterData) data;
			if (access > 0) {
				if (md.id >= 0 && md.id < 255) {
					World.monsterData[md.id] = md;
					game.sendAll(md);
					World.saveMonster(md.id);
					sendTCP(new MonsterReceived());
				}
			} else {
				disconnect("No access to edit monster");
			}
		} else if (data instanceof MapReceived) {
			sendNextMap();
		} else if (data instanceof Chunk) {
			if (access > 0) {
				Chunk c = (Chunk) data;
				if (c.i == 0) {
					// updateBytes = new byte[0];
				}
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				try {
					outputStream.write(updateBytes);
					outputStream.write(c.data);
				} catch (IOException e) {
					e.printStackTrace();
				}
				updateBytes = outputStream.toByteArray();
				if (c.last == 1) {
					MapData md = (MapData) BearTool.deserialize(updateBytes, MapData.class);
					PMap pm = new PMap();
					md.checkAll(pm);
					md.version = World.map[c.m].version + 1;
					World.map[c.m].loadEssentials(md, pm);
					World.map[c.m].reset();
					World.saveMap(c.m, md);
					for (Player p : Game.players.values()) {
						p.mapQueue.addLast(c.m);
						if (!p.updating) {
							p.sendNextMap();
						}
					}
					updateBytes = new byte[0];
					MapReceived cr = new MapReceived(c.m);
					sendTCP(cr);
					map().editor = null;
				}
			} else {
				disconnect("Wtf u doing?");
			}
		} else if (data instanceof ChangeDoor) {
			ChangeDoor cd = (ChangeDoor) data;
			if (cd.id >= 0 && cd.id < 100) {
				if (map().doors[cd.id] != null) {
					Door d = map().doors[cd.id];
					if (d.inRange(x, y)) {
						if (cd.reqState == 2) {
							if (d.state == 0 && d.open) {
								d.close();
							}
						} else if (cd.reqState == 1) {
							if (d.state == 0 && !d.open) {
								d.open();
							}
						}
					}
				}
			}
		} else if (data instanceof DiscardMap) {
			if (access > 0) {
				map().editor = null;
				sendTCP(data);
			}
		} else if (data instanceof SendChat) {
			SendChat sc = (SendChat) data;
			switch (sc.channel) {
			case 0: // mapsay
				map().send(new SendChat(name + " says '" + sc.s + "'", 0, Color.LIGHT_GRAY));
				break;
			case 1: // b
				game.sendAll(new SendChat(name + ": " + sc.s, 1, Color.MAGENTA));
				break;
			case 2: // tell
				break;
			}
		}
	}

	public void message(int channel, String msg, Color col) {
		sendTCP(new SendChat(msg, 0, Color.LIGHT_GRAY));
	}

	public void join() {
		joined = true;
		if (map == 0) {
			// first join! welcome them maybe?
			map = (int) (Math.random() * 10.0) + 1;
			map = 1;
			x = 5;
			y = 5;
		}
		dir = 1;
		PlayerJoined pj = new PlayerJoined(uid, name, spriteSet, sprite);
		JoinGame jg = new JoinGame(uid, spriteSet, sprite, name, map, x, y, dir);
		for (Player p : Game.players.values()) {
			if (p.playing()) {
				jg.players.add(new PlayerData(p.uid, p.name, spriteSet, sprite));
				if (p != this) {
					p.sendTCP(pj);
				}
			}
		}
		sendTCP(jg);
		map().join(this);
		sync();
	}

	public boolean playing() {
		return account_id > 0 && uid > 0 && joined;
	}

	public void warp(int m, int x, int y) {
		warp = true;
		if (m != map) {
			map().part(this);
			map = m;
			map().join(this);
		}

		this.x = x;
		this.y = y;
	}

	public void exit(int d, boolean run) {
		int nm = 0;
		dir = d;
		if (moveStamp < Game.tick) {
			switch (d) {
			case 0:
				if (y == 0) {
					nm = map().options.exit[0];
					y = Shared.MAP_WIDTH - 1;
				}
				break;
			case 1:
				if (y == Shared.MAP_WIDTH - 1) {
					nm = map().options.exit[1];
					y = 0;
				}
				break;
			case 2:
				if (x == 0) {
					nm = map().options.exit[2];
					x = Shared.MAP_WIDTH - 1;
				}
				break;
			case 3:
				if (x == Shared.MAP_WIDTH - 1) {
					nm = map().options.exit[3];
					x = 0;
				}
				break;
			}
			if (nm > 0) {
				map().part(this);
				map = nm;
				map().join(this);
				moveTime = getMoveTime(run);
			}
		}
		unlock = true;
		sync();
	}

	public PlayerSync getSync() {
		if (warp) {
			moveTime = 0;
			moveStamp = 0;
		}
		PlayerSync ps = new PlayerSync(uid, map, x, y, moveTime, dir, warp);
		warp = false;
		long diff = tick - lastMoveAt;
		if (diff <= moveTime) {
			if (diff == 0) {
				diff = moveTime;
			}
			ps.diff = (int) diff;
		} else {
			ps.diff = 0;
		}
		ps.unlock = unlock;
		return ps;
	}

	public void sync() {
		map().send(getSync());
		unlock = false;
	}

	public int getMoveTime(boolean run) {
		return run ? 200 : 400;
	}

	public void disconnect(String s) {
		game.disconnect(this, s);
	}

}
