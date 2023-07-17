package de.keblex.bungeechat;

import com.sun.tools.javac.jvm.Gen;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import utils.BlockedCMDsConfig;
import utils.GeneralConfig;
import utils.LanguageConfig;

public final class BungeeChat extends Plugin {

    private static String currentVersion = "1.0.0";

    @Override
    public void onEnable() {
        // Init config
        GeneralConfig.setup(this);

        // Check if config is up to date
        if(!GeneralConfig.getConfig().getString("version").equals(currentVersion)) {
            getLogger().warning("Config is not up to date. Please update it.");
            getLogger().warning("Current version: "+GeneralConfig.getConfig().getString("version"));
            getLogger().warning("Latest version: "+currentVersion);
            getLogger().warning("Disabling plugin...");

            getProxy().getPluginManager().unregisterListeners(this);
            getProxy().getPluginManager().unregisterCommands(this);
        } else {
            // Init all other configs
            initConfigs();
            registerListeners();

            // Send message for loaded
            getLogger().info(ChatColor.translateAlternateColorCodes('&', LanguageConfig.getConfig().getString("system.plugin-loaded")));
        }
    }

    private void initConfigs() {
        // Init language config
        if(GeneralConfig.getConfig().getString("language-path")=="") {
            LanguageConfig.setup(this, getDataFolder().getPath()+"/language.yml");
        } else {
            LanguageConfig.setup(this, GeneralConfig.getConfig().getString("language-path"));
        }

        // Init blocked commands config
        if(GeneralConfig.getConfig().getBoolean("command-blocker")) {
            if(GeneralConfig.getConfig().getString("command-blocklist-path")=="") {
                BlockedCMDsConfig.setup(this, getDataFolder().getPath()+"/blocklistcmds.yml");
            } else {
                BlockedCMDsConfig.setup(this, GeneralConfig.getConfig().getString("command-blocklist-path"));
            }
        }
    }

    private void registerListeners() {
        // Register command listener
        if(GeneralConfig.getConfig().getBoolean("command-blocker")) {
            getProxy().getPluginManager().registerListener(this, new listener.CommandEvent());
            getLogger().info(ChatColor.translateAlternateColorCodes('&',LanguageConfig.getConfig().getString("system.command-blocker-enabled")));
        }

        getLogger().info(ChatColor.translateAlternateColorCodes('&', LanguageConfig.getConfig().getString("system.plugin-listener-registered")));
    }

    @Override
    public void onDisable() {
        // Send message for unloaded
        getLogger().info(ChatColor.translateAlternateColorCodes('&', LanguageConfig.getConfig().getString("system.plugin-unloaded")));
    }
}
