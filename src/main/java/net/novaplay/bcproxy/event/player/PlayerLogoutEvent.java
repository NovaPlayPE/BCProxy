package net.novaplay.bcproxy.event.player;

import net.novaplay.bcproxy.client.ProxyClient;
import net.novaplay.bcproxy.event.Cancellable;
import net.novaplay.bcproxy.event.Event;
import net.novaplay.bcproxy.event.HandlerList;
import net.novaplay.bcproxy.player.Player;

public class PlayerLogoutEvent extends Event{
	
	private static HandlerList handlers = new HandlerList();
	private Player player;
	private String reason;
	
	public static HandlerList getHandlers() {
		return handlers;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public String getReason() {
		return this.reason;
	}
	
	public PlayerLogoutEvent(Player player, String reason) {
		this.player = player;
		this.reason = reason;
	}
	
}
