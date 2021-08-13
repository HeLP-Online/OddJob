package com.spillhuset.Commands.Trade;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        if (!can(sender,false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
            return;
        }

        Player player = (Player) sender;

        if (checkArgs(2,2,args,sender,getPlugin())) {
            OddJob.getInstance().getMessageManager().sendSyntax(getPlugin(),getSyntax(),sender);
            return;
        }

        Player tradeWith = Bukkit.getPlayer(args[1]);
        if (tradeWith == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(),args[1],sender);
            return;
        }

        OddJob.getInstance().getPlayerManager().getRequestTrade().put(player.getUniqueId(),tradeWith.getUniqueId());
        OddJob.getInstance().getMessageManager().tradeRequest(tradeWith,player);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (sender.getName().equalsIgnoreCase(player.getName())) {
                OddJob.getInstance().log("me");
                continue;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase(args[1])) {
                OddJob.getInstance().log("same");
                continue;
            }
            if (!sender.isOp() && player.isOp()) {
                OddJob.getInstance().log("op/noop");
                continue;
            }
            if (args.length == 1 && player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                list.add(player.getName());
            }
        }
        return list;
    }
}
