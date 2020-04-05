package com.bg.ody.client.scenes;

import com.bg.bearplane.gui.Button;
import com.bg.bearplane.gui.ListBox;
import com.bg.bearplane.gui.Scene;
import com.bg.ody.client.core.Odyssey;
import com.bg.ody.client.core.World;

public class EditListScene extends Scene {

	public ListBox list;

	public void start() {
		super.start();
		started = false;
		listBoxes.clear();
		list = new ListBox(this, 0, 320, 120, 384, 512);
		listBoxes.add(list);
		switch (Odyssey.game.editType) {
		case 1:
			break;
		case 2:
			for (int i = 0; i < 255; i++) {
				list.list.add((i + 1) + ": " + World.monsterData[i].name);
			}
			break;
		}
		buttons.add(new Button(this, 0, 512 - 170, 720, 224, 48, "Cancel"));
		buttons.add(new Button(this, 1, 512 + 170, 720, 224, 48, "Edit"));
	}

	public void update() {
		super.update();

	}

	public void confirm() {
		if (list.sel >= 0 && list.sel < 255) {
			Odyssey.game.editNum = list.sel;
			Odyssey.game.listScroll = list.scroll;
			change("editMonster");
			Odyssey.editMonsterScene.load(World.monsterData[list.sel]);
		}
	}

	@Override
	public void switchTo() {
		super.switchTo();
		list.sel = Odyssey.game.editNum;
		list.scroll = Odyssey.game.listScroll;
	}

	@Override
	public void enterPressedInList(int i) {
		confirm();
	}

	public void cancel() {
		change("play");
	}

	public void render() {
		super.render();
	}

	@Override
	public void buttonPressed(int id) {
		if (id == 0) {
			cancel();
			Scene.input.wasMouseJustClicked[0] = false;
		} else {
			confirm();
		}
	}

	@Override
	public void enterPressedInField(int id) {

	}

}
