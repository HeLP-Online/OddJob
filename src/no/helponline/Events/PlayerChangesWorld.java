package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangesWorld implements Listener {
    @EventHandler
    public void init(PlayerChangedWorldEvent event) {
        OddJob.getInstance().getMySQLManager().updateWorlds(event.getPlayer().getWorld());
    }
}
