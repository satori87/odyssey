package com.bg.ody.shared;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.bg.bearplane.net.NetworkRegistrar;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

// This class is a convenient place to keep things common to both the client and server.
public class Registrar implements NetworkRegistrar {

	// This registers objects that are going to be sent over the network.
	public void registerClasses(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(ClientAction.class);
		kryo.register(NewCharacter.class);
		kryo.register(CharacterCreated.class);
		kryo.register(Tile.class);
		kryo.register(PlayerSync.class);
		kryo.register(PlayerData.class);
		kryo.register(PlayerJoined.class);
		kryo.register(PlayerParted.class);
		kryo.register(JoinGame.class);
		kryo.register(Move.class);
		kryo.register(Exit.class);
		kryo.register(PMap.class);
		kryo.register(PTile.class);
		kryo.register(Play.class);
		kryo.register(Chunk.class);
		kryo.register(MapReceived.class);
		kryo.register(AdminCommand.class);
		kryo.register(ChangeDirection.class);
		kryo.register(SyncDirection.class);
		kryo.register(SendChat.class);
		kryo.register(MonsterData.class);
		kryo.register(MonsterData[].class);
		kryo.register(MapData.class);
		kryo.register(MonsterReceived.class);
		kryo.register(MapOptions.class);
		kryo.register(JoinMap.class);
		kryo.register(MonsterSync.class);
		kryo.register(ResetMap.class);
		kryo.register(DiscardMap.class);
		kryo.register(DoorSync.class);
		kryo.register(ChangeDoor.class);

	}

	// TCP Client to Server

	public static class DiscardMap {

	}

	public static class SendChat {
		public String s = "";
		public int channel = 0; // 0 = mapsay, 1 = broadcast, 2 = tell
		public String to = "";
		public Color col = Color.WHITE;

		public SendChat() {

		}

		public SendChat(String s, int channel, Color col) {
			this.s = s;
			this.channel = channel;
			this.col = col;
		}

		public SendChat(String s, int channel, String to, Color col) {
			this.s = s;
			this.channel = channel;
			this.to = to;
			this.col = col;
		}
	}

	public static class ChangeDirection {
		public int d = 0;
	}

	public static class AdminCommand {
		public int i = 0;
		public int j = 0;
		public int k = 0;
		public int l = 0;
	}

	public static class ClientAction {
		public int act;
	}

	public static class NewCharacter {
		public String name = "";
	}

	public static class Move {
		public int dir = 0;
		public boolean run = false;

		public Move() {

		}

		public Move(int dir, boolean run) {
			this.dir = dir;
			this.run = run;
		}
	}

	public static class Exit {
		public int dir = 0;
		public boolean run = false;

		public Exit() {

		}

		public Exit(int dir, boolean run) {
			this.dir = dir;
			this.run = run;
		}
	}

	public static class Play {
		public int[] mapVersions = new int[Shared.NUM_MAPS];
	}

	public static class MapReceived {
		public int m = 0;

		public MapReceived() {
		}

		public MapReceived(int m) {
			this.m = m;
		}
	}

	public static class ChangeDoor {
		public int id = 0;
		public int reqState = 0;
	}
	
	// TCP Server to Client

	public static class DoorSync {
		public int id = 0;
		public boolean open = false;
		public int state = 0;
		public long diff;

		public DoorSync() {
		}

		public DoorSync(int id, int state, boolean open, long diff) {
			this.id = id;
			this.state = state;
			this.open = open;
			this.diff = diff;
		}
	}

	public static class MonsterReceived {

	}

	public static class SyncDirection {
		public int d = 0;
		public int cid = 0;
	}

	public static class CharacterCreated {
		public String name = "";
	}

	public static class PlayerJoined {
		// someone else joined
		public int cid = 0;
		public String name = "";
		public int spriteSet = 0;
		public int sprite = 0;

		public PlayerJoined() {

		}

		public PlayerJoined(int cid, String name, int spriteSet, int sprite) {
			this.cid = cid;
			this.name = name;
			this.spriteSet = spriteSet;
			this.sprite = sprite;
		}
	}

	public static class PlayerParted {
		// someone else parted
		public int cid = 0;

		public PlayerParted() {

		}

		public PlayerParted(int cid) {
			this.cid = cid;
		}
	}

	public static class JoinMap {
		public List<PlayerSync> players = new ArrayList<PlayerSync>();
		public List<MonsterSync> monsters = new ArrayList<MonsterSync>();
		public List<DoorSync> doors = new ArrayList<DoorSync>();
		public int id = 0;
	}

	public static class JoinGame {
		public int map = 0;
		public int x = 0;
		public int y = 0;
		public int dir = 0;

		public List<PlayerData> players = new ArrayList<PlayerData>();
		public int cid = 0;
		public int sprite = 0;
		public int spriteSet = 0;
		public String name = "";
		public String greeting = "";

		public JoinGame() {
		}

		public JoinGame(int cid, int spriteSet, int sprite, String name, int map, int x, int y, int dir) {
			this.cid = cid;
			this.sprite = sprite;
			this.spriteSet = spriteSet;
			this.name = name;
			this.map = map;
			this.x = x;
			this.y = y;
			this.dir = dir;
		}
	}

	public static class PlayerData {
		public int cid = 0;
		public String name = "";
		public int sprite = 0;
		public int spriteSet = 0;

		public PlayerData() {
		}

		public PlayerData(int cid, String name, int spriteSet, int sprite) {
			this.cid = cid;
			this.name = name;
			this.sprite = sprite;
			this.spriteSet = spriteSet;
		}
	}

	public static class ResetMap {

	}

	public static class MonsterSync {
		public int id = 0;
		public int map = 0;
		public int x = 0;
		public int y = 0;
		public int moveTime = 0;
		public int diff = 0;
		public int dir = 0;
		public int type = 0;

		public MonsterSync() {

		}

		public MonsterSync(int id, int type, int map, int x, int y, int moveTime, int dir) {
			this.id = id;
			this.type = type;
			this.map = map;
			this.x = x;
			this.y = y;
			this.moveTime = moveTime;
			this.dir = dir;
		}
	}

	public static class PlayerSync {
		public int cid = 0;
		public int map = 0; // use 0 to denote no longer on map
		public int x = 0;
		public int y = 0;
		public int moveTime = 0;
		public int dir = 0;
		public boolean unlock = false;
		public int diff = 0;
		public boolean warp = false;

		public PlayerSync() {

		}

		public PlayerSync(int cid, int map, int x, int y, int moveTime, int dir, boolean warp) {
			this.cid = cid;
			this.map = map;
			this.x = x;
			this.y = y;
			this.moveTime = moveTime;
			this.dir = dir;
			this.warp = warp;
		}
	}

	public static class Chunk {
		public byte[] data = new byte[1];
		public int last = 0;
		public int m = 0;
		public int i = 0;

		public Chunk() {

		}

		public Chunk(byte[] data, int m, int last, int i) {
			this.data = data;
			this.m = m;
			this.i = i;
			this.last = last;
		}
	}

}
