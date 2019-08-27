package net.novaplay.jbproxy.client;

import java.util.*;

import lombok.Getter;
import lombok.Setter;
import net.novaplay.jbproxy.player.Player;

public class ProxyClient {
	
	private Map<String,Player> players = new HashMap<String,Player>();
	
	private String serverId;
	private String address;
	private String port;
	@Getter
	@Setter
	public boolean isOnline;
	
	public ProxyClient(String serverId, String address, String port) {
		this.serverId = serverId;
		this.address = address;
		this.port = port;
		this.setOnline(true);
	}
	
	public Map<String,Player> getOnlinePlayers(){
		return players;
	}
	

}
