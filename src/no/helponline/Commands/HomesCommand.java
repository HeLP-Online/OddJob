package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HomesCommand implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            Bukkit.dispatchCommand(sender, cmd.getName() + " help");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.AQUA + "__-- HELP menu for /").append(cmd.getName()).append(" --__\n").append(ChatColor.AQUA + "-----------------------------------------\n");
            for (Args y : Args.values()) {
                if (sender.hasPermission("homes." + y.name())) {
                    sb.append(ChatColor.GOLD + "- Command:" + ChatColor.RESET + " /").append(cmd.getName()).append(" ").append(y.name()).append("\n").append(ChatColor.GOLD + "Description:" + ChatColor.RESET + " ").append(y.get()).append("\n");

                    if (sender instanceof Player) {
                        if (!args[0].equalsIgnoreCase("list")) {
                            sb.append(ChatColor.GOLD + "Usage:" + ChatColor.RESET + " /").append(cmd.getName()).append(" ").append(y.name()).append(" [name_of_home]\n");
                        } else {
                            sb.append(ChatColor.GOLD + "Usage:" + ChatColor.RESET + " /").append(cmd.getName()).append(" ").append(y.name()).append("\n");
                        }
                    }
                    if (sender.hasPermission("homes." + y.name() + ".others") &&
                            !args[0].equalsIgnoreCase("set")) {
                        if (!args[0].equalsIgnoreCase("list")) {
                            sb.append(ChatColor.GOLD + "Usage:" + ChatColor.RESET + " /").append(cmd.getName()).append(" ").append(y.name()).append(" <player> <name_of_home>\n");
                        } else {
                            sb.append(ChatColor.GOLD + "Usage:" + ChatColor.RESET + " /").append(cmd.getName()).append(" ").append(y.name()).append(" <player>\n");
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
                    if (OddJob.getInstance().getHomesManager().has(player.getUniqueId())) {
                        OddJob.getInstance().getHomesManager().del(player.getUniqueId());
                        OddJob.getInstance().getMessageManager().success("Home deleted.", sender);
                        return true;
                    }
                    OddJob.getInstance().getMessageManager().warning("No home found.", sender);
                    return true;
                }
            } else {
                if (args.length == 2) {
                    String name = args[1];
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        OddJob.getInstance().getHomesManager().del(player.getUniqueId(), name);
                        OddJob.getInstance().getMessageManager().success("Home " + name + " deleted.", sender);
                        return true;
                    }
                    OddJob.getInstance().getMessageManager().warning("No home named " + name + " found.", sender);
                    return true;
                }

                if (args.length == 3) {
                    UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                    String name = args[2];

                    if (uuid == null) {
                        OddJob.getInstance().getMessageManager().warning("Sorry, I can't find " + args[0], sender);
                        return true;
                    }

                    if (OddJob.getInstance().getHomesManager().has(uuid, name)) {
                        OddJob.getInstance().getHomesManager().del(uuid, name);
                        OddJob.getInstance().getMessageManager().success("Home " + name + " deleted from " + args[0], sender);
                        return true;
                    }
                    OddJob.getInstance().getMessageManager().warning("No home named " + name + " found at " + args[0], sender);
                    return true;
                }
            }
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 1) {
                    OddJob.getInstance().getHomesManager().add(player.getUniqueId(), player.getLocation());
                    OddJob.getInstance().getMessageManager().success("Home set!", sender);
                    return true;
                }

                if (args.length == 2) {
                    String name = args[1];
                    OddJob.getInstance().getHomesManager().add(player.getUniqueId(), name, player.getLocation());
                    OddJob.getInstance().getMessageManager().success("Home " + name + " set!", sender);
                    return true;
                }

                if (args.length == 3) {
                    String name = args[2];
                    UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                    if (uuid == null) {
                        OddJob.getInstance().getMessageManager().warning("Sorry, I can't find " + args[0], sender);
                        return true;
                    }
                    OddJob.getInstance().getHomesManager().add(uuid, name, player.getLocation());
                    OddJob.getInstance().getMessageManager().success("Home " + name + " set for " + args[0], sender);
                    return true;
                }
                OddJob.getInstance().getMessageManager().danger("Something went wrong!", sender);
                return true;
            }

            OddJob.getInstance().getMessageManager().danger("This command can only be done by a player!", sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("tp")) {
            Location location;
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 1) {
                    if (OddJob.getInstance().getHomesManager().has(player.getUniqueId())) {
                        location = OddJob.getInstance().getHomesManager().get(player.getUniqueId());
                        if (location != null) player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    } else {
                        OddJob.getInstance().getMessageManager().warning("No home found.", sender);
                    }
                    return true;
                }

                if (args.length == 2) {
                    String name = args[1];
                    if (OddJob.getInstance().getHomesManager().has(player.getUniqueId(), name)) {
                        location = OddJob.getInstance().getHomesManager().get(player.getUniqueId(), name);
                        if (location != null)
                            OddJob.getInstance().getTeleportManager().teleport(player, location, 0, PlayerTeleportEvent.TeleportCause.COMMAND);
                    } else {
                        OddJob.getInstance().getMessageManager().warning("Can't find any home named " + name, sender);
                    }
                    return true;
                }

                if (args.length == 3) {
                    UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                    if (uuid == null) {
                        OddJob.getInstance().getMessageManager().warning("Sorry, I can't find " + args[0], sender);
                        return true;
                    }
                    String name = args[2];
                    if (OddJob.getInstance().getHomesManager().has(uuid, name)) {
                        location = OddJob.getInstance().getHomesManager().get(uuid, name);
                        if (location != null) {
                            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            OddJob.getInstance().getMessageManager().success("Teleporting to home named `" + name + "` of " + args[1], sender);
                        } else {
                            OddJob.getInstance().getMessageManager().warning("Something went wrong!", sender);
                        }
                    } else {
                        OddJob.getInstance().getMessageManager().warning("Can't find any home named " + name, sender);
                    }
                    return true;
                }
                OddJob.getInstance().getMessageManager().danger("Too many arguments.", sender);
                return true;
            }

            OddJob.getInstance().getMessageManager().danger("This command can only be done by a player!", sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (sender instanceof Player && args.length == 1) {
                Player player = (Player) sender;
                StringBuilder string = new StringBuilder();

                string.append("List of your homes:\n");
                string.append("-------------------\n");

                List<String> homes = OddJob.getInstance().getHomesManager().list(player.getUniqueId());

                int i = 0;
                if (homes.size() > 0) {
                    for (String s : homes) {
                        i++;
                        string.append(i).append(".) ").append(s).append("\n");
                    }
                }

                sender.sendMessage(string.toString());
            }

            if (args.length == 2) {
                UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                if (uuid == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, I can't find " + args[0], sender);
                    return true;
                }
                StringBuilder string = new StringBuilder();

                string.append("List of your homes:\n");
                string.append("-------------------\n");

                List<String> homes = OddJob.getInstance().getHomesManager().list(uuid);

                int i = 1;
                for (String s : homes) {
                    string.append(i).append(".) ").append(s).append("\n");
                    i++;
                }

                sender.sendMessage(string.toString());
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] g) {
        List<String> list = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("homes")) {
            String[] st;
            if (g.length == 1 || g.length == 0) {
                st = new String[]{"tp", "set", "del", "list"};
                for (String t : st) {
                    if (t.startsWith(g[0])) {
                        list.add(t);
                    }
                }
            }
            if (g.length == 2 && commandSender.hasPermission(command.getName() + "." + g[0] + ".others")) {
                for (String playerName : OddJob.getInstance().getPlayerManager().getNames()) {
                    if (playerName.startsWith(g[1].toLowerCase())) {
                        list.add(playerName);
                    }
                }
            }

            if (commandSender instanceof Player && !g[0].equalsIgnoreCase("set") && !g[0].equalsIgnoreCase("list")) {
                Player player = (Player) commandSender;
                try {
                    for (String home : OddJob.getInstance().getHomesManager().list(player.getUniqueId())) {
                        if (home.toLowerCase().startsWith(g[1].toLowerCase())) {
                            list.add(home);
                        }
                    }
                } catch (Exception exception) {
                }
            }

            if (g.length == 3 &&
                    commandSender.hasPermission(command.getName() + "." + g[0] + ".others")) {
                try {
                    UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(g[1]);
                    for (String home : OddJob.getInstance().getHomesManager().list(uuid)) {
                        if (home.startsWith(g[2].toLowerCase())) {
                            list.add(home);
                        }
                    }
                } catch (Exception exception) {
                }
            }
        }

        Collections.sort(list);
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
