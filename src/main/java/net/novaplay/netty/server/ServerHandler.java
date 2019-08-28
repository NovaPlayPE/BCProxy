package net.novaplay.netty.server;

import net.novaplay.netty.*;
import net.novaplay.netty.packet.Packet;
import net.novaplay.netty.packet.defaultpackets.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;

import java.util.Map;

public class ServerHandler extends SimpleChannelInboundHandler<Packet> {

    @Getter
    private Channel channel = null;
    @Getter
    private NettyServer nettyServer;
    public ServerHandler(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }

    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        for (PacketHandler handler : NettyHandler.getPacketHandlers()) {
            handler.receivePacket(packet, channel);
        }
        if (packet instanceof DisconnectPacket) {
            channel.close();
        }
        NettyHandler.getInstance().runPacketCallbacks(packet);
        if(packet instanceof SetNamePacket) {
            SetNamePacket setNamePacket = (SetNamePacket) packet;
            String oldname = "";
            for (Map.Entry entry : NettyHandler.getClients().entrySet()) {
                String name = (String) entry.getKey();
                Channel channel = (Channel) entry.getValue();
                if (channel == ctx.channel()) {
                    oldname = name;
                    break;
                }
            }
            //remove and add with new name
            if(!oldname.equalsIgnoreCase("")) {
                NettyHandler.getClients().remove(oldname);
                NettyHandler.getClients().put(setNamePacket.getName(), ctx.channel());
            }
        }
        if (packet instanceof PacketTransferPacket) {
            PacketTransferPacket packetTransferPacket = (PacketTransferPacket) packet;
            for(String servername : packetTransferPacket.getTargets()) {
                if(!NettyHandler.getClients().containsKey(servername)) {
                    //error packet
                    ErrorPacket errorPacket = new ErrorPacket();
                    errorPacket.setErrorMessage("The target clientname '" + servername + "' was not found!");
                    PacketHandler.sendPacketDirectly(errorPacket, ctx.channel());
                    continue;
                }
                //transfer packet
                Channel channel = NettyHandler.getClients().get(servername);
                PacketHandler.sendPacketDirectly(packetTransferPacket.getPacket(), channel);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //Logger.log( "Client connected successfully" );
        channel = ctx.channel();
        for(ConnectionListener handler : NettyHandler.getConnectionListeners()) {
            handler.channelConnected(ctx);
        }
        NettyHandler.getClients().put("Channel" + NettyHandler.getClients().size() + 1, ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //Logger.log( "Client disconnected" );
        channel = null;
        for(ConnectionListener handler : NettyHandler.getConnectionListeners()) {
            handler.channelDisconnected(ctx);
        }
        if(NettyHandler.getClients().containsValue(ctx.channel())) {
            String name = "";
            for(Map.Entry entry : NettyHandler.getClients().entrySet()) {
                if(entry.getValue() == ctx.channel()) {
                    name = (String) entry.getKey();
                    break;
                }
            }
            if (!name.equalsIgnoreCase("")) {
                NettyHandler.getClients().remove(name);
            }
        }
    }
}
