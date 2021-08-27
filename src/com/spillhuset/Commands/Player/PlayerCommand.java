package com.spillhuset.Commands.Player;

import com.spillhuset.Commands.Player.List.PlayerBlacklistCommand;
import com.spillhuset.Commands.Player.List.PlayerWhitelistCommand;
import com.spillhuset.Commands.Player.Set.PlayerSetCommand;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Odd.OddPlayer;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public PlayerCommand() {
        subCommands.add(new PlayerSetCommand());
        subCommands.add(new PlayerWhitelistCommand());
        subCommands.add(new PlayerBlacklistCommand());
        subCommands.add(new PlayerLoadCommand());
        subCommands.add(new PlayerSaveCommand());
    }

    @Override
    public boolean denyConsole() {
        return false;
    }

    @Override
    public boolean onlyConsole() {
        return false;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public boolean onlyOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.players;
    }

    @Override
    public String getPermission() {
        return "players";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subcommand : subCommands) {
            String name = subcommand.getName();
            if (args.length >= 1 && name.equalsIgnoreCase(args[0])) {
                subcommand.perform(sender, args);
                return true;
            }
            nameBuilder.append(name).append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
        OddPlayer target = null;
        if (args.length == 1 && !args[0].isEmpty()) {
            if (can(sender, true)) {
                target = OddJob.getInstance().getPlayerManager().getOddPlayer(OddJob.getInstance().getPlayerManager().getUUID(args[0]));
            } else {
                OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
                return true;
            }
        } else if (sender instanceof Player) {
            target = OddJob.getInstance().getPlayerManager().getOddPlayer(((Player) sender).getUniqueId());
        }

        if (target != null) {
            OddJob.getInstance().getMessageManager().playerInfoAreOp(target, sender);
            OddJob.getInstance().getMessageManager().playerInfoDenyTPA(target, sender);
            OddJob.getInstance().getMessageManager().playerInfoDenyTrade(target, sender);
            OddJob.getInstance().getMessageManager().playerInfoBlacklist(target,sender);
            OddJob.getInstance().getMessageManager().playerInfoWhitelist(target,sender);
        } else {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[0], sender);
        }
        OddJob.getInstance().getMessageManager().infoArgs(getPlugin(), nameBuilder.toString(), sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (!subCommand.can(sender, false)) {
                continue;
            }
            if (args[0].isEmpty()) {
                list.add(name);
            } else if (name.equalsIgnoreCase(args[0]) && args.length > 1) {
                return subCommand.getTab(sender, args);
            } else if (name.startsWith(args[0])) {
                list.add(name);
            }
        }
        return list;
    }

    public String getSyntax() {
        return null;
    }
}
