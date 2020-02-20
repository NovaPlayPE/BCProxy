package net.novaplay.bcproxy.event.server;

import net.novaplay.bcproxy.event.Cancellable;
import net.novaplay.bcproxy.event.Event;
import net.novaplay.bcproxy.event.HandlerList;

public class CommandReceiveEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	public String commandName = "";
	public String[] commandArgs;
	
	public static HandlerList getHandlers() {
		return handlers;
	}
	
	public CommandReceiveEvent(String command, String[] args) {
		this.commandName = command;
	}
	
}
