package net.novaplay.jbproxy.command;

public abstract class Command extends net.novaplay.api.command.Command{
	
	public Command(String name) { this(name,false); }
	
	public Command(String name, boolean hasArgs) {
		super(name,hasArgs);
	}
	
	public abstract boolean execute(CommandSender sender);
	public abstract boolean execute(CommandSender sender, String[] args);

}
