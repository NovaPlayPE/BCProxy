package net.novaplay.jbproxy.command;

public class CommandParameter {
	
	private String paramType = "string";
	private String paramName;
	
	public CommandParameter(String type, String name) {
		this.paramType = type;
		this.paramName = name;
	}
	
	public String getParameterType(){
		return this.paramType;
	}
	
	public String getParameterName() {
		return this.paramName;
	}

}
