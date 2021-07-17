package com.spillhuset.Commands.Warp.Set;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class WarpSetCost extends SubCommand {
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
        return "cost";
    }

    @Override
    public String getDescription() {
        return "Sets a warps cost";
    }

    @Override
    public String getSyntax() {
        return "/warp set cost <name> <cost> [password]";
    }

    @Override
    public String getPermission() {
        return "warp.set";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // Args
        if (!checkArgs(4, 5, args, sender, Plugin.warp)) {
            return;
        }

        // Password
        String password = args.length == 5 ? args[4] : "";

        // Cost
        double cost = 0d;
        try {
            cost = Double.parseDouble(args[3]);
        } catch (NumberFormatException ignored) {
            OddJob.getInstance().getMessageManager().invalidNumber(Plugin.currency,args[3], sender);
        }

        // Name
        String name = args[2];

        // UUID
        UUID warp = OddJob.getInstance().getWarpManager().getUUID(name);
        if (warp == null) {
            OddJob.getInstance().getMessageManager().errorWarpNotExists(name, sender);
            return;
        }

        // Setting cost value
        if (OddJob.getInstance().getWarpManager().setCost(warp, cost, password)) {
            OddJob.getInstance().getMessageManager().successWarpSetCost(cost, name, sender);
            return;
        }
        OddJob.getInstance().getMessageManager().warpWrongPassword(name, sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
