package com.bg.ody.shared;

import java.io.Serializable;

public class Tile implements Serializable {
	private static final long serialVersionUID = -280115424790111828L;
	public int[] set = new int[7];
	public int[] tile = new int[7];
	public int[] shiftX = new int[7];
	public int[] shiftY = new int[7];
	public int[] att = new int[2];
	public int[][] attData = new int[2][6];
	public boolean[] wall = new boolean[4];
	public int height = 0; // used for cave/mtns/walls
	public int mount[][] = new int[5][2];
	public int wallPiece[][] = new int[6][4];
	public int section = 0;
	public boolean decorated = false;
	public boolean edge = false;

	public Tile() {

	}

	public Tile(int x, int y) {
		// this.x = x;
		// this.y = y;
	}

	public boolean isMountain(int e) {
		return mount[e][1] > 0;
	}

}
