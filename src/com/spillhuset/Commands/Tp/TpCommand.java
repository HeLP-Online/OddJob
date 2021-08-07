package com.spillhuset.Commands.Tp;

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

public class TpCommand extends SubCommandInterface implements TabCompleter, CommandExecutor {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public TpCommand() {
        subCommands.add(new TpPosCommand());
        subCommands.add(new TpRequestCommand());
        subCommands.add(new TpAcceptCommand());
        subCommands.add(new TpDenyCommand());
        subCommands.add(new TpAllCommand());
    }

    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.tp;
    }

    @Override
    public String getPermission() {
        return "tp";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length >= 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
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
            if (args[0].isEmpty()) {
                list.add(name);
            } else if (name.equalsIgnoreCase(args[0]) && args.length > 1) {
                return subCommand.getTab(sender, args);
            } else if (name.startsWith(args[0])) {
                list.add(name);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (sender.getName().equalsIgnoreCase(player.getName())) {
                continue;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase(args[1])) {
                continue;
            }
            if (!sender.isOp() && player.isOp()) {
                continue;
            }
            if (args.length == 1 && player.getName().toLowerCase().startsWith(args[0].toLowerCase()) ||
                    args.length == 2 && player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                list.add(player.getName());
            }
        }
        return list;
    }
}
