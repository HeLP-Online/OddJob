package no.helponline.Events;

import no.helponline.Managers.LockManager;
import no.helponline.Managers.MessageManager;
import no.helponline.Managers.PlayerManager;
import no.helponline.OddJob;
import no.helponline.Utils.Utility;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class LocksEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        boolean door = false;
        boolean chest = false;
        boolean duo = false;
        boolean lower = false;
        boolean left = false;
        UUID uuid = null;

        if ((event.getHand() == EquipmentSlot.OFF_HAND) || (event.getClickedBlock() == null)) {
            return;
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.PHYSICAL)) {
            Block block = event.getClickedBlock();
            Material t = block.getType();

            OddJob.getInstance().log(block.getState().getBlockData().getAsString());
            if (LockManager.getLockable().contains(t)) {
                try {
                    if (LockManager.getDoors().contains(t)) {
                        door = true;
                        // changing <block>
                        block = Utility.getLowerLeftDoor(block).getBlock();
                        uuid = LockManager.isLocked(block.getLocation());

                    } else if (t.equals(Material.CHEST)) {
                        // changing <block>
                        chest = true;
                        block = Utility.getChestPosition(block).getBlock();
                        uuid = LockManager.isLocked(block.getLocation());

                    } else {
                        uuid = LockManager.isLocked(block.getLocation());
                    }
                    OddJob.getInstance().log("UUID: " + uuid + " : " + PlayerManager.getPlayer(uuid));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (uuid != null) {
                    if (player.getInventory().getItemInMainHand().equals(LockManager.makeSkeletonKey())) {
                        if (door) {
                            Utility.doorToggle(block);
                            player.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
                            event.setCancelled(true);
                        }
                        player.sendMessage(ChatColor.RED + "! " + ChatColor.RESET + "Lock open by the awesome Skeletonkey!");
                        return;
                    }
                    if (player.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                        try {
                            if (meta.hasLore()) {
                                List<String> lore = meta.getLore();
                                if (lore.size() > 1) {
                                    String one = ChatColor.stripColor(lore.get(1));
                                    if (!one.equalsIgnoreCase(uuid.toString())) {
                                        OddJob.getInstance().log("compared: " + lore.get(1) + " vs " + uuid.toString());
                                        player.sendMessage(ChatColor.YELLOW + "You don't have the correct key");
                                        event.setCancelled(true);
                                        return;
                                    }
                                    player.sendMessage(ChatColor.YELLOW + "Lock open by key!");
                                    if (door) {
                                        Utility.doorToggle(block);
                                        player.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
                                        event.setCancelled(true);
                                    }
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    player.sendMessage(ChatColor.RED + "This lock is owned by someone else.");
                    event.setCancelled(true);
                }

                if (player.getInventory().getItemInMainHand().equals(LockManager.infoWand)) {
                    OddJob.getInstance().log("Info Wand");
                    if (LockManager.isLockInfo(player.getUniqueId())) {
                        MessageManager.sendMessage(player, ChatColor.YELLOW + "The lock is owned by " + PlayerManager.getName(uuid));
                        OddJob.getInstance().log("clicked block info");
                        event.setCancelled(true);
                        return;
                    }
                }

                if (player.getInventory().getItemInMainHand().equals(LockManager.lockWand)) {
                    OddJob.getInstance().log("Lock wand");
                    if (LockManager.isLocking(player.getUniqueId())) {
                        if (uuid == player.getUniqueId()) {
                            MessageManager.sendMessage(player, ChatColor.YELLOW + "You have already locked this.");
                            return;
                        }
                        if (uuid != null) {
                            OddJob.getInstance().log("clicked block others chest");
                            MessageManager.sendMessage(player, ChatColor.RED + "A lock is already set on this.");
                            return;
                        }
                        LockManager.lock(player.getUniqueId(), block.getLocation());
                        LockManager.remove(player.getUniqueId());
                        MessageManager.sendMessage(player, ChatColor.GREEN + "Secured!");
                        OddJob.getInstance().log("clicked block locked");
                        event.setCancelled(true);

                        return;
                    }
                }

                if (player.getInventory().getItemInMainHand().equals(LockManager.unlockWand)) {
                    OddJob.getInstance().log("Unlock Wand");
                    if (LockManager.isUnlocking(player.getUniqueId())) {
                        if (uuid != null && !uuid.equals(player.getUniqueId())) {
                            OddJob.getInstance().log("clicked block others chest");
                            MessageManager.sendMessage(player, ChatColor.RED + "A lock is set by someone else.");
                            return;
                        }
                        LockManager.unlock(block.getLocation());
                        LockManager.remove(player.getUniqueId());
                        MessageManager.sendMessage(player, ChatColor.YELLOW + "Unsecured!");
                        OddJob.getInstance().log("clicked block unlocked");
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlaceLock(BlockPlaceEvent event) {
        if (event.getItemInHand().getType().equals(Material.TRIPWIRE_HOOK)) {
            ItemMeta meta = event.getItemInHand().getItemMeta();
            if (ChatColor.stripColor(meta.getDisplayName()).startsWith("Key to")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreakLock(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.CHEST)) {
            block = Utility.getChestPosition(block).getBlock();
        } else if (LockManager.getDoors().contains(block.getType())) {
            block = Utility.getLowerLeftDoor(block).getBlock();
        }
        UUID uuid = LockManager.isLocked(block.getLocation());
        if (uuid != null)
            if (uuid.equals(event.getPlayer().getUniqueId())) {
                LockManager.unlock(block.getLocation());
                MessageManager.sendMessage(event.getPlayer(), ChatColor.YELLOW + "Lock broken!");
            } else {
                event.setCancelled(true);
                MessageManager.sendMessage(event.getPlayer(), ChatColor.RED + "This chest is owned by someone else!");
            }
    }
}
