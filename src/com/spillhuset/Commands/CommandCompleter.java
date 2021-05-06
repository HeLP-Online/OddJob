package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class CommandCompleter {
    public abstract boolean onCommand(CommandSender sender, Command command, String s, String[] args);
    public abstract List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args);
    public abstract String getSyntax();
    boolean checkArgs(int min, int max, String[] args, CommandSender sender, Plugin type) {
        if (args.length > max) {
            OddJob.getInstance().getMessageManager().errorTooManyArgs(type,sender);
            OddJob.getInstance().getMessageManager().sendSyntax(type,getSyntax(),sender);
            return true;
        }
        if (args.length < min) {
            OddJob.getInstance().getMessageManager().errorMissingArgs(type,sender);
            OddJob.getInstance().getMessageManager().sendSyntax(type,getSyntax(),sender);
            return true;
        }
        return false;
    }
}
