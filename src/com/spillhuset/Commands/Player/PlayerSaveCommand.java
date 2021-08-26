package com.spillhuset.Commands.Player;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class PlayerSaveCommand extends com.spillhuset.Utils.SubCommand {
    @Override
    public boolean allowConsole() {
        return true;
    }

    @Override
    public boolean allowOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.players;
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "Saves player to the database";
    }

    @Override
    public String getSyntax() {
        return "/player save [name=<name>|uuid=<uuid>]";
    }

    @Override
    public String getPermission() {
        return "players.save";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(1,2,args,sender,getPlugin())) {
            return;
        }

        if (!can(sender,true)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
            return;
        }

        if (args.length == 1) {
            OddJob.getInstance().getPlayerManager().save();
            return;
        }

        if (args.length == 2) {
            UUID uuid = null;
            String[] split = args[1].split("=");
            if (split.length == 2) {
                if (split[0].equalsIgnoreCase("uuid")) {
                    uuid = UUID.fromString(split[1]);
                } else if (split[0].equalsIgnoreCase("name")) {
                    uuid = OddJob.getInstance().getPlayerManager().getUUID(split[1]);
                }
                if (uuid == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(),split[1],sender);
                    return;
                }
                OddJob.getInstance().getPlayerManager().savePlayer(uuid);
                return;
            }
        }
        OddJob.getInstance().getMessageManager().sendSyntax(getPlugin(),getSyntax(),sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
