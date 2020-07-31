package com.bg.ody.server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.bg.bearplane.engine.BearTool;
import com.bg.bearplane.engine.Log;
import com.bg.bearplane.engine.MySQL;
import com.bg.bearplane.net.BearRegistrar;
import com.bg.bearplane.net.packets.PingPacket;
import com.bg.ody.shared.Registrar;
import com.bg.ody.shared.Shared;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer {
	Server server;
	Game game;
	boolean running = true;
	long tick = System.currentTimeMillis();
	long secondTimer = 0;

	final int MAX_PACKETS_PER_MINUTE = 1200;

	AtomicInteger numConnections = new AtomicInteger(0);

	public HashMap<String, LinkedList<Long>> newAccountLog = new HashMap<String, LinkedList<Long>>();

	public ConcurrentHashMap<String, AtomicInteger> packetCount = new ConcurrentHashMap<String, AtomicInteger>();

	public ConcurrentLinkedQueue<Player> connections = new ConcurrentLinkedQueue<Player>();

	public GameServer() {
		game = new Game(this);
		startServer();
		createWindow();
		serverLoop();
	}

	private void startServer() {
		try {
			game.start();
			server = new Server(32768, 32768) {

				protected Connection newConnection() {
					return new Player(game);
				}
			};
			server.addListener(new Listener() {

				@Override
				public void received(Connection c, Object object) {
					Player p = (Player) c;
					int count = 0;
					if (!(object instanceof PingPacket)) {
						count = packetCount.get(p.ip).incrementAndGet();
					}
					if (count >= MAX_PACKETS_PER_MINUTE) {
						game.disconnect(p, "Flooding");
					} else {
						game.receiveData(p, object);
					}
				}

				@Override
				public void connected(Connection connection) {
					numConnections.incrementAndGet();
					Player p = (Player) connection;
					p.ip = p.getRemoteAddressTCP().getHostString();
					Log.info("Connection accepted from " + p.ip);
					if (numConnections.get() <= Shared.MAX_USERS) {
						for (Player pp : connections) {
							if (pp.ip.equals(p.ip)) {
								if (!Game.ALLOW_MULTI) {
									game.disconnect(p, "Duplicate IP detected");
								} else if (pp.account_id == 0) {
									game.disconnect(p, "Duplicate IP detected");
								}
							}
						}
					} else {
						game.disconnect(p, "Server is full.");
					}
					connections.add(p);
					if (newAccountLog.get(p.ip) == null) {
						newAccountLog.put(p.ip, new LinkedList<Long>());
					}
					if (packetCount.get(p.ip) == null) {
						packetCount.put(p.ip, new AtomicInteger(0));
					}
					long banned = isIPBanned(p.ip);
					String dateTime = new Date(banned).toString();
					if (banned > tick) {
						game.disconnect(p, "You are banned from Odyssey until " + dateTime);
					}
				}

				@Override
				public void disconnected(Connection connection) {
					numConnections.decrementAndGet();
					Player p = (Player) connection;
					game.disconnected(p);
					connections.remove(p);
				}

			});

			BearRegistrar.registerClasses(server);
			new Registrar().registerClasses(server);

			server.bind(Shared.TCP_PORT, Shared.UDP_PORT);
			server.start();
			Log.info("Server started at " + BearTool.getDate("MM-dd-yy HH:MM"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long isUserBanned(int uid) {
		long highest = 0;
		try {
			String statement = "SELECT until_tick FROM bans WHERE uid=?";
			LinkedList<Object> obj = new LinkedList<Object>();
			obj.add(uid);
			ResultSet rs = MySQL.get(statement, obj);
			long bubble = 0;
			while (rs.next()) {
				bubble = rs.getLong(1);
				if (bubble > highest) {
					highest = bubble;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return highest;
	}

	private long isIPBanned(String ip) {
		long highest = 0;
		try {
			String statement = "SELECT until_tick FROM bans WHERE ip=?";
			LinkedList<Object> obj = new LinkedList<Object>();
			obj.add(ip);
			ResultSet rs = MySQL.get(statement, obj);
			long bubble = 0;
			while (rs.next()) {
				bubble = rs.getLong(1);
				if (bubble > highest) {
					highest = bubble;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return highest;
	}

	private void createWindow() {
		// Open a window to provide an easy way to stop the server.
		JFrame frame = new JFrame("Server");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent evt) {
				server.stop();
				running = false;
			}
		});
		frame.getContentPane().add(new JLabel("Be happy instead of sad."));
		frame.setSize(320, 200);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void serverLoop() {

		while (running) {
			tick = System.currentTimeMillis();
			game.update();
			for (String s : newAccountLog.keySet()) {
				LinkedList<Long> log = newAccountLog.get(s);
				Iterator<Long> itr = log.iterator();
				while (itr.hasNext()) {
					long n = itr.next();
					if (n + 120000 < tick) {
						itr.remove();
					}
				}
			}
			if (tick > secondTimer) {
				secondTimer = 1000 + tick;
				for (String s : packetCount.keySet()) {
					int count = packetCount.get(s).get();
					count--;
					if (count < 0) {
						count = 0;
					}
					packetCount.get(s).set(count);
				}
			}
			try {
				int timeStep = 16;
				long t = timeStep - (System.currentTimeMillis() - tick);
				if (t > timeStep) {
					t = timeStep;
				}
				if (t < 1) {
					t = 1;
				}
				Thread.sleep(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

}
