package uwu.nekorise.reCape.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uwu.nekorise.reCape.mannequin.MannequinCreator;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        new MannequinCreator().createSessionSafetly(e.getPlayer());
    }
}
