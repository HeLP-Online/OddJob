package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

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
            } else if (strings[0].equalsIgnoreCase("list")) {
                List<String> list = OddJob.getInstance().getPlayerManager().getNames();
                OddJob.getInstance().getMessageManager().console(Arrays.toString(list.toArray()));
            } else if (strings[0].equalsIgnoreCase("whitelist")) {
                UUID target = null;
                //OfflinePlayer target = OddJob.getInstance().getPlayerManager().getOddPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[2]));
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (offlinePlayer.getName().equalsIgnoreCase(strings[2])) target = offlinePlayer.getUniqueId();
                }
                if (target == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[2], commandSender);
                    return true;
                }
                Player player = (Player) commandSender;
                List<UUID> whitelist = (List<UUID>) OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId()).get("whitelist");
                if (strings[1].equalsIgnoreCase("add")) {
                    whitelist.add(target);
                    OddJob.getInstance().getMessageManager().success("You have added " + OddJob.getInstance().getMySQLManager().getPlayerName(target) + " to your Whitelist of tpa", commandSender);
                    OddJob.getInstance().getMySQLManager().updatePlayerWhitelist(player.getUniqueId(), whitelist);
                } else if (strings[1].equalsIgnoreCase("del")) {
                    whitelist.remove(target);
                    OddJob.getInstance().getMessageManager().success("You have removed " + OddJob.getInstance().getMySQLManager().getPlayerName(target) + " from your Whitelist of tpa", commandSender);
                    OddJob.getInstance().getMySQLManager().updatePlayerWhitelist(player.getUniqueId(), whitelist);
                } else if (strings[1].equalsIgnoreCase("show")) {
                    StringBuilder l = new StringBuilder("Showing whitelist: count: " + whitelist.size() + "\n");
                    for (UUID uuid : whitelist) {
                        l.append(OddJob.getInstance().getPlayerManager().getName(uuid));
                    }
                    OddJob.getInstance().getMessageManager().sendMessage(player, l.toString());
                }
            } else if (strings[0].equalsIgnoreCase("blacklist")) {
                UUID target = null;
                //OfflinePlayer target = OddJob.getInstance().getPlayerManager().getOddPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[2]));
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (offlinePlayer.getName().equalsIgnoreCase(strings[2])) target = offlinePlayer.getUniqueId();
                }
                if (target == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[2], commandSender);
                    return true;
                }
                Player player = (Player) commandSender;
                List<UUID> blacklist = (List<UUID>) OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId()).get("blacklist");
                if (strings[1].equalsIgnoreCase("add")) {
                    blacklist.add(target);
                    OddJob.getInstance().getMessageManager().success("You have added " + OddJob.getInstance().getMySQLManager().getPlayerName(target) + " to your blacklist of tpa", commandSender);
                    OddJob.getInstance().getMySQLManager().updatePlayerBlacklist(player.getUniqueId(), blacklist);
                } else if (strings[1].equalsIgnoreCase("del")) {
                    blacklist.remove(target);
                    OddJob.getInstance().getMessageManager().success("You have removed " + OddJob.getInstance().getMySQLManager().getPlayerName(target) + " from your blacklist of tpa", commandSender);
                    OddJob.getInstance().getMySQLManager().updatePlayerBlacklist(player.getUniqueId(), blacklist);
                } else if (strings[1].equalsIgnoreCase("show")) {
                    StringBuilder l = new StringBuilder("Showing blacklist: count: " + blacklist.size() + "\n");
                    for (UUID uuid : blacklist) {
                        l.append(OddJob.getInstance().getPlayerManager().getName(uuid));
                    }
                    OddJob.getInstance().getMessageManager().sendMessage(player, l.toString());
                }
            } else if (strings[0].equalsIgnoreCase("info")) {
                if (strings.length == 1 && commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    HashMap<String, Object> op = OddJob.getInstance().getMySQLManager().getPlayer(player.getUniqueId());
                    String sb = "Player INFO " + op.get("name") + "\n" +
                            "UUID: " + op.get("uuid") + "\n" +
                            "denyTPA: " + op.get("denytpa") + "\n" +
                            "BlackList count: " + ((List<UUID>) op.get("blacklist")).size() + "\n" +
                            "WhiteList count: " + ((List<UUID>) op.get("whitelist")).size() + "\n";
                    OddJob.getInstance().getMessageManager().sendMessage(player, sb);
                }
            } else if (strings[0].equalsIgnoreCase("set")) {
                if (strings.length == 3 && commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    HashMap<String, Object> op = OddJob.getInstance().getMySQLManager().getPlayer(player.getUniqueId());
                    if (strings[1].equalsIgnoreCase("denytpa")) {
                        boolean deny = Boolean.parseBoolean(strings[2]);
                        OddJob.getInstance().getMySQLManager().setPlayerDenyTpa(player.getUniqueId(), deny);
                        OddJob.getInstance().getMessageManager().success("SET " + strings[1] + " to " + deny, player);
                    }
                }
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
            } else if (strings.length == 3 && strings[0].equalsIgnoreCase("set") && strings[1].equalsIgnoreCase("denytpa")) {
                list.add("true");
                list.add("false");
            }
        }
        return list;
    }
}
