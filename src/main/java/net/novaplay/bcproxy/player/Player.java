package net.novaplay.bcproxy.player;

import java.util.*;

import net.novaplay.bcproxy.client.ProxyClient;
import net.novaplay.bcproxy.command.CommandSender;
import net.novaplay.bcproxy.event.packet.PacketReceiveEvent;
import net.novaplay.bcproxy.server.Server;
import net.novaplay.library.netty.packet.Packet;
import net.novaplay.networking.player.ChatPacket;
import net.novaplay.networking.player.KickPacket;
import net.novaplay.networking.player.TransferPacket;

public class Player implements CommandSender{

	private String name;
	private UUID uuid;
	private Server server;
	private ProxyClient currentServer = null;
	
	public void setCurrentServer(ProxyClient c) { currentServer = c; }
	public ProxyClient getCurrentServer() { return currentServer; }
	
	public boolean isTransfering = false;
	public ProxyClient transferDestination = null;
	
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
		pk.player = this.name;
		pk.type = ChatPacket.CHAT;
		pk.message = message;
		pk.handled = true;
		getServer().getSessionManager().sendPacket(pk, getCurrentClient().getServerChannel());
	}
	
	public void sendTip(String message) {
		ChatPacket pk = new ChatPacket();
		pk.player = this.name;
		pk.type = ChatPacket.TIP;
		pk.message = message;
		pk.handled = true;
		getServer().getSessionManager().sendPacket(pk, getCurrentClient().getServerChannel());
	}
	
	public void sendPopup(String message) {
		ChatPacket pk = new ChatPacket();
		pk.player = this.name;
		pk.type = ChatPacket.POPUP;
		pk.message = message;
		pk.handled = true;
		getServer().getSessionManager().sendPacket(pk, getCurrentClient().getServerChannel());
	}
	
	public void sendTitle(String message) {
		ChatPacket pk = new ChatPacket();
		pk.player = this.name;
		pk.type = ChatPacket.TITLE;
		pk.message = message;
		pk.handled = true;
		getServer().getSessionManager().sendPacket(pk, getCurrentClient().getServerChannel());
	}
	
	public void transfer(ProxyClient client) {
		TransferPacket pk = new TransferPacket();
		pk.player = this.getName();
		pk.destination = client.getServerId();
		pk.handled = true;
		getServer().getSessionManager().sendPacket(pk,getCurrentClient().getServerChannel());
		transferDestination = client;
		isTransfering = true;
		
	}
	
	public void kick(String reason) {
		KickPacket pk = new KickPacket();
		pk.player = this.getName();
		pk.reason = reason;
		pk.handled = true;
		getServer().getSessionManager().sendPacket(pk,getCurrentClient().getServerChannel());
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
