package com.bg.ody.client.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.Log;
import com.bg.bearplane.gui.Button;
import com.bg.bearplane.gui.CheckBox;
import com.bg.bearplane.gui.Frame;
import com.bg.bearplane.gui.Label;
import com.bg.bearplane.gui.ListBox;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.gui.TextBox;
import com.bg.ody.client.core.Assets;
import com.bg.ody.client.core.Odyssey;
import com.bg.ody.client.core.Realm;
import com.bg.ody.shared.ItemData;
import com.bg.ody.shared.Shared;

public class EditItemScene extends Scene {

	int num = 0;

	Label lblNum;
	TextBox name;

	public int curSet = 0;
	public int curSelSet = 0;
	int curSprite = 0;
	int curRare = 0;
	int curType = 0;
	int curDamType = 0;
	int curToolType = 0;
	int curSlot = 0;

	CheckBox undroppable;
	CheckBox unstackable;

	// Wieldable
	CheckBox twoHanded;
	CheckBox offhandable;
	CheckBox indestructible;
	CheckBox unique;
	CheckBox cantAttack;

	CheckBox stackEffect;

	Frame[] page;
	Frame[] type;

	int curPage = 0;

	final int NUM_PAGES = 2;
	final int NUM_TYPES = 5;

	int r = 0;
	int c = 0;
	int cW = 250;
	int nextID = 0;
	int modX = 0;
	int modY = 0;

	public void start() {
		super.start();
		started = false;
		curPage = 0;
		r = 0;
		c = 0;
		modX = 0;
		modY = 0;
		curSet = 0;
		curSelSet = 0;
		curSprite = 0;
		curRare = 0;
		curType = 0;
		curDamType = 0;
		curToolType = 0;
		curSlot = 0;
		int centerX = Shared.GAME_WIDTH / 2;
		lblNum = new Label(this, centerX, 32, 1f, "Editing item " + num, Color.WHITE, true);
		labels.add(lblNum);

		page = new Frame[NUM_PAGES];
		type = new Frame[NUM_TYPES];
		for (int i = 0; i < NUM_PAGES; i++) {
			page[i] = new Frame(this);
			page[i].visible = false;
			frames.add(page[i]);
			page[i].fields = new ArrayList<TextBox>();
		}
		for (int i = 0; i < NUM_TYPES; i++) {
			type[i] = new Frame(this);
			type[i].visible = false;
			page[1].frames.add(type[i]);
			type[i].fields = new ArrayList<TextBox>();
		}

		page[0].visible = true;
		int n = 0;
		int nw = 160;
		undroppable = new CheckBox(this, 0, 640 + (n * nw), 640, "Undroppable");
		n++;
		unstackable = new CheckBox(this, 1, 640 + (n * nw), 640, "Unstackable");
		page[0].checkBoxes.add(undroppable);
		page[0].checkBoxes.add(unstackable);
		page[0].labels.add(new Label(this, centerX, 90, 1f, "Sprite Sheet", Color.WHITE, true));
		addButtons(page[0].buttons, 10, 240, 120, 24, 4, Shared.objRow1, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, false,
				true);
		addButtons(page[0].buttons, 135, 245, 120, 24, 4, Shared.objRow2, new int[] { 9, 10, 11, 12, 13, 14, 15, 16 },
				false, true);
		page[0].buttons.get(0).toggled = true;

		addButtons(page[0].buttons, 400, 130, 120, 24, 4,
				new String[] { "Common", "Uncommon", "Rare", "Epic", "Legendary" }, new int[] { 17, 18, 19, 20, 21 },
				false, true);
		page[0].buttons.get(17).toggled = true;

		page[0].labels.add(new Label(this, 18 + c * cW, 60 + r * 60, 1f, "Name:", Color.WHITE, false));
		name = new TextBox(this, 999, 16, true, 100 + c * cW, 20 + r * 60, 320, false);
		page[0].textBoxes.add(name);
		name.allowSpecial = true;

		TextBox t;
		modX = -160;
		modY = -40;
		c = 3;
		r = 2;
		t = addField(page[0], "Weight", 4);
		t.allowLetters = true;
		t = addField(page[0], "Req Lvl", 3);

		r = 2;
		c = 4;
		t = addField(page[0], "Req Str", 3);
		t = addField(page[0], "Req Dex", 3);
		t = addField(page[0], "Req Con", 3);
		t = addField(page[0], "Req Int", 3);
		t = addField(page[0], "Req Wis", 3);
		t = addField(page[0], "Req Cha", 3);
		t = addField(page[0], "Req Luck", 3);
		c = 3;

		modX = -80;
		int[] classInts = new int[Shared.classes.length];
		for (int i = 0; i < classInts.length; i++) {
			classInts[i] = 3000 + i;
		}
		addRadio(page[0], Shared.classes, classInts, 80, 18);

		page[0].labels.add(new Label(this, 850, 600, 1f, "Classes that can't use", Color.WHITE, true));

		modX = 0;
		modY = 0;

		buttons.add(new Button(this, 900, centerX - 170, 720, 224, 48, "Discard"));
		buttons.add(new Button(this, 901, centerX + 170, 720, 224, 48, "Commit"));

		addButtons(page[1].buttons, 400, 100, 120, 24, 4,
				new String[] { "Resource", "Wieldable", "Wearable", "Consumable" }, new int[] { 22, 23, 24, 25 }, false,
				true);
		page[1].buttons.get(0).toggled = true;

		// Resource

		r = 0;
		c = 0;
		nextID = 100;

		type[0].labels.add(new Label(this, 40, 65, 1f, "Boost from having in inv", Color.WHITE, false));

		t = addField(type[0], "Mod Str", 3);
		t = addField(type[0], "Mod Dex", 3);
		t = addField(type[0], "Mod Con", 3);
		t = addField(type[0], "Mod Int", 3);
		t = addField(type[0], "Mod Wis", 3);
		t = addField(type[0], "Mod Cha", 3);
		t = addField(type[0], "Mod Luck", 3);
		t = addField(type[0], "Mod HP", 3);
		t = addField(type[0], "Mod MP", 3);
		t = addField(type[0], "Mod E", 3);

		n = 0;
		stackEffect = new CheckBox(this, 0, 460 + n * nw, 650, "Stack Effect");

		type[0].checkBoxes.add(stackEffect);

		// Wieldable

		r = 0;
		c = 0;
		nextID = 0;

		t = addField(type[1], "Durability", 6);
		t = addField(type[1], "Hit Dice", 3);
		t = addField(type[1], "Hit Sides", 3);
		t = addField(type[1], "Hit Bonus", 3);
		t = addField(type[1], "Dam Dice", 3);
		t = addField(type[1], "Dam Sides", 3);
		t = addField(type[1], "Dam Bonus", 3);
		t = addField(type[1], "AmmoType", 3);
		addRadio(type[1], new String[] { "Slash", "Bash", "Pierce" }, new int[] { 1000, 1001, 1002 }, 80, 24);
		addRadio(type[1], new String[] { "Weapon", "Chop", "Fish", "Mine", "Dig" },
				new int[] { 2000, 2001, 2002, 2003, 2004 }, 80, 24);
		buttonPressed(2000);

		c++;
		r = 0;
		// 8
		t = addField(type[1], "Fire %", 3);
		t = addField(type[1], "Water %", 3);
		t = addField(type[1], "Earth %", 3);
		t = addField(type[1], "Sky %", 3);
		t = addField(type[1], "Holy %", 3);
		t = addField(type[1], "Shadow %", 3);
		t = addField(type[1], "Magicres%", 3);
		t = addField(type[1], "Physres%", 3);
		t = addField(type[1], "Tool Power", 3);
		// 17

		c++;
		r = 0;
		t = addField(type[1], "Cooldown ms", 5);
		t = addField(type[1], "Bleed", 3);
		t = addField(type[1], "Poison", 3);
		t = addField(type[1], "HP Vampire", 3);
		t = addField(type[1], "MP Vampire", 3);
		t = addField(type[1], "Light Rad", 3);
		t = addField(type[1], "Light R", 3);
		t = addField(type[1], "Light G", 3);
		t = addField(type[1], "Light B", 3);

		c++;
		r = 0;
		t = addField(type[1], "Range", 3);
		t = addField(type[1], "Mod Str", 3);
		t = addField(type[1], "Mod Dex", 3);
		t = addField(type[1], "Mod Con", 3);
		t = addField(type[1], "Mod Int", 3);
		t = addField(type[1], "Mod Wis", 3);
		t = addField(type[1], "Mod Cha", 3);
		t = addField(type[1], "Mod Luck", 3);
		t = addField(type[1], "Req Tool Lvl", 3);

		c++;
		r = 0;
		t = addField(type[1], "Crit%", 3);
		t = addField(type[1], "Dam%", 3);

		// 34

		n = 0;
		twoHanded = new CheckBox(this, 0, 460 + n * nw, 650, "Two Handed");
		n++;
		offhandable = new CheckBox(this, 0, 460 + n * nw, 650, "Offhandable");
		n++;
		indestructible = new CheckBox(this, 0, 460 + n * nw, 650, "Indestructible");
		n++;
		unique = new CheckBox(this, 0, 460 + n * nw, 650, "Unique");
		n++;
		cantAttack = new CheckBox(this, 0, 460 + n * nw, 650, "Cant Attack");

		type[1].checkBoxes.add(twoHanded);
		type[1].checkBoxes.add(offhandable);
		type[1].checkBoxes.add(indestructible);
		type[1].checkBoxes.add(unique);
		type[1].checkBoxes.add(cantAttack);

		// Wearable

		c = 1;
		r = 0;
		nextID = 0;
		int[] armorBtn = new int[Shared.armorTypes.length];
		for (int i = 0; i < Shared.armorTypes.length; i++) {
			armorBtn[i] = i + 3000;
		}
		addRadio(type[2], Shared.armorTypes, armorBtn, 80, 24);
		buttonPressed(3000);
		c = 0;
		r = 1;

		t = addField(type[2], "Durability", 5);
		t = addField(type[2], "AC Slash", 3);
		t = addField(type[2], "AC Bash", 3);
		t = addField(type[2], "AC Pierce", 3);
		t = addField(type[2], "Shield Delay", 3);
		t = addField(type[2], "Shield %Slash", 3);
		t = addField(type[2], "Shield %Bash", 3);
		t = addField(type[2], "Shield %Pierce", 3);

		c = 1;
		r = 1;
		t = addField(type[2], "res Fire%", 3);
		t = addField(type[2], "res Water%", 3);
		t = addField(type[2], "res Earth%", 3);
		t = addField(type[2], "res Sky%", 3);
		t = addField(type[2], "res Holy%", 3);
		t = addField(type[2], "res Shadow%", 3);
		t = addField(type[2], "res Mag%", 3);
		t = addField(type[2], "res Phys%", 3);

		c = 2;
		r = 1;
		t = addField(type[2], "res Poison", 3);
		t = addField(type[2], "res Bleed", 3);
		t = addField(type[2], "Light Rad", 3);
		t = addField(type[2], "Light R", 3);
		t = addField(type[2], "Light G", 3);
		t = addField(type[2], "Light B", 3);

		c = 3;
		r = 1;
		t = addField(type[2], "AC% Slash", 3);
		t = addField(type[2], "AC% Bash", 3);
		t = addField(type[2], "AC% Pierce", 3);
		t = addField(type[2], "Crit%", 3);
		t = addField(type[2], "Dam%", 3);
		t = addField(type[2], "Attack Speed%", 3);
		t = addField(type[2], "Walk Speed%", 3);
		t = addField(type[2], "Hit%", 3);

		c = 4;
		r = 1;
		t = addField(type[2], "Mod Str", 3);
		t = addField(type[2], "Mod Dex", 3);
		t = addField(type[2], "Mod Con", 3);
		t = addField(type[2], "Mod Int", 3);
		t = addField(type[2], "Mod Wis", 3);
		t = addField(type[2], "Mod Cha", 3);
		t = addField(type[2], "Mod Luck", 3);

		// n = 0;
		// indestructible = new CheckBox(this, 0, 460 + n * nw, 650, "Indestructible");
		// n++;
		// unique = new CheckBox(this, 0, 460 + n * nw, 650, "Unique");
		// n++;
		type[2].checkBoxes.add(indestructible);
		type[2].checkBoxes.add(unique);

		// Consumable

		c = 0;
		r = 0;
		nextID = 0;

		t = addField(type[3], "Tick Duration", 3);
		t = addField(type[3], "Total Ticks", 3);
		t = addField(type[3], "Cooldown ms", 3);
		t = addField(type[3], "HP Regen", 3);
		t = addField(type[3], "MP Regen", 3);
		t = addField(type[3], "E Regen", 3);
		t = addField(type[3], "Mod MaxHP", 3);
		t = addField(type[3], "Mod MaxMP", 3);
		t = addField(type[3], "Mod MaxE", 3);
		c = 1;
		r = 0;

		t = addField(type[3], "Mod EXP", 3);
		t = addField(type[3], "Mod Str", 3);
		t = addField(type[3], "Mod Dex", 3);
		t = addField(type[3], "Mod Con", 3);
		t = addField(type[3], "Mod Int", 3);
		t = addField(type[3], "Mod Wis", 3);
		t = addField(type[3], "Mod Cha", 3);
		t = addField(type[3], "Mod Luck", 3);

		c = 2;
		r = 0;

		t = addField(type[3], "Perm HP", 3);
		t = addField(type[3], "Perm MP", 3);
		t = addField(type[3], "Perm E", 3);
		t = addField(type[3], "Perm EXP", 3);

		c = 3;
		r = 0;
		t = addField(type[3], "Perm Str", 3);
		t = addField(type[3], "Perm Dex", 3);
		t = addField(type[3], "Perm Con", 3);
		t = addField(type[3], "Perm Int", 3);
		t = addField(type[3], "Perm Wis", 3);
		t = addField(type[3], "Perm Cha", 3);
		t = addField(type[3], "Perm Luck", 3);

		n = 0;
		// stackEffect = new CheckBox(this, 0, 460 + n * nw, 650, "Stack Effect");
		n++;
		type[3].checkBoxes.add(stackEffect);

		buttons.add(new Button(this, 800, 80, 730, 120, 32, "Prev Page"));
		buttons.add(new Button(this, 801, 1286, 730, 120, 32, "Next Page"));

	}

	public TextBox addField(Frame f, String name, int maxLength, boolean allowLetters) {
		int w = 100;
		f.labels.add(new Label(this, modX + 10 + c * cW, modY + 110 + r * 60, 1f, name + ":", Color.WHITE, false));
		TextBox t = new TextBox(this, nextID, maxLength, false, modX + 120 + c * cW, modY + 70 + r * 60, w, false);
		nextID++;
		f.fields.add(t);
		t.allowLetters = allowLetters;
		f.textBoxes.add(t);
		r++;
		return t;
	}

	public void addRadio(Frame f, String[] names, int[] ids, int w, int h) {
		int count = 0;
		Button b;
		for (int i : ids) {
			b = new Button(this, i, modX + 40 + c * cW + count * (w + 4), modY + 116 + r * 60, w, h, names[count]);
			b.toggle = true;
			f.buttons.add(b);
			count++;
		}
		r++;
	}

	public TextBox addField(Frame f, String name, int maxLength) {
		return addField(f, name, maxLength, false);
	}

	public void update() {
		super.update();
		lblNum.text = "Editing item  " + num;

		int sx = input.mouseX;
		int sy = input.mouseY;
		int mx = (sx - 70) / 32;
		int my = (sy - 170) / 32;
		int s = 0;
		if (input.mouseDown[0]) {
			if (BearTool.inBox(sx, sy, 70, 70 + 512, 170, 170 + 512)) {
				s = my * 16 + mx + 1;
			}
			Log.debug(sx + "," + sy);
		}
		if (s > 0) {
			// Texture tex = Assets.textures.get(Shared.objectSets[curSelSet]);
			// if (tex.getHeight() > (s - 1) * 32) {
			curSprite = s;
			curSet = curSelSet;
			// }
		}
	}

	public void render() {
		super.render();
		drawSprites();
	}

	void drawSprites() {
		if (curPage != 0)
			return;
		Texture tex = Assets.textures.get(Shared.objectSets[curSelSet]);
		draw(tex, 70, 170, 512, 512, 0, 0, 512, 512);
		// for (int i = 0; i < 30; i++) {
		// if (tex.getHeight() > i * 32) {
		//
		// }
		// if (tex.getHeight() > (i + 30) * 32) {
		// draw(tex, 32 + i * 32, 632, 32, 32, step * 32, (i + 30) * 32, 32, 32);
		// }
		// }
		if (curSelSet == curSet && curSprite > 0) {
			int dx = ((curSprite - 1) % 16) * 32 + 70;
			int dy = ((curSprite - 1) / 16) * 32 + 170;
			draw(Assets.textures.get("sel"), dx, dy, 0, 0, 32, 32);
		}
	}

	public void load(ItemData md) {
		name.text = md.name;
		num = md.id;
		curSprite = md.sprite;
		curSet = md.spriteSet;
		buttonPressed(curSet);
		curSelSet = curSet;
		curRare = md.rarity;
		buttonPressed(curRare + 17);
		curType = md.type;

		page[0].fields.get(0).text = (int) (md.weight * 10.0) + "";
		page[0].fields.get(1).text = md.reqLevel + "";
		for (int i = 0; i < 7; i++) {
			page[0].fields.get(2 + i).text = md.reqStat[i] + "";
		}

		for (int i = 0; i < Shared.classes.length; i++) {
			page[0].buttons.get(22 + i).toggled = md.reqClass[i];
		}

		undroppable.toggled = md.undroppable;
		unstackable.toggled = md.unstackable;

		for (int i = 0; i < NUM_TYPES; i++) {
			type[i].visible = false;
		}
		type[md.type].visible = true;
		int i = 0;
		switch (md.type) {
		case 0:
			i = 0;
			type[md.type].fields.get(i).text = md.modStr + "";
			i++;
			type[md.type].fields.get(i).text = md.modDex + "";
			i++;
			type[md.type].fields.get(i).text = md.modCon + "";
			i++;
			type[md.type].fields.get(i).text = md.modInt + "";
			i++;
			type[md.type].fields.get(i).text = md.modWis + "";
			i++;
			type[md.type].fields.get(i).text = md.modCha + "";
			i++;
			type[md.type].fields.get(i).text = md.modLuck + "";
			i++;
			type[md.type].fields.get(i).text = md.modHP + "";
			i++;
			type[md.type].fields.get(i).text = md.modMP + "";
			i++;
			type[md.type].fields.get(i).text = md.modE + "";
			i++;
			if (md.stackEffect) {
				Log.debug("L: SE");
			} else {
				Log.debug("L: not SE");
			}
			stackEffect.toggled = md.stackEffect;
			break;
		case 1:
			i = 0;
			type[md.type].fields.get(0).text = md.dur + "";
			type[md.type].fields.get(1).text = md.hitDice + "";
			type[md.type].fields.get(2).text = md.hitSides + "";
			type[md.type].fields.get(3).text = md.hitBonus + "";
			type[md.type].fields.get(4).text = md.damDice + "";
			type[md.type].fields.get(5).text = md.damSides + "";
			type[md.type].fields.get(6).text = md.damBonus + "";
			type[md.type].fields.get(7).text = md.ammoType + "";
			for (i = 0; i < 6; i++) {
				type[md.type].fields.get(8 + i).text = md.elemental[i] + "";
			}
			type[md.type].fields.get(14).text = md.resistMag + "";
			type[md.type].fields.get(15).text = md.resistPhys + "";
			type[md.type].fields.get(16).text = md.toolPower + "";

			type[md.type].fields.get(17).text = md.cooldown + "";
			type[md.type].fields.get(18).text = md.bleed + "";
			type[md.type].fields.get(19).text = md.poison + "";
			type[md.type].fields.get(20).text = md.hpVampire + "";
			type[md.type].fields.get(21).text = md.mpVampire + "";
			type[md.type].fields.get(22).text = md.lightRad + "";
			type[md.type].fields.get(23).text = md.lightR + "";
			type[md.type].fields.get(24).text = md.lightG + "";
			type[md.type].fields.get(25).text = md.lightB + "";

			type[md.type].fields.get(26).text = md.range + "";

			type[md.type].fields.get(27).text = md.modStr + "";
			type[md.type].fields.get(28).text = md.modDex + "";
			type[md.type].fields.get(29).text = md.modCon + "";
			type[md.type].fields.get(30).text = md.modInt + "";
			type[md.type].fields.get(31).text = md.modWis + "";
			type[md.type].fields.get(32).text = md.modCha + "";
			type[md.type].fields.get(33).text = md.modLuck + "";
			type[md.type].fields.get(34).text = md.toolReq + "";

			type[md.type].fields.get(35).text = md.crit + "";
			type[md.type].fields.get(36).text = md.damPercent + "";

			twoHanded.toggled = md.twoHanded;
			offhandable.toggled = md.offhandable;
			indestructible.toggled = md.indestructible;
			unique.toggled = md.unique;
			cantAttack.toggled = md.cantAttack;

			curDamType = md.damType;
			buttonPressed(curDamType + 1000);
			curToolType = md.toolType;
			buttonPressed(curToolType + 2000);
			break;
		case 2:
			i = 0;
			buttonPressed(md.slot + 3000);
			type[md.type].fields.get(i).text = md.dur + "";
			i++;
			type[md.type].fields.get(i).text = md.acSlash + "";
			i++;
			type[md.type].fields.get(i).text = md.acBash + "";
			i++;
			type[md.type].fields.get(i).text = md.acPierce + "";
			i++;
			type[md.type].fields.get(i).text = md.shieldDelay + "";
			i++;
			type[md.type].fields.get(i).text = md.shieldSlash + "";
			i++;
			type[md.type].fields.get(i).text = md.shieldBash + "";
			i++;
			type[md.type].fields.get(i).text = md.shieldPierce + "";
			i++;

			type[md.type].fields.get(i).text = md.elemental[0] + "";
			i++;
			type[md.type].fields.get(i).text = md.elemental[1] + "";
			i++;
			type[md.type].fields.get(i).text = md.elemental[2] + "";
			i++;
			type[md.type].fields.get(i).text = md.elemental[3] + "";
			i++;
			type[md.type].fields.get(i).text = md.elemental[4] + "";
			i++;
			type[md.type].fields.get(i).text = md.elemental[5] + "";
			i++;
			type[md.type].fields.get(i).text = md.resistMag + "";
			i++;
			type[md.type].fields.get(i).text = md.resistPhys + "";
			i++;

			type[md.type].fields.get(i).text = md.poison + "";
			i++;
			type[md.type].fields.get(i).text = md.bleed + "";
			i++;
			type[md.type].fields.get(i).text = md.lightRad + "";
			i++;
			type[md.type].fields.get(i).text = md.lightR + "";
			i++;
			type[md.type].fields.get(i).text = md.lightG + "";
			i++;
			type[md.type].fields.get(i).text = md.lightB + "";
			i++;

			type[md.type].fields.get(i).text = md.acSlashPercent + "";
			i++;
			type[md.type].fields.get(i).text = md.acBashPercent + "";
			i++;
			type[md.type].fields.get(i).text = md.acPiercePercent + "";
			i++;
			type[md.type].fields.get(i).text = md.crit + "";
			i++;
			type[md.type].fields.get(i).text = md.damPercent + "";
			i++;
			type[md.type].fields.get(i).text = md.attackSpeedPercent + "";
			i++;
			type[md.type].fields.get(i).text = md.walkSpeedPercent + "";
			i++;
			type[md.type].fields.get(i).text = md.hitPercent + "";
			i++;

			type[md.type].fields.get(i).text = md.modStr + "";
			i++;
			type[md.type].fields.get(i).text = md.modDex + "";
			i++;
			type[md.type].fields.get(i).text = md.modCon + "";
			i++;
			type[md.type].fields.get(i).text = md.modInt + "";
			i++;
			type[md.type].fields.get(i).text = md.modWis + "";
			i++;
			type[md.type].fields.get(i).text = md.modCha + "";
			i++;
			type[md.type].fields.get(i).text = md.modLuck + "";
			i++;

			indestructible.toggled = md.indestructible;
			unique.toggled = md.unique;

			break;
		case 3:
			i = 0;

			type[md.type].fields.get(i).text = md.dur + "";
			i++;
			type[md.type].fields.get(i).text = md.ticks + "";
			i++;
			type[md.type].fields.get(i).text = md.cooldown + "";
			i++;
			type[md.type].fields.get(i).text = md.rateHP + "";
			i++;
			type[md.type].fields.get(i).text = md.rateMP + "";
			i++;
			type[md.type].fields.get(i).text = md.rateE + "";
			i++;
			type[md.type].fields.get(i).text = md.modHP + "";
			i++;
			type[md.type].fields.get(i).text = md.modMP + "";
			i++;
			type[md.type].fields.get(i).text = md.modE + "";
			i++;

			type[md.type].fields.get(i).text = md.modEXP + "";
			i++;
			type[md.type].fields.get(i).text = md.modStr + "";
			i++;
			type[md.type].fields.get(i).text = md.modDex + "";
			i++;
			type[md.type].fields.get(i).text = md.modCon + "";
			i++;
			type[md.type].fields.get(i).text = md.modInt + "";
			i++;
			type[md.type].fields.get(i).text = md.modWis + "";
			i++;
			type[md.type].fields.get(i).text = md.modCha + "";
			i++;
			type[md.type].fields.get(i).text = md.modLuck + "";
			i++;

			type[md.type].fields.get(i).text = md.permHP + "";
			i++;
			type[md.type].fields.get(i).text = md.permMP + "";
			i++;
			type[md.type].fields.get(i).text = md.permE + "";
			i++;
			type[md.type].fields.get(i).text = md.permEXP + "";
			i++;

			type[md.type].fields.get(i).text = md.permStr + "";
			i++;
			type[md.type].fields.get(i).text = md.permDex + "";
			i++;
			type[md.type].fields.get(i).text = md.permCon + "";
			i++;
			type[md.type].fields.get(i).text = md.permInt + "";
			i++;
			type[md.type].fields.get(i).text = md.permWis + "";
			i++;
			type[md.type].fields.get(i).text = md.permCha + "";
			i++;
			type[md.type].fields.get(i).text = md.permLuck + "";
			i++;

			stackEffect.toggled = md.stackEffect;
			break;
		}

		buttonPressed(curType + 22);
	}

	@Override
	public void buttonPressed(int id) {
		try {
			if (id < 17) {
				for (int i = 0; i < 17; i++) {
					page[0].buttons.get(i).toggled = false;
				}
				page[0].buttons.get(id).toggled = true;
				curSelSet = id;
			} else if (id > 16 && id < 22) {
				for (int i = 17; i < 22; i++) {
					page[0].buttons.get(i).toggled = false;
				}
				page[0].buttons.get(id).toggled = true;
				curRare = id - 17;
			} else if (id > 21 && id < 26) {
				for (int i = 22; i < 26; i++) {
					page[1].buttons.get(i - 22).toggled = false;
					type[i - 22].visible = false;
				}
				page[1].buttons.get(id - 22).toggled = true;
				type[id - 22].visible = true;
				curType = id - 22;
				buttonPressed(curDamType + 1000);
			} else {
				switch (id) {
				case 800:
					page[curPage].visible = false;
					curPage--;
					if (curPage < 0) {
						curPage = NUM_PAGES - 1;
					}
					page[curPage].visible = true;
					break;
				case 801:
					page[curPage].visible = false;
					curPage++;
					if (curPage >= NUM_PAGES) {
						curPage = 0;
					}
					page[curPage].visible = true;
					break;
				case 900:
					change("editList");
					Scene.input.wasMouseJustClicked[0] = false;
					break;
				case 901:
					confirm();
					break;
				case 1000:
				case 1001:
				case 1002:
					for (int i = 0; i < 3; i++) {
						type[1].buttons.get(i).toggled = false;
					}
					type[1].buttons.get(id - 1000).toggled = true;
					curDamType = id - 1000;
					break;
				case 2000:
				case 2001:
				case 2002:
				case 2003:
				case 2004:
					for (int i = 3; i < 8; i++) {
						type[1].buttons.get(i).toggled = false;
					}
					type[1].buttons.get(id - 2000 + 3).toggled = true;
					curToolType = id - 2000;
					break;
				case 3000:
				case 3001:
				case 3002:
				case 3003:
				case 3004:
				case 3005:
				case 3006:
				case 3007:
				case 3008:
				case 3009:
				case 3010:
				case 3011:
					for (int i = 0; i < Shared.armorTypes.length; i++) {
						type[2].buttons.get(i).toggled = false;
					}
					type[2].buttons.get(id - 3000).toggled = true;
					curSlot = id - 3000;
					break;
				}
			}
		} catch (

		Exception e) {
			Log.error(e);
		}
	}

	void send(ItemData md) {
		md.name = name.text;
		md.spriteSet = curSet;
		md.sprite = curSprite;
		md.rarity = curRare;
		md.type = curType;

		md.weight = ((double) Integer.parseInt(page[0].fields.get(0).text)) / 10.0;
		md.reqLevel = Integer.parseInt(page[0].fields.get(1).text);

		for (int i = 0; i < 7; i++) {
			md.reqStat[i] = Integer.parseInt(page[0].fields.get(i + 2).text);
		}

		for (int i = 0; i < Shared.classes.length; i++) {
			md.reqClass[i] = page[0].buttons.get(22 + i).toggled;
		}

		md.undroppable = undroppable.toggled;
		md.unstackable = unstackable.toggled;

		Odyssey.game.sendTCP(md);
	}

	void confirm() {
		ItemData md = new ItemData(num);
		// ItemWieldableData wield;
		int i = 0;
		switch (curType) {
		case 0:
			i = 0;
			md.modStr = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modDex = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modCon = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modInt = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modWis = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modCha = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modLuck = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modHP = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modMP = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modE = Integer.parseInt(type[curType].fields.get(i).text);
			i++;

			md.stackEffect = stackEffect.toggled;
			if (md.stackEffect) {
				Log.debug("c: SE");
			} else {
				Log.debug("c: not SE");
			}
			send(md);
			break;
		case 1: // wieldable

			md.dur = Integer.parseInt(type[curType].fields.get(0).text);
			md.hitDice = Integer.parseInt(type[curType].fields.get(1).text);
			md.hitSides = Integer.parseInt(type[curType].fields.get(2).text);
			md.hitBonus = Integer.parseInt(type[curType].fields.get(3).text);
			md.damDice = Integer.parseInt(type[curType].fields.get(4).text);
			md.damSides = Integer.parseInt(type[curType].fields.get(5).text);
			md.damBonus = Integer.parseInt(type[curType].fields.get(6).text);
			md.ammoType = Integer.parseInt(type[curType].fields.get(7).text);

			for (i = 0; i < 6; i++) {
				md.elemental[i] = Integer.parseInt(type[curType].fields.get(8 + i).text);
			}
			md.resistMag = Integer.parseInt(type[curType].fields.get(14).text);
			md.resistPhys = Integer.parseInt(type[curType].fields.get(15).text);
			md.toolPower = Integer.parseInt(type[curType].fields.get(16).text);
			md.damType = curDamType;
			md.toolType = curToolType;

			md.cooldown = Integer.parseInt(type[curType].fields.get(17).text);
			md.bleed = Integer.parseInt(type[curType].fields.get(18).text);
			md.poison = Integer.parseInt(type[curType].fields.get(19).text);
			md.hpVampire = Integer.parseInt(type[curType].fields.get(20).text);
			md.mpVampire = Integer.parseInt(type[curType].fields.get(21).text);
			md.lightRad = Integer.parseInt(type[curType].fields.get(22).text);
			md.lightR = Integer.parseInt(type[curType].fields.get(23).text);
			md.lightG = Integer.parseInt(type[curType].fields.get(24).text);
			md.lightB = Integer.parseInt(type[curType].fields.get(25).text);

			md.range = Integer.parseInt(type[curType].fields.get(26).text);
			md.modStr = Integer.parseInt(type[curType].fields.get(27).text);
			md.modDex = Integer.parseInt(type[curType].fields.get(28).text);
			md.modCon = Integer.parseInt(type[curType].fields.get(29).text);
			md.modInt = Integer.parseInt(type[curType].fields.get(30).text);
			md.modWis = Integer.parseInt(type[curType].fields.get(31).text);
			md.modCha = Integer.parseInt(type[curType].fields.get(32).text);
			md.modLuck = Integer.parseInt(type[curType].fields.get(33).text);
			md.toolReq = Integer.parseInt(type[curType].fields.get(34).text);

			md.crit = Integer.parseInt(type[curType].fields.get(35).text);
			md.damPercent = Integer.parseInt(type[curType].fields.get(36).text);

			md.twoHanded = twoHanded.toggled;
			md.offhandable = offhandable.toggled;
			md.indestructible = indestructible.toggled;
			md.unique = unique.toggled;
			md.cantAttack = cantAttack.toggled;

			send(md);

			break;
		case 2:
			md.slot = curSlot;

			i = 0;
			md.dur = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.acSlash = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.acBash = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.acPierce = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.shieldDelay = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.shieldSlash = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.shieldBash = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.shieldPierce = Integer.parseInt(type[curType].fields.get(i).text);
			i++;

			md.elemental[0] = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.elemental[1] = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.elemental[2] = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.elemental[3] = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.elemental[4] = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.elemental[5] = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.resistMag = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.resistPhys = Integer.parseInt(type[curType].fields.get(i).text);
			i++;

			md.poison = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.bleed = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.lightRad = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.lightR = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.lightG = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.lightB = Integer.parseInt(type[curType].fields.get(i).text);
			i++;

			md.acSlashPercent = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.acBashPercent = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.acPiercePercent = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.crit = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.damPercent = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.attackSpeedPercent = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.walkSpeedPercent = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.hitPercent = Integer.parseInt(type[curType].fields.get(i).text);
			i++;

			md.modStr = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modDex = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modCon = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modInt = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modWis = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modCha = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modLuck = Integer.parseInt(type[curType].fields.get(i).text);
			i++;

			md.indestructible = indestructible.toggled;
			md.unique = unique.toggled;

			send(md);
			break;
		case 3:
			i = 0;
			md.dur = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.ticks = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.cooldown = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.rateHP = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.rateMP = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.rateE = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modHP = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modMP = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modE = Integer.parseInt(type[curType].fields.get(i).text);
			i++;

			md.modEXP = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modStr = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modDex = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modCon = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modInt = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modWis = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modCha = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.modLuck = Integer.parseInt(type[curType].fields.get(i).text);
			i++;

			md.permHP = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.permMP = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.permE = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.permEXP = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.permStr = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.permDex = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.permCon = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.permInt = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.permWis = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.permCha = Integer.parseInt(type[curType].fields.get(i).text);
			i++;
			md.permLuck = Integer.parseInt(type[curType].fields.get(i).text);
			i++;

			md.stackEffect = stackEffect.toggled;
			send(md);
			break;
		}

		Scene.lock();

	}

	@Override
	public void enterPressedInField(int id) {
		confirm();
	}

}
