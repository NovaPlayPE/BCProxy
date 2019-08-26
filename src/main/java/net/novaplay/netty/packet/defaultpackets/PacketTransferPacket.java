package net.novaplay.netty.packet.defaultpackets;

import com.google.common.base.Charsets;
import net.novaplay.netty.PacketHandler;
import net.novaplay.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
public class PacketTransferPacket extends Packet {
    @Getter
    @Setter
    private ArrayList<String> targets = new ArrayList<>();

    @Getter
    @Setter
    private Packet packet;
    
    public void setTargets(ArrayList<String> list) {
    	targets = list;
    }
    
    public ArrayList<String> getTargets(){
    	return targets;
    }
    
    public void setPacket(Packet packett) {
    	packet = packett;
    }
    
    public Packet getPacket() {
    	return packet;
    }

    @Override
    public void read(ByteBuf byteBuf) throws Exception {
        int length;
        //target client
        setTargets(new ArrayList<>());
        int arraySize = byteBuf.readInt();
        for (int i = 0; i < arraySize; i++) {
            length = byteBuf.readInt();
            String servername = (String) byteBuf.readCharSequence(length, Charsets.UTF_8);
            getTargets().add(servername);
        }
        //packet
        int id = byteBuf.readInt();
        ByteBuf targetPacketByteBuf = byteBuf.readBytes(new byte[byteBuf.readableBytes()]);
        Class<? extends Packet> packetClass = PacketHandler.PACKETS.get(id);
        if (packetClass == null) {
            throw new NullPointerException("Couldn't find packet by id " + id);
        }
        Packet packet = packetClass.newInstance();
        packet.read(targetPacketByteBuf);
        setPacket(packet);
    }

    @Override
    public void write(ByteBuf byteBuf) throws Exception {
        //target client
        byteBuf.writeInt(getTargets().size());
        for (int i = 0; i < getTargets().size(); i++) {
            byteBuf.writeInt(getTargets().get(i).length());
            byteBuf.writeCharSequence(getTargets().get(i), Charsets.UTF_8);
        }
        ByteBuf targetPacketByteBuf = Unpooled.buffer();
        packet.write(targetPacketByteBuf);
        int id = PacketHandler.PACKETS.indexOf(packet.getClass());
        byteBuf.writeInt(id);
        byteBuf.writeBytes(targetPacketByteBuf);
    }
}
