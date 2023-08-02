package de.keblex.bungeechat;

import commands.BungeeChatCmd;
import listener.TabEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import utils.BlockedCMDsConfig;
import utils.ChatConfig;
import utils.GeneralConfig;
import utils.LanguageConfig;

import java.awt.*;
import java.util.List;

import static utils.ConfigManager.gc;
import static utils.ConfigManager.lc;

public final class BungeeChat extends Plugin {

    private static String currentVersion = "1.0.0";
    private static PluginManager pm;

    @Override
    public void onEnable() {
        // Init config
        GeneralConfig.setup(this);

        // Check if config is up to date
        if (!gc.getString("version").equals(currentVersion)) {
            getLogger().warning("Config is not up to date. Please update it.");
            getLogger().warning("Current version: " + gc.getString("version"));
            getLogger().warning("Latest version: " + currentVersion);
            getLogger().warning("Disabling plugin...");

            getProxy().getPluginManager().unregisterListeners(this);
            getProxy().getPluginManager().unregisterCommands(this);
        } else {
            // Init all other configs
            initConfigs();
            registerListeners();
            registerCommands();

            // Send message for loaded
            getLogger().info(ChatColor.translateAlternateColorCodes('&', lc.getString("system.plugin-loaded")));
        }
    }

    private void initConfigs() {
        // Init language config
        if (gc.getString("language-path") == "") {
            LanguageConfig.setup(this, getDataFolder().getPath() + "/language.yml");
        } else {
            LanguageConfig.setup(this, gc.getString("language-path"));
        }

        // Init blocked commands config
        if (gc.getBoolean("command-blocker")) {
            if (gc.getString("command-blocklist-path") == "") {
                BlockedCMDsConfig.setup(this, getDataFolder().getPath() + "/blocklistcmds.yml");
            } else {
                BlockedCMDsConfig.setup(this, gc.getString("command-blocklist-path"));
            }
        }

        // Init Chat config
        if (gc.getBoolean("chat-system")) {
            if (gc.getString("chat-path") == "") {
                ChatConfig.setup(this, getDataFolder().getPath() + "/chat.yml");
            } else {
                ChatConfig.setup(this, gc.getString("chat-path"));
            }
        }

        getLogger().info(trns(lc.getString("system.plugin-configs-loaded")));
    }

    private void registerListeners() {
        // Register command listener
        pm = getProxy().getPluginManager();
        getProxy().getPlayer("abc").getServer()-

        if (gc.getBoolean("command-blocker")) {
            pm.registerListener(this, new listener.CommandEvent());
            pm.registerListener(this, new TabEvent());
            getLogger().info(ChatColor.translateAlternateColorCodes('&', lc.getString("system.command-blocker-enabled")));
        }

        getLogger().info(trns(lc.getString("system.plugin-listener-registered")));
    }

    private void registerCommands() {
        if (gc.getBoolean("plugin-command")) {
            String[] aliases = gc.getStringList("command-aliases").toArray(new String[0]);
            pm.registerCommand(this, new BungeeChatCmd(gc.getString("command-name"), gc.getString("command-permission"), aliases));
            getLogger().info(trns(lc.getString("system.plugin-command-enabled")));
        }

        getLogger().info(trns(lc.getString("system.plugin-commands-registered")));
    }

    @Override
    public void onDisable() {
        // Send message for unloaded
        getLogger().info(trns(lc.getString("system.plugin-unloaded")));
    }

    public static PluginManager getPluginManager() {
        return pm;
    }

    private String trns(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
