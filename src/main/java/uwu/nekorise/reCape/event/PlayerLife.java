package uwu.nekorise.reCape.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import uwu.nekorise.reCape.mannequin.MannequinCreator;
import uwu.nekorise.reCape.mannequin.MannequinRegistry;

public class PlayerLife implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        MannequinRegistry.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e){
        new MannequinCreator().createSessionSafetly(e.getPlayer());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e){
        MannequinRegistry.remove(e.getPlayer().getName());
        new MannequinCreator().createSessionSafetly(e.getPlayer());
    }
}
