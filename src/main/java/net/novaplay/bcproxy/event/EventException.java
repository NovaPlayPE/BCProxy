package net.novaplay.bcproxy.event;
import net.novaplay.bcproxy.server.ServerException;

public class EventException extends ServerException {
    public EventException(String message) {
        super(message);
    }
}