package com.spillhuset.Utils;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommandInterface {
    public abstract boolean allowOp();

    public abstract boolean allowConsole();

    public abstract Plugin getPlugin();

    public abstract String getPermission();

    public ArrayList<SubCommand> subCommands = new ArrayList<>();

    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args);

    /**
     *
     * @param sender CommandSender should be checked
     * @param others boolean others...
     * @return boolean true if access/permission
     */
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

    public boolean checkArgs(int min, int max, String[] args, CommandSender sender, Plugin type) {
        if (max != 0 && args.length > max) {
            OddJob.getInstance().getMessageManager().errorTooManyArgs(type, sender);
            return true;
        } else if (args.length < min) {
            OddJob.getInstance().getMessageManager().errorMissingArgs(type, sender);
            return true;
        }
        return false;
    }
}
