package de.keblex.bungeechat;

import com.sun.tools.javac.jvm.Gen;
import net.md_5.bungee.api.plugin.Plugin;
import utils.GeneralConfig;
import utils.LanguageConfig;

public final class BungeeChat extends Plugin {

    @Override
    public void onEnable() {
        // Init config
        GeneralConfig.setup(this);

        // Check if config is up to date
        if(GeneralConfig.getConfig().getString("version")!="1.0.0") {
            getLogger().warning("Config is not up to date. Please update it.");
            getLogger().warning("Disabling plugin...");

            getProxy().getPluginManager().unregisterListeners(this);
            getProxy().getPluginManager().unregisterCommands(this);
        } else {
            // Init lanuage config
            if(GeneralConfig.getConfig().getString("language-path")=="") {
                LanguageConfig.setup(this, getDataFolder().getPath()+"/language.yml");
            } else {
                LanguageConfig.setup(this, GeneralConfig.getConfig().getString("language-path"));
            }

            // Send message for loaded
            getLogger().info(LanguageConfig.getConfig().getString("messages.plugin-loaded"));
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(LanguageConfig.getConfig().getString("messages.plugin-unloaded"));
    }
}
