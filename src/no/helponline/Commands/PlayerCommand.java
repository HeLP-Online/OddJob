package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PlayerCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("player")) {
            if (strings.length == 0) {
                Bukkit.dispatchCommand(commandSender, command.getName() + " help");
            }
            if (strings[0].equalsIgnoreCase("help")) {
                OddJob.getInstance().log("HELP");
            } else if (strings[0].equalsIgnoreCase("list")) {
                List<String> list = OddJob.getInstance().getPlayerManager().listPlayers();
                OddJob.getInstance().getMessageManager().console(Arrays.toString(list.toArray()));
            } else if (strings[0].equalsIgnoreCase("info")) {
                if (strings.length == 1 && commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    OddPlayer op = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());
                    String sb = "Player INFO " + op.getName() + "\n" +
                            "UUID: " + op.getUuid() + "\n" +
                            "denyTPA: " + op.isDenyTPA() + "\n" +
                            "BlackList count: " + op.getBlackList().size() + "\n" +
                            "WhiteList count: " + op.getWhiteList().size() + "\n";
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
        return null;
    }
}
