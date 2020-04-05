package com.bg.ody.client.core;

import com.badlogic.gdx.graphics.Color;
import com.bg.bearplane.engine.Log;

public class ChatEntry {
	public String s = "";
	public Color col = Color.WHITE;
	public int channel = 0;

	public ChatEntry(int channel, String s, Color col) {
		try {
			this.channel = channel;// 0 = mapsay, 1 = broadcast, 2 = tell, 3 = info, 4 = game
			this.s = s;
			this.col = col;
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}

	}

}
