package net.novaplay.jbproxy.command;

import net.novaplay.jbproxy.administration.ServerAdministrator;

public interface CommandSender extends ServerAdministrator{

	void sendMessage(String message);
	
}
