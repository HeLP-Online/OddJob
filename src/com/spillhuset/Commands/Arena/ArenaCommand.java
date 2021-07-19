package com.spillhuset.Commands.Arena;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ArenaCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public ArenaCommand() {
        subCommands.add(new ArenaCreateCommand());
    }

    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public boolean allowConsole() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.arena;
    }

    @Override
    public String getPermission() {
        return "arena";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args.length >= 1 && name.equalsIgnoreCase(args[0])) {
                subCommand.perform(sender, args);
                return true;
            }
            nameBuilder.append(name).append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(";"));

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
        return list;
    }
}
