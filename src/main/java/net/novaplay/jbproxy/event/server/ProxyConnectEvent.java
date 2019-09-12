package net.novaplay.jbproxy.event.server;

import net.novaplay.jbproxy.event.Event;
import net.novaplay.jbproxy.event.Cancellable;
import net.novaplay.jbproxy.event.HandlerList;
import net.novaplay.networking.types.ConnectType;

public class ProxyConnectEvent extends Event implements Cancellable{
	
	private static HandlerList handlers = new HandlerList();
	
	private String address;
	private int port;
	private String serverId;
	private String password;
	private ConnectType type;
	
	public static HandlerList getHandlers() {
		return handlers;
	}
	
	public ProxyConnectEvent(String address, int port, String serverId, String password, ConnectType type) {
		this.address = address;
		this.port = port;
		this.serverId = serverId;
		this.password = password;
		this.type = type;
	}
	
	public String getAddress() { return this.address; }
	public int getPort() { return this.port; }
	public String getServerId() { return this.serverId; }
	public String getPassword() { return this.password; }
	public ConnectType getConnectionType() { return this.type; }
	
}
