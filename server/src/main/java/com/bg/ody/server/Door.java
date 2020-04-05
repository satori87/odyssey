package com.bg.ody.server;

import com.bg.ody.shared.DoorData;
import com.bg.ody.shared.Registrar.DoorSync;

public class Door extends DoorData {

	Map map;

	public Door(Map map, int id, int type, int x, int y, int d, int openTime, int flags, int key) {
		super(id, type, x, y, d, openTime, flags, key);
		this.map = map;
	}

	public void update(long tick) {

		this.tick = tick;
		if(state > 0) {
			if(tick > stamp) {				
				if(state == 1) {
					open = true;
					state = 0;
					sync();
				} else if (state == 2) {
					open = false;
					state = 0;
					sync();
				}
			}
		}
	}

	public DoorSync getSync() {
		DoorSync ds = new DoorSync(id, state, open, stamp - tick);
		return ds;
	}
	
	public void open() {
		state = 1;
		stamp = tick + openTime;
		sync();
	}
	
	public void sync() {
		map.send(getSync());
	}
	
	public void close() {
		state = 2;
		stamp = tick + openTime;
		sync();
	}

}
