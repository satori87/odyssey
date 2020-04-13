package com.bg.ody.client.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.IntMap;
import com.bg.bearplane.engine.Effect;
import com.bg.bearplane.engine.Log;
import com.bg.bearplane.gui.Scene;
import com.bg.ody.shared.MapData;
import com.bg.ody.shared.MonsterData;
import com.bg.ody.shared.PMap;
import com.bg.ody.shared.PTile;
import com.bg.ody.shared.Shared;
import com.bg.ody.shared.Tile;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import box2dLight.RayHandler;

public class Realm {

	public static Realm realm;
	
	static LightManager lightMan;
	
	long tick = 0;

	public static int curMap = 0;

	// prototypes
	public static PMap[] pmap = new PMap[Shared.NUM_MAPS];
	public static MapData[] mapData = new MapData[Shared.NUM_MAPS];
	public static MonsterData[] monsterData = new MonsterData[Shared.NUM_MONSTERS];
	public static IntMap<ParticleEffect> effectData = new IntMap<ParticleEffect>();
	public static IntMap<ParticleEffectPool> effectPool = new IntMap<ParticleEffectPool>();

	// instances
	public static IntMap<Sprite> players = new IntMap<Sprite>();
	public static IntMap<Monster> monsters = new IntMap<Monster>();
	public static IntMap<Door> doors = new IntMap<Door>();
	public static List<Effect> effects = new ArrayList<Effect>();

	public Realm() {
		Shared.populateEdges();
		for (int i = 0; i < Shared.NUM_MONSTERS; i++) {
			monsterData[i] = new MonsterData(i);
		}
		realm = this;

	}

	public static void checkAll() {
		for (int i = 0; i < Shared.NUM_MAPS; i++) {
			if (mapData[i] != null) {
				if (pmap[i] == null) {
					pmap[i] = new PMap();
					mapData[i].checkAll(pmap[i]);
				}
			}
		}
	}

	public void update(long tick) {
		this.tick = tick;
		if(lightMan != null) {
		lightMan.update(tick);
		}
		Effect fx = null;
		Iterator<Effect> itr = effects.iterator();
		for (Sprite p : players.values()) {
			p.update(tick);
		}
		for (Monster p : monsters.values()) {
			p.update(tick);
		}
		for (Door d : doors.values()) {
			d.update(tick);
		}
		while (itr.hasNext()) {
			fx = itr.next();
			if (fx != null && fx.fx != null) {
				fx.fx.update(Gdx.graphics.getDeltaTime());
				if (fx.fx.isComplete()) {
					fx.fx.free();
					effects.remove(fx);
				}
			}
		}
	}

	public void render() {
	lightMan.render();
	}

	public static MapData map() {
		return mapData[curMap];
	}

	public static PMap pmap() {
		return pmap[curMap];
	}

	public static Effect addEffect(int type, int i, int x, int y, int modX, int modY, float scale) {
		Effect bfx = null;
		try {
			bfx = new Effect(effects.size(), type, i, x, y, modX, modY, scale);
			bfx.fx = Realm.effectPool.get(type).obtain();
			bfx.fx.setPosition(x * 32 + 16 + modX, y * 32 + 16 + modY);
			bfx.fx.scaleEffect(0.1f);
			effects.add(bfx);
		} catch (Exception e) {
			return null;
		}
		return bfx;
	}

	public static void addFX() {
		Tile t = null;
		int x = 0;
		int y = 0;
		int a = 0;
		for (x = 0; x < Shared.MAP_WIDTH; x++) {
			for (y = 0; y < Shared.MAP_WIDTH; y++) {
				t = map().tile[x][y];
				for (a = 0; a < 2; a++) {
					if (t.att[a] == 5) { // effecta
						Effect e = addEffect(t.attData[a][4], t.attData[a][1], x, y, t.attData[a][2], t.attData[a][3],
								(float) t.attData[a][0] / 10f);
						if (e != null && e.fx != null) {
							e.fx.scaleEffect(t.attData[a][0]);
							for (int i = 0; i < 10; i++) {
								e.fx.update(0.1f);
							}
						}
					}
				}
			}
		}
	}

	public static void addDoors() {
		Tile t = null;
		int x = 0;
		int y = 0;
		int a = 0;
		int curD = 0;
		for (x = 0; x < Shared.MAP_WIDTH; x++) {
			for (y = 0; y < Shared.MAP_WIDTH; y++) {
				t = map().tile[x][y];
				for (a = 0; a < 2; a++) {
					if (t.att[a] == 4) { // doora
						Door d = new Door(curD, t.attData[a][4], x, y, t.attData[a][0], t.attData[a][3],
								t.attData[a][2], t.attData[a][1]);
						if (d.d == 0) {
							d.y--;
						}
						if (curD < 100) {
							doors.put(curD, d);
							curD++;
						}
					}
				}
			}
		}
	}

	public static void renderFX(int i) {
		Effect fx = null;
		Iterator<Effect> itr = effects.iterator();
		while (itr.hasNext()) {
			fx = itr.next();
			if (fx.i == i) {
				fx.fx.draw(Scene.batcher);
			}
		}
	}

	public static void resetMap() {
		monsters.clear();
		doors.clear();
		for (Effect e : effects) {
			e.fx.free();
		}
		effects.clear();
		lightMan.reset();
		addDoors();
		addFX();
	}

	public static void loadMap(int i) {
		InputStream inputStream;
		try {
			Log.info("Loading map " + i);
			FileHandle f = Gdx.files.local("maps/map" + i + ".map");
			inputStream = new InflaterInputStream(f.read());
			Input input = new Input(inputStream);
			Kryo kryo = new Kryo();
			mapData[i] = kryo.readObject(input, MapData.class);
			input.close();
		} catch (Exception e) {
			Log.error(e);
			mapData[i] = new MapData();
		}
	}

	public void load() {
		lightMan = new LightManager();		
		//lightMan.createLight(32, 64, 0.5f, true, true, 1.2f, lBody)
	}

	public static MapData getNeighbor(int m, int d) {
		if (m >= 0 && m < Shared.NUM_MAPS) {
			MapData md = mapData[m];
			if (d < 4) {
				if (md.options.exit[d] >= 0 && md.options.exit[d] < Shared.NUM_MAPS) {
					return mapData[md.options.exit[d]];
				}
			} else {
				// add support for corners here
			}
		}
		return null;
	}

	public static void saveMap(int i) {
		try {
			OutputStream outputStream = new DeflaterOutputStream(new FileOutputStream("maps/map" + i + ".map"));
			Output output = new Output(outputStream);
			Kryo kryo = new Kryo();
			kryo.writeObject(output, mapData[i]);
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static boolean isVacant(int x, int y, int dir, int fx, int fy) {
		try {
			if (!MapData.inBounds(x, y) || !MapData.inBounds(fx, fy)) {
				return false;
			}
			PTile p = pmap().tile[x][y];
			Tile t = map().tile[x][y];
			PTile fp = pmap().tile[fx][fy];
			Tile ft = map().tile[fx][fy];
			for (int a = 0; a < 2; a++) {
				switch (t.att[a]) {
				case 1:
					return false;
				}
			}
			if (p.wall[4]) {
				return false;
			}
			for (Sprite c : players.values()) {
				if (c.map == curMap) {
					if (c.x == x && c.y == y) {
						return false;
					}
				}
			}
			for (Monster c : monsters.values()) {
				if (c.map == curMap) {
					if (c.x == x && c.y == y) {
						return false;
					}
				}
			}
			for (Door d : doors.values()) {
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
			if (fp.wall[dir] || ft.wall[dir]) {
				return false;
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
		return true;
	}

}
