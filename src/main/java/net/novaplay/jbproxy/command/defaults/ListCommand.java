package net.novaplay.jbproxy.command.defaults;

import net.novaplay.jbproxy.client.ProxyClient;
import net.novaplay.jbproxy.command.Command;
import net.novaplay.jbproxy.command.CommandSender;
import net.novaplay.jbproxy.player.Player;
import net.novaplay.jbproxy.server.Server;

public class ListCommand extends Command {

	public ListCommand(String name) {
		super(name);
		this.usage = "list";
		this.description = "Shows players list";
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(args.length == 0) {
			String list = "";
			int c = 0;
			for(Player p : Server.getInstance().getOnlinePlayers().values()) {
				list += p.getName() + ", ";
				c++;
			}
			sender.sendMessage("§6Players online ("+ Server.getInstance().getOnlinePlayers().size()+"): §a" +list);
		} else {
			if(!args[0].isEmpty()) {
				String server = args[0];
				ProxyClient client = Server.getInstance().getOnlineClientByName(server);
				if(client != null) {
					String list = "";
					int c = 0;
					for(Player p : client.getOnlinePlayers().values()) {
						list += p.getName() + ", ";
						c++;
					}
					sender.sendMessage("§6Players online on client §b"+server+" §6("+ client.getOnlinePlayers().size()+"): §a" +list);
				} else {
					sender.sendMessage("§cClient §b"+server+" §cis offline");	
				}
			}
		}
		return false;
	}

}
