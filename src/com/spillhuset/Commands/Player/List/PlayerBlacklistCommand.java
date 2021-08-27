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

public class PlayerBlacklistCommand extends SubCommand {
    private final List<SubCommand> subCommands = new ArrayList<>();

    public PlayerBlacklistCommand() {
        subCommands.add(new PlayerBlacklistAddCommand());
        subCommands.add(new PlayerBlacklistRemoveCommand());
    }
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
        return Plugin.players;
    }

    @Override
    public String getName() {
        return "blacklist";
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
        UUID targetUUID;
        OddPlayer target;
        if (args.length == 1 && can(sender,false)) {
            targetUUID = ((Player)sender).getUniqueId();
            target = OddJob.getInstance().getPlayerManager().getOddPlayer(targetUUID);
            OddJob.getInstance().getMessageManager().playerBlacklist(target,sender);
        } else {
            if (args.length==2 && can(sender,true)) {
                targetUUID = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                if (targetUUID == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(),args[1],sender);
                    return;
                }
                target = OddJob.getInstance().getPlayerManager().getOddPlayer(targetUUID);
                OddJob.getInstance().getMessageManager().playerBlacklist(target,sender);
            }
        }
// SubCommands
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subcommand : subCommands) {
            String name = subcommand.getName();
            if (args.length >= 2 && name.equalsIgnoreCase(args[1])) {
                subcommand.perform(sender, args);
                return;
            }
            nameBuilder.append(name).append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
        OddJob.getInstance().getMessageManager().infoArgs(Plugin.players, nameBuilder.toString(), sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args[1].isEmpty()) {
                list.add(name);
            } else if (name.equalsIgnoreCase(args[1]) && args.length > 2) {
                return subCommand.getTab(sender, args);
            } else if (name.startsWith(args[1])) {
                list.add(name);
            }
        }
        return list;
    }
}
