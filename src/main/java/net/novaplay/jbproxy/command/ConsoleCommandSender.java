package net.novaplay.jbproxy.command;

import net.novaplay.jbproxy.server.Server;

public class ConsoleCommandSender implements CommandSender{
	
	public void sendMessage(String message) {
		Server.getInstance().getLogger().log(message);
	}

	@Override
	public void setOp(boolean value) {
		
	}

	@Override
	public boolean isOp() {
		return true;
	}

}
