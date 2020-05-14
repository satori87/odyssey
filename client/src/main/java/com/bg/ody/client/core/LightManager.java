package com.bg.ody.client.core;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.Log;
import com.bg.bearplane.gui.Scene;
import com.bg.ody.shared.Shared;
import com.bg.ody.shared.Tile;

import box2dLight.RayHandler;

public class LightManager {
	public static World world;
	public static RayHandler rayHandler;
	private float[] dark;
	private List<DynamicLight> lights;
	private long lightStamp;
	long tick = 0;

	public LightManager() {
		lightStamp = 0L;
		dark = new float[3600];
		for (int i = 0; i < 1800; ++i) {
			dark[i] = 1.0f - i * 5.58E-4f;
			if (dark[i] <= 0.0f) {
				dark[i] = 0.0f;
			}
			if (dark[i] > 1.0f) {
				dark[i] = 1.0f;
			}
		}
		int c = 1799;
		for (int j = 1800; j < 3600; ++j) {
			dark[j] = dark[c];
			--c;
		}
		reset();
	}

	public void reset() {
		world = new World(new Vector2(0, 0), true);
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0, 0, 0, 0.5f);
		lights = new LinkedList<DynamicLight>();
		world = new World(new Vector2(0.0f, 0.0f), true);
		if (rayHandler != null) {
			rayHandler.dispose();
		}
		(rayHandler = new RayHandler(world)).setAmbientLight(0.0f, 0.0f, 0.0f, 1.0f);
		rayHandler.setShadows(true);
		addMapLights();
		addMapShadows();
	}

	public void update(long tick) {
		this.tick = tick;
		setAmbient();
		for (final DynamicLight pl : lights) {
			pl.update(tick);
		}

	}

	public DynamicLight createLight(final int lightRays, final float lightDistance, final float lightAlpha,
			final boolean flickers, final boolean soft, final float softLength, final Body lBody) {
		if (lightDistance == 0.0f || lightAlpha == 0.0f) {
			return null;
		}
		final DynamicLight light = new DynamicLight(this, flickers, rayHandler, lightRays,
				new Color(0.0f, 0.0f, 0.0f, lightAlpha), lightDistance, lBody.getPosition().x, lBody.getPosition().y);
		light.setSoft(true);
		light.setSoftnessLength(1.5f);
		light.attachToBody(lBody);
		light.setIgnoreAttachedBody(true);
		return light;
	}

	void setAmbient() {
		// if (map.data.flags[0]) {
		// rayHandler.setAmbientLight(0.0f, 0.0f, 0.0f, map.datt.attData[b][0] /
		// 255.0f);
		// } else {
		final Calendar rightNow = Calendar.getInstance();
		int m = rightNow.get(12);
		final int s = rightNow.get(13);
		m = 10;
		rayHandler.setAmbientLight(0.0f, 0.0f, 0.0f, dark[m * 60 + s]);
		// }

	}

	public void addWallBody(final Body b, final float w, final float h, final float x, final float y) {
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0.5f;
		fixtureDef.filter.categoryBits = 8;
		fixtureDef.filter.maskBits = 3;
		final PolygonShape p = new PolygonShape();
		p.setAsBox(w / 2.0f, h / 2.0f, new Vector2(x, y), 0.0f);
		fixtureDef.shape = (Shape) p;
		b.createFixture(fixtureDef);
	}

	boolean[][] setShadow(boolean[][] shad, int wx, int wy, boolean value) {
		boolean[][] shadow = shad;
		if (wx >= 0 && wx < Shared.MAP_WIDTH * 8 && wy >= 0 && wy < Shared.MAP_WIDTH * 8) {
			shadow[wx][wy] = value;
		}
		return shadow;
	}

	public void addMapShadows() {
		int wx = 0;
		int wy = 0;
		int x = 0;
		int y = 0;
		int w = 0;
		int h = 0;
		int i = 0;
		Tile t = null;
		boolean[][] shad = new boolean[Shared.MAP_WIDTH * 8][Shared.MAP_WIDTH * 8];
		for (x = 0; x < Shared.MAP_WIDTH; ++x) {
			for (y = 0; y < Shared.MAP_WIDTH; ++y) {
				t = Odyssey.map().tile[x][y];
				if (t.cast[1] || t.decorated) {
					wy = y * 8 - ((t.height - 1) * 8);
					wx = x * 8;
					for (w = 0; w < 8; ++w) {
						if (wx + x >= 0 && wx + x < Shared.MAP_WIDTH * 8 && wy >= 0 && wy < Shared.MAP_WIDTH * 8) {
							shad = setShadow(shad, wx + w, wy, true);
							shad = setShadow(shad, wx + w, wy + 1, true);
						}
					}
				}
				if (t.cast[2]) {
					wy = y * 8;
					wx = x * 8;
					for (i = 0; i <= t.vheight[0]; i++) {
						for (h = 0; h < 8; h++) {
							shad = setShadow(shad, wx, wy + h - i * 8, true);
						}
					}
				}
				if (t.cast[3]) {
					wy = y * 8;
					wx = x * 8 + 7;
					for (i = 0; i <= t.vheight[1]; i++) {
						for (h = 0; h < 8; h++) {
							shad = setShadow(shad, wx, wy + h - i * 8, true);
						}
					}
				}
			}
		}
		for (x = 0; x < 288; ++x) {
			for (y = 0; y < 288; ++y) {
				if (shad[x][y]) {
					// Odyssey.map().shadow[x][y] = shad[x][y];
				}
			}
		}

		for (x = 0; x < Shared.MAP_WIDTH; ++x) {
			for (y = 0; y < Shared.MAP_WIDTH; ++y) {
				t = Odyssey.map().tile[x][y];
				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyDef.BodyType.StaticBody;
				bodyDef.position.set((x * 32 + 16), (y * 32 + 16));
				Body bo = world.createBody(bodyDef);
				for (w = 0; w < 8; ++w) {
					for (h = 0; h < 8; ++h) {
						wx = x * 8 + w;
						wy = y * 8 + h;
						if (Odyssey.map().shadow[wx][wy] || shad[wx][wy]) {
							addWallBody(bo, 4.0f, 4.0f, (float) (w * 4 - 16) + 4f, (float) (h * 4 - 16) + 2f);
						}
					}
				}
			}
		}
	}

	public void addMapLights() {
		if (tick > lightStamp) {
			lightStamp = tick + 200L;
			lights.clear();
			Tile t = Odyssey.map().tile[7][7];
			// t.att[0] = 6;
			// t.attData[0][1] = 250;
			// t.attData[0][2] = 3;
			// t.attData[0][3] = 0;
			// t.attData[0][4] = 0;
			// t.attData[0][5] = 0;
			// t.attData[0][6] = 256;
			// t.attData[0][7] = 0;
			// t.attData[0][8] = 32;
			// t.attData[0][9] = 20;
			for (int x = 0; x < Shared.MAP_WIDTH; x++) {
				for (int y = 0; y < Shared.MAP_WIDTH; y++) {

					// for (int x = (int) Scene.cam.position.x / 32 - 35; x < (int)
					// Scene.cam.position.x / 32 + 35; ++x) {
					// for (int y = (int) Scene.cam.position.y / 32 - 35; y < (int)
					// Scene.cam.position.y / 32 + 35; ++y) {
					// if (x >= 0 && y >= 0 && x < Shared.MAP_WIDTH && y < Shared.MAP_WIDTH) {
					t = Odyssey.map().tile[x][y];

					for (int b = 0; b < 2; ++b) {
						if (t.att[b] == 6) {
							final float dist = t.attData[b][2];
							final int modX = t.attData[b][3];
							final int modY = t.attData[b][4];
							final Color c = new Color(t.attData[b][5] / 255.0f, t.attData[b][6] / 255.0f,
									t.attData[b][7] / 255.0f, t.attData[b][1] / 255.0f);
							final int cx = x * 32 + 16 + modX;
							final int cy = y * 32 + 16 + modY;
							// final float dx = cx / 100.0f;
							// final float dy = cy / 100.0f;
							int r = t.attData[b][8];
							if (r < 128) {
								r = 128;
							}
							// r = 1024;
							final DynamicLight pl = new DynamicLight(this, BearTool.checkBit(t.attData[b][0], 0),
									rayHandler, r, c, dist, cx, cy);
							pl.flickers = BearTool.checkBit(t.attData[b][0], 0);
							pl.setSoft(BearTool.checkBit(t.attData[b][0], 1));
							pl.setSoftnessLength(t.attData[b][9] * 10f);

							pl.setSoftnessLength(20f);
							pl.setSoft(true);

							if (BearTool.checkBit(t.attData[b][0], 2)) {
								pl.setXray(true);
								pl.setStaticLight(false);
							}
							lights.add(pl);
						} /*
							 * } else if (t.attData[b][0] == 6) { final Calendar rightNow =
							 * Calendar.getInstance(); final int m = rightNow.get(12); final int s =
							 * rightNow.get(13); final float d = dark[m * 60 + s]; if (d > 0.0f) { final int
							 * dir = t.attData[b][2]; int fx = 0; final int fy = 0; int angle = 0; if (dir
							 * == 0) { angle = 270; } else if (dir == 1) { fx += 8; angle = 180; } else if
							 * (dir == 2) { angle = 90; } else if (dir == 3) { fx -= 8; angle = 0; } final
							 * ConeLight cl = new ConeLight(rayHandler, 60, new Color(0.0f, 0.0f, 0.0f, d),
							 * t.attData[b][1] / 10.0f, (x * 32 + 16 + fx) / 100.0f, (y * 32 + 16 + fy) /
							 * 100.0f, (float) angle, 50.0f); cl.setSoft(true); cl.setStaticLight(false);
							 * cl.setSoftnessLength(2.5f); coneLights.add(cl); } }
							 */

					}
				}
			}
		}
	}

	public void render() {
		Scene.batcher.end();
		Scene.batcher.begin();
		rayHandler.setCombinedMatrix(Scene.cam);
		rayHandler.updateAndRender();
		Scene.batcher.end();
		Scene.batcher.begin();
	}

}
