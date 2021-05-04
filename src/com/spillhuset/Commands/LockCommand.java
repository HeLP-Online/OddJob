package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LockCommand implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("locks")) {
            if (args.length == 0) {
                Bukkit.getServer().dispatchCommand(sender, cmd.getName() + " help");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ChatColor.DARK_RED + "__-- HELP menu for ").append(cmd.getName()).append(" --__\n").append(ChatColor.AQUA + "-----------------------------------------\n");
                    for (Args y : Args.values()) {
                        if (sender.hasPermission("locks." + y.name())) {
                            sb.append(ChatColor.GOLD + "`").append(cmd.getName()).append(" ").append(y.name()).append(ChatColor.RESET).append("`: ").append(y.get()).append("\n");
                        }
                    }
                    sender.sendMessage(sb.toString());
                } else if (args[0].equalsIgnoreCase("count") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    if (!(sender instanceof Player)) {
                        return true;
                    }
                    Player player = (Player) sender;
                    int i = OddJob.getInstance().getLockManager().count(player.getUniqueId());
                    player.sendMessage("You have " + i + " lock" + ((i != 1) ? "s" : ""));
                } else if (args[0].equalsIgnoreCase("make") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    if (!(sender instanceof Player)) {
                        return true;
                    }
                    Player player = (Player) sender;
                    player.getInventory().addItem(OddJob.getInstance().getLockManager().makeKey(player.getUniqueId()));
                    OddJob.getInstance().getMessageManager().lockKey(sender);

                } else if (args[0].equalsIgnoreCase("lock") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    if (!(sender instanceof Player)) {
                        return true;
                    }
                    Player player = (Player) sender;
                    OddJob.getInstance().getLockManager().lockLocking(player.getUniqueId());
                } else if (args[0].equalsIgnoreCase("unlock") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    if (!(sender instanceof Player)) {
                        return true;
                    }
                    Player player = (Player) sender;
                    OddJob.getInstance().getLockManager().lockUnlocking(player.getUniqueId());
                } else if (args[0].equalsIgnoreCase("info") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    if (!(sender instanceof Player)) {
                        return true;
                    }
                    Player player = (Player) sender;
                    OddJob.getInstance().getLockManager().lockInfo(player.getUniqueId());
                } else if (args[0].equalsIgnoreCase("show") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    if (!(sender instanceof Player)) {
                        return true;
                    }
                    Player player = (Player) sender;
                    int i = 0;
                    if (OddJob.getInstance().getLockManager().getLocks() != null) {
                        for (Location location : OddJob.getInstance().getLockManager().getLocks().keySet()) {
                            if (OddJob.getInstance().getLockManager().getLocks().get(location) == player.getUniqueId()) {
                                i++;
                                sender.sendMessage(i + ") X=" + location.getBlockX() + "; Y=" + location.getBlockY() + "; Z=" + location.getBlockZ() + "; W=" + location.getWorld().getName() + ";");
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("load")) {
                    OddJob.getInstance().getLockManager().load();
                }
            } else if (args.length == 2 &&
                    args[0].equalsIgnoreCase("make") &&
                    args[1].equalsIgnoreCase("skeleton") &&
                    sender.hasPermission(cmd.getName() + "." + args[0] + "." + args[1]) &&
                    sender.hasPermission("locks.skeletonkey")) {
                if (!(sender instanceof Player)) {
                    return true;
                }
                Player player = (Player) sender;
                player.getInventory().addItem(OddJob.getInstance().getLockManager().skeletonKey);
                player.sendMessage(ChatColor.RED + " !!! Warning !!! " + ChatColor.RESET + "This is a dangerous key! Do not loose it!");
            }
        }


        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] g) {
        List<String> list = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("locks") &&
                g.length == 1) {
            for (Args f : Args.values()) {
                if (f.toString().startsWith(g[0].toLowerCase()) &&
                        commandSender.hasPermission("locks." + f)) {
                    list.add(f.toString());
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    enum Args {
        lock("Secure your stuff by locking it."),
        unlock("Unsecure your stuff by unlocking it"),
        info("Shows who owns what is locked"),
        count("Tells you have many locked objects you have"),
        list("Lists locking status of players"),
        help("Shows this menu"),
        load("Load the locks from locks.yml"),
        save("Saves the locks to locks.yml"),
        make("Makes a key to your locks");

        private String s;

        Args(String s) {
            this.s = s;
        }

        public String get() {
            return this.s;
        }
    }
}
