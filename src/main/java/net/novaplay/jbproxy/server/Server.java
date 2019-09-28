package net.novaplay.jbproxy.server;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.novaplay.jbproxy.client.ProxyClient;
import net.novaplay.jbproxy.command.Command;
import net.novaplay.jbproxy.command.CommandArgument;
import net.novaplay.jbproxy.command.CommandExecution;
import net.novaplay.jbproxy.command.CommandLine;
import net.novaplay.jbproxy.command.CommandSender;
import net.novaplay.jbproxy.config.Config;
import net.novaplay.jbproxy.config.ConfigSection;
import net.novaplay.jbproxy.player.Player;
import net.novaplay.jbproxy.plugin.Plugin;
import net.novaplay.jbproxy.plugin.PluginManager;
import net.novaplay.jbproxy.plugin.SimplePluginManager;
import net.novaplay.jbproxy.plugin.java.JavaPluginLoader;
import net.novaplay.networking.IPlayerPacket;
import net.novaplay.networking.server.ProxyConnectPacket;
import net.novaplay.jbproxy.scheduler.ServerScheduler;
import net.novaplay.jbproxy.session.SessionManager;
import net.novaplay.jbproxy.utils.Color;
import net.novaplay.jbproxy.utils.Logger;
import net.novaplay.library.netty.packet.Packet;

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
    private SessionManager sessionManager = null;
    private PluginManager pluginManager = null;
    
    private Config properties;
    private Config clientConfig;
    
	private Map<String,Player> players = new HashMap<String,Player>();
	private Map<String, ProxyClient> clients = new HashMap<String,ProxyClient>();
    
	public String getApiVersion() {
		return "1.0.0";
	}
	
	public String getVersion() {
		return "1.0";
	}
	
    @Getter
    @Setter
    private int playersPerThread;

    private final ThreadPoolExecutor playerTicker = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(),
            1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("Player Ticker - #%d").setDaemon(true).build());

	
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
        this.logger.info(Color.GREEN + "Loading " + Color.WHITE + "server.properties");
        this.properties = new Config(this.dataPath + "server.properties", Config.PROPERTIES, new ConfigSection() {
            {
                put("proxy-ip", "0.0.0.0");
                put("proxy-port", 9855);
                put("password", "ExamplePassword123");
                put("async-workers", "auto");
                put("enable-profiling", false);
                put("profile-report-trigger", 20);
                put("max-players", 100);
                //put("rcon.password", Base64.getEncoder().encodeToString(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(3, 13));
                put("debug", 1);
                put("display-stats-in-title", true);
            }
        });
        this.clientConfig = new Config(this.dataPath + "clients.yml", Config.YAML, new ConfigSection() {
        	{
        		put("server.Server1.address", "0.0.0.0");
        		put("server.Server1.port", 19132);
        		put("server.Server1.type", "bedrock");
        		put("server.Server2.address", "0.0.0.0");
            	put("server.Server2.port", 25565);
            	put("server.Server2.type", "java");
        	}
        });
        Object poolSize = this.getConfig("async-workers", "auto");
        if (!(poolSize instanceof Integer)) {
            try {
                poolSize = Integer.valueOf((String) poolSize);
            } catch (Exception e) {
                poolSize = Math.max(Runtime.getRuntime().availableProcessors() + 1, 4);
            }
        }
        ServerScheduler.WORKERS = (int) poolSize;
        scheduler = new ServerScheduler();
        
        this.logger.info(Color.GREEN + "Loading all plugins");
        pluginManager = new SimplePluginManager(this);
        pluginManager.registerInterface(JavaPluginLoader.class);
        pluginManager.loadPlugins(this.pluginPath);
        enablePlugins();
        
        this.logger.info(Color.GREEN + "Creating netty server");
        sessionManager = new SessionManager(this, getPort());
        sessionManager.start();
	}
	
	public PluginManager getPluginManager() {
		return this.pluginManager;
	}
	
	public void enablePlugins() {
		for(Plugin plugin : getPluginManager().getPlugins().values()) {
			if(!plugin.isEnabled()) {
				getPluginManager().enablePlugin(plugin);
			}
		}
	}
	
	public SessionManager getSessionManager() {
		return sessionManager;
	}
	
	public int getPort() {
		return this.getPropertyInt("proxy-port",9855);
	}
	
	public void handleProxyPackets(Packet packet, Channel channel) {
		if(packet instanceof ProxyConnectPacket) {
			if(getSessionManager().getVerifiedChannels().contains(channel)) {
				return;
			}
			ProxyConnectPacket pk = (ProxyConnectPacket)packet;
			if(pk.address.equalsIgnoreCase("0.0.0.0")) {
				pk.address = "localhost";
			}
			ProxyClient client = getClientByName(pk.serverId);
			if(client == null) {
				pk.success = false;
				getSessionManager().sendPacket(pk,channel);
				return;
			}
			try {
				InetAddress add1 = InetAddress.getByName(pk.address);
				InetAddress add2 = InetAddress.getByName(client.getAddress());
				if(add1 != add2) {
					pk.success = false;
					getSessionManager().sendPacket(pk,channel);
					return;
				}
			} catch(UnknownHostException e) {
				return;
			}
			if(client.getPort() != pk.port) {
				pk.success = false;
				getSessionManager().sendPacket(pk,channel);
				return;
			}
			if(!client.isOnline()) {
				client.setOnline(true);
			}
			getSessionManager().getVerifiedChannels().add(channel);
			client.setServerChannel(channel);
			this.logger.info(Color.GREEN + "Client " + pk.serverId + " [" + pk.address + ":" + pk.port + "] connected");
			//ProxyClient client = 
			getSessionManager().sendPacket(pk,channel);
			
		} if(getSessionManager().getVerifiedChannels().contains(channel)) {
			return;
		} 
		
		if(packet instanceof IPlayerPacket) {
			players.forEach((id,pla) -> {pla.handleDataPacket(packet);});
		}
	}
	
	public void refreshClients() {
		this.clients = new HashMap<>();
		this.players = new HashMap<>();
		ConfigSection section = this.clientConfig.getSection("servers");
		for(Map.Entry map : section.getAll().entrySet()) {
			String serverId = (String) map.getKey();
			ConfigSection info = (ConfigSection) map.getValue();
			String address = info.getString("address");
			int port = info.getInt("port");
			String type = info.getString("type");
			registerNewClient(serverId,port,address,type);
		}
	}
	
	public ProxyClient registerNewClient(String serverId, int port, String address) {
		return registerNewClient(serverId, port, address, "java");
	}
	
	public ProxyClient registerNewClient(String serverId, int port, String address, String type) {
		if(type.equals("java") || type.equals("bedrock")) {
			ProxyClient client = new ProxyClient(serverId, address, port,type);
			clients.put(serverId.toLowerCase(),client);
			return client;
		}
		return null;
	}
	
	public Map<String,Player> getOnlinePlayers(){
		return players;
	}
	
	public Map<String,ProxyClient> getOnlineClients(){
		Map<String,ProxyClient> clients = new HashMap<String,ProxyClient>();
		for(Map.Entry<String,ProxyClient> connected : getClients().entrySet()) {
			if(connected.getValue().isOnline()) {
				clients.put(connected.getKey(),connected.getValue());
			}
		}
		return clients;
	}
	
	public Map<String,ProxyClient> getClients(){
		return clients;
	}
	
	
	/**
	 *This is an example. In future it will be changed, because I still fighting with command api...
	 */
	
	/*
	public void createCommands() {
		Command command = new Command("pl");
		
		Command help = new Command("help",true);
		CommandLine line1 = (CommandLine) help.createNewCommandLine();
		CommandArgument arg = new CommandArgument("1");
		line1.registerNewArguments(arg);
		CommandLine line2 = (CommandLine) help.createNewCommandLine();
		CommandArgument arg2 = new CommandArgument("2");
		line2.registerNewArguments(arg);
		help.setExecution(new CommandExecution() {
			public boolean execute(CommandSender sender, CommandLine line) {
				if(sender instanceof Player) {
					Player player = (Player) sender;
					
				}
				return false;
			}
		});
	}
	
	*/
	
	public ProxyClient getOnlineClientByName(String name) {
		if(getOnlineClients().containsKey(name.toLowerCase())) {
			return getOnlineClients().get(name.toLowerCase());
		}
		return null;
	}
	
	public ProxyClient getClientByName(String name) {
		if(clients.containsKey(name.toLowerCase())) {
			return clients.get(name.toLowerCase());
		}
		return null;
	}
	
	public Object getConfig(String variable) {
        return this.getConfig(variable, null);
    }

    public Object getConfig(String variable, Object defaultValue) {
        Object value = this.properties.get(variable);
        return value == null ? defaultValue : value;
    }
    
    public Object getProperty(String variable) {
        return this.getProperty(variable, null);
    }

    public Object getProperty(String variable, Object defaultValue) {
        return this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
    }

    public void setPropertyString(String variable, String value) {
        this.properties.set(variable, value);
        this.properties.save();
    }

    public String getPropertyString(String variable) {
        return this.getPropertyString(variable, null);
    }

    public String getPropertyString(String variable, String defaultValue) {
        return this.properties.exists(variable) ? (String) this.properties.get(variable) : defaultValue;
    }

    public int getPropertyInt(String variable) {
        return this.getPropertyInt(variable, null);
    }

    public int getPropertyInt(String variable, Integer defaultValue) {
        return this.properties.exists(variable) ? (!this.properties.get(variable).equals("") ? Integer.parseInt(String.valueOf(this.properties.get(variable))) : defaultValue) : defaultValue;
    }

    public void setPropertyInt(String variable, int value) {
        this.properties.set(variable, value);
        this.properties.save();
    }

    public boolean getPropertyBoolean(String variable) {
        return this.getPropertyBoolean(variable, null);
    }

    public boolean getPropertyBoolean(String variable, Object defaultValue) {
        Object value = this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        switch (String.valueOf(value)) {
            case "on":
            case "true":
            case "1":
            case "yes":
                return true;
        }
        return false;
    }

    public void setPropertyBoolean(String variable, boolean value) {
        this.properties.set(variable, value ? "1" : "0");
        this.properties.save();
    }
	
}
