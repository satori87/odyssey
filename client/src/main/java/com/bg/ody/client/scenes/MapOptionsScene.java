package com.bg.ody.client.scenes;

import com.badlogic.gdx.graphics.Color;
import com.bg.bearplane.gui.Button;
import com.bg.bearplane.gui.CheckBox;
import com.bg.bearplane.gui.Label;
import com.bg.bearplane.gui.ListBox;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.gui.TextBox;
import com.bg.ody.client.core.Odyssey;
import com.bg.ody.client.core.Realm;
import com.bg.ody.shared.MapOptions;
import com.bg.ody.shared.Shared;

public class MapOptionsScene extends Scene {

	int curPK = 0;
	Label lblNum;
	TextBox name;

	// TextBox[] exits = new TextBox[4];

	Label[] lblExits = new Label[4];
	int[] iExits = new int[4];
	Button[] bExits = new Button[4];
	
	CheckBox indoors;
	
	TextBox alpha;

	int map = 0;

	ListBox mapList;

	public void start() {
		super.start();
		int centerX = Shared.GAME_WIDTH / 2;
		lblNum = new Label(this, centerX, 80, 2f, "Map  " + map + " options", Color.WHITE, true);
		labels.add(lblNum);

		labels.add(new Label(this, centerX, 140, 2f, "Name", Color.WHITE, true));
		name = new TextBox(this, 0, 16, true, centerX, 150, 320, true);
		textBoxes.add(name);
		name.allowSpecial = true;

		labels.add(new Label(this, centerX, 250, 2f, "Combat", Color.WHITE, true));
		addButtons(265, 330, 120, 24, 4, new String[] { "Friendly", "Guild Only", "PK" }, new int[] { 0, 1, 2 }, false,
				true);

		labels.add(new Label(this, centerX, 330, 2f, "Exits", Color.WHITE, true));
		for (int i = 0; i < 4; i++) {
			labels.add(new Label(this, 380, 356 + i * 40, 1.4f, Shared.dirNames[i] + ":", Color.WHITE, true));
			lblExits[i] = new Label(this, 460, 356 + i * 40-10, 1.4f, "exit", Color.WHITE, false);
			bExits[i] = new Button(this, i + 100, 300, 356 + i * 40, 48, 24, ">>");
			buttons.add(bExits[i]);
			// exits[i] = new TextBox(this, i + 1, 3, false, 340 + i * 100, 350, 64, true);
			// textBoxes.add(exits[i]);
			// exits[i].allowLetters = false;
			labels.add(lblExits[i]);
			iExits[i] = 0;
		}

		// buttons.add(new Button(this,100,centerX-170,290,40,24,"-",false));
		// buttons.add(new Button(this,100,centerX+170,290,40,24,"+",false));

		buttons.add(new Button(this, 900, centerX - 170, 720, 224, 48, "Discard"));
		buttons.add(new Button(this, 901, centerX + 170, 720, 224, 48, "Ok"));

	}

	@Override
	public void switchTo() {
		map = Realm.curMap;
		MapOptions mo = Odyssey.map().options;
		name.text = mo.name;
		buttonPressed(mo.pk);
		for (int i = 0; i < 4; i++) {
			iExits[i] = mo.exit[i];
			lblExits[i].text = mo.exit[i] + ": " + Realm.mapData[mo.exit[i]].options.name;
		}
		listBoxes.clear();
		mapList = new ListBox(this, 0, 10, 10, 256, 512);
		listBoxes.add(mapList);
		for (int i = 0; i < Shared.NUM_MAPS; i++) {
			if (i == 0) {
				mapList.list.add("NOTHING");
			} else {
				mapList.list.add(i + ": " + Realm.mapData[i].options.name);
			}
		}
		mapList.sel = 0;
		mapList.visible = true;
	}

	public void commit() {
		MapOptions mo = new MapOptions();
		for (int i = 0; i < 4; i++) {
			mo.exit[i] = iExits[i];
		}
		mo.name = name.text;
		mo.pk = curPK;
		mo.id = map;
		Odyssey.map().options = mo;
		change("editMap");
	}

	public void update() {
		super.update();
		lblNum.text = "Map  " + map + " options";
	}

	public void render() {
		super.render();
	}

	@Override
	public void buttonPressed(int id) {
		int mid = 0;
		if (id < 3) {
			for (int i = 0; i < 3; i++) {
				buttons.get(i).toggled = false;
			}
			buttons.get(id).toggled = true;
			curPK = id;
		} else {
			switch (id) {
			case 100:
			case 101:
			case 102:
			case 103:
				mid = id - 100;
				iExits[mid] = mapList.sel;
				lblExits[mid].text = iExits[mid] + ": " + Realm.mapData[iExits[mid]].options.name;
				break;
			case 900:
				change("editMap");
				break;
			case 901:
				commit();
				break;
			}
		}
	}

	@Override
	public void enterPressedInField(int id) {

	}
}
