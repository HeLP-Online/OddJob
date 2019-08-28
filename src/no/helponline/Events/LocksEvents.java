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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityOpen(EntityInteractEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            Block block = event.getBlock();
            boolean locked = false;

            Material t = block.getType();
            if (OddJob.getInstance().getLockManager().getLockable().contains(t)) {
                try {
                    if (OddJob.getInstance().getLockManager().getDoors().contains(t)) {
                        // changing <block>
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        boolean door = false;
        boolean chest = false;
        boolean duo = false;
        boolean lower = false;
        boolean left = false;
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
                OddJob.getInstance().log("lockable");
                try {
                    // a door?
                    if (OddJob.getInstance().getLockManager().getDoors().contains(t)) {
                        OddJob.getInstance().log("lockable door");
                        door = true;
                        block = Utility.getLowerLeftDoor(block).getBlock();
                        uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
                    }
                    // a chest?
                    else if (t.equals(Material.CHEST)) {
                        OddJob.getInstance().log("lockable chest");
                        block = Utility.getChestPosition(block).getBlock();
                        uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
                    }
                    // anything else.
                    else {
                        OddJob.getInstance().log("lockable item");
                        uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // has a lock!
                if (uuid != null) {
                    OddJob.getInstance().log("it's locked!");
                    sb.append(player.getName()).append(" trigger lock owned by: ").append(OddJob.getInstance().getPlayerManager().getOffPlayer(uuid).getName()).append("; ");
                    // has skeletonkey?
                    if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().makeSkeletonKey())) {
                        OddJob.getInstance().log("skeletonkey");
                        if (door) {
                            Utility.doorToggle(block);
                            player.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
                            event.setCancelled(true);
                        }
                        player.sendMessage(ChatColor.RED + "! " + ChatColor.RESET + "Lock open by the awesome Skeletonkey!");
                        sb.append("Opened by skeletonkey; ");
                        return;
                    }

                    // has key in main-hand?
                    if (player.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                        OddJob.getInstance().log("main-hand");
                        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                        if (lore(event, player, door, uuid, block, meta)) return;
                    }
                    // has key in off-hand?
                    else if (player.getInventory().getItemInOffHand().getType().equals(Material.TRIPWIRE_HOOK)) {
                        OddJob.getInstance().log("off-hand");
                        ItemMeta met = player.getInventory().getItemInOffHand().getItemMeta();
                        if (lore(event, player, door, uuid, block, met)) return;
                    }

                    // it's your lock
                    if (uuid.equals(player.getUniqueId())) {
                        if (door) {
                            Utility.doorToggle(block);
                        }
                        OddJob.getInstance().log("opened your own");
                        return;
                    }

                    //player.sendMessage(ChatColor.RED + "This lock is owned by someone else.");
                    OddJob.getInstance().log("Oh ooh..");
                    event.setCancelled(true);

                }

                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().infoWand)) {
                    OddJob.getInstance().log("Info Wand");
                    if (OddJob.getInstance().getLockManager().isLockInfo(player.getUniqueId())) {
                        OddJob.getInstance().getMessageManager().sendMessage(player, ChatColor.YELLOW + "The lock is owned by " + OddJob.getInstance().getPlayerManager().getName(uuid));
                        event.setCancelled(true);
                        return;
                    }
                }

                if (player.getInventory().getItemInMainHand().equals(OddJob.getInstance().getLockManager().lockWand)) {
                    OddJob.getInstance().log("Lock wand");
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
                    OddJob.getInstance().log("Unlock Wand");
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
            }
            // GUILD owning block
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(block.getChunk(), block.getWorld());
            // GUILD associated with you
            UUID g2 = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());

            // you are in a guild, and block is owned by a guild
            if (guild != null && g2 != null) {
                OddJob.getInstance().log("guild not null");
                // both are the same guild
                if (guild.equals(g2)) {
                    OddJob.getInstance().log("same guild");
                    if (door) {
                        Utility.doorToggle(block);
                    }
                }
                // is not owned by a ZONE or a guild
                else if (!OddJob.getInstance().getGuildManager().getZoneByGuild(guild).equals(Zone.GUILD)) {
                    OddJob.getInstance().log("in wild");
                    if (door) {
                        Utility.doorToggle(block);
                    }
                } else {
                    // IS OWNED BY A GUILD, BUT NOT YOURS!
                    OddJob.getInstance().log("locked by guild or zone");
                    event.setCancelled(true);
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
                        OddJob.getInstance().log("compared: " + lore.get(1) + " vs " + uuid.toString());
                        player.sendMessage(ChatColor.YELLOW + "You don't have the correct key");
                        event.setCancelled(true);
                        return true;
                    }
                    player.sendMessage(ChatColor.YELLOW + "Lock open by key!");
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
        if (event.getItemInHand().equals(OddJob.getInstance().getLockManager().unlockWand) || event.getItemInHand().equals(OddJob.getInstance().getLockManager().lockWand) || event.getItemInHand().equals(OddJob.getInstance().getLockManager().infoWand)) {
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreakLock(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.CHEST)) {
            block = Utility.getChestPosition(block).getBlock();
        } else if (OddJob.getInstance().getLockManager().getDoors().contains(block.getType())) {
            block = Utility.getLowerLeftDoor(block).getBlock();
        }
        UUID uuid = OddJob.getInstance().getLockManager().isLocked(block.getLocation());
        if (uuid != null) {
            if (uuid.equals(event.getPlayer().getUniqueId())) {
                OddJob.getInstance().getLockManager().unlock(block.getLocation());
                OddJob.getInstance().getMessageManager().sendMessage(event.getPlayer(), ChatColor.YELLOW + "Lock broken!");
            } else {
                event.setCancelled(true);
                OddJob.getInstance().getMessageManager().sendMessage(event.getPlayer(), ChatColor.RED + "This lock is owned by someone else!");
            }
        }
    }

    @EventHandler
    public void targetDummyHit(PlayerInteractEvent event) {

    }
}
