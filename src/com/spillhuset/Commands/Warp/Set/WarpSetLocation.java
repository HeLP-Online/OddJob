package com.spillhuset.Commands.Warp.Set;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpSetLocation extends SubCommand {
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
        return "location";
    }

    @Override
    public String getDescription() {
        return "Sets a warps location";
    }

    @Override
    public String getSyntax() {
        return "/warp set location <name> [password=<password>]";
    }

    @Override
    public String getPermission() {
        return "warp.set";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // Check arguments
        if (checkArgs(3, 4, args, sender, Plugin.warp)) {
            return;
        }

        //Check permission
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }

        // Sets initial values
        String password = "";
        String name = args[2];
        UUID uuid = OddJob.getInstance().getWarpManager().getUUID(name);

        // Check if exists
        if (uuid == null) {
            OddJob.getInstance().getMessageManager().errorWarpNotExists(name, sender);
            return;
        }

        if (OddJob.getInstance().getWarpManager().get(uuid).hasPassword()) {
            if (args.length == 4 && args[3].startsWith("password=")) {
                String[] split = args[3].split("=");
                if (split.length > 1) {
                    password = (split[1]);
                }
            }
        }

        if (OddJob.getInstance().getWarpManager().setLocation(uuid, password, ((Player) sender).getLocation())) {
            OddJob.getInstance().getMessageManager().successWarpSetLocation(name, sender);
            return;
        }

        OddJob.getInstance().getMessageManager().warpWrongPassword(name, sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 3) {
            for (Warp warp : OddJob.getInstance().getWarpManager().getAll().values()) {
                if (args[2].isBlank() || warp.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    list.add(warp.getName());
                }
            }
        } else if (args.length == 4) {
            list.add("password=");
        }
        return list;
    }
}
