package com.spillhuset.Events;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Zone;
import com.spillhuset.Utils.Guild;
import com.spillhuset.Utils.Lore;
import com.spillhuset.Utils.Utility;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class PlayerInteract implements Listener {
    /**
     * @param event Interact made by player
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItem();

        Chunk chunk = player.getLocation().getChunk();

        UUID playerUUID = player.getUniqueId();

        UUID chunkGuildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(chunk);
        UUID playerGuildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(playerUUID);
        Guild chunkGuild = OddJob.getInstance().getGuildManager().getGuild(chunkGuildUUID);
        Zone zone = OddJob.getInstance().getGuildManager().getZoneByGuild(chunkGuildUUID);
        Block block = event.getClickedBlock();
        Material material = null;

        boolean ownGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(playerUUID) == chunkGuildUUID;
        boolean door = false;
        /* Owner of the block */
        UUID uuid = null;

        if (event.getClickedBlock() != null && event.getItem() != null) {
            OddJob.getInstance().log(player.getName() + ": Interacted with " + event.getClickedBlock().getType().name() + " using " + event.getItem().getType().name());
        }

        // Prevent generating map from InfoLockTool
        if (item != null && item.equals(OddJob.getInstance().getLocksManager().infoWand)) {
            event.setUseItemInHand(Event.Result.DENY);
        }

        // Check Authorization
        // ignore if nothing in hand, in hand is air, or use of offhand
        if ((event.getClickedBlock() == null || event.getClickedBlock().getType().equals(Material.AIR)) || (event.getHand() == EquipmentSlot.OFF_HAND)) {
            return;
        }

        // Admin tool
        // admin a lockable material -> need op, and use of right-click against a block
        if (OddJob.getInstance().getLocksManager().getAdminTools().contains(event.getItem())) {
            if (player.isOp() && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                block = event.getClickedBlock();
                material = block.getType();
                // use of delete lockable tool
                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLocksManager().delMaterialWand)) {
                    uuid = event.getPlayer().getUniqueId();
                    OddJob.getInstance().getLocksManager().remove(material);
                    OddJob.getInstance().getLocksManager().remove(uuid);
                    OddJob.getInstance().getMessageManager().locksMaterialAdded(material.name(), player);
                    event.setCancelled(true);
                    return;
                }
                // use of add lockable tool
                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLocksManager().addMaterialWand)) {
                    // is it already lockable?
                    if (OddJob.getInstance().getLocksManager().getLockable().contains(material)) {
                        OddJob.getInstance().getMessageManager().lockMaterialAlready(material.name(), player);
                        return;
                    }
                    uuid = event.getPlayer().getUniqueId();
                    OddJob.getInstance().getLocksManager().add(material);
                    OddJob.getInstance().getLocksManager().remove(uuid);
                    OddJob.getInstance().getMessageManager().lockMaterialRemoved(material.name(), player);
                    event.setCancelled(true);
                    return;
                }
                return;
            }
        }
        // Admin tool - end

        // Accessing a lockable block, with right click block or stepping on pressure_plate
        if (block != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || (event.getAction().equals(Action.PHYSICAL))) {
            material = block.getType();
            if (OddJob.getInstance().getLocksManager().getLockable().contains(material)) {
                // It is a lockable Block
                if (OddJob.getInstance().getLocksManager().getDoors().contains(material)) {
                    // It is a door
                    door = true;
                    block = Utility.getLowerLeftDoor(block).getBlock();
                    uuid = OddJob.getInstance().getLocksManager().getLockOwner(block.getLocation());
                } else if (material.equals(Material.CHEST)) {
                    // It is a check
                    block = Utility.getChestPosition(block).getBlock();
                    uuid = OddJob.getInstance().getLocksManager().getLockOwner(block.getLocation());
                } else {
                    // It is an ordinary block!
                    uuid = OddJob.getInstance().getLocksManager().getLockOwner(block.getLocation());
                }

                // The lock is personal
                if (uuid != null) {
                    if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLocksManager().infoWand)) {
                        // InfoWand in hand
                        if (OddJob.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLocksManager().infoWand)) {
                            if (OddJob.getInstance().getPlayerManager().getName(uuid) != null)
                                OddJob.getInstance().getMessageManager().lockBlockOwned(block.getType().name(), OddJob.getInstance().getPlayerManager().getName(uuid), player);
                            event.setCancelled(true);
                            return;
                        }
                    }
                    if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLocksManager().unlockWand)) {
                        // UnlockWand in hand
                        if (OddJob.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLocksManager().unlockWand)) {
                            if (!uuid.equals(player.getUniqueId())) {
                                OddJob.getInstance().getMessageManager().lockSomeoneElse(player);
                                event.setCancelled(true);
                                return;
                            }

                            // Unlocking
                            OddJob.getInstance().getLocksManager().unlock(block.getLocation());
                            OddJob.getInstance().getLocksManager().remove(player.getUniqueId());
                            OddJob.getInstance().getMessageManager().lockUnlocked(block.getType().name(), player);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    // Oh... it's yours
                    if (uuid.equals(player.getUniqueId())) {
                        if (door) {
                            Utility.doorToggle(block);
                        }
                        OddJob.getInstance().getMessageManager().locksOpenedOwnLock(playerUUID);
                        return;
                    }

                    if (player.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                        // Has a Key in hand
                        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                        if (Lore.lore(event, player, door, uuid, block, meta, false)) {
                            // Has the correct key
                            OddJob.getInstance().getMessageManager().locksOpenedWithPlayerKey(uuid, playerUUID);
                            return;
                        }
                    } else if (player.getInventory().getItemInOffHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                        // Has a key in offhand
                        ItemMeta meta = player.getInventory().getItemInOffHand().getItemMeta();
                        if (Lore.lore(event, player, door, uuid, block, meta, false)) {
                            // Has the correct key
                            OddJob.getInstance().getMessageManager().locksOpenedWithPlayerKey(uuid, playerUUID);
                            return;
                        }
                    }

                    OddJob.getInstance().getMessageManager().lockOwned(player);
                    OddJob.getInstance().log("Wrong key");
                    event.setCancelled(true);

                }

                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLocksManager().lockWand)) {
                    // LockWand in hand
                    OddJob.getInstance().getMessageManager().console("In hand");
                    if (OddJob.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getInventory().getItemInMainHand().equals(OddJob.getInstance().getLocksManager().lockWand)) {
                        OddJob.getInstance().getMessageManager().console("Locking");
                        if (uuid == player.getUniqueId()) {
                            OddJob.getInstance().getMessageManager().lockAlreadyBlock(player);
                            event.setCancelled(true);
                            return;
                        }
                        if (uuid != null) {
                            OddJob.getInstance().getMessageManager().lockAlreadyEntity(player);
                            event.setCancelled(true);
                            return;
                        }

                        // Locking
                        OddJob.getInstance().getLocksManager().lock(player.getUniqueId(), block.getLocation());
                        OddJob.getInstance().getLocksManager().remove(player.getUniqueId());
                        OddJob.getInstance().getMessageManager().lockBlockLocked(block.getType().name(), player);

                        event.setCancelled(true);
                        return;
                    }
                }

                // Changing to the guild who owns the block
                chunkGuildUUID = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(block.getChunk());
                chunkGuild = OddJob.getInstance().getGuildManager().getGuild(chunkGuildUUID);

                // Chunk owned by a guild, player is in a guild
                if (chunkGuild != null && playerGuildUUID != null && zone != Zone.WILD) {
                    // Owned by your guild
                    if (chunkGuildUUID.equals(playerGuildUUID)) {
                        if (door) Utility.doorToggle(block);
                    } else if (!zone.equals(Zone.GUILD)) {
                        // chunk is claimed, but not to a guild
                        if (door) {
                            // Door
                            // -- Utility.doorToggle(block);
                            OddJob.getInstance().log("Locked");
                            event.setCancelled(true);
                        }
                    } else {
                        // Is owned by a Guild, but not yours
                        // if has key
                        if (player.getInventory().getItemInOffHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                            // Has a key in offhand
                            ItemMeta meta = player.getInventory().getItemInOffHand().getItemMeta();
                            if (Lore.lore(event, player, door, playerGuildUUID, block, meta, true)) {
                                // Has the correct key
                                OddJob.getInstance().getMessageManager().locksUnlockedWithGuildKey(playerUUID, chunkGuild.getName());
                                return;
                            }
                        }
                        OddJob.getInstance().getMessageManager().locksGuild(chunkGuild.getName(), player);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
