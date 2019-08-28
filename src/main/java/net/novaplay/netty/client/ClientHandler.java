package net.novaplay.netty.client;

import net.novaplay.netty.ConnectionListener;
import net.novaplay.netty.NettyHandler;
import net.novaplay.netty.PacketHandler;
import net.novaplay.netty.packet.Packet;
import net.novaplay.netty.packet.defaultpackets.DisconnectPacket;
import net.novaplay.netty.packet.defaultpackets.ErrorPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;

public class ClientHandler extends SimpleChannelInboundHandler<Packet> {

    @Getter
    private Channel channel = null;

    @Getter
    private NettyClient nettyClient;

    public ClientHandler(NettyClient client) {
        nettyClient = client;
    }

    public NettyClient getNettyClient() {
    	return nettyClient;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }

    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        for(PacketHandler handler : NettyHandler.getPacketHandlers()) {
            handler.receivePacket(packet, channel);
        }
        if(packet instanceof ErrorPacket) {
            String message = ((ErrorPacket)packet).getErrorMessage();
            System.out.println(message);
        }
        if(packet instanceof DisconnectPacket) {
            channel.close();
        }
        NettyHandler.getInstance().runPacketCallbacks(packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        for(ConnectionListener handler : NettyHandler.getConnectionListeners()) {
            handler.channelConnected(ctx);
        }
        getNettyClient().setChannel(ctx.channel());
        if(NettyHandler.getPacketHandlers().size() > 0) {
            if(PacketHandler.packetsToSend.size() > 0) {
                for(Packet packet : PacketHandler.packetsToSend) {
                    NettyHandler.getPacketHandlers().get(0).sendPacket(packet);
                }
                PacketHandler.packetsToSend.clear();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channel = null;
        for(ConnectionListener handler : NettyHandler.getConnectionListeners()) {
            handler.channelDisconnected(ctx);
        }
        getNettyClient().setChannel(null);
    }
}