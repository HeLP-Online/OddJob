package com.spillhuset.Commands.Teleport;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Odd.OddPlayer;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TeleportDenyCommand extends SubCommand {
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
        return "deny";
    }

    @Override
    public String getDescription() {
        return "Denies an incoming teleport request";
    }

    @Override
    public String getSyntax() {
        return "/teleport deny";
    }

    @Override
    public String getPermission() {
        return "teleport";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }

        UUID topUUID = null;
        UUID bottomUUID = ((Player) sender).getUniqueId();
        OddPlayer topOddPlayer = null;
        OddPlayer bottomOddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(bottomUUID);


        int count = OddJob.getInstance().getTeleportManager().hasRequests(bottomUUID);

        if (count == 0) {
            // No requests
            OddJob.getInstance().getMessageManager().teleportNoRequest(sender);
        } else if (count == 1) {
            // Has one request
            topUUID = OddJob.getInstance().getTeleportManager().getRequestTop(bottomUUID);
            topOddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(topUUID);
            if (topOddPlayer == null) {
                OddJob.getInstance().log("null");
                OddJob.getInstance().getMessageManager().teleportNotOnline(bottomUUID);
                return;
            } else {
                OddJob.getInstance().log("deny");
                OddJob.getInstance().getTeleportManager().deny(topUUID);
            }
            OddJob.getInstance().log("here");
            OddJob.getInstance().getTeleportManager().delRequest(topUUID);
            OddJob.getInstance().getMessageManager().teleportDenied(topOddPlayer, bottomOddPlayer);
        } else {
            if (!checkArgs(2, 2, args, sender, getPlugin())) {
                topUUID = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                topOddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(topUUID);
                // Targeting player
                OddJob.getInstance().getTeleportManager().deny(topUUID);
                OddJob.getInstance().getTeleportManager().delRequest(topUUID);
                OddJob.getInstance().getMessageManager().teleportDenied(topOddPlayer, bottomOddPlayer);
            } else if (args.length == 1) {
                // No name given, listing up
                OddJob.getInstance().getMessageManager().teleportRequestList(sender, OddJob.getInstance().getTeleportManager().getRequestList(bottomUUID));
            }
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
