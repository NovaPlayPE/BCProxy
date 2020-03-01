package net.novaplay.bcproxy.event.player;

import net.novaplay.bcproxy.client.ProxyClient;
import net.novaplay.bcproxy.event.Cancellable;
import net.novaplay.bcproxy.event.Event;
import net.novaplay.bcproxy.event.HandlerList;
import net.novaplay.bcproxy.player.Player;

public class PlayerLoginEvent extends Event implements Cancellable {
	
	private static HandlerList handlers = new HandlerList();
	private Player player;
	private ProxyClient client;
	
	public static HandlerList getHandlers() {
		return handlers;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public ProxyClient getClient() {
		return this.client;
	}
	
	public PlayerLoginEvent(Player player, ProxyClient client) {
		this.player = player;
		this.client = client;
	}

}
