package no.helponline.Managers;

import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import no.helponline.OddJob;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {
    private HashMap<UUID, String> names = new HashMap<>();
    private HashMap<UUID, UUID> requestTrade;
    private HashMap<UUID, UUID> tradingPlayers;
    private HashMap<UUID, Long> inCombat = new HashMap<>();
    private HashMap<UUID, BukkitTask> timerCombat = new HashMap<>();
    public HashMap<UUID, UUID> in = new HashMap<>();

    public PlayerManager() {
        requestTrade = new HashMap<>();
        tradingPlayers = new HashMap<>();
    }

    public void updatePlayer(UUID uuid, String name) {
        OddJob.getInstance().getMySQLManager().updatePlayer(uuid, name);
    }

    public String getName(UUID uuid) {
        return names.getOrDefault(uuid, null);
    }

    public void setNames(HashMap<UUID, String> map) {
        this.names.putAll(map);
    }

    public UUID getUUID(String name) {
        for (UUID uuid : names.keySet()) {
            if (names.get(uuid).equalsIgnoreCase(name)) return uuid;
        }
        return null;
    }

    public Set<UUID> getUUIDs() {
        return names.keySet();
    }

    public Player getPlayer(UUID uniqueId) {
        return Bukkit.getServer().getPlayer(uniqueId);
    }

    public HashMap<String, Object> getOddPlayer(UUID uniqueId) {
        return OddJob.getInstance().getMySQLManager().getPlayer(uniqueId);
    }

    public boolean request(UUID moving, UUID destination) {
        boolean request = OddJob.getInstance().getMySQLManager().getPlayerDenyTpa(destination); // false
        if (OddJob.getInstance().getMySQLManager().getPlayerWhiteList(destination).contains(moving)) { //false
            OddJob.getInstance().log("whitelist");
            request = true;
        } else if (OddJob.getInstance().getMySQLManager().getPlayerBlackList(destination).contains(moving)) { // false
            OddJob.getInstance().log("blacklist");
            request = false;
        } else if (OddJob.getInstance().getMySQLManager().getPlayerDenyTpa(destination)) { // false
            OddJob.getInstance().log("tpa deny");
            OddJob.getInstance().getMessageManager().warning(getName(destination) + " is denying all request!", moving, false);
            request = false;
        }
        return !request;
    }

    public List<String> getNames() {
        return OddJob.getInstance().getMySQLManager().getPlayerMapNames();
    }

    public GameMode getGameMode(Player player, World world) {
        return OddJob.getInstance().getMySQLManager().getPlayerMode(player, world);
    }

    public void setGameMode(Player player, GameMode gameMode) {
        OddJob.getInstance().getMySQLManager().setGameMode(player, gameMode);
        player.setGameMode(gameMode);
        OddJob.getInstance().getMessageManager().success("Your GameMode is set to " + ChatColor.GOLD + gameMode.name() + ChatColor.GREEN + " in " + ChatColor.DARK_AQUA + player.getWorld().getName(), player.getUniqueId(), true);
    }

    public boolean isInCombat(UUID player) {
        if (inCombat.get(player) != null) {
            return inCombat.get(player) > (System.currentTimeMillis() - 1000L);
        }
        return false;
    }

    public void setInCombat(UUID player) {
        if (timerCombat.get(player) != null) timerCombat.get(player).cancel();
        inCombat.put(player, System.currentTimeMillis());
        String s = ChatColor.DARK_RED + "In combat";
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + s + "\"}"), 40, 20, 20);
        (((CraftPlayer) Bukkit.getPlayer(player)).getHandle()).playerConnection.sendPacket(title);
        timerCombat.put(player, new BukkitRunnable() {
            @Override
            public void run() {
                removeInCombat(player);
            }
        }.runTaskLater(OddJob.getInstance(), 200L)); // 200 = 10s (20 ticks/s)
    }

    private void removeInCombat(UUID player) {
        if (timerCombat.get(player) != null) timerCombat.get(player).cancel();
        timerCombat.remove(player);
        inCombat.remove(player);
        String s = ChatColor.GREEN + "Out of combat";
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + s + "\"}"), 40, 20, 20);
        if (OddJob.getInstance().getPlayerManager().getPlayer(player).isOnline())
            (((CraftPlayer) Bukkit.getPlayer(player)).getHandle()).playerConnection.sendPacket(title);
    }

    public void abort(UUID player) {
        inCombat.put(player, System.currentTimeMillis());
        String s = ChatColor.YELLOW + "Aborted";
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + s + "\"}"), 40, 20, 20);
        if (OddJob.getInstance().getPlayerManager().getPlayer(player).isOnline())
            (((CraftPlayer) Bukkit.getPlayer(player)).getHandle()).playerConnection.sendPacket(title);
        timerCombat.put(player, new BukkitRunnable() {
            @Override
            public void run() {
                removeInCombat(player);
            }
        }.runTaskLater(OddJob.getInstance(), 40L)); // 40 = 2s (20 ticks/s)
    }

    public HashMap<UUID, UUID> getRequestTrade() {
        return requestTrade;
    }

    public HashMap<UUID, UUID> getTradingPlayers() {
        return tradingPlayers;
    }

    public void acceptTrade(Player player, ItemStack item) {
        if (item.getType().equals(Material.REDSTONE_BLOCK)) {
            item.setType(Material.EMERALD_BLOCK);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(player.getName());
            }
            item.setItemMeta(meta);
        } else if (item.getType().equals(Material.EMERALD_BLOCK)) {
            item.setType(Material.REDSTONE_BLOCK);
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getDisplayName().equals(player.getName())) {
                meta.setDisplayName(null);
                item.setItemMeta(meta);
            } else {
                finishTrade(player.getOpenInventory().getTopInventory());
            }

        }
    }

    private void finishTrade(Inventory inv) {
        List<HumanEntity> viewers = inv.getViewers();
        Player one;
        Player two;
        if (tradingPlayers.containsKey(viewers.get(0).getUniqueId())) {
            one = (Player) viewers.get(0);
            two = (Player) viewers.get(1);
        } else {
            one = (Player) viewers.get(1);
            two = (Player) viewers.get(0);
        }

        one.closeInventory();
        two.closeInventory();

        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i) != null) {
                two.getInventory().addItem(inv.getItem(i));
            }
            if (inv.getItem(i + 18) != null) {
                one.getInventory().addItem(inv.getItem(i + 18));
            }
        }
        tradingPlayers.remove(one.getUniqueId());
    }

    public void loadPlayers() {
        OddJob.getInstance().getMySQLManager().loadPlayers();
    }
}
