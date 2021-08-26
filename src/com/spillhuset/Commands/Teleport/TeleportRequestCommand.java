package com.spillhuset.Commands.Teleport;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Odd.OddPlayer;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeleportRequestCommand extends SubCommand {
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
        return "request";
    }

    @Override
    public String getDescription() {
        return "Sends a request to teleport to a given player";
    }

    @Override
    public String getSyntax() {
        return "/teleport request <player>";
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

        if (checkArgs(2, 2, args, sender, getPlugin())) {
            return;
        }

        // Requesting player
        Player topPlayer = (Player) sender;

        // Target player
        Player bottomPlayer = Bukkit.getPlayer(args[1]);
        if (bottomPlayer == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[1], sender);
            return;
        }
        UUID bottomUUID = bottomPlayer.getUniqueId();
        OddPlayer bottomOddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(bottomUUID);

        // Check: Blacklist Whitelist DenyTPA
        if ((bottomOddPlayer.getBlacklist().contains(topPlayer.getUniqueId()) || bottomOddPlayer.getDenyTpa()) && (bottomOddPlayer.getDenyTpa() && !bottomOddPlayer.getWhitelist().contains(topPlayer.getUniqueId()))) {
            OddJob.getInstance().getMessageManager().tpDenied(args[1], sender);
            return;
        }

        // Check Request queue
        if (OddJob.getInstance().getTeleportManager().hasRequest(topPlayer.getUniqueId())) {
            OddPlayer prevOddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(OddJob.getInstance().getTeleportManager().getRequestBottom(topPlayer.getUniqueId()));
            if (prevOddPlayer == bottomOddPlayer) {
                OddJob.getInstance().getMessageManager().teleRequestAlready(bottomPlayer.getName(),sender);
                return;
            }
            OddJob.getInstance().getMessageManager().tpAlreadySent(prevOddPlayer, sender);
        }
        OddJob.getInstance().getTeleportManager().addRequest(topPlayer.getUniqueId(), bottomUUID);
        OddJob.getInstance().getMessageManager().teleportRequestPlayer(topPlayer, bottomPlayer);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if ((!player.isOp() || sender.hasPermission("teleport.op")) && (sender != player)) {
                if (args[1].isEmpty() || player.getName().startsWith(args[1])) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
