package com.bg.ody.client.scenes;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.bg.bearplane.engine.DrawTask;
import com.bg.bearplane.gui.Label;
import com.bg.bearplane.gui.Scene;
import com.bg.ody.client.core.Assets;
import com.bg.ody.client.core.Realm;
import com.bg.ody.shared.MapData;
import com.bg.ody.shared.PMap;
import com.bg.ody.shared.PTile;
import com.bg.ody.shared.Shared;
import com.bg.ody.shared.Tile;
import com.kotcrab.vis.ui.widget.color.ColorPicker;

public class RenderEditMapScene extends Scene {

	int editMode = 0;
	boolean tileBoxing = false;
	boolean tileBox = false;
	boolean mapBoxing = false;
	int boxSet = 0;
	int boxDown = 0;
	int boxButton = 0;
	int boxStart = 0;
	int boxEnd = 0;
	int boxUp = 0;
	boolean halting = false; // dont do any more mouse move stuff until a mouseUp
	long leftCoolDown = 0;
	public boolean shift = false;
	int curHeight = 1;
	int curElevation = 0;
	int att = 1;
	int[] attData = new int[10];
	boolean[] vis = new boolean[10];
	int curSelTile = 0;
	int curSelSet = 0;
	int curWall = 0;
	boolean wallShadow = true;
	int oldSet = 0;
	int oldTile = 0;
	int curWallMod = 0;

	int curShadow = 0;

	long scrollStamp = 0;
	public int scrollX = 0;
	public int scrollY = 0;
	public Label lblTileSet;
	Label lblName;
	int curSet = 0;
	int[] recentTiles = new int[12];
	int[] recentSets = new int[12];

	ArrayList<DrawTask> drawList = new ArrayList<DrawTask>();

	// int doorMode = 0;
	boolean walling = false;
	int wallType = 0;
	int wallX = 0;
	int wallY = 0;
	int wallButton = 0;

	public boolean copy = false;

	public boolean isMountain(int set, int tile) {
		if (set == 0) {
			switch ((tile - 1) / 16) {
			case 8:
				return true;
			}
		}
		return false;
	}

	boolean inBounds(int x, int y) {
		return (x >= 0 && y >= 0 && x < Shared.MAP_WIDTH && y < Shared.MAP_WIDTH);
	}

	public boolean isCaveable(int set, int tile) {
		if (set == 0) {
			switch ((tile - 1) / 16) {
			case 9:
				return true;
			}
		}
		return false;
	}

	public void start() {
		super.start();

	}

	public MapData map() {
		return Realm.map();
	}

	public PMap pmap() {
		return Realm.pmap();
	}

	@Override
	public void buttonPressed(int id) {
	}

	@Override
	public void enterPressedInField(int id) {

	}

	void drawWallPreview(int mx, int my, int piece) {
		Texture w = Assets.textures.get("wallsel");
		int tx = mx;
		int ty = my;
		int dx = tx * 32 + 20;
		int dy = ty * 32 + 40;
		if (tx >= 0 && tx < Shared.MAP_WIDTH && tx >= 0 && tx < Shared.MAP_WIDTH) {
			draw(w, dx, dy, piece * 32, 0, 32, 32);
		}
	}

	void drawWallsPreview() {
		int scx = input.mouseX;
		int scy = input.mouseY;
		int mx = (scx - 20) / 32;
		int my = (scy - 40) / 32;
		int dmx = (scx - 20) - mx * 32;
		int dmy = (scy - 40) - my * 32;
		if (mapBoxing) {
			return;
		}
		if (walling) {
			switch (wallType) {
			case 0:
				drawWallPreview(mx, wallY, 0);
				if (!shift)
					drawWallPreview(mx, wallY - 1, 1);
				break;
			case 1:
				drawWallPreview(mx, wallY, 1);
				if (!shift)
					drawWallPreview(mx, wallY + 1, 0);
				break;
			case 2:
				drawWallPreview(wallX, my, 2);
				if (!shift)
					drawWallPreview(wallX - 1, my, 3);
				break;
			case 3:
				drawWallPreview(wallX, my, 3);
				if (!shift)
					drawWallPreview(wallX + 1, my, 2);
				break;
			case 4:
				drawWallPreview(mx, my, 0);
				drawWallPreview(mx, my, 1);
				drawWallPreview(mx, my, 2);
				drawWallPreview(mx, my, 3);
				if (!shift) {
					drawWallPreview(mx + 1, my, 2);
					drawWallPreview(mx, my - 1, 1);
					drawWallPreview(mx - 1, my, 3);
					drawWallPreview(mx, my + 1, 0);
				}
				break;
			case 5:
				drawWallPreview(mx, my, 0);
				if (!shift)
					drawWallPreview(mx, my - 1, 1);
				drawWallPreview(mx, my, 2);
				if (!shift)
					drawWallPreview(mx - 1, my, 3);
				break;
			case 6:
				drawWallPreview(mx, my, 0);
				if (!shift)
					drawWallPreview(mx, my - 1, 1);
				drawWallPreview(mx, my, 3);
				if (!shift)
					drawWallPreview(mx + 1, my, 2);
				break;
			case 7:
				drawWallPreview(mx, my, 1);
				if (!shift)
					drawWallPreview(mx, my + 1, 0);
				drawWallPreview(mx, my, 2);
				if (!shift)
					drawWallPreview(mx - 1, my, 3);
				break;
			case 8:
				drawWallPreview(mx, my, 1);
				if (!shift)
					drawWallPreview(mx, my + 1, 0);
				drawWallPreview(mx, my, 3);
				if (!shift)
					drawWallPreview(mx + 1, my, 2);
				break;
			}
		} else {
			if (dmy < 8) {
				drawWallPreview(mx, my, 0);
				if (!shift)
					drawWallPreview(mx, my - 1, 1);

			} else if (dmy >= 24) {
				drawWallPreview(mx, my, 1);
				if (!shift)
					drawWallPreview(mx, my + 1, 0);
			}
			if (dmx < 8) {
				drawWallPreview(mx, my, 2);
				if (!shift)
					drawWallPreview(mx - 1, my, 3);
			} else if (dmx >= 24) {
				drawWallPreview(mx, my, 3);
				if (!shift)
					drawWallPreview(mx + 1, my, 2);
			}
			if (dmx >= 8 && dmx < 24) { // in the middle x-wise
				if (dmy >= 8 && dmy < 24) { // in the middle y-wise
					// WHOLE TILE
					drawWallPreview(mx, my, 0);
					drawWallPreview(mx, my, 1);
					drawWallPreview(mx, my, 2);
					drawWallPreview(mx, my, 3);
					if (!shift) {
						drawWallPreview(mx + 1, my, 2);
						drawWallPreview(mx, my - 1, 1);
						drawWallPreview(mx - 1, my, 3);
						drawWallPreview(mx, my + 1, 0);
					}
				}
			}
		}
	}

	void drawPreview() {
		int scx = input.mouseX;
		int scy = input.mouseY;
		int mx = (scx - 20) / 32;
		int my = (scy - 40) / 32;
		int dmx = 0;
		int dmy = 0;
		if (shift && editMode > 0) {
			dmx = (scx - 20) - mx * 32;
			dmy = (scy - 40) - my * 32;
		}
		int dx = 0;
		int dy = 0;
		if (tileBox) {
			// draw a sample of what it will look like
			int sx = boxStart % 16;
			int sy = boxStart / 16;
			int ex = boxEnd % 16;
			int ey = boxEnd / 16;
			for (int bx = sx; bx <= ex; bx++) {
				for (int by = sy; by <= ey; by++) {
					dx = mx + (bx - sx);
					dy = my + (by - sy);
					if (dx >= 0 && dy >= 0 && dx < 16 && dy < 16) {
						drawMapTile(boxSet, by * 16 + bx + 1, dx, dy, dmx, dmy);
					}
				}
			}
			drawBox(mx * 32 - sx * 32 + 20 + dmx, my * 32 - sy * 32 + 40 + dmy);
		} else if (mapBoxing) {
			int sx = boxStart % 16;
			int sy = boxStart / 16;
			int ex = boxEnd % 16;
			int ey = boxEnd / 16;
			if (boxButton == 0) {
				for (int bx = sx; bx <= ex; bx++) {
					for (int by = sy; by <= ey; by++) {
						dx = bx;
						dy = by;
						if (dx >= 0 && dy >= 0 && dx < 16 && dy < 16) {
							if (editMode < 7) {
								drawMapTile(curSelSet, curSelTile, dx, dy, 0, 0);
							} else if (editMode == 9) {
								Texture a = Assets.textures.get("att");
								int s = shift ? 4 : -4;
								int cx = ((att - 1) % 16) * 32;
								int cy = ((att - 1) / 16) * 32;
								draw(a, dx * 32 + s + 20, dy * 32 + s + 40, cx, cy, 32, 32);
							}
						}
					}
				}
			}
			drawBox(20, 40);
		} else if (curSelTile > 0 && editMode < 7) {
			drawMapTile(curSelSet, curSelTile, mx, my, dmx, dmy);
			draw(Assets.textures.get("sel"), mx * 32 + dmx + 20, my * 32 + dmy + 40, 0, 0, 32, 32);
		} else if (editMode == 9) {

		}

	}

	void drawRecent() {
		for (int i = 0; i < 12; i++) {
			drawTile(recentSets[i], recentTiles[i], 554 + 64 + 5 + i * 32, 585 + 1);
			if (recentSets[i] == curSelSet && recentTiles[i] == curSelTile && curSelTile > 0) {
				draw(Assets.textures.get("sel"), 554 + 64 + 5 + i * 32, 585 + 1, 0, 0, 32, 32);
			}
		}
	}

	void drawBox(int ox, int oy) {
		int dx = 0;
		int dy = 0;
		int w = 16;
		int sx = boxStart % w;
		int sy = boxStart / w;
		int ex = boxEnd % w;
		int ey = boxEnd / w;
		if (ex - sx == 0 && ey - sy == 0) {
			// single sel
			dx = sx * 32 + ox;
			dy = sy * 32 + oy;
			draw(Assets.textures.get("sel"), dx, dy, 0, 0, 32, 32);
		} else {
			// do corners first
			dx = sx * 32 + ox;
			dy = sy * 32 + oy;
			draw(Assets.textures.get("bigsel"), dx, dy, 0, 0, 32, 32); // top left
			dx = ex * 32 + ox;
			dy = sy * 32 + oy;
			draw(Assets.textures.get("bigsel"), dx, dy, 64, 0, 32, 32); // top right
			dx = sx * 32 + ox;
			dy = ey * 32 + oy;
			draw(Assets.textures.get("bigsel"), dx, dy, 0, 64, 32, 32); // bottom left
			dx = ex * 32 + ox;
			dy = ey * 32 + oy;
			draw(Assets.textures.get("bigsel"), dx, dy, 64, 64, 32, 32); // bottom right
			for (int x = sx + 1; x < ex; x++) { // top part
				dx = x * 32 + ox;
				dy = sy * 32 + oy;
				draw(Assets.textures.get("bigsel"), dx, dy, 32, 0, 32, 32);
			}
			for (int y = sy + 1; y < ey; y++) { // left part
				dx = sx * 32 + ox;
				dy = y * 32 + oy;
				draw(Assets.textures.get("bigsel"), dx, dy, 0, 32, 32, 32);
			}
			for (int x = sx + 1; x < ex; x++) { // bottom part
				dx = x * 32 + ox;
				dy = ey * 32 + oy;
				draw(Assets.textures.get("bigsel"), dx, dy, 32, 64, 32, 32);
			}
			for (int y = sy + 1; y < ey; y++) { // right part
				dx = ex * 32 + ox;
				dy = y * 32 + oy;
				draw(Assets.textures.get("bigsel"), dx, dy, 64, 32, 32, 32);
			}
		}
	}

	void clipToTiles() {
		clip(559, 40, 512, 512);
	}

	public int curHoverX = 0;
	public int curHoverY = 0;

	void drawTiles() {
		int dx = 0;
		int dy = 0;
		if (editMode < 7) {
			drawCurTile();
			drawRecent();
			clipToTiles();
			Texture t = Assets.textures.get(Shared.tilesets[curSet]);
			draw(t, 559, 40, 0, 0, t.getWidth(), t.getHeight());
			endClip();
			if ((tileBoxing || tileBox)) {
				drawBox(559, 40);
			} else if (curSet == curSelSet && curSelTile > 0) {
				dx = ((curSelTile - 1) % 16) * 32 + 559;
				dy = ((curSelTile - 1) / 16) * 32 + 40;
				draw(Assets.textures.get("sel"), dx, dy, 0, 0, 32, 32);
			}
			// endClip();
		} else if (editMode == 7) {
			Texture t = Assets.textures.get("wallshortcuts");
			draw(t, 559, 40, 0, 0, 512, 512);
			dx = ((curWall) % 16) * 32 + 559;
			dy = ((curWall) / 16) * 32 + 40;
			draw(Assets.textures.get("sel"), dx, dy, 0, 0, 32, 32);
			dx = ((curWallMod) % 16) * 32 + 559;
			dy = ((curWallMod) / 16) * 32 + 40 + (9 * 32);
			draw(Assets.textures.get("sel"), dx, dy, 0, 0, 32, 32);
			if(wallShadow) {
				draw(Assets.textures.get("sel"), 559, 40+160, 0, 0, 32, 32);
			} else {
				draw(Assets.textures.get("sel"), 559, 40+160+32, 0, 0, 32, 32);
			}
		} else if (editMode == 8) {
			Texture t = Assets.textures.get("shadow");
			draw(t, 559, 40, 0, 0, 256, 32);
			drawCurTile();
		} else if (editMode == 9) {
			Texture a = Assets.textures.get("att");
			draw(a, 559, 40, 0, 0, 512, 128);
			int ax = ((att - 1) % 16) * 32;
			int ay = ((att - 1) / 16) * 32;
			int s = shift ? 4 : -4;
			draw(a, 559 + s, 586 + s, ax, ay, 32, 32);
		}

	}

	public void drawTile(int s, int tile, int x, int y) {
		if (s == -1) {
			return;
		}
		Texture set = Assets.textures.get(Shared.tilesets[s]);
		if (tile > 0) {
			tile--;
			int tileX = (tile % 16) * 32;
			int tileY = (tile / 16) * 32;
			draw(set, x, y, tileX, tileY, 32, 32);
		}
	}

	public void drawMapTile(int s, int tile, int x, int y, int dmx, int dmy) {
		drawTile(s, tile, 20 + x * 32 + dmx, 40 + y * 32 + dmy);
	}

	public void drawCurTile() {
		if (editMode < 7) {
			drawTile(curSelSet, curSelTile, 559, 586);
		} else if (editMode == 8) {
			Texture t = Assets.textures.get("shadow");
			draw(t, 559, 586, curShadow * 32, 0, 32, 32);
		}
	}

	void drawShadows() {
		int mx = 0;
		int my = 0;
		Texture s = Assets.textures.get("shadow");
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				mx = x + scrollX;
				my = y + scrollY;
				if (inBounds(mx, my)) {
					for (int a = 0; a < 8; a++) {
						for (int b = 0; b < 8; b++) {
							if (map().shadow[mx * 8 + a][my * 8 + b]) {
								draw(s, x * 32 + a * 4 + 20, y * 32 + b * 4 + 40, 14, 14, 4, 4);
							}
						}
					}
				}
			}
		}
	}

	void drawAtt() {
		Texture a = Assets.textures.get("att");
		int s = 0;
		int cx = 0;
		int cy = 0;
		int mx = 0;
		int my = 0;
		Tile t = null;
		int d = 0;
		MapData md = null;
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				mx = x + scrollX;
				my = y + scrollY;
				t = null;
				if (inBounds(mx, my)) {
					t = map().tile[mx][my];
				} else {
					d = -1;
					if (mx < 0 && my >= 0 && my < Shared.MAP_WIDTH) {
						d = 2;
					} else if (mx >= Shared.MAP_WIDTH && my >= 0 && my < Shared.MAP_WIDTH) {
						d = 3;
					} else if (my < 0 && mx >= 0 && mx < Shared.MAP_WIDTH) {
						d = 0;
					} else if (my >= Shared.MAP_WIDTH && mx >= 0 && mx < Shared.MAP_WIDTH) {
						d = 1;
					}
					if (d >= 0) {
						if (mx < 0)
							mx += Shared.MAP_WIDTH;
						if (mx >= Shared.MAP_WIDTH)
							mx -= Shared.MAP_WIDTH;
						if (my < 0)
							my += Shared.MAP_WIDTH;
						if (my >= Shared.MAP_WIDTH)
							my -= Shared.MAP_WIDTH;
						md = Realm.getNeighbor(Realm.curMap, d);
						if (md != null) {
							t = md.tile[mx][my];
						}
					}
				}
				if (t != null) {
					for (int i = 0; i < 2; i++) {
						if (t.att[i] > 0) {
							s = i == 1 ? 4 : -4;
							cx = ((t.att[i] - 1) % 16) * 32;
							cy = ((t.att[i] - 1) / 16) * 32;
							draw(a, x * 32 + 20 + s, y * 32 + 40 + s, cx, cy, 32, 32);
						}
					}
				}
			}
		}
	}

	void drawMapLayer(int i) {
		for (DrawTask d : drawList) {
			if (d.i == i) {
				drawTile(d.set, d.num, d.x - 0 + 20, d.y - 0 + 40);
			}
		}
		if (editMode == i) {
			drawPreview();
		}

	}

	void drawMap() {
		processOrder();
		if (vis[0]) {
			drawMapLayer(0);
			drawMapLayer(7);
			drawMapLayer(9);
		}
		for (int i = 1; i < 7; i++) {
			if (vis[i]) {
				drawMapLayer(i);
				if (i == 3) {
					drawMapLayer(8);
				}
			}
		}
		if (vis[7]) {
			drawWalls();
			if (editMode == 7) {
				drawPreview();
				drawWallsPreview();
			}
		}
		if (vis[8]) {
			drawShadows();
		}
		if (vis[9]) {
			drawAtt();
			drawPreview();
		}
		int hy = curHoverY;
		if (mapMouseY % 32 < 8) {
			hy--;
		}
		if (curHoverX >= 0 && curHoverY >= 0) {
			if (editMode != 7 && editMode != 8) {
				draw(Assets.textures.get("sel"), (curHoverX - scrollX) * 32 + 20, (curHoverY - scrollY) * 32 + 40, 0, 0,
						32, 32);
			} else if (editMode == 7) {
				if (curWall > 0) {
					if (curWallMod == 0) {
						for (int i = 0; i < curHeight; i++) {
							draw(Assets.textures.get("sel"), (curHoverX - scrollX) * 32 + 20,
									(hy - scrollY) * 32 + 40 - i * 32, 0, 0, 32, 32);
						}
					} else {
						draw(Assets.textures.get("sel"), (curHoverX - scrollX) * 32 + 20,
								(hy - scrollY) * 32 + 40 - (curHeight - 1) * 32, 0, 0, 32, 32);
					}
				}
			}
		}
		if (scrollX < 0) {
			for (int y = 0; y < 16; y++) {
				draw(Assets.textures.get("bigsel"), (scrollX * -32) + 20 - 32, y * 32 + 40, 64, 32, 32, 32);
			}
		} else if (scrollX >= Shared.MAP_WIDTH - 12) {
			int a = 0;
			for (int y = 0; y < 16; y++) {
				a = ((scrollX - 12) * 32) + 20;
				draw(Assets.textures.get("bigsel"), a, y * 32 + 40, 0, 32, 32, 32);
			}
		}
		if (scrollY < 0) {
			for (int x = 0; x < 16; x++) {
				draw(Assets.textures.get("bigsel"), x * 32 + 20, (scrollY * -32) + 40 - 32, 32, 64, 32, 32);
			}
		} else if (scrollY >= Shared.MAP_WIDTH - 12) {
			int a = 0;
			for (int x = 0; x < 16; x++) {
				a = ((scrollY - 12) * 32) + 40;
				draw(Assets.textures.get("bigsel"), x * 32 + 20, a, 32, 0, 32, 32);
			}
		}
	}

	public int mapMouseX = 0;
	public int mapMouseY = 0;

	void drawWalls() {
		Texture w = Assets.textures.get("wall");
		Tile t = null;
		PTile p = null;
		int mx = 0;
		int my = 0;
		int d = 0;
		MapData md = null;
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				t = null;
				p = null;
				mx = x + scrollX;
				my = y + scrollY;
				if (MapData.inBounds(mx, my)) {
					t = map().tile[mx][my];
					p = pmap().tile[mx][my];
				} else {
					d = -1;
					if (mx < 0 && my >= 0 && my < Shared.MAP_WIDTH) {
						d = 2;
					} else if (mx >= Shared.MAP_WIDTH && my >= 0 && my < Shared.MAP_WIDTH) {
						d = 3;
					} else if (my < 0 && mx >= 0 && mx < Shared.MAP_WIDTH) {
						d = 0;
					} else if (my >= Shared.MAP_WIDTH && mx >= 0 && mx < Shared.MAP_WIDTH) {
						d = 1;
					}
					if (d >= 0) {
						if (mx < 0)
							mx += Shared.MAP_WIDTH;
						if (mx >= Shared.MAP_WIDTH)
							mx -= Shared.MAP_WIDTH;
						if (my < 0)
							my += Shared.MAP_WIDTH;
						if (my >= Shared.MAP_WIDTH)
							my -= Shared.MAP_WIDTH;
						md = Realm.getNeighbor(Realm.curMap, d);
						if (md != null) {
							t = md.tile[mx][my];
							if (Realm.pmap[map().options.exit[d]] != null) {
								p = Realm.pmap[map().options.exit[d]].tile[mx][my];
							}
						}
					}
				}
				if (t != null && p != null) {
					for (int i = 0; i < 5; i++) {
						if (i < 4 && (t.wall[i] || p.wall[i])) {
							if(t.cast[i] || p.cast[i]) {
								draw(w, x * 32 + 20, y * 32 + 40, i * 32, 32, 32, 32);
							} else {
							draw(w, x * 32 + 20, y * 32 + 40, i * 32, 0, 32, 32);}
						}
					}
				}
			}
		}
	}

	void processOrder() {
		drawList.clear();
		processLayer(0);
		processLayer(7);
		processLayer(9);
		for (int i = 1; i < 7; i++) {
			processLayer(i);
			if (i == 3) {
				processLayer(8);
			}
		}

	}

	void processLayer(int i) {
		String a = "";
		int aa = 0;
		ArrayList<ArrayList<DrawTask>> layerList = null;
		layerList = new ArrayList<ArrayList<DrawTask>>();
		for (int y = 0; y < Shared.MAP_WIDTH * 32 + 128; y++) {
			layerList.add(new ArrayList<DrawTask>());
		}
		Tile t = null;
		PTile p = null;
		DrawTask dt = null;
		MapData md = null;
		PMap pd = null;
		int mx = 0;
		int my = 0;
		int d = 0;
		for (int y = 0; y < 26; y++) {
			for (int x = 0; x < 16; x++) {
				t = null;
				p = null;
				mx = x + scrollX;
				my = y + scrollY;
				if (inBounds(mx, my)) {
					t = map().tile[mx][my];
					p = pmap().tile[mx][my];
				} else {
					d = -1;
					if (mx < 0 && my >= 0 && my < Shared.MAP_WIDTH) {
						d = 2;
					} else if (mx >= Shared.MAP_WIDTH && my >= 0 && my < Shared.MAP_WIDTH) {
						d = 3;
					} else if (my < 0 && mx >= 0 && mx < Shared.MAP_WIDTH) {
						d = 0;
					} else if (my >= Shared.MAP_WIDTH && mx >= 0 && mx < Shared.MAP_WIDTH) {
						d = 1;
					}
					if (d >= 0) {
						if (mx < 0)
							mx += Shared.MAP_WIDTH;
						if (mx >= Shared.MAP_WIDTH)
							mx -= Shared.MAP_WIDTH;
						if (my < 0)
							my += Shared.MAP_WIDTH;
						if (my >= Shared.MAP_WIDTH)
							my -= Shared.MAP_WIDTH;
						md = Realm.getNeighbor(Realm.curMap, d);
						if (md != null) {
							t = md.tile[mx][my];
							int exit = map().options.exit[d];
							pd = Realm.pmap[exit];
							if (pd == null) {
							} else {
								p = pd.tile[mx][my];
							}
						}
					}
				}
				if (t != null && p != null) {
					if (i < 7) {
						if (t.tile[i] > 0) {
							dt = new DrawTask(i, t.set[i], t.tile[i], x * 32 + t.shiftX[i], y * 32 + t.shiftY[i]);
							layerList.get(64 + y * 32 + t.shiftY[i]).add(dt);
						}
						if (i == 3) {
							if (p.cave[4] > 0) {
								dt = new DrawTask(i, Shared.getTileSetNum("auto-caves"), p.cave[4], x * 32, y * 32);
								layerList.get(64 + y * 32).add(dt);
							}
							if (p.mtn[0][1] > 0) {
								dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[0][1], x * 32,
										y * 32);
								layerList.get(64 + y * 32).add(dt);
							}
							for (int e = 1; e < 5; e++) {
								if (p.mtn[e][0] > 0) {
									dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[e][0], x * 32,
											y * 32);
									layerList.get(64 + y * 32).add(dt);
								}
								if (p.mtn[e][1] > 0) {
									dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[e][1], x * 32,
											y * 32);
									layerList.get(64 + y * 32).add(dt);
								}
								if (p.mtn[e][2] > 0) {
									dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[e][2], x * 32,
											y * 32);
									layerList.get(64 + y * 32).add(dt);
								}
							}
							for (int h = 0; h < 6; h++) {
								if (t.wallPiece[h][1] > 0) {
									if (t.wallPiece[h][1] > 256) {
										a = ((t.wallPiece[h][1] / 256) + 1) + "";
									} else {
										a = "";
									}
									aa = ((t.wallPiece[h][1] - 1) / 256);
									dt = new DrawTask(i, Shared.getTileSetNum("auto-walls" + a),
											t.wallPiece[h][1] - 256 * aa, x * 32, y * 32 - (h * 32));
									layerList.get(64 + y * 32).add(dt);
								}
							}
						}
					} else if (i == 7) {
						for (int h = 9; h >= 0; h--) {
							for (int b = 0; b < 2; b++) {

								if (p.edge[b][h] > 0) {
									if (p.edge[b][h] > 256) {
										a = (((p.edge[b][h] - 1) / 256) + 1) + "";
									} else {
										a = "";
									}
									aa = ((p.edge[b][h] - 1) / 256);
									dt = new DrawTask(i, Shared.getTileSetNum("auto-terrain" + a),
											p.edge[b][h] - 256 * aa, x * 32, y * 32);
									layerList.get(64 + y * 32).add(dt);
								}
							}
						}
					} else if (i == 8) {
						for (int h = 0; h < 6; h++) {
							for (int b = 2; b < 4; b++) {
								if (t.wallPiece[h][b] > 0) {
									if (t.wallPiece[h][b] > 256) {
										a = ((t.wallPiece[h][b] / 256) + 1) + "";
									} else {
										a = "";
									}
									aa = ((t.wallPiece[h][b] - 1) / 256);
									dt = new DrawTask(i, Shared.getTileSetNum("auto-walls" + a),
											t.wallPiece[h][b] - 256 * aa, x * 32, y * 32 - (h * 32));
									layerList.get(64 + y * 32).add(dt);
								}
							}
						}
					} else if (i == 9) {
						for (int c = 0; c < 4; c++) {
							if (p.cave[c] > 0) {
								dt = new DrawTask(i, Shared.getTileSetNum("auto-caves"), p.cave[c], x * 32, y * 32);
								layerList.get(64 + y * 32).add(dt);
							}
						}
						if (p.mtn[0][0] > 0) {
							dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[0][0], x * 32, y * 32);
							layerList.get(64 + y * 32).add(dt);
						}
						if (p.mtn[0][2] > 0) {
							dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[0][2], x * 32, y * 32);
							layerList.get(64 + y * 32).add(dt);
						}
						for (int h = 0; h < 6; h++) {
							if (t.wallPiece[h][0] > 0) {
								if (t.wallPiece[h][0] > 256) {
									a = ((t.wallPiece[h][0] / 256) + 1) + "";
								} else {
									a = "";
								}
								aa = ((t.wallPiece[h][0] - 1) / 256);
								dt = new DrawTask(i, Shared.getTileSetNum("auto-walls" + a),
										t.wallPiece[h][0] - 256 * aa, x * 32, y * 32 - (h * 32));
								layerList.get(64 + y * 32).add(dt);
							}
						}
					}
				}
			}
		}
		for (ArrayList<DrawTask> sortedY : layerList) {
			for (DrawTask sortedX : sortedY) {
				drawList.add(sortedX);
			}
		}
	}

	public void render() {
		super.render();
		drawTiles();
		clip(20, 40, 512, 512);
		drawMap();
		endClip();
		// draw(Assets.textures.get("tilebar"), 690, 2, 0, 0, 320, 32);
		// draw(Assets.textures.get("sel"), 690 + curSet * 32, 2, 0, 0, 32, 32);
		String options = "Shift: " + (shift ? "On" : "Off");
		if (editMode == 0 || (editMode == 7 && curWall > 0)) {
			if (isMountain(curSelSet, curSelTile) || isCaveable(curSelSet, curSelTile) || curWall > 0) {
				options += " Height: " + curHeight;
				if (isMountain(curSelSet, curSelTile)) {
					options += " Elevation: " + curElevation;
				}
			}
		}
		if (editMode == 7) {

		}
		drawFont(0, 560, 562, options, false, 1);

	}

}
