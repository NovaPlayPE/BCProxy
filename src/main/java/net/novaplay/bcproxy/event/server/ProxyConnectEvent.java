package net.novaplay.bcproxy.event.server;

import net.novaplay.bcproxy.event.Cancellable;
import net.novaplay.bcproxy.event.Event;
import net.novaplay.bcproxy.event.HandlerList;

public class ProxyConnectEvent extends Event implements Cancellable{
	
	
	
	private String address;
	private int port;
	private String serverId;
	private String password;
	
	private static HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlers() {
		return handlers;
	}
	
	public ProxyConnectEvent(String address, int port, String serverId, String password) {
		this.address = address;
		this.port = port;
		this.serverId = serverId;
		this.password = password;
	}
	
	public String getAddress() { return this.address; }
	public int getPort() { return this.port; }
	public String getServerId() { return this.serverId; }
	public String getPassword() { return this.password; }
	
}
