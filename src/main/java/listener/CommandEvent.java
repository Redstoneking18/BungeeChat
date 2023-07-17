package listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import utils.BlockedCMDsConfig;

public class CommandEvent implements Listener {
    @EventHandler
    public void onCommand(ChatEvent event) {
        if (!event.getMessage().startsWith("/")) return;

        String command = event.getMessage().replace("/", "").toLowerCase();
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        /* Block Command if not alias */
        if(BlockedCMDsConfig.getConfig().getKeys().contains(command)) {
            if(BlockedCMDsConfig.getConfig().getString(command+".bypass-permission")=="" || !player.hasPermission(BlockedCMDsConfig.getConfig().getString(command + ".bypass-permission"))) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', BlockedCMDsConfig.getConfig().getString(command+".message")));
            }
        } else if((BlockedCMDsConfig.getConfig().getKeys().contains(command.split(" ")[0]) && BlockedCMDsConfig.getConfig().getBoolean(event.getMessage().replace("/", "").split(" ")[0]+".include-subcommands"))) {
            command = command.split(" ")[0];
            if(BlockedCMDsConfig.getConfig().getString(command+".bypass-permission")=="" || !player.hasPermission(BlockedCMDsConfig.getConfig().getString(command + ".bypass-permission"))) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', BlockedCMDsConfig.getConfig().getString(command+".message")));
            }
        } else {
            for (String key : BlockedCMDsConfig.getConfig().getKeys()) {
                if(BlockedCMDsConfig.getConfig().getStringList(key+".aliases").contains(command)) {
                    if(BlockedCMDsConfig.getConfig().getString(key+".bypass-permission")=="" || !player.hasPermission(BlockedCMDsConfig.getConfig().getString(command + ".bypass-permission"))) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', BlockedCMDsConfig.getConfig().getString(key+".message")));
                    }
                }
            }
        }
    }
}
