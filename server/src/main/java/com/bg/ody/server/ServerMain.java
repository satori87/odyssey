package com.bg.ody.server;

import com.bg.bearplane.engine.Log;

public class ServerMain {

	public static void main(String[] args) {
		try {
			Log.init(args);
			new GameServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
