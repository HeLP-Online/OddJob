package no.helponline.Managers;

import com.sk89q.worldedit.bukkit.fastutil.Hash;
import no.helponline.OddJob;
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
    public ItemStack lockWand = new ItemStack(Material.TRIPWIRE_HOOK);
    public ItemStack unlockWand = new ItemStack(Material.TRIPWIRE_HOOK);
    public ItemStack infoWand = new ItemStack(Material.MAP);
    public ItemStack skeletonKey = new ItemStack(Material.REDSTONE_TORCH);
    private final ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);

    private final Collection<UUID> locking = new ArrayList<>();
    private final Collection<UUID> unlocking = new ArrayList<>();
    private final Collection<UUID> lockinfo = new ArrayList<>();

    private HashMap<Location, UUID> locked = new HashMap<>();
    private HashMap<UUID, UUID> armor = new HashMap<>();
    private List<Material> lockAble = new ArrayList<>();
    private final List<Material> doors = new ArrayList<>();

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

        meta = skeletonKey.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "The Skeletonkey");
        List<String> list = new ArrayList<>();
        list.add(ChatColor.YELLOW + "This key may open any locked object.");
        meta.setLore(list);
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
        if (lockinfo.contains(uniqueId)) {
            remove(uniqueId);
            OddJob.getInstance().getMessageManager().warning("No longer showing lock info.", uniqueId, false);
            return;
        }
        remove(uniqueId);
        lockinfo.add(uniqueId);
        OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().addItem(infoWand);
        OddJob.getInstance().getMessageManager().info("Right click with the tool to show it's owner.", uniqueId, false);
    }

    public void lockLocking(UUID uniqueId) {
        if (locking.contains(uniqueId)) {
            remove(uniqueId);
            OddJob.getInstance().getMessageManager().warning("Aborting", uniqueId, false);
            return;
        }
        remove(uniqueId);
        locking.add(uniqueId);
        OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().addItem(lockWand);
        OddJob.getInstance().getMessageManager().info("Right click with the tool to lock it.", uniqueId, false);
    }

    public void lockUnlocking(UUID uniqueId) {
        if (unlocking.contains(uniqueId)) {
            remove(uniqueId);
            OddJob.getInstance().getMessageManager().warning("Aborting", uniqueId, false);
            return;
        }
        remove(uniqueId);
        unlocking.add(uniqueId);
        OddJob.getInstance().getPlayerManager().getPlayer(uniqueId).getInventory().addItem(unlockWand);
        OddJob.getInstance().getMessageManager().info("Right click with the tool unlock it.", uniqueId, false);
    }

    public void remove(UUID uniqueId) {
        Player player = OddJob.getInstance().getPlayerManager().getPlayer(uniqueId);
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.remove(lockWand);
        playerInventory.remove(unlockWand);
        playerInventory.remove(infoWand);
        lockinfo.remove(uniqueId);
        unlocking.remove(uniqueId);
        locking.remove(uniqueId);
    }

    public boolean isLocking(UUID uuid) {
        if (locking.isEmpty()) return false;
        return locking.contains(uuid);
    }

    public boolean isUnlocking(UUID uuid) {
        if (unlocking.isEmpty()) return false;
        return unlocking.contains(uuid);
    }

    public boolean isLockInfo(UUID uuid) {
        if (lockinfo.isEmpty()) return false;
        return lockinfo.contains(uuid);
    }

    public void lock(UUID uuid, Entity entity) {
        if (entity.getType().equals(EntityType.ARMOR_STAND)) {
            armor.put(entity.getUniqueId(), uuid);
            OddJob.getInstance().getMySQLManager().createLock(uuid, entity);
        }
    }

    public void lock(UUID uuid, Location location) {
        locked.put(location, uuid);
        OddJob.getInstance().getMySQLManager().createLock(uuid, location);
    }

    public void unlock(Entity entity) {
        armor.remove(entity.getUniqueId());
        OddJob.getInstance().getMySQLManager().deleteLock(entity);
    }

    public void unlock(Location location) {
        locked.remove(location);
        OddJob.getInstance().getMySQLManager().deleteLock(location);
    }

    public boolean isLocked(Entity entity) {
        return getArmorStands().containsKey(entity);
    }

    public boolean isLocked(Location location) {
        return getLocks().containsKey(location);
    }

    public HashMap<Location, UUID> getLocks() {
        if (locked.isEmpty()) return null;
        return locked;
    }
    public HashMap<Location, UUID> getArmorStands() {
        if (locked.isEmpty()) return null;
        return locked;
    }

    public ItemStack makeKey(UUID target) {
        ItemStack newKey = key;
        ItemMeta meta = newKey.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Key to " + OddJob.getInstance().getPlayerManager().getName(target));
        List<String> list = new ArrayList<>();
        list.add(ChatColor.YELLOW + "This key will open a chest owned by " + OddJob.getInstance().getPlayerManager().getName(target));
        list.add(ChatColor.GRAY + target.toString());
        meta.setLore(list);
        newKey.setItemMeta(meta);
        return newKey;
    }

    public Collection<UUID> getLocking() {
        return locking;
    }


    public Collection<UUID> getUnlocking() {
        return unlocking;
    }


    public Collection<UUID> getLockinfo() {
        return lockinfo;
    }

    public List<Material> getDoors() {
        return doors;
    }

    public List<Material> getLockable() {
        return lockAble;
    }

    public UUID getLockOwner(Location location) {
        return getLocks().get(location);
    }
    public UUID getLockOwner(Entity entity) {
        return getArmorStands().get(entity);
    }
    public void load() {
        doors.add(Material.IRON_DOOR);
        doors.add(Material.DARK_OAK_DOOR);
        doors.add(Material.ACACIA_DOOR);
        doors.add(Material.BIRCH_DOOR);
        doors.add(Material.JUNGLE_DOOR);
        doors.add(Material.OAK_DOOR);
        doors.add(Material.SPRUCE_DOOR);

        lockAble = OddJob.getInstance().getMySQLManager().getLockableMaterials();
        armor = OddJob.getInstance().getMySQLManager().loadArmorStands();
        locked = OddJob.getInstance().getMySQLManager().loadLockedBlocks();
    }
}
