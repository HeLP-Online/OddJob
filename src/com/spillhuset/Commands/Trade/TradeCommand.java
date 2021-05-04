package com.spillhuset.Commands.Trade;

import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TradeCommand implements CommandExecutor, TabCompleter {
    private ArrayList<SubCommand> subCommands = new ArrayList<>();

    public TradeCommand(){
        subCommands.add(new TradeRequestCommand());
        subCommands.add(new TradeAcceptCommand());
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
