package com.spillhuset.Commands.Teleport;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class TeleportAllCommand extends SubCommand {
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
        return Plugin.teleport;
    }

    @Override
    public String getName() {
        return "all";
    }

    @Override
    public String getDescription() {
        return "Teleport all players to your location";
    }

    @Override
    public String getSyntax() {
        return "/teleport all";
    }

    @Override
    public String getPermission() {
        return "teleport.all";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender,false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
            return;
        }

        if (checkArgs(1,1,args,sender,getPlugin())) {
            return;
        }

        for (Player target: Bukkit.getOnlinePlayers()) {
            if (!target.equals(sender)) {
                OddJob.getInstance().getMessageManager().tpAllPlayer(target,sender.getName());
                OddJob.getInstance().getTeleportManager().teleport(target,sender, PlayerTeleportEvent.TeleportCause.COMMAND,false);
            } else {
                OddJob.getInstance().getMessageManager().tpAllTarget(sender);
            }

        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
