package com.bg.ody.shared;

import java.io.Serializable;

public class MapData implements Serializable {

	private static final long serialVersionUID = -1260740057557047017L;
	public int version = 0;
	public Tile[][] tile = new Tile[Shared.MAP_WIDTH][Shared.MAP_WIDTH];
	public MapOptions options = new MapOptions();

	int mmx = 0;
	int mmy = 0;
	int cx = 0;
	int cy = 0;
	int px = 0;
	int py = 0;
	int p = 0;
	int[] edge = new int[4];
	int[] corner = new int[4];

	public MapData() {
		for (int x = 0; x < Shared.MAP_WIDTH; x++) {
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				tile[x][y] = new Tile(x, y);
			}
		}
	}

	void checkMountains(PMap pmap) {
		Tile t = null;
		PTile p = null;
		Tile left = null;
		Tile right = null;
		Tile below = null;
		Tile above = null;
		Tile tleft = null;
		Tile tright = null;
		PTile pleft = null;
		PTile pright = null;
		PTile pbelow = null;
		PTile pabove = null;
		PTile ptleft = null;
		PTile ptright = null;
		PTile pbleft = null;
		PTile pbright = null;
		PTile pcur = null;

		int modT = 0;
		for (int e = 0; e < 5; e++) {
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				for (int x = 0; x < Shared.MAP_WIDTH; x++) {
					p = pmap.tile[x][y];
					p.mtn[e][0] = 0;
					p.mtn[e][1] = 0;
					p.mtn[e][2] = 0;

				}
			}
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				for (int x = 0; x < Shared.MAP_WIDTH; x++) {
					t = tile[x][y];
					p = pmap.tile[x][y];
					if (t.isMountain(e)) {
						modT = (t.mount[e][1] - 1) * 32;
						p.mtnT[e] = modT;
						p.mtn[e][0] = 1 + modT;
						left = getTile(x - 1, y);
						right = getTile(x + 1, y);
						above = getTile(x, y - 1);
						below = getTile(x, y + 1);
						tleft = getTile(x - 1, y - 1);
						tright = getTile(x + 1, y - 1);
						pleft = getPTile(pmap, x - 1, y);
						pright = getPTile(pmap, x + 1, y);
						pabove = getPTile(pmap, x, y - 1);
						pbelow = getPTile(pmap, x, y + 1);
						ptleft = getPTile(pmap, x - 1, y - 1);
						ptright = getPTile(pmap, x + 1, y - 1);

						pleft.mtnT[e] = modT;
						pright.mtnT[e] = modT;
						pabove.mtnT[e] = modT;
						pbelow.mtnT[e] = modT;
						ptleft.mtnT[e] = modT;
						ptright.mtnT[e] = modT;

						if (above != null && !above.isMountain(e)) {
							pabove.mtn[e][1] = 2 + modT;
						}
						if (left != null && !left.isMountain(e)) {
							if (below != null && !below.isMountain(e)) {
								pleft.mtn[e][0] = 8 + modT;
								for (int i = 1; i < t.mount[e][0]; i++) {
									pcur = getPTile(pmap, x - 1, y + i);
									if (pcur != null) {
										if (i == t.mount[e][0] - 1) {
											pcur.mtn[e][0] = 18 + modT;
										} else {
											pcur.mtn[e][0] = 17 + modT;
										}
									}
								}
								pcur = getPTile(pmap, x - 1, y + t.mount[e][0]);
								if (pcur != null) {
									pcur.mtn[e][0] = 19 + modT;
								}
							} else {
								pleft.mtn[e][1] = 4 + modT;
							}
							if (tleft != null && !tleft.isMountain(e) && above != null && !above.isMountain(e)) {
								ptleft.mtn[e][1] = 6 + modT;
							}
						}
						if (right != null && !right.isMountain(e)) {
							if (below != null && !below.isMountain(e)) {
								pright.mtn[e][0] = 9 + modT;
								for (int i = 1; i < t.mount[e][0]; i++) {
									pcur = getPTile(pmap, x + 1, y + i);
									if (pcur != null) {
										if (i == t.mount[e][0] - 1) {
											pcur.mtn[e][0] = 24 + modT;
										} else {
											pcur.mtn[e][0] = 23 + modT;
										}
									}
								}
								pcur = getPTile(pmap, x + 1, y + t.mount[e][0]);
								if (pcur != null) {
									pcur.mtn[e][0] = 25 + modT;
								}
							} else {
								pright.mtn[e][1] = 5 + modT;
							}
							if (tright != null && !tright.isMountain(e) && above != null && !above.isMountain(e)) {
								ptright.mtn[e][1] = 7 + modT;
							}
						}
						if (below != null && !below.isMountain(e)) {
							if (t.mount[e][0] == 1) {
								pbelow.mtn[e][0] = 21 + modT;
							} else {
								pbelow.mtn[e][0] = 3 + modT;
								for (int i = 1; i < t.mount[e][0] - 1; i++) {
									pcur = getPTile(pmap, x, y + 1 + i);
									if (pcur != null) {
										pcur.mtn[e][0] = 20 + modT;
									}
								}
								pcur = getPTile(pmap, x, y + t.mount[e][0]);
								if (pcur != null) {
									pcur.mtn[e][0] = 22 + modT;
								}
							}
						}
					}
				}
			}
			// second pass, fix things
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				for (int x = 0; x < Shared.MAP_WIDTH; x++) {
					p = pmap.tile[x][y];
					pleft = getPTile(pmap, x - 1, y);
					pright = getPTile(pmap, x + 1, y);
					pabove = getPTile(pmap, x, y - 1);
					t = tile[x][y];
					modT = (t.mount[e][1] - 1) * 32;
					if (p.mtn[e][1] > 0 && pabove != null && pabove.mtn[e][1] > 0) {
						if (pleft != null && pleft.mtn[e][1] > 0) {
							p.mtn[e][1] = 0;
							p.mtn[e][0] = 1 + p.mtnT[e];
						}
						if (pright != null && pright.mtn[e][1] > 0) {
							p.mtn[e][1] = 0;
							p.mtn[e][0] = 1 + p.mtnT[e];
						}
					}
				}
			}
			// third pass lulz
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				for (int x = 0; x < Shared.MAP_WIDTH; x++) {
					t = tile[x][y];
					modT = (t.mount[e][1] - 1) * 32;
					p = pmap.tile[x][y];
					if (p.mtn[e][1] % 32 == 4) {
						p.mtn[e][1] = 0;
						p.mtn[e][2] = 4 + p.mtnT[e];
					}
					if (p.mtn[e][1] % 32 == 5) {
						p.mtn[e][1] = 0;
						p.mtn[e][2] = 5 + p.mtnT[e];
					}
				}
			}
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				for (int x = 0; x < Shared.MAP_WIDTH; x++) {
					t = tile[x][y];
					p = pmap.tile[x][y];
					modT = (t.mount[e][1] - 1) * 32;
					pleft = getPTile(pmap, x - 1, y);
					pright = getPTile(pmap, x + 1, y);
					pabove = getPTile(pmap, x, y - 1);
					pbelow = getPTile(pmap, x, y + 1);
					ptleft = getPTile(pmap, x - 1, y - 1);
					ptright = getPTile(pmap, x + 1, y - 1);
					pbleft = getPTile(pmap, x - 1, y + 1);
					pbright = getPTile(pmap, x + 1, y + 1);
					for (int c = 0; c < 3; c++) {
						switch (p.mtn[e][c] % 32) {
						case 3:
						case 17:
						case 18:
						case 20:
						case 21:
						case 22:
						case 23:
						case 24:
							p.wall[4] = true;
							break;
						case 6:
							pbelow.wall[2] = true;
							pbleft.wall[3] = true;
							p.wall[1] = true;
							pbelow.wall[0] = true;
							if (pright.mtn[e][1] == 0) {
								p.wall[3] = true;
								pright.wall[2] = true;
							}
							break;
						case 7:
							pbelow.wall[3] = true;
							pbright.wall[2] = true;
							p.wall[1] = true;
							pbelow.wall[0] = true;

							if (pleft.mtn[e][1] == 0) {
								p.wall[2] = true;
								pleft.wall[3] = true;
							}
							break;
						case 2:
							p.wall[1] = true;
							pbelow.wall[0] = true;
							if (pright.mtn[e][1] == 0) {
								p.wall[3] = true;
								pright.wall[2] = true;
							}
							if (pleft.mtn[e][1] == 0) {
								p.wall[2] = true;
								pleft.wall[3] = true;
							}

							break;
						case 8:
							p.wall[4] = false;

							p.wall[2] = true;
							pleft.wall[3] = true;
							p.wall[1] = true;
							pbelow.wall[0] = true;
						case 4:
							p.wall[4] = false;
							p.wall[2] = true;
							pleft.wall[3] = true;
							break;
						case 9:
							p.wall[4] = false;
							p.wall[3] = true;
							pright.wall[2] = true;
							p.wall[1] = true;
							pbelow.wall[0] = true;
						case 5:
							p.wall[4] = false;
							p.wall[3] = true;
							pright.wall[2] = true;

							break;
						}
					}
				}
			}
		}
	}

	public Tile getTile(int mx, int my) {
		if (inBounds(mx, my)) {
			return tile[mx][my];
		}
		return new Tile();
	}

	public PTile getPTile(PMap pmap, int mx, int my) {
		if (inBounds(mx, my)) {
			return pmap.tile[mx][my];
		}
		return new PTile();
	}

	public static boolean inBounds(int x, int y) {
		return (x >= 0 && y >= 0 && x < Shared.MAP_WIDTH && y < Shared.MAP_WIDTH);
	}

	public void checkAll(PMap pmap) {
		pmap.clear();
		try {
			checkAllEdges(pmap);
			Thread.sleep(1);
			checkCaves(pmap);
			Thread.sleep(1);
			checkMountains(pmap);
			Thread.sleep(1);
			checkEdgeWalls(pmap);

		} catch (Exception e) {

		}
	}

	public void check(PMap pmap, int mx, int my, int scrollX, int scrollY) {
		checkEdges(pmap, scrollX, scrollY);
		checkCaves(pmap);
		checkMountains(pmap);
	}

	void checkCaves(PMap pmap) {
		Tile t = null;
		PTile p = null;
		Tile left = null;
		Tile right = null;
		Tile below = null;
		Tile above = null;
		Tile tleft = null;
		Tile tright = null;
		PTile pleft = null;
		PTile pright = null;
		PTile pbelow = null;
		PTile pabove = null;
		PTile ptleft = null;
		PTile ptright = null;
		PTile pbleft = null;
		PTile pbright = null;
		PTile pcur = null;
		for (int y = 0; y < Shared.MAP_WIDTH; y++) {
			for (int x = 0; x < Shared.MAP_WIDTH; x++) {
				p = pmap.tile[x][y];
				for (int c = 0; c < 5; c++) {
					p.cave[c] = 0;
				}
				p.wall[0] = false;
				p.wall[1] = false;
				p.wall[2] = false;
				p.wall[3] = false;
				p.wall[4] = false;
			}
		}
		int modT = 0;
		for (int i = 0; i < 10; i++) {
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				for (int x = 0; x < Shared.MAP_WIDTH; x++) {
					t = tile[x][y];
					p = pmap.tile[x][y];
					if (isCaveable(t.set[0], t.tile[0])) {
						p.cave[0] = (((t.tile[0] - 145) / 2) * 16) + 1;
					}
				}
			}
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				for (int x = 0; x < Shared.MAP_WIDTH; x++) {
					modT = 0;
					t = tile[x][y];
					p = pmap.tile[x][y];
					if (p.cave[0] % 16 == 1 && t.height == i) {
						modT = (p.cave[0] / 16) * 16;
						above = getNeighbor(x, y, 0);
						below = getNeighbor(x, y, 1);
						left = getNeighbor(x, y, 2);
						right = getNeighbor(x, y, 3);
						tleft = getTile(x - 1, y - 1);
						tright = getTile(x + 1, y - 1);
						pabove = getPTile(pmap, x, y - 1);
						pbelow = getPTile(pmap, x, y + 1);
						pleft = getPTile(pmap, x - 1, y);
						pright = getPTile(pmap, x + 1, y);
						ptleft = getPTile(pmap, x - 1, y - 1);
						ptright = getPTile(pmap, x + 1, y - 1);
						pbleft = getPTile(pmap, x - 1, y + 1);
						pbright = getPTile(pmap, x + 1, y + 1);

						if (above != null && above.height != t.height) {
							pabove.cave[4] = 9 + modT;
							pabove.wall[1] = true;
							p.wall[0] = true;
							if (tleft != null && tleft.height != t.height) {
								ptleft.cave[2] = 10 + modT;
							}
							if (tright != null && tright.height != t.height) {
								ptright.cave[3] = 11 + modT;
							}
						}
						if (left != null && left.height != t.height) {
							pleft.cave[2] = 10 + modT;
							pleft.wall[3] = true;
							p.wall[2] = true;
						}
						if (right != null && right.height != t.height) {
							pright.cave[3] = 11 + modT;
							pright.wall[2] = true;
							p.wall[3] = true;
						}
						if (below != null && below.height != t.height) {
							pbelow.clearCave();
							pbelow.cave[0] = 3 + modT;
							pbelow.wall[4] = true;
							for (int h = 1; h <= t.height; h++) {
								pcur = getPTile(pmap, x, y + h);
								if (pcur != null && h > 1) {
									pcur.cave[0] = 2 + modT;
									pcur.wall[4] = true;
								}
							}
							int h = t.height + 1;
							pcur = getPTile(pmap, x, y + h);
							if (pcur != null && pcur.cave[0] == 0) {
								pcur.cave[1] = 7 + modT;
							}
						}
					}
				}
			}
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				for (int x = 0; x < Shared.MAP_WIDTH; x++) {
					t = tile[x][y];
					p = pmap.tile[x][y];
					if (p.cave[0] % 16 == 1) {
						if (t.height == i) {
							modT = (p.cave[0] / 16) * 16;
							above = getNeighbor(x, y, 0);
							below = getNeighbor(x, y, 1);
							left = getNeighbor(x, y, 2);
							right = getNeighbor(x, y, 3);
							tleft = getTile(x - 1, y - 1);
							tright = getTile(x + 1, y - 1);
							if (below != null && below.height != t.height) {
								for (int h = 1; h <= t.height; h++) {
									pcur = getPTile(pmap, x, y + h);
									if (pcur != null && h > 1) {
										pcur.cave[0] = 2 + modT;
										pcur.wall[4] = true;
									}
									pcur = getPTile(pmap, x - 1, y + h);
									if (pcur != null && (pcur.cave[0] % 16 == 0)) {
										pcur.cave[2] = 4 + modT;
									}
									pcur = getPTile(pmap, x + 1, y + h);
									if (pcur != null && (pcur.cave[0] % 16 == 0)) {
										pcur.cave[3] = 5 + modT;
									}
								}
								int h = t.height + 1;
								pcur = getPTile(pmap, x - 1, y + h);
								if (pcur != null && pcur.cave[1] % 16 != 7 && pcur.cave[0] % 16 != 1
										&& pcur.cave[0] % 16 != 2) {
									pcur.cave[1] = 6 + modT;
								}
								pcur = getPTile(pmap, x + 1, y + h);
								if (pcur != null && pcur.cave[1] % 16 != 7 && pcur.cave[0] % 16 != 1
										&& pcur.cave[0] % 16 != 2) {
									pcur.cave[1] = 8 + modT;
								}
								pcur = getPTile(pmap, x - 1, y + 2);
								if (pcur != null && (pcur.cave[2] % 16 == 4 || pcur.cave[1] % 16 == 6)) {
									pbleft.cave[2] = 4 + modT;
								}
								pcur = getPTile(pmap, x + 1, y + 2);
								if (pcur != null && (pcur.cave[3] % 16 == 5 || pcur.cave[1] % 16 == 8)) {
									pbright.cave[3] = 5 + modT;
								}
							}
						}
					}
				}
			}
		}
	}

	public static boolean isCaveable(int set, int tile) {
		if (set == 0) {
			switch ((tile - 1) / 16) {
			case 9:
				return true;
			}
		}
		return false;
	}

	public Tile getNeighbor(int mx, int my, int dir) {
		if (dir == 0) {
			my--;
		} else if (dir == 1) {
			my++;
		} else if (dir == 2) {
			mx--;
		} else if (dir == 3) {
			mx++;
		} else if (dir == 4) {
			mx--;
			my--;
		} else if (dir == 5) {
			mx++;
			my--;
		} else if (dir == 6) {
			mx--;
			my++;
		} else if (dir == 7) {
			mx++;
			my++;
		}
		if (inBounds(mx, my)) {
			return tile[mx][my];
		}
		return null;
	}

	public boolean isEdgeable(int set, int tile) {
		return getEdgeType(set, tile) < 10;
	}

	public boolean isEdgeWallable(int set, int tile) {
		return set == 0 && (tile == 247 || tile == 246);
		// return false;
	}

	public static int getEdgeType(int set, int tile) {
		if (tile == 0) {
			return 10;
		}
		switch (set) {
		case 0: // terrain
			switch ((tile - 1) / 16) {
			case 0:
				return 0; // snow
			case 1:
			case 2:
			case 3:
				return 1; // grass
			case 4:
				return 2; // sand
			case 5:
				return 3;// dark sand
			case 6:
				return 4; // dirt
			case 7: // terrain version of mtns
				int t = (tile - 1) % 16 + 5; // 5,6,7,8
				if (t > 8 || t < 5) {
					t = 5;
				}
				return t;
			}
			break;
		}
		return 10;
	}

	public void checkEdgeWalls(PMap pmap) {
		Tile t = null;
		PTile p = null;
		boolean f = false;
		for (int x = 0; x < Shared.MAP_WIDTH; x++) {
			for (int y = 0; y < Shared.MAP_WIDTH; y++) {
				p = pmap.tile[x][y];
				t = tile[x][y];
				if (isEdgeWallable(t.set[0], t.tile[0])) {

					f = false;
					for (int b = 0; b < 2; b++) {
						for (int a = 0; a < 10; a++) {
							if (p.edge[b][a] % 32 == 7 || p.edge[b][a] % 32 == 11 || p.edge[b][a] % 32 == 13
									|| p.edge[b][a] % 32 == 14 || p.edge[b][a] % 32 == 15) {
								f = true;
							}
						}
					}
					if (!f) {
						p.wall[4] = true;
					}
				} else {

				}
			}
		}
	}

	public void checkAllEdges(PMap pmap) {
		try {
			Tile t = null;
			PTile p = null;
			for (int x = 0; x < Shared.MAP_WIDTH; x++) {
				for (int y = 0; y < Shared.MAP_WIDTH; y++) {
					p = pmap.tile[x][y];
					for (int h = 0; h < 10; h++) {
						p.edge[0][h] = 0;
						p.edge[1][h] = 0;
					}
				}
			}
			for (int x = 0; x < Shared.MAP_WIDTH; x++) {
				for (int y = 0; y < Shared.MAP_WIDTH; y++) {
					t = tile[x][y];
					if (t.tile[0] > 0) {
						checkEdge(pmap, x, y, t.set[0], t.tile[0]);
					}
				}
			}
		} catch (Exception e) {
		}

	}

	public void checkEdges(PMap pmap, int scrollX, int scrollY) {
		int mx = 0;
		int my = 0;
		Tile t = null;
		PTile p = null;
		for (int x = -4; x < 20; x++) {
			for (int y = -4; y < 20; y++) {
				mx = x + scrollX;
				my = y + scrollY;
				if (inBounds(mx, my)) {
					t = tile[mx][my];
					p = pmap.tile[mx][my];
					for (int h = 0; h < 10; h++) {
						p.edge[0][h] = 0;
						p.edge[1][h] = 0;
					}
				}
			}
		}
		for (int x = -5; x < 21; x++) {
			for (int y = -5; y < 21; y++) {
				mx = x + scrollX;
				my = y + scrollY;
				if (inBounds(mx, my)) {
					t = tile[mx][my];
					if (t.tile[0] > 0) {
						checkEdge(pmap, mx, my, t.set[0], t.tile[0]);
					}
				}
			}
		}
		checkEdgeWalls(pmap);
	}

	void checkEdge(PMap pmap, int mx, int my, int set, int ttile) {
		if (isEdgeable(set, ttile)) {
			for (cx = -1; cx < 2; cx++) {
				for (cy = -1; cy < 2; cy++) {
					mmx = mx + cx;
					mmy = my + cy;
					if (inBounds(mmx, mmy)) {
						// if the tile at mmx and mmy has a LOWER priority than set/tile, call placeEdge
						if (tile[mx][my].edge && getEdgeType(tile[mmx][mmy].set[0],
								tile[mmx][mmy].tile[0]) > getEdgeType(set, ttile)) {
							placeEdge(pmap, mmx, mmy, set, ttile);
						}
					}
				}
			}
		} else if (isEdgeWallable(set, ttile)) {

		}
	}

	void placeEdge(PMap pmap, int mx, int my, int set, int ttile) {
		Tile t = null;
		PTile pt = null;
		if (inBounds(mx, my)) {
			t = tile[mx][my];
			pt = pmap.tile[mx][my];
			int[][] grid = new int[3][3];
			for (px = -1; px < 2; px++) {
				for (py = -1; py < 2; py++) {
					if (inBounds(px + mx, py + my)) {
						t = tile[px + mx][py + my];
						if ((getEdgeType(set, ttile) == getEdgeType(t.set[0], t.tile[0]))) {
							grid[px + 1][py + 1] = 1;
						} else {
							grid[px + 1][py + 1] = 0;
						}
					} else {
						grid[px + 1][py + 1] = 0;
					}
				}
			}
			p = getEdgeType(set, ttile);
			edge[0] = grid[1][0];
			edge[1] = grid[1][2];
			edge[2] = grid[0][1];
			edge[3] = grid[2][1];
			corner[0] = grid[2][0];
			corner[1] = grid[0][2];
			corner[2] = grid[0][0];
			corner[3] = grid[2][2];
			if (grid[1][1] == 0) {
				pt.edge[1][p] = getTerrainEdgeTile(edge, set, ttile) + p * 32;
				pt.edge[0][p] = getTerrainEdgeTile(corner, set, ttile) + 16 + p * 32;
			} else {
				pt.edge[1][p] = 0;
				pt.edge[0][p] = 0;
			}
		}
	}

	int getTerrainEdgeTile(int[] grid, int set, int tile) {
		int i = Shared.edges[grid[0]][grid[1]][grid[2]][grid[3]];
		return i;
	}

}
