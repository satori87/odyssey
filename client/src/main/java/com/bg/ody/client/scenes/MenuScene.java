package com.bg.ody.client.scenes;

import com.badlogic.gdx.graphics.Color;
import com.bg.bearplane.gui.Button;
import com.bg.bearplane.gui.Frame;
import com.bg.bearplane.gui.Label;
import com.bg.bearplane.gui.Scene;
import com.bg.ody.shared.Shared;

public class MenuScene extends Scene {

	public static String status = "";
	public Label lblStatus;

	public MenuScene() {

	}

	public void start() {
		super.start();
		int hw = Shared.GAME_WIDTH / 2;
		int hh = Shared.GAME_HEIGHT / 2;
		int y = 64;

		lblStatus = new Label(this, hw, hh + 256, 2f, "", Color.WHITE, true);
		labels.add(lblStatus);
		frames.add(new Frame(this, hw, hh - 60 + y, 288, 392, true, true));

		labels.add(new Label(this, hw, hh - 182 + y, 3f, "Odyssey", Color.WHITE, true));

		buttons.add(new Button(this, 0, hw, hh - 96 + y, 256, 48, "Login"));
		buttons.add(new Button(this, 1, hw, hh - 32 + y, 256, 48, "New Account"));
		//buttons.add(new Button(this, 2, hw, hh + 32 + y, 256, 48, ""));
		buttons.add(new Button(this, 3, hw, hh + 96 + y, 256, 48, "Quit"));

	}

	public void update() {
		super.update();
	}

	public void render() {
		super.render();
		lblStatus.text = status;
	}

	@Override
	public void buttonPressed(int id) {		
		switch (id) {
		case 0:
			Scene.change("login");
			LoginScene.newAcct = false;
			break;
		case 1:
			LoginScene.newAcct = true;
			Scene.change("login");
			break;
		case 2:

			break;
		case 3:
			//Odyssey.game.dispose();
			System.exit(0);
			break;
		}
	}

	@Override
	public void enterPressedInField(int id) {
		// TODO Auto-generated method stub

	}

}
