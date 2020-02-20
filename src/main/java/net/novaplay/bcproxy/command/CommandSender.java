package net.novaplay.bcproxy.command;

import net.novaplay.bcproxy.administration.ServerAdministrator;

public interface CommandSender extends ServerAdministrator{

	void sendMessage(String message);
	
}
