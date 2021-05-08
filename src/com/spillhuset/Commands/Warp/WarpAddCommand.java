package com.spillhuset.Commands.Warp;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpAddCommand extends SubCommand {
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
        return "add";
    }

    @Override
    public String getDescription() {
        return "Adds a new warp to the list";
    }

    @Override
    public String getSyntax() {
        return "/warp add <name> [password] [cost]";
    }

    @Override
    public String getPermission() {
        return "warp.admin.add";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.warp);
            return;
        }

        Player player = (Player)  sender;
        if (args.length < 2) {
            OddJob.getInstance().getMessageManager().errorMissingArgs(Plugin.warp,sender);
            return;
        } else if (args.length > 4) {
            OddJob.getInstance().getMessageManager().errorTooManyArgs(Plugin.warp, sender);
            return;
        }

        String name = args[1];
        UUID warp = OddJob.getInstance().getWarpManager().getUUID(name);
        if (warp != null) {
            OddJob.getInstance().getMessageManager().errorWarpExists(name,sender);
            return;
        }

        if (args.length == 4) {
            try {
                double cost = Double.parseDouble(args[3]);
                String password = args[2];
                OddJob.getInstance().getWarpManager().add(player,name,password,cost);
            } catch (NumberFormatException ignored) {
                OddJob.getInstance().getMessageManager().errorNumber(Plugin.warp,args[3],sender);
            }
        } else if (args.length == 3){
            String password = args[2];
            OddJob.getInstance().getWarpManager().add(player,name,password);
        } else {
            OddJob.getInstance().getWarpManager().add(player,name);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            list.add("<name>");
        } else if(args.length == 3) {
            list.add("[password]");
        } else if(args.length == 4) {
            list.add("[cost]");
        }
        return list;
    }
}
