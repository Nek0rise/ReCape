package uwu.nekorise.reCape.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class LanguageDataStorage {
    @Getter private static String capeSetUsage = "";
    @Getter private static String capeSetSucc = "";
    @Getter private static String capeRemoveSucc = "";
    @Getter private static String capeRemoveFailed = "";
    @Getter private static String capeUsage = "";

    @Getter private static String reCapeRepackSucc = "";
    @Getter private static String reCapeReloadSucc = "";
    @Getter private static String reCapeUsage = "";

    @Getter private static String playerNotFound = "";
    @Getter private static String onlyForPlayers = "";
    @Getter private static String noPermission = "";
    @Getter private static String capeNotFound = "";

    public static void loadData() {
        try {
            FileConfiguration cfg = ConfigManager.getConfig("langs/" + MainDataStorage.getLanguage() + ".yml");

            capeSetUsage = cfg.getString("cape.set.usage");
            capeSetSucc = cfg.getString("cape.set.successfully");
            capeRemoveSucc = cfg.getString("cape.remove.successfully");
            capeRemoveFailed = cfg.getString("cape.remove.failed");
            capeUsage = cfg.getString("cape.usage");

            reCapeRepackSucc = cfg.getString("recape.repack.successfully");
            reCapeReloadSucc = cfg.getString("recape.reload.successfully");
            reCapeUsage = cfg.getString("recape.reload.usage");

            playerNotFound = cfg.getString("player-not-found");
            onlyForPlayers = cfg.getString("only-for-players");
            noPermission = cfg.getString("no-permission");
            capeNotFound = cfg.getString("cape-not-found");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
