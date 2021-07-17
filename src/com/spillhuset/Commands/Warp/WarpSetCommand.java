package com.spillhuset.Commands.Warp;

import com.spillhuset.Commands.Warp.Set.WarpSetCost;
import com.spillhuset.Commands.Warp.Set.WarpSetLocation;
import com.spillhuset.Commands.Warp.Set.WarpSetName;
import com.spillhuset.Commands.Warp.Set.WarpSetPasswd;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class WarpSetCommand extends SubCommand {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public WarpSetCommand() {
        subCommands.add(new WarpSetLocation());
        subCommands.add(new WarpSetCost());
        subCommands.add(new WarpSetPasswd());
        subCommands.add(new WarpSetName());
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
        return Plugin.warp;
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Set new information about a warp";
    }

    @Override
    public String getSyntax() {
        return "/warp set <location,cost,passwd,name> <value>";
    }

    @Override
    public String getPermission() {
        return "warp.set";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // Check args
        if (checkArgs(2, 0, args, sender, getPlugin())) {
            return;
        }

        // Check permission
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }

        // Make subcommands
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (name.equalsIgnoreCase(args[1])) {
                subCommand.perform(sender, args);
                return;
            }
            nameBuilder.append(name).append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
        // Fallback
        sender.sendMessage(ChatColor.GOLD + "args: " + ChatColor.RESET + nameBuilder.toString());
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
