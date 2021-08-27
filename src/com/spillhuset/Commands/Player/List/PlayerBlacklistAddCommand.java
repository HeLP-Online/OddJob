package com.spillhuset.Commands.Player.List;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Odd.OddPlayer;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerBlacklistAddCommand extends SubCommand {
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
        return Plugin.players;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getPermission() {
        return "players";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }
        if (checkArgs(3, 3, args, sender, getPlugin())) {
            return;
        }

        OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(((Player) sender).getUniqueId());
        UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[2]);
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[2], sender);
            return;
        }

        oddPlayer.addBlacklist(target);
        OddJob.getInstance().getMessageManager().blacklistAdd(args[2], sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (UUID uuid : OddJob.getInstance().getPlayerManager().getUUIDs()) {
            OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(uuid);
            if (!oddPlayer.getName().equals(sender.getName()) && !oddPlayer.isOp() && !oddPlayer.getBlacklist().contains(uuid)) {
                if (args[2].isEmpty()) {
                    list.add(oddPlayer.getName());
                } else if (oddPlayer.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    list.add(oddPlayer.getName());
                }
            }
        }
        return list;
    }
}
