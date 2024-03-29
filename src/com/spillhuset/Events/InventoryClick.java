package com.spillhuset.Events;

import com.spillhuset.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryClick implements Listener {
    List<ItemStack> tools = new ArrayList<>();

    public InventoryClick() {
        tools = OddJob.getInstance().getLocksManager().getTools();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if (tools.contains(event.getCurrentItem())) {
            OddJob.getInstance().getLocksManager().remove(event.getWhoClicked().getUniqueId());
            event.setCancelled(true);
            return;
        }
        // Find Trades
        if (event.getView().getTitle().equals("FAIR TRADE")) {
            /* Is a trade inventory
            if (event.getCurrentItem() != null && (
                    event.getCurrentItem().equals(OddJob.getInstance().getLocksManager().lockWand) ||
                            event.getCurrentItem().equals(OddJob.getInstance().getLocksManager().infoWand) ||
                            event.getCurrentItem().equals(OddJob.getInstance().getLocksManager().unlockWand)
            )) {
                OddJob.getInstance().log("locked tool");
                // Prevent trading of the Lock tools
                event.setCancelled(true);
            }
            */
            Player player = (Player) event.getWhoClicked();
            UUID topUUID = null;
            UUID bottomUUID = null;
            Player topPlayer = null;
            Player bottomPlayer = null;
            if (OddJob.getInstance().getPlayerManager().getTradingPlayers().containsKey(player.getUniqueId())) {
                topUUID = player.getUniqueId();
                topPlayer = player;
                bottomUUID = OddJob.getInstance().getPlayerManager().getTradingPlayers().get(player.getUniqueId());
                bottomPlayer = Bukkit.getPlayer(bottomUUID);
            } else {
                for (UUID uuid : OddJob.getInstance().getPlayerManager().getTradingPlayers().keySet()) {
                    if (OddJob.getInstance().getPlayerManager().getTradingPlayers().get(uuid) == player.getUniqueId()) {
                        topUUID = uuid;
                        topPlayer = Bukkit.getPlayer(uuid);
                    }
                }
                bottomUUID = player.getUniqueId();
                bottomPlayer = player;
            }

            if (topPlayer == null) {
                OddJob.getInstance().getMessageManager().tradeNotOnline(bottomPlayer);
                return;
            } else if (bottomPlayer == null) {
                OddJob.getInstance().getMessageManager().tradeNotOnline(topPlayer);
                return;
            }

            if (event.getRawSlot() >= 36) {
                // Own inventory
                OddJob.getInstance().log(">=36");
            } else if (event.getRawSlot() >= 0 && event.getRawSlot() <= 8 && player == topPlayer) {
                // Player top inventory
                OddJob.getInstance().log(">=0 && <=8");
            } else if (event.getRawSlot() >= 27 && event.getRawSlot() <= 35 && player == bottomPlayer) {
                // Player bottom inventory
                OddJob.getInstance().log(">=27 && <=35");
            } else if (event.getRawSlot() == 17) {
                // Accept button
                OddJob.getInstance().log("== 17");
                OddJob.getInstance().getPlayerManager().acceptTrade(player, event.getCurrentItem());
                event.setCancelled(true);
            } else if ((event.getRawSlot() >= 9 && event.getRawSlot() <= 11) || (event.getRawSlot() >= 18 && event.getRawSlot() <= 20)) {
                // Currency buttons
                OddJob.getInstance().log("(>=9 && <=11) || (>=18 && <=20)");
                OddJob.getInstance().getPlayerManager().tradeBalance(event.getCurrentItem(), player);
                event.setCancelled(true);
            } else {
                // anything else
                event.setCancelled(true);
            }
            OddJob.getInstance().log(player.getName() + ": " + event.getRawSlot() + " c:" + event.isCancelled());
            // Line 1 = 0-8
            // Line 2 = 9-17
            // Line 3 = 18-26
            // Line 4 = 27-35


        }
    }
}
