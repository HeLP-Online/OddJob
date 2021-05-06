package com.spillhuset.Commands.Player.Set;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.ScoreBoard;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerSetScoreboardCommand extends SubCommand {
    @Override
    public String getName() {
        return "scoreboard";
    }

    @Override
    public String getDescription() {
        return "Sets a player scoreboard";
    }

    @Override
    public String getSyntax() {
        return "/player set scoreboard <option>";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.player);
            return;
        }
        if (checkArgs(3, 3, args, sender, Plugin.player)) {
            return;
        }
        Player player = (Player) sender;
        OddJob.getInstance().getScoreManager().create(player, ScoreBoard.valueOf(args[2]));
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 3) {
            for (ScoreBoard sb : ScoreBoard.values()) {
                if (args[2].isEmpty()) {
                    list.add(sb.name());
                } else if (sb.name().startsWith(args[2])) {
                    list.add(sb.name());
                }
            }
        }
        return list;
    }
}
