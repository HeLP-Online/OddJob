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

        OddJob.getInstance().getPlayerManager().getNotInBed(worldUUID).remove(playerUUID);

        OddJob.getInstance().getPlayerManager().setInBed(worldUUID,playerUUID);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp() || player.hasPermission("oddjob.no_sleep")) continue;
            if (OddJob.getInstance().getPlayerManager().getInBed(worldUUID).contains(playerUUID)) continue;
            needToSleep++;
        }

        if (needToSleep == 0) {
            Bukkit.getWorld(worldUUID).setTime(0L);
        }
    }
}
