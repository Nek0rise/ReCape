package uwu.nekorise.reCape.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import uwu.nekorise.reCape.mannequin.MannequinRegistry;

public class PlayerLeave implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        MannequinRegistry.remove(player.getName());
    }
}
