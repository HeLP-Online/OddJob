package com.spillhuset.Commands.Teleport;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeleportAcceptCommand extends SubCommand {
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
        return Plugin.teleport;
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
        return "/teleport accept";
    }

    @Override
    public String getPermission() {
        return "teleport";
    }

    /*
     * /tp accept performed by the destination (sender)
     */
    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }

        Player topPlayer = null;
        Player bottomPlayer = (Player) sender;
        UUID topUUID = null;

        int count = OddJob.getInstance().getTeleportManager().hasRequests(bottomPlayer.getUniqueId());

        if (count == 0) {
            // No requests
            OddJob.getInstance().getMessageManager().teleportNoRequest(sender);
        } else if (count == 1) {
            // Has one request
            topUUID = OddJob.getInstance().getTeleportManager().getRequest(bottomPlayer.getUniqueId());
            topPlayer = Bukkit.getPlayer(topUUID);
            if (topPlayer == null) {
                // Target not online
                OddJob.getInstance().getMessageManager().teleportNotOnline(bottomPlayer.getUniqueId());
                return;
            }
            // Targeting
            OddJob.getInstance().getTeleportManager().acceptRequest(topPlayer,bottomPlayer);
        } else {
            // Has two or more
            if (!checkArgs(2, 2, args, sender, getPlugin())) {
                topPlayer = Bukkit.getPlayer(args[1]);
                if (topPlayer == null) {
                    // Player is not online
                    OddJob.getInstance().getMessageManager().teleportNotOnline(bottomPlayer.getUniqueId());
                } else {
                    // Targeting player
                    OddJob.getInstance().getTeleportManager().acceptRequest(topPlayer,bottomPlayer);
                }
                // Removing request
                OddJob.getInstance().getTeleportManager().delRequest(OddJob.getInstance().getPlayerManager().getUUID(args[1]));
            } else if (args.length == 1) {
                // No name given, listing up
                OddJob.getInstance().getMessageManager().teleportRequestList(sender, OddJob.getInstance().getTeleportManager().getRequestList(bottomPlayer.getUniqueId()));
            }
        }
    }


    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            list.addAll(OddJob.getInstance().getTeleportManager().getRequestList(((Player) sender).getUniqueId()));
        }
        return list;
    }
}
