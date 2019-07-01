package no.helponline.Managers;

import no.helponline.Utils.DoubleChestUtil;
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
        List<String> lore = new ArrayList<String>();
        lore.add("Right click a chest to lock it to you.");
        meta.setLore(lore);
        lockWand.setItemMeta(meta);

        meta = lockWand.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Unlocking tool");
        lore = new ArrayList<String>();
        lore.add("Right click a chest of yours to unlock it.");
        meta.setLore(lore);
        unlockWand.setItemMeta(meta);

        meta = lockWand.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Lock info tool");
        lore = new ArrayList<String>();
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
        if (location.getBlock().getType().equals(Material.CHEST)) {
            Location bottom = DoubleChestUtil.getBottomLocation(location.getBlock());
            Location top = DoubleChestUtil.getTopLocation(location.getBlock());
            if (bottom != null) {
                two.put(top, bottom);
            }
            locked.put(top, uuid);
        } else {
            locked.put(location, uuid);
        }
    }

    public static void unlock(Entity entity) {
        armor.remove(entity.getUniqueId());
    }

    public static void unlock(Location location) {
        if (two.containsValue(location)) {
            for (Location top : two.keySet()) {
                if (two.get(top).equals(location)) {
                    two.remove(top);
                    locked.remove(top);
                    return;
                }
            }
        } else {
            two.remove(location);
            locked.remove(location);
        }
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
        if (two.containsValue(location)) {
            for (Location top : two.keySet()) {
                if ((two.get(top)).equals(location)) {
                    return locked.get(top);
                }
            }
        }
        return null;
    }

    public static HashMap<UUID, UUID> getArmor() {
        if (armor.isEmpty()) return null;
        return armor;
    }

    public static HashMap<Location, UUID> getLocks() {
        if (locked.isEmpty()) return null;
        return locked;
    }

    public static HashMap<Location, Location> getTwo() {
        if (two.isEmpty()) return null;
        return two;
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

    public static void setLocks(HashMap<Location, UUID> locked, HashMap<Location, Location> two, HashMap<UUID, UUID> armor) {
        LockManager.armor = armor;
        LockManager.two = two;
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

    enum Type {
        TRAPDOOR, DOOR, CHEST, GATE, FURNACE, WORKBENCH, JUKEBOX, ENCHANTING, ANVIL, SMOKER, GRINDSTONE, SMITHING, STONECUTTER
    }
}
