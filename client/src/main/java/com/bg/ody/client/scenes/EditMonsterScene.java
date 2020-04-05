package com.bg.ody.client.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.Log;
import com.bg.bearplane.gui.Button;
import com.bg.bearplane.gui.CheckBox;
import com.bg.bearplane.gui.Label;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.gui.TextBox;
import com.bg.ody.client.core.Assets;
import com.bg.ody.client.core.Odyssey;
import com.bg.ody.client.core.Sprite;
import com.bg.ody.shared.MonsterData;
import com.bg.ody.shared.Shared;

public class EditMonsterScene extends Scene {

	int num = 0;

	Label lblNum;
	TextBox name, maxHP, attackSpeed, walkSpeed, dodge, accuracy, wanderRange, stepsPerWalk;
	TextBox acPierce, acBludge, acSlash, damSides, damDice, damBonus;
	Button pierce, slash, bludge;

	CheckBox friendly, guard;

	public int curSet = 0;
	public int curSelSet = 0;
	int curSprite = 0;
	Sprite sprite = new Sprite(0, 0);

	int step = 0;
	long stamp = 0;

	public void start() {
		super.start();
		int centerX = Shared.GAME_WIDTH / 2;
		lblNum = new Label(this, centerX, 32, 1f, "Editing monster " + num, Color.WHITE, true);
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

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "Max HP:", Color.WHITE, false));
		maxHP = new TextBox(this, 1, 6, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(maxHP);
		maxHP.allowLetters = false;
		r++;

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "Walk Speed:", Color.WHITE, false));
		walkSpeed = new TextBox(this, 1, 4, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(walkSpeed);
		walkSpeed.allowLetters = false;
		r++;

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "Atk Speed:", Color.WHITE, false));
		attackSpeed = new TextBox(this, 1, 4, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(attackSpeed);
		attackSpeed.allowLetters = false;
		r++;

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "Dodge:", Color.WHITE, false));
		dodge = new TextBox(this, 1, 3, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(dodge);
		dodge.allowLetters = false;
		r++;

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "Accuracy:", Color.WHITE, false));
		accuracy = new TextBox(this, 1, 3, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(accuracy);
		accuracy.allowLetters = false;
		r++;

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "Steps per walk", Color.WHITE, false));
		stepsPerWalk = new TextBox(this, 1, 2, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(stepsPerWalk);
		stepsPerWalk.allowLetters = false;
		r++;

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "Wander Range:", Color.WHITE, false));
		wanderRange = new TextBox(this, 1, 2, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(wanderRange);
		wanderRange.allowLetters = false;
		r++;

		c = 1;
		r = 1;
		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "AC Pierce:", Color.WHITE, false));
		acPierce = new TextBox(this, 1, 3, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(acPierce);
		acPierce.allowLetters = false;
		r++;

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "AC Slash:", Color.WHITE, false));
		acSlash = new TextBox(this, 1, 3, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(acSlash);
		acSlash.allowLetters = false;
		r++;

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "AC Bludgeon:", Color.WHITE, false));
		acBludge = new TextBox(this, 1, 3, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(acBludge);
		acBludge.allowLetters = false;
		r++;

		r = 1;
		c++;
		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "Dam Dice:", Color.WHITE, false));
		damDice = new TextBox(this, 1, 3, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(damDice);
		damDice.allowLetters = false;
		r++;

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "Dam Sides:", Color.WHITE, false));
		damSides = new TextBox(this, 1, 3, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(damSides);
		damSides.allowLetters = false;
		r++;

		labels.add(new Label(this, 10 + c * cW, 80 + r * 60, 1f, "Dam Bonus:", Color.WHITE, false));
		damBonus = new TextBox(this, 1, 3, false, 120 + c * cW, 40 + r * 60, 100, false);
		textBoxes.add(damBonus);
		damBonus.allowLetters = false;
		r++;

		labels.add(new Label(this, 260, 314, 1f, "Dam Type:", Color.WHITE, false));
		pierce = new Button(this, 800, 410, 320, 120, 32, "Pierce", true);
		buttons.add(pierce);
		slash = new Button(this, 801, 410 + 140, 320, 120, 32, "Slash", true);
		buttons.add(slash);
		bludge = new Button(this, 802, 410 + 280, 320, 120, 32, "Bludgeon", true);
		buttons.add(bludge);
		// pierce.toggled = true;

		labels.add(new Label(this, 484, 60, 1f, "Friendly:", Color.WHITE, false));
		labels.add(new Label(this, 484, 85, 1f, "Guard:", Color.WHITE, false));

		friendly = new CheckBox(this, 0, 570, 68);
		checkBoxes.add(friendly);
		guard = new CheckBox(this, 1, 570, 93);
		checkBoxes.add(guard);

		buttons.add(new Button(this, 900, centerX - 170, 720, 224, 48, "Discard"));
		buttons.add(new Button(this, 901, centerX + 170, 720, 224, 48, "Commit"));

	}

	public void update() {
		super.update();
		lblNum.text = "Editing monster " + num;
		if (tick > stamp) {
			stamp = tick + 300 + (int) (Math.random() * 100.0);
			step++;
			if (step > 11) {
				step = 0;
			}
		}
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
				draw(tex, 32 + i * 32, 600, 32, 32, step * 32, i * 32, 32, 32);
			}
			if (tex.getHeight() > (i + 30) * 32) {
				draw(tex, 32 + i * 32, 632, 32, 32, step * 32, (i + 30) * 32, 32, 32);
			}
		}
		if (curSelSet == curSet && curSprite > 0) {
			int dx = ((curSprite - 1) % 30) * 32 + 32;
			int dy = ((curSprite - 1) / 30) * 32 + 600;
			draw(Assets.textures.get("sel"), dx, dy, 0, 0, 32, 32);
		}
	}

	public void load(MonsterData md) {
		name.text = md.name;
		num = md.id;
		maxHP.text = md.maxHP + "";
		curSprite = md.sprite;
		curSet = md.spriteSet;
		buttonPressed(curSet);
		curSelSet = curSet;
		attackSpeed.text = "" + md.attackSpeed;
		walkSpeed.text = "" + md.walkSpeed;
		dodge.text = "" + md.dodge;
		accuracy.text = "" + md.accuracy;
		acPierce.text = "" + md.acPierce;
		acBludge.text = "" + md.acBludge;
		acSlash.text = "" + md.acSlash;
		damSides.text = "" + md.damSides;
		damDice.text = "" + md.damDice;
		damBonus.text = "" + md.damBonus;
		stepsPerWalk.text = "" + md.stepsPerWalk;
		wanderRange.text = "" + md.wanderRange;
		pierce.toggled = false;
		slash.toggled = false;
		bludge.toggled = false;
		if (md.damType == 0) {
			pierce.toggled = true;
		} else if (md.damType == 1) {
			slash.toggled = true;
		} else if (md.damType == 2) {
			bludge.toggled = true;
		}
		friendly.toggled = md.friendly;
		guard.toggled = md.guard;
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
				case 800:
					pierce.toggled = true;
					slash.toggled = false;
					bludge.toggled = false;
					break;
				case 801:
					pierce.toggled = false;
					slash.toggled = true;
					bludge.toggled = false;
					break;
				case 802:
					pierce.toggled = false;
					slash.toggled = false;
					bludge.toggled = true;
					break;
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
		MonsterData md = new MonsterData();
		md.id = num;
		md.maxHP = Integer.parseInt(maxHP.text);
		md.name = name.text;
		md.spriteSet = curSet;
		md.sprite = curSprite;
		md.acBludge = Integer.parseInt(acBludge.text);
		md.accuracy = Integer.parseInt(accuracy.text);
		md.acPierce = Integer.parseInt(acPierce.text);
		md.acSlash = Integer.parseInt(acSlash.text);
		md.attackSpeed = Integer.parseInt(attackSpeed.text);
		md.damBonus = Integer.parseInt(damBonus.text);
		md.damDice = Integer.parseInt(damDice.text);
		md.damSides = Integer.parseInt(damSides.text);
		md.stepsPerWalk = Integer.parseInt(stepsPerWalk.text);
		md.wanderRange = Integer.parseInt(wanderRange.text);
		if (pierce.toggled) {
			md.damType = 0;
		} else if (slash.toggled) {
			md.damType = 1;
		} else if (bludge.toggled) {
			md.damType = 2;
		}
		md.dodge = Integer.parseInt(dodge.text);
		md.friendly = friendly.toggled;
		md.guard = friendly.toggled;
		md.walkSpeed = Integer.parseInt(walkSpeed.text);
		Scene.lock();
		Odyssey.game.sendTCP(md);
	}

	@Override
	public void enterPressedInField(int id) {
		confirm();
	}

}
