package net.novaplay.jbproxy.command;

import java.util.*;

public abstract class Command {
	
	private String name;
	private boolean hasArgs = false;
	
	//PLAN HOW TO DO COMMANDS
	
	///rank = new Command("command");
	///rank args = new Command("command",true);
	///rank Ragnok123 OWN = (new Command("command",true)).registerArgument(int order, new CommandArgument(){{registerParameter(new CommandParametres("string", "Player");}}).registerArgument(1, new CommandArgument("rank"){{registerParameter(new CommandParametres("string", "rank"));}});
	///rank settings = (new Command("command",true)).registerArgument(int order, new CommandArgument("settings"){{registerParameter(new CommandParametres("string", "Player");}})
	
	public Command(String name) { this(name,false); }
	
	public Command(String name, boolean hasArgs) {
		this.name = name;
		this.hasArgs = hasArgs;
	}
	
	public String getName() { return this.name; }
	
	public String asString() { return "Command(NAME="+this.getName()+")"; }
	
	public boolean hasArgs() { return this.hasArgs; }
	
	public abstract boolean execute(CommandSender sender);
	public abstract boolean execute(CommandSender sender, String[] args);

}
