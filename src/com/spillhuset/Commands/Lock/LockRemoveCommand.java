package com.spillhuset.Commands.Lock;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LockRemoveCommand extends SubCommand {
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
        return "RemoveMaterial";
    }

    @Override
    public String getDescription() {
        return "Removes a Material from the lockable list";
    }

    @Override
    public String getSyntax() {
        return "/locks remove";
    }

    @Override
    public String getPermission() {
        return "locks.admin";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(1,1,args,sender,getPlugin())) {
            return;
        }
        if (!can(sender,false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
            return;
        }
        Player player = (Player) sender;
        OddJob.getInstance().getLockManager().delLockMaterial(player.getUniqueId());
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
