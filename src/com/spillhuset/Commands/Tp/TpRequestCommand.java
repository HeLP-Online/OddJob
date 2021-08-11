package com.spillhuset.Commands.Tp;

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

public class TpRequestCommand extends SubCommand {
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
        return Plugin.tp;
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
        return "/tp request <player>";
    }

    @Override
    public String getPermission() {
        return "tp";
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

        Player player = (Player) sender;

        // Find player
        Player destinationPlayer = Bukkit.getPlayer(args[1]);
        if (destinationPlayer == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[1], sender);
            return;
        }

        UUID destinationUUID = player.getUniqueId();
        OddPlayer destinationOddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(destinationUUID);

        if (!destinationPlayer.isOnline()) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[1], sender);
            return;
        }
        // Check: Blacklist Whitelist DenyTPA
        else if ((destinationOddPlayer.getBlacklist().contains(player.getUniqueId()) || destinationOddPlayer.getDenyTpa()) && (destinationOddPlayer.getDenyTpa() && !destinationOddPlayer.getWhitelist().contains(player.getUniqueId()))) {
            OddJob.getInstance().getMessageManager().tpDenied(args[1], sender);
            return;
        }

        // Check Request queue
        if (OddJob.getInstance().getTeleportManager().hasRequest(player.getUniqueId())) {
            OddJob.getInstance().getMessageManager().tpAlreadySent(destinationOddPlayer.getName(), sender);
            return;
        }
        if (OddJob.getInstance().getTeleportManager().request(player.getUniqueId(), destinationUUID)) {
            OddJob.getInstance().getMessageManager().tpRequestPlayer(destinationPlayer.getName(), sender);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOp() || sender.hasPermission("tp.op")) {
                if (args[1].isEmpty() || player.getName().startsWith(args[1])) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
