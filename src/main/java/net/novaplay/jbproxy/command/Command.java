package net.novaplay.jbproxy.command;

public class Command extends net.novaplay.api.command.Command{
	
	private CommandExecution execution = null;
	
	public Command(String name) { this(name,false); }
	
	public Command(String name, boolean hasArgs) {
		super(name,hasArgs);
	}
	
	public CommandExecution getExecution() {
		return execution;
	}
	
	public void setExecution(CommandExecution execution) {
		this.execution = execution;
	}
	
	public boolean execute(CommandSender sender, CommandLine line) {return this.execution.execute(sender, line);}

}
