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

import java.util.*;

public class HomesCommand implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            Bukkit.dispatchCommand(sender, cmd.getName() + " help");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            return true;
        }
        if (args[0].equalsIgnoreCase("del")) {
            if (args.length == 2) {
                String name = args[1];
                UUID uuid = ((Player) sender).getUniqueId();
                if (!OddJob.getInstance().getHomesManager().has(uuid, name)) {
                    OddJob.getInstance().getMessageManager().errorHome(name, sender);
                    return true;
                }
                OddJob.getInstance().getHomesManager().del(uuid, name);
                OddJob.getInstance().getMessageManager().success("Home " + ChatColor.GOLD + name + ChatColor.GREEN + " deleted", sender, true);
                return true;
            }

            // Command /homes del <player> <name>
            if (args.length == 3) {
                UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                String name = args[2];

                if (uuid == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(args[1], sender);
                    return true;
                }

                if (!OddJob.getInstance().getHomesManager().has(uuid, name)) {
                    OddJob.getInstance().getMessageManager().errorHome(name, sender);
                    return true;
                }

                OddJob.getInstance().getHomesManager().del(uuid, name);
                OddJob.getInstance().getMessageManager().success("Home " + ChatColor.GOLD + name + ChatColor.GREEN + " deleted from " + ChatColor.DARK_AQUA + args[1], sender, true);
                return true;
            }
        }


        if (args[0].equalsIgnoreCase("set")) {
            if (!(sender instanceof Player)) {
                OddJob.getInstance().getMessageManager().errorConsole();
                return true;
            }
            Player player = (Player) sender;
            // Command /homes set <name>
            if (args.length == 2) {
                String name = args[1];
                OddJob.getInstance().getHomesManager().add(player.getUniqueId(), name, player.getLocation());
                OddJob.getInstance().getMessageManager().success("Home " + ChatColor.GOLD + name + ChatColor.GREEN + " set!", sender, true);
                return true;
            }

            // Command /homes set <player> <name>
            if (args.length == 3) {
                String name = args[2];
                UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                if (uuid == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(args[1], sender);
                    return true;
                }

                OddJob.getInstance().getHomesManager().add(uuid, name, player.getLocation());
                OddJob.getInstance().getMessageManager().success("Home " + ChatColor.GOLD + name + ChatColor.GREEN + " set for " + ChatColor.DARK_AQUA + args[1], sender, true);
                return true;
            }
            OddJob.getInstance().getMessageManager().danger("Something went wrong!", sender, false);
            return true;

        }

        if (args[0].equalsIgnoreCase("tp")) {
            if (!(sender instanceof Player)) {
                OddJob.getInstance().getMessageManager().errorConsole();
                return true;
            }

            Location location;
            Player player = (Player) sender;

            // Command /homes tp <name>
            if (args.length == 2) {
                String name = args[1];
                if (!OddJob.getInstance().getHomesManager().has(player.getUniqueId(), name)) {
                    OddJob.getInstance().getMessageManager().errorHome(name, player);
                    return true;
                }

                location = OddJob.getInstance().getHomesManager().get(player.getUniqueId(), name);

                if (location == null) {
                    OddJob.getInstance().getMessageManager().danger("Something went wrong!", sender, false);
                    return true;
                }

                OddJob.getInstance().getTeleportManager().teleport(player, location, 0, PlayerTeleportEvent.TeleportCause.COMMAND);
                return true;
            }

            if (args.length == 3) {
                // Command /homes tp <player> <name>
                UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                if (uuid == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(args[1], sender);
                    return true;
                }

                String name = args[2];
                if (!OddJob.getInstance().getHomesManager().has(uuid, name)) {
                    OddJob.getInstance().getMessageManager().errorHome(name, player);
                    return true;
                }
                location = OddJob.getInstance().getHomesManager().get(uuid, name);
                if (location == null) {
                    OddJob.getInstance().getMessageManager().danger("Something went wrong!", sender, false);
                    return true;
                }

                OddJob.getInstance().getTeleportManager().teleport(player, location, 0, PlayerTeleportEvent.TeleportCause.COMMAND);
                OddJob.getInstance().getMessageManager().success("Teleporting to home named `" + ChatColor.GOLD + name + ChatColor.GREEN + "` of " + args[1], sender, true);
                return true;
            }

            OddJob.getInstance().getMessageManager().danger("Something went wrong!", sender, false);
            return true;
        }
        if (args[0].equalsIgnoreCase("save")) {
            OddJob.getInstance().getHomesManager().save();
        }
        if (args[0].equalsIgnoreCase("load")) {
            OddJob.getInstance().getHomesManager().load();
        }

        if (args[0].equalsIgnoreCase("list")) {
            // Command /homes list
            if (sender instanceof Player && args.length == 1) {
                Player player = (Player) sender;
                StringBuilder string = new StringBuilder();

                string.append("List of your homes:\n");
                string.append("-------------------\n");

                Set<String> homes = OddJob.getInstance().getHomesManager().list(player.getUniqueId());

                int i = 0;
                if (homes.size() > 0) {
                    for (String s : homes) {
                        i++;
                        string.append(i).append(".) ").append(ChatColor.GOLD).append(s).append(ChatColor.RESET).append("\n");
                    }
                }

                sender.sendMessage(string.toString());
            }

            if (args.length == 2) {
                UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                if (uuid == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(args[1], sender);
                    return true;
                }

                StringBuilder string = new StringBuilder();

                string.append("List of your homes:\n");
                string.append("-------------------\n");

                Set<String> homes = OddJob.getInstance().getHomesManager().list(uuid);

                int i = 1;
                for (String s : homes) {
                    string.append(i).append(".) ").append(ChatColor.GOLD).append(s).append(ChatColor.RESET).append("\n");
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
                String name = commandSender.getName();
                for (String playerName : OddJob.getInstance().getPlayerManager().getNames()) {
                    if (playerName.toLowerCase().startsWith(g[1].toLowerCase()) && !playerName.equals(name)) {
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
