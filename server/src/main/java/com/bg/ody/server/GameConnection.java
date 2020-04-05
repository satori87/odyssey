package com.bg.ody.server;

import com.bg.bearplane.net.packets.SimpleServerPacket;
import com.esotericsoftware.kryonet.Connection;

public class GameConnection extends Connection {

	public Game game;
	public String user = "";
	public int account_id = 0;
	public int uid = 0;
	public int access = 0;
	public String ip = "";
	public String name = "";

	public void sendSimple(int a) {
		SimpleServerPacket p = new SimpleServerPacket();
		p.a = (byte) a;
		sendTCP(p);
	}

	public String id() {
		if (this instanceof Player) {
			return "ip: " + ip + " name: " + name + " uid: " + account_id + " id: " + uid + " access: " + access;
		} else {
			return "name: " + name + " id: " + uid;
		}
	}

}
