package com.bg.ody.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.MySQL;
import com.bg.ody.shared.ItemData;
import com.bg.ody.shared.MapData;
import com.bg.ody.shared.MonsterData;
import com.bg.ody.shared.PMap;
import com.bg.ody.shared.Shared;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Realm {

	Game game;

	public static MonsterData[] monsterData = new MonsterData[Shared.NUM_MONSTERS];
	public static ItemData[] itemData = new ItemData[Shared.NUM_ITEMS];

	public static Map[] map = new Map[Shared.NUM_MAPS];
	public static Realm world;

	long tick = 0;

	public Realm(Game game) {
		this.game = game;
		for (int i = 0; i < Shared.NUM_MONSTERS; i++) {
			monsterData[i] = new MonsterData(i);
		}
		for (int i = 0; i < Shared.NUM_ITEMS; i++) {
			itemData[i] = new ItemData(i);
		}
		for (int i = 0; i < Shared.NUM_MAPS; i++) {
			map[i] = new Map(game, i);
		}
		world = this;
	}

	public void update(long tick) {
		this.tick = tick;
		for (Map m : map) {
			if (m != null) {
				m.update(tick);
			}
		}
	}

	static void loadMaps() {
		File f = null;
		try {
			f = new File(new File(".").getCanonicalPath() + "/maps");
		} catch (IOException e) {
			e.printStackTrace();
		}
		int n = 0;
		MapData md = null;
		PMap pm = null;
		if (f != null) {
			String[] pathnames = f.list();
			if (pathnames != null) {
				for (String s : pathnames) {
					n = Integer.parseInt(s.substring(3).substring(0, s.length() - 7));
					md = loadMap(n);
					pm = new PMap();
					md.checkAll(pm);
					map[n].loadEssentials(md, pm);
					map[n].reset();
				}
			}
		}
	}

	public static void load() {
		Shared.populateEdges();
		loadMaps();
		loadMonsters();
		loadItems();
	}

	public static boolean monsterExists(int mon) {
		boolean found = false;
		String statement = "SELECT * FROM MONSTERS WHERE id=" + mon;
		ResultSet rs = MySQL.get(statement);
		try {
			while (rs.next()) {
				found = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return found;
	}

	public static boolean itemExists(int item) {
		boolean found = false;
		String statement = "SELECT * FROM items WHERE id=" + item;
		ResultSet rs = MySQL.get(statement);
		try {
			while (rs.next()) {
				found = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return found;
	}

	public static void saveMonster(int mon) {
		if (!monsterExists(mon)) {
			MySQL.save("INSERT INTO monsters (id) VALUES (" + mon + ")");
		}
		String statement = "UPDATE monsters SET name=?,data=? WHERE id=?";
		LinkedList<Object> obj = new LinkedList<Object>();
		obj.add(monsterData[mon].name);
		obj.add(BearTool.serialize(monsterData[mon]));
		obj.add(mon);
		MySQL.save(statement, obj);
	}

	public static void saveItem(int item) {
		if (!itemExists(item)) {
			MySQL.save("INSERT INTO items (id) VALUES (" + item + ")");
		}
		String statement = "UPDATE items SET name=?,data=? WHERE id=?";
		LinkedList<Object> obj = new LinkedList<Object>();
		obj.add(itemData[item].name);
		obj.add(BearTool.serialize(itemData[item]));
		obj.add(item);
		MySQL.save(statement, obj);
	}

	public static void loadItems() {
		try {
			ResultSet rs;
			rs = MySQL.get("SELECT id,data FROM items");
			while (rs.next()) {
				itemData[rs.getInt(1)] = (ItemData) BearTool.deserialize((byte[]) rs.getObject(2), ItemData.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadMonsters() {
		try {
			ResultSet rs;
			rs = MySQL.get("SELECT id,data FROM monsters");
			while (rs.next()) {
				monsterData[rs.getInt(1)] = (MonsterData) BearTool.deserialize((byte[]) rs.getObject(2),
						MonsterData.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MapData loadMap(int i) {
		InputStream inputStream;
		MapData m = null;
		try {
			inputStream = new InflaterInputStream(new FileInputStream("maps/map" + i + ".map"));
			Input input = new Input(inputStream);
			Kryo kryo = new Kryo();
			m = kryo.readObject(input, MapData.class);
			input.close();
		} catch (Exception e) {
			m = new MapData();
		}
		return m;
	}

	public static void saveMap(int i, MapData m) {
		try {
			OutputStream outputStream = new DeflaterOutputStream(new FileOutputStream("maps/map" + i + ".map"));
			Output output = new Output(outputStream);
			Kryo kryo = new Kryo();
			kryo.writeObject(output, m);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
