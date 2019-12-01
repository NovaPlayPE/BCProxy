package net.novaplay.jbproxy.command.defaults;

import net.novaplay.jbproxy.client.ProxyClient;
import net.novaplay.jbproxy.command.Command;
import net.novaplay.jbproxy.command.CommandSender;
import net.novaplay.jbproxy.server.Server;
import net.novaplay.jbproxy.utils.Color;

public class ClientsCommand extends Command {

	public ClientsCommand(String name) {
		super(name);
		this.usage = "clients";
		this.description = "Shows all clients";
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		sender.sendMessage("§6Connected clients:");
		for(ProxyClient client : Server.getInstance().getClients().values()) {
			sender.sendMessage("§f-" + Color.GREEN + client.getServerId() + ":" + client.getAddress() + ":" +
					client.getPort() + ":" + client.isOnline());
		}
		return false;
	}
}
