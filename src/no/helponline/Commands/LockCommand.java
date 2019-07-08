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

import java.util.*;

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
                } else if (args[0].equalsIgnoreCase("load") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    OddJob.getInstance().getConfigManager().loadLocks();
                } else if (args[0].equalsIgnoreCase("save") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    OddJob.getInstance().getConfigManager().saveLocks();
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
                    OddJob.getInstance().getMessageManager().sendMessage(player, ChatColor.RED + "! " + ChatColor.RESET + "This is a key to all your chests, keep in mind who you share it with.");
                    OddJob.getInstance().getMessageManager().sendMessage(player, ChatColor.RED + "! " + ChatColor.RESET + "Stolen item or lost keys will not be refunded.");
                } else if (args[0].equalsIgnoreCase("lock") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    if (!(sender instanceof Player)) {
                        return true;
                    }
                    Player player = (Player) sender;
                    OddJob.getInstance().getLockManager().locking(player.getUniqueId());
                } else if (args[0].equalsIgnoreCase("unlock") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    if (!(sender instanceof Player)) {
                        return true;
                    }
                    Player player = (Player) sender;
                    OddJob.getInstance().getLockManager().unlocking(player.getUniqueId());
                } else if (args[0].equalsIgnoreCase("info") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    if (!(sender instanceof Player)) {
                        return true;
                    }
                    Player player = (Player) sender;
                    OddJob.getInstance().getLockManager().infolock(player.getUniqueId());
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

                } else if (args[0].equalsIgnoreCase("list") && sender.hasPermission(cmd.getName() + "." + args[0])) {
                    Collection<UUID> locking = OddJob.getInstance().getLockManager().getLocking();
                    Collection<UUID> unlocking = OddJob.getInstance().getLockManager().getUnlocking();
                    Collection<UUID> lockinfo = OddJob.getInstance().getLockManager().getLockinfo();
                    if (sender instanceof Player) {
                        return true;
                    }
                    sender.sendMessage(ChatColor.GOLD + "Locking: " + ChatColor.RESET);
                    if (locking.isEmpty()) {
                        sender.sendMessage("None");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (UUID uuid : locking) {
                            sb.append(OddJob.getInstance().getPlayerManager().getName(uuid)).append(", ");
                        }
                        sender.sendMessage(sb.toString().substring(0, sb.length() - 2));
                    }

                    sender.sendMessage(ChatColor.GOLD + "Unlocking: " + ChatColor.RESET);
                    if (unlocking.isEmpty()) {
                        sender.sendMessage("None");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (UUID uuid : unlocking) {
                            sb.append(OddJob.getInstance().getPlayerManager().getName(uuid)).append(", ");
                        }
                        sender.sendMessage(sb.toString().substring(0, sb.length() - 2));
                    }

                    sender.sendMessage(ChatColor.GOLD + "Lockinfo: " + ChatColor.RESET);
                    if (lockinfo.isEmpty()) {
                        sender.sendMessage("None");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (UUID uuid : lockinfo) {
                            sb.append(OddJob.getInstance().getPlayerManager().getName(uuid)).append(", ");
                        }
                        sender.sendMessage(sb.toString().substring(0, sb.length() - 2));
                    }
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
                player.getInventory().addItem(OddJob.getInstance().getLockManager().makeSkeletonKey());
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
