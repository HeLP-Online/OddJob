package com.spillhuset.Commands.Arena.Set;

import com.spillhuset.Commands.Player.Set.PlayerSetScoreboardCommand;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ArenaSetCommand extends SubCommand {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public ArenaSetCommand() {
        subCommands.add(new ArenaSetAreaCommand());
    }

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
        return Plugin.arena;
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Arena 'set' menu";
    }

    @Override
    public String getSyntax() {
        return "/arena <name> set ...";
    }

    @Override
    public String getPermission() {
        return "arena.admin";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // SubCommands
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subcommand : subCommands) {
            String name = subcommand.getName();
            if (args.length >= 2 && name.equalsIgnoreCase(args[1]) && subcommand.can(sender,false)) {
                subcommand.perform(sender, args);
                return;
            }
            nameBuilder.append(name).append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
        OddJob.getInstance().getMessageManager().infoArgs(Plugin.arena, nameBuilder.toString(), sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args[1].isEmpty()) {
                list.add(name);
            } else if (name.equalsIgnoreCase(args[1]) && args.length > 2 && can(sender,false)) {
                return subCommand.getTab(sender, args);
            } else if (name.startsWith(args[1])) {
                list.add(name);
            }
        }
        return list;
    }
}
