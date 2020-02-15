package no.helponline.Events;

import no.helponline.OddJob;
import no.helponline.Utils.Utility;
import no.helponline.Utils.Zone;
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
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class LocksEvents implements Listener {

    /**
     * @param event Interact made by a non player entity
     *              <p>
     *              No need to check if block is a chest
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityOpen(EntityInteractEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            Block block = event.getBlock();
            boolean locked = false;

            Material t = block.getType();
            // LOCKABLE BLOCK?
            if (OddJob.getInstance().getLockManager().getLockable().contains(t)) {
                try {
                    // DOOR?
                    if (OddJob.getInstance().getLockManager().getDoors().contains(t)) {
                        block = Utility.getLowerLeftDoor(block).getBlock();
                        locked = OddJob.getInstance().getLockManager().isLocked(block.getLocation()) != null;
                    } else {
                        locked = OddJob.getInstance().getLockManager().isLocked(block.getLocation()) != null;
                    }
                } catch (Exception e) {
                    return;
                }
            }

            if (locked) event.setCancelled(true);
        }
    }

    /**
     * @param event Interact made by player
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        boolean door = false;
        UUID uuid = null;

        StringBuilder sb = new StringBuilder();

        if ((event.getHand() == EquipmentSlot.OFF_HAND) || (event.getClickedBlock() == null) || player.isOp()) {
            return;
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || (event.getAction().equals(Action.PHYSICAL))) {
            Block block = event.getClickedBlock();
            Material t = block.getType();
            // A lockable block
            if (OddJob.getInstance().getLockManager().getLockable().contains(t)) {
                try {
                    // a door?
                    if (OddJob.getInstance().getLockManager().getDoors().contains(t)) {
                        door = true;
                        block = Utility.getLowerLeftDoor(block).getBlock();
                        uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
                    }
                    // a chest?
                    else if (t.equals(Material.CHEST)) {
                        block = Utility.getChestPosition(block).getBlock();
                        uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
                    }
                    // anything else.
                    else {
                        uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // has a lock!
                if (uuid != null) {
                    // it's your lock
                    if (uuid.equals(player.getUniqueId())) {
                        if (door) {
                            Utility.doorToggle(block);
                        }
                        OddJob.getInstance().getMessageManager().console(sb.toString());
                        return;
                    }
                    // has skeletonkey?
                    if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().makeSkeletonKey())) {
                        if (door) {
                            Utility.doorToggle(block);
                            player.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
                            event.setCancelled(true);
                        }
                        player.sendMessage(ChatColor.RED + "! " + ChatColor.RESET + "Lock open by the awesome Skeletonkey!");
                        OddJob.getInstance().getMessageManager().console(sb.toString());
                        return;
                    }

                    // has key in main-hand?
                    if (player.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                        if (lore(event, player, door, uuid, block, meta)) {
                            return;
                        }
                    }
                    // has key in off-hand?
                    else if (player.getInventory().getItemInOffHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                        ItemMeta meta = player.getInventory().getItemInOffHand().getItemMeta();
                        if (lore(event, player, door, uuid, block, meta)) {
                            return;
                        }
                    }

                    player.sendMessage(ChatColor.RED + "This block is locked by someone else.");
                    event.setCancelled(true);

                }

                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().infoWand)) {
                    if (OddJob.getInstance().getLockManager().isLockInfo(player.getUniqueId())) {
                        OddJob.getInstance().getMessageManager().sendMessage(player, ChatColor.YELLOW + "The lock is owned by " + OddJob.getInstance().getPlayerManager().getName(uuid));
                        event.setCancelled(true);
                        return;
                    }
                }

                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().lockWand)) {
                    if (OddJob.getInstance().getLockManager().isLocking(player.getUniqueId())) {
                        if (uuid == player.getUniqueId()) {
                            OddJob.getInstance().getMessageManager().sendMessage(player, ChatColor.YELLOW + "You have already locked this.");
                            return;
                        }
                        if (uuid != null) {
                            OddJob.getInstance().getMessageManager().sendMessage(player, ChatColor.RED + "A lock is already set on this.");
                            return;
                        }
                        OddJob.getInstance().getLockManager().lock(player.getUniqueId(), block.getLocation());
                        OddJob.getInstance().getLockManager().remove(player.getUniqueId());
                        OddJob.getInstance().getMessageManager().sendMessage(player, ChatColor.GREEN + "Secured!");
                        event.setCancelled(true);

                        return;
                    }
                }

                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().unlockWand)) {
                    if (OddJob.getInstance().getLockManager().isUnlocking(player.getUniqueId())) {
                        if (uuid != null && !uuid.equals(player.getUniqueId())) {
                            OddJob.getInstance().getMessageManager().sendMessage(player, ChatColor.RED + "A lock is set by someone else.");
                            return;
                        }
                        OddJob.getInstance().getLockManager().unlock(block.getLocation());
                        OddJob.getInstance().getLockManager().remove(player.getUniqueId());
                        OddJob.getInstance().getMessageManager().sendMessage(player, ChatColor.YELLOW + "Unsecured!");
                        event.setCancelled(true);
                        return;
                    }
                }

                // GUILD owning block
                UUID blockGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(block.getChunk(), block.getWorld());
                // GUILD associated with you
                UUID yourGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());

                // you are in a guild, and block is owned by a guild
                sb = new StringBuilder();
                if (blockGuild != null && yourGuild != null) {
                    if (blockGuild.equals(yourGuild)) {
                        if (door) {
                            Utility.doorToggle(block);
                        }
                    }
                    // is not owned by a ZONE or a guild
                    else if (!OddJob.getInstance().getGuildManager().getZoneByGuild(blockGuild).equals(Zone.GUILD)) {
                        if (door) {
                            Utility.doorToggle(block);
                        }
                    } else {
                        // IS OWNED BY A GUILD, BUT NOT YOURS!
                        OddJob.getInstance().getMessageManager().warning("Block is locked by a guild.", player);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean lore(PlayerInteractEvent event, Player player, boolean door, UUID uuid, Block block, ItemMeta meta) {
        try {
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore.size() > 1) {
                    String one = ChatColor.stripColor(lore.get(1));
                    if (!one.equalsIgnoreCase(uuid.toString())) {
                        OddJob.getInstance().getMessageManager().warning("You don't have the correct key", player);
                        event.setCancelled(true);
                        return true;
                    }
                    OddJob.getInstance().getMessageManager().success("Lock open by key!", player);
                    if (door) {
                        Utility.doorToggle(block);
                        player.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
                        event.setCancelled(true);
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @EventHandler
    public void onPlaceLock(BlockPlaceEvent event) {
        if (event.getItemInHand().equals(OddJob.getInstance().getLockManager().unlockWand) ||
                event.getItemInHand().equals(OddJob.getInstance().getLockManager().lockWand) ||
                event.getItemInHand().equals(OddJob.getInstance().getLockManager().infoWand)) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemInHand().getType().equals(Material.TRIPWIRE_HOOK)) {
            ItemMeta meta = event.getItemInHand().getItemMeta();
            if (ChatColor.stripColor(meta.getDisplayName()).startsWith("Key to")) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * @param event Breaking block
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBreakLock(BlockBreakEvent event) {
        Block block = event.getBlock();
        // CHEST ?
        if (block.getType().equals(Material.CHEST)) {
            block = Utility.getChestPosition(block).getBlock();
        }
        // DOOR ?
        else if (OddJob.getInstance().getLockManager().getDoors().contains(block.getType())) {
            block = Utility.getLowerLeftDoor(block).getBlock();
        }

        UUID uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
        // LOCKED BY A PLAYER ?
        if (uuid != null) {
            if (uuid.equals(event.getPlayer().getUniqueId())) {
                OddJob.getInstance().getLockManager().unlock(block.getLocation());
                OddJob.getInstance().getMessageManager().sendMessage(event.getPlayer(), ChatColor.YELLOW + "Lock broken!");
            } else {
                OddJob.getInstance().getMessageManager().sendMessage(event.getPlayer(), ChatColor.RED + "This lock is owned by someone else!");
                event.setCancelled(true);
            }
        }
    }
}
