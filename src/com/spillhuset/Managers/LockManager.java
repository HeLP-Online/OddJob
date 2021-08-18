package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.LockSQL;
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
    public ItemStack addMaterialWand = new ItemStack(Material.COBWEB);
    public ItemStack delMaterialWand = new ItemStack(Material.CACTUS);
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
            lore.add("Right click a material to lock it to you.");
            meta.setLore(lore);
        }
        lockWand.setItemMeta(meta);

        meta = addMaterialWand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Make MATERIAL lockable");
            List<String> lore = new ArrayList<>();
            lore.add("Right click a material to save it to the list.");
            meta.setLore(lore);
        }
        addMaterialWand.setItemMeta(meta);

        meta = delMaterialWand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Remove MATERIAL lockable");
            List<String> lore = new ArrayList<>();
            lore.add("Right click a material to remove it from the list.");
            meta.setLore(lore);
        }
        delMaterialWand.setItemMeta(meta);

        meta = unlockWand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Unlocking tool");
            List<String> lore = new ArrayList<>();
            lore.add("Right click a material of yours to unlock it.");
            meta.setLore(lore);
        }
        unlockWand.setItemMeta(meta);

        meta = infoWand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Lock INFO tool");
            List<String> lore = new ArrayList<>();
            lore.add("Right click a material to see who owns it.");
            meta.setLore(lore);
        }
        infoWand.setItemMeta(meta);

        meta = skeletonKey.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "The Skeletonkey");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "This key may open any locked materials.");
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

    public void addLockMaterial(UUID uniqueId) {
        if (OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().contains(addMaterialWand)) {
            remove(uniqueId);
            return;
        }
        remove(uniqueId);
        OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().addItem(addMaterialWand);
        OddJob.getInstance().getMessageManager().lockToolAdd(uniqueId);
    }
    public void delLockMaterial(UUID uniqueId) {
        if (OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().contains(delMaterialWand)) {
            remove(uniqueId);
            return;
        }
        remove(uniqueId);
        OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().addItem(delMaterialWand);
        OddJob.getInstance().getMessageManager().lockToolDel(uniqueId);
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
        if(CostManager.cost(uniqueId,"lock.lock")) {
            OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().addItem(lockWand);
            OddJob.getInstance().getMessageManager().lockToolLock(uniqueId);
        }
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
        playerInventory.remove(addMaterialWand);
        playerInventory.remove(delMaterialWand);
    }

    public void lock(UUID uuid, Entity entity) {
        if (entity.getType().equals(EntityType.ARMOR_STAND)) {
            armor.put(entity.getUniqueId(), uuid);
            LockSQL.createSecuredArmorStand(uuid, entity);
        }
    }

    public void lock(UUID uuid, Location location) {
        locked.put(location, uuid);
        LockSQL.createSecuredBlock(uuid, location);
    }

    public void unlock(Entity entity) {
        armor.remove(entity.getUniqueId());
        LockSQL.deleteSecuredArmorStand(entity);
    }

    public void unlock(Location location) {
        locked.remove(location);
        LockSQL.deleteSecuredBlock(location);
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

    public void getKey(Player player) {
        ItemStack newKey = key;
        ItemMeta meta = newKey.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Key to " + player.getName());
            List<String> list = new ArrayList<>();
            list.add(ChatColor.YELLOW + "This key will open a lock owned by " + player.getName());
            list.add(""+ChatColor.GRAY + player.getUniqueId());
            meta.setLore(list);
            newKey.setItemMeta(meta);
        }

        if (CostManager.cost(player.getUniqueId(), "locks.make")) {
            OddJob.getInstance().getMessageManager().locksKey(player);
            player.getInventory().addItem(newKey);
        }

    }

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

    public void add(Material material) {
        lockAble.add(material);
        LockSQL.addMaterial(material);
        OddJob.getInstance().log("Material "+material.name()+" added");
    }
    public void remove(Material material) {
        lockAble.remove(material);
        LockSQL.remove(material);
        OddJob.getInstance().log("Material "+material.name()+" removed");
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
        lockAble = LockSQL.getLockableMaterials();

        // List of Locked ArmorStands
        armor = LockSQL.loadSecuredArmorStands();

        // List of Locked Blocks
        locked = LockSQL.loadSecuredBlocks();
        // BUTTON TRAPDOOR PRESSURE_PLATE FENCE_GATE
        // CRIMSON WARPED
        // -DOOR -BUTTON -TRAPDOOR
    }

    public void save() {

    }
}
