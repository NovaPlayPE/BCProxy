package net.novaplay.netty;

import net.novaplay.callback.Callback;
import net.novaplay.netty.client.NettyClient;
import net.novaplay.netty.packet.Packet;
import net.novaplay.netty.server.NettyServer;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NettyHandler {
    @Getter
    private static ArrayList<PacketHandler> packetHandlers = new ArrayList<>();
    @Getter
    private static ArrayList<ConnectionListener> connectionListeners = new ArrayList<>();
    @Getter
    private static HashMap<String, Channel> clients = new HashMap<>();
    @Getter
    private static NettyHandler instance;
    @Getter
    @Setter
    private PacketHandler handler;
    @Getter
    @Setter
    private types type = types.CLIENT;
    @Getter
    private NettyClient nettyClient;
    @Getter
    private NettyServer nettyServer;
    @Getter
    private HashMap<UUID, ArrayList<Callback>> packetCallbacks = new HashMap<>();
    
    public NettyClient getNettyClient() {
    	return nettyClient;
    }
    
    public static HashMap<String, Channel> getClients(){
    	return clients;
    }
    
    public static NettyHandler getInstance() {
    	return instance;
    }

    public static ArrayList<PacketHandler> getPacketHandlers(){
    	return packetHandlers;
    }
    
    public static ArrayList<ConnectionListener> getConnectionListeners(){
    	return connectionListeners;
    }
    
    public HashMap<UUID, ArrayList<Callback>> getPacketCallbacks(){
    	return packetCallbacks;
    }
    
    public NettyServer getNettyServer() {
    	return nettyServer;
    }
    
    public NettyHandler() {
        instance = this;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if(getNettyClient() != null) {
                    getNettyClient().disconnect();
                }
                if(getNettyServer() != null) {
                    getNettyServer().stopServer();
                }
            }
        } ) );
    }

    public void connectToServer(String host, int port, Callback callback) {
        type = types.CLIENT;
        unregisterAllPacketHandler();
        unregisterAllConnectionListener();
        //close server connection
        if (nettyServer != null) {
            nettyServer.stopServer();
        }
        //close client connection
        if (nettyClient != null) {
            nettyClient.disconnect();
        }
        nettyClient = new NettyClient();
        nettyClient.connect(host, port, callback);
    }

    public void startServer(int port, Callback callback) {
        type = types.SERVER;
        unregisterAllPacketHandler();
        unregisterAllConnectionListener();
        //close server connection
        if (nettyServer != null) {
            nettyServer.stopServer();
        }
        //close client connection
        if (nettyClient != null) {
            nettyClient.disconnect();
        }
        nettyServer = new NettyServer();
        nettyServer.startServer(port, callback);
    }

    public void registerPacketHandler(PacketHandler handler) {
        if(getPacketHandlers().contains(handler)) {
            return;
        }
        getPacketHandlers().add(handler);
    }

    public void unregisterPacketHandler(PacketHandler handler) {
        if(!getPacketHandlers().contains(handler)) {
            return;
        }
        getPacketHandlers().remove(handler);
    }

    public void unregisterAllPacketHandler() {
        getPacketHandlers().clear();
    }

    public void registerConnectionListener(ConnectionListener handler) {
        if(getConnectionListeners().contains(handler)) {
            return;
        }

        getConnectionListeners().add(handler);
    }

    public void unregisterConnectionListener(ConnectionListener handler) {
        if(!getConnectionListeners().contains(handler)) {
            return;
        }
        getConnectionListeners().remove(handler);
    }

    public void unregisterAllConnectionListener() {
        getConnectionListeners().clear();
    }

    public String getClientnameByChannel(Channel channel) {
        for(Map.Entry entry : clients.entrySet()) {
            if(entry.getValue() == channel) {
                return (String) entry.getKey();
            }
        }
        return "";
    }

    public void addPacketCallback(Packet packet, Callback callback) {
        if(!getPacketCallbacks().containsKey( packet.getUniqueId())) {
            getPacketCallbacks().put(packet.getUniqueId(), new ArrayList<>());
        }
        getPacketCallbacks().get(packet.getUniqueId()).add(callback);
    }

    public void removePacketCallbacks(Packet packet) {
        if(!getPacketCallbacks().containsKey(packet.getUniqueId())) {
            return;
        }
        getPacketCallbacks().remove(packet.getUniqueId());
    }

    public void runPacketCallbacks(Packet packet) {
        if(!getPacketCallbacks().containsKey(packet.getUniqueId())) {
            return;
        }
        for(Callback callback : getPacketCallbacks().get(packet.getUniqueId())) {
            callback.accept(packet);
        }
    }
    
    public types getTypes() {
    	return type;
    }

    public enum types {
        SERVER,
        CLIENT
    }
}