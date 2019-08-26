package net.novaplay.jbproxy.command;

import java.io.IOException;

import jline.console.ConsoleReader;
import jline.console.CursorBuffer;
import net.novaplay.jbproxy.InterruptibleThread;
import net.novaplay.jbproxy.server.Server;
import net.novaplay.jbproxy.utils.Utils;

public class CommandReader extends Thread implements InterruptibleThread{
	
	public static CommandReader instance;
	private ConsoleReader reader;
	private CursorBuffer stashed;
	
	 public CommandReader() {
		 if (instance != null) {
			 throw new RuntimeException("Command Reader is already exist");
		 }
	     try {
	         this.reader = new ConsoleReader();
	         reader.setPrompt("> ");
	         instance = this;
	     } catch (IOException e) {
	         Server.getInstance().getLogger().error("Unable to start Console Reader\r\n"+Utils.getExceptionMessage(e));
	     }
	     this.setName("Console");
	 }
	 
	 public static CommandReader getInstance() {
		 return instance;
	 }

	 public String readLine() {
		 try {
			 reader.resetPromptLine("", "", 0);
			 return this.reader.readLine("> ");
		 } catch (IOException e) {
			 Server.getInstance().getLogger().logException(e);
	         return "";
	     }
	 }
	 
	 public synchronized void stashLine() {
	        this.stashed = reader.getCursorBuffer().copy();
	        try {
	            reader.getOutput().write("\u001b[1G\u001b[K");
	            reader.flush();
	        } catch (IOException e) {
	            // ignore
	        }
	 }
	 
	 public synchronized void unstashLine() {
	        try {
	            reader.resetPromptLine("> ", this.stashed.toString(), this.stashed.cursor);
	        } catch (IOException e) {
	            // ignore
	        }
	    }

	 public void removePromptLine() {
	       try {
	            reader.resetPromptLine("", "", 0);
        } catch (IOException e) {
	            e.printStackTrace();
	    }
	 }
}
