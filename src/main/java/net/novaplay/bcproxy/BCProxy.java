package net.novaplay.bcproxy;

import net.novaplay.bcproxy.command.CommandReader;
import net.novaplay.bcproxy.server.*;
import net.novaplay.bcproxy.utils.*;

public class BCProxy {
	
	public final static String API_VERSION = "1.0.0";
	public final static String VERSION = "1.0";
	public final static String PATH = System.getProperty("user.dir") + "/";
    public final static String DATA_PATH = System.getProperty("user.dir") + "/";
    public final static String PLUGIN_PATH = DATA_PATH + "plugins";
    public static final long START_TIME = System.currentTimeMillis();
    public static boolean ANSI = true;
    public static boolean shortTitle = false;
    public static int DEBUG = 1;
    
    public static void main(String[] args) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            if (osName.contains("windows 8") || osName.contains("2012") || osName.contains("10")) {
                shortTitle = true;
            }
        }
        for (String arg : args) {
            switch (arg) {
                case "disable-ansi":
                    ANSI = false;
                    break;
            }
        }

        try {
            if (ANSI) {
                System.out.print((char) 0x1b + "]0;Starting BCProxy for network listening" + (char) 0x07);
            }
            Server server = new Server(PATH, DATA_PATH, PLUGIN_PATH);
        } catch (Exception e) {
        }
    }

}
