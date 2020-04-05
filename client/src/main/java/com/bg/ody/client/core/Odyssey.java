package com.bg.ody.client.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.Bearable;
import com.bg.bearplane.engine.Log;
import com.bg.bearplane.engine.Timer;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.net.BearNet;
import com.bg.bearplane.net.TCPClient;
import com.bg.bearplane.net.packets.DisconnectError;
import com.bg.bearplane.net.packets.Logon;
import com.bg.bearplane.net.packets.PingPacket;
import com.bg.ody.client.scenes.CharacterScene;
import com.bg.ody.client.scenes.EditListScene;
import com.bg.ody.client.scenes.LoginScene;
import com.bg.ody.client.scenes.MapOptionsScene;
import com.bg.ody.client.scenes.EditMapScene;
import com.bg.ody.client.scenes.EditMonsterScene;
import com.bg.ody.client.scenes.MenuScene;
import com.bg.ody.client.scenes.NewCharacterScene;
import com.bg.ody.client.scenes.OptionsScene;
import com.bg.ody.client.scenes.PlayScene;
import com.bg.ody.client.scenes.TestMapScene;
import com.bg.ody.client.scenes.UpdateScene;
import com.bg.ody.shared.Shared;
import com.bg.ody.shared.MapData;
import com.bg.ody.shared.MonsterData;
import com.esotericsoftware.kryo.util.IntMap;
import com.bg.ody.shared.Registrar.AdminCommand;
import com.bg.ody.shared.Registrar.CharacterCreated;
import com.bg.ody.shared.Registrar.Chunk;
import com.bg.ody.shared.Registrar.DiscardMap;
import com.bg.ody.shared.Registrar.DoorSync;
import com.bg.ody.shared.Registrar.MapReceived;
import com.bg.ody.shared.Registrar.MonsterReceived;
import com.bg.ody.shared.Registrar.MonsterSync;
import com.bg.ody.shared.Registrar.SendChat;
import com.bg.ody.shared.Registrar.JoinGame;
import com.bg.ody.shared.Registrar.JoinMap;
import com.bg.ody.shared.Registrar.Play;
import com.bg.ody.shared.Registrar.PlayerData;
import com.bg.ody.shared.Registrar.PlayerJoined;
import com.bg.ody.shared.Registrar.PlayerParted;
import com.bg.ody.shared.Registrar.PlayerSync;
import com.bg.ody.shared.Registrar.ResetMap;
import com.bg.ody.shared.Registrar.SyncDirection;
import com.bg.ody.shared.PMap;

public class Odyssey extends TCPClient implements Bearable, BearNet {

	public static Odyssey game;
	public static Assets assets;
	public World world = new World();

	// timing
	long tick = 0;
	public IntMap<Timer> timers = new IntMap<Timer>();
	int ping = 0;

	// state variables
	public boolean playing = false;
	public int cid = 0;
	public boolean joinGame = false;

	// admin
	public int editType = 0; // for lists, 1 = monster
	public int editNum = 0;
	public int listScroll = 0;
	public int editListType = 0;

	// chat
	public ChatEntry[] chatLog = new ChatEntry[Shared.CHAT_SIZE];
	public String curChatText = "";
	public int curChat = 0;
	public int chatLines = 4;

	// scenes
	public static EditMonsterScene editMonsterScene = new EditMonsterScene();
	public static MapOptionsScene mapOptionsScene = new MapOptionsScene();
	public static EditListScene editListScene = new EditListScene();
	public static LoginScene loginScene = new LoginScene();
	public static EditMapScene editMapScene = new EditMapScene();
	public static PlayScene playScene; // dont instantiate this yet
	public static OptionsScene optionsScene = new OptionsScene();

	// update
	byte[] updateBytes = new byte[0];

	public Odyssey(Assets a) {
		super();
		Log.info("Odyssey Initializing");
		try {
			game = this;
			assets = a;
			for (int i = 0; i < Shared.CHAT_SIZE; i++) {
				chatLog[i] = new ChatEntry(-1, "", Color.WHITE);
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	@Override
	public void addTimers() {
		try {
			timers.put(250, new Timer(this, 250));
			timers.put(1000, new Timer(this, 1000));
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	@Override
	public void doTimer(int interval) {
		try {
			switch (interval) {
			case 250:
				break;
			case 1000:
				secondTimer();
				break;
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	void secondTimer() {
		if (playing) {
			PingPacket pp = new PingPacket();
			pp.stamp = tick;
			sendTCP(pp);
		}
	}

	@Override
	public void doTimers(long tick) {
		try {
			this.tick = tick;
			for (Timer t : timers.values()) {
				t.update(tick);
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	@Override
	public void update() {
		try {
			world.update(tick);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void godCommand(String gc) {
		try {
			String rest = "";
			if (gc.length() > 1) {
				rest = gc.substring(gc.indexOf(" ") + 1);
				Log.debug(rest);
				if (gc != null && gc.length() > 0) {
					String words[] = gc.split(" ");
					if (words.length > 0) {
						switch (words[0].toUpperCase()) {
						case "WARP":
						case "WAR":
						case "WA":
						case "W":
							if (words.length == 2) {
								AdminCommand ac = new AdminCommand();
								ac.i = 100;
								ac.j = Integer.parseInt(words[1]);
								ac.k = getMe().x;
								ac.l = getMe().y;
								Log.debug("ok");
								sendTCP(ac);
							} else if (words.length == 4) {
								AdminCommand ac = new AdminCommand();
								ac.i = 100;
								ac.j = Integer.parseInt(words[1]);
								ac.k = Integer.parseInt(words[2]);
								ac.l = Integer.parseInt(words[3]);

								Log.debug("p: " + ac.j + "," + ac.k + "," + ac.l);
								sendTCP(ac);
							}
							break;
						}
					}
				}
			}
		} catch (Exception e) {

		}
	}

	public void processTextInput() {
		try {
			String s = curChatText;
			if (s.length() < 1) {
				return;
			}
			String words[];

			if (s.substring(0, 1).equals("/") && s.length() > 1) {
				words = s.substring(1).split(" ");
				String rest = "";
				int c = 0;
				for (String w : words) {
					if (c > 0) {
						rest += w;
						if (c < words.length) {
							rest += " ";
						}
					}
					c++;
				}
				switch (words[0].toUpperCase()) {
				case "GOD":
				case "GO":
				case "G":
					godCommand(rest);
					break;
				case "WHO":
				case "WH":
				case "W":
					String ss = "Currently online: ";
					ss += getPlayerList(false);
					addChat(3, ss, Color.CYAN);
					break;
				case "BROADCAST":
				case "BROADCAS":
				case "BROADCA":
				case "BROADC":
				case "BROAD":
				case "BROA":
				case "BRO":
				case "BR":
				case "B":
					sendTCP(new SendChat(rest, 1, null));
					break;
				case "WHERE":
				case "WHER":
				case "WHE":
					addChat(3, "Map: " + getMe().map + " X: " + getMe().x + " Y: " + getMe().y, Color.YELLOW);
					break;
				case "PING":
					addChat(3, "Ping: " + ping + " ms", Color.YELLOW);
					break;

				}
			} else {
				sendTCP(new SendChat(s, 0, null));
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void chatScrollUp() {
		try {
			// screen.scene.playSound(23, 1, 1);
			curChat += 1;
			if (curChat > (400 - chatLines)) {
				curChat = 400 - chatLines;
			}
			if (curChat > getHighestChat() + 1 - chatLines) {
				curChat = getHighestChat() + 1 - chatLines;
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void chatScrollDown() {
		try {
			// screen.scene.playSound(23, 1, 1);
			curChat -= 1;
			if (curChat < 0) {
				curChat = 0;
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void toggleChat() {
		try {
			chatLines++;
			if (chatLines > 10) {
				chatLines = 0;
			}
			if (chatLines > getHighestChat()) {
				// chatLines = getHighestChat();
				// addChat(3,"Chat resized to: " + getHighestChat(), Color.LIGHT_GRAY);
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	int getHighestChat() {
		try {
			for (int i = 1; i < 400; i++) {
				if (chatLog[i].s.length() < 1) {
					return i - 1;
				}
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
		return 400;
	}

	void addChatLine(int channel, String s, Color c) {
		try {
			float l = assets.getStringWidth(s, 1, 0, 1);
			int u = 0;
			Character cc = ' ';
			int k = 0;
			int begin = 0;
			int end = 0;
			boolean f = true;
			boolean found = false;
			if (l >= 1018) { // split it up
				for (Character ch : s.toCharArray()) {
					if (f) {
						k++;
						u += assets.fontWidth[ch] + 1;
						if (u >= 1018) {
							begin = 0;
							end = k - 1;
							found = false;
							for (int i = k - 1; i >= 0 && !found; i--) {
								cc = s.charAt(i);
								if (cc.equals(' ')) {
									found = true;
									end = i;
								}
							}
							if (end >= s.length() - 1) {
								end = s.length() - 2;
							}
							String s1 = s.substring(begin, end);
							s = s.substring(end + 1);
							addChatLine(channel, s1, c);
							f = false;
						}
					}
				}

			}
			for (int i = Shared.CHAT_SIZE - 1; i > 0; i--) {
				chatLog[i] = chatLog[i - 1];
			}
			chatLog[0] = new ChatEntry(channel, s, c);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	void addChat(int channel, String s, Color c) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
		Date date = new Date(System.currentTimeMillis());
		s = "[" + simpleDateFormat.format(date) + "] " + s;
		addChatLine(channel, s, c);
	}

	private Sprite player(int c) {
		return World.players.get(c);
	}

	private Monster monster(int c) {
		return World.monsters.get(c);
	}

	private Door door(int c) {
		return World.doors.get(c);
	}

	public Sprite getMe() {
		return World.players.get(cid);
	}

	public void adminCommand(int i) {
		AdminCommand ac = new AdminCommand();
		switch (i) {
		case 1: // editmap
			ac.i = 1;
			sendTCP(ac);
			break;
		case 2: // editMonster
			ac.i = 2;
			sendTCP(ac);
			break;

		}
	}

	public static MapData map() {
		return World.map();
	}

	public static PMap pmap() {
		return World.pmap();
	}

	@Override
	public void addScenes() {
		playScene = new PlayScene();
		Scene.addScene("menu", new MenuScene());
		Scene.addScene("login", loginScene);
		Scene.addScene("character", new CharacterScene());
		Scene.addScene("newCharacter", new NewCharacterScene());
		Scene.addScene("editMap", editMapScene);
		Scene.addScene("testMap", new TestMapScene());
		Scene.addScene("play", playScene);
		Scene.addScene("update", new UpdateScene());
		Scene.addScene("editMonster", editMonsterScene);
		Scene.addScene("editList", editListScene);
		Scene.addScene("mapOptions", new MapOptionsScene());
		Scene.addScene("options", optionsScene);

	}

	public void play() {
		try {
			Scene.change("update");
			Scene.lock();
			Play p = new Play();
			for (int i = 0; i < Shared.NUM_MAPS; i++) {
				p.mapVersions[i] = World.mapData[i].version;
			}
			sendTCP(p);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	@Override
	public void loaded() {
		Scene.change("menu");
		World.checkAll();
	}

	@Override
	public void dispose() {
		assets.dispose();
	}

	@Override
	public int getGameWidth() {
		return Shared.GAME_WIDTH;
	}

	@Override
	public int getGameHeight() {
		return Shared.GAME_HEIGHT;
	}

	@Override
	public String getGameName() {
		return Shared.GAME_NAME;
	}

	@Override
	public String getClientVersion() {
		return Shared.CLIENT_VERSION;
	}

	@Override
	public void clientConnected() {
		connected = true;
		// if (Scene.scene instanceof LoginScene) {
		authenticate(LoginScene.suser, LoginScene.spass, LoginScene.newAcct);
		// }
	}

	@Override
	public void clientDisconnected() {
		playing = false;
		MenuScene.status = "Disconnected from server";
		Scene.change("menu");
		Scene.unlock();
	}

	public void connectGame(String hostname) {
		MenuScene.status = "Connecting to " + hostname + ":" + Shared.TCP_PORT + "...";
		new Thread("Connect") {
			public void run() {
				try {
					connect(hostname, Shared.TCP_PORT, Shared.UDP_PORT);
				} catch (Exception e) {
					disconnect();
					MenuScene.status = "Failed to connect to server";
				}
			}
		}.start();
	}

	@Override
	public void processPacket(Object object) {
		try {
			if (object == null) {
				return;
			}
			if (!(object instanceof PingPacket)) {
				// Log.debug(object.getClass().getCanonicalName());
			}
			// Lo.debug("Packet " + object.getClass().getName() + " received");
			if (object instanceof SendChat) {
				SendChat sc = (SendChat) object;
				addChat(sc.channel, sc.s, sc.col);
			} else if (object instanceof ResetMap) {
				World.resetMap();
			} else if (object instanceof PingPacket) {
				PingPacket pp = (PingPacket) object;
				ping = (int) (tick - pp.stamp);
			} else if (object instanceof DisconnectError) {
				Scene.scene.msgBox(((DisconnectError) object).msg);
			} else if (object instanceof Logon) {
				Logon logon = (Logon) object;
				CharacterScene cs = (CharacterScene) Scene.get("character");
				cs.name = logon.name;
				Scene.change("character");
				Scene.unlock();
			} else if (object instanceof CharacterCreated) {
				CharacterCreated cc = (CharacterCreated) object;
				((CharacterScene) Scene.get("character")).name = cc.name;
				Scene.change("character");
				Scene.unlock();
			} else if (object instanceof MonsterReceived) {
				Scene.unlock();
				Scene.change("editList");
			} else if (object instanceof MonsterData) {
				MonsterData md = (MonsterData) object;
				World.monsterData[md.id] = md;
			} else if (object instanceof JoinGame) {
				JoinGame jg = (JoinGame) object;
				joinGame = true;
				cid = jg.cid;
				Sprite c = new Sprite(jg.spriteSet, jg.sprite);
				c.name = jg.name;
				World.players.put(cid, c);
				for (PlayerData pd : jg.players) {
					processPacket(pd);
				}
				c.dir = jg.dir;
				c.map = jg.map;
				World.curMap = c.map;
				c.x = jg.x;
				c.y = jg.y;
				int pc = getPlayerCount();
				String add = "Welcome to " + Shared.TITLE_STRING + ". There ";
				if (pc == 0) {
					add += "GLITCH";
				} else if (pc == 1) {
					add += "are no other players online.";
				} else if (pc == 2) {
					add += "is one other player online: " + getPlayerList(true);
				} else {
					add += "are " + (pc - 1) + " other players online: " + getPlayerList(true);
				}
				addChat(3, add, Color.CYAN);
			} else if (object instanceof JoinMap) {
				JoinMap jm = (JoinMap) object;
				World.curMap = jm.id;
				getMe().map = jm.id;
				for (PlayerSync ps : jm.players) {
					processPacket(ps);
				}
				World.resetMap();
				for (MonsterSync ms : jm.monsters) {
					processPacket(ms);
				}
				for (DoorSync ds : jm.doors) {
					processPacket(ds);
				}
				if (!playing) {
					playing = true;
					Scene.change("play");
					Scene.unlock();
				}
			} else if (object instanceof PlayerJoined) {
				PlayerJoined pj = (PlayerJoined) object;
				Sprite c = new Sprite(pj.spriteSet, pj.sprite);
				c.name = pj.name;
				World.players.put(pj.cid, c);
				addChat(4, pj.name + " has joined the game.", Color.TEAL);
			} else if (object instanceof PlayerParted) {
				PlayerParted pj = (PlayerParted) object;
				addChat(4, player(pj.cid).name + " has left the game.", Color.TEAL);
				World.players.remove(pj.cid);
			} else if (object instanceof PlayerData) {
				PlayerData pd = (PlayerData) object;
				Sprite c = null;
				if (player(cid) == null) {
					c = new Sprite(pd.spriteSet, pd.sprite);
				} else {
					c = player(pd.cid);
					if (c == null) {
						c = new Sprite(pd.spriteSet, pd.sprite);
					}
				}
				c.name = pd.name;
				World.players.put(pd.cid, c);
			} else if (object instanceof SyncDirection) {
				SyncDirection sd = (SyncDirection) object;
				Sprite c = player(sd.cid);
				if (c != null) {
					c.dir = sd.d;
					c.moveDir = sd.d;
				}
			} else if (object instanceof DoorSync) {
				DoorSync ds = (DoorSync) object;
				Door d = door(ds.id);
				if (d == null) {
					d = new Door(ds.id);
					World.doors.put(ds.id, d);
				}
				d.open = ds.open;
				d.state = ds.state;
				d.stamp = tick + ds.diff;
				if (d.state == 1) {
					d.open = false;
				} else if (d.state == 2) {
					d.open = false;
				}
			} else if (object instanceof MonsterSync) {
				MonsterSync ms = (MonsterSync) object;
				Monster m = monster(ms.id);
				if (m == null) {
					m = new Monster(ms.type);
					World.monsters.put(ms.id, m);
				}
				m.map = ms.map;
				m.dir = ms.dir;
				m.moveDir = ms.dir;
				m.x = ms.x;
				m.y = ms.y;
				m.moveTime = ms.moveTime;
				m.XO = 0;
				m.YO = 0;
				if (ms.diff > 0) {
					m.moveStamp = tick + ms.diff;
				} else {
					m.moveStamp = 0;
				}
				m.update(tick);
			} else if (object instanceof PlayerSync) {
				PlayerSync ps = (PlayerSync) object;
				Sprite c = player(ps.cid);
				if (c != null) {
					c.dir = ps.dir;
					c.moveDir = ps.dir;
					if (ps.map != c.map || joinGame) {
						joinGame = false;
						c.map = ps.map;
						if (ps.cid == cid) {
							World.curMap = c.map;
							World.pmap[c.map] = null;
							playScene.processed = false;
						}
					}
					c.x = ps.x;
					c.y = ps.y;
					long d = tick + ps.moveTime - c.moveTimer;
					if (d > ps.moveTime) {
						d = ps.moveTime;
					}
					if (ps.moveTime == 0) {
						d = 0;
					}
					c.moveTime = ps.moveTime;
					c.moveStamp = tick + ps.moveTime;
					c.moveTimer = tick + ps.moveTime;
					c.XO = 0;
					c.YO = 0;

					if (ps.cid != cid) {
						if (ps.diff > 0) {
							c.moveStamp = tick + ps.diff;
						} else {
							c.moveStamp = 0;
						}
					} else {
						c.delay = d;
						if (ps.unlock)
							Scene.unlock();
					}
					if (ps.warp) {
						c.moveStamp = tick - 1;
						c.moveTimer = tick + 50;
					}
					c.update(tick);
				}
			} else if (object instanceof Chunk) {
				Chunk c = (Chunk) object;
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				try {
					outputStream.write(updateBytes);
					outputStream.write(c.data);
				} catch (IOException e) {
					e.printStackTrace();
				}
				updateBytes = outputStream.toByteArray();
				if (c.last == 1) { // map chunks
					World.mapData[c.m] = (MapData) BearTool.deserialize(updateBytes, MapData.class);
					World.saveMap(c.m);
					if (World.curMap == c.m) {
						World.pmap[c.m] = null;
						playScene.processed = false;
					}
					updateBytes = new byte[0];
					MapReceived cr = new MapReceived(c.m);
					sendTCP(cr);
				} else if (c.last == 2) { // monster chunks
					World.monsterData = (MonsterData[]) BearTool.deserialize(updateBytes, MonsterData[].class);
					updateBytes = new byte[0];
				}
			} else if (object instanceof DiscardMap) {
				Scene.unlock();
				Scene.change("play");
			} else if (object instanceof MapReceived) {
				Scene.unlock();
				Scene.change("play");
			} else if (object instanceof AdminCommand) {
				AdminCommand ac = (AdminCommand) object;
				switch (ac.i) {
				case 1: // edit map:
					Scene.change("editMap");
					break;
				case 2: // edit monster list
					if (editType != 2) {
						editType = 2;
						editNum = 0;
						listScroll = 0;
					}
					Scene.change("editList");
					break;
				case 3: // edit monster #:
					Scene.change("editMonster");
					break;
				}
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public Iterable<Sprite> players() {
		return World.players.values();
	}

	public int getPlayerCount() {
		int c = 0;
		for (Sprite p : players()) {
			if (p != null) {
				c++;
			}
		}
		return c;
	}

	public String getPlayerList(boolean ignore) {
		String ss = "";
		List<Sprite> who = new ArrayList<Sprite>();
		for (Sprite p : players()) {
			if (p != null && (p != getMe() || !ignore)) {
				who.add(p);
			}
		}
		for (Sprite p : who) {
			ss += p.name;
			if (who.get(who.size() - 1) != p) {
				ss += ", ";
			}
		}
		return ss;
	}

}
