package net.novaplay.bcproxy.plugin;
import net.novaplay.bcproxy.server.ServerException;
public class PluginException extends ServerException {
    public PluginException(String message) {
        super(message);
    }
}