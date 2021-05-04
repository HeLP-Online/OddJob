package com.spillhuset.Events;

import com.spillhuset.OddJob;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

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

                OddJob.getInstance().getMessageManager().tradeAborted(OddJob.getInstance().getPlayerManager().getName(closing),uuid);
            } else if (OddJob.getInstance().getPlayerManager().getTradingPlayers().containsValue(closing)) {
                // Looping through the list
                for (UUID uuid : OddJob.getInstance().getPlayerManager().getTradingPlayers().keySet()) {
                    // Found a Player in the list
                    if (OddJob.getInstance().getPlayerManager().getTradingPlayers().get(uuid).equals(closing)) {
                        // Removing from the lost
                        OddJob.getInstance().getPlayerManager().getTradingPlayers().remove(uuid);
                        // Closing inventory
                        OddJob.getInstance().getPlayerManager().getPlayer(uuid).closeInventory();

                        OddJob.getInstance().getMessageManager().tradeAborted(OddJob.getInstance().getPlayerManager().getName(closing),uuid);
                    }
                }
            }
        } else if (event.getView().getTitle().equals("DEATH CHEST")) {
            for (HumanEntity human : event.getViewers()) {
                for (UUID entityUUID : OddJob.getInstance().getDeathManager().getOwners().keySet()) {
                    if (OddJob.getInstance().getDeathManager().getOwners().get(entityUUID).equals(human.getUniqueId())) {
                        OddJob.getInstance().getDeathManager().replace(human.getWorld().getUID(), entityUUID, human.getUniqueId());
                    }
                }
            }
        }
    }
}
