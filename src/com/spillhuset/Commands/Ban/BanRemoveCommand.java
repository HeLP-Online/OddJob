package com.spillhuset.Commands.Ban;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanRemoveCommand extends SubCommand {

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
        return Plugin.ban;
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Removes a ban from the player";
    }

    @Override
    public String getSyntax() {
        return "/ban remove <player>";
    }

    @Override
    public String getPermission() {
        return "ban.remove";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // Check args
        if (checkArgs(2,2,args,sender,getPlugin())) {
            return;
        }

        // Find Player
        UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.player, args[1], sender);
            return;
        }

        if (OddJob.getInstance().getBanManager().getBan(target) != null) {
            OddJob.getInstance().getBanManager().unban(target);
            OddJob.getInstance().getMessageManager().banRemoveSuccess(args[1], sender);
            return;
        }

        OddJob.getInstance().getMessageManager().banRemoveError(args[1], sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (UUID uuid : OddJob.getInstance().getBanManager().getBans()) {
            String name = OddJob.getInstance().getPlayerManager().getName(uuid);
            if (args[1].isEmpty()) {
                list.add(name);
            } else if (name.startsWith(args[1])) {
                list.add(name);
            }
        }
        return list;
    }
}
