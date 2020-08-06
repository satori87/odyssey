package com.bg.ody.shared;

import java.io.Serializable;

public class ItemData implements Serializable {

	private static final long serialVersionUID = 3L;

	public String name = "";
	public int id = 0;

	public int sprite = 0;
	public int spriteSet = 0;

	public int rarity = 0;
	public double weight = 0.0;

	public int reqLevel = 0;
	public boolean[] reqClass = new boolean[Shared.classes.length];
	public int[] reqStat = new int[7];

	public int hitDice = 0;
	public int hitSides = 0;
	public int hitBonus = 0;
	public int damDice = 0;
	public int damSides = 0;
	public int damBonus = 0;
	public int damType = 0;
	public int ammoType = 0;
	public int toolPower = 0;
	public int toolType = 0;

	public int cooldown = 0;
	public int bleed = 0;
	public int poison = 0;
	public int hpVampire = 0;
	public int mpVampire = 0;
	public int lightR = 0;
	public int lightG = 0;
	public int lightB = 0;
	public int lightRad = 0;

	public int range = 1;

	public int toolReq = 0;
	public int crit = 0;
	public int damPercent = 0;

	public int dur = 0;
	public int[] elemental = new int[6];
	public int resistPhys = 0;
	public int resistMag = 0;

	public boolean undroppable = false;
	public boolean unstackable = false;

	public boolean stackEffect = false;

	public boolean twoHanded = false;
	public boolean offhandable = false;
	public boolean indestructible = false;
	public boolean unique = false;
	public boolean cantAttack = false;

	public int type = 0; // 0 = resource, 1 = wearable, 2 = wieldable, 3 = consumable

	public int slot = 0;
	public int acSlash = 0;
	public int acBash = 0;
	public int acPierce = 0;

	public int acSlashPercent = 0;
	public int acBashPercent = 0;
	public int acPiercePercent = 0;

	public int shieldSlash = 0;
	public int shieldBash = 0;
	public int shieldPierce = 0;
	public int shieldDelay = 0;

	public int attackSpeedPercent = 0;
	public int walkSpeedPercent = 0;
	public int hitPercent = 0;

	public int rateHP = 0;
	public int rateMP = 0;
	public int rateE = 0; // restores this much energy per tick
	public int ticks = 0; // how many ticks of length dur to run for
	public int modHP = 0; // modifies max HP for duration*ticks
	public int modMP = 0;
	public int modE = 0;
	public int modEXP = 0; // factor * 100
	public int modStr = 0;
	public int modInt = 0;
	public int modDex = 0;
	public int modWis = 0;
	public int modCon = 0;
	public int modCha = 0;
	public int modLuck = 0;
	
	public int permHP = 0;
	public int permMP = 0;
	public int permE = 0;
	public int permEXP = 0;
	public int permStr = 0;
	public int permDex = 0;
	public int permCon = 0;
	public int permInt = 0;
	public int permWis = 0;
	public int permCha = 0;
	public int permLuck = 0;
	
	
	public ItemData() {

	}

	public ItemData(int id) {
		this.id = id;
	}

}
