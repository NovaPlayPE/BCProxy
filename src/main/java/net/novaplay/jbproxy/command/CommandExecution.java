package net.novaplay.jbproxy.command;

public abstract class CommandExecution {
	
	public CommandExecution() {}
	
	public abstract boolean execute(CommandSender sender, CommandLine line);

}
