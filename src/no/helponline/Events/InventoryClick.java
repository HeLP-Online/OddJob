package no.helponline.Events;

import no.helponline.OddJob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class InventoryClick implements Listener {

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        if (event.getItem().equals(OddJob.getInstance().getLockManager().lockWand) || event.getItem().equals(OddJob.getInstance().getLockManager().infoWand) || event.getItem().equals(OddJob.getInstance().getLockManager().unlockWand)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        // Trade
        if (event.getView().getTitle().equals("TRADE INVENTORY")) {
            // Prevent trading of the Lock tools
            if (event.getCurrentItem() != null && (event.getCurrentItem().equals(OddJob.getInstance().getLockManager().lockWand) || event.getCurrentItem().equals(OddJob.getInstance().getLockManager().infoWand) || event.getCurrentItem().equals(OddJob.getInstance().getLockManager().unlockWand))) {
                event.setCancelled(true);
            }
            Player player = (Player) event.getWhoClicked();
            OddJob.getInstance().getMessageManager().console("Trading");
            OddJob.getInstance().getMessageManager().console("Slot: " + event.getSlot());
            OddJob.getInstance().getMessageManager().console("Slot-type: " + event.getSlotType().name());
            OddJob.getInstance().getMessageManager().console("Raw-Slot: " + event.getRawSlot());
            OddJob.getInstance().getMessageManager().console("Action: " + event.getAction().name());
            if (OddJob.getInstance().getPlayerManager().getTradingPlayers().containsKey(player.getUniqueId())) {
                OddJob.getInstance().getMessageManager().console("Player 1");
                if (event.getRawSlot() <= 8 || event.getRawSlot() == 17 || event.getRawSlot() >= 27) {
                    if (event.getRawSlot() == 17) {
                        OddJob.getInstance().getMessageManager().console("Accept");
                        OddJob.getInstance().getPlayerManager().acceptTrade(player, event.getCurrentItem());
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            } else if (OddJob.getInstance().getPlayerManager().getTradingPlayers().containsValue(player.getUniqueId())) {
                OddJob.getInstance().getMessageManager().console("Player 2");
                if (event.getRawSlot() >= 17) {
                    if (event.getRawSlot() == 17) {
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
