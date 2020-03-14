package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.OddPlayer;
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
                //TODO
            } else if (strings[0].equalsIgnoreCase("list")) {
                Collection<String> list = OddJob.getInstance().getPlayerManager().getNames();
                OddJob.getInstance().getMessageManager().console(Arrays.toString(list.toArray()));
            } else if (strings[0].equalsIgnoreCase("whitelist")) {
                UUID target = null;
                //OfflinePlayer target = OddJob.getInstance().getPlayerManager().getOddPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[2]));
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (offlinePlayer.getName().equalsIgnoreCase(strings[2])) target = offlinePlayer.getUniqueId();
                }
                if (target == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[2], commandSender, false);
                    return true;
                }
                Player player = (Player) commandSender;
                OddPlayer odd = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                OddPlayer oddTarget = OddJob.getInstance().getPlayerManager().getOddPlayer(target);
                if (strings[1].equalsIgnoreCase("add")) {
                    odd.addWhitelist(target);
                    OddJob.getInstance().getMessageManager().success("You have added " + oddTarget.getName() + " to your Whitelist of tpa", commandSender, true);
                } else if (strings[1].equalsIgnoreCase("del")) {
                    odd.removeWhitelist(target);
                    OddJob.getInstance().getMessageManager().success("You have removed " + oddTarget.getName() + " from your Whitelist of tpa", commandSender, true);
                } else if (strings[1].equalsIgnoreCase("show")) {
                    StringBuilder l = new StringBuilder("Showing whitelist: count: " + odd.getWhitelist().size() + "\n");
                    for (UUID uuid : odd.getWhitelist()) {
                        l.append(OddJob.getInstance().getPlayerManager().getName(uuid));
                    }
                    player.sendMessage(l.toString());
                }
            } else if (strings[0].equalsIgnoreCase("blacklist")) {
                UUID target = null;
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (offlinePlayer.getName().equalsIgnoreCase(strings[2])) target = offlinePlayer.getUniqueId();
                }
                if (target == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[2], commandSender, false);
                    return true;
                }
                Player player = (Player) commandSender;
                OddPlayer odd = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                OddPlayer oddTarget = OddJob.getInstance().getPlayerManager().getOddPlayer(target);
                if (strings[1].equalsIgnoreCase("add")) {
                    odd.addBlacklist(target);
                    OddJob.getInstance().getMessageManager().success("You have added " + oddTarget.getName() + " to your blacklist of tpa", commandSender, true);
                } else if (strings[1].equalsIgnoreCase("del")) {
                    odd.removeBlacklist(target);
                    OddJob.getInstance().getMessageManager().success("You have removed " + oddTarget.getName() + " from your blacklist of tpa", commandSender, true);
                } else if (strings[1].equalsIgnoreCase("show")) {
                    StringBuilder l = new StringBuilder("Showing blacklist: count: " + odd.getBlacklist().size() + "\n");
                    for (UUID uuid : odd.getBlacklist()) {
                        l.append(OddJob.getInstance().getPlayerManager().getName(uuid));
                    }
                    player.sendMessage(l.toString());
                }
            } else if (strings[0].equalsIgnoreCase("info")) {
                if (strings.length == 1 && commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    OddPlayer op = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                    String sb = "Player INFO " + op.getName() + "\n" +
                            "UUID: " + op.getUuid() + "\n" +
                            "denyTPA: " + op.isDeny_tpa() + "\n" +
                            "BlackList count: " + op.getBlacklist().size() + "\n" +
                            "WhiteList count: " + op.getWhitelist().size() + "\n";
                    player.sendMessage(sb);
                }
            } else if (strings[0].equalsIgnoreCase("set")) {
                if (strings.length == 3 && commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    OddPlayer op = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                    if (strings[1].equalsIgnoreCase("denytpa")) {
                        boolean deny = Boolean.parseBoolean(strings[2]);
                        op.setDeny_tpa(deny);
                        OddJob.getInstance().getMessageManager().success("SET " + strings[1] + " to " + deny, player, true);
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
