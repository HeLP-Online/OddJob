package no.helponline.Commands.Ban;

import no.helponline.OddJob;
import no.helponline.Utils.SubCommand;
import no.helponline.Utils.SubCommandInterface;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BanCommand implements CommandExecutor, TabCompleter, SubCommandInterface {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public BanCommand() {
        subCommands.add(new BanAddCommand());
        subCommands.add(new BanListCommand());
        subCommands.add(new BanRemoveCommand());
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if(name.equalsIgnoreCase(args[0])) {
                subCommand.perform(sender,args);
                return true;
            }
            nameBuilder.append(name).append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));

        sender.sendMessage(ChatColor.GOLD + "args: " + ChatColor.RESET + nameBuilder.toString());
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

