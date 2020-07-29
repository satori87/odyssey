package com.bg.ody.client.scenes;

import com.badlogic.gdx.Input.Keys;
import com.bg.bearplane.engine.Log;
import com.bg.bearplane.gui.Scene;
import com.bg.ody.client.core.Odyssey;
import com.bg.ody.shared.Registrar.ChangeDirection;
import com.bg.ody.shared.Registrar.ReqAttack;

public class PlayScene extends LiveMapScene {

	void checkKeys() {
		super.checkKeys();
		int od = character.dir;
		if (!character.dead && tick > character.moveTimer) {
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
				Odyssey.game.adminCommand(1);
				break;
			case Keys.F5:
				Odyssey.game.adminCommand(3);
				break;
			case Keys.F6:
				Odyssey.game.adminCommand(2);
				break;
			}
		}
		if (!character.dead && tick > character.moveTimer) {
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
		if (tick > character.moveTimer) { // we didnt send a move packet
			if (!character.dead && od != character.dir) {
				ChangeDirection cd = new ChangeDirection();
				cd.d = character.dir;
				Odyssey.game.sendTCP(cd);
			}
		}
		if (input.keyDown[Keys.CONTROL_LEFT] || input.keyDown[Keys.CONTROL_RIGHT]) {
			if (!character.dead && tick > character.attackStamp) {
				character.attackStamp = tick + (int) ((float) Odyssey.game.getMe().attackTime * 1.1f);
				ReqAttack ca = new ReqAttack();
				Odyssey.game.getMe().lastReqAttackAt = tick;
				Odyssey.game.sendTCP(ca);
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
		super.enterPressedInField(id);
	}

	public void switchTo() {
		super.switchTo();
	}
}
