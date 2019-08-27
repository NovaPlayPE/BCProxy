package net.novaplay.jbproxy.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import net.novaplay.jbproxy.client.ProxyClient;
import net.novaplay.jbproxy.player.Player;
import net.novaplay.jbproxy.scheduler.ServerScheduler;
import net.novaplay.jbproxy.utils.Logger;

public class Server {

	private static Server instance = null;
	@Getter
	public Logger logger = null;
    @Getter
	public String filePath;
    @Getter
    public String dataPath;
    @Getter
    public String pluginPath;
    @Getter
    public ServerScheduler scheduler = null;
    
	private Map<String,Player> players = new HashMap<String,Player>();
	private Map<String, ProxyClient> clients = new HashMap<String,ProxyClient>();
    
    public static Server getInstance() {
    	return instance;
    }
    
	public Server(Logger logger, String filePath, String dataPath, String pluginPath) {
		instance = this;
		this.logger = logger;
		this.filePath = filePath;
        if (!new File(pluginPath).exists()) {
            new File(pluginPath).mkdirs();
        }
        this.dataPath = new File(dataPath).getAbsolutePath() + "/";
        this.pluginPath = new File(pluginPath).getAbsolutePath() + "/";
        scheduler = new ServerScheduler();
	}
	
	public ProxyClient registerNewClient(String serverId, String port, String address) {
		ProxyClient client = new ProxyClient(serverId, address, port);
		return client;
	}
	
	public Map<String,Player> getOnlinePlayers(){
		return players;
	}
	
	public Map<String,ProxyClient> getOnlineClients(){
		return clients;
	}
	
}
