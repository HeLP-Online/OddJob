package com.spillhuset.Commands.Player.Set;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Odd.OddPlayer;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerSetDenyTradeCommand extends SubCommand {
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
        return Plugin.players;
    }

    @Override
    public String getName() {
        return "denytrade";
    }

    @Override
    public String getDescription() {
        return "Sets if you are automatically denying trade request";
    }

    @Override
    public String getSyntax() {
        return "/player set denytrade [true|false]";
    }

    @Override
    public String getPermission() {
        return "players";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(3, 3, args, sender, getPlugin())) {
            return;
        }

        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }

        Player player = (Player) sender;
        OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(player.getUniqueId());

        boolean deny = Boolean.parseBoolean(args[2]);
        oddPlayer.setDenyTpa(deny);
        OddJob.getInstance().getMessageManager().playerSetDenyTrade(deny, sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args[2].isEmpty() || "true".startsWith(args[2].toLowerCase()))
            list.add("true");
        if (args[2].isEmpty() || "false".startsWith(args[2].toLowerCase()))
            list.add("false");
        return list;
    }
}
