package com.bg.ody.server;

import java.util.ArrayList;
import java.util.List;

import com.bg.bearplane.ai.FlatTiledNode;
import com.bg.bearplane.ai.TiledSmoothableGraphPath;
import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.Coord;
import com.bg.bearplane.engine.Log;
import com.bg.ody.shared.MapData;
import com.bg.ody.shared.MonsterData;
import com.bg.ody.shared.Registrar.MonsterSync;
import com.bg.ody.shared.Shared;

public class Monster extends Mobile {

	public int type = 0;

	public Spawner spawner;

	long targetStamp = 0;

	// int state = 0; // 0 = wander, 1 = pursue, 2 = flee, 3 = idle

	// TiledSmoothableGraphPath<FlatTiledNode> path;

	public int pathX = 0;
	public int pathY = 0;
	boolean pathing = false;

	public long wanderStamp = 0;
	public int wanderDelay = 2;

	int pathCount = 0;
	TiledSmoothableGraphPath<FlatTiledNode> path = new TiledSmoothableGraphPath<FlatTiledNode>();
	int curX = 0;
	int curY = 0;
	boolean recalc = false;

	public Monster(Game game) {
		super(game);
	}

	public Monster(Game game, int id, int type, int map, int x, int y) {
		super(game);
		this.uid = id;
		this.type = type;
		this.map = map;
		this.x = x;
		this.y = y;
		dir = (int) (Math.random() * 4);
		load();
		lastMoveAt = tick;

	}

	public MonsterSync getSync() {
		MonsterSync ms = new MonsterSync(uid, type, map, x, y, moveTime, dir);
		long diff = tick - lastMoveAt;
		if (diff <= moveTime) {
			if (diff == 0) {
				diff = moveTime;
			}
			ms.diff = (int) diff;
		} else {
			ms.diff = 0;
		}
		return ms;
	}

	public void sync() {
		map().send(getSync());
	}

	boolean arrived() {
		boolean arrived = (x == pathX && y == pathY);
		if (arrived) {
			pathing = false;
		}
		return arrived;
	}

	public void move(int d, boolean run) {
		super.move(d, run);
	}

	int getWanderRange() {
		int r = data().wanderRange;
		if (r == 0) {
			r = 1;
		}
		return r;
	}

	void setPath(int x, int y) {
		pathX = x;
		pathY = y;
		recalc = true;
		pathing = true;
		pathStamp = tick + 200;
	}

	void noPath() {
		pathing = false;
		target = null;
		recalc = false;
	}

	void wander() {
		target = null;
		Coord c = map().findFreeNode(spawner.x, spawner.y, Realm.monsterData[this.type].wanderRange);
		setPath(c.x, c.y);
		wanderStamp = tick + wanderDelay + (int) (Math.random() * 1000) - 500;
	}

	public void calculatePath() {
		recalc = false;
		path.clear();
		map().calculatePath(path, x, y, pathX, pathY);
	}

	void checkForTargets() {
		// if (!Realm.monsterData[type].friendly) {
		// we are not friendly

		// check within sight range for enemy targets
		List<Player> nearby = map().getPlayersNear(x, y, getSight());
		if (nearby != null && nearby.size() > 0) {
			int closest = 99999;
			Player cp = null;
			int d = 0;
			for (Player p : nearby) {
				d = (int) distanceTo(p);
				if (d < closest) {
					closest = d;
					cp = p;
				}
			}
			if (cp != null) {
				noPath();
				target = cp;
			}
		} else {

		}
		// }
	}

	int getSight() {
		return Realm.monsterData[type].sight;
	}

	void checkCurrentTarget() {
		boolean stillValid = false;
		if (target != null) {
			// target is not null
			if (target.playing()) {
				// target is playing
				if (map().id == target.map().id) {
					// we are on same map
					if (inRange(target, getSight())) {
						// we can still see this target
						stillValid = true;
					}
				}
			}
		}
		if (!stillValid) {
			noPath();
		}
	}

	void checkForBetterTargets() {

	}

	long pathStamp = 0;
	long recalcStamp = 0;
	long strafeStamp = 0;

	public void pathToTarget() {
		List<Coord> valid = new ArrayList<Coord>();
		if (MapData.inBounds(target.x, target.y - 1)) {
			if (map().isVacantWalls(target.x, target.y - 1, 0, x, y) && map().isVacantTile(target.x, target.y - 1)
					&& map().isVacantElse(target.x, target.y - 1)) {
				valid.add(new Coord(target.x, target.y - 1));
			}
		}
		if (MapData.inBounds(target.x, target.y + 1)) {
			if (map().isVacantWalls(target.x, target.y + 1, 1, x, y) && map().isVacantTile(target.x, target.y + 1)
					&& map().isVacantElse(target.x, target.y + 1)) {
				valid.add(new Coord(target.x, target.y + 1));
			}
		}
		if (MapData.inBounds(target.x - 1, target.y)) {
			if (map().isVacantWalls(target.x - 1, target.y, 2, x, y) && map().isVacantTile(target.x - 1, target.y)
					&& map().isVacantElse(target.x - 1, target.y)) {
				valid.add(new Coord(target.x - 1, target.y));
			}
		}
		if (MapData.inBounds(target.x + 1, target.y)) {
			if (map().isVacantWalls(target.x + 1, target.y, 3, x, y) && map().isVacantTile(target.x + 1, target.y)
					&& map().isVacantElse(target.x + 1, target.y)) {
				valid.add(new Coord(target.x + 1, target.y));
			}
		}
		int low = 99999;
		double d = 0;
		Coord lc = null;
		for (Coord c : valid) {
			d = distanceTo(c.x, c.y);
			if ((int) d < low) {
				low = (int) d;
				lc = c;
			}
		}
		if (lc != null) {
			setPath(lc.x, lc.y);
		}
	}

	public void update(long tick) {
		this.tick = tick;
		if (tick > targetStamp) {
			targetStamp = tick + 200;
			if (target != null) {
				// we are already targeting something, is it still valid?
				checkCurrentTarget();
			} // do not put else here, we want this next if to run in both cases
			if (target == null) {
				// we are not targeting anything, should we be?
				checkForTargets();
			} else {
				checkForBetterTargets(); // TODO
			}
		}
		if (pathing) {
			if (target == null) { // wander
				if (tick > wanderStamp) {
					wander();
				}
			} else {
				if (tick > pathStamp) {
					pathStamp = tick + 200;
					pathToTarget();
				}

				// we are following a path to our target
			}
		} else {
			if (target == null) { // wander
				if (arrived() || path.getCount() < 1) {
					noPath();
					wanderStamp = tick + wanderDelay + (int) (Math.random() * 1000) - 500;
				}
			} else {
				// we have a target but no path to it

				pathToTarget();
			}
		}
		if (pathing) {
			if (recalc || tick > recalcStamp) {
				recalcStamp = tick + 200;
				calculatePath();
			}
			if (tick > moveStamp) {
				if (!adjacentTo(target.x, target.y) || (BearTool.randInt(1, 100) < 5 && tick > strafeStamp)) {
					strafeStamp = tick + 1000;
					if (path.getCount() > 0) {
						FlatTiledNode t = path.get(0);
						int d = 4;
						if (t.x > x) {
							d = 3;
						} else if (t.x < x) {
							d = 2;
						} else if (t.y < y) {
							d = 0;
						} else if (t.y > y) {
							d = 1;
						}
						moved = false;
						if (d < 4) {
							move(d, false);
						}

						if (moved) {
							path.pop();
						} else {
							if (target == null) {
								wander();
							}
						}
					} else {
						if (target == null) {
							wander();
						} else {

						}
					}
				}
			}
		}
	}

	public int getMoveTime(boolean run) {
		return data().walkSpeed;
	}

	public MonsterData data() {
		try {
			if (type >= 0 && type < Shared.NUM_MONSTERS) {
				return Realm.monsterData[type];
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	public void load() {
		MonsterData md = data();
		maxHP = md.maxHP;
		sprite = md.sprite;
		spriteSet = md.sprite;
		hp = maxHP;
	}

}
