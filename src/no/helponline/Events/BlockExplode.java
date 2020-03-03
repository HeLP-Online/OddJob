package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

import java.util.List;

public class BlockExplode implements Listener {
    @EventHandler
    public void onExplosion(BlockExplodeEvent event) {
        List<Block> blocks = event.blockList();
        Location location;
        for (Block block : blocks) {
            // Is a Chest
            if (block.getType().equals(Material.CHEST)) {
                Chest chest = (Chest) block.getState();
                // Is a DoubleChest
                if (chest.getInventory().getHolder() instanceof DoubleChest) {
                    DoubleChest doubleChest = (DoubleChest) ((Chest) block.getState()).getInventory().getHolder();
                    if (doubleChest != null) {
                        location = ((Chest) doubleChest.getLeftSide()).getLocation();
                        // Left side
                        if (OddJob.getInstance().getDeathManager().isDeathChest(location)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

}
