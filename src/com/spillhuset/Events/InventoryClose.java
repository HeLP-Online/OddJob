package com.spillhuset.Events;

import com.spillhuset.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.UUID;

public class InventoryClose implements Listener {
    @EventHandler
    public void inventory(InventoryCloseEvent event) {
        // Closing trade windows for both traders
        if (event.getView().getTitle().equals("FAIR TRADE")) {
            // Player closing trade windows
            UUID closing = event.getPlayer().getUniqueId();
            UUID bottomUUID = null;
            UUID topUUID = null;

            // If topPlayer is closing
            if (OddJob.getInstance().getPlayerManager().getTradingPlayers().containsKey(closing)) {
                // Trading player
                bottomUUID = OddJob.getInstance().getPlayerManager().getTradingPlayers().get(closing);
                topUUID = closing;
                OddJob.getInstance().log("closing = top");
                // Removing from the list
                OddJob.getInstance().getPlayerManager().getTradingPlayers().remove(closing);

                // Closing the inventory for bottomPlayer
                OddJob.getInstance().getPlayerManager().getPlayer(bottomUUID).closeInventory();

                OddJob.getInstance().getMessageManager().tradeAborted(OddJob.getInstance().getPlayerManager().getName(closing), bottomUUID);
            } else if (OddJob.getInstance().getPlayerManager().getTradingPlayers().containsValue(closing)) {
                // Looping through the list
                for (UUID uuid : OddJob.getInstance().getPlayerManager().getTradingPlayers().keySet()) {
                    // Found a Player in the list
                    if (OddJob.getInstance().getPlayerManager().getTradingPlayers().get(uuid).equals(closing)) {
                        // Trading players
                        topUUID = uuid;
                        bottomUUID = closing;
                        OddJob.getInstance().log("closing = bottom");
                        // Removing from the lost
                        OddJob.getInstance().getPlayerManager().getTradingPlayers().remove(topUUID);

                        // Closing inventory
                        OddJob.getInstance().getPlayerManager().getPlayer(topUUID).closeInventory();

                        OddJob.getInstance().getMessageManager().tradeAborted(OddJob.getInstance().getPlayerManager().getName(bottomUUID), topUUID);
                    }
                }
            }
            if (event.getInventory().getItem(17).getType().equals(Material.BARRIER))
            for (int i = 0; i < 36; i++) {
                if (i <= 8) {
                    if (topUUID != null) {
                        Player topPlayer = Bukkit.getPlayer(topUUID);
                        if (event.getInventory().getItem(i) != null) {
                            if (topPlayer != null) {
                                topPlayer.getInventory().addItem(event.getInventory().getItem(i));
                            }
                        }
                    }
                } else if (i >= 27) {
                    if (bottomUUID != null) {
                        Player bottomPlayer = Bukkit.getPlayer(bottomUUID);
                        if (event.getInventory().getItem(i) != null) {
                            if (bottomPlayer != null) {
                                bottomPlayer.getInventory().addItem(event.getInventory().getItem(i));
                            }
                        }
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
