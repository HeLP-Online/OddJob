package com.spillhuset.Commands.Auction;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AuctionAutoCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return true;
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
        return "auto";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getPermission() {
        return "auctions.admin";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        OddJob.getInstance().getAuctionManager().checkExpiredBids();
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
