package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClose implements Listener {
    @EventHandler
    public void inventory(InventoryCloseEvent event) {
        Player player = null;
        HumanEntity human = event.getPlayer();
        if (human instanceof Player) {
            player = (Player) human;
        }

        Inventory inventory = event.getInventory();

        int i = 0;
        for (ItemStack is : inventory.getContents()) {
            if (is != null) i++;
        }

        if (i < 1) {
            if (inventory.getHolder() instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) inventory.getHolder();
                Chest left = (Chest) doubleChest.getLeftSide();
                if (left != null) {
                    OddJob.getInstance().getDeathManager().replace(left.getLocation(), player.getUniqueId());
                }
            }
        }
    }
}
