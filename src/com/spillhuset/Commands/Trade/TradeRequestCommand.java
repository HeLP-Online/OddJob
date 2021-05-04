package com.spillhuset.Commands.Trade;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TradeRequestCommand extends SubCommand {
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
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.trade);
            return;
        }

        Player player = (Player) sender;

        if (!checkArgs(2,2,args,sender,Plugin.trade)) {
            return;
        }

        Player tradeWith = Bukkit.getPlayer(args[1]);
        if (tradeWith == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.trade,args[1],sender);
            return;
        }

        OddJob.getInstance().getPlayerManager().getRequestTrade().put(tradeWith.getUniqueId(),player.getUniqueId());
        OddJob.getInstance().getMessageManager().tradeRequest(tradeWith,player);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
