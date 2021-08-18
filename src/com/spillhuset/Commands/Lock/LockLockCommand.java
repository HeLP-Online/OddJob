package com.spillhuset.Commands.Lock;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LockLockCommand extends SubCommand {
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
        return Plugin.locks;
    }

    @Override
    public String getName() {
        return "lock";
    }

    @Override
    public String getDescription() {
        return "Gives you the locking tool";
    }

    @Override
    public String getSyntax() {
        return "/locks lock";
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
        OddJob.getInstance().getLocksManager().lockLocking(player.getUniqueId());
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
