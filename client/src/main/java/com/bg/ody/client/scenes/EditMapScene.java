package com.bg.ody.client.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.Log;
import com.bg.bearplane.gui.Button;
import com.bg.bearplane.gui.CheckBox;
import com.bg.bearplane.gui.Frame;
import com.bg.bearplane.gui.Label;
import com.bg.bearplane.gui.ListBox;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.gui.TextBox;
import com.bg.ody.client.core.Odyssey;
import com.bg.ody.client.core.Realm;
import com.bg.ody.shared.MapData;
import com.bg.ody.shared.PMap;
import com.bg.ody.shared.PTile;
import com.bg.ody.shared.Shared;
import com.bg.ody.shared.Tile;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.bg.ody.shared.Registrar.Chunk;
import com.bg.ody.shared.Registrar.DiscardMap;

public class EditMapScene extends RenderEditMapScene {

	byte[][] chunks = new byte[1][1];

	Label hover;
	public int lastMonsterSel = 0;

	ListBox setList;
	ListBox mapList;
	ListBox monsterList;
	ListBox fxList;

	Frame frmWarp = null;
	Label lblWarpMap;
	Label lblWarpX;
	Label lblWarpY;
	int lastWarpX = 0;
	int lastWarpY = 0;
	int lastWarpMap = 0;

	Frame frmSpawn = null;
	Label lblSpawnType;
	int lastSpawnType = 0;
	TextBox spawnRange;
	TextBox spawnCount;
	TextBox spawnMin;
	TextBox spawnMax;

	Frame frmDoor;
	Button[] doorButton = new Button[4];
	int lastDoorDir = 0;
	int lastDoorFlags = 0;
	CheckBox doorOpen;
	CheckBox doorGate;
	TextBox doorKey;
	TextBox doorTime;
	TextBox doorType;
	// Button[] doorGate = new Button[7];

	Frame frmFX;
	int lastFXScale = 0;
	Label lblFXScale;
	int lastFXLayer = 0;
	Label lblFXLayer;
	int lastFXX = 0;
	Label lblFXX;
	int lastFXY = 0;
	Label lblFXY;
	int lastFXType = 0;
	Label lblFXType;

	Frame frmLight = null;
	int[] lastLight = new int[9];
	Label[] lblLight = new Label[9];
	int[] maxLight = new int[9];
	int[] minLight = new int[9];
	String[] strLight = new String[9];
	int lastLightFlags = 0;
	CheckBox lightIsSoft;
	CheckBox lightIsXRay;
	CheckBox lightFlickers;

	void startLight() {
		int s = 36;
		int i = 0;
		Button b = null;
		frmLight = new Frame(this, 584, 184, 460, 370, false, false, true);
		frames.add(frmLight);
		frmLight.visible = false;
		frmLight.labels.add(new Label(this, 776, 210, 2f, "Light Source", Color.WHITE, true));
		minLight = new int[] { 0, 0, -64, -64, 0, 0, 0, 0, 0 };
		maxLight = new int[] { 255, 1000, 64, 64, 255, 255, 255, 255, 30 };
		strLight = new String[] { "Alpha", "Size", "Mod X", "Mod Y", "Red", "Green", "Blue", "RayCount", "Softness" };
		for (i = 0; i < 9; i++) {
			lblLight[i] = new Label(this, 776, 240 + i * s, 1f, lastLight[i] + "", Color.WHITE, true);
			frmLight.labels.add(lblLight[i]);
			b = new Button(this, 700 + i * 2, 676, 240 + i * s, 32, 24, "-");
			b.interval = 8;
			frmLight.buttons.add(b);
			b = new Button(this, 701 + i * 2, 876, 240 + i * s, 32, 24, "+");
			b.interval = 8;
			frmLight.buttons.add(b);
		}
		i = 1;
		lightIsSoft = new CheckBox(this, 11, 906, 340 + i * s, "Soft Shadows");
		i = 2;
		lightFlickers = new CheckBox(this, 11, 906, 340 + i * s, "Flickers");
		i = 3;
		lightIsXRay = new CheckBox(this, 11, 906, 340 + i * s, "X-Ray");

		frmLight.checkBoxes.add(lightIsSoft);
		frmLight.checkBoxes.add(lightFlickers);
		frmLight.checkBoxes.add(lightIsXRay);
	}

	void startDoor() {
		frmDoor = new Frame(this, 564, 184, 444, 362, false, false, true);
		frames.add(frmDoor);
		frmDoor.visible = false;
		frmDoor.labels.add(new Label(this, 776, 210, 2f, "Door and Lock", Color.WHITE, true));

		doorButton[0] = new Button(this, 500, 910, 340, 64, 24, "Above", true);
		doorButton[1] = new Button(this, 501, 910, 440, 64, 24, "Below", true);
		doorButton[2] = new Button(this, 502, 860, 390, 64, 24, "Left", true);
		doorButton[3] = new Button(this, 503, 960, 390, 64, 24, "Right", true);

		for (int i = 0; i < 4; i++) {
			frmDoor.buttons.add(doorButton[i]);
		}

		getButton(frmDoor.buttons, 500).toggled = true;

		frmDoor.labels.add(new Label(this, 890, 500, 1f, "Starts Open", Color.WHITE, true));
		doorOpen = new CheckBox(this, 10, 950, 500);
		frmDoor.checkBoxes.add(doorOpen);

		frmDoor.labels.add(new Label(this, 890, 530, 1f, "Is Gate", Color.WHITE, true));
		doorGate = new CheckBox(this, 10, 950, 530);
		frmDoor.checkBoxes.add(doorGate);

		int i = 0;
		doorTime = new TextBox(this, 4, 5, false, 580, 320, 80, false, frmDoor);
		doorTime.allowLetters = false;
		doorTime.text = "0";
		frmDoor.textBoxes.add(doorTime);
		frmDoor.labels.add(new Label(this, 580, 320, 1f, "OpenTime ms", Color.WHITE, false));
		i++;
		doorKey = new TextBox(this, 5, 3, false, 580, 320 + i * 70, 80, false, frmDoor);
		doorKey.allowLetters = false;
		doorKey.text = "0";
		frmDoor.textBoxes.add(doorKey);
		frmDoor.labels.add(new Label(this, 580, 320 + i * 70, 1f, "Key #", Color.WHITE, false));
		i++;
		doorType = new TextBox(this, 6, 3, false, 580, 320 + i * 70, 80, false, frmDoor);
		doorType.allowLetters = false;
		doorType.text = "0";
		frmDoor.textBoxes.add(doorType);
		frmDoor.labels.add(new Label(this, 580, 320 + i * 70, 1f, "Style", Color.WHITE, false));

	}

	void startFX() {
		int i = 0;
		int s = 36;
		frmFX = new Frame(this, 584, 284, 384, 262, false, false, true);
		frames.add(frmFX);
		frmFX.visible = false;
		frmFX.labels.add(new Label(this, 776, 310, 2f, "Effect Tile", Color.WHITE, true));
		i = 0;
		lblFXScale = new Label(this, 776, 340 + i * s, 1f, lastFXScale + "", Color.WHITE, true);
		frmFX.labels.add(lblFXScale);
		frmFX.buttons.add(new Button(this, 600 + i * 2, 656, 340 + i * s, 32, 24, "-"));
		frmFX.buttons.add(new Button(this, 601 + i * 2, 896, 340 + i * s, 32, 24, "+"));
		i = 1;
		lblFXLayer = new Label(this, 776, 340 + i * s, 1f, lastFXLayer + "", Color.WHITE, true);
		frmFX.labels.add(lblFXLayer);
		frmFX.buttons.add(new Button(this, 600 + i * 2, 656, 340 + i * s, 32, 24, "-"));
		frmFX.buttons.add(new Button(this, 601 + i * 2, 896, 340 + i * s, 32, 24, "+"));
		i = 2;
		lblFXX = new Label(this, 776, 340 + i * s, 1f, lastFXX + "", Color.WHITE, true);
		frmFX.labels.add(lblFXX);
		frmFX.buttons.add(new Button(this, 600 + i * 2, 656, 340 + i * s, 32, 24, "-"));
		frmFX.buttons.add(new Button(this, 601 + i * 2, 896, 340 + i * s, 32, 24, "+"));
		i = 3;
		lblFXY = new Label(this, 776, 340 + i * s, 1f, lastFXY + "", Color.WHITE, true);
		frmFX.labels.add(lblFXY);
		frmFX.buttons.add(new Button(this, 600 + i * 2, 656, 340 + i * s, 32, 24, "-"));
		frmFX.buttons.add(new Button(this, 601 + i * 2, 896, 340 + i * s, 32, 24, "+"));
		i = 4;
		lblFXType = new Label(this, 776, 340 + i * s, 1f, lastFXType + "", Color.WHITE, true);
		frmFX.labels.add(lblFXType);
		frmFX.buttons.add(new Button(this, 600 + i * 2, 656, 340 + i * s, 32, 24, "-"));
		frmFX.buttons.add(new Button(this, 601 + i * 2, 896, 340 + i * s, 32, 24, "+"));

	}

	void startWarp() {
		int i = 0;
		int s = 36;
		frmWarp = new Frame(this, 584, 284, 384, 262, false, false, true);
		frames.add(frmWarp);
		frmWarp.visible = false;
		frmWarp.labels.add(new Label(this, 776, 310, 2f, "Warp Tile", Color.WHITE, true));
		i = 0;
		lblWarpMap = new Label(this, 776, 340 + i * s, 1f, lastWarpMap + "", Color.WHITE, true);
		frmWarp.labels.add(lblWarpMap);
		frmWarp.buttons.add(new Button(this, 300 + i * 2, 656, 340 + i * s, 32, 24, "-"));
		frmWarp.buttons.add(new Button(this, 301 + i * 2, 896, 340 + i * s, 32, 24, "+"));
		i = 1;
		lblWarpX = new Label(this, 776, 340 + i * s, 1f, lastWarpX + "", Color.WHITE, true);
		frmWarp.labels.add(lblWarpX);
		frmWarp.buttons.add(new Button(this, 300 + i * 2, 656, 340 + i * s, 32, 24, "-"));
		frmWarp.buttons.add(new Button(this, 301 + i * 2, 896, 340 + i * s, 32, 24, "+"));
		i = 2;
		lblWarpY = new Label(this, 776, 340 + i * s, 1f, lastWarpY + "", Color.WHITE, true);
		frmWarp.labels.add(lblWarpY);
		frmWarp.buttons.add(new Button(this, 300 + i * 2, 656, 340 + i * s, 32, 24, "-"));
		frmWarp.buttons.add(new Button(this, 301 + i * 2, 896, 340 + i * s, 32, 24, "+"));
		for (Button b : frmWarp.buttons) {
			b.interval = 10;
		}

	}

	void startSpawn() {
		int i = 0;
		int s = 36;
		frmSpawn = new Frame(this, 584, 284, 384, 262, false, false, true);
		frames.add(frmSpawn);
		frmSpawn.visible = false;
		frmSpawn.labels.add(new Label(this, 776, 310, 2f, "Monster Spawn", Color.WHITE, true));
		i = 0;
		lblSpawnType = new Label(this, 776, 340 + i * s, 1f, lastSpawnType + "", Color.WHITE, true);
		frmSpawn.labels.add(lblSpawnType);
		frmSpawn.buttons.add(new Button(this, 400 + i * 2, 656, 340 + i * s, 32, 24, "-"));
		frmSpawn.buttons.add(new Button(this, 401 + i * 2, 896, 340 + i * s, 32, 24, "+"));
		i = 0;
		spawnRange = new TextBox(this, 3, 2, false, 800, 340 + i * s, 100, true, frmSpawn);
		frmSpawn.labels.add(new Label(this, 680, 380 + i * s, 1f, "Range:", Color.WHITE, true));
		frmSpawn.textBoxes.add(spawnRange);
		spawnRange.text = "0";
		i = 1;
		spawnCount = new TextBox(this, 0, 2, false, 800, 340 + i * s, 100, true, frmSpawn);
		frmSpawn.labels.add(new Label(this, 680, 380 + i * s, 1f, "Count:", Color.WHITE, true));
		frmSpawn.textBoxes.add(spawnCount);
		spawnCount.text = "0";
		i = 2;
		spawnMin = new TextBox(this, 1, 2, false, 800, 340 + i * s, 100, true, frmSpawn);
		frmSpawn.labels.add(new Label(this, 680, 380 + i * s, 1f, "MinFreq s:", Color.WHITE, true));
		frmSpawn.textBoxes.add(spawnMin);
		spawnMin.text = "0";
		i = 3;
		spawnMax = new TextBox(this, 2, 2, false, 800, 340 + i * s, 100, true, frmSpawn);
		frmSpawn.labels.add(new Label(this, 680, 380 + i * s, 1f, "MaxFreq s:", Color.WHITE, true));
		frmSpawn.textBoxes.add(spawnMax);
		spawnMax.text = "0";

	}

	public void start() {
		super.start();
		buttons.clear();
		frames.clear();
		labels.clear();
		Button b = null;
		hover = new Label(this, 840, 564, 1f, "", Color.WHITE, false);
		labels.add(hover);
		frames.add(new Frame(this, 15, 35, 522, 522, false, false));
		frames.add(new Frame(this, 554, 35, 522, 522, false, false));

		frames.add(new Frame(this, 554, 585 - 4, 42, 42, false, false));
		frames.add(new Frame(this, 554 + 64, 585 - 4, 10 + 384, 42, false, false));

		for (int i = 0; i < 5; i++) {
			checkBoxes.add(new CheckBox(this, i, i * 100 + 36, 585));
			buttons.add(new Button(this, i, i * 100 + 84, 585, 72, 24, Shared.layerName[i], true));
		}
		buttons.get(0).toggled = true;
		for (int i = 0; i < 5; i++) {
			checkBoxes.add(new CheckBox(this, i + 5, i * 100 + 36, 585 + 34));
			buttons.add(new Button(this, i + 5, i * 100 + 84, 585 + 34, 72, 24, Shared.layerName[i + 5], true));
		}
		for (int i = 0; i < 10; i++) {
			vis[i] = true;
			checkBoxes.get(i).toggled = true;
		}

		for (int i = 0; i < 6; i++) {
			b = new Button(this, 200 + i, i * 90 + 600 - 90, 740, 72, 24, Shared.panelName[i], false);
			buttons.add(b);
		}

		lblTileSet = new Label(this, 554 + 263, 18, 1f, "Buildings", Color.WHITE, true);
		buttons.add(new Button(this, 100, 554 + 263 - 150, 18, 24, 24, "<"));
		buttons.add(new Button(this, 101, 554 + 263 + 150, 18, 24, 24, ">"));
		labels.add(lblTileSet);
		lblName = new Label(this, 276, 20, 2f, "", Color.WHITE, true);
		labels.add(lblName);
		setupAttPanels();

	}

	@Override
	public void switchTo() {
		try {
			input.mouseDown[0] = false;
			input.wasMouseJustClicked[0] = false;
			super.switchTo();
			Shared.GAME_WIDTH = 1376;
			Gdx.graphics.setWindowedMode(Shared.GAME_WIDTH, Shared.GAME_HEIGHT);
			setupScreen(Shared.GAME_WIDTH, Shared.GAME_HEIGHT);
			scrollX = Math.round(cam.position.x / 32 - Shared.GAME_WIDTH / 64);
			scrollY = Math.round(cam.position.y / 32 - Shared.GAME_HEIGHT / 64);
			moveCameraTo(Shared.GAME_WIDTH / 2, Shared.GAME_HEIGHT / 2);

			listBoxes.clear();

			setList = new ListBox(this, 0, 1104, 35, 256, 530);
			listBoxes.add(setList);
			for (String s : Shared.tilesets) {
				setList.list.add(s);
			}

			mapList = new ListBox(this, 1, 1104, 35, 256, 530);
			mapList.sel = 0;
			for (int i = 0; i < Shared.NUM_MAPS; i++) {
				if (Realm.mapData[i] != null) {
					mapList.list.add(i + ": " + Realm.mapData[i].options.name);
				}
			}
			mapList.sel = Realm.curMap;
			mapList.visible = false;
			listBoxes.add(mapList);

			monsterList = new ListBox(this, 2, 1104, 35, 256, 530);
			monsterList.sel = 0;
			for (int i = 0; i < Shared.NUM_MONSTERS; i++) {
				if (Realm.monsterData[i] != null) {
					monsterList.list.add(i + ": " + Realm.monsterData[i].name);
				}
			}
			monsterList.visible = false;
			monsterList.sel = lastMonsterSel;
			listBoxes.add(monsterList);

			fxList = new ListBox(this, 3, 1104, 35, 256, 530);
			fxList.sel = 0;
			for (int i = 0; i < 255; i++) {
				if (Realm.effectData.get(i) != null) {
					fxList.list.add(i + ": " + Realm.effectData.get(i).getEmitters().get(0).getName());
				}
			}
			fxList.visible = false;
			fxList.sel = lastFXType;
			listBoxes.add(fxList);

			if (editMode == 9) {
				switchAtt(att);
			}

		} catch (Exception e) {
			Log.error(e);
		}
	}

	void setupAttPanels() {
		startWarp();
		startSpawn();
		startDoor();
		startFX();
		startLight();
	}

	@Override
	public void listChanged(int id, int sel) {
		switch (id) {
		case 0: // tileset
			break;
		case 1: // map
			attData[0] = sel;
			lastWarpMap = sel;
			break;
		case 2: // monster
			attData[0] = sel;
			lastSpawnType = sel;
			break;
		case 3: // fx
			attData[4] = sel;
			lastFXType = sel;
		}
	}

	@Override
	public void buttonPressed(int id) {
		super.buttonPressed(id);
		int mid = 0;
		if (id < 10) {
			editMode = id;
			for (int i = 0; i < 10; i++) {
				buttons.get(i).toggled = false;
			}
			buttons.get(id).toggled = true;
			tileBoxing = false;
			tileBox = false;
			mapBoxing = false;
			walling = false;
			halting = false;
			if (editMode == 7) {
				curWall = 0;
			}
			if (editMode != 9) {
				hideFrames();
				setList.visible = true;
			} else {
				setList.visible = false;
			}
		}
		switch (id) {
		case 100:
			curSet--;
			if (curSet < 0) {
				curSet = Shared.tilesets.length - 1;
			}
			setList.sel = curSet;
			tileBoxing = false;
			tileBox = false;
			mapBoxing = false;
			walling = false;
			halting = false;
			break;
		case 101:
			curSet++;
			if (curSet > Shared.tilesets.length - 1) {
				curSet = 0;
			}
			setList.sel = curSet;
			tileBoxing = false;
			tileBox = false;
			mapBoxing = false;
			walling = false;
			halting = false;
			break;
		case 200:
			change("mapOptions");
			break;
		case 201:
			importMap();
			break;
		case 202:
			export();
			break;
		case 203:
			test();
			break;
		case 204:
			discard();
			break;
		case 205: // commit
			commit();
			break;

		}
		if (id >= 300 && id < 400) {
			switch (id % 300) {
			case 0:
				attData[0]--;
				if (attData[0] < 0)
					attData[0] = 0;
				lastWarpMap = attData[0];
				mapList.sel = lastWarpMap;
				break;
			case 1:
				attData[0]++;
				if (attData[0] > 2000)
					attData[0] = 2000;
				lastWarpMap = attData[0];
				mapList.sel = lastWarpMap;
				break;
			case 2:
				attData[1]--;
				if (attData[1] < 0)
					attData[1] = 0;
				lastWarpX = attData[0];
				break;
			case 3:
				attData[1]++;
				if (attData[1] >= Shared.MAP_WIDTH)
					attData[1] = Shared.MAP_WIDTH - 1;
				lastWarpX = attData[0];
				break;
			case 4:
				attData[2]--;
				if (attData[2] < 0)
					attData[2] = 0;
				lastWarpY = attData[0];
				break;
			case 5:
				attData[2]++;
				if (attData[2] >= Shared.MAP_WIDTH)
					attData[2] = Shared.MAP_WIDTH - 1;
				lastWarpY = attData[0];
				break;
			}
		} else if (id >= 400 && id < 500) {
			switch (id % 400) {
			case 0:
				attData[0]--;
				if (attData[0] < 0)
					attData[0] = 0;
				lastSpawnType = attData[0];
				monsterList.sel = lastSpawnType;
				break;
			case 1:
				attData[0]++;
				if (attData[0] >= Shared.NUM_MONSTERS)
					attData[0] = Shared.NUM_MONSTERS - 1;
				lastSpawnType = attData[0];
				monsterList.sel = lastSpawnType;
				break;
			}
		} else if (id >= 500 && id < 504) {
			mid = id % 500;
			for (int i = 0; i < 4; i++) {
				getButton(frmDoor.buttons, i + 500).toggled = false;
			}
			getButton(frmDoor.buttons, id).toggled = true;
			lastDoorDir = mid;
			attData[0] = lastDoorDir;
		} else if (id >= 600 && id < 700) {
			switch (id - 600) {
			case 0:
				attData[0]--;
				if (attData[0] < 0) {
					attData[0] = 0;
				}
				lastFXScale = attData[0];
				break;
			case 1:
				attData[0]++;
				if (attData[0] > 100) {
					attData[0] = 100;
				}
				lastFXScale = attData[0];
				break;
			case 2:
				attData[1]--;
				if (attData[1] < 0) {
					attData[1] = 0;
				}
				lastFXLayer = attData[1];
				break;
			case 3:
				attData[1]++;
				if (attData[1] > 3) {
					attData[1] = 3;
				}
				lastFXLayer = attData[1];
				break;
			case 4:
				attData[2]--;
				if (attData[2] < -64) {
					attData[2] = -64;
				}
				lastFXX = attData[2];
				break;
			case 5:
				attData[2]++;
				if (attData[2] > 64) {
					attData[2] = 64;
				}
				lastFXX = attData[2];
				break;
			case 6:
				attData[3]--;
				if (attData[3] < -64) {
					attData[3] = -64;
				}
				lastFXY = attData[3];
				break;
			case 7:
				attData[3]++;
				if (attData[3] > 64) {
					attData[3] = 64;
				}
				lastFXY = attData[3];
				break;
			case 8:
				attData[4]--;
				if (attData[4] < 0) {
					attData[4] = 0;
				}
				lastFXType = attData[4];
				fxList.sel = lastFXType;
				break;
			case 9:
				attData[4]++;
				if (attData[4] > 100) {
					attData[4] = 100;
				}
				lastFXType = attData[4];
				fxList.sel = lastFXType;
				break;
			}
		} else if (id >= 700 && id < 800) {
			mid = id - 700;
			int i = (mid / 2) + 1;
			if (mid % 2 == 0) {
				attData[i]--;
				if (attData[i] < minLight[i - 1]) {
					attData[i] = minLight[i - 1];
				}
			} else {
				attData[i]++;
				if (attData[i] > maxLight[i - 1]) {
					attData[i] = maxLight[i - 1];
				}
			}
			lastLight[i - 1] = attData[i];
		}
	}

	@Override
	public void enterPressedInField(int id) {
		super.enterPressedInField(id);
	}

	void updateBox() {
		int i = 16;
		int bux = boxUp % i;
		int buy = boxUp / i;
		int bdx = boxDown % i;
		int bdy = boxDown / i;
		int leftX = bux < bdx ? bux : bdx;
		int topY = buy < bdy ? buy : bdy;
		boxStart = topY * i + leftX;
		int rightX = bux < bdx ? bdx : bux;
		int bottomY = buy < bdy ? bdy : buy;
		boxEnd = bottomY * i + rightX;
	}

	void scrollMap() {
		int scrollSpeed = 1;
		if (tick > scrollStamp) {
			scrollStamp = tick + 30;
			if (input.keyDown[Keys.UP]) {
				scrollY -= scrollSpeed;
			} else if (input.keyDown[Keys.DOWN]) {
				scrollY += scrollSpeed;
			}
			if (input.keyDown[Keys.LEFT]) {
				scrollX -= scrollSpeed;
			} else if (input.keyDown[Keys.RIGHT]) {
				scrollX += scrollSpeed;
			}
		}
		if (scrollX < -4) {
			scrollX = -4;
		}
		if (scrollX > Shared.MAP_WIDTH - 12) {
			scrollX = Shared.MAP_WIDTH - 12;
		}
		if (scrollY > Shared.MAP_WIDTH - 12) {
			scrollY = Shared.MAP_WIDTH - 12;
		}
		if (scrollY < -4) {
			scrollY = -4;
		}
	}

	void fill(int mx, int my, int oset, int otile, int set, int tile, int i) {
		if (tile == 0 || (oset == set && otile == tile)) {
			return;
		}
		if (inBounds(mx, my)) {
			map().tile[mx][my].set[i] = set;
			map().tile[mx][my].tile[i] = tile;
			map().tile[mx][my].shiftX[i] = 0;
			map().tile[mx][my].shiftY[i] = 0;
			if (set == 0 && MapData.getEdgeType(set, tile) < 10) {
				map().tile[mx][my].edge = !shift;
			}
			int nx = mx + 1;
			int ny = my;
			if (inBounds(nx, ny) && map().tile[nx][ny].tile[i] == otile && map().tile[nx][ny].set[i] == oset) {
				fill(nx, ny, map().tile[nx][ny].set[i], map().tile[nx][ny].tile[i], set, tile, i);
			}
			nx = mx - 1;
			if (inBounds(nx, ny) && map().tile[nx][ny].tile[i] == otile && map().tile[nx][ny].set[i] == oset) {
				fill(nx, ny, map().tile[nx][ny].set[i], map().tile[nx][ny].tile[i], set, tile, i);
			}
			nx = mx;
			ny = my + 1;
			if (inBounds(nx, ny) && map().tile[nx][ny].tile[i] == otile && map().tile[nx][ny].set[i] == oset) {
				fill(nx, ny, map().tile[nx][ny].set[i], map().tile[nx][ny].tile[i], set, tile, i);
			}
			ny = my - 1;
			if (inBounds(nx, ny) && map().tile[nx][ny].tile[i] == otile && map().tile[nx][ny].set[i] == oset) {
				fill(nx, ny, map().tile[nx][ny].set[i], map().tile[nx][ny].tile[i], set, tile, i);
			}
		}
	}

	void placeWalls(int b) {
		int sx = input.mouseX;
		int sy = input.mouseY;
		int mx = (sx - 20) / 32;
		int my = (sy - 40) / 32;
		int dmx = (sx - 20) - mx * 32;
		int dmy = (sy - 40) - my * 32;
		int i = 0;
		if (dmy < 8 && dmx < 8) {
			i = 5;
		} else if (dmy < 8 && dmx >= 24) {
			i = 6;
		} else if (dmy >= 24 && dmx < 8) {
			i = 7;
		} else if (dmy >= 24 && dmx >= 24) {
			i = 8;
		} else if (dmy < 8) {
			i = 0;
		} else if (dmy >= 24) {
			i = 1;
		} else if (dmx < 8) {
			i = 2;
		} else if (dmx >= 24) {
			i = 3;
		}
		if (dmx >= 8 && dmx < 24) { // in the middle x-wise
			if (dmy >= 8 && dmy < 24) { // in the middle y-wise
				i = 4;
			}
		}
		if (walling) {
			switch (wallType) {
			case 0:
				placeWall(mx, wallY, 0);
				if (!shift)
					placeWall(mx, wallY - 1, 1);
				break;
			case 1:
				placeWall(mx, wallY, 1);
				if (!shift)
					placeWall(mx, wallY + 1, 0);
				break;
			case 2:
				placeWall(wallX, my, 2);
				if (!shift)
					placeWall(wallX - 1, my, 3);
				break;
			case 3:
				placeWall(wallX, my, 3);
				if (!shift)
					placeWall(wallX + 1, my, 2);
				break;
			case 4:
				placeWall(mx, my, 0);
				placeWall(mx, my, 1);
				placeWall(mx, my, 2);
				placeWall(mx, my, 3);
				if (!shift) {
					placeWall(mx + 1, my, 2);
					placeWall(mx, my - 1, 1);
					placeWall(mx - 1, my, 3);
					placeWall(mx, my + 1, 0);
				}
				break;
			case 5:
				placeWall(mx, my, 0);
				if (!shift)
					placeWall(mx, my - 1, 1);
				placeWall(mx, my, 2);
				if (!shift)
					placeWall(mx - 1, my, 3);
				break;
			case 6:
				placeWall(mx, my, 0);
				if (!shift)
					placeWall(mx, my - 1, 1);
				placeWall(mx, my, 3);
				if (!shift)
					placeWall(mx + 1, my, 2);
				break;
			case 7:
				placeWall(mx, my, 1);
				if (!shift)
					placeWall(mx, my + 1, 0);
				placeWall(mx, my, 2);
				if (!shift)
					placeWall(mx - 1, my, 3);
				break;
			case 8:
				placeWall(mx, my, 1);
				if (!shift)
					placeWall(mx, my + 1, 0);
				placeWall(mx, my, 3);
				if (!shift)
					placeWall(mx + 1, my, 2);
				break;

			}
		} else {
			walling = true;
			wallType = i;
			wallX = mx;
			wallY = my;
			wallButton = b;
		}
	}

	void placeWall(int mx, int my, int piece) {
		int tx = mx + scrollX;
		int ty = my + scrollY;
		Tile t = getTile(tx, ty);
		Tile up = getTile(tx, ty - 1);
		Tile down = getTile(tx, ty + 1);
		if (t != null) {
			if (piece < 4) {
				if (curWallMod > 0) {
					if (t.wall[piece]) {

						if (wallButton == 0) {
							t.wall[piece] = true;
							t.cast[piece] = wallShadow ? true : false;
						} else {
							t.wall[piece] = false;
							t.cast[piece] = false;
						}

					}
					if (wallButton == 1) {
						t.decorated = false;
						for (int i = 0; i < 6; i++) {
							for (int b = 0; b < 4; b++) {
								t.wallPiece[i][b] = 0;
							}
						}
					}
				} else {
					if (wallButton == 0) {
						t.wall[piece] = true;
						t.cast[piece] = wallShadow ? true : false;
					} else {
						t.wall[piece] = false;
						t.cast[piece] = false;
						t.decorated = false;
						for (int i = 0; i < 6; i++) {
							for (int b = 0; b < 4; b++) {
								t.wallPiece[i][b] = 0;
							}
						}
					}
				}
				if (curWall > 0) {
					if (t.wall[piece] || t.decorated) {
						int modT = (curWall - 1) * 16;
						if (piece == 1) {
							if (curWallMod == 0) {
								t.height = curHeight;
								t.wallPiece[curHeight - 1][2] = 0;
								for (int i = 0; i < curHeight - 1; i++) {
									t.wallPiece[i][1] = 1 + modT;
								}
								t.wallPiece[curHeight - 1][1] = 2 + modT;
							} else {
								if (curHeight == t.height) {
									t.wallPiece[curHeight - 1][2] = 5 + modT;
									t.wallPiece[curHeight - 1][1] = curWallMod + 9 + modT;
								} else if (curHeight < t.height) {
									t.wallPiece[curHeight - 1][1] = curWallMod + 9 + modT;
								}
								t.decorated = true;
								if (curHeight == 1 && curWallMod < 3) {
									t.wall[1] = false;
									t.cast[1] = false;
									if (down != null) {
										down.wall[0] = false;
										down.cast[0] = false;
									}
								}
							}
						} else if (piece == 2) {
							t.vheight[0] = curHeight;
							for (int i = 0; i < curHeight - 1; i++) {
								t.wallPiece[i][0] = 9 + modT;
							}
							t.wallPiece[curHeight - 1][0] = 7 + modT;
							if (up != null) {
								up.wallPiece[curHeight - 1][3] = 4 + modT;
							}
						} else if (piece == 3) {
							t.vheight[1] = curHeight;
							for (int i = 0; i < curHeight - 1; i++) {
								t.wallPiece[i][0] = 8 + modT;
							}
							t.wallPiece[curHeight - 1][0] = 6 + modT;
							if (up != null) {
								up.wallPiece[curHeight - 1][3] = 3 + modT;
							}
						}
					} else {
						if (piece == 1) {
							for (int i = 0; i < 6; i++) {
								t.wallPiece[i][1] = 0;
								t.wallPiece[i][2] = 0;
							}
							t.decorated = false;
						} else if (piece == 2) {
							for (int i = 0; i < 6; i++) {
								t.wallPiece[i][0] = 0;
							}
							if (up != null) {
								for (int i = 0; i < 6; i++) {
									up.wallPiece[i][3] = 0;
								}
							}
						} else if (piece == 3) {
							for (int i = 0; i < 6; i++) {
								t.wallPiece[i][0] = 0;
							}
							if (up != null) {
								for (int i = 0; i < 6; i++) {
									up.wallPiece[i][3] = 0;
								}
							}
						}
					}
				}
			}
		}
	}

	void test() {
		// TestMapScene ps = (TestMapScene) scenes.get("testMap");
		change("testMap");
	}

	void export() {
		try {
			// map().version++;
			// OutputStream outputStream = new DeflaterOutputStream(new
			// FileOutputStream("file.bin"));
			// Output output = new Output(outputStream);
			// Kryo kryo = new Kryo();
			// kryo.writeObject(output, map());
			// output.close();
		} catch (Exception e) {
			Log.error(e);
		}
	}

	void commit() {
		lock();
		byte[] str = BearTool.serialize(Realm.mapData[Realm.curMap]);
		chunks = BearTool.divideArray(str, Shared.CHUNK_SIZE);
		for (int i = 0; i < chunks.length; i++) {
			Odyssey.game.sendTCP(new Chunk(chunks[i], Realm.curMap, i == chunks.length - 1 ? 1 : 0, i));
		}
	}

	void discard() {
		int m = Realm.curMap;
		Realm.mapData[m] = null;
		Realm.loadMap(m);
		Realm.pmap[m] = new PMap();
		Realm.mapData[m].checkAll(Realm.pmap[m]);
		Scene.lock();
		Odyssey.game.sendTCP(new DiscardMap());
	}

	void importMap() {
		try {
			// InputStream inputStream = new InflaterInputStream(new
			// FileInputStream("file.bin"));
			// Input input = new Input(inputStream);
			// Kryo kryo = new Kryo();
			// map = kryo.readObject(input, MapData.class);
			// input.close();
		} catch (Exception e) {
			Log.error(e);
		}
	}

	void checkKeys() {
		for (Integer a : Scene.input.keyPress) {
			switch (a) {
			case Keys.C:
				// if (input.keyDown[Keys.CONTROL_LEFT] || input.keyDown[Keys.CONTROL_RIGHT]) {
				// if(!mapBoxing && !tileBoxing) {
				// copy = true;
				// }
				// }
				break;
			case Keys.SHIFT_LEFT:
			case Keys.SHIFT_RIGHT:
				shift = !shift;
				break;
			case Keys.H:
				curHeight++;
				if (curHeight > 6) {
					curHeight = 1;
				}
				break;
			case Keys.B:
				curHeight--;
				if (curHeight < 1) {
					curHeight = 6;
				}
				break;
			case Keys.PERIOD:
				curElevation++;
				if (curElevation > 4) {
					curElevation = 0;
				}
				break;
			case Keys.COMMA:
				curElevation--;
				if (curElevation < 0) {
					curElevation = 4;
				}

			case Keys.E:
				map().checkAllEdges(pmap());
				break;
			case Keys.F4:
				// test();
				break;
			case Keys.F5:
				// commit();
				break;
			case Keys.F6:
				// importMap();
				break;
			}
		}
	}

	void updateLabels() {
		switch (att) {
		case 2: // spawn
			lblSpawnType.text = (attData[0] + 1) + ": " + Realm.monsterData[attData[0]].name;
			int r = 0;
			int c = 0;
			int min = 0;
			int max = 0;
			try {
				r = Integer.parseInt(spawnRange.text);
			} catch (Exception e) {

			}
			try {
				c = Integer.parseInt(spawnCount.text);
			} catch (Exception e) {

			}
			try {
				min = Integer.parseInt(spawnMin.text);
			} catch (Exception e) {

			}
			try {
				max = Integer.parseInt(spawnMax.text);
			} catch (Exception e) {

			}

			attData[1] = r;
			attData[2] = c;
			attData[3] = min;
			attData[4] = max;
			spawnRange.text = attData[1] + "";
			spawnCount.text = attData[2] + "";
			spawnMin.text = attData[3] + "";
			spawnMax.text = attData[4] + "";
			break;
		case 3: // warp
			lblWarpMap.text = "Map " + attData[0] + ": " + Realm.mapData[attData[0]].options.name;
			lblWarpX.text = "X: " + attData[1] + "";
			lblWarpY.text = "Y: " + attData[2] + "";
			break;
		case 4: // door
			// doorKey.text = attData[1] + "";
			attData[1] = Integer.parseInt(doorKey.text);
			attData[3] = Integer.parseInt(doorTime.text);
			attData[4] = Integer.parseInt(doorType.text);
			break;
		case 5: // fx
			lblFXScale.text = "Scale: " + ((float) attData[0] / 10f) + "x";
			lblFXLayer.text = "Layer: " + Shared.fxLayerName[attData[1]];
			lblFXX.text = "X Mod: " + attData[2] + "";
			lblFXY.text = "Y Mod: " + attData[3] + "";
			ParticleEffect pe = Realm.effectData.get(attData[4]);
			if (pe != null) {
				lblFXType.text = "fx" + attData[4] + ": " + pe.getEmitters().get(0).getName();
			} else {
				lblFXType.text = "fx " + attData[4] + ": invalid";
			}
			break;
		case 6: // light
			for (int i = 0; i < 9; i++) {
				lblLight[i].text = strLight[i] + ": " + attData[i + 1];
			}
			break;
		}
		lblTileSet.text = Shared.tilesets[curSet];
		if (curHoverX >= 0 && curHoverX < Shared.MAP_WIDTH && curHoverY >= 0 && curHoverY < Shared.MAP_WIDTH) {
			hover.text = "Map: " + Realm.curMap + " X: " + curHoverX + " Y: " + curHoverY;
		} else {
			hover.text = "Map: " + Realm.curMap;
		}
		hover.text += " (" + input.mouseX + "," + input.mouseY + ")";
	}

	public void update() {
		super.update();
		curSet = setList.sel;
		int sx = input.mouseX;
		int sy = input.mouseY;
		lblName.text = map().options.name;
		int mx = ((sx - 20) / 32);
		int my = ((sy - 40) / 32);
		mapMouseX = sx - 20;
		mapMouseY = sy - 40;
		if (mx >= 0 && mx < 16 && my >= 0 && my < 16) {
			curHoverX = mx + scrollX;
			curHoverY = my + scrollY;
		} else {
			curHoverX = -1;
			curHoverY = -1;
		}

		int tx = (sx - 559) / 32;
		int ty = (sy - 40) / 32;
		int dmx = 0;
		int dmy = 0;
		if (shift) {
			dmx = (sx - 20) - mx * 32;
			dmy = (sy - 40) - my * 32;
		}

		checkKeys();
		updateLabels();
		scrollMap();
		if (tileBoxing) {
			if (tx > 15) {
				tx = 15;
			}
			if (ty > 15) {
				ty = 15;
			}
			if (tx < 0) {
				tx = 0;
			}
			if (ty < 0) {
				ty = 0;
			}
			boxUp = ty * 16 + tx;
			updateBox();
		} else if (mapBoxing) {
			if (mx > 15) {
				mx = 15;
			}
			if (my > 15) {
				my = 15;
			}
			if (mx < 0) {
				mx = 0;
			}
			if (my < 0) {
				my = 0;
			}
			boxUp = my * 16 + mx;
			updateBox();
		}

		if (BearTool.inBox(sx, sy, 20, 20 + 522, 40, 40 + 522)) {
			for (Integer a : Scene.input.keyPress) {
				switch (a) {
				case Keys.G:
					if (editMode < 7) {
						fill(mx, my, map().tile[mx][my].set[editMode], map().tile[mx][my].tile[editMode], curSelSet,
								curSelTile, editMode);
						map().checkAllEdges(pmap());
					}
					break;
				}
			}
			if (curSelTile > 0 || tileBox || editMode == 7 || editMode == 8 || editMode == 9) {
				if (input.mouseDown[0]) {
					LClickMap(mx, my, dmx, dmy);
				} else if (input.mouseDown[1]) {
					RClickMap(mx, my);
				}
			}

		} else if (BearTool.inBox(sx, sy, 559, 559 + 512, 40, 40 + 512)) {

		} else if (BearTool.inBox(sx, sy, 690, 690 + 320, 2, 34)) {
			if (input.mouseDown[0]) {
				// curSet = (sx - 690) / 32;
				// tileBoxing = false;
				// tileBox = false;
			}
		}
		// clicked map
	}

	void LClickMap(int mx, int my, int dmx, int dmy) {
		if (!mapBoxing) {
			if (tick > leftCoolDown) {
				if (vis[editMode]) {
					if (editMode < 7) {
						if (tileBox) {
							leftCoolDown = tick + 100;
							placeBox(mx, my, editMode, dmx, dmy);
						} else if (!halting) {
							if (alting) {
								changeTile(map().tile[mx + scrollX][my + scrollY].set[editMode],
										map().tile[mx + scrollX][my + scrollY].tile[editMode]);
							} else {
								int mmx = mx + scrollX;
								int mmy = my + scrollY;
								if (inBounds(mmx, mmy)) {
									Tile t = map().tile[mx + scrollX][my + scrollY];
									oldSet = t.set[editMode];
									oldTile = t.tile[editMode];
									if (curSelSet != oldSet || curSelTile != oldTile
											|| isMountain(curSelSet, curSelTile) || editMode == 0) {
										t.set[editMode] = curSelSet;
										t.tile[editMode] = curSelTile;
										t.shiftX[editMode] = 0;
										t.shiftY[editMode] = 0;
										if (isCaveable(curSelSet, curSelTile) || isMountain(curSelSet, curSelTile)) {
											t.height = curHeight;
											if (isMountain(curSelSet, curSelTile)) {
												t.mount[curElevation][0] = curHeight;
												t.mount[curElevation][1] = curSelTile - 128;
											}
										} else {
											t.mount[curElevation][0] = 0;
											t.mount[curElevation][1] = 0;
											t.height = 0;
										}
										if (editMode > 0) {
											t.shiftX[editMode] = dmx;
											t.shiftY[editMode] = dmy;
										} else {
											t.edge = !shift;
											map().check(pmap(), mmx, mmy, scrollX, scrollY);
										}
									}
								}
							}
						}
					} else if (editMode == 7) {
						if (!halting) {
							placeWalls(0);
						}
					} else if (editMode == 8) {
						int smx = ((input.mouseX - 20) % 32) / 4;
						int smy = ((input.mouseY - 40) % 32) / 4;
						int mmx = mx + scrollX;
						int mmy = my + scrollY;
						if (inBounds(mmx, mmy)) {
							if (curShadow == 0) {
								map().shadow[mmx * 8 + smx][mmy * 8 + smy] = true;
							} else {
								for (int wxx = -(curShadow + 1) / 2; wxx < (curShadow + 1) / 2; ++wxx) {
									for (int wyy = -(curShadow + 1) / 2; wyy < (curShadow + 1) / 2; ++wyy) {
										if (wxx + smx + mmx * 8 >= 0 && wyy + smy + mmy * 8 >= 0
												&& wxx + smx + mmx * 8 < 512 && wyy + smy + mmy * 8 < 512) {
											map().shadow[mmx * 8 + smx + wxx][mmy * 8 + smy + wyy] = true;
										}
									}
								}
							}
						}
					} else if (editMode == 9) {
						if (!halting) {
							int msx = mx + scrollX;
							int msy = my + scrollY;
							int i = shift ? 1 : 0;
							if (alting) {
								if (map().tile[msx][msy].att[i] > 0) {
									att = map().tile[msx][msy].att[i];
									for (int c = 0; c < 10; c++) {
										attData[c] = map().tile[msx][msy].attData[i][c];
									}
									switch (att) {
									case 3: // warp
										lastWarpMap = attData[0];
										mapList.sel = lastWarpMap;
										lastWarpX = attData[1];
										lastWarpY = attData[2];
										break;
									case 2: // spawn
										lastSpawnType = attData[0];
										monsterList.sel = lastSpawnType;
										spawnRange.text = attData[1] + "";
										spawnCount.text = attData[2] + "";
										spawnMin.text = attData[3] + "";
										spawnMax.text = attData[4] + "";
										break;
									case 4: // door
										lastDoorDir = attData[0];
										for (int b = 500; b < 504; b++) {
											getButton(frmDoor.buttons, b).toggled = false;
										}
										getButton(frmDoor.buttons, 500 + lastDoorDir).toggled = true;
										doorKey.text = attData[1] + "";
										doorTime.text = attData[3] + "";
										doorType.text = attData[4] + "";
										doorOpen.toggled = BearTool.checkBit(attData[2], 0);
										doorGate.toggled = BearTool.checkBit(attData[2], 1);
										lastDoorFlags = attData[2];
										break;
									case 5: // fx
										lastFXScale = attData[0];
										lastFXLayer = attData[1];
										lastFXX = attData[2];
										lastFXY = attData[3];
										lastFXType = attData[4];
										fxList.sel = lastFXType;
										break;
									case 6: // light
										for (int j = 0; j < 9; j++) {
											lastLight[j] = attData[j + 1];
										}
										lightFlickers.toggled = BearTool.checkBit(attData[0], 0);
										lightIsSoft.toggled = BearTool.checkBit(attData[0], 1);
										lightIsXRay.toggled = BearTool.checkBit(attData[0], 2);
										lastLightFlags = attData[0];
										break;
									}
									switchAtt(att);
								}
							} else {
								if (MapData.inBounds(msx, msy)) {
									map().tile[msx][msy].att[i] = att;
									for (int c = 0; c < 10; c++) {
										map().tile[msx][msy].attData[i][c] = attData[c];
									}
								}
							}
						}
					}
				}
			}
		}
	}

	void RClickMap(int mx, int my) {
		if (!mapBoxing) {
			if (vis[editMode]) {
				if (editMode < 7) {
					if (alting) {
						if (map().tile[mx + scrollX][my + scrollY].tile[editMode] > 0) {
							curSet = map().tile[mx + scrollX][my + scrollY].set[editMode];
						}
					} else {
						int mmx = mx + scrollX;
						int mmy = my + scrollY;
						// int s = map().tile[mmx][mmy].set[editMode];
						if (inBounds(mmx, mmy)) {
							Tile t = map().tile[mmx][mmy];
							PTile p = pmap().tile[mmx][mmy];
							if (t.tile[editMode] > 0) {
								t.set[editMode] = 0;
								t.tile[editMode] = 0;
								t.shiftX[editMode] = 0;
								t.shiftY[editMode] = 0;
								t.height = 0;
								p.cave[0] = 0;
								p.cave[1] = 0;
								p.cave[2] = 0;
								t.mount[curElevation][0] = 0;
								t.mount[curElevation][1] = 0;
								if (editMode == 0) {
									map().check(pmap(), mmx, mmy, scrollX, scrollY);
								}
							}
						}
					}
				} else if (editMode == 7) {
					placeWalls(1);
				} else if (editMode == 8) {
					int smx = ((input.mouseX - 20) % 32) / 4;
					int smy = ((input.mouseY - 40) % 32) / 4;
					int mmx = mx + scrollX;
					int mmy = my + scrollY;
					if (inBounds(mmx, mmy)) {
						if (curShadow == 0) {
							map().shadow[mmx * 8 + smx][mmy * 8 + smy] = false;
						} else {
							for (int wxx = -(curShadow + 1) / 2; wxx < (curShadow + 1) / 2; ++wxx) {
								for (int wyy = -(curShadow + 1) / 2; wyy < (curShadow + 1) / 2; ++wyy) {
									if (wxx + smx + mmx * 8 >= 0 && wyy + smy + mmy * 8 >= 0
											&& wxx + smx + mmx * 8 < 512 && wyy + smy + mmy * 8 < 512) {
										map().shadow[mmx * 8 + smx + wxx][mmy * 8 + smy + wyy] = false;
									}
								}
							}
						}
					}
				} else if (editMode == 9) {
					if (!halting) {
						int msx = mx + scrollX;
						int msy = my + scrollY;
						int i = shift ? 1 : 0;
						if (alting) {

						} else {
							map().tile[msx][msy].att[i] = 0;
							for (int c = 0; c < 10; c++) {
								map().tile[msx][msy].attData[i][c] = 0;
							}
						}
					}
				}
			}
		}
	}

	public void render() {
		super.render();

	}

	void changeTile(int set, int tile) {
		if (tile == 0 || (set == curSelSet && tile == curSelTile)) {
			return;
		}

		// first check if its already in recent
		boolean f = false;
		for (int i = 0; i < 12; i++) {
			if (recentSets[i] == curSelSet && recentTiles[i] == curSelTile) {
				f = true;
			}
		}
		if (!f) {
			for (int i = 11; i > 0; i--) {
				recentTiles[i] = recentTiles[i - 1];
				recentSets[i] = recentSets[i - 1];
			}
			recentTiles[0] = curSelTile;
			recentSets[0] = curSelSet;
		}
		curSelTile = tile;
		curSelSet = set;
	}

	public void mouseUp(int x, int y, int button) {
		halting = false;
		walling = false;
		int tx = (x - 559) / 32;
		int ty = (y - 40) / 32;
		// if (ctrl) { // start box select
		if (tileBoxing) {
			tileBoxing = false;
			if (tx < 0) {
				tx = 0;
			}
			if (ty < 0) {
				ty = 0;
			}
			if (tx > 15) {
				tx = 15;
			}
			if (ty > 15) {
				ty = 15;
			}
			boxUp = ty * 16 + tx;
			updateBox();
			tileBox = true;
			boxSet = curSet;
		} else if (mapBoxing) {
			if (boxButton == button) {
				mapBoxing = false;
				tx = (x - 20) / 32;
				ty = (y - 40) / 32;
				if (tx < 0) {
					tx = 0;
				}
				if (ty < 0) {
					ty = 0;
				}
				if (tx > 15) {
					tx = 15;
				}
				if (ty > 15) {
					ty = 15;
				}
				boxUp = ty * 16 + tx;
				updateBox();
				if (editMode < 7) {
					// if (alting) {

					// } else {
					placeField(boxButton == 0 ? curSelSet : 0, boxButton == 0 ? curSelTile : 0, editMode);
					// }
				} else if (editMode == 7) {
					if (boxButton == 1) {
						clearWallField(tx, ty);
					}
				} else if (editMode == 9) {
					placeAttField(tx, ty, boxButton == 0 ? 1 : 0);
				}
			}
		}
		if (BearTool.inBox(x, y, 20, 20 + 522, 40, 40 + 522)) {

		} else if (BearTool.inBox(x, y, 559, 559 + 512, 40, 40 + 512)) {

		}
	}

	void clearWallField(int mx, int my) {
		int sx = boxStart % 16;
		int sy = boxStart / 16;
		int ex = boxEnd % 16;
		int ey = boxEnd / 16;
		Tile t = null;
		for (int x = sx; x <= ex; x++) {
			for (int y = sy; y <= ey; y++) {
				for (int i = 0; i < 4; i++) {
					t = map().tile[x + scrollX][y + scrollY];
					t.wall[i] = false;
					for (int a = 0; a < 6; a++) {
						for (int b = 0; b < 4; b++) {
							t.wallPiece[a][b] = 0;
						}
					}
				}
			}
		}
		mapBoxing = false;
		if (input.mouseDown[0] || input.mouseDown[1]) {
			halting = true;
		}
	}

	void placeAttField(int mx, int my, int val) {
		int sx = boxStart % 16;
		int sy = boxStart / 16;
		int ex = boxEnd % 16;
		int ey = boxEnd / 16;
		for (int x = sx; x <= ex; x++) {
			for (int y = sy; y <= ey; y++) {
				int i = shift ? 1 : 0;
				if (val > 0) {
					map().tile[x + scrollX][y + scrollY].att[i] = att;
					for (int c = 0; c < 10; c++) {
						map().tile[x + scrollX][y + scrollY].attData[i][c] = attData[c];
					}
				} else {
					map().tile[x + scrollX][y + scrollY].att[i] = 0;
					for (int c = 0; c < 10; c++) {
						map().tile[x + scrollX][y + scrollY].attData[i][c] = 0;
					}
				}

			}
		}
		mapBoxing = false;
		if (input.mouseDown[0] || input.mouseDown[1]) {
			halting = true;
		}
	}

	void placeField(int set, int tile, int i) {
		int sx = boxStart % 16;
		int sy = boxStart / 16;
		int ex = boxEnd % 16;
		int ey = boxEnd / 16;
		int mx = 0;
		int my = 0;
		for (int x = sx; x <= ex; x++) {
			for (int y = sy; y <= ey; y++) {
				mx = x + scrollX;
				my = y + scrollY;
				if (inBounds(mx, my)) {
					map().tile[mx][my].set[i] = set;
					map().tile[mx][my].tile[i] = tile;
					map().tile[mx][my].shiftX[i] = 0;
					map().tile[mx][my].shiftY[i] = 0;
					map().tile[mx][my].height = 0;
					map().tile[mx][my].mount[curElevation][1] = 0;
					map().tile[mx][my].mount[curElevation][0] = 0;
					map().tile[mx][my].edge = !shift;
				}
			}
		}
		map().checkAll(pmap());
		// if (editMode == 0 && map().isEdgeable(set, tile)) {
		// map().check(pmap(), mx, my, scrollX, scrollY);
		// }
		mapBoxing = false;
		if (input.mouseDown[0] || input.mouseDown[1]) {
			halting = true;
		}

	}

	void placeBox(int mx, int my, int i, int dmx, int dmy) {
		int sx = boxStart % 16;
		int sy = boxStart / 16;
		int ex = boxEnd % 16;
		int ey = boxEnd / 16;
		int dx = 0;
		int dy = 0;
		int layer = i;
		for (int x = sx; x <= ex; x++) {
			for (int y = sy; y <= ey; y++) {
				dx = x - sx + mx + scrollX;
				dy = y - sy + my + scrollY;
				if (inBounds(dx, dy)) {
					if (alting) {
						layer = 4;
						if (y == ey) {
							layer = i;
						}
					}
					map().tile[dx][dy].set[layer] = boxSet;
					map().tile[dx][dy].tile[layer] = y * 16 + x + 1;
					if (layer > 0) {
						map().tile[dx][dy].shiftX[layer] = dmx;
						map().tile[dx][dy].shiftY[layer] = dmy;
					}
				}
			}
		}
		if (input.mouseDown[0] || input.mouseDown[1]) {
			halting = true;
		}
	}

	Tile getTile(int mx, int my) {
		if (inBounds(mx, my)) {
			return map().tile[mx][my];
		}
		return null;
	}

	public void mouseDown(int x, int y, int button) {
		try {
			// non dialog, non button, non text touches. overload this in specific scene

			if (BearTool.inBox(x, y, 20, 20 + 522, 40, 40 + 522)) {
				// clicked map
				// only use this for single click instances! it will only be called the one time
				// on mousedown
				// for painting, use update()
				int mx = ((x - 20) / 32);
				int my = ((y - 40) / 32);
				if (editMode < 7 || editMode == 7 || editMode == 9) {
					if (button == 1 && tileBox) {
						tileBox = false;
					} else if (ctrling && !tileBoxing && !tileBox && !mapBoxing) {
						mapBoxing = true;
						boxDown = my * 16 + mx;
						boxButton = button;
						boxUp = boxDown;
						updateBox();
					}
				}
			} else if (BearTool.inBox(x, y, 559, 559 + 512, 40, 40 + 512)) {
				int tx = (x - 559) / 32;
				int ty = (y - 40) / 32;
				if (editMode < 7) {
					// clicked tiles
					if (tileBox) {
						tileBox = false;
					}
					if (ctrling) { // start box select
						if (!tileBoxing) {
							tileBoxing = true;
							boxDown = ty * 16 + tx;
							boxUp = boxDown;
						}
					} else {
						changeTile(curSet, ty * 16 + tx + 1);
					}
				} else if (editMode == 7) {

					if (ty < 2) {
						curWall = ty * 16 + tx;
						if (curWall < 0) {
							curWall = 0;
						}
						if (curWall > 26) {
							curWall = 26;
						}
					} else if (ty == 9) {
						curWallMod = tx;
					} else if (ty == 5 && tx == 0) {
						wallShadow = true;
					} else if (ty == 6 && tx == 0) {
						wallShadow = false;
					}
				} else if (editMode == 8) {
					if (ty < 1 && tx < 8) {
						curShadow = tx;
					}
				} else if (editMode == 9) {
					int a = ty * 16 + tx + 1;
					if (a != att && validAtt(a)) {
						att = a;
						for (int c = 0; c < 6; c++) {
							attData[c] = 0;
						}
						switchAtt(a);
					}
				}
			} else if (BearTool.inBox(x, y, 554 + 64, 554 + 64 + 384, 585 - 4, 585 - 4 + 32)) {
				int r = (x - 618) / 32;
				if (button == 0) {
					changeTile(recentSets[r], recentTiles[r]);
				} else {
					curSet = recentSets[r];
				}
			} else if (BearTool.inBox(x, y, 559, 559 + 32, 586, 586 + 32)) {
				curSet = curSelSet;
			}
		} catch (Exception e) {
			Log.error(e);
		}
	}

	void switchAtt(int a) {
		hideFrames();
		switch (a) {
		case 1: // wall
			break;
		case 2: // spawn
			attData[0] = lastSpawnType;
			attData[1] = Integer.parseInt(spawnRange.text);
			attData[2] = Integer.parseInt(spawnCount.text);
			attData[3] = Integer.parseInt(spawnMin.text);
			attData[4] = Integer.parseInt(spawnMax.text);
			frmSpawn.visible = true;
			monsterList.visible = true;
			monsterList.sel = lastSpawnType;
			break;
		case 3: // warp
			mapList.visible = true;
			mapList.sel = lastWarpMap;
			attData[0] = lastWarpMap;
			attData[1] = lastWarpX;
			attData[2] = lastWarpY;
			frmWarp.visible = true;
			break;
		case 4: // door
			attData[0] = lastDoorDir;
			attData[1] = Integer.parseInt(doorKey.text);
			attData[2] = lastDoorFlags;
			attData[3] = Integer.parseInt(doorTime.text);
			attData[4] = Integer.parseInt(doorType.text);
			frmDoor.visible = true;
			break;
		case 5: // fx
			attData[0] = lastFXScale;
			attData[1] = lastFXLayer;
			attData[2] = lastFXX;
			attData[3] = lastFXY;
			attData[4] = lastFXType;
			frmFX.visible = true;
			fxList.visible = true;
			fxList.sel = lastFXType;
			break;
		case 6: // light
			// flags attData[0] =
			attData[0] = lastLightFlags;
			lightFlickers.toggled = BearTool.checkBit(attData[0], 0);
			lightIsSoft.toggled = BearTool.checkBit(attData[0], 1);
			lightIsXRay.toggled = BearTool.checkBit(attData[0], 2);
			for (int i = 0; i < 9; i++) {
				attData[i + 1] = lastLight[i];
				frmLight.visible = true;
			}
		}
	}

	boolean validAtt(int a) {
		if (a <= 56) {
			return true;
		}
		return false;
	}

	void hideFrames() {
		fxList.visible = false;
		mapList.visible = false;
		monsterList.visible = false;
		setList.visible = false;
		frmWarp.visible = false;
		frmLight.visible = false;
		frmSpawn.visible = false;
		frmDoor.visible = false;
		frmFX.visible = false;
	}

	public void checkBox(int id) {
		if (id < 10) {
			vis[id] = checkBoxes.get(id).toggled;
		} else {
			if (id == 10) {
				attData[2] = BearTool.setBit(attData[2], 0, doorOpen.toggled);
				attData[2] = BearTool.setBit(attData[2], 1, doorGate.toggled);
			} else if (id == 11) {
				attData[0] = BearTool.setBit(attData[0], 0, lightFlickers.toggled);
				attData[0] = BearTool.setBit(attData[0], 1, lightIsSoft.toggled);
				attData[0] = BearTool.setBit(attData[0], 2, lightIsXRay.toggled);
				lastLightFlags = attData[0];

			}
		}
	}

}
