package com.spillhuset.Utils;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SubCommand {
    public abstract boolean allowConsole();

    public abstract boolean allowOp();

    public abstract Plugin getPlugin();

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract String getPermission();

    public abstract void perform(CommandSender sender, String[] args);

    public abstract List<String> getTab(CommandSender sender, String[] args);

    /**
     * @param min    minimum amount of args
     * @param max    maximum amount of args
     * @param args   the arguments
     * @param sender the sender
     * @param type   SubPlugin
     * @return boolean, false if requirements are not met
     */
    public boolean checkArgs(int min, int max, String[] args, CommandSender sender, Plugin type) {
        if (max != 0 && args.length > max) {
            OddJob.getInstance().getMessageManager().errorTooManyArgs(type, sender);
            OddJob.getInstance().getMessageManager().sendSyntax(type, getSyntax(), sender);
            return true;
        } else if (args.length < min) {
            OddJob.getInstance().getMessageManager().errorMissingArgs(type, sender);
            OddJob.getInstance().getMessageManager().sendSyntax(type, getSyntax(), sender);
            return true;
        }
        return false;
    }

    public boolean can(CommandSender sender, boolean others) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().log("console = "+allowConsole());
            return allowConsole();
        } else if (sender.isOp()) {
            OddJob.getInstance().log("op = "+allowOp());
            return allowOp();
        } else {
            if (others) {
                OddJob.getInstance().log("others = "+sender.hasPermission(getPermission()+".others"));
                return sender.hasPermission(getPermission() + ".others");
            } else {
                OddJob.getInstance().log("permission = "+sender.hasPermission(getPermission()));
                return sender.hasPermission(getPermission());
            }
        }
    }

    public boolean needGuild() {
        return true;
    }
}
