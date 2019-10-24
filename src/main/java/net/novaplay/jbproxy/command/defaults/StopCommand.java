package net.novaplay.jbproxy.command.defaults;

import net.novaplay.jbproxy.command.Command;
import net.novaplay.jbproxy.command.CommandSender;
import net.novaplay.jbproxy.server.Server;

public class StopCommand extends Command {

	public StopCommand(String name) {
		super(name);
		this.description = "Stops a server";
		this.usage = "stop";
	}
	
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		Server.getInstance().shutdown();
		return false;
	}

}
