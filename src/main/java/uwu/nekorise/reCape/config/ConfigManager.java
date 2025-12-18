package uwu.nekorise.reCape.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uwu.nekorise.reCape.ReCape;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    private final static String path = "./plugins/" + ReCape.getInstance().getName() + "/";
    private final static String[] languageFiles = {"ru.yml", "en.yml"};

    public static FileConfiguration getConfig(String cfgName) throws IOException, InvalidConfigurationException {
        FileConfiguration fileConfiguration = new YamlConfiguration();
        fileConfiguration.options().parseComments(true);
        fileConfiguration.load(path + cfgName);

        return fileConfiguration;
    }

    private static void createConfigs() {
        File defaultConfig = new File(path, "config.yml");
        if (!defaultConfig.exists())
        {
            ReCape.getInstance().saveResource("config.yml", false);
        }

        List<String> langConfigFiles = new ArrayList<>(Arrays.asList(languageFiles));
        for (String langConfigName : langConfigFiles)
        {
            File langConfig = new File(path, "langs/" + langConfigName);
            if (!langConfig.exists())
            {
                ReCape.getInstance().saveResource("langs/" + langConfigName, false);
            }
        }
    }

    public static void loadConfig() {
        createConfigs();
        MainDataStorage.loadData();
        LanguageDataStorage.loadData();
    }
}
