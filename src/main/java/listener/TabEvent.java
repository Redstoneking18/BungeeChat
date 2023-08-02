package listener;

import de.keblex.bungeechat.BungeeChat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import utils.BlockedCMDsConfig;

import java.util.*;

public class TabEvent implements Listener {
    private HashMap<UUID, String> currentCommand = new HashMap<UUID, String>();

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        ProxiedPlayer p = (ProxiedPlayer) event.getSender();
        currentCommand.put(p.getUniqueId(), event.getCursor());
    }

    @EventHandler
    public void onTabCompleteResponse(TabCompleteResponseEvent event) {
        ProxiedPlayer p = (ProxiedPlayer) event.getReceiver();

        if (!currentCommand.containsKey(p.getUniqueId())) return;

        String command = currentCommand.get(p.getUniqueId()).replace("/", "").toLowerCase();
        if (!command.endsWith(" ")) {
            String[] commandParts = command.split(" ");
            command = "";
            for (int i = 0; i < commandParts.length - 1; i++) {
                command += commandParts[i] + " ";
            }
        }

        Configuration config = BlockedCMDsConfig.getConfig();
        List<String> suggestions = event.getSuggestions();

        for (String key : config.getKeys()) {
            if (checkCommand(command, key, suggestions)) {
                String perm = config.getString(key + ".bypass-permission");
                if (perm == "" || !p.hasPermission(perm)) foundCommand(key, command, config, p, event);
            } else {
                Boolean found = false;
                String aliasFound = "";
                for (String alias : config.getSection(key).getStringList("aliases")) {
                    if (checkCommand(command, alias, suggestions)) {
                        found = true;
                        aliasFound = alias;
                    }
                }

                String perm = config.getString(key + ".bypass-permission");
                if (found && (perm == "" || !p.hasPermission(perm)))
                    foundCommand(aliasFound, command, config, p, event);
            }
        }
    }

    private Boolean checkCommand(String command, String key, List<String> suggestions) {
        if ((command + " ").startsWith(key + " ")) return true;
        if (key.contains(" ")) {
            String[] keyArray = key.split(" ").clone();
            if (suggestions.contains(keyArray[keyArray.length - 1])) {
                if ((command + keyArray[keyArray.length - 1]).startsWith(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void foundCommand(String key, String command, Configuration config, ProxiedPlayer player, TabCompleteResponseEvent event) {
        if (command.startsWith(key)) {
            event.getSuggestions().clear();
        } else {
            String[] keyArray = key.split(" ").clone();
            event.getSuggestions().remove(keyArray[keyArray.length - 1]);
            currentCommand.remove(player.getUniqueId());
        }
    }
}
