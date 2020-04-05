package com.bg.ody.client.core;

import com.bg.bearplane.engine.DrawTask;
import com.bg.ody.shared.DoorData;

public class Door extends DoorData {

	public boolean hover = false;

	public Door(int id) {
		super(id);
	}

	public Door(int id, int type, int x, int y, int d, int openTime, int flags, int key) {
		super(id, type, x, y, d, openTime, flags, key);
	}

	public int getTrueX() {
		if (d < 2) {
			return x * 32 + 16;
		} else if (d == 2) {
			return x * 32 - 16 + 16;
		} else {
			return x * 32 + 16 + 16;
		}
	}

	public int getTrueY() {
		if (d < 2) {
			return y * 32 + 16;
		} else {
			return y * 32 - 16 + 8;
		}
	}

	public void update(long tick) {
		long diff = 0;
		this.tick = tick;
		if (d > 1 && type % 2 == 1) {
			type--;
		}
		if (state == 0) {

		} else if (state >= 1) {
			if (tick > stamp) {
				if (state == 1) {
					open = true;
				} else {
					open = false;
				}
				// open = true;
				state = 0;
			} else {
				diff = stamp - tick;
				int p = 0;
				if (gate == 1) {
					p = (int) (((float) diff / (float) (openTime)) * 17f);
					step = p;
				} else if (gate == 2) {
					p = (int) (((float) diff / (float) (openTime)) * 17f);
					step = p;
				} else {
					p = (int) (((float) diff / (float) (openTime)) * 6f);
					step = p;
				}

			}
		}
	}

	public String getDrawTex() {
		if (gate == 0) {
			if (d == 0) {
				return "vdoor";
			} else if (d == 2) {
				return "hdoor";
			} else if (d == 3) {
				return "hdoor";
			} else {
				return "vdoor";
			}
		} else {
			return "gate";
		}
	}

	public int getDrawY() {
		if (gate == 0) {
			if (d == 0) {
				return y * 32;
			} else if (d == 2) {
				return y * 32 - 32;
			} else if (d == 3) {
				return y * 32 - 32;
			} else {
				return y * 32;
			}
		} else {
			return y * 32;
		}
	}

	public int getDrawX() {
		if (gate == 0) {
			if (d == 0) {
				return x * 32 - 32;
			} else if (d == 2) {
				return x * 32 - 32;
			} else if (d == 3) {
				return x * 32;
			} else {
				return x * 32 - 32;
			}
		} else {
			return x * 32;
		}
	}

	public int getDrawSX() {
		if (gate == 0) {
			return getFrame() * getDrawW();
		} else if (gate == 0 || gate == 1) {
			return 0;
		} else if (gate == 2) {

		} else if (gate == 3) {

		}
		return 0;

	}

	public int getDrawSY() {
		if (gate == 0) {
			return type * getDrawH();
		} else if (gate == 6) {
			return type * 32;
		} else if (gate == 1) {
			if (state == 0) {
				if (open) {
					return type * 32 + 17;
				} else {
					return type * 32;
				}
			} else if (state == 1) {
				int a = type * 32 + (17 - step);
				return a;
			} else if (state == 2) {
				int a = type * 32 + step;
				return a;
			}
		} else if (gate == 2) {
			if (state == 0) {
				if (open) {
					return type * 32 + 17;
				} else {
					return type * 32;
				}
			} else if (state == 1) {
				return type * 32 + (17 - step);
			} else if (state == 2) {
				return type * 32 + step;
			}
		}
		return type * getDrawH();
	}

	public DrawTask getDrawTask() {
		DrawTask dt = null;
		if (gate > 1) {
			if (open) {
				return null;
			}
		}
		dt = new DrawTask(3, getDrawTex(), getDrawX(), getDrawY(), getDrawSX(), getDrawSY(), getDrawW(), getDrawH());
		return dt;
	}

	public int getZOrder() {
		int ly = -1;
		if (gate == 0) {
			if (d == 0) {
				ly = 32 + y * 32;
			} else if (d == 2) {
				ly = 32 + y * 32 - 32 + 7;
			} else if (d == 3) {
				ly = 32 + y * 32 - 32 + 7;
			} else {
				ly = 32 + y * 32 + 1;
			}
		} else {
			ly = 32 + y * 32;
		}
		return ly;
	}

	public int getOpenFrame() {
		if (d == 0) {
			return 10;
		} else if (d == 1) {
			return 0;
		} else if (d == 2) {
			return 10;
		} else if (d == 3) {
			return 0;
		}
		return 5;
	}

	public int getDrawW() {
		if (gate == 0) {
			if (d < 2) {
				return 96;
			} else {
				return 64;
			}
		} else {
			return 32;
		}
	}

	public int getDrawH() {
		if (gate == 0) {
			return 64;
		} else {
			return 32;
		}
	}

	public int getHoverDrawX() {
		if (d == 0) {
			return x * 32 - 32;
		} else if (d == 2) {
			return x * 32 - 32;
		} else if (d == 3) {
			return x * 32;
		} else {
			return x * 32 - 32;
		}
	}

	public int getHoverDrawY() {
		if (d == 0) {
			return 32 + y * 32 + 7;
		} else if (d == 2) {
			return 32 + y * 32 - 32;
		} else if (d == 3) {
			return 32 + y * 32 - 32;
		} else {
			return 32 + y * 32 + 7;
		}
	}

	public int getFrame() {
		if (state == 0) {
			if (open) {
				return getOpenFrame();
			} else {
				return 5;
			}
		} else if (state == 1) {
			if (d == 0) {
				if (step == 6) {
					return 5;
				} else {
					return 5 + (6 - (step + 1));
				}
			} else if (d == 1) {
				if (step == 6) {
					return 5;
				} else {
					return step;
				}
			} else if (d == 2) {
				if (step == 6) {
					return 5;
				} else {
					return 5 + (6 - (step + 1));
				}
			} else if (d == 3) {
				if (step == 6) {
					return 5;
				} else {
					return step;
				}
			}
		} else if (state == 2) {
			if (d == 0) {
				if (step == 6) {
					return getOpenFrame();
				} else {
					return 5 + step;
				}
			} else if (d == 1) {
				if (step == 6) {
					return getOpenFrame();
				} else {
					return (6 - (step + 1));
				}
			} else if (d == 2) {
				if (step == 6) {
					return getOpenFrame();
				} else {
					return 5 + step;
				}
			} else if (d == 3) {
				if (step == 6) {
					return getOpenFrame();
				} else {
					return (6 - (step + 1));
				}
			}
		}
		return 5;
	}
}
