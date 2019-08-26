package net.novaplay.jbproxy.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Logger {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm:ss"); //"HH:mm:ss" "dd.MM.yyyy HH:mm:ss"
    private static boolean timeZoneSetted = false;
    public static File file = null;
    
    public Logger(String path) {
    	file = new File(path);
    	if(!file.exists()) {
    		try {
    			file.createNewFile();
    		} catch(IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * log error message
     */
    public void error(String message) {
        if (!timeZoneSetted) {
            setTimeZone();
        }
        message = Color.RESET + Color.CYAN + "[" + simpleDateFormat.format( new Date() ) + "] " + Color.RED + message + Color.WHITE;
        try {
        	BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
        	bw.append(message);
        	bw.close();
        } catch(IOException e) {
        	e.printStackTrace();
        }
        System.out.println(message);
    }

    /**
     * log normal message
     */
    public void info(String message) {
        if (!timeZoneSetted) {
            setTimeZone();
        }
        message = Color.RESET + Color.CYAN + "[" + simpleDateFormat.format( new Date() ) + "] " + Color.RESET + message + Color.WHITE;
        try {
        	BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
        	bw.append(message);
        	bw.close();
        } catch(IOException e) {
        	e.printStackTrace();
        }
        System.out.println(message);
    }

    /**
     * log normal message
     */
    public void log(String message) {
        if (!timeZoneSetted) {
            setTimeZone();
        }
        message = Color.RESET + Color.CYAN + "[" + simpleDateFormat.format( new Date() ) + "] " + Color.RESET + message + Color.WHITE;
        try {
        	BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
        	bw.append(message);
        	bw.close();
        } catch(IOException e) {
        	e.printStackTrace();
        }
        System.out.println(message);
    }

    /**
     * log warning message
     */
    public void warning(String message) {
        if (!timeZoneSetted) {
            setTimeZone();
        }
        message = Color.RESET + Color.CYAN + "[" + simpleDateFormat.format( new Date() ) + "] " + Color.RESET + Color.YELLOW + message + Color.WHITE ;
        try {
        	BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
        	bw.append(message);
        	bw.close();
        } catch(IOException e) {
        	e.printStackTrace();
        }
        System.out.println(message);
    }

    /**
     * log debug message
     */
    public void debug(String message) {
        if (!timeZoneSetted) {
            setTimeZone();
        }
        message = Color.RESET + Color.CYAN + "[" + simpleDateFormat.format( new Date() ) + "] " + Color.RESET + Color.CYAN + message + Color.WHITE;
        try {
        	BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
        	bw.append(message);
        	bw.close();
        } catch(IOException e) {
        	e.printStackTrace();
        }
        System.out.println(message);
    }
    
    /**
     * log exception
     */
    public void logException(Exception message) {
    	warning(Utils.getExceptionMessage(message));
    }

    /**
     * set timezone
     */
    private static void setTimeZone() {
        simpleDateFormat.setTimeZone( TimeZone.getTimeZone( "Europe/Berlin" ));
        timeZoneSetted = true;
    }
}
