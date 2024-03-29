package com.spillhuset.Commands.Player.Set;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class PlayerSetCommand extends SubCommand {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public PlayerSetCommand() {
        //subCommands.add(new PlayerSetScoreboardCommand());
        subCommands.add(new PlayerSetDenyTPACommand());
        subCommands.add(new PlayerSetDenyTradeCommand());
    }

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
        return "set";
    }

    @Override
    public String getDescription() {
        return "Players 'set' menu";
    }

    @Override
    public String getSyntax() {
        return "/player set ...";
    }

    @Override
    public String getPermission() {
        return "player";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
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
