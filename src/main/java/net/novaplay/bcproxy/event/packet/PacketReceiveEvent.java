package net.novaplay.bcproxy.event.packet;

import net.novaplay.bcproxy.event.Cancellable;
import net.novaplay.bcproxy.event.HandlerList;
import net.novaplay.bcproxy.server.Server;
import net.novaplay.library.netty.packet.Packet;

public class PacketReceiveEvent extends PacketEvent{

	private static HandlerList handlers = new HandlerList();
	
	private Server server;
	private Packet packet;
	
	public PacketReceiveEvent(Server server, Packet packet) {
		this.server = server;
		this.packet = packet;
	}
	
	public Server getServer() {
		return this.server;
	}
	
	public Packet getPacket() {
		return this.packet;
	}
	
	public static HandlerList getHandlers() {
		return handlers;
	}
	
}
