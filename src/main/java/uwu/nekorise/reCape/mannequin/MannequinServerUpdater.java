package uwu.nekorise.reCape.mannequin;

import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import uwu.nekorise.reCape.ReCape;

public class MannequinServerUpdater {
    public int getTask(Player owner, Mannequin mannequin) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (!owner.isValid() || !mannequin.isValid()) {
                    cancel();
                    return;
                }
                mannequin.setRotation(owner.getYaw(), owner.getPitch());
            }
        }.runTaskTimer(ReCape.getInstance(), 0L, 1L).getTaskId();
    }
}
