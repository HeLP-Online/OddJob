package com.spillhuset.Commands.Warp;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpAddCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return false;
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
        return "add";
    }

    @Override
    public String getDescription() {
        return "Adds a new warp to the list";
    }

    @Override
    public String getSyntax() {
        return "/warp add <name> [password=<password>] [cost=<value>]";
    }

    @Override
    public String getPermission() {
        return "warp.admin.add";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // Check console
        if (!(sender instanceof Player player)) {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.warp);
            return;
        }

        // Check arguments
        if (checkArgs(2, 4, args, sender, getPlugin())) {
            return;
        }

        // Check permission
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
            return;
        }

        // Sets initial values
        String name = args[1];
        String password = "";
        double value = 0D;

        // Check name
        if (OddJob.getInstance().getWarpManager().getUUID(name) != null) {
            OddJob.getInstance().getMessageManager().errorWarpExists(name, sender);
            return;
        }

        // Check attributes
        for (int i = 2; i < args.length; i++) {
            String[] split = args[i].split("=");
            if (split[0].equalsIgnoreCase("password")) {
                password = split[1];
            } else if (split[0].equalsIgnoreCase("cost")) {
                value = Double.parseDouble(split[1]);
            } else {
                OddJob.getInstance().getMessageManager().sendSyntax(getPlugin(), getSyntax(), sender);
                return;
            }
        }
        OddJob.getInstance().getWarpManager().add(player, name, password, value);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            list.add("name");
        } else if (args.length == 3) {
            list.add("password=");
            list.add("cost=");
        } else if (args.length == 4) {
            if (args[2].toLowerCase().startsWith("cost")) {
                list.add("password=");
            }
            if (args[2].toLowerCase().startsWith("password")) {
                list.add("cost=");
            }
        }
        return list;
    }
}
