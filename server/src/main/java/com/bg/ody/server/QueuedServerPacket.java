package com.bg.ody.server;

public class QueuedServerPacket {
	public Object o;
	public GameConnection c;

	public QueuedServerPacket(GameConnection c, Object o) {
		this.c = c;
		this.o = o;
	}
}
