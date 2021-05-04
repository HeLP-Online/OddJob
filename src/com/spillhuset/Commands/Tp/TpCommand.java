package com.spillhuset.Commands.Tp;

import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TpCommand implements TabCompleter, CommandExecutor {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public TpCommand() {
        subCommands.add(new TpPosCommand());
        subCommands.add(new TpRequestCommand());
        subCommands.add(new TpAcceptCommand());
        subCommands.add(new TpDenyCommand());
        subCommands.add(new TpAllCommand());
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
