package com.bg.ody.shared;

public class PMap {

	public PTile[][] tile = new PTile[Shared.MAP_WIDTH][Shared.MAP_WIDTH];

	public PMap() {
		clear();
	}
	
	public void clear() {
		for (int x = 0; x < Shared.MAP_WIDTH; x++) {
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				tile[x][y] = new PTile();
			}
		}
	}

}
