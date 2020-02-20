package net.novaplay.bcproxy.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import jline.console.ConsoleReader;
import net.novaplay.bcproxy.BCProxy;
import net.novaplay.bcproxy.server.Server;

public class Logger {
	
	private ConsoleReader console;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private org.apache.log4j.Logger apacheLog = null;
    private org.apache.log4j.Logger fileLog = null;
    public static File file = null;
    private static Set<Logger> loggers = new HashSet<>();
    private Map<String, String> textFormats = new HashMap<String, String>() {{
        this.put("§a", Color.GREEN);
        this.put("§b", Color.CYAN);
        this.put("§c", Color.RED);
        this.put("§d", Color.MAGENTA);
        this.put("§e", Color.YELLOW);
        this.put("§f", Color.WHITE);
        this.put("§0", Color.RESET);
        this.put("§1", Color.BLUE);
        this.put("§2", Color.GREEN);
        this.put("§3", Color.CYAN);
        this.put("§4", Color.RED);
        this.put("§5", Color.MAGENTA);
        this.put("§6", Color.YELLOW);
        this.put("§9", Color.BLUE);
        this.put("§r", Color.RESET);
        this.put("§l", Color.BOLD);
        this.put("§n", Color.UNDERLINED);
    }};
    
    public ConsoleReader getConsole() {
    	return this.console;
    }
    
    public Logger(String path) {
    	file = new File(path);
    	if(!file.exists()) {
    		try {
    			file.createNewFile();
    		} catch(IOException e) {
    			e.printStackTrace();
    		}
    	} else {
    		long date = file.lastModified();
    		String logName = new SimpleDateFormat("Y-M-d HH.mm.ss").format(new Date(date)) + ".log";
    		File logsPath = new File(BCProxy.DATA_PATH, "logs");
    		if(!logsPath.exists()) {
    			logsPath.mkdirs();
    		}
    		file.renameTo(new File(logsPath, logName));
    		file = new File(path);
    		if(!file.exists()) {
        		try {
        			file.createNewFile();
        		} catch(IOException e) {
        			e.printStackTrace();
        		}
        	}
    	}
    	org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);
    	apacheLog = org.apache.log4j.Logger.getLogger("ApacheLogger");
    	fileLog = org.apache.log4j.Logger.getLogger("FileLogger");
    	PatternLayout layout = new PatternLayout("[%d{HH:mm:ss}] %m%n");
    	ConsoleAppender ap1 = new ConsoleAppender(layout);
    	apacheLog.addAppender(ap1);
    	try {
    		FileAppender f1 = new FileAppender(layout,path,false);
    		fileLog.addAppender(f1);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	apacheLog.setLevel(Level.INFO);
    	fileLog.setLevel(Level.INFO);
    	try {
    		this.console = new ConsoleReader(System.in, System.out);
    		this.console.setExpandEvents(false);
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	loggers.add(this);
    }
    
    public static Logger getLogger() {
    	return Logger.loggers.iterator().next();
    }
    
    public void error(String message) {
    	message = "§c[ERROR]§r " +message +"§r";
    	this.fileLog.error(removeColors(message));
    	message = colorize(message);
    	this.apacheLog.error(message);
    }

    public void info(String message) {
    	message = "§b[INFO]§r " +message +"§r";
    	this.fileLog.info(removeColors(message));
    	message = colorize(message);
    	this.apacheLog.info(message);
    }

    public void log(String message) {
    	message = "§b[INFO]§r " +message +"§r";
    	this.fileLog.info(removeColors(message));
    	message = colorize(message);
    	this.apacheLog.info(message);
    }

    public void warning(String message) {
    	message = "§e[WARNING]§r " +message +"§r";
    	this.fileLog.warn(removeColors(message));
    	message = colorize(message);
    	this.apacheLog.warn(message);
    }

    public void debug(String message) {
    	message = "§b[DEBUG]§r " +message +"§r";
    	this.fileLog.info(removeColors(message));
    	message = colorize(message);
    	this.apacheLog.info(message);
    }
    
    
    public void logException(Exception message) {
    	warning(Utils.getExceptionMessage(message));
    }
    
    private String removeColors(String message) {
    	String[] list = new String[]{"a", "b", "c", "d", "e", "f","0", "1", "2", "3", "4", "5", "6", "7", "8", "9","r", "l", "n"};
    	for(String colors : list) {
    		message = message.replaceAll("§" + colors, "");
    		}
    	return message;
    }
    
    public String colorize(String message) {
    	for(Map.Entry<String, String> map : textFormats.entrySet()) {
    		message = message.replaceAll(map.getKey(),map.getValue());
    	}
    	return message;
    }
}
