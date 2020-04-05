package com.bg.ody.server;

import com.bg.bearplane.ai.FlatTiledNode;

public class ServerTile extends FlatTiledNode {

	public int[] att = new int[2];
	public int[][] attData = new int[2][6];
	public boolean[] wall = new boolean[5];

	public ServerTile(int x, int y) {
		super(x, y);

	}

	public void clearWalls() {
		for (int a = 0; a < 5; a++) {
			wall[a] = false;
		}
	}
}
