package com.spillhuset.Commands.Trade;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TradeAcceptCommand extends SubCommand {
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
        return "accept";
    }

    @Override
    public String getDescription() {
        return "Accepts an incoming trade request";
    }

    @Override
    public String getSyntax() {
        return "/trade accept <player>";
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

        if (checkArgs(2, 2, args, sender, Plugin.trade)) {
            OddJob.getInstance().getMessageManager().sendSyntax(getPlugin(), getSyntax(), sender);
            return;
        }

        Player player = (Player) sender;

        Player tradeWith = Bukkit.getPlayer(args[1]);

        if (tradeWith == null || !tradeWith.isOnline()) {
            OddJob.getInstance().getMessageManager().tradeNotOnline(player);
            return;
        }

        if (OddJob.getInstance().getPlayerManager().getRequestTrade().get(tradeWith.getUniqueId()) == player.getUniqueId()) {
            Inventory trade = OddJob.getInstance().getPlayerManager().getTradeInventory();
            player.openInventory(trade);
            tradeWith.openInventory(trade);
            OddJob.getInstance().getPlayerManager().getTradingPlayers().put(player.getUniqueId(), tradeWith.getUniqueId());

            OddJob.getInstance().getPlayerManager().getRequestTrade().remove(player.getUniqueId());
        } else {
            OddJob.getInstance().getMessageManager().tradeNone(player);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        Player player = (Player) sender;

        for (UUID uuid : OddJob.getInstance().getPlayerManager().getRequestTrade().keySet()) {
            if (OddJob.getInstance().getPlayerManager().getRequestTrade().get(uuid) == player.getUniqueId()) {
                Player tradeWith = Bukkit.getPlayer(uuid);
                if (tradeWith != null) {
                    list.add(tradeWith.getName());
                }
            }
        }

            return list;
    }
}
