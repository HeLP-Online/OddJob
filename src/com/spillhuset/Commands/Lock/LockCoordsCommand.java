package com.spillhuset.Commands.Lock;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LockCoordsCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return true;
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
        return "coords";
    }

    @Override
    public String getDescription() {
        return "Shows a list of your locked objects coordinates";
    }

    @Override
    public String getSyntax() {
        return "/locks coords";
    }

    @Override
    public String getPermission() {
        return "locks.use";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender,false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
            return;
        }

        if (checkArgs(1,2,args,sender,getPlugin())) {
            return;
        }

        UUID target = null;
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
                return;
            }
            target = ((Player) sender).getUniqueId();
        } else {
            target = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
            if (target == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(),args[1],sender);
                return;
            }
        }

        List<String> list = new ArrayList<>();
        if (OddJob.getInstance().getLocksManager().getLocks() != null){
            for(Location location : OddJob.getInstance().getLocksManager().getLocks().keySet()) {
                if (OddJob.getInstance().getLocksManager().getLocks().get(location) == target) {
                    list.add("X="+location.getBlockX()+";Y="+location.getBlockY()+";Z="+location.getBlockZ()+";W="+location.getWorld().getName()+";");
                }
            }
            OddJob.getInstance().getMessageManager().lockList(list,sender);
        } else {
            OddJob.getInstance().getMessageManager().locksNoLocks(OddJob.getInstance().getPlayerManager().getName(target),sender);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
