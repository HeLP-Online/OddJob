package com.spillhuset.Commands.Auction;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AuctionBuyoutCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.auctions;
    }

    @Override
    public String getName() {
        return "buy";
    }

    @Override
    public String getDescription() {
        return "Buy a listed stack of items";
    }

    @Override
    public String getSyntax() {
        return "/auction buyout <item>";
    }

    @Override
    public String getPermission() {
        return "auction";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }

        if (checkArgs(2, 2, args, sender, getPlugin())) {
            return;
        }

        int item = 0;

        try {
            item = Integer.parseInt(args[1]);
        } catch (NumberFormatException ignored) {
            OddJob.getInstance().getMessageManager().invalidNumber(getPlugin(), args[1], sender);
        }

        //OddJob.getInstance().getAuctionManager().buyout(item, (Player) sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
