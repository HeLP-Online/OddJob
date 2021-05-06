package com.spillhuset.Utils;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public interface SubCommandInterface {
    ArrayList<SubCommand> subCommands = new ArrayList<>();

    boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args);
}
