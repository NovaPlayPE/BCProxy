package net.novaplay.jbproxy.client;

import java.util.*;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.novaplay.jbproxy.player.Player;
import net.novaplay.networking.types.ConnectType;

public class ProxyClient {
	
	private Map<String,Player> players = new HashMap<String,Player>();
	
	
	private String serverId;
	private String address;
	private int port;
	private ConnectType serverType = ConnectType.JAVA;
	@Getter
	@Setter
	public Channel serverChannel = null;
	@Getter
	@Setter
	public boolean isOnline = false;
	
	public ProxyClient(String serverId, String address, int port, String type) {
		this.serverId = serverId;
		this.address = address;
		this.port = port;
		if(type.equals("bedrock")) {
			serverType = ConnectType.BEDROCK;
		}
	}
	
	public ConnectType getServerType() {
		return this.serverType;
	}
	
	public void addPlayer(Player player) {
		if(!players.containsKey(player.getName())) {
			players.put(player.getName(),player);
		}
	}
	
	public void removePlayer(String name) {
		if(players.containsKey(name)) {
			players.remove(name);
		}
	}
	
	public Map<String,Player> getOnlinePlayers(){
		return players;
	}
	
	public int getPlayerCount() {
		return players.size();
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public String getServerId() {
		return this.serverId;
	}
	
	public int getPort() {
		return this.port;
	}
	
}
