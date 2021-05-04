package com.spillhuset.Events;

import com.spillhuset.OddJob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClick implements Listener {
    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        // CHECK TRADE
        if (event.getView().getTitle().equals("TRADE INVENTORY")) {
            // Is a trade inventory
            if (event.getCurrentItem() != null && (event.getCurrentItem().equals(OddJob.getInstance().getLockManager().lockWand) || event.getCurrentItem().equals(OddJob.getInstance().getLockManager().infoWand) || event.getCurrentItem().equals(OddJob.getInstance().getLockManager().unlockWand))) {
                // Prevent trading of the Lock tools
                event.setCancelled(true);
            }

            Player player = (Player) event.getWhoClicked();
            if (OddJob.getInstance().getPlayerManager().getTradingPlayers().containsKey(player.getUniqueId())) {
                // Trade Player 1
                //OddJob.getInstance().getMessageManager().console("Player 1");
                if (event.getRawSlot() <= 8 || event.getRawSlot() == 17 || event.getRawSlot() >= 27) {
                    if (event.getRawSlot() == 17) {
                        // Accept button
                        OddJob.getInstance().getMessageManager().console("Accept");
                        OddJob.getInstance().getPlayerManager().acceptTrade(player, event.getCurrentItem());
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            } else if (OddJob.getInstance().getPlayerManager().getTradingPlayers().containsValue(player.getUniqueId())) {
                // Trade Player 2
                //OddJob.getInstance().getMessageManager().console("Player 2");
                if (event.getRawSlot() >= 17) {
                    if (event.getRawSlot() == 17) {
                        // Accept button
                        OddJob.getInstance().getMessageManager().console("Accept");
                        OddJob.getInstance().getPlayerManager().acceptTrade(player, event.getCurrentItem());
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
