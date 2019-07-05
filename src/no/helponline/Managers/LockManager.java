package no.helponline.Managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LockManager {
    public static ItemStack lockWand = new ItemStack(Material.TRIPWIRE_HOOK);
    public static ItemStack unlockWand = new ItemStack(Material.TRIPWIRE_HOOK);
    public static ItemStack infoWand = new ItemStack(Material.MAP);
    public static Collection<ItemStack> keys = new ArrayList<>();
    private static ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
    private static ItemStack skeletonKey = new ItemStack(Material.REDSTONE_TORCH);
    private static Collection<UUID> locking = new ArrayList<>();
    private static Collection<UUID> unlocking = new ArrayList<>();
    private static Collection<UUID> lockinfo = new ArrayList<>();
    private static HashMap<Location, UUID> locked = new HashMap<>();
    private static HashMap<Location, Location> two = new HashMap<>();
    private static HashMap<UUID, UUID> armor = new HashMap<>();

    public LockManager() {
        ItemMeta meta = lockWand.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Locking tool");
        List<String> lore = new ArrayList<>();
        lore.add("Right click a chest to lock it to you.");
        meta.setLore(lore);
        lockWand.setItemMeta(meta);

        meta = lockWand.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Unlocking tool");
        lore = new ArrayList<>();
        lore.add("Right click a chest of yours to unlock it.");
        meta.setLore(lore);
        unlockWand.setItemMeta(meta);

        meta = lockWand.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Lock info tool");
        lore = new ArrayList<>();
        lore.add("Right click a chest to see who owns it.");
        meta.setLore(lore);
        infoWand.setItemMeta(meta);

        skeletonKey = makeSkeletonKey();
    }

    public static int count(UUID uniqueId) {
        int i = 0;
        for (UUID uuid : locked.values()) {
            if (uuid.equals(uniqueId)) i++;
        }
        return i;
    }

    public static void infolock(UUID uniqueId) {
        if (lockinfo.contains(uniqueId)) {
            remove(uniqueId);
            MessageManager.sendMessage(uniqueId, "No longer showing lock info.");
            return;
        }
        remove(uniqueId);
        lockinfo.add(uniqueId);
        PlayerManager.getPlayer(uniqueId).getInventory().addItem(infoWand);
        MessageManager.sendMessage(uniqueId, "Right click with the tool to show it's owner.");
    }

    public static void locking(UUID uniqueId) {
        if (locking.contains(uniqueId)) {
            remove(uniqueId);
            MessageManager.sendMessage(uniqueId, "Aborting");
            return;
        }
        remove(uniqueId);
        locking.add(uniqueId);
        PlayerManager.getPlayer(uniqueId).getInventory().addItem(lockWand);
        MessageManager.sendMessage(uniqueId, "Right click with the tool to lock it.");
    }

    public static void remove(UUID uniqueId) {
        Player player = PlayerManager.getPlayer(uniqueId);
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.remove(lockWand);
        playerInventory.remove(unlockWand);
        playerInventory.remove(infoWand);
        lockinfo.remove(uniqueId);
        unlocking.remove(uniqueId);
        locking.remove(uniqueId);
    }

    public static void unlocking(UUID uniqueId) {
        if (unlocking.contains(uniqueId)) {
            remove(uniqueId);
            MessageManager.sendMessage(uniqueId, "Aborting");
            return;
        }
        remove(uniqueId);
        unlocking.add(uniqueId);
        PlayerManager.getPlayer(uniqueId).getInventory().addItem(unlockWand);
        MessageManager.sendMessage(uniqueId, "Right click with the tool unlock it.");
    }

    public static boolean isLocking(UUID uuid) {
        if (locking.isEmpty()) return false;
        return locking.contains(uuid);
    }

    public static boolean isUnlocking(UUID uuid) {
        if (unlocking.isEmpty()) return false;
        return unlocking.contains(uuid);
    }

    public static boolean isLockInfo(UUID uuid) {
        if (lockinfo.isEmpty()) return false;
        return lockinfo.contains(uuid);
    }

    public static void lock(UUID uuid, Entity entity) {
        if (entity.getType().equals(EntityType.ARMOR_STAND))
            armor.put(entity.getUniqueId(), uuid);
    }

    public static void lock(UUID uuid, Location location) {
        locked.put(location, uuid);
    }

    public static void unlock(Entity entity) {
        armor.remove(entity.getUniqueId());
    }

    public static void unlock(Location location) {
        locked.remove(location);
    }

    public static UUID isLocked(Entity entity) {
        if (armor.containsKey(entity.getUniqueId())) {
            return armor.get(entity.getUniqueId());
        }
        return null;
    }

    public static UUID isLocked(Location location) {
        if (locked.containsKey(location))
            return locked.get(location);
        return null;
    }

    public static HashMap<Location, UUID> getLocks() {
        if (locked.isEmpty()) return null;
        return locked;
    }

    public static ItemStack makeSkeletonKey() {
        ItemStack newKey = skeletonKey;
        ItemMeta meta = newKey.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "THE SKELETONKEY");
        List<String> list = new ArrayList<>();
        list.add(ChatColor.YELLOW + "This key may open any chest.");
        list.add(ChatColor.RED + "Do not lose it!");
        meta.setLore(list);
        newKey.setItemMeta(meta);
        return newKey;
    }

    public static ItemStack makeKey(UUID target) {
        ItemStack newKey = key;
        ItemMeta meta = newKey.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Key to " + PlayerManager.getName(target));
        List<String> list = new ArrayList<>();
        list.add(ChatColor.YELLOW + "This key will open a chest owned by " + PlayerManager.getName(target));
        list.add(ChatColor.GRAY + target.toString());
        meta.setLore(list);
        newKey.setItemMeta(meta);
        return newKey;
    }

    public static void setLocks(HashMap<Location, UUID> locked) {
        LockManager.locked = locked;
    }

    public static Collection<UUID> getLocking() {
        return locking;
    }


    public static Collection<UUID> getUnlocking() {
        return unlocking;
    }


    public static Collection<UUID> getLockinfo() {
        return lockinfo;
    }

    public static List<Material> getDoors() {
        List<Material> doors = new ArrayList<>();
        doors.add(Material.IRON_DOOR);
        doors.add(Material.DARK_OAK_DOOR);
        doors.add(Material.ACACIA_DOOR);
        doors.add(Material.BIRCH_DOOR);
        doors.add(Material.JUNGLE_DOOR);
        doors.add(Material.OAK_DOOR);
        doors.add(Material.SPRUCE_DOOR);
        return doors;
    }

    public static List<Material> getLockable() {
        List<Material> may = new ArrayList<>();
        may.add(Material.FURNACE);
        may.add(Material.JUKEBOX);
        may.add(Material.ENCHANTING_TABLE);
        may.add(Material.CRAFTING_TABLE);
        may.add(Material.ANVIL);
        may.add(Material.SMOKER);
        may.add(Material.BLAST_FURNACE);
        may.add(Material.CARTOGRAPHY_TABLE);
        may.add(Material.FLETCHING_TABLE);
        may.add(Material.GRINDSTONE);
        may.add(Material.SMITHING_TABLE);
        may.add(Material.STONECUTTER);
        may.add(Material.LEVER);
        may.add(Material.CHEST);
        // Doors
        may.add(Material.IRON_DOOR);
        may.add(Material.DARK_OAK_DOOR);
        may.add(Material.ACACIA_DOOR);
        may.add(Material.BIRCH_DOOR);
        may.add(Material.JUNGLE_DOOR);
        may.add(Material.OAK_DOOR);
        may.add(Material.SPRUCE_DOOR);
        // Pressure
        may.add(Material.STONE_PRESSURE_PLATE);
        may.add(Material.DARK_OAK_PRESSURE_PLATE);
        may.add(Material.ACACIA_PRESSURE_PLATE);
        may.add(Material.BIRCH_PRESSURE_PLATE);
        may.add(Material.JUNGLE_PRESSURE_PLATE);
        may.add(Material.OAK_PRESSURE_PLATE);
        may.add(Material.SPRUCE_PRESSURE_PLATE);
        // Buttons
        may.add(Material.STONE_BUTTON);
        may.add(Material.DARK_OAK_BUTTON);
        may.add(Material.ACACIA_BUTTON);
        may.add(Material.BIRCH_BUTTON);
        may.add(Material.JUNGLE_BUTTON);
        may.add(Material.OAK_BUTTON);
        may.add(Material.SPRUCE_BUTTON);
        // Trapdoor
        may.add(Material.IRON_TRAPDOOR);
        may.add(Material.DARK_OAK_TRAPDOOR);
        may.add(Material.ACACIA_TRAPDOOR);
        may.add(Material.BIRCH_TRAPDOOR);
        may.add(Material.JUNGLE_TRAPDOOR);
        may.add(Material.OAK_TRAPDOOR);
        may.add(Material.SPRUCE_TRAPDOOR);
        // Fencegate
        may.add(Material.DARK_OAK_FENCE_GATE);
        may.add(Material.ACACIA_FENCE_GATE);
        may.add(Material.BIRCH_FENCE_GATE);
        may.add(Material.JUNGLE_FENCE_GATE);
        may.add(Material.OAK_FENCE_GATE);
        may.add(Material.SPRUCE_FENCE_GATE);
        return may;
    }
}
