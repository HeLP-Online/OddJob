package com.spillhuset.Commands.Player;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerLoadCommand extends SubCommand {
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
        return Plugin.player;
    }

    @Override
    public String getName() {
        return "load";
    }

    @Override
    public String getDescription() {
        return "Loads the list of Players from database";
    }

    @Override
    public String getSyntax() {
        return "/player load [name=<name>|uuid=<uuid>]";
    }

    @Override
    public String getPermission() {
        return "players.load";
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
            OddJob.getInstance().getPlayerManager().load();
            return;
        }

        if (args.length == 2) {
            UUID uuid = null;
            String[] split = args[1].split("=");
            if (split.length == 2) {
                if (split[0].equalsIgnoreCase("uuid")) {
                    OddJob.getInstance().log("uuid");
                    OddJob.getInstance().log(args[1]);
                    uuid = UUID.fromString(split[1]);
                } else if (split[0].equalsIgnoreCase("name")) {
                    OddJob.getInstance().log("name");
                    OddJob.getInstance().log(args[1]);
                    uuid = OddJob.getInstance().getPlayerManager().getUUID(split[1]);
                }
                if (uuid == null) {
                    OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(),split[1],sender);
                    return;
                }
                OddJob.getInstance().getPlayerManager().loadPlayer(uuid);
                return;
            }
        }
        OddJob.getInstance().getMessageManager().sendSyntax(getPlugin(),getSyntax(),sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args[1].startsWith("name=")) {
            for (String name : OddJob.getInstance().getPlayerManager().getNames()) {
                if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                    list.add("name="+name);
                }
            }
        }else if ("name=".startsWith(args[1])) {
            list.add("name=");
        }

        if (args[1].startsWith("uuid=")) {
            for (UUID uuid : OddJob.getInstance().getPlayerManager().getUUIDs()) {
                if (uuid.toString().startsWith(args[1].toLowerCase())) {
                    list.add("uuid="+uuid);
                }
            }
        }else if ("uuid=".startsWith(args[1])) {
            list.add("uuid=");
        }
        return list;
    }
}
