package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.ScoreBoard;
import no.helponline.Utils.OddPlayer;
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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("player")) {
            if (strings.length == 0) {
                Bukkit.dispatchCommand(commandSender, command.getName() + " help");
                return true;
            }
            if (strings[0].equalsIgnoreCase("help")) {
                OddJob.getInstance().log("HELP");
                //TODO
            } else if (strings[0].equalsIgnoreCase("list")) {
                // Command /player list
                OddJob.getInstance().getMessageManager().infoListPlayers("List of known Players:", OddJob.getInstance().getPlayerManager().getUUIDs(), commandSender);
            } else if (strings[0].equalsIgnoreCase("whitelist")) {
                // Command /player whitelist <add:del:show> [player]
                // Find target Player
                UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[2]);
                if (target == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(strings[2], commandSender);
                    return true;
                }

                Player player = (Player) commandSender;
                OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                OddPlayer oddTarget = OddJob.getInstance().getPlayerManager().getOddPlayer(target);
                if (strings[1].equalsIgnoreCase("add")) {
                    // Command /player whitelist add <player>
                    oddPlayer.addWhitelist(target);
                    OddJob.getInstance().getMessageManager().success("You have added " + oddTarget.getName() + " to your Whitelist", commandSender, true);
                } else if (strings[1].equalsIgnoreCase("del")) {
                    // Command /player whitelist del <player>
                    oddPlayer.removeWhitelist(target);
                    OddJob.getInstance().getMessageManager().success("You have removed " + oddTarget.getName() + " from your Whitelist", commandSender, true);
                } else if (strings[1].equalsIgnoreCase("show")) {
                    // Command /player whitelist show
                    OddJob.getInstance().getMessageManager().infoListPlayers("Your WHITELIST count: " + oddPlayer.getWhitelist().size(), oddPlayer.getWhitelist(), commandSender);
                }
            } else if (strings[0].equalsIgnoreCase("blacklist")) {
                // Command /player blacklist <add:del:show> [player]
                // Find target Player
                UUID target = OddJob.getInstance().getPlayerManager().getUUID(strings[2]);
                if (target == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(strings[2], commandSender);
                    return true;
                }

                Player player = (Player) commandSender;
                OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                OddPlayer oddTarget = OddJob.getInstance().getPlayerManager().getOddPlayer(target);
                if (strings[1].equalsIgnoreCase("add")) {
                    // Command /player whitelist add <player>
                    oddPlayer.addBlacklist(target);
                    OddJob.getInstance().getMessageManager().success("You have added " + oddTarget.getName() + " to your Blacklist", commandSender, true);
                } else if (strings[1].equalsIgnoreCase("del")) {
                    // Command /player whitelist del <player>
                    oddPlayer.removeBlacklist(target);
                    OddJob.getInstance().getMessageManager().success("You have removed " + oddTarget.getName() + " from your Blacklist", commandSender, true);
                } else if (strings[1].equalsIgnoreCase("show")) {
                    // Command /player whitelist show
                    OddJob.getInstance().getMessageManager().infoListPlayers("Your BLACKLIST count: " + oddPlayer.getWhitelist().size(), oddPlayer.getWhitelist(), commandSender);
                }
            } else if (strings[0].equalsIgnoreCase("info")) {
                // Command /player info
                if (strings.length == 1 && commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                    HashMap<String, String> info = new HashMap<>();
                    info.put("Deny TPA:", oddPlayer.getDenyTpa() ? "true" : "false");
                    info.put("Deny Trade:", oddPlayer.getDenyTrade() ? "true" : "false");
                    info.put("Whitelist count:", oddPlayer.getWhitelist().size() + "");
                    info.put("Blacklist count:", oddPlayer.getBlacklist().size() + "");
                    info.put("Showing Scoreboard:", oddPlayer.getScoreboard().name());
                    OddJob.getInstance().getMessageManager().infoHashmap("Info of " + oddPlayer.getName(), info, commandSender);
                }
            } else if (strings[0].equalsIgnoreCase("set")) {
                // Command /player set <denytpa:denytrade:scoreboard> [guild,none]
                if (strings.length == 3 && commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                    if (strings[1].equalsIgnoreCase("denytpa")) {
                        boolean deny = Boolean.parseBoolean(strings[2]);
                        oddPlayer.setDenyTpa(deny);
                        OddJob.getInstance().getMessageManager().success("SET " + strings[1] + " to " + deny, player, true);
                    } else if (strings[1].equalsIgnoreCase("denytrade")) {
                        boolean deny = Boolean.parseBoolean(strings[2]);
                        oddPlayer.setDenyTrade(deny);
                        OddJob.getInstance().getMessageManager().success("SET " + strings[1] + " to " + deny, player, true);
                    } else if (strings[1].equalsIgnoreCase("scoreboard")) {
                        ScoreBoard score = ScoreBoard.valueOf(strings[2]);
                        oddPlayer.setScoreBoard(score);
                        OddJob.getInstance().getMessageManager().success("SET " + strings[1] + " to " + score.name(), player, true);
                    }
                }
            } else if (strings[0].equalsIgnoreCase("save")) {
                OddJob.getInstance().getPlayerManager().save();
            } else if (strings[0].equalsIgnoreCase("load")) {
                OddJob.getInstance().getPlayerManager().load();
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("player")) {
            if (strings.length == 1) {
                String[] st = new String[]{"set", "info", "blacklist", "whitelist", "help"};
                for (String t : st) {
                    if (t.startsWith(strings[0])) list.add(t);
                }
            } else if (strings.length == 2 && (strings[0].equalsIgnoreCase("blacklist") || strings[0].equalsIgnoreCase("whitelist"))) {
                String[] st = new String[]{"add", "del", "show"};
                for (String t : st) {
                    if (t.startsWith(strings[1])) list.add(t);
                }
            } else if (strings.length == 3 && (strings[0].equalsIgnoreCase("blacklist") || strings[0].equalsIgnoreCase("whitelist")) && (strings[1].equalsIgnoreCase("add") || strings[1].equalsIgnoreCase("del"))) {
                for (String op : OddJob.getInstance().getPlayerManager().getNames()) {
                    if (op.startsWith(strings[2])) list.add(op);
                }
            } else if (strings.length == 2 && strings[0].equalsIgnoreCase("set")) {
                list.add("denytpa");
                list.add("denytrade");
                list.add("scoreboard");
            } else if (strings.length == 3 && strings[0].equalsIgnoreCase("set") && (strings[1].equalsIgnoreCase("denytpa") || strings[1].equalsIgnoreCase("denytrade"))) {
                list.add("true");
                list.add("false");
            } else if (strings.length == 3 && strings[0].equalsIgnoreCase("set") && strings[1].equalsIgnoreCase("scoreboard")) {
                for (ScoreBoard sb : ScoreBoard.values()) {
                    list.add(sb.name());
                }
            }
        }
        return list;
    }
}
