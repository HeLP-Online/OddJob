package com.spillhuset.Commands.Teleport;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public class TeleportCommand extends SubCommandInterface implements TabCompleter, CommandExecutor {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public TeleportCommand() {
        subCommands.add(new TeleportPositionCommand());
        subCommands.add(new TeleportRequestCommand());
        subCommands.add(new TeleportAcceptCommand());
        subCommands.add(new TeleportDenyCommand());
        subCommands.add(new TeleportAllCommand());
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
        return Plugin.teleport;
    }

    @Override
    public String getPermission() {
        return "teleport";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length >= 1 && can("teleport.admin",sender)) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                if (args.length == 1) {
                    OddJob.getInstance().getTeleportManager().teleport((Player) sender, target, PlayerTeleportEvent.TeleportCause.COMMAND, false);
                    return true;
                }
                Player dest = Bukkit.getPlayer(args[1]);
                if (dest == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[1], sender);
                    return true;
                }
                OddJob.getInstance().getTeleportManager().teleport(target, dest, PlayerTeleportEvent.TeleportCause.COMMAND, true);
                return true;
            }
        }
        // SubCommands
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

        OddJob.getInstance().getMessageManager().infoArgs(getPlugin(), nameBuilder.toString(), sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (!can(subCommand.getPermission(),sender)) {
            } else if (args[0].isEmpty()) {
                list.add(name);
            } else if (name.equalsIgnoreCase(args[0]) && args.length > 1) {
                return subCommand.getTab(sender, args);
            } else if (name.startsWith(args[0])) {
                list.add(name);
            }
        }
        if (can("teleport.admin", sender)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (sender.getName().equalsIgnoreCase(player.getName())) {

                }
                if (args.length == 2 && args[0].equalsIgnoreCase(args[1])) {

                }
                if (!sender.isOp() && player.isOp()) {

                }
                if (args.length == 1 && player.getName().toLowerCase().startsWith(args[0].toLowerCase()) ||
                        args.length == 2 && player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
