package com.spillhuset.Commands.Homes;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HomeListCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return true;
    }

    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.home;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "List homes";
    }

    @Override
    public String getSyntax() {
        return "/homes list";
    }

    @Override
    public String getPermission() {
        return "homes.use";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        UUID target;
        String targetName;
        if (!(sender instanceof Player)) {
            if (checkArgs(2, 2, args, sender, getPlugin())) {
                return;
            }
            targetName = args[1];
            target = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
        } else {
            if (checkArgs(1, 2, args, sender, getPlugin())) {
                return;
            }
            if (args.length == 2) {
                if (can(sender, true)) {
                    targetName = args[1];
                    target = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                } else {
                    OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
                    return;
                }
            } else {
                targetName = sender.getName();
                target = ((Player) sender).getUniqueId();
            }
        }

        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), targetName, sender);
            return;
        }
        OddJob.getInstance().getHomesManager().list(target, sender);

    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
