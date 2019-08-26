package net.novaplay.netty.packet.defaultpackets;

import com.google.common.base.Charsets;
import net.novaplay.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
public class ErrorPacket extends Packet {
    @Getter
    @Setter
    private String errorMessage = "";

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        int length = byteBuf.readInt();
        setErrorMessage((String) byteBuf.readCharSequence(length, Charsets.UTF_8));
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        byteBuf.writeInt(getErrorMessage().length());
        byteBuf.writeCharSequence(getErrorMessage(), Charsets.UTF_8);
    }
    
    public void setErrorMessage(String error) {
    	errorMessage = error;
    }
    
    public String getErrorMessage() {
    	return errorMessage;
    }
}