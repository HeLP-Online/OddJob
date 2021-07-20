package com.spillhuset.Commands.Tp;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TpAcceptCommand extends SubCommand {
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
        return Plugin.tp;
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String getDescription() {
        return "Accepts an incoming teleport request";
    }

    @Override
    public String getSyntax() {
        return "/tp accept";
    }

    @Override
    public String getPermission() {
        return "tp";
    }

    /*
     * /tp accept performed by the destination (sender)
     */
    @Override
    public void perform(CommandSender sender, String[] args) {
        if (can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
        }
        Player moving = null;
        Player destination = (Player) sender;
        if (checkArgs(2,2,args,sender,getPlugin())) {
            moving = Bukkit.getPlayer(args[1]);
            if (moving == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[1], sender);
                return;
            }
        } else {
            OddJob.getInstance().getTeleportManager().hasRequest(destination.getUniqueId());
        }
    }


    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
