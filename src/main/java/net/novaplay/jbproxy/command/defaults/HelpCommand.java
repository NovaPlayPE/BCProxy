package net.novaplay.jbproxy.command.defaults;

import java.util.ArrayList;

import net.novaplay.jbproxy.command.Command;
import net.novaplay.jbproxy.command.CommandSender;
import net.novaplay.jbproxy.server.Server;

public class HelpCommand extends Command{

	public HelpCommand(String name) {
		super(name);
		this.usage = "help";
		this.description = "Shows help list";
		this.aliases.add("?");
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		for(Command command : Server.getInstance().getCommandMap().getCommands()) {
			sender.sendMessage("§6/"+command.getName() + " §f- " +command.getDescription());
		}
		return false;
	}

}
