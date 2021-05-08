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
    public boolean allowConsole() {
        return false;
    }

    @Override
    public boolean allowOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.player;
    }

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
        // Check Args
        if (checkArgs(3, 3, args, sender, getPlugin())) {
            return;
        }

        // Check Player
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
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
