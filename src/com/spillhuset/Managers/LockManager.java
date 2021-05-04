package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LockManager {
    public ItemStack lockWand = new ItemStack(Material.TRIPWIRE_HOOK);
    public ItemStack unlockWand = new ItemStack(Material.TRIPWIRE_HOOK);
    public ItemStack infoWand = new ItemStack(Material.MAP);
    public ItemStack skeletonKey = new ItemStack(Material.REDSTONE_TORCH);
    private final ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
    private HashMap<Location, UUID> locked = new HashMap<>();
    private HashMap<UUID, UUID> armor = new HashMap<>();
    private List<Material> lockAble = new ArrayList<>();
    private final List<Material> doors = new ArrayList<>();

    public LockManager() {
        ItemMeta meta = lockWand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Locking tool");
            List<String> lore = new ArrayList<>();
            lore.add("Right click a chest to lock it to you.");
            meta.setLore(lore);
        }
        lockWand.setItemMeta(meta);

        meta = lockWand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Unlocking tool");
            List<String> lore = new ArrayList<>();
            lore.add("Right click a chest of yours to unlock it.");
            meta.setLore(lore);
        }
        unlockWand.setItemMeta(meta);

        meta = lockWand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Lock info tool");
            List<String> lore = new ArrayList<>();
            lore.add("Right click a chest to see who owns it.");
            meta.setLore(lore);
        }
        infoWand.setItemMeta(meta);

        meta = skeletonKey.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "The Skeletonkey");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "This key may open any locked object.");
            meta.setLore(lore);
        }
        skeletonKey.setItemMeta(meta);
    }

    public int count(UUID uniqueId) {
        int i = 0;
        for (UUID uuid : locked.values()) {
            if (uuid.equals(uniqueId)) i++;
        }
        return i;
    }

    public void lockInfo(UUID uniqueId) {
        if (OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().contains(infoWand)) {
            remove(uniqueId);
            return;
        }
        remove(uniqueId);
        OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().addItem(infoWand);
        OddJob.getInstance().getMessageManager().lockToolInfo(uniqueId);
    }

    public void lockLocking(UUID uniqueId) {
        if (OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().contains(lockWand)) {
            remove(uniqueId);
            return;
        }
        remove(uniqueId);
        OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().addItem(lockWand);
        OddJob.getInstance().getMessageManager().lockToolLock(uniqueId);
    }

    public void lockUnlocking(UUID uniqueId) {
        if (OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().contains(unlockWand)) {
            remove(uniqueId);
            return;
        }
        remove(uniqueId);
        OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().addItem(unlockWand);
        OddJob.getInstance().getMessageManager().lockToolUnlock(uniqueId);
    }

    public void remove(UUID uniqueId) {
        Player player = OddJob.getInstance().getPlayerManager().getPlayer(uniqueId);
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.remove(lockWand);
        playerInventory.remove(unlockWand);
        playerInventory.remove(infoWand);
    }

    public void lock(UUID uuid, Entity entity) {
        if (entity.getType().equals(EntityType.ARMOR_STAND)) {
            armor.put(entity.getUniqueId(), uuid);
            OddJob.getInstance().getMySQLManager().createSecuredArmorStand(uuid, entity);
        }
    }

    public void lock(UUID uuid, Location location) {
        locked.put(location, uuid);
        OddJob.getInstance().getMySQLManager().createSecuredBlock(uuid, location);
    }

    public void unlock(Entity entity) {
        armor.remove(entity.getUniqueId());
        OddJob.getInstance().getMySQLManager().deleteSecuredArmorStand(entity);
    }

    public void unlock(Location location) {
        locked.remove(location);
        OddJob.getInstance().getMySQLManager().deleteSecuredBlock(location);
    }

    public boolean isLocked(Entity entity) {
        return getArmorStands().containsKey(entity.getUniqueId());
    }

    public boolean isLocked(Location location) {
        return locked.containsKey(location);
    }

    public HashMap<Location, UUID> getLocks() {
        if (locked.isEmpty()) return null;
        return locked;
    }

    public HashMap<UUID, UUID> getArmorStands() {
        if (armor.isEmpty()) return null;
        return armor;
    }

    public ItemStack makeKey(UUID target) {
        ItemStack newKey = key;
        ItemMeta meta = newKey.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Key to " + OddJob.getInstance().getPlayerManager().getName(target));
        List<String> list = new ArrayList<>();
        list.add(ChatColor.YELLOW + "This key will open a lock owned by " + OddJob.getInstance().getPlayerManager().getName(target));
        list.add(ChatColor.GRAY + target.toString());
        meta.setLore(list);
        newKey.setItemMeta(meta);
        return newKey;
    }

    /*
        public Collection<UUID> getLocking() {
            return locking;
        }


        public Collection<UUID> getUnlocking() {
            return unlocking;
        }


        public Collection<UUID> getLockinfo() {
            return lockinfo;
        }
    */
    public List<Material> getDoors() {
        return doors;
    }

    public List<Material> getLockable() {
        return lockAble;
    }

    public UUID getLockOwner(Location location) {
        return locked.get(location);
    }

    public UUID getLockOwner(Entity entity) {
        return armor.get(entity.getUniqueId());
    }

    public void load() {
        doors.add(Material.IRON_DOOR);
        doors.add(Material.DARK_OAK_DOOR);
        doors.add(Material.ACACIA_DOOR);
        doors.add(Material.BIRCH_DOOR);
        doors.add(Material.JUNGLE_DOOR);
        doors.add(Material.OAK_DOOR);
        doors.add(Material.SPRUCE_DOOR);

        // List of Lockable Blocks
        lockAble = OddJob.getInstance().getMySQLManager().getLockableMaterials();

        // List of Locked ArmorStands
        armor = OddJob.getInstance().getMySQLManager().loadSecuredArmorStands();

        // List of Locked Blocks
        locked = OddJob.getInstance().getMySQLManager().loadSecuredBlocks();
        // BUTTON TRAPDOOR PRESSURE_PLATE FENCE_GATE
        // CRIMSON WARPED
        // -DOOR -BUTTON -TRAPDOOR
    }

    public void save() {

    }
}
