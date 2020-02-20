package net.novaplay.bcproxy.plugin;

import java.io.File;
import java.io.InputStream;

import net.novaplay.bcproxy.config.Config;
import net.novaplay.bcproxy.server.Server;
import net.novaplay.bcproxy.utils.Logger;

public interface Plugin {

    void onLoad();
    void onEnable();
    boolean isEnabled();
    void onDisable();
    boolean isDisabled();
    File getDataFolder();
    PluginDescription getDescription();
    InputStream getResource(String filename);
    boolean saveResource(String filename);
    boolean saveResource(String filename, boolean replace);
    boolean saveResource(String filename, String outputName, boolean replace);
    Config getConfig();
    void saveConfig();
    void saveDefaultConfig();
    void reloadConfig();
    Server getServer();
    String getName();
    Logger getLogger();
    PluginLoader getPluginLoader();
	
}
