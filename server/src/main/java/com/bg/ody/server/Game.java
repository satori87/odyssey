package com.bg.ody.server;

import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.bg.bearplane.engine.Log;
import com.bg.bearplane.engine.MySQL;
import com.bg.bearplane.net.packets.Authenticate;
import com.bg.bearplane.net.packets.DisconnectError;
import com.bg.bearplane.net.packets.Logon;
import com.bg.bearplane.net.packets.PingPacket;
import com.bg.bearplane.net.packets.SimpleClientPacket;
import com.bg.bearplane.net.packets.SimpleServerPacket;
import com.bg.ody.shared.Shared;
import com.esotericsoftware.kryo.util.IntMap;
import com.bg.ody.shared.Registrar.CharacterCreated;
import com.bg.ody.shared.Registrar.MapReceived;
import com.bg.ody.shared.Registrar.NewCharacter;
import com.bg.ody.shared.Registrar.Play;

public class Game {

	public static Game game;

	GameServer gameServer;

	public static Realm world;

	ConcurrentLinkedQueue<QueuedServerPacket> packetQueue = new ConcurrentLinkedQueue<QueuedServerPacket>();

	public static IntMap<Player> players = new IntMap<Player>();

	public static long tick = 0;

	long sqlStamp = 0;

	public Game(GameServer server) {
		this.gameServer = server;
		game = this;
	}

	public void start() {
		MySQL.connectSQL(Shared.MYSQL_ADDRESS, Shared.MYSQL_PORT, Shared.MYSQL_DB, Shared.MYSQL_USER,
				Shared.MYSQL_PASS);
		Log.info("Connected to mySQL.");
		world = new Realm(this);
		Realm.load();
		Log.info("Server ready");
	}

	public void update() {
		tick = System.currentTimeMillis();
		processPacketQueue();
		if (MySQL.connected) {
			if (tick > sqlStamp) {
				sqlStamp = tick + 60000;
				MySQL.get("SHOW COLUMNS FROM BANS");
			}
			world.update(tick);
		} else {
			Log.error("SQL Connection failed. Retrying...");
			MySQL.connectSQL(MySQL.saddress, MySQL.sport, MySQL.sdb, MySQL.suser, MySQL.spass);
		}

	}

	void processPacketQueue() {
		// now we can process the packets in the main thread, and our safety is done
		QueuedServerPacket p;
		while (!packetQueue.isEmpty()) {
			p = packetQueue.poll();
			processPacket((Player) p.c, p.o);
		}
	}

	public void disconnected(Player c) {
		Log.info(c.id() + " disconnected");
		if (c.playing()) {
			c.part();
		}
		if (c.uid > 0) {
			players.remove(c.uid);
		}
	}

	public void receiveData(Player c, Object data) {
		// this method is called from many threads, so we will queue the packet in a
		// thread-safe concurrentlinkedqueue
		packetQueue.add(new QueuedServerPacket(c, data));
	}

	public int findPlayer(String name) {
		int cid = 0;
		int uid = 0;
		try {
			String statement = "SELECT uid,cid FROM characters WHERE name=?";
			LinkedList<Object> obj = new LinkedList<Object>();
			obj.add(name);
			ResultSet rs = MySQL.get(statement, obj);
			if (rs != null) {
				while (rs.next()) {
					uid = rs.getInt(1);
					if (uid > 0) {
						cid = rs.getInt(2);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cid;
	}

	private void newCharacter(Player c, Object data) {
		try {
			if (!c.playing() && c.account_id > 0) {
				NewCharacter nc = (NewCharacter) data;
				if (Shared.validName(nc.name)) {
					// disable existing character, but dont delete it
					String statement = "UPDATE characters SET uid=0 WHERE uid=?";
					LinkedList<Object> obj = new LinkedList<Object>();
					obj.add(c.account_id);
					MySQL.save(statement, obj);
					if (findPlayer(nc.name) == 0) {
						statement = "INSERT INTO characters (uid,name) VALUES (?,?)";
						// String content = objectMapper.writeValueAsString(fields);
						// fields = objectMapper.readValue(content, fields.getClass());
						obj.add(nc.name);
						MySQL.save(statement, obj);
						obj.clear();
						obj.add(c.account_id);
						statement = "SELECT cid FROM characters WHERE uid=?";
						ResultSet rs = MySQL.get(statement, obj);
						while (rs.next()) {
							c.uid = rs.getInt(1);
						}
						c.name = nc.name;
						CharacterCreated cc = new CharacterCreated();
						cc.name = nc.name;
						c.sendTCP(cc);
					} else {
						sendError(c, "That username is in use. Please try again, or be more creative.");
					}
				} else {
					sendError(c, "Name must be between 3 and 16 alphanumeric characters.");
				}
			} else {
				disconnect(c, "Disconnected");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void play(Player c, Object data) {
		Play pp = (Play) data;
		if (!c.playing() && c.account_id > 0 && c.uid > 0) {
			// i guess then this is a valid attempt to play, yeah?
			Player p = (Player) c;
			players.put(p.uid, p);
			p.updatePlayerAssets(pp.mapVersions);
		} else {
			disconnect(c, "Disconnected");
		}
	}

	private void processPacket(Player p, Object data) {
		if (!p.playing()) {
			if (data instanceof Authenticate) {
				logon(p, data);
			} else if (data instanceof PingPacket) {
				PingPacket pp = (PingPacket) data;
				p.sendTCP(pp);
			} else if (data instanceof NewCharacter) {
				gameServer.newAccountLog.get(p.ip).add(tick);
				if (gameServer.newAccountLog.get(p.ip).size() > 5) {
					disconnect(p, "Too many new accounts or characters in a short time.");
					// maybe replace this with a short ban
				} else {
					newCharacter(p, data);
				}
			} else if (data instanceof SimpleClientPacket) {
				SimpleClientPacket s = (SimpleClientPacket) data;
				switch (s.a) {
				case 1:

					break;
				}
			} else if (data instanceof Play) {
				play(p, data);
			} else if (data instanceof MapReceived) {
				// ChunkReceived cr = (ChunkReceived) data;
				p.sendNextMap();
			} else {
				// disconnect(p, "Hacker");
			}
		} else {
			p.processPacket(data);
		}
	}

	private void loadCharacter(Player c, int uid) {
		try {
			Log.info(c.id() + " logged in");
			String statement = "SELECT cid,name,x,y,map FROM characters WHERE uid=?";
			LinkedList<Object> obj = new LinkedList<Object>();
			obj.add(uid);
			ResultSet rs = MySQL.get(statement, obj);
			while (rs.next()) {
				c.uid = rs.getInt(1);
				c.name = rs.getString(2);
				c.x = rs.getInt(3);
				c.y = rs.getInt(4);
				c.map = rs.getInt(5);

			}
			c.account_id = uid;
			Logon log = new Logon();
			log.name = c.name;
			log.cid = c.uid;
			c.sendTCP(log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect(Player c, String msg) {
		Log.warn(c.id() + " disconnecting for " + msg);
		sendError(c, msg);
		c.close();
	}

	public void sendAll(Object o) {
		for (Player p : players.values()) {
			if (p.playing()) {
				p.sendTCP(o);
			}
		}
	}

	public void sendAllBut(Player but, Object o) {
		for (Player p : players.values()) {
			if (p.playing() && p != but) {
				p.sendTCP(o);
			}
		}
	}

	public void sendError(Player c, String msg) {
		DisconnectError error = new DisconnectError();
		error.msg = msg;
		c.sendTCP(error);
	}

	private boolean userExists(String user) {
		try {
			LinkedList<Object> obj = new LinkedList<Object>();
			String statement = "SELECT uid FROM accounts WHERE user=?";
			obj.add(user);
			ResultSet rs = MySQL.get(statement, obj);
			if (rs != null) {
				while (rs.next()) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void logon(Player c, Object data) {
		if (c.playing() || c.account_id > 0 || c.uid > 0) {
			disconnect(c, "Disconnected");
			return;
		}
		int uid = 0;
		String pass = "";
		int access = 0;
		boolean found = false;
		ResultSet rs;
		String statement = "";
		LinkedList<Object> obj = new LinkedList<Object>();
		try {
			Authenticate na = (Authenticate) data;
			if (!Shared.validName(na.user)) {
				disconnect(c, "Invalid username.");
				return;
			}
			if (!na.version.equals(Shared.CLIENT_VERSION)) {
				disconnect(c,
						"Your client is out of date! Quit the game and run the Odyssey Launcher. If problems persists, redownload from "
								+ Shared.PUBLIC_WEBSITE);
				return;
			}
			if (na.newAcct) {
				gameServer.newAccountLog.get(c.ip).add(tick);
				if (gameServer.newAccountLog.get(c.ip).size() > 5) {
					disconnect(c, "Too many new accounts or characters in a short time.");
					return;
					// maybe replace this with a short ban
				}
				if (userExists(na.user)) {
					// send error and disconnect
					disconnect(c, "That username is already in use.");
				} else {
					// create the account
					statement = "INSERT INTO accounts (user,pass,access) VALUES (?,?,?)";
					obj.add(na.user);
					obj.add(na.pass);
					obj.add(0);
					MySQL.save(statement, obj);
					statement = "SELECT uid FROM accounts WHERE user=?";
					obj.clear();
					obj.add(na.user);
					rs = MySQL.get(statement, obj);
					found = false;
					while (rs.next()) {
						found = true;
						uid = rs.getInt(1);
					}
					loadCharacter(c, uid);
				}
			} else {
				statement = "SELECT uid,pass,access FROM accounts WHERE user=?";
				obj.add(na.user);
				rs = MySQL.get(statement, obj);
				while (rs.next()) {
					found = true;
					uid = rs.getInt(1);
					pass = rs.getString(2);
					access = rs.getInt(3);
					c.access = access;
				}
				if (found && pass.equals(na.pass)) {
					found = false;
					for (Player p : gameServer.connections) {
						if (p.account_id == uid) {
							found = true;
						}
					}
					if (!found) {
						// successful authentication, now check for bans
						long banned = gameServer.isUserBanned(uid);
						String dateTime = new Date(banned).toString();
						if (banned > tick) {
							// consider adding this IP to the ban table, but thats kind of intense maybe?
							disconnect(c, "You are banned from Odyssey until " + dateTime);
							return;
						} else {
							loadCharacter(c, uid);
						}
					} else {
						disconnect(c, "That user is already in use!");
						return;
					}
				} else {
					// incorrect user/pass!
					disconnect(c, "Invalid username/password");
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendSimple(Player c, int a) {
		SimpleServerPacket p = new SimpleServerPacket();
		p.a = (byte) a;
		c.sendTCP(p);
	}

}
