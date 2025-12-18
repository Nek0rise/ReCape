package uwu.nekorise.reCape.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MainDataStorage {
    @Getter
    @Setter
    private static List<String> capes = new ArrayList<>();

    @Getter
    private static String language = "en";

    public static void loadData() {
        try {
            FileConfiguration cfg = ConfigManager.getConfig("config.yml");
            language = cfg.getString("language");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
