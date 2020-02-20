package net.novaplay.bcproxy.command.defaults;

import net.novaplay.bcproxy.command.Command;
import net.novaplay.bcproxy.command.CommandSender;
import net.novaplay.bcproxy.server.Server;

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
