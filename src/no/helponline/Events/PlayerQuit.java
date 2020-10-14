package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuit implements Listener {
    @EventHandler
    public void leave(PlayerQuitEvent event) {
        //ArenaMechanics.cancel(event.getPlayer());
        UUID uuid = event.getPlayer().getUniqueId();
        OddJob.getInstance().getScoreManager().clear(event.getPlayer());

    }
}
