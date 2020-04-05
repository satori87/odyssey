package com.bg.ody.client.core;

import java.io.File;
import java.util.HashMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.bg.bearplane.engine.BearEssentials;
import com.bg.bearplane.engine.BearNecessities;
import com.bg.bearplane.engine.Log;
import com.bg.ody.shared.MapData;
import com.bg.ody.shared.Shared;

public class Assets extends BearNecessities implements BearEssentials {

	public static Cursor[] cursor = new Cursor[3];
	public static HashMap<String, Texture> textures = new HashMap<String, Texture>();

	static String[] mapnames;
	static String[] effectnames;

	public HashMap<String, String> textureList = new HashMap<String, String>();

	public void preload() {
		try {
			cursor[0] = Gdx.graphics.newCursor(new Pixmap(Gdx.files.local("assets/iface/cur0.png")), 0, 0);
			// FileHandle artFile = Gdx.files.local("assets/textures.def");
			// String text = artFile.readString();
			// List<String> lines = Arrays.asList(text.split("\\r?\\n"));
			// String[] words;
			// for (String curLine : lines) {
			// words = curLine.split(",");
			// textureList.put(words[0], "assets/" + words[1]);
			// }
			// for (String s : textureList.values()) {
			// manager.load(s, Texture.class);
			// }
			manager.setLoader(MapData.class, new MapLoader(new InternalFileHandleResolver()));
			File f = null;
			f = new File(new File(".").getCanonicalPath() + "/maps");
			if (f != null) {
				mapnames = f.list();
				if (mapnames != null) {
					for (String s : mapnames) {
						manager.load("maps/" + s, MapData.class);
					}
				}
			}
			f = new File(new File(".").getCanonicalPath() + "/assets/effects");
			if (f != null) {
				effectnames = f.list();
				if (effectnames != null) {
					for (String s : effectnames) {
						if (s.substring(s.length() - 2).equals(".p")) {
							manager.load("assets/effects/" + s, ParticleEffect.class);
						}
					}
				}
			}
			loadAllPNGFromDir("assets/anim");
			loadAllPNGFromDir("assets/editor");
			loadAllPNGFromDir("assets/iface");
			loadAllPNGFromDir("assets/sprites");
			loadAllPNGFromDir("assets/tiles");
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void loadAllPNGFromDir(String d) {
		try {
			File f = new File(new File(".").getCanonicalPath() + "/" + d);
			String[] dirNames = f.list();
			for (String s : dirNames) {
				if (s.substring(s.length() - 4).equals(".png")) {
					s = s.substring(0, s.length() - 4);
					textureList.put(s, d + "/" + s + ".png");
					manager.load(d + "/" + s + ".png", Texture.class);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load() {
		try {
			String n = "";
			for (String s : textureList.keySet()) {
				textures.put(s, manager.get(textureList.get(s)));
			}
			cursor[1] = Gdx.graphics.newCursor(new Pixmap(Gdx.files.local("assets/iface/cur1.png")), 1, 1);
			cursor[2] = Gdx.graphics.newCursor(new Pixmap(Gdx.files.local("assets/iface/cur2.png")), 1, 1);
			if (mapnames != null) {
				for (String s : mapnames) {
					n = s.substring(3).substring(0, s.length() - 7);
					World.mapData[Integer.parseInt(n)] = manager.get("maps/" + s);
				}
			}
			for (int i = 0; i < Shared.NUM_MAPS; i++) {
				if (World.mapData[i] == null) {
					World.mapData[i] = new MapData();
				}
			}
			if (effectnames != null) {
				for (String s : effectnames) {
					if (s.substring(s.length() - 2).equals(".p")) {
						n = s.substring(0, s.length() - 2);
						ParticleEffect pe = manager.get("assets/effects/" + s);
						pe.flipY();
						World.effectData.put(Integer.parseInt(n), pe);
						ParticleEffectPool pec = new ParticleEffectPool(pe, 1, 20);
						World.effectPool.put(Integer.parseInt(n), pec);
					}
				}
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}
}
