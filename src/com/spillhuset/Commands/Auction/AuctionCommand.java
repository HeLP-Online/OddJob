package com.spillhuset.Commands.Auction;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AuctionCommand extends SubCommandInterface implements CommandExecutor {
    private final List<SubCommand> subCommands = new ArrayList<>();
    public AuctionCommand() {
        subCommands.add(new AuctionSellCommand());
        subCommands.add(new AuctionBuyoutCommand());
        subCommands.add(new AuctionBidCommand());
        subCommands.add(new AuctionAutoCommand());
    }

    @Override
    public boolean denyConsole() {
        return false;
    }

    @Override
    public boolean onlyConsole() {
        return false;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public boolean onlyOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.auctions;
    }

    @Override
    public String getPermission() {
        return "auction";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OddJob.getInstance().log("auctioneer");
        if (checkArgs(1,0,args,sender,getPlugin())) {
            OddJob.getInstance().log("args");
            return true;
        }
        if (!can(sender,false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
            return true;
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (name.equalsIgnoreCase(args[0])) {
                subCommand.perform(sender, args);
                return true;
            }
            nameBuilder.append(name).append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
        // Fallback
        OddJob.getInstance().getMessageManager().sendSyntax(getPlugin(),nameBuilder.toString(),sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (can(sender,false)) {
                if (args[0].isEmpty()) {
                    list.add(name);
                } else if (name.equalsIgnoreCase(args[0]) && args.length > 1) {
                    return subCommand.getTab(sender, args);
                } else if (name.startsWith(args[0])) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
