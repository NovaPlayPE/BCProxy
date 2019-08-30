package net.novaplay.jbproxy.protocol;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import net.novaplay.jbproxy.protocol.types.ConnectType;
import net.novaplay.netty.packet.Packet;

public class ProxyConnectPacket extends Packet{

	public String serverId = "";
	public String address = "";
	public String password = "";
	public int port = 25565;
	public ConnectType type = ConnectType.JAVA;
	public boolean success = false;
	
	@Override
	public void read(ByteBuf byteBuf) throws Exception {
		int length;
		length = byteBuf.readInt();
		serverId = (String) byteBuf.readCharSequence(length, Charsets.UTF_8);
		length = byteBuf.readInt();
		address = (String) byteBuf.readCharSequence(length, Charsets.UTF_8);
		port = byteBuf.readInt();
		length = byteBuf.readInt();
		switch(length) {
		case 0:
			type = ConnectType.JAVA;
			break;
		case 1:
			type = ConnectType.BEDROCK;
			break;
		}
		length = byteBuf.readInt();
		password = (String) byteBuf.readCharSequence(length, Charsets.UTF_8);
		success = byteBuf.readBoolean();
	}

	@Override
	public void write(ByteBuf byteBuf) throws Exception {	
		byteBuf.writeInt(serverId.length());
		byteBuf.writeCharSequence(serverId,Charsets.UTF_8);
		byteBuf.writeInt(address.length());
		byteBuf.writeCharSequence(address,Charsets.UTF_8);
		byteBuf.writeInt(port);
		byteBuf.writeInt(type.getType());
		byteBuf.writeInt(password.length());
		byteBuf.writeCharSequence(password,Charsets.UTF_8);
		byteBuf.writeBoolean(success);
	}

}
