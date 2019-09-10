package net.novaplay.jbproxy.command;

import net.novaplay.api.command.CommandArgument;

import java.util.*;

public class CommandLine implements net.novaplay.api.command.CommandLine{
	
	public ArrayList<CommandArgument> args = new ArrayList<CommandArgument>();
	
	public CommandLine() {}
	
	public void registerNewArguments(CommandArgument argument) {
		if(!args.contains(argument)) {
			args.add(argument);
		}
	}
	
	public ArrayList<CommandArgument> getArguments(){
		return args;
	}

	public void setArguments(ArrayList<CommandArgument> args) {
		this.args = args;
	}

	@Override
	public int getId() {
		return 0;
	}
	
}
