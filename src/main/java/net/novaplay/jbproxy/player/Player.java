package net.novaplay.jbproxy.player;

import java.util.*;

import net.novaplay.jbproxy.client.ProxyClient;
import net.novaplay.jbproxy.event.packet.PacketReceiveEvent;
import net.novaplay.jbproxy.server.Server;
import net.novaplay.netty.packet.Packet;

public class Player {

	private String name;
	private UUID uuid;
	private Server server;
	private ProxyClient currentServer;
	
	public Player(String playerName, UUID uuid, Server server) {
		this.name = playerName;
		this.uuid = uuid;
		this.server = server;
	}
	
	public Server getServer() {
		return this.server;
	}
	
	public void setCurrentClient(ProxyClient client) {
		this.currentServer = client;
	}
	
	public ProxyClient getCurrentClient() {
		return currentServer != null ? currentServer : null; // wery crap code, but what i can do? 
	}
	
	public void sendMessage(String message) {}
	
	public void sendTitle(String title) {}
	public void sendTitle(String title, String subtitle) {}
	
	public void transfer() {};
	
	public void handleDataPacket(Packet packet) {
		PacketReceiveEvent e = new PacketReceiveEvent(this,packet);
		this.server.getPluginManager().callEvent(e);
		if(e.isCancelled()) {
			return;
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
}
