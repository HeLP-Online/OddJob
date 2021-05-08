package com.spillhuset.Commands.Warp.Set;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class WarpSetLocation extends SubCommand {
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
        return "location";
    }

    @Override
    public String getDescription() {
        return "Sets a warps location";
    }

    @Override
    public String getSyntax() {
        return "/warp set location <name> [password]";
    }

    @Override
    public String getPermission() {
        return "warp.set";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.warp);
            return;
        }
        Player player = (Player) sender;

        if (!checkArgs(3,4,args,sender, Plugin.warp)) {
            return;
        }

        String password = args.length == 4 ? args[3] : "";
        String name = args[2];

        UUID warp = OddJob.getInstance().getWarpManager().getUUID(name);
        if (warp == null) {
            OddJob.getInstance().getMessageManager().errorWarpNotExists(name, sender);
            return;
        }

        if (OddJob.getInstance().getWarpManager().setLocation(warp, password, player)) {
            OddJob.getInstance().getMessageManager().successWarpSetLocation(name, sender);
            return;
        }
        OddJob.getInstance().getMessageManager().warpWrongPassword(name, sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
