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

public class PlayerWhitelistRemoveCommand extends SubCommand {
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
        return "remove";
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

        if (!oddPlayer.getWhitelist().contains(target)) {
            OddJob.getInstance().getMessageManager().whitelistNotFound(args[2],sender);
            return;
        }

        oddPlayer.removeWhitelist(target);
        OddJob.getInstance().getMessageManager().whitelistDel(args[2], sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(((Player)sender).getUniqueId());
        if (oddPlayer.getWhitelist().size() > 0) {
            for (UUID uuid : oddPlayer.getWhitelist()) {
                list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
            }
        }
        return list;
    }
}
