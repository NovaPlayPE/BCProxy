package net.novaplay.jbproxy.command;

import net.novaplay.api.command.CommandParameter;
public class CommandArgument extends net.novaplay.api.command.CommandArgument{
	
	public CommandArgument() {
		isEmpty = true;
	}
	
	public CommandArgument(String name) {
		super(name);
	}

}
