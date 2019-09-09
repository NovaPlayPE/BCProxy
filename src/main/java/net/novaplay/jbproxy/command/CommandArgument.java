package net.novaplay.jbproxy.command;

public class CommandArgument {
	
	private String name;
	private boolean isEmpty = false;
	private boolean needsParameter = false;
	private CommandParameter param = null;
	
	public CommandArgument() {
		isEmpty = true;
	}
	
	public CommandArgument(String name) {
		this.name = name;
	}
	
	public void registerParameter(CommandParameter param) {
		this.param = param;
	}
	
	public String getName() { return this.name; }
	public boolean isEmpty() { return this.isEmpty; }
	public boolean needsParameter() { return this.needsParameter; }
	public CommandParameter getCommandParameter() { return this.param; }

}
