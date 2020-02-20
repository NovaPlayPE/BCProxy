package net.novaplay.bcproxy.command.defaults;

import java.util.HashMap;
import java.util.Map;

import net.novaplay.bcproxy.command.Command;
import net.novaplay.bcproxy.command.CommandSender;
import net.novaplay.bcproxy.plugin.Plugin;
import net.novaplay.bcproxy.server.Server;
import net.novaplay.bcproxy.utils.Color;

public class PluginsCommand extends Command {

	public PluginsCommand(String name) {
		super(name);
		this.usage = "/plugins";
		this.aliases.add("pl");
		this.description = "Shows a plugin list";
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		String list = "";
		Map<String, Plugin> pl = Server.getInstance().getPluginManager().getPlugins();
		for(Plugin p : pl.values()) {
			if(list.length() > 0) { list += Color.WHITE + ", "; }
			list += p.isEnabled() ? Color.GREEN : Color.RED;
			list += p.getDescription().getFullName();
		}
		sender.sendMessage(list);
		return false;
	}
	
	
}
