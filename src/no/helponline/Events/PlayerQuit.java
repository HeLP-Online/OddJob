package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.ArenaMechanics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuit implements Listener {
    @EventHandler
    public void leave(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (OddJob.getInstance().getScoreManager().scores.containsKey(uuid)) {
            OddJob.getInstance().getScoreManager().scores.get(uuid).cancel();
            OddJob.getInstance().getScoreManager().scores.remove(uuid);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        ArenaMechanics.cancel(event.getPlayer());
        UUID uuid = event.getPlayer().getUniqueId();

    }
}
