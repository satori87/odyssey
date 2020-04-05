package com.bg.ody.server;

import java.util.ArrayList;
import java.util.List;
import com.bg.bearplane.engine.Coord;

public class Spawner {

	public Map map;
	public int x = 0;
	public int y = 0;
	public int type = 0;
	public int max = 0;
	public int range = 0;
	public int freqMin = 0;
	public int freqMax = 0;

	public long tick = 0;

	public long nextSpawnAt = 0;

	public List<Monster> monsters = new ArrayList<Monster>();

	public Spawner(Map map, int x, int y, int type, int range, int max, int freqMin, int freqMax) {
		this.map = map;
		this.x = x;
		this.y = y;
		this.type = type;
		this.max = max;
		this.range = range;
		this.freqMin = freqMin;
		this.freqMax = freqMax;
		// this.max = 1;
		tick = System.currentTimeMillis();
		nextSpawnAt = tick + ((int) (Math.random() * (freqMax - freqMin)) + freqMin) * 1000;
	}

	public void remove(Monster m) {
		if (monsters.size() == max) {
			nextSpawnAt = tick + ((int) (Math.random() * (freqMax - freqMin)) + freqMin) * 1000;
		}
		monsters.remove(m);
	}

	public void spawn() {
		nextSpawnAt = tick + ((int) (Math.random() * (freqMax - freqMin)) + freqMin) * 1000;
		Coord c = map.findFreeNode(x, y, range);
		Monster m = map.spawnMonster(type, c.x, c.y);
		if (m != null) {
			m.spawner = this;
			monsters.add(m);
		}
	}

	public void update(long tick) {
		this.tick = tick;
		if (monsters.size() < max) {
			if (tick > nextSpawnAt) {
				spawn();
			}
		}
	}

}
