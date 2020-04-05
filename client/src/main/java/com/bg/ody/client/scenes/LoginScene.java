package com.bg.ody.client.scenes;

import com.badlogic.gdx.graphics.Color;
import com.bg.bearplane.gui.Button;
import com.bg.bearplane.gui.Frame;
import com.bg.bearplane.gui.Label;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.gui.TextBox;
import com.bg.ody.client.core.Odyssey;
import com.bg.ody.shared.Shared;

public class LoginScene extends Scene {

	TextBox user;
	TextBox pass;

	public static String suser = "bb";
	public static String spass = "";

	public static boolean newAcct = false;

	public void start() {
		super.start();
		int centerX = Shared.GAME_WIDTH / 2;
		int centerY = Shared.GAME_HEIGHT / 2;
		frames.add(new Frame(this, centerX, centerY, 512, 512, true, true));
		labels.add(new Label(this, centerX, centerY - 128 - 16, 2f, "Username", Color.WHITE, true));
		labels.add(new Label(this, centerX, centerY - 16, 2f, "Password", Color.WHITE, true));

		user = new TextBox(this, 0, 16, true, centerX - 16, centerY - 128, 320, true);
		textBoxes.add(user);
		user.max = Shared.MAX_NAME_LEN;
		pass = new TextBox(this, 1, 16, false, centerX - 16, centerY, 320, true);
		pass.allowSpecial = true;
		textBoxes.add(pass);
		pass.max = Shared.MAX_PASS_LEN;
pass.password = true;
		buttons.add(new Button(this, 0, centerX, centerY + 128, 224, 48, "Connect"));
		buttons.add(new Button(this, 1, centerX, centerY + 128 + 64, 224, 48, "Back"));

	}

	public void update() {
		super.update();
	}

	public void render() {
		super.render();
	}

	@Override
	public void buttonPressed(int id) {
		switch (id) {
		case 0:
			// send new account packet

			suser = user.text;
			spass = pass.text;
			connectGame();
			break;
		case 1:
			change("menu");
			break;
		}
	}

	@Override
	public void enterPressedInField(int id) {
		suser = user.text;
		spass = pass.text;
		connectGame();
	}

	void connectGame() {

		// verify username and password first
		String user = textBoxes.get(0).text;
		String pass = textBoxes.get(1).text;
		if (Shared.validUser(user) && Shared.validPass(pass)) {
			lock();
			Odyssey.game.connectGame(Shared.SERVER_IP);
		} else {
			msgBox("Username must be between " + Shared.MIN_NAME_LEN + " and " + Shared.MAX_NAME_LEN
					+ " alphanumeric characters. Passwords must be between " + Shared.MIN_PASS_LEN + " and "
					+ Shared.MAX_PASS_LEN + " characters.");
		}
	}

}
