package com.bg.ody.shared;

import java.io.Serializable;

public class MonsterData implements Serializable {

	private static final long serialVersionUID = 1L;
	public String name = "";
	public int maxHP = 0;
	public int spriteSet = 0;
	public int sprite = 0;
	public int id = 0;

	public int dodge = 0;
	public int accuracy;
	public int acPierce = 0;
	public int acSlash = 0;
	public int acBludge = 0;

	public int damDice = 0;
	public int damSides = 0;
	public int damBonus = 0;
	public int damType = 0;

	public int walkSpeed = 0;
	public int attackSpeed = 0;
	
	public boolean friendly = false;
	public boolean guard = false;
	
	public int wanderRange = 0;
	public int stepsPerWalk = 0;

	public int sight = 8;
	
	public MonsterData() {

	}

	public MonsterData(int id) {
		this.id = id;
	}

	public MonsterData(int id, String name, int maxHP, int spriteSet, int sprite) {
		this.id = id;
		this.name = name;
		this.maxHP = maxHP;
		this.spriteSet = spriteSet;
		this.sprite = sprite;
	}

}
