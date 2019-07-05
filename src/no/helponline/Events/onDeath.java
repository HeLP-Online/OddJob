package no.helponline.Events;

import no.helponline.Managers.HomesManager;
import no.helponline.Managers.PlayerManager;
import no.helponline.OddJob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class onDeath implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Location location = player.getLocation();
        location.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) location.getBlock().getState();
        chest.getBlockInventory().addItem(player.getInventory().getContents());
        chest.update(true, true);

        if (OddJob.deathChest.containsKey(player.getUniqueId())) {
            Location trap = OddJob.deathChest.get(player.getUniqueId());
            trap.getBlock().setType(Material.TRAPPED_CHEST);
            Block block = trap.getBlock().getRelative(0, -2, 0);
            block.setType(Material.TNT);
        }
        OddJob.deathChest.put(player.getUniqueId(), location);
        player.setCompassTarget(location);


    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && (event.getClickedBlock().getType().equals(Material.CHEST) || event.getClickedBlock().getType().equals(Material.TRAPPED_CHEST))) {
            if (OddJob.deathChest.containsValue(event.getClickedBlock().getLocation())) {
                for (UUID uuid : OddJob.deathChest.keySet()) {
                    if (OddJob.deathChest.get(uuid).equals(event.getClickedBlock().getLocation())) {
                        PlayerManager.getPlayer(uuid).setCompassTarget(HomesManager.get(uuid));
                    }
                    Location location = OddJob.deathChest.get(uuid);
                    location.getBlock().setType(Material.AIR);
                }
            }
        }
    }
}
