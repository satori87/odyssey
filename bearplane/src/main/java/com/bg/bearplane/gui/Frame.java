package com.bg.bearplane.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bg.bearplane.engine.BearGame;

public class Frame {

	Scene scene;

	public boolean useBackground = true;
	public boolean useFrame = true;
	public boolean centered = false;

	public int x = 0;
	public int y = 0;

	public int width = 32;
	public int height = 32;

	public boolean visible = true;
	public boolean relative = false;

	public List<Frame> frames = new LinkedList<Frame>();
	public List<Button> buttons = new LinkedList<Button>();
	public List<Label> labels = new LinkedList<Label>();
	public List<TextBox> textBoxes = new LinkedList<TextBox>();
	public List<Dialog> dialogs = new ArrayList<Dialog>();
	public List<CheckBox> checkBoxes = new ArrayList<CheckBox>();

	Frame parent = null;

	public Frame(Scene scene, int x, int y, int width, int height, boolean useBackground, boolean centered,
			boolean useFrame) {
		this.scene = scene;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.useBackground = useBackground;
		this.centered = centered;
		this.useFrame = useFrame;
	}

	public Frame(Scene scene, int x, int y, int width, int height, boolean useBackground, boolean centered) {
		this(scene, x, y, width, height, useBackground, centered, true);
	}

	int focus = 0;

	void nextFocus() {
		if (textBoxes.size() > 0) {
			boolean f = false;
			for (TextBox t : textBoxes) {
				if (t.focus) {
					f = true;
				}
			}
			if (f) {
				if (textBoxes.get(focus) != null) {
					textBoxes.get(focus).focus = false;
				}
				focus++;
				if (focus >= textBoxes.size()) {
					focus = 0;
				}
				if (textBoxes.get(focus) != null) {
					textBoxes.get(focus).focus = true;
				}
			}
		}
	}

	public void update(long tick) {
		if (!visible) {
			return;
		}
		for (Frame d : frames) {
			d.update(tick);
		}
		for (Button b : buttons) {
			b.update(tick);
		}
		for (TextBox t : textBoxes) {
			t.update(tick);
		}
		for (CheckBox c : checkBoxes) {
			c.update(tick);
		}
	}

	public void render() {
		if (!visible) {
			return;
		}



		if (centered) {
			this.x -= (width / 2);
			this.y -= (height / 2);
		}
		if (parent != null && relative) {
			this.x += parent.x;
			this.y += parent.y;
		}
		TextureRegion[] frame = BearGame.assets.frame;

		if (useBackground) {
			// for(int a = 0; a < height; a+= 8) {
			// for(int b = 0; b < width; b += 8) {
			// scene.drawRegion(frame[8],x+b, y+a, false, 0, 1);
			// }
			// }
			scene.draw(BearGame.assets.bg[1], x + 4, y + 4, width - 8, height - 8, 0, 0, width, height);
		}
		if (useFrame) {
			// draw top left
			scene.drawRegion(frame[0], x, y, false, 0, 1);
			// top right
			scene.drawRegion(frame[1], x + width - 32, y, false, 0, 1);
			// bottom left
			scene.drawRegion(frame[2], x, y + height - 32, false, 0, 1);
			// bottom right
			scene.drawRegion(frame[3], x + width - 32, y + height - 32, false, 0, 1);

			// left side
			for (int b = 32; b < height - 32; b += 32) {
				scene.drawRegion(frame[4], x, y + b, false, 0, 1);
			}
			// right side
			for (int b = 32; b < height - 32; b += 32) {
				scene.drawRegion(frame[5], x + width - 32, y + b, false, 0, 1);
			}
			// top side
			for (int b = 32; b < width - 32; b += 32) {
				scene.drawRegion(frame[6], x + b, y, false, 0, 1);
			}
			// bottom side
			for (int b = 32; b < width - 32; b += 32) {
				scene.drawRegion(frame[7], x + b, y + height - 32, false, 0, 1);
			}
		}
		if (centered) {
			this.x += (width / 2);
			this.y += (height / 2);
		}
		if (parent != null && relative) {
			this.x -= parent.x;
			this.y -= parent.y;
		}
		for (Frame d : frames) {
			d.render();
		}
		for (Button b : buttons) {
			b.render();
		}
		for (Label l : labels) {
			l.render();
		}
		for (TextBox t : textBoxes) {
			t.render();
		}
		for (CheckBox c : checkBoxes) {
			c.render();
		}
	}

}
