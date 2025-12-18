package uwu.nekorise.reCape;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.nekorise.reCape.command.CapeCommand;
import uwu.nekorise.reCape.command.CapeTabCompleter;
import uwu.nekorise.reCape.command.ReCapeCommand;
import uwu.nekorise.reCape.command.ReCapeTabCompleter;
import uwu.nekorise.reCape.config.ConfigManager;
import uwu.nekorise.reCape.database.CapesDatabase;
import uwu.nekorise.reCape.event.PlayerJoin;
import uwu.nekorise.reCape.event.PlayerLeave;
import uwu.nekorise.reCape.event.PlayerLife;
import uwu.nekorise.reCape.mannequin.MannequinRegistry;
import uwu.nekorise.reCape.packet.MannequinPacketListener;
import uwu.nekorise.reCape.recourcepack.RecourcepackBuilder;
import lombok.Getter;

public final class ReCape extends JavaPlugin {
    @Getter
    private static ReCape instance;
    @Getter
    private CapesDatabase database;

    @Override
    public void onEnable() {
        instance = this;
        PacketEvents.getAPI().init();

        ConfigManager.loadConfig();
        registerCommands();
        registerTabCompleters();
        registerListeners();

        RecourcepackBuilder rpBuilder = new RecourcepackBuilder();
        rpBuilder.buildAsync(null);
        database = new CapesDatabase(instance);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        MannequinRegistry.removeAll();
        database.close();
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(instance));
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getEventManager().registerListener(new MannequinPacketListener(), PacketListenerPriority.HIGH);
    }

    private void registerCommands() {
        getCommand("cape").setExecutor(new CapeCommand());
        getCommand("recape").setExecutor(new ReCapeCommand());

    }

    private void registerTabCompleters() {
        getCommand("cape").setTabCompleter(new CapeTabCompleter());
        getCommand("recape").setTabCompleter(new ReCapeTabCompleter());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerLeave(), instance);
        pm.registerEvents(new PlayerJoin(), instance);
        pm.registerEvents(new PlayerLife(), instance);
    }
}
