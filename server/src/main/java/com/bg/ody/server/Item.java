package com.bg.ody.server;

import com.bg.ody.shared.ItemData;

public class Item {

	public int id = 0; // item number
	public int qty = 1;

	public int map = 0;
	public int x = 0;
	public int y = 0; // for when item is on map

	Mobile owner = null;

	public Item(int id, int qty, int map, int x, int y) {
		this.id = id;
		this.qty = qty;
		this.map = map;
		this.x = x;
		this.y = y;
	}

	public Item(int id, int qty, Mobile owner) {
		this.id = id;
		this.qty = qty;
		this.owner = owner;
	}

	public double getWeight() {
		return data().weight;
	}

	public ItemData data() {
		return Realm.itemData[id];
	}

}
