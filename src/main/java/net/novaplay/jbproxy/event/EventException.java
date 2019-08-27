package net.novaplay.jbproxy.event;
import net.novaplay.jbproxy.server.ServerException;

public class EventException extends ServerException {
    public EventException(String message) {
        super(message);
    }
}