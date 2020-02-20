package net.novaplay.bcproxy.command;

import java.io.IOException;

import jline.console.ConsoleReader;
import jline.console.CursorBuffer;
import net.novaplay.bcproxy.InterruptibleThread;
import net.novaplay.bcproxy.event.server.CommandEvent;
import net.novaplay.bcproxy.server.Server;
import net.novaplay.bcproxy.utils.Utils;

public class CommandReader extends Thread implements InterruptibleThread{
	
	public static CommandReader instance;
	private ConsoleReader reader;
	private CursorBuffer stashed;
	
	private boolean running = true;
	
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
	 
	 @Override
	 public void run() {
		 long lastLine = System.currentTimeMillis();
		 while(this.running) {
			 if(Server.getInstance().getPluginManager() == null) { continue; }
			 String line = readLine();
			 if(line != null && !line.trim().equals("")) {
				 try {
					 CommandEvent e = new CommandEvent(line);
					 Server.getInstance().getPluginManager().callEvent(e);
					 if(!e.isCancelled()) {
						 
					 }
				 } catch(Exception e){
					 Server.getInstance().getLogger().logException(e);
				 }
			 } else if(System.currentTimeMillis() - lastLine <= 1) {
				 try {
					 sleep(40);
				 } catch(InterruptedException e) {
					 Server.getInstance().getLogger().logException(e);
				 }
			 }
			 lastLine = System.currentTimeMillis();
		 }
	 }
	 
	 public void shutdown() {
		 this.running  = false;
	 }

	 public void removePromptLine() {
	       try {
	            reader.resetPromptLine("", "", 0);
        } catch (IOException e) {
	            e.printStackTrace();
	    }
	 }
}
