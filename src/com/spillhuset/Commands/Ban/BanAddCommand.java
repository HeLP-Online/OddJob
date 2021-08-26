package com.spillhuset.Commands.Ban;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanAddCommand extends SubCommand {
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
        return "add";
    }

    @Override
    public String getDescription() {
        return "Makes a player banned";
    }

    @Override
    public String getSyntax() {
        return "/ban add <name> [description]";
    }

    @Override
    public String getPermission() {
        return "ban.add";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // Check args
        if (checkArgs(2, 0, args, sender, getPlugin())) {
            return;
        }

        // Find Player
        UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.players, args[1], sender);
            return;
        }

        // Make string message
        String ban = OddJob.getInstance().getBanManager().getBan(target);
        if (ban == null) {
            StringBuilder sb = new StringBuilder();
            if (args.length >= 3) {
                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
            } else {
                sb.append("punishment");
            }
            OddJob.getInstance().getBanManager().ban(target, sb.toString());
            OddJob.getInstance().getMessageManager().banAddedSuccess(args[1], sb.toString(), sender.getName(), sender);
            return;
        }

        OddJob.getInstance().getMessageManager().banAddError(args[1], sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (String name : OddJob.getInstance().getPlayerManager().getNames()) {
                if (args[1].isEmpty()) {
                    list.add(name);
                } else if (name.startsWith(args[1])) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
