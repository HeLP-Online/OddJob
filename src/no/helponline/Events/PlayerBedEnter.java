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
            if (player.isOp() || player.hasPermission("oddjob.no_sleep")) {
                // Has permission to not sleep
            } else if (OddJob.getInstance().getPlayerManager().getInBed(worldUUID).contains(player.getUniqueId())) {
                // Player is already in bed
            } else {
                // Player needs to sleep
                OddJob.getInstance().getPlayerManager().setNotInBed(worldUUID, player.getUniqueId());
                needToSleep++;
            }
        }

        if (needToSleep == 0) {
            OddJob.getInstance().getPlayerManager().sleep(worldUUID);
        }
    }
}
