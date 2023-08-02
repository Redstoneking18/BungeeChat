package commands;

import com.sun.tools.javac.jvm.Gen;
import de.keblex.bungeechat.BungeeChat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import utils.BlockedCMDsConfig;
import utils.LanguageConfig;

import java.util.ArrayList;
import java.util.List;

import static utils.ConfigManager.gc;
import static utils.ConfigManager.lc;

public class BungeeChatCmd extends Command implements TabExecutor {

    public BungeeChatCmd(String name, String permission, String... aliases) {
        super(name, permission, aliases);
        if (lc.getString("command.permission") != "")
            setPermissionMessage(ChatColor.translateAlternateColorCodes('&', lc.getString("command.permission")));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermission())) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("info")) {
                    if (sender.hasPermission(getPermission() + ".info")) {
                        if (args.length == 1) {
                            for (String message : lc.getStringList("command.info.message")) {
                                message = message.replaceAll("%version%", gc.getString("version"));
                                message = message.replaceAll("%functions_enabled%", (((gc.getBoolean("command-blocker")) ? "Command Blocker, " : "") + ((gc.getBoolean("plugin-command")) ? "Plugin Command, " : "") + ""));
                                message = message.replaceAll("%functions_disabled%", (gc.getBoolean("command-blocker") ? "" : "Command Blocker, ") + (gc.getBoolean("plugin-command") ? "" : "Plugin Command, "));

                                String[] splitConfigMessage = message.split("%config:");
                                message = splitConfigMessage[0];
                                for (int i = 1; i < splitConfigMessage.length; i++) {
                                    String[] msgParts = splitConfigMessage[i].split("%", 2);
                                    message += gc.get(msgParts[0]);
                                    message += msgParts[1];
                                }

                                String[] splitLanguageMessage = message.split("%language:");
                                message = splitLanguageMessage[0];
                                for (int i = 1; i < splitLanguageMessage.length; i++) {
                                    String[] msgParts = splitLanguageMessage[i].split("%", 2);
                                    message += lc.get(msgParts[0]);
                                    message += msgParts[1];
                                }

                                sender.sendMessage(trns(message));
                            }
                        } else {
                            for (String msg : lc.getString("command.info.usage").split("&n")) {
                                sender.sendMessage(trns(msg));
                            }
                        }
                    } else {
                        sender.sendMessage((lc.getString("command.subcommand-permission") == "") ? getPermission() : trns(lc.getString("command.subcommand-permission")));
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission(getPermission() + ".reload")) {
                        if (args.length == 1) {
                            BungeeChat.getPluginManager().getPlugin("BungeeChat").onDisable();
                            BungeeChat.getPluginManager().getPlugin("BungeeChat").onEnable();
                            sender.sendMessage(trns(lc.getString("command.reload.message.all")));
                        } if(args.length == 2) {
                            if(args[1].equalsIgnoreCase("language") && sender.hasPermission(getPermission()+".reload.language")) {
                                LanguageConfig.reloadConfig();
                                sender.sendMessage(trns(lc.getString("command.reload.message.language")));
                            } else if(args[1].equalsIgnoreCase("commands") && sender.hasPermission(getPermission()+".reload.commands")) {
                                BlockedCMDsConfig.reloadConfig();
                                sender.sendMessage(trns(lc.getString("command.reload.message.commands")));
                            } else {
                                for (String msg : lc.getString("command.reload.usage").split("&n")) {
                                    sender.sendMessage(trns(msg));
                                }
                            }
                        } else {
                            for (String msg : lc.getString("command.reload.usage").split("&n")) {
                                sender.sendMessage(trns(msg));
                            }
                        }
                    } else {
                        sender.sendMessage((lc.getString("command.subcommand-permission") == "") ? getPermission() : trns(lc.getString("command.subcommand-permission")));
                    }
                } else {
                    for (String key : lc.getSection("command.usage").getKeys()) {
                        if (key == "default" || sender.hasPermission(getPermission() + "." + key)) {
                            String[] description = lc.getString("command.usage." + key).split("&n");
                            for (String message : description) {
                                sender.sendMessage(trns(message));
                            }
                        }
                    }
                }
            } else {
                for (String key : lc.getSection("command.usage").getKeys()) {
                    if (key == "default" || sender.hasPermission(getPermission() + "." + key)) {
                        String[] description = lc.getString("command.usage." + key).split("&n");
                        for (String message : description) {
                            sender.sendMessage(trns(message));
                        }
                    }
                }
            }
        } else sender.sendMessage(getPermissionMessage());
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> tabs = new ArrayList<String>();
        if (sender.hasPermission(getPermission())) {
            if (args.length == 1) {
                if (sender.hasPermission(getPermission() + ".info")) tabs.add("info");
                if (sender.hasPermission(getPermission() + ".reload")) tabs.add("reload");
            } else if(args.length==2 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission(getPermission() + ".reload.language")) tabs.add("language");
                if (sender.hasPermission(getPermission() + ".reload.commands")) tabs.add("commands");
            }
        }

        return tabs;
    }

    private String trns(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
