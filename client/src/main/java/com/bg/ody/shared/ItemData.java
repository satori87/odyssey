package com.bg.ody.shared;

import java.io.Serializable;

public class ItemData implements Serializable {
	private static final long serialVersionUID = 1L;

	public int type = 0;
	public int subtype = 0;

	public String name = "";
	public int id = 0;
	
	public int sprite = 0;
	public int spriteSet = 0;

	public ItemData() {

	}

	public ItemData(int id) {
		this.id = id;
	}

	public ItemData(int id, String name, int type) {
		this.id = id;
		this.name = name;
	}

}
