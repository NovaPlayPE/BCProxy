package net.novaplay.jbproxy.command;

public abstract class Command {
	
	private String name;
	
	public Command(String name) {
		this.name = name;
	}
	
	public String getName() { return this.name; }
	
	public String asString() { return "Command(NAME="+this.getName()+")"; }
	
	public abstract boolean execute(CommandSender sender, String[] args);

}
