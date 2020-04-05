package com.bg.ody.server;

import com.bg.bearplane.engine.Log;

public class Mobile extends GameConnection {

	// public HashMap<String, Object> fields = new HashMap<String, Object>();
	public int x = 0;
	public int y = 0;
	public int map = 0;
	public int dir = 0;
	public long moveStamp = 0;
	int moveTime = 0;
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

	public Mobile(Game game) {
		this.game = game;
	}

	public Map map() {
		return World.map[map];
	}

	public int getMoveTime(boolean run) {
		return 400;
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
