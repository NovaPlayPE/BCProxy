package net.novaplay.jbproxy.event.server;

import net.novaplay.jbproxy.event.Cancellable;
import net.novaplay.jbproxy.event.Event;
import net.novaplay.jbproxy.event.HandlerList;

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
