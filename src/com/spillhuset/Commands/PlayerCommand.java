package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.ScoreBoard;
import com.spillhuset.Utils.Odd.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("player")) {
            if (args.length == 0) {
                Bukkit.dispatchCommand(sender, command.getName() + " help");
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                OddJob.getInstance().log("HELP");
                //TODO
            } else if (args[0].equalsIgnoreCase("list")) {
                // Command /player list
                OddJob.getInstance().getMessageManager().infoListPlayers("List of known Players:", OddJob.getInstance().getPlayerManager().getUUIDs(), sender);
            } else if (args[0].equalsIgnoreCase("whitelist")) {
                // Command /player whitelist <add:del:show> [player]
                // Find target Player
                UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[2]);
                if (target == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(Plugin.ban, args[2], sender);
                    return true;
                }

                Player player = (Player) sender;
                OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                OddPlayer oddTarget = OddJob.getInstance().getPlayerManager().getOddPlayer(target);
                if (args[1].equalsIgnoreCase("add")) {
                    // Command /player whitelist add <player>
                    oddPlayer.addWhitelist(target);
                    OddJob.getInstance().getMessageManager().whitelistAdd(oddTarget.getName(),sender);
                } else if (args[1].equalsIgnoreCase("del")) {
                    // Command /player whitelist del <player>
                    oddPlayer.removeWhitelist(target);
                    OddJob.getInstance().getMessageManager().whitelistDel(oddTarget.getName(),sender);
                } else if (args[1].equalsIgnoreCase("show")) {
                    // Command /player whitelist show
                    // TODO OddJob.getInstance().getMessageManager().infoListPlayers("Your WHITELIST count: " + oddPlayer.getWhitelist().size(), oddPlayer.getWhitelist(), sender);
                }
            } else if (args[0].equalsIgnoreCase("blacklist")) {
                // Command /player blacklist <add:del:show> [player]
                // Find target Player
                UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[2]);
                if (target == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(Plugin.players, args[2], sender);
                    return true;
                }

                Player player = (Player) sender;
                OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                OddPlayer oddTarget = OddJob.getInstance().getPlayerManager().getOddPlayer(target);
                if (args[1].equalsIgnoreCase("add")) {
                    // Command /player whitelist add <player>
                    oddPlayer.addBlacklist(target);
                    OddJob.getInstance().getMessageManager().blacklistAdd(oddTarget.getName(),sender);
                } else if (args[1].equalsIgnoreCase("del")) {
                    // Command /player whitelist del <player>
                    oddPlayer.removeBlacklist(target);
                    OddJob.getInstance().getMessageManager().blacklistDel(oddTarget.getName(),sender);
                } else if (args[1].equalsIgnoreCase("show")) {
                    // Command /player whitelist show
                    // TODO OddJob.getInstance().getMessageManager().infoListPlayers("Your BLACKLIST count: " + oddPlayer.getWhitelist().size(), oddPlayer.getWhitelist(), sender);
                }
            } else if (args[0].equalsIgnoreCase("info")) {
                // Command /player info
                if (args.length == 1 && sender instanceof Player) {
                    Player player = (Player) sender;
                    OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                    HashMap<String, String> info = new HashMap<>();
                    info.put("Deny TPA:", oddPlayer.getDenyTpa() ? "true" : "false");
                    info.put("Deny Trade:", oddPlayer.getDenyTrade() ? "true" : "false");
                    info.put("Whitelist count:", oddPlayer.getWhitelist().size() + "");
                    info.put("Blacklist count:", oddPlayer.getBlacklist().size() + "");
                    info.put("Showing Scoreboard:", oddPlayer.getScoreboard().name());
                    // TODO OddJob.getInstance().getMessageManager().infoHashmap("Info of " + oddPlayer.getName(), info, sender);
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                // Command /player set <denytpa:denytrade:scoreboard> [guild,none]
                if (args.length == 3 && sender instanceof Player) {
                    Player player = (Player) sender;
                    OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                    if (args[1].equalsIgnoreCase("denytpa")) {
                        boolean deny = Boolean.parseBoolean(args[2]);
                        oddPlayer.setDenyTpa(deny);
                        //OddJob.getInstance().getMessageManager().playerSetDenyTPA(args[1],deny,player);
                    } else if (args[1].equalsIgnoreCase("denytrade")) {
                        boolean deny = Boolean.parseBoolean(args[2]);
                        oddPlayer.setDenyTrade(deny);
                        //OddJob.getInstance().getMessageManager().playerSetDenyTrade(args[1],deny,player);
                    } else if (args[1].equalsIgnoreCase("scoreboard")) {
                        ScoreBoard score = ScoreBoard.valueOf(args[2]);
                        if (score == ScoreBoard.None) OddJob.getInstance().getScoreManager().clear(player);
                        else OddJob.getInstance().getScoreManager().create(player,score);
                        oddPlayer.setScoreboard(score);
                        OddJob.getInstance().getMessageManager().playerSetScoreboard(args[1],score.name(),player);
                    }
                }
            } else if (args[0].equalsIgnoreCase("save")) {
                OddJob.getInstance().getPlayerManager().save();
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("player")) {
            if (args.length == 1) {
                String[] st = new String[]{"set", "info", "blacklist", "whitelist", "help"};
                for (String t : st) {
                    if (t.startsWith(args[0])) list.add(t);
                }
            } else if (args.length == 2 && (args[0].equalsIgnoreCase("blacklist") || args[0].equalsIgnoreCase("whitelist"))) {
                String[] st = new String[]{"add", "del", "show"};
                for (String t : st) {
                    if (t.startsWith(args[1])) list.add(t);
                }
            } else if (args.length == 3 && (args[0].equalsIgnoreCase("blacklist") || args[0].equalsIgnoreCase("whitelist")) && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("del"))) {
                for (String op : OddJob.getInstance().getPlayerManager().getNames()) {
                    if (op.startsWith(args[2])) list.add(op);
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                list.add("denytpa");
                list.add("denytrade");
                list.add("scoreboard");
            } else if (args.length == 3 && args[0].equalsIgnoreCase("set") && (args[1].equalsIgnoreCase("denytpa") || args[1].equalsIgnoreCase("denytrade"))) {
                list.add("true");
                list.add("false");
            } else if (args.length == 3 && args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("scoreboard")) {
                for (ScoreBoard sb : ScoreBoard.values()) {
                    list.add(sb.name());
                }
            }
        }
        return list;
    }
}
