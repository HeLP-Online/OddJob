package com.spillhuset.Commands.Lock;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Types;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LockMakeCommand extends SubCommand {
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
        return Plugin.lock;
    }

    @Override
    public String getName() {
        return "make";
    }

    @Override
    public String getDescription() {
        return "Makes the perfect key to your locked objects";
    }

    @Override
    public String getSyntax() {
        return "/locks make";
    }

    @Override
    public String getPermission() {
        return "locks.make";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(1, 2, args, sender, getPlugin())) {
            return;
        }
        Player player = (Player) sender;

        if (args.length == 2 && args[1].equalsIgnoreCase("skeleton") && can(sender, true)) {
            player.getInventory().addItem(OddJob.getInstance().getLockManager().skeletonKey);
            OddJob.getInstance().getMessageManager().lockSkeleton(sender);
            return;
        } else {
            double cost = OddJob.getInstance().getConfig().getDouble("default.lock.make", 100d);
            if (OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId()
                    , cost, player.hasPermission("currency.negative"), Types.AccountType.pocket)) {
                OddJob.getInstance().getMessageManager().locksKey(sender);
            } else {
                OddJob.getInstance().getMessageManager().insufficientFunds(sender);
                return;
            }
        }
        player.getInventory().addItem(OddJob.getInstance().getLockManager().makeKey(player.getUniqueId()));
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
