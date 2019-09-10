package net.novaplay.jbproxy.command;

public class CommandParameter implements net.novaplay.api.command.CommandParameter{
	
	private String paramType = "string";
	private String paramName;
	
	public CommandParameter(String name, String type) {
		this.paramType = type;
		this.paramName = name;
	}
	
	public String getParameterType(){ return this.paramType; }
	
	public String getParameterName() { return this.paramName; }

	@Override
	public void setParameterName(String pname) { this.paramName = pname; }

	@Override
	public void setParameterType(String ptype) { this.paramType = ptype; }

}
