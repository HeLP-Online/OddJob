package no.helponline.Commands;

import no.helponline.Managers.HomesManager;
import no.helponline.Managers.MessageManager;
import no.helponline.Managers.PlayerManager;
import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class HomesCommand implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            Bukkit.dispatchCommand(sender, cmd.getName() + " help");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.DARK_RED + "__-- HELP menu for /" + cmd.getName() + " --__\n");
            sb.append(ChatColor.AQUA + "-----------------------------------------\n");
            for (Args y : Args.values()) {
                if (sender.hasPermission("homes." + y.name())) {
                    sb.append(ChatColor.GOLD + "- Command:" + ChatColor.RESET + " /" + cmd.getName() + " " + y.name() + "\n");
                    sb.append(ChatColor.GOLD + "Description:" + ChatColor.RESET + " " + y.get() + "\n");

                    if (sender instanceof Player) {
                        if (!args[0].equalsIgnoreCase("list")) {
                            sb.append(ChatColor.GOLD + "Usage:" + ChatColor.RESET + " /" + cmd.getName() + " " + y.name() + " [name_of_home]\n");
                        } else {
                            sb.append(ChatColor.GOLD + "Usage:" + ChatColor.RESET + " /" + cmd.getName() + " " + y.name() + "\n");
                        }
                    }
                    if (sender.hasPermission("homes." + y.name() + ".others") &&
                            !args[0].equalsIgnoreCase("set")) {
                        if (!args[0].equalsIgnoreCase("list")) {
                            sb.append(ChatColor.GOLD + "Usage:" + ChatColor.RESET + " /" + cmd.getName() + " " + y.name() + " <player> <name_of_home>\n");
                        } else {
                            sb.append(ChatColor.GOLD + "Usage:" + ChatColor.RESET + " /" + cmd.getName() + " " + y.name() + " <player>\n");
                        }
                    }
                }
            }

            sender.sendMessage(sb.toString());
            return true;
        }
        if (args[0].equalsIgnoreCase("del")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (HomesManager.has(player.getUniqueId())) {
                        HomesManager.del(player.getUniqueId());
                        MessageManager.success("Home deleted.", sender);
                        return true;
                    }
                    MessageManager.warning("No home found.", sender);
                    return true;
                }
            } else {
                if (args.length == 2) {
                    String name = args[1];
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        HomesManager.del(player.getUniqueId(), name);
                        MessageManager.success("Home " + name + " deleted.", sender);
                        return true;
                    }
                    MessageManager.warning("No home named " + name + " found.", sender);
                    return true;
                }

                if (args.length == 3) {
                    UUID uuid = PlayerManager.getUUID(args[1]);
                    String name = args[2];

                    if (uuid == null) {
                        MessageManager.warning("Sorry, I can't find " + args[0], sender);
                        return true;
                    }

                    if (HomesManager.has(uuid, name)) {
                        HomesManager.del(uuid, name);
                        MessageManager.success("Home " + name + " deleted from " + args[0], sender);
                        return true;
                    }
                    MessageManager.warning("No home named " + name + " found at " + args[0], sender);
                    return true;
                }
            }
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 1) {
                    HomesManager.add(player.getUniqueId(), player.getLocation());
                    MessageManager.success("Home set!", sender);
                    return true;
                }

                if (args.length == 2) {
                    String name = args[1];
                    HomesManager.add(player.getUniqueId(), name, player.getLocation());
                    MessageManager.success("Home " + name + " set!", sender);
                    return true;
                }

                if (args.length == 3) {
                    String name = args[2];
                    UUID uuid = PlayerManager.getUUID(args[1]);
                    if (uuid == null) {
                        MessageManager.warning("Sorry, I can't find " + args[0], sender);
                        return true;
                    }
                    HomesManager.add(uuid, name, player.getLocation());
                    MessageManager.success("Home " + name + " set for " + args[0], sender);
                    return true;
                }
                MessageManager.danger("Something went wrong!", sender);
                return true;
            }

            MessageManager.danger("This command can only be done by a player!", sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("tp")) {
            Location loc;
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 1) {
                    if (HomesManager.has(player.getUniqueId())) {
                        loc = HomesManager.get(player.getUniqueId());
                        if (loc != null) player.teleport(loc);
                    } else {
                        MessageManager.warning("No home found.", sender);
                    }
                    return true;
                }

                if (args.length == 2) {
                    String name = args[1];
                    if (HomesManager.has(player.getUniqueId(), name)) {
                        loc = HomesManager.get(player.getUniqueId(), name);
                        if (loc != null) player.teleport(loc);
                    } else {
                        MessageManager.warning("Can't find any home named " + name, sender);
                    }
                    return true;
                }

                if (args.length == 3) {
                    UUID uuid = PlayerManager.getUUID(args[1]);
                    if (uuid == null) {
                        MessageManager.warning("Sorry, I can't find " + args[0], sender);
                        return true;
                    }
                    String name = args[2];
                    if (HomesManager.has(player.getUniqueId(), name)) {
                        loc = HomesManager.get(player.getUniqueId(), name);
                        if (loc != null) {
                            player.teleport(loc);
                            MessageManager.success("Teleporting to home named `" + name + "` of " + args[1], sender);
                        } else {
                            MessageManager.warning("Something went wrong!", sender);
                        }
                    } else {
                        MessageManager.warning("Can't find any home named " + name, sender);
                    }
                    return true;
                }
                MessageManager.danger("Too many arguments.", sender);
                return true;
            }

            MessageManager.danger("This command can only be done by a player!", sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (sender instanceof Player && args.length == 1) {
                Player player = (Player) sender;
                StringBuilder string = new StringBuilder();

                string.append("List of your homes:\n");
                string.append("-------------------\n");

                Set<String> homes = HomesManager.list(player.getUniqueId());

                int i = 1;
                for (String s : homes) {
                    string.append(i + ") " + s + "\n");
                    i++;
                }

                sender.sendMessage(string.toString());
            }

            if (args.length == 2) {
                UUID uuid = PlayerManager.getUUID(args[1]);
                if (uuid == null) {
                    MessageManager.warning("Sorry, I can't find " + args[0], sender);
                    return true;
                }
                StringBuilder string = new StringBuilder();

                string.append("List of your homes:\n");
                string.append("-------------------\n");

                Set<String> homes = HomesManager.list(uuid);

                int i = 1;
                for (String s : homes) {
                    string.append(i + ") " + s + "\n");
                    i++;
                }

                sender.sendMessage(string.toString());
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] g) {
        List<String> list = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("homes") && commandSender.hasPermission(command.getName())) {
            if (g.length == 1) {
                for (Args a : Args.values()) {
                    if (a.name().startsWith(g[0].toLowerCase())) {
                        list.add(a.toString());
                    }
                }
            }
            if (g.length == 2 &&
                    commandSender.hasPermission(command.getName() + "." + g[0] + ".others")) {
                for (String player : PlayerManager.getPlayersMap().values()) {
                    if (player.startsWith(g[1].toLowerCase())) {
                        list.add(player);
                    }
                }
            }

            if (commandSender instanceof Player && !g[0].equalsIgnoreCase("set") && !g[0].equalsIgnoreCase("list")) {
                Player player = (Player) commandSender;
                try {
                    for (String home : HomesManager.list(player.getUniqueId())) {
                        if (home.startsWith(g[1].toLowerCase())) {
                            list.add(home);
                        }
                    }
                } catch (Exception exception) {
                }
            }

            if (g.length == 3 &&
                    commandSender.hasPermission(command.getName() + "." + g[0] + ".others")) {
                try {
                    UUID uuid = PlayerManager.getUUID(g[1]);
                    for (String home : HomesManager.list(uuid)) {
                        if (home.startsWith(g[2].toLowerCase())) {
                            list.add(home);
                        }
                    }
                } catch (Exception exception) {
                }
            }
        }

        Collections.sort(list);
        OddJob.getInstance().log(list.toString());
        return list;
    }

    enum Args {
        tp("Teleport you to your home"),
        list("Lists your homes"),
        set("Sets a new home"),
        del("Deletes a home");

        private String s;

        Args(String s) {
            this.s = s;
        }

        public String get() {
            return this.s;
        }
    }
}
