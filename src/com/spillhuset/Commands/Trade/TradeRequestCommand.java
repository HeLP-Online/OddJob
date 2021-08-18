package com.spillhuset.Commands.Trade;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TradeRequestCommand extends SubCommand {
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
        return Plugin.trade;
    }

    @Override
    public String getName() {
        return "request";
    }

    @Override
    public String getDescription() {
        return "Requests to trade with a player";
    }

    @Override
    public String getSyntax() {
        return "/trade request <player>";
    }

    @Override
    public String getPermission() {
        return "trade";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }

        Player topPlayer = (Player) sender;

        if (checkArgs(2, 2, args, sender, getPlugin())) {
            OddJob.getInstance().getMessageManager().sendSyntax(getPlugin(), getSyntax(), sender);
            return;
        }

        Player bottomPlayer = Bukkit.getPlayer(args[1]);
        if (bottomPlayer == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[1], sender);
            return;
        }

        OddJob.getInstance().getPlayerManager().getRequestTrade().put(topPlayer.getUniqueId(), bottomPlayer.getUniqueId());
        OddJob.getInstance().getMessageManager().tradeRequest(topPlayer, bottomPlayer);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (((Player)sender).getUniqueId().equals(player.getUniqueId())) {
                OddJob.getInstance().log("me");
            } else if (!sender.isOp() && player.isOp()) {
                OddJob.getInstance().log("op/noop");
            } else if (args.length == 2 && player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                list.add(player.getName());
            }
        }
        return list;
    }
}
