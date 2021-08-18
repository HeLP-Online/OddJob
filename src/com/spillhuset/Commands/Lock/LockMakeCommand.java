package com.spillhuset.Commands.Lock;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
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
        return Plugin.locks;
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
        return "locks.use";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }
        if (checkArgs(1, 1, args, sender, getPlugin())) {
            return;
        }

        Player player = (Player) sender;
        OddJob.getInstance().getLocksManager().getKey(player);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
