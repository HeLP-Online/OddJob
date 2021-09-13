package com.spillhuset.Events;

import com.spillhuset.OddJob;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkLoad implements Listener {
    List<UUID> ignore = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getWorld().getEntitiesByClass(ArmorStand.class)) {
            UUID uuid = entity.getUniqueId();
            // If it is a Spirit
            if (entity.getCustomName() != null && entity.getCustomName().startsWith("The spirit of")) {
                // Check if it is added to the ignoring list
                if (!ignore.contains(uuid)) {
                    // If the Spirit is not in the ignoring list, and it contains things in the DeathManager-inventory
                    if (OddJob.getInstance().getDeathManager().getInventory(uuid) != null) {
                        // Add the Spirit to the ignoring list
                        ignore.add(uuid);
                    } else {
                        // So if the Spirit it not in the ignoring list, and it does not have any DeathManager-inventory, delete it.
                        OddJob.getInstance().log("Removed " + entity.getCustomName());
                        OddJob.getInstance().getDeathManager().remove(uuid);
                    }
                }
            }
        }
    }
}
