package com.bg.ody.server;

import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.Log;
import com.bg.ody.shared.Registrar.AttackData;

public class Mobile extends GameConnection {

	// public HashMap<String, Object> fields = new HashMap<String, Object>();
	public int x = 0;
	public int y = 0;
	public int map = 0;
	public int dir = 0;
	public long moveStamp = 0;
	int moveTime = 0;
	int attackTime = 0;
	public long attackStamp = 0;

	boolean warp = false;

	public boolean moved = false;

	public boolean dead = false;
	public long diedAt = 0;

	public long lastMoveAt = 0;

	long tick = 0;

	public int sprite = 1;
	public int spriteSet = 0;

	public int maxHP = 0;
	public int hp = 0;

	public Mobile target = null;

	public Mobile(Game game) {
		this.game = game;
	}

	public boolean playing() {
		return true;
	}

	public boolean inRange(Mobile target, int range) {
		return inRange(target.x, target.y, range);
	}

	public boolean inRange(int nx, int ny, int range) {
		return (int) distanceTo(nx, ny) <= range;
	}

	public double distanceTo(int nx, int ny) {
		return BearTool.distance(x, y, nx, ny);
	}

	public double distanceTo(Mobile target) {
		return distanceTo(target.x, target.y);
	}

	public boolean canAttack(Mobile m) {
		boolean can = true;
		if (m instanceof Monster) {
			// TODO
		} else if (m instanceof Player) {
			// TODO
		}
		return can;
	}

	public boolean adjacentTo(int nx, int ny) {
		boolean a = false;
		if (nx == x && (ny == y + 1 || ny == y - 1)) {
			a = true;
		}
		if (ny == y && (nx == x + 1 || nx == x - 1)) {
			a = true;
		}
		return a;
	}

	public int checkHit(Mobile m) {
		// returns your score of hitting this target
		return BearTool.randInt(1, 100);
	}

	public int checkBlock(Mobile m) {
		// returns your score of blocking this target
		return BearTool.randInt(1, 20);
	}

	public int checkDamage(Mobile m) {
		// returns your damage against this target
		return 5 + BearTool.randInt(0, 5);
	}

	public int checkArmor(Mobile m) {
		// returns your armor against this target
		return BearTool.randInt(2, 3);
	}

	public void attack(Mobile m) {

		if (canAttack(m)) { // then lets check to make sure you can legally attack this target

			if (checkHit(m) > m.checkBlock(this)) {

				int dam = checkDamage(m) - m.checkArmor(this);
				m.hp -= dam;
				AttackData ad = new AttackData(uid, (this instanceof Player), m.uid,( m instanceof Player), dam);
				if (m.hp <= 0) {
					m.die(this);
					ad.deathblow = true;
				}

				// tell everyone else about the attack and its result

				attackStamp = tick + attackTime; // update attack timing and relay timing and attack info to attacker
			}
		}

	}

	public void die(Mobile m) {
		// m killed you
	}

	public Map map() {
		return Realm.map[map];
	}

	public int getMoveTime(boolean run) {
		return 400;
	}

	public int getAttackTime() {
		return 1000;
	}

	public void sync() {

	}

	public void move(int d, boolean run) {
		moved = false;
		int nx = x;
		int ny = y;
		boolean blocked = false;
		moveTime = 0;
		if (d < 4) {
			if (moveStamp < Game.tick) {
				if (d == 0) {
					ny = y - 1;
				} else if (d == 1) {
					ny = y + 1;
				} else if (d == 2) {
					nx = x - 1;
				} else {
					nx = x + 1;
				}
				if (map().isVacantWalls(nx, ny, d, x, y)) {

					if (!map().isVacantElse(nx, ny)) {
						if (this instanceof Monster) {
							blocked = true;
						} else {
							// keep a running tally of these. too many, log it for admins to see possible
							// cheating
						}

					}
					if (!blocked) {
						moveTime = getMoveTime(run);
						moveStamp = Game.tick + moveTime;
						dir = d;
						if (this instanceof Player) {
							if (map().checkWalk((Player) this, nx, ny)) {
								x = nx;
								y = ny;
								moved = true;

							}
						} else {
							x = nx;
							y = ny;
							moved = true;
						}
						lastMoveAt = tick;
					}
				}
			} else {
				Log.error(getID() + " potential speedWalking");
			}
		}
		sync();
	}

}
