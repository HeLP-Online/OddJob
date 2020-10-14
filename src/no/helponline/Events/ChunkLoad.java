package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkLoad implements Listener {
    List<UUID> ignore = new ArrayList<>();
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity:event.getChunk().getWorld().getEntitiesByClass(ArmorStand.class)) {
            UUID uuid = entity.getUniqueId();
            if (ignore.contains(uuid)) return;
            if (OddJob.getInstance().getDeathManager().getInventory(uuid) != null) {
                ignore.add(uuid);
                return;
            }
            OddJob.getInstance().getDeathManager().remove(uuid);
            OddJob.getInstance().log("Removed "+entity.getUniqueId().toString());
        }
    }
}
