package com.bg.bearplane.gui;


public abstract class Component {

	Scene scene;

	public int id = 0;
	public int x = 0;
	public int y = 0;
	public int width = 32;
	public int height = 32;
	long stamp = 0;

	long tick = 0;

	public boolean visible = true;
	public boolean updateAnyway = false;
	public boolean disabled = false;
	public boolean centered = false;

	public String text = "";

	public Component() {

	}

	public Component(Scene scene, int id, int x, int y) {
		this.scene = scene;
		this.x = x;
		this.y = y;
		this.id = id;
	}

	public abstract void render();

	public abstract void update();

	public void updateComponent(long tick) {
		this.tick = tick;
		if ((!disabled && visible) || updateAnyway) {
			update();
		}
	}

	public void renderComponent() {
		if (visible) {
			render();
		}
	}

}
