package com.bg.ody.client.core;

import com.bg.ody.shared.MonsterData;

public class Monster extends Sprite {

	public int type = 0;

	public Monster(int set, int sprite) {
		super(set, sprite);
		dir = 1;
	}

	public Monster(int type) {
		super();
		this.type = type;
		dir = 1;
		set = data().spriteSet;
		sprite = data().sprite;
		name = data().name;
		
	}

	public MonsterData data() {
		return World.monsterData[type];
	}

}
