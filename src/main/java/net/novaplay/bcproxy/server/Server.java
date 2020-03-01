package net.novaplay.bcproxy.server;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.novaplay.networking.IPlayerPacket;
import net.novaplay.networking.IServerPacket;
import net.novaplay.networking.player.ChatPacket;
import net.novaplay.networking.player.KickPacket;
import net.novaplay.networking.player.LoginPacket;
import net.novaplay.networking.player.LogoutPacket;
import net.novaplay.networking.player.TransferPacket;
import net.novaplay.networking.server.PlayerInfoPacket;
import net.novaplay.networking.server.ProxyConnectPacket;
import net.novaplay.networking.server.ServerInfoPacket;
import net.novaplay.networking.server.ServerListSyncPacket;
import net.novaplay.bcproxy.BCProxy;
import net.novaplay.bcproxy.client.ProxyClient;
import net.novaplay.bcproxy.command.CommandMap;
import net.novaplay.bcproxy.command.CommandReader;
import net.novaplay.bcproxy.command.CommandSender;
import net.novaplay.bcproxy.command.ConsoleCommandSender;
import net.novaplay.bcproxy.command.defaults.ClientsCommand;
import net.novaplay.bcproxy.command.defaults.HelpCommand;
import net.novaplay.bcproxy.command.defaults.ListCommand;
import net.novaplay.bcproxy.command.defaults.PluginsCommand;
import net.novaplay.bcproxy.command.defaults.StopCommand;
import net.novaplay.bcproxy.config.Config;
import net.novaplay.bcproxy.config.ConfigSection;
import net.novaplay.bcproxy.event.HandlerList;
import net.novaplay.bcproxy.event.packet.PacketReceiveEvent;
import net.novaplay.bcproxy.event.player.PlayerLoginEvent;
import net.novaplay.bcproxy.event.player.PlayerLogoutEvent;
import net.novaplay.bcproxy.player.Player;
import net.novaplay.bcproxy.plugin.Plugin;
import net.novaplay.bcproxy.plugin.PluginManager;
import net.novaplay.bcproxy.plugin.SimplePluginManager;
import net.novaplay.bcproxy.plugin.java.JavaPluginLoader;
import net.novaplay.bcproxy.scheduler.ServerScheduler;
import net.novaplay.bcproxy.session.SessionManager;
import net.novaplay.bcproxy.utils.Color;
import net.novaplay.bcproxy.utils.Logger;
import net.novaplay.library.netty.packet.Packet;

public class Server {

	private static Server instance = null;
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
	private String password = null;

	private boolean isRunning = true;
	private boolean isStopped = false;
	private Config properties;
	private Config clientConfig;
	private int tickCounter = 0;
	private long nextTick = 0;
	private ConsoleCommandSender commandSender = new ConsoleCommandSender();
	private CommandMap commandMap = null;
	private ExecutorService pool = Executors.newCachedThreadPool();
	private ExecutorService synchronizedPool = Executors.newSingleThreadExecutor();

	private Map<String, Player> players = new HashMap<String, Player>();
	private Map<String, ProxyClient> clients = new HashMap<String, ProxyClient>();

	public String getApiVersion() {
		return BCProxy.API_VERSION;
	}

	public String getVersion() {
		return BCProxy.VERSION;
	}

	public static Server getInstance() {
		return instance;
	}

	public Server(String filePath, String dataPath, String pluginPath) {
		instance = this;
		this.logger = new Logger(dataPath + "server.log");
		this.filePath = filePath;
		if (!new File(pluginPath).exists()) {
			new File(pluginPath).mkdirs();
		}
		this.dataPath = new File(dataPath).getAbsolutePath() + "/";
		this.pluginPath = new File(pluginPath).getAbsolutePath() + "/";

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		}));
		Thread.currentThread().setName("JBProxy");

		this.logger.info(Color.GREEN + "Loading " + Color.WHITE + "server.properties");
		this.properties = new Config(this.dataPath + "server.properties", Config.PROPERTIES, new ConfigSection() {
			{
				put("proxy-ip", "0.0.0.0");
				put("proxy-port", 9855);
				put("password", "ExamplePassword123");
				put("async-workers", "auto");
				put("only-via-main",true);
			}
		});
		File file = new File(this.dataPath + "clients.yml");
		if (!file.exists()) {
			this.clientConfig = new Config(this.dataPath + "clients.yml", Config.YAML);
			clientConfig.set("servers.Server1.address", "0.0.0.0");
			clientConfig.set("servers.Server1.port", 19132);
			clientConfig.set("servers.Server1.isMain", true);
			clientConfig.set("servers.Server2.address", "0.0.0.0");
			clientConfig.set("servers.Server2.port", 19133);
			clientConfig.set("servers.Server2.isMain",false);
			clientConfig.save();
		} else {
			this.clientConfig = new Config(this.dataPath + "clients.yml", Config.YAML);
		}

		this.commandSender = new ConsoleCommandSender();
		this.commandMap = new CommandMap(this);
		this.pool.execute(() -> {
			Thread.currentThread().setName("JBProxy-Commands");
			this.getCommandMap().dispatch(this.commandSender,
					command -> getLogger().error("Command " + command + " not found"));
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

		this.properties.save(true);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			this.isStopped = true;

			getLogger().info("Disabling all plugins");
			getPluginManager().disablePlugins();

			getLogger().info("Disabling event handlers");
			HandlerList.unregisterAll();

			getLogger().info("Stopping all tasks");
			this.scheduler.cancelAllTasks();
			this.scheduler.mainThreadHeartbeat(Integer.MAX_VALUE);
		}));
		registerCommands();
		start();
	}

	private void registerCommands() {
		this.getCommandMap().registerCommand(new HelpCommand("help"));
		this.getCommandMap().registerCommand(new StopCommand("stop"));
		this.getCommandMap().registerCommand(new PluginsCommand("plugins"));
		this.getCommandMap().registerCommand(new ClientsCommand("clients"));
		this.getCommandMap().registerCommand(new ListCommand("list"));
	}

	public void start() {
		getLogger().info(Color.GREEN + "Server has been started!, Type help for help (?)");
		this.nextTick = System.currentTimeMillis();
		while (this.isRunning) {
			try {
				long tick = System.currentTimeMillis();
				if((tick - this.nextTick) < -5) {
					return;
				}
				++this.tickCounter;
				this.scheduler.mainThreadHeartbeat(this.tickCounter);
			} catch (RuntimeException e) {
				getLogger().logException(e);
			}
			
			try {
				Thread.sleep(1);
			} catch(InterruptedException ex) {
				getLogger().logException(ex);
			}
		}
	}

	public void shutdown() {
		this.synchronizedPool.shutdown();
		this.pool.shutdown();
		this.isRunning = false;
		System.exit(0);
	}

	public Logger getLogger() {
		return Logger.getLogger();
	}

	public PluginManager getPluginManager() {
		return this.pluginManager;
	}

	public void enablePlugins() {
		for (Plugin plugin : getPluginManager().getPlugins().values()) {
			if (!plugin.isEnabled()) {
				getPluginManager().enablePlugin(plugin);
			}
		}
	}

	public CommandMap getCommandMap() {
		return this.commandMap;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public boolean onlyViaMain() {
		return this.getPropertyBoolean("only-via-main",true);
	}
	
	public String getPassword() {
		return this.getPropertyString("proxy-password", "ExamplePassword123");
	}

	public int getPort() {
		return this.getPropertyInt("proxy-port", 9855);
	}

	public void handleProxyPackets(Packet packet, Channel channel) {
		if (packet instanceof ProxyConnectPacket) {
			if (getSessionManager().getVerifiedChannels().contains(channel)) {
				return;
			}
			ProxyConnectPacket pk = (ProxyConnectPacket) packet;
			if (pk.address.equalsIgnoreCase("0.0.0.0")) {
				pk.address = "localhost";
			}
			ProxyClient client = getClientByName(pk.serverId);
			if (client == null) {
				this.logger.info(Color.RED + "Unknown client");
				pk.success = false;
				getSessionManager().sendPacket(pk, channel);
				return;
			}
			InetAddress add1;
			InetAddress add2;
			try {
				add1 = InetAddress.getByName(pk.address);
				add2 = InetAddress.getByName(client.getAddress());
				if (add1 != add2) {
					this.logger.info(pk.address + ":" + client.getAddress());
					this.logger.info(Color.RED + "Unallowed adress");
					pk.success = false;
					getSessionManager().sendPacket(pk, channel);
					return;
				}
			} catch (UnknownHostException e) {
				return;
			}
			if (client.getPort() != pk.port) {
				this.logger.info(pk.port + ":" + client.getPort());
				this.logger.info(Color.RED + "Right address and id, but wrong port");
				pk.success = false;
				getSessionManager().sendPacket(pk, channel);
				return;
			}
			if (!this.getPassword().equals(pk.password)) {
				this.logger.info(Color.RED + "Wrong password");
				pk.success = false;
				getSessionManager().sendPacket(packet, channel);
				return;
			}
			if (!client.isOnline()) {
				client.setOnline(true);
			}
			pk.success = true;
			getSessionManager().getVerifiedChannels().add(channel);
			client.setServerChannel(channel);
			this.logger.info(Color.GREEN + "Client " + pk.serverId + " [" + pk.address + ":" + pk.port + "] connected");
			// ProxyClient client =
			getSessionManager().sendPacket(pk, channel);
		}
		
		if (!getSessionManager().getVerifiedChannels().contains(channel)) {
			return;
		}
		PacketReceiveEvent event = new PacketReceiveEvent(this,packet);
		getPluginManager().callEvent(event);
		if(packet instanceof IServerPacket) {
			handleServerPackets(packet, channel);
		} else if(packet instanceof IPlayerPacket) {
			handlePlayerPackets(packet, channel);
		}	
	}

	public void handleServerPackets(Packet packet, Channel channel) {
		if (packet instanceof ServerInfoPacket) {
			ServerInfoPacket pk1 = (ServerInfoPacket) packet;
			try {
				ProxyClient client = getOnlineClientByName(pk1.serverId);
				pk1.address = client.getAddress();
				pk1.port = client.getPort();
				pk1.isMain = client.isMain();
				ArrayList<String> list = new ArrayList<String>();
				for (String s : client.getOnlinePlayers().keySet()) {
					list.add(s);
				}
				pk1.players = list;
				getSessionManager().sendPacket(pk1, channel);
			} catch (NullPointerException ex) {

			}
			
		} else if(packet  instanceof ServerListSyncPacket) {
			ServerListSyncPacket pk2 = (ServerListSyncPacket) packet;
			ArrayList<String> servers = new ArrayList<String>();
			for(String c : getOnlineClients().keySet()) {
				servers.add(c);
			}
			pk2.serverList = servers;
			getSessionManager().sendPacket(pk2,channel);
			
		} else if(packet instanceof PlayerInfoPacket) {
			getLogger().info("Gotcha info");
			PlayerInfoPacket pk = (PlayerInfoPacket)packet;
			Player p = getPlayer(pk.player);
			if(p == null) {
				getLogger().info("Null");
				pk.online = false;
				pk.handled = true;
				getSessionManager().sendPacket(pk,channel);
			}else {
				getLogger().info("True");
				pk.online = true;
				pk.serverId = p.getCurrentClient().getServerId();
				pk.uuid = p.getUUID();
				pk.handled = true;
				getSessionManager().sendPacket(pk,channel);
			}
		}
	}

	public void handlePlayerPackets(Packet packet, Channel channel) {
		if (packet instanceof LoginPacket) {
			LoginPacket pk1 = (LoginPacket) packet;
			String nick = pk1.username;
			UUID uid = pk1.uuid;
			String client = pk1.serverId;
			Player player = new Player(nick,uid,this);
			ProxyClient server = getOnlineClientByName(client);
			PlayerLoginEvent event = new PlayerLoginEvent(player, server);
			getPluginManager().callEvent(event);
			if(!event.isCancelled()) {
				Player oldPlayer = getPlayer(nick);
				if(oldPlayer != null) {
					if(oldPlayer.isTransfering) {
						if(oldPlayer.transferDestination.getServerId().equals(server.getServerId())) {
							ProxyClient oldServer = oldPlayer.getCurrentClient();
							oldServer.removePlayer(oldPlayer.getName());
							oldPlayer.setCurrentClient(server);
							server.addPlayer(oldPlayer);
							oldPlayer.isTransfering = false;
							oldPlayer.transferDestination = null;
							this.logger.info("§ePlayer §a" + player.getName() + " §etransfered from §b" + oldServer.getServerId() + " §eto §b"+server.getServerId());
						}
					} 
				} else {
					players.put(nick,player);
					server.addPlayer(player);
					this.logger.info("§ePlayer §a" + player.getName() + " §econnected");
				}
			}	
		} else if(packet instanceof LogoutPacket) {
			LogoutPacket pk2 = (LogoutPacket)packet;
			String nick = pk2.username;
			UUID uid = pk2.uuid;
			String reason = pk2.reason;
			Player player = getPlayer(nick);
			PlayerLogoutEvent event = new PlayerLogoutEvent(player,reason);
			getPluginManager().callEvent(event);
			players.remove(nick);
			for(ProxyClient client : getOnlineClients().values()) {
				if(client.getOnlinePlayers().containsKey(nick)) {
					client.removePlayer(nick);
					this.logger.info("§cPlayer §a" + nick + " §cdisconnected from server §b"+client.getServerId());
				}
			}
		} else if(packet instanceof TransferPacket) {
			TransferPacket pk3 = (TransferPacket)packet;
			String nick = pk3.player;
			String dest = pk3.destination;
			Player p = getPlayerByName(nick);
			if(isClientOnline(dest)) {
				p.transfer(getOnlineClientByName(dest));
				return;
			}
			p.sendMessage("§cFailed to transfer");
		} else if(packet instanceof KickPacket) {
			KickPacket pk4 = (KickPacket)packet;
			Player player = getPlayerByName(pk4.player);
			player.kick(pk4.reason);
		} else if(packet instanceof ChatPacket) {
			ChatPacket pk5 = (ChatPacket)packet;
			//getLogger().info("Chat");
			Player player = getPlayerByName(pk5.player);
			String msg = pk5.message;
			switch(pk5.type) {
			case "chat":
				player.sendMessage(msg);
				break;
			case "tip":
				player.sendTip(msg);
				break;
			case "popup":
				player.sendPopup(msg);
				break;
			case "title":
				player.sendTitle(msg);
				break;
			}
		}
	}

	public void refreshClients() {
		this.clients = new HashMap<>();
		this.players = new HashMap<>();
		ConfigSection section = this.clientConfig.getSection("servers");
		for (Map.Entry map : section.getAll().entrySet()) {
			String serverId = (String) map.getKey();
			ConfigSection info = (ConfigSection) map.getValue();
			String address = info.getString("address");
			int port = info.getInt("port");
			boolean main = info.getBoolean("isMain");
			registerNewClient(serverId, port, address,main);
		}
	}

	public ProxyClient registerNewClient(String serverId, int port, String address, boolean main) {
		ProxyClient client = new ProxyClient(serverId, address, port,main);
		clients.put(serverId, client);
		getLogger().log("Registered new Client " + serverId);
		return client;
	}

	public Map<String, Player> getOnlinePlayers() {
		return players;
	}
	
	public Player getPlayer(String nick) {
		return players.get(nick);
	}
	
	public void addPlayer(String identifier, Player p) {
		if(!players.containsKey(identifier)) {
			players.put(identifier,p);
		}
	}
	
	public void removePlayer(String identifier) {
		if(players.containsKey(identifier)) {
			players.remove(identifier);
		}
	}

	public Player getPlayerByName(String name) {
		int delta = Integer.MAX_VALUE;
		for (ProxyClient client : getOnlineClients().values()) {
			for (Player pla : client.getOnlinePlayers().values()) {
				if (pla.getName().toLowerCase().startsWith(name)) {
					int curDelta = pla.getName().length() - name.length();
					if (curDelta < delta) {
						delta = curDelta;
						return pla;
					}
				}
			}
		}
		return null;
	}
	
	public boolean isClientOnline(String name) {
		ProxyClient client = getClientByName(name);
		return client.isOnline();
	}

	public Map<String, ProxyClient> getOnlineClients() {
		Map<String, ProxyClient> clients = new HashMap<String, ProxyClient>();
		for (Map.Entry<String, ProxyClient> connected : getClients().entrySet()) {
			if (connected.getValue().isOnline()) {
				clients.put(connected.getKey(), connected.getValue());
			}
		}
		return clients;
	}

	public Map<String, ProxyClient> getClients() {
		return clients;
	}

	public ProxyClient getOnlineClientByName(String name) {
		if (getOnlineClients().containsKey(name)) {
			return getOnlineClients().get(name);
		}
		return null;
	}

	public ProxyClient getClientByName(String name) {
		if (clients.containsKey(name)) {
			return clients.get(name);
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
		return this.properties.exists(variable) ? (!this.properties.get(variable).equals("")
				? Integer.parseInt(String.valueOf(this.properties.get(variable)))
				: defaultValue) : defaultValue;
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
