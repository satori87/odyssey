package com.bg.ody.shared;

public class PTile {
	public int mtn[][] = new int[5][3];
	public int edge[][] = new int[2][10];
	public int cave[] = new int[5];
	public boolean[] wall = new boolean[5];
	public boolean[] cast = new boolean[5];
	public int mtnT[] = new int[5];

	public PTile() {

	}

	public void clearCave() {
		for (int c = 0; c < 5; c++) {
			cave[c] = 0;
		}
	}
}