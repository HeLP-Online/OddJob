package com.spillhuset.Commands.Tp;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TpAcceptCommand extends SubCommand {
    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String getDescription() {
        return "Accepts an incoming teleport request";
    }

    @Override
    public String getSyntax() {
        return "/tp accept";
    }

    @Override
    public String getPermission() {
        return "tp";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.tp);
        } else {

            UUID playerUUID = ((Player) sender).getUniqueId();

            UUID targetUUID = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
            if (targetUUID == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(Plugin.tp, args[1], sender);
                return;
            }

            if(OddJob.getInstance().getTeleportManager().hasRequest(targetUUID)){
                OddJob.getInstance().getTeleportManager().accept(targetUUID);
            }
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
