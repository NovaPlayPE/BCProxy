package net.novaplay.jbproxy.command;

import net.novaplay.api.command.CommandParameter;
public class CommandArgument implements net.novaplay.api.command.CommandArgument{
	
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
	public void setName(String name) { this.name = name; }
	public boolean isEmpty() { return this.isEmpty; }
	public void setEmpty(boolean value) { this.isEmpty = value; }
	public boolean needsParameter() { return this.needsParameter; }
	public void setNeed(boolean value) { this.needsParameter = value; }
	public void registerParameter(String a, String b) { registerParameter(new net.novaplay.jbproxy.command.CommandParameter(a,b)); }
	public CommandParameter getCommandParameter() { return this.param; }





}
