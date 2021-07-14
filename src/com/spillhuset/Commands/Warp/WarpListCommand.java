package com.spillhuset.Commands.Warp;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class WarpListCommand extends SubCommand {
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
        return "list";
    }

    @Override
    public String getDescription() {
        return "Lists all warps";
    }

    @Override
    public String getSyntax() {
        return "/warp list";
    }

    @Override
    public String getPermission() {
        return "warps.list";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender,false)) {
            return;
        }

        if (checkArgs(1,1,args,sender,getPlugin())) {
            return;
        }

        OddJob.getInstance().getMessageManager().infoListWarps(OddJob.getInstance().getWarpManager().listWarps(),sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
