package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.UUID;

public class PlayerBedEnter implements Listener {
    @EventHandler
    public void enterBed(PlayerBedEnterEvent event) {
        UUID worldUUID = event.getBed().getWorld().getUID();
        UUID playerUUID = event.getPlayer().getUniqueId();
        int needToSleep = 0;
        int i = 0;

        if (OddJob.getInstance().getPlayerManager().getNotInBed(worldUUID) != null)
            OddJob.getInstance().getPlayerManager().getNotInBed(worldUUID).remove(playerUUID);

        OddJob.getInstance().getPlayerManager().setInBed(worldUUID, playerUUID);

        for (Player player : Bukkit.getOnlinePlayers()) {
            OddJob.getInstance().getMessageManager().console("Looping");
            if (player.isOp() || player.hasPermission("oddjob.no_sleep")) {
                // Has permission to not sleep
                OddJob.getInstance().getMessageManager().console("Has permission");
            } else if (OddJob.getInstance().getPlayerManager().getInBed(worldUUID).contains(player.getUniqueId())) {
                // Player is already in bed
                OddJob.getInstance().getMessageManager().console("Is sleeping");
            } else {
                // Player needs to sleep
                OddJob.getInstance().getPlayerManager().setNotInBed(worldUUID, player.getUniqueId());
                needToSleep++;

                OddJob.getInstance().getMessageManager().console("More needs to sleep: " + needToSleep);
            }
        }
        OddJob.getInstance().getMessageManager().console("Need to sleep: " + needToSleep);
        OddJob.getInstance().getMessageManager().console("Is sleeping: " + OddJob.getInstance().getPlayerManager().getInBed(worldUUID).size());
        OddJob.getInstance().getMessageManager().console("Is not sleeping: " + OddJob.getInstance().getPlayerManager().getNotInBed(worldUUID).size());
        OddJob.getInstance().getMessageManager().console("Online players: " + Bukkit.getOnlinePlayers().size());

        if (needToSleep == 0) {
            Bukkit.getWorld(worldUUID).setTime(0L);
        }
    }
}
