package no.helponline.Managers;

import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import no.helponline.OddJob;
import no.helponline.Utils.Odd.OddPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
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
    //private final HashMap<UUID, String> names = new HashMap<>();
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
        OddJob.getInstance().getMySQLManager().savePlayers(players);
    }

    public void load() {
        players = OddJob.getInstance().getMySQLManager().loadPlayers();
    }

    public void updatePlayer(UUID uuid, String name) {
        OddJob.getInstance().getMySQLManager().updatePlayer(uuid, name);
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
        OddJob.getInstance().getMessageManager().console("request");
        OddPlayer oddDestination = OddJob.getInstance().getPlayerManager().getOddPlayer(destination);
        boolean request = true; // false
        if (oddDestination.getWhitelist().contains(moving)) { //false
            OddJob.getInstance().log("whitelist");
            request = true;
        } else if (oddDestination.getBlacklist().contains(moving)) { // false
            OddJob.getInstance().log("blacklist");
            request = false;
        } else if (oddDestination.getDenyTpa()) { // false
            OddJob.getInstance().log("tpa deny");
            OddJob.getInstance().getMessageManager().warning(getName(destination) + " is denying all request!", moving, false);
            request = false;
        }
        OddJob.getInstance().getMessageManager().console("request "+request);
        return !request;
    }

    public Collection<String> getNames() {
        Collection<String> list = new ArrayList<>();
        for (UUID uuid : players.keySet()){
            list.add(players.get(uuid).getName());
        }
        return list;
    }

    public GameMode getGameMode(Player player, World world) {
        return OddJob.getInstance().getMySQLManager().getPlayerMode(player, world);
    }

    public void setGameMode(Player player, GameMode gameMode) {
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
}
