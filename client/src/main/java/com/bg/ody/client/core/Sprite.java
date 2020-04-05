package com.bg.ody.client.core;

import com.bg.bearplane.gui.Scene;
import com.bg.ody.shared.Shared;
import com.bg.ody.shared.Registrar.Exit;
import com.bg.ody.shared.Registrar.Move;

public class Sprite {

	long tick = System.currentTimeMillis();

	public String name = "";
	public int set = 0;
	public int sprite = 0;

	public int x = 0;
	public int y = 0;
	public int XO = 0;
	public int YO = 0;
	public int dir = 0;
	public int map = 0;

	public long moveStamp = 0;
	public long moveTimer = 0;
	public int moveTime = 500;
	public int moveDir = 0;
	public int walkStep = 0;
	public long delay = 0;

	long exitStamp = 0;

	public Sprite(int set, int sprite) {
		this.set = set;
		this.sprite = sprite;
		dir = 1;
	}

	public Sprite() {
		dir = 1;
	}

	public int trueX() {
		return x * 32 + XO;
	}

	public int trueY() {
		return y * 32 + YO - 16;
	}

	void exit(int d) {
		if (Odyssey.map().options.exit[d] > 0) {
			// if (tick > exitStamp) {
			int ox = x;
			int oy = y;
			if (d == 0) {
				oy--;
			} else if (d == 1) {
				oy++;
			} else if (d == 2) {
				ox--;
			} else {
				ox++;
			}
			x = ox;
			y = oy;
			XO = 0;
			YO = 0;
			Scene.lock();
			Odyssey.game.sendTCP(new Exit(d, Scene.shifting()));
			moveTime = getMoveTime(Scene.shifting());
			moveStamp = Scene.tick + moveTime;
			moveTimer = moveStamp;
			moveStamp += delay;
			dir = d;
			moveDir = d;
			exitStamp = moveStamp + 100;
			// }
		}
	}

	public void move(int d) {
		int ox = x;
		int oy = y;
		if (d > 3) {
			return;
		}
		if (d == 0) {
			oy--;
		} else if (d == 1) {
			oy++;
		} else if (d == 2) {
			ox--;
		} else {
			ox++;
		}
		if (ox < 0)
			exit(d);
		else if (oy < 0)
			exit(d);
		else if (ox >= Shared.MAP_WIDTH)
			exit(d);
		else if (oy >= Shared.MAP_WIDTH)
			exit(d);
		else if (x != ox || y != oy) {
			if (World.isVacant(ox, oy, d, x, y)) {
				x = ox;
				y = oy;
				XO = 0;
				YO = 0;
				moveTime = getMoveTime(Scene.shifting());
				moveStamp = Scene.tick + moveTime;
				moveTimer = moveStamp;
				moveStamp += delay;
				dir = d;
				moveDir = d;
				if (Odyssey.game.playing) {
					Move m = new Move(d, Scene.shifting());
					Odyssey.game.sendTCP(m);
				}
			}
		}
	}

	public int getMoveTime(boolean shift) {
		return shift ? 200 : 400;
	}

	public int getFrame() {
		return dir * 3 + walkStep;
	}

	public void update(long tick) {
		this.tick = tick;
		int step = 16;
		int spw = 0;
		walkStep = 0;
		if (tick > moveStamp) {
			moveTime = 0;
			XO = 0;
			YO = 0;
			//Log.debug("cool");
		} else {
			long diff = moveStamp - tick;
			float p = ((float) diff / (float) (moveTime + delay)) * 32f;
			if (this instanceof Monster) {
				spw = World.monsterData[((Monster) this).type].stepsPerWalk;
			} else {
				spw = 2;
			}
			if (spw < 2)
				spw = 2;
			step = 32 / spw;
			if (step == 0) {
				walkStep = 0;
			} else {
				walkStep = (Math.round(p / (float)step) % 2);
			}
			if (moveDir == 3) {
				XO = -Math.round(p);
			} else if (moveDir == 2) {
				XO = Math.round(p);
			} else if (moveDir == 0) {
				YO = Math.round(p);
			} else if (moveDir == 1) {
				YO = -Math.round(p);
			}
		}
		// }
	}
}
