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

    public void updatePlayer(UUID uuid, String name) {
        OddJob.getInstance().getMySQLManager().updatePlayer(uuid, name);
        /*if (oddPlayers.containsKey(uuid)) {
            OddPlayer oddPlayer = oddPlayers.get(uuid);
            oddPlayer.setName(name);
            oddPlayers.put(uuid, oddPlayer);
        } else {
            create(uuid, Bukkit.getPlayer(uuid).getName(), false, new ArrayList<>(), new ArrayList<>());
            OddJob.getInstance().log("created " + uuid.toString());
        }*/
    }

    public String getName(UUID uuid) {
        return OddJob.getInstance().getMySQLManager().getPlayerName(uuid);
        //return oddPlayers.get(uuid).getName();
    }

    public UUID getUUID(String name) {

        UUID uuid = OddJob.getInstance().getMySQLManager().getPlayerUUID(name);
        if (uuid == null) {
            for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                if (op.getName().equalsIgnoreCase(name)) {
                    uuid = op.getUniqueId();
                }
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

    public boolean request(UUID to, UUID from) {
        boolean request = OddJob.getInstance().getMySQLManager().getPlayerDenyTpa(to); // false
        if (OddJob.getInstance().getMySQLManager().getPlayerWhiteList(to).contains(from)) { //false
            OddJob.getInstance().log("whitelist");
            request = true;
        } else if (OddJob.getInstance().getMySQLManager().getPlayerBlackList(to).contains(from)) { // false
            OddJob.getInstance().log("blacklist");
            request = false;
        } else if (OddJob.getInstance().getMySQLManager().getPlayerDenyTpa(to)) { // false
            OddJob.getInstance().log("tpa deny");
            OddJob.getInstance().getMessageManager().warning(getName(to) + " is denying all request!", from);
            request = false;
        }
        OddJob.getInstance().log("request : deny");
        return !request;
    }

    public List<String> getNames() {
        return OddJob.getInstance().getMySQLManager().getPlayerMapNames();
    }

    public GameMode getGamemode(Player player, World world) {
        return OddJob.getInstance().getMySQLManager().getPlayerMode(player, world);
    }

    public void setGameMode(Player player, GameMode gameMode) {
        OddJob.getInstance().getMySQLManager().setGameMode(player, gameMode);
        player.setGameMode(gameMode);
        player.sendMessage("Your GameMode is set to " + gameMode.name() + " in " + player.getWorld().getName());
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
                OddJob.getInstance().getPlayerManager().removeInCombat(player);
            }
        }.runTaskLater(OddJob.getInstance(), 200));
    }

    private void removeInCombat(UUID player) {
        if (timerCombat.get(player) != null) timerCombat.get(player).cancel();
        timerCombat.remove(player);
        inCombat.remove(player);
        String s = ChatColor.GREEN + "Out of combat";
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + s + "\"}"), 40, 20, 20);
        (((CraftPlayer) Bukkit.getPlayer(player)).getHandle()).playerConnection.sendPacket(title);
    }
}
