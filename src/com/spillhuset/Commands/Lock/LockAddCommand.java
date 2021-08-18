package com.spillhuset.Commands.Lock;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LockAddCommand extends SubCommand {
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
        return "add";
    }

    @Override
    public String getDescription() {
        return "Adds a new Material to the lockable list";
    }

    @Override
    public String getSyntax() {
        return "/locks add";
    }

    @Override
    public String getPermission() {
        return "locks.admin";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(1, 1, args, sender, getPlugin())) {
            return;
        }
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }
        Player player = (Player) sender;
        OddJob.getInstance().getLocksManager().addLockMaterial(player.getUniqueId());
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
