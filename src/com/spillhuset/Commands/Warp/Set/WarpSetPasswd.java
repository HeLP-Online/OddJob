package com.spillhuset.Commands.Warp.Set;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class WarpSetPasswd extends SubCommand {
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
        return "password";
    }

    @Override
    public String getDescription() {
        return "Sets a password to a given warp";
    }

    @Override
    public String getSyntax() {
        return "/warp set password <name> [old_pw] [new_pw]";
    }

    @Override
    public String getPermission() {
        return "warp.set";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!checkArgs(4,5,args,sender,Plugin.warp)) {
            return;
        }

        String name = args[2];
        UUID warp = OddJob.getInstance().getWarpManager().getUUID(name);
        if (warp == null) {
            OddJob.getInstance().getMessageManager().errorWarpNotExists(name, sender);
            return;
        }
        if (args.length == 5) {
            String oldPass = args[3];
            String newPass = args[4];

            if (OddJob.getInstance().getWarpManager().setPasswd(warp, newPass, oldPass)) {
                OddJob.getInstance().getMessageManager().successWarpSetPasswd(newPass, name, sender);
                return;
            }
        }


        OddJob.getInstance().getMessageManager().warpWrongPassword(name, sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
