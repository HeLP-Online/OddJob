package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
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
            } else if (strings[0].equalsIgnoreCase("list")) {
                List<String> list = OddJob.getInstance().getPlayerManager().listPlayers();
                OddJob.getInstance().getMessageManager().console(Arrays.toString(list.toArray()));
            } else if (strings[0].equalsIgnoreCase("whitelist")) {
                OddPlayer target = OddJob.getInstance().getPlayerManager().getOddPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[2]));
                if (target == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[2], commandSender);
                    return true;
                }
                Player player = (Player) commandSender;
                if (strings[1].equalsIgnoreCase("add")) {
                    OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId()).addWhiteList(target.getUuid());
                    OddJob.getInstance().getMessageManager().success("You have added " + target.getName() + " to your Whitelist of tpa", commandSender);
                } else if (strings[1].equalsIgnoreCase("del")) {
                    OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId()).delWhiteList(target.getUuid());
                    OddJob.getInstance().getMessageManager().success("You have removed " + target.getName() + " from your Whitelist of tpa", commandSender);
                } else if (strings[1].equalsIgnoreCase("show")) {
                    List<UUID> list = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId()).getWhiteList();
                    StringBuilder l = new StringBuilder("Showing whitelist: count: " + list.size() + "\n");
                    for (UUID uuid : list) {
                        l.append(OddJob.getInstance().getPlayerManager().getOddPlayer(uuid).getName());
                    }
                    OddJob.getInstance().getMessageManager().sendMessage(player, l.toString());
                }
            } else if (strings[0].equalsIgnoreCase("blacklist")) {
                Player player = (Player) commandSender;
                if (strings[1].equalsIgnoreCase("show")) {
                    List<UUID> list = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId()).getBlackList();
                    StringBuilder l = new StringBuilder("Showing blacklist: count: " + list.size() + "\n");
                    for (UUID uuid : list) {
                        l.append(OddJob.getInstance().getPlayerManager().getOddPlayer(uuid).getName());
                    }
                    OddJob.getInstance().getMessageManager().sendMessage(player, l.toString());
                } else {
                    OddPlayer target = OddJob.getInstance().getPlayerManager().getOddPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[2]));
                    if (target == null) {
                        OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[2], commandSender);
                        return true;
                    }

                    if (strings[1].equalsIgnoreCase("add")) {
                        OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId()).addBlackList(target.getUuid());
                        OddJob.getInstance().getMessageManager().success("You have added " + target.getName() + " to your Blacklist of tpa", commandSender);
                    } else if (strings[1].equalsIgnoreCase("del")) {
                        OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId()).delBlackList(target.getUuid());
                        OddJob.getInstance().getMessageManager().success("You have removed " + target.getName() + " from your Blacklist of tpa", commandSender);
                    }
                }
            } else if (strings[0].equalsIgnoreCase("info")) {
                if (strings.length == 1 && commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    OddPlayer op = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                    OddJob.getInstance().log(op.getBlackList().toArray().toString());
                    String sb = "Player INFO " + op.getName() + "\n" +
                            "UUID: " + op.getUuid() + "\n" +
                            "denyTPA: " + op.isDenyTPA() + "\n" +
                            "BlackList count: " + ((!op.getBlackList().isEmpty() || op.getBlackList() == null) ? op.getBlackList().size() : 0) + "\n" +
                            "WhiteList count: " + ((!op.getWhiteList().isEmpty() || op.getWhiteList() == null) ? op.getWhiteList().size() : 0) + "\n";
                    OddJob.getInstance().getMessageManager().sendMessage(player, sb);
                }
            } else if (strings[0].equalsIgnoreCase("set")) {
                if (strings.length == 3 && commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    OddPlayer op = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                    if (strings[1].equalsIgnoreCase("denytpa")) {
                        boolean deny = Boolean.parseBoolean(strings[2]);
                        op.setDenyTPA(deny);
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
                for (OddPlayer op : OddJob.getInstance().getPlayerManager().getPlayersMap()) {
                    if (op.getName().startsWith(strings[2])) list.add(op.getName());
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
