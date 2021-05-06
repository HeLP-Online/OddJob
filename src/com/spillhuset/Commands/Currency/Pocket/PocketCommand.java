package com.spillhuset.Commands.Currency.Pocket;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class PocketCommand extends SubCommand {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public PocketCommand() {
        subCommands.add(new PocketSetCommand());
        subCommands.add(new PocketSubCommand());
        subCommands.add(new PocketAddCommand());
    }

    @Override
    public String getName() {
        return "pocket";
    }

    @Override
    public String getDescription() {
        return "Utility for a players pocket";
    }

    @Override
    public String getSyntax() {
        return "/currency pocket <args>";
    }

    @Override
    public String getPermission() {
        return "currency.pocket";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args.length >= 2) {
                if (name.equalsIgnoreCase(args[1])) {
                    subCommand.perform(sender, args);
                    return;
                }
            }
            nameBuilder.append(name).append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));

        // /currency pocket

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
