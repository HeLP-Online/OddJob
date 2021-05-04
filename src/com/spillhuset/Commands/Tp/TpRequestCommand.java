package com.spillhuset.Commands.Tp;
import com.spillhuset.Managers.TeleportManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Odd.OddPlayer;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TpRequestCommand extends SubCommand {
    @Override
    public String getName() {
        return "request";
    }

    @Override
    public String getDescription() {
        return "Sends a request to teleport to a given player";
    }

    @Override
    public String getSyntax() {
        return "/tp request <player>";
    }

    @Override
    public String getPermission() {
        return "tp";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        // Teleport tp
        UUID destinationUUID = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
        OddPlayer destinationOddPlayer = OddJob.getInstance().getPlayerManager().getOddPlayer(destinationUUID);
        Player destinationPlayer = destinationOddPlayer.getPlayer();

        if (destinationPlayer == null || !destinationPlayer.isOnline()) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.tp,args[1],sender);
            return;
        } else if((destinationOddPlayer.getBlacklist().contains(player.getUniqueId()) || destinationOddPlayer.getDenyTpa()) && !destinationOddPlayer.getWhitelist().contains(player.getUniqueId())){
            OddJob.getInstance().getMessageManager().tpDenied(args[1],sender);
            return;
        }

        if(OddJob.getInstance().getTeleportManager().hasRequest(player.getUniqueId())) {
            OddJob.getInstance().getMessageManager().tpAlreadySent(destinationOddPlayer.getName(),sender);
            return;
        }
        if (OddJob.getInstance().getTeleportManager().request(player.getUniqueId(),destinationUUID)) {
            OddJob.getInstance().getMessageManager().tpRequestPlayer(destinationPlayer.getName(),sender);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
