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

    @EventHandler (priority = EventPriority.HIGH)
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getWorld().getEntitiesByClass(ArmorStand.class)) {
            UUID uuid = entity.getUniqueId();
            if (entity.getCustomName() != null && !entity.getCustomName().startsWith("The spirit of")) {
                return;
            }
            if (ignore.contains(uuid)) return;
            if (OddJob.getInstance().getDeathManager().getInventory(uuid) != null) {
                ignore.add(uuid);
                return;
            }
            OddJob.getInstance().getDeathManager().remove(uuid);
            OddJob.getInstance().log("Removed " + entity.getUniqueId().toString());
        }
    }
}
