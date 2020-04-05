package com.bg.ody.client.scenes;

import com.badlogic.gdx.Input.Keys;
import com.bg.bearplane.gui.Scene;

public class TestMapScene extends LiveMapScene {

	void checkKeys() {

		if (character.moveTime == 0) {
			if (input.keyDown[Keys.UP]) {
				character.dir = 0;
			} else if (input.keyDown[Keys.DOWN]) {
				character.dir = 1;
			} else if (input.keyDown[Keys.LEFT]) {
				character.dir = 2;
			} else if (input.keyDown[Keys.RIGHT]) {
				character.dir = 3;
			}
		}
		for (Integer a : Scene.input.keyPress) {
			switch (a) {
			case Keys.F4:
				change("editMap");
				break;
			}
		}
		if (tick > character.moveTimer) {
			if (input.keyDown[Keys.UP]) {
				if (tick - input.keyDownAt[Keys.UP] > 20) {
					character.move(0);
				}
			} else if (input.keyDown[Keys.DOWN]) {
				if (tick - input.keyDownAt[Keys.DOWN] > 20) {
					character.move(1);
				}
			} else if (input.keyDown[Keys.LEFT]) {
				if (tick - input.keyDownAt[Keys.LEFT] > 20) {
					character.move(2);
				}
			} else if (input.keyDown[Keys.RIGHT]) {
				if (tick - input.keyDownAt[Keys.RIGHT] > 20) {
					character.move(3);
				}
			}
		}
	}

	public void start() {
		super.start();
	}

	public void update() {
		super.update();
	}

	public void render() {
		super.render();
	}

	@Override
	public void buttonPressed(int id) {

	}

	@Override
	public void enterPressedInField(int id) {

	}

	public void switchTo() {
		super.switchTo();
	}
}
