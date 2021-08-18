package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.PlayerSQL;
import com.spillhuset.Utils.Enum.Types;
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
    /**
     * UUID player | UUID guild
     */
    public HashMap<UUID, UUID> in = new HashMap<>();

    private HashMap<UUID, OddPlayer> players = new HashMap<>();

    /**
     * topPlayer UUID | bottomPlayer UUID
     */
    private final HashMap<UUID, UUID> requestTrade;

    /**
     * topPlayer UUID | bottomPlayer UUID
     */
    private final HashMap<UUID, UUID> tradingPlayers;

    /**
     * Combat log for UUID started from LONG
     */
    private final HashMap<UUID, Long> inCombat = new HashMap<>();

    /**
     * UUID timing out of combat with BukkitTask
     */
    private final HashMap<UUID, BukkitTask> timerCombat = new HashMap<>();
    private HashMap<UUID, Inventory> trades = new HashMap<>();

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

    private void setCombatTitle(String text, UUID uuid) {

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

    /**
     * topPlayer | bottomPlayer
     *
     * @return HashMap
     */
    public HashMap<UUID, UUID> getRequestTrade() {
        return requestTrade;
    }

    /**
     * topPlayer UUID | bottomPlayer UUID
     */
    public HashMap<UUID, UUID> getTradingPlayers() {
        return tradingPlayers;
    }

    public void tradeBalance(ItemStack item, Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        boolean top = tradingPlayers.containsKey(player.getUniqueId());
        int value = 0;
        int old = 0;
        boolean negative = false;
        switch (item.getType()) {
            case GOLD_NUGGET -> value = 1;
            case RAW_GOLD -> value = 10;
            case RAW_GOLD_BLOCK -> value = 100;
            case IRON_NUGGET -> {
                value = 1;
                negative = true;
            }
            case RAW_IRON -> {
                value = 10;
                negative = true;
            }
            case RAW_IRON_BLOCK -> {
                value = 100;
                negative = true;
            }
        }
        ItemStack change = inventory.getItem((top) ? 13 : 22);
        if (change != null) {
            ItemMeta meta = change.getItemMeta();
            if (meta != null) {
                old = Integer.parseInt(meta.getDisplayName());
                if (negative && old >= value) {
                    old -= value;
                } else if (!negative) {
                    if (OddJob.getInstance().getCurrencyManager().get(player.getUniqueId()).get(Types.AccountType.pocket) >= (old + value)) {
                        old += value;
                    }
                }
                meta.setDisplayName("" + old);
            }
            change.setItemMeta(meta);
        }
        inventory.setItem((top) ? 13 : 22, change);
    }

    public void acceptTrade(Player player, ItemStack item) {
        if (item.getType().equals(Material.BARRIER)) {
            item.setType(Material.EMERALD_BLOCK);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(player.getName());
            }
            item.setItemMeta(meta);
        } else if (item.getType().equals(Material.EMERALD_BLOCK)) {
            item.setType(Material.BARRIER);
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
        Player topPlayer;
        Player bottomPlayer;
        if (tradingPlayers.containsKey(viewers.get(0).getUniqueId())) {
            topPlayer = (Player) viewers.get(0);
            bottomPlayer = (Player) viewers.get(1);
        } else {
            topPlayer = (Player) viewers.get(1);
            bottomPlayer = (Player) viewers.get(0);
        }

        topPlayer.closeInventory();
        bottomPlayer.closeInventory();

        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i) != null) {
                bottomPlayer.getInventory().addItem(inv.getItem(i));
            }
            if (inv.getItem(i + 27) != null) {
                topPlayer.getInventory().addItem(inv.getItem(i + 27));
            }
        }
        ItemStack topItem = inv.getItem(13);
        if (topItem != null) {
            ItemMeta topMeta = topItem.getItemMeta();
            if (topMeta != null) {
                double topMoney = topMeta.hasDisplayName() ? Double.parseDouble(topMeta.getDisplayName()) : 0.0d;
                if (topMoney > 0.0) {
                    OddJob.getInstance().getCurrencyManager().transfer(topPlayer.getUniqueId(), bottomPlayer.getUniqueId(), topMoney);
                    OddJob.getInstance().getMessageManager().tradedTopPlayer(topPlayer, bottomPlayer, topMoney);
                }
            }
        }

        ItemStack bottomItem = inv.getItem(22);
        if (bottomItem != null) {
            ItemMeta bottomMeta = bottomItem.getItemMeta();
            if (bottomMeta != null) {
                double bottomMoney = bottomMeta.hasDisplayName() ? Double.parseDouble(bottomMeta.getDisplayName()) : 0.0d;
                if (bottomMoney > 0.0) {
                    OddJob.getInstance().getCurrencyManager().transfer(bottomPlayer.getUniqueId(), topPlayer.getUniqueId(), bottomMoney);
                    OddJob.getInstance().getMessageManager().tradedBottomPlayer(topPlayer, bottomPlayer, bottomMoney);
                }
            }
        }
        tradingPlayers.remove(topPlayer.getUniqueId());
    }

    public Inventory getTradeInventory(String topName, String with) {
        Inventory trade = Bukkit.createInventory(null, 36, "FAIR TRADE");
        ItemStack button = new ItemStack(Material.BARRIER);
        ItemStack glass = new ItemStack(Material.GLASS_PANE);
        //** One
        ItemStack subOne = new ItemStack(Material.IRON_NUGGET);
        ItemMeta subOneMeta = subOne.getItemMeta();
        if (subOneMeta != null) {
            subOneMeta.setDisplayName("-1");
            List<String> lore = new ArrayList<>();
            lore.add("-1 from the trade back to you pocket");
            subOneMeta.setLore(lore);
            subOne.setItemMeta(subOneMeta);
        }
        //** Ten
        ItemStack subTen = new ItemStack(Material.RAW_IRON);
        ItemMeta subTenMeta = subTen.getItemMeta();
        if (subTenMeta != null) {
            subTenMeta.setDisplayName("-10");
            List<String> lore = new ArrayList<>();
            lore.add("-10 from the trade back to your pocket");
            subTenMeta.setLore(lore);
            subTen.setItemMeta(subTenMeta);
        }
        //** Ten
        ItemStack subHundred = new ItemStack(Material.RAW_IRON_BLOCK);
        ItemMeta subHundredMeta = subHundred.getItemMeta();
        if (subHundredMeta != null) {
            subHundredMeta.setDisplayName("-100");
            List<String> lore = new ArrayList<>();
            lore.add("-100 from the trade back to the pocket");
            subHundredMeta.setLore(lore);
            subHundred.setItemMeta(subHundredMeta);
        }
        //** One
        ItemStack addOne = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta addOneMeta = addOne.getItemMeta();
        if (addOneMeta != null) {
            addOneMeta.setDisplayName("+1");
            List<String> lore = new ArrayList<>();
            lore.add("+1 to the trade from your pocket");
            addOneMeta.setLore(lore);
            addOne.setItemMeta(addOneMeta);
        }
        //** Ten
        ItemStack addTen = new ItemStack(Material.RAW_GOLD);
        ItemMeta addTenMeta = addTen.getItemMeta();
        if (addTenMeta != null) {
            addTenMeta.setDisplayName("+10");
            List<String> lore = new ArrayList<>();
            lore.add("+10 to the trade from your pocket");
            addTenMeta.setLore(lore);
            addTen.setItemMeta(addTenMeta);
        }
        //** addHundred
        ItemStack addHundred = new ItemStack(Material.RAW_GOLD_BLOCK);
        ItemMeta addHundredMeta = addHundred.getItemMeta();
        if (addHundredMeta != null) {
            addHundredMeta.setDisplayName("+100");
            List<String> lore = new ArrayList<>();
            lore.add("+100 to the trade from your pocket");
            addHundredMeta.setLore(lore);
            addHundred.setItemMeta(addHundredMeta);
        }

        //** TOP offer
        ItemStack topOffer = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta topOfferMeta = topOffer.getItemMeta();
        if (topOfferMeta != null) {
            topOfferMeta.setDisplayName("0");
            List<String> lore = new ArrayList<>();
            lore.add("Offer from " + topName);
            topOfferMeta.setLore(lore);
            topOffer.setItemMeta(topOfferMeta);
        }

        //** BOTTOM offer
        ItemStack bottomOffer = new ItemStack(Material.COPPER_BLOCK);
        ItemMeta bottomOfferMeta = bottomOffer.getItemMeta();
        if (bottomOfferMeta != null) {
            bottomOfferMeta.setDisplayName("0");
            List<String> lore = new ArrayList<>();
            lore.add("Offer from " + with);
            bottomOfferMeta.setLore(lore);
            bottomOffer.setItemMeta(bottomOfferMeta);
        }

        trade.setItem(9, addOne);
        trade.setItem(10, addTen);
        trade.setItem(11, addHundred);
        trade.setItem(12, glass);
        trade.setItem(13, topOffer);
        trade.setItem(14, glass);
        trade.setItem(15, glass);
        trade.setItem(16, glass);
        trade.setItem(17, button);
        trade.setItem(18, subOne);
        trade.setItem(19, subTen);
        trade.setItem(20, subHundred);
        trade.setItem(21, glass);
        trade.setItem(22, bottomOffer);
        trade.setItem(23, glass);
        trade.setItem(24, glass);
        trade.setItem(25, glass);
        trade.setItem(26, glass);

        return trade;
    }

    public void loadPlayer(UUID uuid) {
        OddPlayer oddPlayer = PlayerSQL.load(uuid);
        if (oddPlayer != null) {
            players.put(uuid, oddPlayer);
            oddPlayer.getPlayer().setScoreboard(OddJob.getInstance().getScoreManager().scoreboardManager.getNewScoreboard());
        }
    }

    public int getMaxHomes(UUID target) {
        return players.get(target).getMaxHomes();
    }

    public void setMaxHomes(UUID target, int i) {
        OddPlayer oddPlayer = players.get(target);
        oddPlayer.setMaxHomes(i);
        savePlayer(target);
    }

    public void setGameMode(Player target, GameMode gm) {
        target.setGameMode(gm);
        getOddPlayer(target.getUniqueId()).setGameMode(gm);
    }

    public void load() {
        players = PlayerSQL.load();
    }

    public void savePlayer(UUID uuid) {
        OddPlayer oddPlayer = players.get(uuid);
        if (oddPlayer != null) {
            PlayerSQL.save(oddPlayer);
        }
    }

    public GameMode getGameMode(UUID uuid) {
        return getOddPlayer(uuid).getGameMode();
    }

    public void addTrade(UUID topPlayer, UUID bottomPlayer, Inventory trade) {
        trades.put(topPlayer, trade);
        trades.put(bottomPlayer, trade);
    }


    public void tradeAccept(Player topPlayer, Player bottomPlayer) {
        if (OddJob.getInstance().getPlayerManager().getRequestTrade().get(topPlayer.getUniqueId()) == bottomPlayer.getUniqueId()) {
            Inventory trade = OddJob.getInstance().getPlayerManager().getTradeInventory(topPlayer.getDisplayName(),bottomPlayer.getDisplayName());
            OddJob.getInstance().getPlayerManager().addTrade(topPlayer.getUniqueId(),bottomPlayer.getUniqueId(),trade);
            bottomPlayer.openInventory(trade);
            topPlayer.openInventory(trade);
            OddJob.getInstance().getPlayerManager().getTradingPlayers().put(topPlayer.getUniqueId(), bottomPlayer.getUniqueId());
            OddJob.getInstance().getPlayerManager().getRequestTrade().remove(topPlayer.getUniqueId());
        } else {
            OddJob.getInstance().getMessageManager().tradeNone(bottomPlayer);
        }
    }
}
