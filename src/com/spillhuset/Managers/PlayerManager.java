package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.PlayerSQL;
import com.spillhuset.Utils.Odd.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PlayerManager {
    private HashMap<UUID, OddPlayer> players = new HashMap<>();
    private final HashMap<UUID, UUID> requestTrade;
    private final HashMap<UUID, UUID> tradingPlayers;
    private final HashMap<UUID, Long> inCombat = new HashMap<>();
    private final HashMap<UUID, BukkitTask> timerCombat = new HashMap<>();
    public HashMap<UUID, UUID> in = new HashMap<>();
    private final HashMap<UUID, List<UUID>> inBed = new HashMap<>();
    private final HashMap<UUID, List<UUID>> notInBed = new HashMap<>();

    public PlayerManager() {
        requestTrade = new HashMap<>();
        tradingPlayers = new HashMap<>();
    }

    public void save() {
        PlayerSQL.save(players);
    }

    public String getName(UUID uuid) {
        return players.get(uuid).getName();
    }

    public UUID getUUID(String name) {
        for (UUID uuid : players.keySet()) {
            if (players.get(uuid).getName().equalsIgnoreCase(name)) return uuid;
        }
        return null;
    }

    public Set<UUID> getUUIDs() {
        return players.keySet();
    }

    public Player getPlayer(UUID uniqueId) {
        return Bukkit.getServer().getPlayer(uniqueId);
    }

    public OddPlayer getOddPlayer(UUID uniqueId) {
        return players.get(uniqueId);
    }

    public boolean request(UUID moving, UUID destination) {
        OddPlayer oddDestination = OddJob.getInstance().getPlayerManager().getOddPlayer(destination);
        boolean request = true; // false
        if (oddDestination.getBlacklist().contains(moving)) {
            request = false;
        } else if (oddDestination.getDenyTpa()) {
            OddJob.getInstance().getMessageManager().playerDenying(getName(destination), moving);
            request = false;
        }
        return !request;
    }

    public Collection<String> getNames() {
        Collection<String> list = new ArrayList<>();
        for (UUID uuid : players.keySet()) {
            list.add(players.get(uuid).getName());
        }
        return list;
    }

    public boolean isInCombat(UUID player) {
        if (inCombat.get(player) != null) {
            return inCombat.get(player) > (System.currentTimeMillis() - 1000L);
        }
        return false;
    }

    public void setInCombat(UUID player) {
        if (timerCombat.get(player) != null) {
            timerCombat.get(player).cancel();
        }
        inCombat.put(player, System.currentTimeMillis());
        String text = ChatColor.DARK_RED + "In combat";
        setCombatTitle(text, player);
        timerCombat.put(player, new BukkitRunnable() {
            @Override
            public void run() {
                removeInCombat(player);
            }
        }.runTaskLater(OddJob.getInstance(), 200L)); // 200 = 10s (20 ticks/s)
    }

    private void setCombatTitle(String text, UUID player) {
        /*PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), 40, 20, 20);
        if (OddJob.getInstance().getPlayerManager().getPlayer(player).isOnline())
            (((CraftPlayer) Bukkit.getPlayer(player)).getHandle()).playerConnection.sendPacket(title);*/
    }

    private void removeInCombat(UUID player) {
        if (timerCombat.get(player) != null) timerCombat.get(player).cancel();
        timerCombat.remove(player);
        inCombat.remove(player);
        String text = ChatColor.GREEN + "Out of combat";
        setCombatTitle(text, player);
    }

    public void abort(UUID player) {
        inCombat.put(player, System.currentTimeMillis());
        String text = ChatColor.YELLOW + "Aborted";
        setCombatTitle(text, player);
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

    public List<UUID> getInBed(UUID worldUUID) {
        if (!inBed.containsKey(worldUUID)) inBed.put(worldUUID, new ArrayList<>());
        return inBed.get(worldUUID);
    }

    public void setInBed(UUID worldUUID, UUID playerUUID) {
        if (!inBed.containsKey(worldUUID)) inBed.put(worldUUID, new ArrayList<>());
        inBed.get(worldUUID).add(playerUUID);
    }

    public List<UUID> getNotInBed(UUID worldUUID) {
        if (!notInBed.containsKey(worldUUID)) notInBed.put(worldUUID, new ArrayList<>());
        return notInBed.get(worldUUID);
    }

    public void setNotInBed(UUID worldUUID, UUID playerUUID) {
        if (!notInBed.containsKey(worldUUID)) notInBed.put(worldUUID, new ArrayList<>());
        notInBed.get(worldUUID).add(playerUUID);
    }

    public void sleep(UUID worldUUID) {
        inBed.remove(worldUUID);
        notInBed.remove(worldUUID);
        Bukkit.getWorld(worldUUID).setTime(0L);
    }

    public Inventory getTradeInventory() {
        Inventory trade = Bukkit.createInventory(null, 27, "FAIR TRADE");
        ItemStack button = new ItemStack(Material.REDSTONE_BLOCK);
        ItemStack glass = new ItemStack(Material.GLASS_PANE);

        trade.setItem(9, glass);
        trade.setItem(10, glass);
        trade.setItem(11, glass);
        trade.setItem(12, glass);
        trade.setItem(13, glass);
        trade.setItem(14, glass);
        trade.setItem(15, glass);
        trade.setItem(16, glass);
        trade.setItem(17, button);

        return trade;
    }

    public void loadPlayer(UUID uuid) {
        OddPlayer oddPlayer = PlayerSQL.load(uuid);
        if (oddPlayer != null) {
            players.put(uuid, oddPlayer);
        }
    }

    public int getMaxHomes(UUID target) {
        return players.get(target).getMaxHomes();
    }

    public void setMaxHomes(UUID target, int i) {
        OddPlayer oddPlayer = players.get(target);
        oddPlayer.setMaxHomes(i);
        save(oddPlayer);
    }

    public void save(OddPlayer target) {
        OddJob.getInstance().log("saving: " + target.getName());
        PlayerSQL.save(target);
    }

    public void setGameMode(Player target, GameMode gm) {
        target.setGameMode(gm);
    }

    public void load() {
        players = PlayerSQL.load();
    }
}
