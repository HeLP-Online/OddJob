package com.spillhuset.Commands.Auction;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AuctionBidCommand extends SubCommand {
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
        return Plugin.auctions;
    }

    @Override
    public String getName() {
        return "bid";
    }

    @Override
    public String getDescription() {
        return "Placing a bid on an item for sale";
    }

    @Override
    public String getSyntax() {
        return "/auction bid <item> <offer>";
    }

    @Override
    public String getPermission() {
        return "auction";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender,false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
            return;
        }

        if (checkArgs(3,3,args,sender,getPlugin())) {
            return;
        }

        int item;
        double offer;

        try {
            item = Integer.parseInt(args[1]);
            offer = Double.parseDouble(args[2]);
        } catch (NumberFormatException ignored) {
            OddJob.getInstance().getMessageManager().invalidNumber(getPlugin(),args[1]+" or "+args[2],sender);
            return;
        }

        Player player = (Player) sender;
        OddJob.getInstance().getAuctionManager().bid(item,offer,player);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
