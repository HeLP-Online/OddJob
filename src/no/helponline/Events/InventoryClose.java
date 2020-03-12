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

import java.util.UUID;

public class InventoryClose implements Listener {
    @EventHandler
    public void inventory(InventoryCloseEvent event) {
        // Closing trade windows for both traders
        if (event.getView().getTitle().equals("TRADE INVENTORY")) {
            // Player closing trade windows
            UUID closing = event.getPlayer().getUniqueId();

            if (OddJob.getInstance().getPlayerManager().getTradingPlayers().containsKey(closing)) {
                // Trading player
                UUID uuid = OddJob.getInstance().getPlayerManager().getTradingPlayers().get(closing);
                // Removing from the list
                OddJob.getInstance().getPlayerManager().getTradingPlayers().remove(closing);
                // Closing inventory
                OddJob.getInstance().getPlayerManager().getPlayer(uuid).closeInventory();

                OddJob.getInstance().getMessageManager().danger("Trading aborted by " + OddJob.getInstance().getPlayerManager().getName(closing), uuid, false);
            } else if (OddJob.getInstance().getPlayerManager().getTradingPlayers().containsValue(closing)) {
                // Looping through the list
                for (UUID uuid : OddJob.getInstance().getPlayerManager().getTradingPlayers().keySet()) {
                    // Found a Player in the list
                    if (OddJob.getInstance().getPlayerManager().getTradingPlayers().get(uuid).equals(closing)) {
                        // Removing from the lost
                        OddJob.getInstance().getPlayerManager().getTradingPlayers().remove(uuid);
                        // Closing inventory
                        OddJob.getInstance().getPlayerManager().getPlayer(uuid).closeInventory();

                        OddJob.getInstance().getMessageManager().danger("Trading aborted by " + OddJob.getInstance().getPlayerManager().getName(closing), uuid, false);
                    }
                }
            }
        }

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
