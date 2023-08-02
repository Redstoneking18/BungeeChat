package utils;

import de.keblex.bungeechat.BungeeChat;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

public class BlockedCMDsConfig {

    private static File file;
    private static Configuration config;

    public static void setup(BungeeChat plugin, String path) {
        String folder = String.join("/", Arrays.copyOf(path.split("/"), path.split("/").length - 1));
        String child = path.split("/")[path.split("/").length - 1];


        if (!new File(folder).exists()) {
            new File(folder).mkdir();
        }

        file = new File(folder, child);

        if (!file.exists()) {
            try (InputStream in = plugin.getResourceAsStream("blocklistcmds.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            ConfigManager.bc = config;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getter
    public static Configuration getConfig() {
        return config;
    }

    // Save
    public static void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Reload
    public static void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            ConfigManager.bc = config;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
