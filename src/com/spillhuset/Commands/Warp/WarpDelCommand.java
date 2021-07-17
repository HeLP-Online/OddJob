package com.spillhuset.Commands.Warp;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpDelCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return true;
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
        return "del";
    }

    @Override
    public String getDescription() {
        return "Deletes a give warp";
    }

    @Override
    public String getSyntax() {
        return "/warp del <name> [password=<password>]";
    }

    @Override
    public String getPermission() {
        return "warp.del";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // Check console
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.warp);
            return;
        }

        // Check arguments
        if (checkArgs(2, 3, args, sender, getPlugin())) {
            return;
        }

        // Check permission
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
            return;
        }

        // Sets initial values
        String password = "";
        String name = args[1];
        UUID uuid = OddJob.getInstance().getWarpManager().getUUID(name);

        // Check if exists
        if (uuid == null) {
            OddJob.getInstance().getMessageManager().errorWarpNotExists(args[1], sender);
            return;
        }

        if (OddJob.getInstance().getWarpManager().get(uuid).hasPassword()) {
            if (args.length == 3 && args[2].startsWith("password=")) {
                String[] split = args[2].split("=");
                if (split.length > 1) {
                    password = split[1];
                }
            }
        }

        OddJob.getInstance().getWarpManager().del(sender, uuid, password);

    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (UUID uuid : OddJob.getInstance().getWarpManager().listWarps().keySet()) {
                Warp warp = OddJob.getInstance().getWarpManager().listWarps().get(uuid);
                if (warp.getName().startsWith(args[1]) || args[1].isBlank()) {
                    list.add(warp.getName());
                }
            }
        } else if (args.length == 3) {
            list.add("password=");
        }
        return list;
    }
}
