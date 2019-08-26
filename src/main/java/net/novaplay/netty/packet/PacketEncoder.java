package net.novaplay.netty.packet;

import com.google.common.base.Charsets;
import net.novaplay.netty.PacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.UUID;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf byteBuf) throws Exception {
        int id = PacketHandler.PACKETS.indexOf(packet.getClass());
        if(id == -1) {
            throw new NullPointerException("Couldn't find id of packet " + packet.getClass().getSimpleName());
        }
        UUID uuid = packet.getUniqueId();
        byteBuf.writeInt(id);
        byteBuf.writeInt(uuid.toString().length());
        byteBuf.writeCharSequence(uuid.toString(), Charsets.UTF_8);
        packet.write(byteBuf);
    }
}