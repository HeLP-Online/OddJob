package com.spillhuset.Commands.Auction;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AuctionSellCommand extends SubCommand {
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
        return Plugin.auction;
    }

    @Override
    public String getName() {
        return "sell";
    }

    @Override
    public String getDescription() {
        return "Sell the stack from hand, to given value";
    }

    @Override
    public String getSyntax() {
        return "/ah sell <value> [buyout]";
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
        if (checkArgs(2, 4, args, sender, getPlugin())) {
            OddJob.getInstance().log("Test");
            return;
        }
        double value = 0.0, buyout = 0.0;
        int expire = 24;
        Player player = (Player) sender;

        try {
            if (args.length >= 2) {
                value = Double.parseDouble(args[1]);
            }
            if (args.length >= 3) {
                buyout = Double.parseDouble(args[2]);
            }
            if (args.length == 4) {
                expire = Integer.parseInt(args[3]);
            }
        } catch (NumberFormatException ex) {
            OddJob.getInstance().getMessageManager().invalidNumber(getPlugin(), "", sender);
            return;
        }

        OddJob.getInstance().getAuctionManager().sell(player, value, buyout, expire);
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
