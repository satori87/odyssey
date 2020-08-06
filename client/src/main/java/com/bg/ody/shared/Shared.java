package com.bg.ody.shared;

public class Shared {

	public static String TITLE_STRING = "Odyssey Beta";

	public static final int MAX_LOGIN_ATTEMPTS = 4;
	public static final int MAX_USERS = 100;
	public static final int TCP_PORT = 2597;
	public static final int UDP_PORT = 2598;

	public static int GAME_WIDTH = 1366;
	public static int GAME_HEIGHT = 768;

	public static final int MAP_WIDTH = 36;

	public static final int NUM_MAPS = 100;

	public static final int NUM_MONSTERS = 300;
	public static final int NUM_ITEMS = 1000;

	public static final int CHAT_SIZE = 400;

	public static final int MIN_PASS_LEN = 3;
	public static final int MAX_PASS_LEN = 16;
	public static final int MIN_NAME_LEN = 3;
	public static final int MAX_NAME_LEN = 16;

	public static final int BOUNDING_BOX = 5;

	public static final int CHUNK_SIZE = 8192;

	public static final String GAME_NAME = "Odyssey Beta";

	public static final String SERVER_IP = "server.bearable.games";
	// public static final String DEV_IP = "dev.bearable.games";

	public static final String DEV_IP = "127.0.0.1";
	public static final String CLIENT_VERSION = "bananas17";

	public static final int SPAWN_MAP = 1;
	public static final int SPAWN_X = 5;
	public static final int SPAWN_Y = 10;

	public static final String PUBLIC_WEBSITE = "http://bearable.games";

	public static short[][][][] edges = new short[2][2][2][2];

	public static String[] tilesets = new String[] { "magic-terrain", "magic-trees", "magic-nature", "magic-doodads",
			"magic-doodads2", "carpets", "floors-brick", "floors-cobble", "floors-tile", "floors-wood", "floors-misc",
			"misc", "markings", "walls-misc", "walls-wood", "hdoor", "vdoor", "auto-caves", "auto-mountains",
			"auto-terrain", "auto-terrain2", "auto-walls", "auto-walls2" };
	public static String[] spritesets = new String[] { "players", "animals", "beasts", "humanoid", "supernatural",
			"human-common", "human-fighter", "human-magic" };
	public static String[] dirNames = new String[] { "up", "down", "left", "right" };
	public static String[] layerName = new String[] { "Ground", "BG1", "BG2", "Mid", "FG1", "FG2", "Ceiling", "Wall",
			"Shadow", "Att" };
	public static String[] panelName = new String[] { "Options", "Import", "Export", "Test", "Discard", "Commit" };
	public static String[] fxLayerName = new String[] { "BG3", "Mid", "FG0", "Above All" };
	public static String[] gateName = new String[] { "Swings", "Slides Up", "Slides Down", "Slides Left",
			"Slides Right", "Splits", "Disappears" };

	public static String[] objectSets = new String[] { "items_accessory", "items_ammo", "items_body",
			"items_consumable", "items_feet", "items_hand", "items_head", "items_legs", "items_misc", "items_regents",
			"items_shield", "items_tools", "items_weapon_crushing", "items_weapon_piercing", "items_weapon_slashing",
			"items_weapon_slashing2", "items_weapon_wand" };

	public static String[] classes = new String[] { "Knight", "Paladin", "Barbarian", "Rogue", "Cleric", "Dark Mage",
			"Necromancer", "Wizard" };
	
	public static String[] armorTypes = new String[] {"Shield", "Chest", "Cape", "Boots", "Hands", "Head", "Accessory", "Feet", "Ring"};

	public static String[] objRow1 = new String[] { "accessory", "ammo", "body", "consumable", "feet", "hand", "head",
			"legs", "misc" };
	public static String[] objRow2 = new String[] { "regents", "shield", "tool", "crush", "pierce", "slash", "slash2",
			"wand" };

	public static boolean validName(String name) {
		if (name != null && name.length() >= MIN_NAME_LEN && name.length() <= MAX_NAME_LEN) {
			for (char c : name.toCharArray()) {
				int a = (int) c;
				if (!((a >= 48 && a <= 57) || (a >= 65 && a <= 90) || (a >= 97 && a <= 122))) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	public static boolean validUser(String user) {
		return validName(user);
	}

	public static boolean validPass(String pass) {
		return pass != null && pass.length() >= MIN_PASS_LEN && pass.length() <= MAX_PASS_LEN;
	}

	public static void populateEdges() {
		edges[0][0][0][0] = 0;
		edges[0][0][0][1] = 4;
		edges[0][0][1][0] = 1;
		edges[0][0][1][1] = 5;
		edges[0][1][0][0] = 8;
		edges[0][1][0][1] = 12;
		edges[0][1][1][0] = 9;
		edges[0][1][1][1] = 13;
		edges[1][0][0][0] = 2;
		edges[1][0][0][1] = 6;
		edges[1][0][1][0] = 3;
		edges[1][0][1][1] = 7;
		edges[1][1][0][0] = 10;
		edges[1][1][0][1] = 14;
		edges[1][1][1][0] = 11;
		edges[1][1][1][1] = 15;
	}

	public static int getTileSetNum(String name) {
		for (int i = 0; i < tilesets.length; i++) {
			if (tilesets[i].equals(name)) {
				return i;
			}
		}
		return -1;
	}

}
