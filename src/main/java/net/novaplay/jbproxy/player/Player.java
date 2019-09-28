package net.novaplay.jbproxy.player;

import java.util.*;

import net.novaplay.jbproxy.client.ProxyClient;
import net.novaplay.jbproxy.command.CommandSender;
import net.novaplay.jbproxy.event.packet.PacketReceiveEvent;
import net.novaplay.jbproxy.server.Server;
import net.novaplay.library.netty.packet.Packet;
import net.novaplay.networking.player.ChatPacket;

public class Player implements CommandSender{

	private String name;
	private UUID uuid;
	private Server server;
	private ProxyClient currentServer;
	private boolean isOp = false;
	
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
	
	public void sendMessage(String message) {
		ChatPacket pk = new ChatPacket();
		pk.player = this.name.toLowerCase();
		pk.type = "chat";
		pk.message = message;
		getServer().getSessionManager().sendPacket(pk, getCurrentClient().getServerChannel());
	}
	
	public void sendTitle(String title) {}
	public void sendTitle(String title, String subtitle) {}
	
	
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

	@Override
	public void setOp(boolean value) {this.isOp = value;}

	@Override
	public boolean isOp() {return this.isOp;}
	
}
