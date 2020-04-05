package com.bg.ody.server;

import com.bg.bearplane.ai.FlatTiledNode;
import com.bg.bearplane.ai.TiledSmoothableGraphPath;
import com.bg.bearplane.engine.Coord;
import com.bg.bearplane.engine.Log;
import com.bg.ody.shared.MonsterData;
import com.bg.ody.shared.Registrar.MonsterSync;
import com.bg.ody.shared.Shared;

public class Monster extends Mobile {

	public int type = 0;

	public Spawner spawner;

	int state = 0; // 0 = wander, 1 = pursue, 2 = flee, 3 = idle

	// TiledSmoothableGraphPath<FlatTiledNode> path;

	public int pathX = 0;
	public int pathY = 0;
	boolean pathing = false;

	public long wanderStamp = 0;
	public int wanderDelay = 2;

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

	void wander() {
		state = 0;
		Coord c = map().findFreeNode(spawner.x, spawner.y, 50);
		pathX = c.x;
		pathY = c.y;
		wanderStamp = tick + wanderDelay + (int) (Math.random() * 1000) - 500;
		recalc = true;
		pathing = true;

	}

	int pathCount = 0;
	TiledSmoothableGraphPath<FlatTiledNode> path = new TiledSmoothableGraphPath<FlatTiledNode>();
	int curX = 0;
	int curY = 0;
	boolean recalc = false;

	public void calculatePath() {
		recalc = false;
		path.clear();
		map().calculatePath(path, x, y, pathX, pathY);
	}

	public void update(long tick) {
		this.tick = tick;
		if (!pathing) {
			switch (state) {
			case 0: // wander
				if (tick > wanderStamp) {
					wander();
				}
				break;
			}
		} else {
			switch (state) {
			case 0: // wander
				if (arrived() || path.getCount() < 1) {
					pathing = false;
					wanderStamp = tick + wanderDelay + (int) (Math.random() * 1000) - 500;
				}
				break;
			}
		}
		if (pathing) {
			if (recalc) {
				calculatePath();
			}
			if (tick > moveStamp) {
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
						if (state == 0) {
							wander();
						}
					}
				} else {
					wander();
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
				return World.monsterData[type];
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
