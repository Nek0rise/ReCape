package uwu.nekorise.reCape.mannequin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uwu.nekorise.reCape.ReCape;
import uwu.nekorise.reCape.config.MainDataStorage;
import uwu.nekorise.reCape.database.CapesDatabase;
import uwu.nekorise.reCape.entity.CapeEntity;

public class MannequinCreator {
    public void createSessionSafetly(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(ReCape.getInstance(), () -> {
            CapesDatabase database = ReCape.getInstance().getDatabase();
            CapeEntity capeEntity = database.getByUsername(player.getName());

            if (capeEntity == null) return;

            String capeType = capeEntity.getCapeType();

            if (!MainDataStorage.getCapes().contains(capeType.toLowerCase())) return;

            Bukkit.getScheduler().runTask(ReCape.getInstance(), () -> {
                if (!player.isOnline()) return;

                MannequinSession session = new MannequinSession(player, capeType);
                MannequinRegistry.register(session);
            });
        });
    }
}
