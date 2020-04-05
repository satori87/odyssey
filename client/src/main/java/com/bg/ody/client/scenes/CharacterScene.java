package com.bg.ody.client.scenes;

import com.badlogic.gdx.graphics.Color;
import com.bg.bearplane.gui.Button;
import com.bg.bearplane.gui.Frame;
import com.bg.bearplane.gui.Label;
import com.bg.bearplane.gui.Scene;
import com.bg.ody.client.core.Odyssey;
import com.bg.ody.shared.Shared;

public class CharacterScene extends Scene {

	public Label lblName;

	public String name = "";

	public void start() {
		super.start();
		int hw = Shared.GAME_WIDTH / 2;
		int hh = Shared.GAME_HEIGHT / 2;

		frames.add(new Frame(this, hw, hh, 512, 512, true, true));

		labels.add(new Label(this, hw - 128, hh + 64, 2f, "Name:", Color.WHITE, true));
		lblName = new Label(this, hw, hh + 64 - 16, 2f, "", Color.WHITE, false);
		labels.add(lblName);
		// labels.add(new Label(game, hw-256, hh + 256+48, 2f, "Password", Color.WHITE,
		// true));

		// frames.add(new Frame(game,hw,hh,512,512,true,true));

		// user = new TextBox(game, 0, 16, true, hw - 16, hh - 128, 384);
		// textboxes.add(user);
		// pass = new TextBox(game, 1, 16, false, hw - 16, hh, 384);
		// pass.allowSpecial = true;
		// textboxes.add(pass);

		buttons.add(new Button(this, 0, hw, hh - 224 + 8, 384, 48, "Play"));
		buttons.add(new Button(this, 1, hw, hh - 224 + 8 + 64, 384, 48, "New Character"));
		buttons.add(new Button(this, 2, hw, hh - 224 + 8 + 128, 384, 48, "Disconnect"));

	}

	public void update() {
		super.update();
		lblName.text = name;
		// Log.debug(game.input.mouseX +"," + game.input.mouseY);
	}

	public void render() {
		super.render();
		// game.drawFont(0, Shared.GAME_WIDTH / 2, Shared.GAME_HEIGHT / 2, (progress *
		// 100) + "%", true, 3f);
	}

	@Override
	public void buttonPressed(int id) {
		switch (id) {
		case 0:
			// play
			Odyssey.game.play();
			break;
		case 1:
			// new
			Scene.change("newCharacter");
			break;
		case 2:
			Odyssey.game.connected = false;
			Odyssey.game.close();
			break;
		}
	}

	@Override
	public void enterPressedInField(int id) {
		// TODO Auto-generated method stub

	}

	public void switchTo() {
		super.switchTo();
		if (name == null || (name != null && name.length() == 0)) {
			buttons.get(0).disabled = true;
		} else {
			lblName.text = name;
			buttons.get(0).disabled = false;
		}
	}
}
