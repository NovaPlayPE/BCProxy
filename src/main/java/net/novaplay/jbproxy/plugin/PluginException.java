package net.novaplay.jbproxy.plugin;
import net.novaplay.jbproxy.server.ServerException;
public class PluginException extends ServerException {
    public PluginException(String message) {
        super(message);
    }
}