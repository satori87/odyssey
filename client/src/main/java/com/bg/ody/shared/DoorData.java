package com.bg.ody.shared;

import com.bg.bearplane.engine.BearTool;

public class DoorData {

	public int id = 0;
	public int x = 0;
	public int y = 0;

	public int d = 0;
	public boolean open = false;
	public int state = 0; // 0 = idle 1 = opening 2 = closing

	public int gate = 0;

	public int key = 0;

	public long stamp = 0;
	public long tick = 0;
	public int step = 5;

	public int openTime = 0;

	public int type = 0;

	public DoorData(int id) {
		this.id = id;
	}

	public DoorData(int id, int type, int x, int y, int d, int openTime, int flags, int key) {
		this.id = id;
		this.type = type;
		this.x = x;
		this.y = y;
		this.d = d;
		this.openTime = openTime;
		open = BearTool.checkBit(flags, 0);
		this.key = key;
		tick = System.currentTimeMillis();
		stamp = tick;
		if (BearTool.checkBit(flags, 1)) {
			gate = 1;
		}
	}

	public boolean inRange(int px, int py) {
		if (d == 0) {
			if (x == px && (y == py || y == py - 1)) {
				return true;
			}
		} else if (d == 1) {
			if (x == px && (y == py || y == py - 1)) {
				return true;
			}
		} else if (d == 2) {
			if (y == py && (x == px || x == px + 1)) {
				return true;
			}
		} else if (d == 3) {
			if (y == py && (x == px || x == px - 1)) {
				return true;
			}
		}
		return false;
	}
}
