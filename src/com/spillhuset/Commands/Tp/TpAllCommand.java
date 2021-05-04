package com.spillhuset.Commands.Tp;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class TpAllCommand extends SubCommand {
    @Override
    public String getName() {
        return "all";
    }

    @Override
    public String getDescription() {
        return "Teleport all players to your location";
    }

    @Override
    public String getSyntax() {
        return "/tp all";
    }

    @Override
    public String getPermission() {
        return "tp.all";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.tp);
            return;
        }

        Player player = (Player) sender;

        for (Player target: Bukkit.getOnlinePlayers()) {
            if (!target.equals(player)) {
                OddJob.getInstance().getMessageManager().tpAllPlayer(target,player.getName());
                OddJob.getInstance().getTeleportManager().teleport(target,player, PlayerTeleportEvent.TeleportCause.COMMAND,false);
            } else {
                OddJob.getInstance().getMessageManager().tpAllTarget(sender);
            }

        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
