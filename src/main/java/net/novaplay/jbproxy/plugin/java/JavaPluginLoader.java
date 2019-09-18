package net.novaplay.jbproxy.plugin.java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import net.novaplay.jbproxy.plugin.Plugin;
import net.novaplay.jbproxy.plugin.PluginDescription;
import net.novaplay.jbproxy.plugin.PluginLoader;
import net.novaplay.jbproxy.server.Server;
import net.novaplay.jbproxy.utils.Utils;

public class JavaPluginLoader implements PluginLoader {

	private Server server = null;
    private Map<String, Class> classes = new HashMap<String, Class>();
    private Map<String, JavaClassLoader> classLoaders = new HashMap<String, JavaClassLoader>();
	
    public JavaPluginLoader(Server server) { this.server = server; }
    
	@Override
	public Plugin loadPlugin(String filename) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Plugin loadPlugin(File file) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginDescription getPluginDescription(String filename) {
		return getPluginDescription(new File(filename));
	}

	@Override
	public PluginDescription getPluginDescription(File file) {
		try {
			JarFile jar = new JarFile(file);
			JarEntry entry = jar.getJarEntry("jbproxy.yml");
			if (entry == null) {
				entry = jar.getJarEntry("plugin.yml");
				if (entry == null) {
					return null;
				}
			}
			InputStream stream = jar.getInputStream(entry);
			return new PluginDescription(Utils.readFile(stream));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public Pattern[] getPluginFilters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enablePlugin(Plugin plugin) {
		if(plugin instanceof JavaPlugin) {
			if(!plugin.isEnabled()) {
				server.getLogger().info("Enabled plugin " + plugin.getDescription().getFullName());
				((JavaPlugin)plugin).setEnabled(true);
			}
		}
	}

	@Override
	public void disablePlugin(Plugin plugin) {
		if(plugin instanceof JavaPlugin) {
			if(plugin.isEnabled()) {
				server.getLogger().info("Disabled plugin " + plugin.getDescription().getFullName());
				((JavaPlugin)plugin).setEnabled(false);
			}
		}
	}
	
	
	
	protected void removeClass(String name) {
		Class<?> clazz = classes.remove(name);
	}

}
