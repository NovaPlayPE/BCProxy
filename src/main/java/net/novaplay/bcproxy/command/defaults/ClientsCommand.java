package net.novaplay.bcproxy.command.defaults;

import net.novaplay.bcproxy.client.ProxyClient;
import net.novaplay.bcproxy.command.Command;
import net.novaplay.bcproxy.command.CommandSender;
import net.novaplay.bcproxy.server.Server;
import net.novaplay.bcproxy.utils.Color;

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
