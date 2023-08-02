package listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import utils.BlockedCMDsConfig;

import static utils.ConfigManager.bc;

public class CommandEvent implements Listener {
    @EventHandler
    public void onCommand(ChatEvent event) {
        if (!event.getMessage().startsWith("/")) return;

        String command = event.getMessage().replace("/", "").toLowerCase() + " ";
        Configuration config = BlockedCMDsConfig.getConfig();

        for (String key : bc.getKeys()) {
            if (command.startsWith(key + " ")) {
                foundCommand(key, event);
            } else {
                Boolean found = false;
                for (String alias : config.getSection(key).getStringList("aliases")) {
                    if (command.startsWith(alias + " ")) {
                        found = true;
                    }
                }

                if (found) foundCommand(key, event);
            }
        }
    }

    private void foundCommand(String key, ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String perm = bc.getString(key + ".bypass-permission");
        if (perm == "" || !player.hasPermission(perm)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', bc.getString(key + ".message")));
        }
    }
}
