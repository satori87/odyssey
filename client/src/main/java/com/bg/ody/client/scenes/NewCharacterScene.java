package com.bg.ody.client.scenes;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.badlogic.gdx.graphics.Color;
import com.bg.bearplane.gui.Button;
import com.bg.bearplane.gui.Frame;
import com.bg.bearplane.gui.Label;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.gui.TextBox;
import com.bg.ody.client.core.Odyssey;
import com.bg.ody.shared.Shared;
import com.bg.ody.shared.Registrar.NewCharacter;

public class NewCharacterScene extends Scene {

	public TextBox name;

	public NewCharacterScene() {

	}

	public void start() {
		super.start();
		int hw = Shared.GAME_WIDTH / 2;
		int hh = Shared.GAME_HEIGHT / 2;

		frames.add(new Frame(this, hw, hh, 512, 512, true, true));

		labels.add(new Label(this, hw, hh - 128, 2f, "Name", Color.WHITE, true));

		name = new TextBox(this, 0, 16, true, hw - 16, hh - 128, 384, true);
		textBoxes.add(name);

		buttons.add(new Button(this, 0, hw, hh, 384, 48, "Create"));
		buttons.add(new Button(this, 1, hw, hh + 64, 384, 48, "Cancel"));

	}

	public void update() {
		super.update();
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
			// mmmk create
			NewCharacter nc = new NewCharacter();
			nc.name = textBoxes.get(0).text;
			if (Shared.validName(nc.name)) {
				CharacterScene cs = ((CharacterScene) Scene.get("character"));
				int dialogResult = 0;
				if (!cs.name.equals("")) {
					final JDialog dialog = new JDialog();
					dialog.setAlwaysOnTop(true);    
					dialogResult = JOptionPane.showConfirmDialog(dialog,
							"Creating a new character will erase your old one. Are you sure?", "Warning",
							JOptionPane.YES_NO_OPTION);
				}
				if (dialogResult == JOptionPane.YES_OPTION) {
					Odyssey.game.sendTCP(nc);
					lock();
				}
			} else {
				msgBox("Name must be between 3 and 16 alphanumeric characters.");
			}
			break;
		case 1:
			Scene.change("character");
			// cancel
			break;
		}
	}

	@Override
	public void enterPressedInField(int id) {
		// TODO Auto-generated method stub

	}
}
