package net.novaplay.bcproxy.event.packet;

import net.novaplay.bcproxy.event.Cancellable;
import net.novaplay.bcproxy.event.HandlerList;
import net.novaplay.bcproxy.player.Player;
import net.novaplay.library.netty.packet.Packet;

public class PacketReceiveEvent extends PacketEvent implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	
	private Player player;
	private Packet packet;
	
	public PacketReceiveEvent(Player player, Packet packet) {
		this.player = player;
		this.packet = packet;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Packet getPacket() {
		return this.packet;
	}
	
	public static HandlerList getHandlers() {
		return handlers;
	}
	
}
