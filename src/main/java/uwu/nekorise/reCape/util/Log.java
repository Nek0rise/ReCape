package uwu.nekorise.reCape.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    public static void error(JavaPlugin caller, String msg)
    {
        Logger.getLogger("Minecraft").log(Level.SEVERE, "[" + caller.getName() + "] " + msg);
    }
    public static void warn(JavaPlugin caller, String msg)
    {
        Logger.getLogger("Minecraft").log(Level.WARNING, "[" + caller.getName() + "] " + msg);
    }
    public static void info(JavaPlugin caller, String msg)
    {
        Logger.getLogger("Minecraft").log(Level.INFO, "[" + caller.getName() + "] " + msg);
    }
    public static void debug(JavaPlugin caller, String msg)
    {
        Logger.getLogger("Minecraft").log(Level.WARNING, "[DEBUG] [" + caller.getName() + "] " + msg);
    }
}
