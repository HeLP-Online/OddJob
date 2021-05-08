package com.spillhuset.Commands.Warp;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpDelCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public boolean allowOp() {
        return false;
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
        return "/warp del <name> [password]";
    }

    @Override
    public String getPermission() {
        return "warp.del";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.warp);
            return;
        }

        Player player = (Player) sender;
        if (args.length < 2) {
            OddJob.getInstance().getMessageManager().errorMissingArgs(Plugin.warp,sender);
            return;
        } else if (args.length > 3) {
            OddJob.getInstance().getMessageManager().errorTooManyArgs(Plugin.warp, sender);
            return;
        }

        UUID warp =OddJob.getInstance().getWarpManager().getUUID(args[1]);
        if (warp != null) {
            OddJob.getInstance().getMessageManager().errorWarpExists(args[1], sender);
            return;
        }

        if (args.length == 3) {
            String password = args[2];
            String name = args[1];
            OddJob.getInstance().getWarpManager().del(sender, warp, password);
        } else {
            String name = args[1];
            OddJob.getInstance().getWarpManager().del(sender, warp);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            if(args[1].equalsIgnoreCase("")) {
                list.add("<name>");
            } else {
                for(String name: OddJob.getInstance().getWarpManager().listWarps()) {
                    if (name.startsWith(args[1])) {
                        list.add(name);
                    }
                }
            }
        } else if(args.length == 3) {
            list.add("[password]");
        }
        return list;
    }
}
