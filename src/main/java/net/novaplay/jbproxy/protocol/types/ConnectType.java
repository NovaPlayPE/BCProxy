package net.novaplay.jbproxy.protocol.types;

public enum ConnectType{
	JAVA(0),
	BEDROCK(1);
	
	private int type;
	
	ConnectType(int type) {
		this.type = type;
	}
	
	public int getType() {return this.type;}
	
}