package net.novaplay.jbproxy.server;

import java.io.File;

import lombok.Getter;
import net.novaplay.jbproxy.scheduler.ServerScheduler;
import net.novaplay.jbproxy.utils.Logger;

public class Server {

	private static Server instance = null;
	@Getter
	public Logger logger = null;
    @Getter
	public String filePath;
    @Getter
    public String dataPath;
    @Getter
    public String pluginPath;
    @Getter
    public ServerScheduler scheduler = null;
    
    public static Server getInstance() {
    	return instance;
    }
    
	public Server(Logger logger, String filePath, String dataPath, String pluginPath) {
		instance = this;
		this.logger = logger;
		this.filePath = filePath;
        if (!new File(pluginPath).exists()) {
            new File(pluginPath).mkdirs();
        }
        this.dataPath = new File(dataPath).getAbsolutePath() + "/";
        this.pluginPath = new File(pluginPath).getAbsolutePath() + "/";
        scheduler = new ServerScheduler();
	}
	
}
