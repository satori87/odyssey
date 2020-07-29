package com.bg.ody.client.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.Log;
import com.bg.bearplane.gui.Button;
import com.bg.bearplane.gui.Label;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.gui.TextBox;
import com.bg.ody.client.core.Assets;
import com.bg.ody.client.core.Odyssey;
import com.bg.ody.shared.ItemData;
import com.bg.ody.shared.Shared;

public class EditItemScene extends Scene {

	int num = 0;

	Label lblNum;
	TextBox name;

	public int curSet = 0;
	public int curSelSet = 0;
	int curSprite = 0;

	public void start() {
		super.start();
		int centerX = Shared.GAME_WIDTH / 2;
		lblNum = new Label(this, centerX, 32, 1f, "Editing item " + num, Color.WHITE, true);
		labels.add(lblNum);

		labels.add(new Label(this, centerX, 550, 1f, "Sprite Sheet", Color.WHITE, true));
		addButtons(70, 670, 120, 24, 4, Shared.spritesets, new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }, false, true);
		buttons.get(0).toggled = true;

		int r = 0;
		int c = 0;
		int cW = 250;

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "Name:", Color.WHITE, false));
		name = new TextBox(this, 0, 16, true, 120 + c * cW, 40 + r * 60, 320, false);
		textBoxes.add(name);
		name.allowSpecial = true;
		r++;

		buttons.add(new Button(this, 900, centerX - 170, 720, 224, 48, "Discard"));
		buttons.add(new Button(this, 901, centerX + 170, 720, 224, 48, "Commit"));

	}

	public void update() {
		super.update();
		lblNum.text = "Editing item  " + num;

		int sx = input.mouseX;
		int sy = input.mouseY;
		int mx = (sx - 32) / 32;
		// int my = (sy - 500) / 32;
		int s = 0;
		if (input.mouseDown[0]) {
			if (BearTool.inBox(sx, sy, 32, 32 + 960, 600, 632)) {
				s = mx + 1;
			} else if (BearTool.inBox(sx, sy, 32, 32 + 960, 632, 664)) {
				s = mx + 31;
			}
		}
		if (s > 0) {
			Texture tex = Assets.textures.get(Shared.spritesets[curSelSet]);
			if (tex.getHeight() > (s - 1) * 32) {
				curSprite = s;
				curSet = curSelSet;
			}
		}
	}

	public void render() {
		super.render();
		drawSprites();
	}

	void drawSprites() {
		Texture tex = Assets.textures.get(Shared.spritesets[curSelSet]);
		for (int i = 0; i < 30; i++) {
			if (tex.getHeight() > i * 32) {
				// draw(tex, 32 + i * 32, 600, 32, 32, step * 32, i * 32, 32, 32);
			}
			if (tex.getHeight() > (i + 30) * 32) {
				// draw(tex, 32 + i * 32, 632, 32, 32, step * 32, (i + 30) * 32, 32, 32);
			}
		}
		if (curSelSet == curSet && curSprite > 0) {
			int dx = ((curSprite - 1) % 30) * 32 + 32;
			int dy = ((curSprite - 1) / 30) * 32 + 600;
			draw(Assets.textures.get("sel"), dx, dy, 0, 0, 32, 32);
		}
	}

	public void load(ItemData md) {
		name.text = md.name;
		num = md.id;
		curSprite = md.sprite;
		curSet = md.spriteSet;
		buttonPressed(curSet);
		curSelSet = curSet;

	}

	@Override
	public void buttonPressed(int id) {
		try {
			if (id < 8) {
				for (int i = 0; i < 8; i++) {
					buttons.get(i).toggled = false;
				}
				buttons.get(id).toggled = true;
				curSelSet = id;
			} else {
				switch (id) {
				case 900:
					change("editList");
					Scene.input.wasMouseJustClicked[0] = false;
					break;
				case 901:
					confirm();
					break;
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
	}

	void confirm() {
		ItemData md = new ItemData();
		md.id = num;
		md.name = name.text;
		md.spriteSet = curSet;
		md.sprite = curSprite;
		Scene.lock();
		Odyssey.game.sendTCP(md);
	}

	@Override
	public void enterPressedInField(int id) {
		confirm();
	}

}
