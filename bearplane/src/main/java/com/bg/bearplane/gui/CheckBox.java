package com.bg.bearplane.gui;

import com.bg.bearplane.engine.BearGame;
import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.Log;

public class CheckBox {

	Scene scene;

	public int id = 0;

	public boolean disabled = false;

	public int x = 0;
	public int y = 0;

	public boolean toggled = false;

	public CheckBox(Scene scene, int id, int x, int y) {
		this.scene = scene;
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public void update(long tick) {
		try {
			int mX = Scene.input.mouseX;
			int mY = Scene.input.mouseY;
			if (disabled) {
				return;
			}
			if (Scene.input.mouseDown[0]) {
				if (BearTool.inCenteredBox(mX, mY, x, y, 13, 13)) {
					if (Scene.input.wasMouseJustClicked[0]) {
						Scene.input.wasMouseJustClicked[0] = false;
						toggled = !toggled;
						scene.checkBox(id);
					}
				}
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void render() {
		try {
			// render thyself, peasant
			scene.drawRegion(BearGame.assets.frame[17 + (toggled ? 1 : 0)], x - 6, y - 6, false, 0, 1);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

}
