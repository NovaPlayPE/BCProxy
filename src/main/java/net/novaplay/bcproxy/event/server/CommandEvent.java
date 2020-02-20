package net.novaplay.bcproxy.event.server;

import net.novaplay.bcproxy.event.Cancellable;
import net.novaplay.bcproxy.event.Event;
import net.novaplay.bcproxy.event.HandlerList;

public class CommandEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	
	public String command = "";
	
	public CommandEvent(String command) {
		this.command = command;
	}
	
	public static HandlerList getHandlers() {
		return handlers;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}

}
