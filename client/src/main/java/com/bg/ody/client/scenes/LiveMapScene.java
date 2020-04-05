package com.bg.ody.client.scenes;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.bg.bearplane.engine.DrawTask;
import com.bg.bearplane.engine.Effect;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.gui.TextBox;
import com.bg.ody.client.core.Sprite;
import com.bg.ody.client.core.World;
import com.bg.ody.client.core.Assets;
import com.bg.ody.client.core.ChatEntry;
import com.bg.ody.client.core.Door;
import com.bg.ody.client.core.Monster;
import com.bg.ody.client.core.Odyssey;
import com.bg.ody.shared.MapData;
import com.bg.ody.shared.PMap;
import com.bg.ody.shared.PTile;
import com.bg.ody.shared.Registrar.ChangeDoor;
import com.bg.ody.shared.Shared;
import com.bg.ody.shared.Tile;

public class LiveMapScene extends Scene {

	Sprite character;

	public static OrthographicCamera fCam = new OrthographicCamera();

	FrameBuffer mapBG = new FrameBuffer(Format.RGBA8888, Shared.MAP_WIDTH * 32, Shared.MAP_WIDTH * 32, false);
	FrameBuffer mapFG = new FrameBuffer(Format.RGBA8888, Shared.MAP_WIDTH * 32, Shared.MAP_WIDTH * 32, false);

	ArrayList<DrawTask> drawList = new ArrayList<DrawTask>();

	public boolean processed = false;

	TextBox text;

	public void start() {
		super.start();
		autoCenter = false;
		text = new TextBox(this, 0, 141, true, 0, 0, 555, true);
		text.visible = false;
		text.allowSpecial = true;
		textBoxes.add(text);
		fCam.setToOrtho(false, Shared.MAP_WIDTH * 32, Shared.MAP_WIDTH * 32);
		character = new Sprite(0, 1);
	}

	int mx = 0;
	int my = 0;
	int mmx = 0;
	int mmy = 0;

	public void update() {
		mx = (input.mouseX / 32) + (int) (cam.position.x / 32) - Shared.GAME_WIDTH / 64;
		my = (input.mouseY / 32) + (int) (cam.position.y / 32) - Shared.GAME_HEIGHT / 64;
		mmx = (int) (input.mouseX + cam.position.x - Shared.GAME_WIDTH / 2);
		mmy = (int) (input.mouseY + cam.position.y - Shared.GAME_HEIGHT / 2);
		Odyssey.game.curChatText = text.text;
		checkKeys();
		if (this instanceof PlayScene) {
			character = World.players.get(Odyssey.game.cid);
		} else {
			character.update(tick);
		}
		checkMouse();
		super.update();

	}

	long doorStamp = 0;

	void clickDoor(Door d) {
		if (d.state == 0) {
			if (d.inRange(Odyssey.game.getMe().x, Odyssey.game.getMe().y)) {
				if (tick > doorStamp) {
					doorStamp = tick + 100;
					ChangeDoor cd = new ChangeDoor();
					cd.id = d.id;
					if (d.open) {
						cd.reqState = 2;
					} else {
						cd.reqState = 1;
					}
					Odyssey.game.sendTCP(cd);
				}
			}
		}
	}

	void checkMouse() {
		if (mx >= 0 && mx < Shared.MAP_WIDTH && my >= 0 && my < Shared.MAP_WIDTH) {
			for (Door d : World.doors.values()) {
				d.hover = false;
				if (Math.abs(mmx - d.getTrueX()) < 16) {
					if (Math.abs(mmy - d.getTrueY()) < 16) {
						d.hover = true;
						if (input.mouseDown[0] && input.wasMouseJustClicked[0]) {
							input.wasMouseJustClicked[0] = false;
							clickDoor(d);
						}
					}
				}
			}
		}
	}

	void checkKeys() {
		try {
			// overload me
			for (Integer i : input.keyPress) {
				switch (i) {
				case Keys.PAGE_DOWN:
					Odyssey.game.chatScrollDown();
					break;
				case Keys.PAGE_UP:
					Odyssey.game.chatScrollUp();
					break;
				case Keys.HOME:
					Odyssey.game.toggleChat();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void render() {
		super.render();
		int cx = character.trueX();
		int cy = character.trueY() + 16;

		int lx = Shared.GAME_WIDTH / 2;
		int rx = Shared.MAP_WIDTH * 32 - Shared.GAME_WIDTH / 2;
		int ty = Shared.GAME_HEIGHT / 2;
		int by = Shared.MAP_WIDTH * 32 - Shared.GAME_HEIGHT / 2;

		int ncx = Math.round(cam.position.x);
		int ncy = Math.round(cam.position.y);

		if (cx - ncx > Shared.BOUNDING_BOX * 32) {
			ncx = cx - Shared.BOUNDING_BOX * 32;
		} else if (ncx - cx > (Shared.BOUNDING_BOX + 1) * 32) {
			ncx = cx + (Shared.BOUNDING_BOX + 1) * 32;
		}
		if (cy - ncy > Shared.BOUNDING_BOX * 32) {
			ncy = cy - Shared.BOUNDING_BOX * 32;
		} else if (ncy - cy > Shared.BOUNDING_BOX * 32) {
			ncy = cy + Shared.BOUNDING_BOX * 32;
		}

		if (ncx < lx) {
			// ncx = lx;
		}
		if (ncx > rx) {
			// ncx = rx;
		}
		if (ncy < ty) {
			// ncy = ty;
		}
		if (ncy > by) {
			// ncy = by;
		}
		moveCameraTo(ncx, ncy);
		drawMap();
		drawChat();
	}

	public void drawChat() {
		drawFontAbs(0, 4, 4, Odyssey.game.curChatText, false, 1);
		ChatEntry ce;
		for (int i = 0; i < Odyssey.game.chatLines; i++) {
			ce = Odyssey.game.chatLog[i + Odyssey.game.curChat];
			if (ce.channel >= 0) {
				drawFontAbs(0, 4, Odyssey.game.chatLines * 20 - i * 20, ce.s, false, 1, ce.col);
			}
		}

		if (Odyssey.game.timers.get(250).alt && Odyssey.game.curChatText.length() < 1) {
			drawFontAbs(0, 4, 4, "|" + Odyssey.game.curChatText, false, 1);
		}
	}

	void drawMapLayer(int i) {
		for (DrawTask d : drawList) {
			if (d.i == i) {
				if (d.type == 0) {
					drawTile(d.set, d.num, d.x, d.y);
				} else if (d.type == 1) {
					drawSprite(d.set, d.num, d.f, d.x, d.y);
				} else if (d.type == 3) {
					draw(Assets.textures.get(d.tex), d.x, d.y, d.sx, d.sy, d.w, d.h);
				} else if (d.type == 4) {
					World.effects.get(d.f).fx.draw(Scene.batcher);
				}
			}
		}
	}

	public void drawSprite(int s, int sprite, int f, int x, int y) {
		Texture set = Assets.textures.get(Shared.spritesets[s]);
		if (sprite > 0) {
			sprite--;
			int tileX = f * 32;
			int tileY = sprite * 32;
			draw(set, x, y, tileX, tileY, 32, 32);
		}
	}

	public void drawTile(int s, int tile, int x, int y) {
		Texture set = Assets.textures.get(Shared.tilesets[s]);
		if (tile > 0) {
			tile--;
			int tileX = (tile % 16) * 32;
			int tileY = (tile / 16) * 32;
			draw(set, x, y, tileX, tileY, 32, 32);
		}
	}

	void drawMap() {
		if (!processed) {
			preprocessMap();
			processed = true;
		}
		TextureRegion region;
		region = new TextureRegion(mapBG.getColorBufferTexture(), 0, 0, Shared.MAP_WIDTH * 32, Shared.MAP_WIDTH * 32);
		region.flip(false, true);
		drawRegion(region, 0, 0, false, 0, 1);
		World.renderFX(0);
		drawList.clear();
		processMidLayer();
		drawMapLayer(3);
		World.renderFX(2);
		region = new TextureRegion(mapFG.getColorBufferTexture(), 0, 0, Shared.MAP_WIDTH * 32, Shared.MAP_WIDTH * 32);
		region.flip(false, true);
		drawRegion(region, 0, 0, false, 0, 1);
		World.renderFX(3);
		processTextLayer();
	}

	void preprocessMap() {
		if (Odyssey.pmap() == null) {
			World.pmap[World.curMap] = new PMap();
			Odyssey.map().checkAll(Odyssey.pmap());
		}

		mapBG = new FrameBuffer(Format.RGBA8888, Shared.MAP_WIDTH * 32, Shared.MAP_WIDTH * 32, false);
		mapFG = new FrameBuffer(Format.RGBA8888, Shared.MAP_WIDTH * 32, Shared.MAP_WIDTH * 32, false);

		batcher.end();
		drawList.clear();

		mapBG.begin();
		batcher.begin();

		changeCamera(fCam);
		// draw to bg fb
		processLayer(0);
		processLayer(7);
		processLayer(9);
		processLayer(1);
		processLayer(2);
		drawMapLayer(0);
		drawMapLayer(7);
		drawMapLayer(9);
		drawMapLayer(1);
		drawMapLayer(2);
		batcher.end();
		mapBG.end();

		mapFG.begin();
		batcher.begin();

		changeCamera(fCam);
		drawList.clear();
		// draw to fg fb
		processLayer(8);
		processLayer(4);
		processLayer(5);
		processLayer(6);
		drawMapLayer(8);
		drawMapLayer(4);
		drawMapLayer(5);
		drawMapLayer(6);
		batcher.end();
		mapFG.end();

		batcher.begin();
		changeCamera(cam);

	}

	boolean inSight(int mx, int my) {
		return Math.abs(mx - cam.position.x / 32) < 17 && Math.abs(my - cam.position.y / 32) < 21;
	}

	void processTextLayer() {
		String s = "";
		if (this instanceof PlayScene) {
			for (Sprite c : World.players.values()) {
				if (c.map == World.curMap) {
					drawFont(0, c.trueX() + 16, c.trueY() - 8, c.name, true, 1f);
				}
			}
			for (Monster c : World.monsters.values()) {
				if (c.map == World.curMap) {
					drawFont(0, c.trueX() + 16, c.trueY() - 8, c.name, true, 1f);
				}
			}
			for (Door c : World.doors.values()) {
				if (c.hover) {
					if (c.state == 0) {
						if (c.open) {
							s = "Close Door";
						} else {
							s = "Open Door";
						}
					} else if (c.state == 1) {
						s = "Opening";
					} else {
						s = "Closing";
					}
					Color col = Color.RED;
					if (c.inRange(Odyssey.game.getMe().x, Odyssey.game.getMe().y)) {
						col = Color.WHITE;
					}
					drawFont(0, c.getTrueX(), c.getTrueY(), s, true, 1f, col);
				}
			}
		}
	}

	public MapData map() {
		return World.map();
	}

	public PMap pmap() {
		return World.pmap();
	}

	void processMidLayer() {
		int i = 3;
		int aa = 0;
		String a = "";
		ArrayList<ArrayList<DrawTask>> layerList = null;
		layerList = new ArrayList<ArrayList<DrawTask>>();
		for (int y = 0; y < Shared.MAP_WIDTH * 32 + 64; y++) {
			layerList.add(new ArrayList<DrawTask>());
		}
		Tile t = null;
		PTile p = null;
		DrawTask dt = null;
		int mx = 0;
		int my = 0;
		int ly = 0;
		if (this instanceof PlayScene) {
			for (Effect f : World.effects) {
				if (f.fx != null && f.visible && f.i == 1) {
					dt = f.render();
					ly = 32 + f.getTrueY();
					if (ly >= 0 && ly < Shared.MAP_WIDTH * 32 + 64) {
						layerList.get(ly - 16).add(dt);
					}
				}
			}
			for (Sprite c : World.players.values()) {
				if (c.map == World.curMap) {
					dt = new DrawTask(i, c.set, c.sprite, c.getFrame(), c.trueX(), c.trueY());
					ly = 32 + c.trueY();
					if (ly >= 0 && ly < Shared.MAP_WIDTH * 32 + 64) {
						layerList.get(ly).add(dt);
					}
				}
			}
			for (Monster c : World.monsters.values()) {
				if (c.map == World.curMap) {
					dt = new DrawTask(i, c.set, c.sprite, c.getFrame(), c.trueX(), c.trueY());
					ly = 32 + c.trueY();
					if (ly >= 0 && ly < Shared.MAP_WIDTH * 32 + 64) {
						layerList.get(ly).add(dt);
					}
				}
			}
			for (Door c : World.doors.values()) {
				dt = c.getDrawTask();
				ly = c.getZOrder();
				if (dt != null && ly >= 0 && ly < Shared.MAP_WIDTH * 32 + 64) {
					layerList.get(ly).add(dt);
				}
			}
		} else {
			dt = new DrawTask(i, character.set, character.sprite, character.getFrame(), character.trueX(),
					character.trueY());
			layerList.get(32 + character.trueY() + 16).add(dt);
		}
		for (int y = 0; y < Shared.MAP_WIDTH; y++) {
			for (int x = 0; x < Shared.MAP_WIDTH; x++) {
				mx = x;
				my = y;
				if (inSight(mx, my) && MapData.inBounds(mx, my)) {
					t = map().tile[mx][my];
					p = pmap().tile[mx][my];
					if (t.tile[i] > 0) {
						dt = new DrawTask(i, t.set[i], t.tile[i], x * 32 + t.shiftX[i], y * 32 + t.shiftY[i]);
						layerList.get(32 + y * 32 + t.shiftY[i]).add(dt);
					}
					if (p.cave[4] > 0) {
						dt = new DrawTask(i, Shared.getTileSetNum("auto-caves"), p.cave[4], x * 32, y * 32);
						layerList.get(32 + y * 32).add(dt);
					}
					if (p.mtn[0][1] > 0) {
						dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[0][1], x * 32, y * 32);
						layerList.get(32 + y * 32).add(dt);
					}
					for (int e = 1; e < 5; e++) {
						if (p.mtn[e][0] > 0) {
							dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[e][0], x * 32, y * 32);
							layerList.get(32 + y * 32).add(dt);
						}
						if (p.mtn[e][1] > 0) {
							dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[e][1], x * 32, y * 32);
							layerList.get(32 + y * 32).add(dt);
						}
						if (p.mtn[e][2] > 0) {
							dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[e][2], x * 32, y * 32);
							layerList.get(32 + y * 32).add(dt);
						}
					}
					for (int h = 0; h < 6; h++) {
						for (int b = 0; b < 2; b++) {
							if (t.wallPiece[h][b] > 0) {
								if (t.wallPiece[h][b] > 256) {
									a = ((t.wallPiece[h][b] / 256) + 1) + "";
								} else {
									a = "";
								}
								aa = ((t.wallPiece[h][b] - 1) / 256);
								dt = new DrawTask(i, Shared.getTileSetNum("auto-walls" + a),
										t.wallPiece[h][b] - 256 * aa, x * 32, y * 32 - (h * 32));
								layerList.get(y * 32 + 32).add(dt);
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

	void processLayer(int i) {
		String a = "";
		int aa = 0;
		ArrayList<ArrayList<DrawTask>> layerList = null;
		layerList = new ArrayList<ArrayList<DrawTask>>();
		for (int y = 0; y < Shared.MAP_WIDTH * 32; y++) {
			layerList.add(new ArrayList<DrawTask>());
		}
		Tile t = null;
		PTile p = null;
		DrawTask dt = null;
		for (int y = 0; y < Shared.MAP_WIDTH; y++) {
			for (int x = 0; x < Shared.MAP_WIDTH; x++) {
				t = map().tile[x][y];
				p = pmap().tile[x][y];
				if (i < 7) {
					if (t.tile[i] > 0) {
						dt = new DrawTask(i, t.set[i], t.tile[i], x * 32 + t.shiftX[i], y * 32 + t.shiftY[i]);
						layerList.get(y * 32 + t.shiftY[i]).add(dt);
					}
				} else if (i == 7) {
					for (int h = 9; h >= 0; h--) {
						for (int b = 0; b < 2; b++) {
							if (p.edge[b][h] > 0) {
								if (p.edge[b][h] > 256) {
									a = ((p.edge[b][h] / 256) + 1) + "";
								} else {
									a = "";
								}
								aa = ((p.edge[b][h] - 1) / 256);
								dt = new DrawTask(i, Shared.getTileSetNum("auto-terrain" + a), p.edge[b][h] - 256 * aa,
										x * 32, y * 32);
								layerList.get(y * 32).add(dt);
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
								layerList.get(y * 32).add(dt);
							}
						}
					}
				} else if (i == 9) {
					for (int c = 0; c < 4; c++) {
						if (p.cave[c] > 0) {
							dt = new DrawTask(i, Shared.getTileSetNum("auto-caves"), p.cave[c], x * 32, y * 32);
							layerList.get(y * 32).add(dt);
						}
					}
					if (p.mtn[0][0] > 0) {
						dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[0][0], x * 32, y * 32);
						layerList.get(y * 32).add(dt);
					}
					if (p.mtn[0][2] > 0) {
						dt = new DrawTask(i, Shared.getTileSetNum("auto-mountains"), p.mtn[0][2], x * 32, y * 32);
						layerList.get(y * 32).add(dt);
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

	@Override
	public void buttonPressed(int id) {

	}

	@Override
	public void enterPressedInField(int id) {
		Odyssey.game.processTextInput();
		Odyssey.game.curChatText = "";
		text.text = "";
	}

	public void switchTo() {
		super.switchTo();
		Shared.GAME_WIDTH = 1024;
		Gdx.graphics.setWindowedMode(Shared.GAME_WIDTH, Shared.GAME_HEIGHT);
		setupScreen(Shared.GAME_WIDTH, Shared.GAME_HEIGHT);
		int cx = character.trueX() + 16;
		int cy = character.trueY() + 16;
		if (cx < Shared.GAME_WIDTH / 2) {
			cx = Shared.GAME_WIDTH / 2;
		}
		if (cx > Shared.MAP_WIDTH * 32 - Shared.GAME_WIDTH / 2) {
			cx = Shared.MAP_WIDTH * 32 - Shared.GAME_WIDTH / 2;
		}
		if (cy < Shared.GAME_HEIGHT / 2) {
			cy = Shared.GAME_HEIGHT / 2;
		}
		if (cy > Shared.MAP_WIDTH * 32 - Shared.GAME_HEIGHT / 2) {
			cy = Shared.MAP_WIDTH * 32 - Shared.GAME_HEIGHT / 2;
		}
		moveCameraTo(cx, cy);
	}
}
