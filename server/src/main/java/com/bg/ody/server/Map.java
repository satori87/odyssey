package com.bg.ody.server;

import java.util.ArrayList;
import java.util.List;
import com.bg.bearplane.ai.FlatTiledGraph;
import com.bg.bearplane.ai.FlatTiledNode;
import com.bg.bearplane.ai.IndexedAStarPathFinder;
import com.bg.bearplane.ai.TiledManhattanDistance;
import com.bg.bearplane.ai.TiledSmoothableGraphPath;
import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.Coord;
import com.bg.ody.shared.MapData;
import com.bg.ody.shared.MapOptions;
import com.bg.ody.shared.PMap;
import com.bg.ody.shared.Registrar.DoorSync;
import com.bg.ody.shared.Registrar.JoinMap;
import com.bg.ody.shared.Registrar.MonsterSync;
import com.bg.ody.shared.Registrar.PlayerSync;
import com.bg.ody.shared.Registrar.ResetMap;
import com.bg.ody.shared.Shared;

public class Map {

	Game game;

	long tick = 0;
	public int id = 0;
	int version = 0;

	public ServerTile[][] tile = new ServerTile[Shared.MAP_WIDTH][Shared.MAP_WIDTH];
	public MapOptions options = new MapOptions();
	byte[] data = null;

	public Monster[] monsters = new Monster[Game.MAX_MONSTERS];
	public ArrayList<Spawner> spawners = new ArrayList<Spawner>();
	public ArrayList<Player> players = new ArrayList<Player>();
	public Door[] doors = new Door[100];

	FlatTiledGraph graph;

	TiledManhattanDistance<FlatTiledNode> heuristic;
	IndexedAStarPathFinder<FlatTiledNode> pathFinder;

	long lastHadPlayerAt = 0;

	public Player editor = null;

	public Map(Game game, int id) {
		this.game = game;
		MapData md = new MapData();
		data = BearTool.serialize(md);
		for (int x = 0; x < Shared.MAP_WIDTH; x++) {
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				tile[x][y] = new ServerTile(x, y);
			}
		}
		this.id = id;
		graph = new FlatTiledGraph(this);
		heuristic = new TiledManhattanDistance<FlatTiledNode>();
		graph.init();
		pathFinder = new IndexedAStarPathFinder<FlatTiledNode>(graph, true);
	}

	public Monster spawnMonster(int type, int x, int y) {
		Monster m = null;
		int i = getFreeMonster();
		if (i >= 0) {
			m = new Monster(game, i, type, id, x, y);
			monsters[i] = m;
			send(m.getSync());
		}
		return m;
	}

	public int getFreeMonster() {
		for (int i = 0; i < Game.MAX_MONSTERS; i++) {
			if (monsters[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public void reset() {
		for (int i = 0; i < Game.MAX_MONSTERS; i++) {
			monsters[i] = null;
		}
		for (int i = 0; i < 100; i++) {
			doors[i] = null;
		}
		spawners.clear();
		addSpawners();
		addDoors();
		send(new ResetMap());
	}

	public void addDoors() {
		ServerTile t = null;
		int x = 0;
		int y = 0;
		int a = 0;
		int curD = 0;
		for (x = 0; x < Shared.MAP_WIDTH; x++) {
			for (y = 0; y < Shared.MAP_WIDTH; y++) {
				t = tile[x][y];
				for (a = 0; a < 2; a++) {
					if (t.att[a] == 4) { // doora
						Door d = new Door(this, curD, t.attData[a][4], x, y, t.attData[a][0], t.attData[a][3],
								t.attData[a][2], t.attData[a][1]);
						if (d.d == 0) {
							d.y--;
						}
						if (curD < 100) {
							doors[curD] = d;
							curD++;
						}
					}
				}
			}
		}
	}

	public void addSpawners() {
		ServerTile t = null;
		int x = 0;
		int y = 0;
		int a = 0;
		for (x = 0; x < Shared.MAP_WIDTH; x++) {
			for (y = 0; y < Shared.MAP_WIDTH; y++) {
				t = tile[x][y];
				for (a = 0; a < 2; a++) {
					if (t.att[a] == 2) { // SPAWNA
						Spawner sp = new Spawner(this, x, y, t.attData[a][0], t.attData[a][1], t.attData[a][2],
								t.attData[a][3], t.attData[a][4]);
						spawners.add(sp);
					}
				}

			}
		}
	}

	public void part(Player p) {

		// tell everyone else player parted
		PlayerSync ps = new PlayerSync(p.uid, 0, p.x, p.y, p.moveTime, p.dir, false);

		send(ps);

		// tell monsters player parted
		// DO IT HERE

		players.remove(p);
		if (players.size() == 0) {
			lastHadPlayerAt = tick;
		}
	}

	public void join(Player p) {
		// tell player everything thats on map

		JoinMap jm = new JoinMap();
		for (Player other : players) {
			if (other.playing() && p != other && p.map == other.map) {
				jm.players.add(new PlayerSync(p.uid, p.map, p.x, p.y, 0, p.dir, false));
			}
		}
		players.add(p);
		MonsterSync ms = null;
		for (Monster m : monsters) {
			if (m != null) {
				ms = m.getSync();
				jm.monsters.add(ms);
			}
		}
		DoorSync ds = null;
		for (Door d : doors) {
			if (d != null) {
				ds = d.getSync();
				jm.doors.add(ds);
			}
		}
		jm.id = id;
		p.sendTCP(jm);
		// dont need to tell other players anything
		// this is covered by player's sync() soon
	}

	public void update(long tick) {
		if (players.size() > 0 || tick - lastHadPlayerAt < 10000 || Game.PROCESS_IDLE_MAPS) {
			this.tick = tick;
			Monster m;
			for (Spawner sp : spawners) {
				sp.update(tick);
			}
			for (Door d : doors) {
				if (d != null) {
					d.update(tick);
				}
			}
			for (int i = 0; i < Game.MAX_MONSTERS; i++) {
				m = monsters[i];
				if (m != null && m.dead) {
					if (tick > m.diedAt + 10000) {
						m.spawner.remove(m);
						monsters[i] = null;
					}
				} else if (m != null) {
					m.update(tick);
				}
			}
		}
	}

	public void send(Object o) {
		try {
			for (Player p : players) {
				if (p.map == id) {
					p.sendTCP(o);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public void loadEssentials(MapData md, PMap pm) {
		data = BearTool.serialize(md);
		version = md.version;
		options = md.options;
		int x = 0;
		int y = 0;
		int a = 0;
		ServerTile t = null;
		for (x = 0; x < Shared.MAP_WIDTH; x++) {
			for (y = 0; y < Shared.MAP_WIDTH; y++) {
				t = tile[x][y];
				t.clearWalls();
				t.att = md.tile[x][y].att;
				t.attData = md.tile[x][y].attData;
				// t.wall = md.tile[x][y].wall;
				for (a = 0; a < 5; a++) {
					if (a < 4) {
						t.wall[a] = md.tile[x][y].wall[a];
					}
					if (pm.tile[x][y].wall[a]) {
						t.wall[a] = true;
					}
				}
			}
		}
		pathFinder = new IndexedAStarPathFinder<FlatTiledNode>(graph, false);
	}

	public void sendBut(Player pp, Object o) {
		for (Player p : players) {
			if (p.map == id && pp.uid != p.uid) {
				p.sendTCP(o);
			}
		}
	}

	public boolean isVacantElse(int x, int y) {
		for (Player c : players) {
			if (c.playing()) {
				if (c.x == x && c.y == y && !c.dead) {
					return false;
				}
			}
		}
		for (Monster c : monsters) {
			if (c != null && !c.dead) {
				if (c.x == x && c.y == y) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean checkWalk(Player p, int x, int y) {
		if (!MapData.inBounds(x, y)) {
			return false;
		}
		ServerTile t = tile[x][y];
		for (int a = 0; a < 2; a++) {
			switch (t.att[a]) {
			case 1:
				return false;
			case 3: // warp
				if (p instanceof Player) {
					int wm = t.attData[a][0];
					int wx = t.attData[a][1];
					int wy = t.attData[a][2];
					if (wm >= 0 && wm < Shared.NUM_MAPS) {
						if (wx >= 0 && wx < Shared.MAP_WIDTH) {
							if (wy >= 0 && wy < Shared.MAP_WIDTH) {
								p.warp(wm, wx, wy);
							}
						}
					}
					return false;
				}
			}
		}
		return true;
	}

	List<Player> getPlayersNear(int nx, int ny, int range) {
		List<Player> mobs = new ArrayList<Player>();
		for (Player p : players) {
			if (p.inRange(nx, ny, range)) {
				mobs.add(p);
			}
		}
		return mobs;
	}

	List<Monster> getMonstersNear(int nx, int ny, int range) {
		List<Monster> mobs = new ArrayList<Monster>();
		for (Monster m : monsters) {
			if (m.inRange(nx, ny, range) && !m.dead) {
				mobs.add(m);
			}
		}
		return mobs;
	}

	List<Mobile> getMobsNear(int nx, int ny, int range) {
		List<Mobile> mobs = new ArrayList<Mobile>();
		for (Monster m : monsters) {
			if (m.inRange(nx, ny, range) && !m.dead) {
				mobs.add(m);
			}
		}
		for (Player p : players) {
			if (p.inRange(nx, ny, range)) {
				mobs.add(p);
			}
		}
		return mobs;
	}

	public boolean isVacantTile(int x, int y) {
		if (!MapData.inBounds(x, y)) {
			return false;
		}
		ServerTile t = tile[x][y];
		for (int a = 0; a < 2; a++) {
			switch (t.att[a]) {
			case 1:
			case 3:
				return false;
			}
		}
		if (t.wall[4]) {
			return false;
		}
		return true;
	}

	public boolean isVacantWalls(int x, int y, int dir, int fx, int fy) {
		if (dir > 3 || !MapData.inBounds(x, y) || !MapData.inBounds(fx, fy)) {
			return false;
		}
		ServerTile t = tile[x][y];
		ServerTile ft = tile[fx][fy];
		if (t.wall[4]) {
			return false;
		}
		for (int a = 0; a < 2; a++) {
			switch (t.att[a]) {
			case 1:
				return false;
			}
		}
		if (ft.wall[dir]) {
			return false;
		}
		for (Door d : doors) {
			if (d != null) {
				if (d.x == x && d.y == y) {
					if (d.d == 0 && dir == 0) {
						if (!d.open) {
							return false;
						}
					} else if (d.d == 1 && dir == 0) {
						if (!d.open) {
							return false;
						}
					} else if (d.d == 3 && dir == 2) {
						if (!d.open) {
							return false;
						}
					} else if (d.d == 2 && dir == 3) {
						if (!d.open) {
							return false;
						}
					}
				} else if (d.x == fx && d.y == fy) {
					if (d.d == 0 && dir == 1) {
						if (!d.open) {
							return false;
						}
					} else if (d.d == 1 && dir == 1) {
						if (!d.open) {
							return false;
						}
					} else if (d.d == 3 && dir == 3) {
						if (!d.open) {
							return false;
						}
					} else if (d.d == 2 && dir == 2) {
						if (!d.open) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	void calculatePath(TiledSmoothableGraphPath<FlatTiledNode> path, int startX, int startY, int endX, int endY) {
		try {
			graph.update();
			// pathFinder = new IndexedAStarPathFinder<FlatTiledNode>(graph, false);
			if (MapData.inBounds(startX, startY) && MapData.inBounds(endX, endY)) {
				pathFinder.searchNodePath(tile[startX][startY], tile[endX][endY], heuristic, path);
				if (path.getCount() > 0) {
					path.pop();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Coord findFreeNode(int x, int y, int range) {
		int nx = x;
		int ny = y;
		Coord c = new Coord(x, y);
		int tries = 0;
		do {
			nx = BearTool.rndInt(x - range, x + range);
			ny = BearTool.rndInt(y - range, y + range);
			// nx = BearTool.rndInt(0, 35);
			// ny = BearTool.rndInt(0, 35);
			tries++;
		} while (!MapData.inBounds(nx, ny) && (!isVacantTile(nx, ny) || !isVacantElse(nx, ny)) && tries < 1000);
		if (tries < 1000) {
			c.x = nx;
			c.y = ny;
		}
		return c;
	}

}
