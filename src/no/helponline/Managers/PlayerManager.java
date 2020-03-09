package no.helponline.Managers;

import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import no.helponline.OddJob;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private HashMap<UUID, Long> inCombat = new HashMap<>();
    private HashMap<UUID, BukkitTask> timerCombat = new HashMap<>();
    public HashMap<UUID, UUID> in = new HashMap<>();

    public void updatePlayer(UUID uuid, String name) {
        OddJob.getInstance().getMySQLManager().updatePlayer(uuid, name);
    }

    public String getName(UUID uuid) {
        return OddJob.getInstance().getMySQLManager().getPlayerName(uuid);
    }

    public UUID getUUID(String name) {
        UUID uuid = null;
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (op.getPlayer().getName().equalsIgnoreCase(name)) {
                uuid = op.getUniqueId();
            }
        }
        return uuid;
    }

    public List<UUID> getUUIDs() {
        return OddJob.getInstance().getMySQLManager().getPlayerMapUUIDs();
    }

    public Player getPlayer(UUID uniqueId) {
        return Bukkit.getServer().getPlayer(uniqueId);
    }

    public OfflinePlayer getOffPlayer(UUID uniqueId) {
        return Bukkit.getServer().getOfflinePlayer(uniqueId);
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
        if (OddJob.getInstance().getPlayerManager().getOffPlayer(player).isOnline())
            (((CraftPlayer) Bukkit.getPlayer(player)).getHandle()).playerConnection.sendPacket(title);
    }

    public void abort(UUID player) {
        inCombat.put(player, System.currentTimeMillis());
        String s = ChatColor.YELLOW + "Aborted";
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + s + "\"}"), 40, 20, 20);
        if (OddJob.getInstance().getPlayerManager().getOffPlayer(player).isOnline())
            (((CraftPlayer) Bukkit.getPlayer(player)).getHandle()).playerConnection.sendPacket(title);
        timerCombat.put(player, new BukkitRunnable() {
            @Override
            public void run() {
                removeInCombat(player);
            }
        }.runTaskLater(OddJob.getInstance(), 40L)); // 40 = 2s (20 ticks/s)
    }
}
