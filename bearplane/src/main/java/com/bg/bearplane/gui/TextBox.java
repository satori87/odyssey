package com.bg.bearplane.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.bg.bearplane.engine.BearGame;
import com.bg.bearplane.engine.Log;

public class TextBox extends Component {

	public boolean focus = false;
	public boolean blink = false;
	public long blinkStamp = 0;

	public int max = 10;

	public boolean allowSpecial = false;
	public boolean allowLetters = true;

	public boolean password = false;

	public Frame frame = null;

	public TextBox(Scene scene, int id, int max, boolean focus, int x, int y, int width, boolean centered,
			Frame frame) {
		super(scene, id, x, y + 16);
		this.max = max;
		this.focus = focus;
		this.frame = frame;
		if (centered) {
			this.x -= width / 2;
		}
		this.width = width;
	}

	public TextBox(Scene scene, int id, int max, boolean focus, int x, int y, int width, boolean centered) {
		this(scene, id, max, focus, x, y, width, centered, null);
	}

	static public boolean inCentered(int x, int y, int centerX, int centerY, int width, int height) {
		int topY = centerY - (height / 2);
		int bottomY = centerY + (height / 2);
		int leftX = centerX - (width / 2);
		int rightX = centerX + (width / 2);
		if (x > leftX && x < rightX && y > topY && y < bottomY) {
			return true;
		}
		return false;
	}

	static public boolean inBox(int x, int y, int lowerX, int upperX, int lowerY, int upperY) {
		return (x >= lowerX && x <= upperX && y >= lowerY && y <= upperY);
	}

	public void update() {
		try {
			int mX = Scene.input.mouseX;
			int mY = Scene.input.mouseY;
			if (Scene.input.wasMouseJustClicked[0]) {
				if (inBox(mX, mY, x, x + width, y - 7, y + 10 + 36)) {
					Scene.input.wasMouseJustClicked[0] = false;
					for (TextBox t : scene.textBoxes) {
						t.focus = false;
					}
					for (Frame f : scene.frames) {
						for (TextBox t : f.textBoxes) {
							t.focus = false;
						}
						for(Frame ff : f.frames) {
							for (TextBox t : ff.textBoxes) {
								t.focus = false;
							}
						}
					}
					focus = true;
				}
			}
			if (tick > blinkStamp) {
				blink = !blink;
				blinkStamp = tick + 400;
			}
			if (focus) {
				processKeys(max);
			} else {
			}
			if (!allowLetters) {
				if (text.length() == 0) {
					text = "0";
				} else {
					text = Integer.parseInt(text) + "";
				}
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	void processKey(int a) {
		if (!allowLetters && text.length() == 1 && text.equals("0")) {

		}
		try {
			switch (a) {
			case Input.Keys.SPACE:
				if (text.length() < max) {
					text += " ";
				}
				break;
			case Input.Keys.BACKSPACE:
				if (text.length() > 0) {
					text = text.substring(0, text.length() - 1);
				}
				break;
			case Input.Keys.TAB:
				if (frame == null) {
					scene.nextFocus();
				} else {
					frame.nextFocus();
				}
				break;
			case Input.Keys.ENTER:
				scene.enterPressedInField(id);
				// Gdx.input.setOnscreenKeyboardVisible(false);
				break;
			default:
				if (text.length() < max) {
					if (a >= 29 && a <= 54) {
						if (allowLetters) {
							if (Scene.input.keyDown[Input.Keys.SHIFT_LEFT]
									|| Scene.input.keyDown[Input.Keys.SHIFT_RIGHT]) {
								text += Input.Keys.toString(a);
							} else {
								text += Input.Keys.toString(a).toLowerCase();
							}
						}
					} else if (a >= 144 && a <= 153) {
						text += (a - 144) + "";
					} else if (a >= 7 && a <= 16) {
						if (!Scene.input.keyDown[Input.Keys.SHIFT_LEFT]
								&& !Scene.input.keyDown[Input.Keys.SHIFT_RIGHT]) {
							text += Input.Keys.toString(a);
						} else {
							if (allowSpecial) {
								switch (a) {
								case 16:
									text += "(";
									break;
								case 7:
									text += ")";
									break;
								case 8:
									text += "!";
									break;
								case 9:
									text += "@";
									break;
								case 10:
									text += "#";
									break;
								case 11:
									text += "$";
									break;
								case 12:
									text += "%";
									break;
								case 13:
									text += "^";
									break;
								case 14:
									text += "&";
									break;
								case 15:
									text += "*";
									break;
								}
							}
						}
					} else {
						if (allowSpecial) {
							if (!Scene.input.keyDown[Input.Keys.SHIFT_LEFT]
									&& !Scene.input.keyDown[Input.Keys.SHIFT_RIGHT]) {
								switch (a) {
								case 55:
									text += ",";
									break;
								case 56:
									text += ".";
									break;
								case 68:
									text += "`";
									break;
								case 69:
									text += "-";
									break;
								case 70:
									text += "=";
									break;
								case 71:
									text += "[";
									break;
								case 72:
									text += "]";
									break;
								case 73:
									text += "\\";
									break;
								case 74:
									text += ";";
									break;
								case 75:
									text += "'";
									break;
								case 76:
									text += "/";
									break;
								}
							} else {
								switch (a) {
								case 55:
									text += "<";
									break;
								case 56:
									text += ">";
									break;
								case 68:
									text += "~";
									break;
								case 69:
									text += "_";
									break;
								case 70:
									text += "+";
									break;
								case 71:
									text += "{";
									break;
								case 72:
									text += "}";
									break;
								case 73:
									text += "|";
									break;
								case 74:
									text += ":";
									break;
								case 75:
									text += "\"";
									break;
								case 76:
									text += "?";
									break;
								}
							}
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void processKeys(int max) {
		try {
			for (Integer a : Scene.input.keyPress) {
				if (a == Keys.V
						&& (Scene.input.keyDown[Keys.CONTROL_LEFT] || Scene.input.keyDown[Keys.CONTROL_RIGHT])) {
					Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
					String s = (String) c.getData(DataFlavor.stringFlavor);
					if (s.length() > 0) {
						text += s;
					}
				} else {
					processKey(a);
				}
			}
			for (int i = 0; i < 255; i++) {
				if (Scene.input.keyDown[i] && Scene.tick - Scene.input.keyDownAt[i] > 500) {
					if (Scene.tick - Scene.lastRepeat > 50) {
						Scene.lastRepeat = Scene.tick;
						processKey(i);
					}
				}
			}
			Scene.input.keyPress.clear();
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void render() {
		try {
			// render thyself, peasant
			int l = 0;
			int sx = 12;
			scene.drawRegion(BearGame.assets.field[l][0], x, y, false, 0, 2);
			for (int b = sx; b <= width + 8; b += 8) {
				scene.drawRegion(BearGame.assets.field[l][1], x + b, y, false, 0, 2);
			}
			scene.drawRegion(BearGame.assets.field[l][2], sx + 6 + x + width - 6, y, false, 0, 2);
			float len = 0;
			if (password) {
				String p = "";
				for (int i = 0; i < text.length(); i++) {
					p += "*";
				}
				scene.drawFont(0, sx + x + width / 2, y + 28 - 2, p, true, 1.6f, Color.WHITE);
				len = BearGame.assets.getStringWidth(p, 1.6f, 1, 0);
			} else {
				scene.drawFont(0, sx + x + width / 2, y + 28 - 2, text, true, 1.6f, Color.WHITE);
				len = BearGame.assets.getStringWidth(text, 1.6f, 1, 0);
			}
			if (focus && blink) {
				scene.drawFont(0, sx + (int) (x + width / 2 + len / 2), y + 28 - 2, "|", true, 1.6f, Color.WHITE);
			}
		} catch (

		Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

}
