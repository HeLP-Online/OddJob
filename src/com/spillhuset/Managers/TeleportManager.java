package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeleportManager extends CostManager {
    /**
     * topPlayer | bottomPlayer
     */
    private final HashMap<UUID, UUID> request = new HashMap<>();

    /**
     * topPlayer | TPATimer
     */
    private final HashMap<UUID, BukkitRunnable> reset = new HashMap<>();

    /**
     * topPlayer | teleportTimer
     */
    private final HashMap<UUID, BukkitTask> teleportTimer;

    public TeleportManager() {
        teleportTimer = new HashMap<>();
    }

    /**
     * Performing the requested /tpa
     *
     * @param topPlayer    UUID
     * @param bottomPlayer UUID
     * @return boolean
     */
    public boolean request(UUID topPlayer, UUID bottomPlayer) {
        OddJob.getInstance().log("Opps here");
        return true;
    }

    /**
     * Check if a player got has a request.
     *
     * @param topPlayer UUID of the sender of /tpa
     * @return boolean true if exists
     */
    public boolean hasRequest(UUID topPlayer) {
        return request.containsKey(topPlayer);
    }

    // TODO Tpa cost?
    public void accept(UUID topUUID) {
        if (hasRequest(topUUID)) {
            // Find the request
            UUID bottomUUID = request.get(topUUID);

            // player (sends request) // target (teleport to)
            Player topPlayer = Bukkit.getPlayer(topUUID);
            Player bottomPlayer = Bukkit.getPlayer(bottomUUID);

            // Check if both is online
            if (bottomPlayer == null || !bottomPlayer.isOnline()) {
                OddJob.getInstance().getMessageManager().tpOffline(OddJob.getInstance().getPlayerManager().getOddPlayer(bottomUUID).getName(), topPlayer);
                return;
            } else if (topPlayer == null || !topPlayer.isOnline()) {
                OddJob.getInstance().getMessageManager().tpOffline(OddJob.getInstance().getPlayerManager().getOddPlayer(topUUID).getName(), bottomPlayer);
                return;
            }

            // Performing
            OddJob.getInstance().getMessageManager().tpAccepted(bottomUUID, bottomPlayer.getName(), topUUID, topPlayer.getName());
            teleport(topPlayer, bottomPlayer, PlayerTeleportEvent.TeleportCause.COMMAND, true);
            if (reset.containsKey(topUUID)) {
                reset.get(topUUID).cancel();
            }
            removeRequest(topUUID);
        }

    }


    public void cancel(UUID uuid) {
        if (teleportTimer.containsKey(uuid)) {
            BukkitTask task = teleportTimer.get(uuid);
            task.cancel();
            teleportTimer.remove(uuid);
        }
    }

    public void teleport(Player movingPlayer, CommandSender destinationPlayer, PlayerTeleportEvent.TeleportCause cause, boolean countdown) {
        boolean test = true;
        if (OddJob.getInstance().getPlayerManager().isInCombat(movingPlayer.getUniqueId())) {
            // Player is in combat
            OddJob.getInstance().getMessageManager().console("aborted combat");
            test = false;
            // TODO
        /*} else if (OddJob.getInstance().getArenaManager().isInArena(movingPlayer.getUniqueId())) {
            // Player is in Arena
            OddJob.getInstance().getMessageManager().console("aborted arena");
            test = false;
        */
        } else if (OddJob.getInstance().getJailManager().in(movingPlayer.getUniqueId()) != null) {
            // Player is in Jail
            OddJob.getInstance().getMessageManager().console("aborted in jail");
            test = false;
        } else if (OddJob.getInstance().getFreezeManager().get(movingPlayer.getUniqueId()) != null) {
            // Player is Frozen
            OddJob.getInstance().getMessageManager().console("aborted frozen");
            test = false;
        }

        if (test) {
            // Updating /back command
            OddJob.getInstance().getMySQLManager().updateTeleport(movingPlayer);
            UUID movingUUID = movingPlayer.getUniqueId();
            if (movingPlayer.hasPermission("oddjob.teleport.now")) {
                OddJob.getInstance().getMessageManager().tpNow(movingUUID);
                movingPlayer.teleport((Player) destinationPlayer, cause);
                return;
            }
            if (teleportTimer.containsKey(movingUUID)) {
                // Changing teleport location
                teleportTimer.get(movingUUID).cancel();
                teleportTimer.remove(movingUUID);
                OddJob.getInstance().getMessageManager().tpChanging(movingUUID);
            }
            // Start timer
            if (countdown) {
                teleportTimer.put(movingUUID, new BukkitRunnable() {
                    int i = 10;

                    @Override
                    public void run() {
                        if (!movingPlayer.isOnline()) cancel();

                        if (OddJob.getInstance().getPlayerManager().isInCombat(movingUUID)) {
                            // In combat, cancel
                            OddJob.getInstance().getMessageManager().tpInterrupt(movingUUID);
                            cancel();
                        }
                        if (i > 0) {
                            OddJob.getInstance().getMessageManager().tpCountdown(i, movingUUID);
                        } else {
                            OddJob.getInstance().getMessageManager().tpTeleporting(movingUUID);
                            movingPlayer.teleport((Player) destinationPlayer, cause);

                            cancel();
                        }
                        i--;
                    }
                }.runTaskTimer(OddJob.getInstance(), 20, 10));
            } else {
                movingPlayer.teleport((Player) destinationPlayer, cause);
            }
        }
    }

    public boolean teleport(Player movingPlayer, Location destination, PlayerTeleportEvent.TeleportCause cause, boolean countdown) {
        boolean test = true;
        if (OddJob.getInstance().getPlayerManager().isInCombat(movingPlayer.getUniqueId())) {
            // Player is in combat
            OddJob.getInstance().getMessageManager().console("aborted combat");
            test = false;
            // TODO
        /*} else if (OddJob.getInstance().getArenaManager().isInArena(movingPlayer.getUniqueId())) {
            // Player is in Arena
            OddJob.getInstance().getMessageManager().console("aborted arena");
            test = false;
        */
        } else if (OddJob.getInstance().getJailManager().in(movingPlayer.getUniqueId()) != null) {
            // Player is in Jail
            OddJob.getInstance().getMessageManager().console("aborted in jail");
            test = false;
        } else if (OddJob.getInstance().getFreezeManager().get(movingPlayer.getUniqueId()) != null) {
            // Player is Frozen
            OddJob.getInstance().getMessageManager().console("aborted frozen");
            test = false;
        }

        if (test) {
            OddJob.getInstance().getMySQLManager().updateTeleport(movingPlayer);
            UUID movingUUID = movingPlayer.getUniqueId();
            if (movingPlayer.hasPermission("oddjob.teleport.now")) {
                OddJob.getInstance().getMessageManager().tpNow(movingUUID);
                movingPlayer.teleport(destination, cause);
                return true;
            } else {
                if (teleportTimer.containsKey(movingUUID)) {
                    teleportTimer.get(movingUUID).cancel();
                    teleportTimer.remove(movingUUID);
                    OddJob.getInstance().getMessageManager().tpChanging(movingUUID);
                }
                if (countdown) {
                    teleportTimer.put(movingPlayer.getUniqueId(), new BukkitRunnable() {
                        int i = 10;

                        @Override
                        public void run() {
                            if (!movingPlayer.isOnline()) cancel();
                            if (OddJob.getInstance().getPlayerManager().isInCombat(movingPlayer.getUniqueId())) {
                                OddJob.getInstance().getMessageManager().tpInterrupt(movingUUID);
                                cancel();
                                return;
                            }
                            if (i > 0) {
                                OddJob.getInstance().getMessageManager().tpCountdown(i, movingUUID);
                            } else {
                                teleportTimer.remove(movingUUID);
                                OddJob.getInstance().getMessageManager().tpTeleporting(movingUUID);
                                movingPlayer.teleport(destination, cause);

                                cancel();
                            }
                            i--;
                        }
                    }.runTaskTimer(OddJob.getInstance(), 20, 10));
                } else {
                    movingPlayer.teleport(destination, cause);
                }
            }
        }
        return test;
    }

    public void deny(UUID bottomUUID) {
        if (request.containsValue(bottomUUID)) {
            for (UUID topUUID : request.keySet()) {
                if (request.get(topUUID).equals(bottomUUID)) {
                    // topPlayer | bottomPlayer
                    removeRequest(topUUID);
                    if (reset.containsKey(topUUID)) reset.get(topUUID).cancel();
                }
            }
        }
    }

    private void startTimer(UUID topUUID, UUID bottomUUID) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (hasRequest(topUUID)) {
                    removeRequest(topUUID);
                    reset.remove(topUUID);
                    Player bottomPlayer = Bukkit.getPlayer(topUUID);
                    if (bottomPlayer == null) {
                        OddJob.getInstance().getMessageManager().teleportNotOnline(topUUID);
                        return;
                    }
                    bottomPlayer = Bukkit.getPlayer(bottomUUID);
                    if (bottomPlayer == null) {
                        OddJob.getInstance().getMessageManager().teleportNotOnline(bottomUUID);
                        return;
                    }
                    OddJob.getInstance().getMessageManager().teleportTimedOut(topUUID, bottomUUID);
                }
            }
        };
        task.runTaskLater(OddJob.getInstance(), 1200L);
        reset.put(topUUID, task);
    }

    private void removeRequest(UUID topUUID) {
        request.remove(topUUID);
        reset.remove(topUUID);
    }

    // back <player>
    public void back(Player player) {
        //teleport(player, OddJob.getInstance().getMySQLManager().getBack(player.getUniqueId()), PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    public void teleport(Player target, int x, int y, int
            z, World world, boolean countdown) {
        Location destination = new Location(world, x, y, z);
        teleport(target, destination, PlayerTeleportEvent.TeleportCause.COMMAND, countdown);
        OddJob.getInstance().getMessageManager().tpPos(target.getUniqueId());
    }

    public void teleport(CommandSender commandSender, String destinationName) {
        if (!(commandSender instanceof Player movingPlayer)) {
            return;
        }
        Player destinationPlayer = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(destinationName));
        if (destinationPlayer == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.teleport, destinationName, commandSender);
            return;
        }
        teleport(movingPlayer, destinationPlayer.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND, true);
        OddJob.getInstance().getMessageManager().tpPlayer(destinationName, movingPlayer);
    }

    public void spawn(Player player, Location location) {
        if(CostManager.cost(player.getUniqueId(),"teleport.spawn")) {
            teleport(player, location, PlayerTeleportEvent.TeleportCause.COMMAND, true);
            OddJob.getInstance().getMessageManager().teleportSpawn(player);
        }
    }

    public void jail(Player player, Location lobby) {
        player.teleport(lobby, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public Location getSpawn() {
        return Bukkit.getWorld("world").getSpawnLocation();
    }

    public int hasRequests(UUID bottomUUID) {
        int i = 0;
        for (UUID uuid : request.values()) {
            if (uuid.equals(bottomUUID)) i++;
        }
        return i;
    }

    public UUID getRequest(UUID bottomUUID) {
        for (UUID uuid : request.keySet()) {
            if (request.get(uuid) == bottomUUID) {
                return uuid;
            }
        }
        return null;
    }

    public void delRequest(UUID topPlayer) {
        request.remove(topPlayer);
    }

    public List<String> getRequestList(UUID bottomPlayer) {
        List<String> list = new ArrayList<>();
        for (UUID uuid : request.keySet()) {
            if (request.get(uuid) == bottomPlayer) {
                list.add(OddJob.getInstance().getPlayerManager().getName(uuid));
            }
        }
        return list;
    }

    public void addRequest(UUID topUUID, UUID bottomUUID) {
        if (CostManager.cost(topUUID, bottomUUID, "teleport.request")) {
            request.put(topUUID, bottomUUID);
            startTimer(topUUID, bottomUUID);
        }
    }

    public void acceptRequest(Player topPlayer, Player bottomPlayer) {
        if (CostManager.cost(bottomPlayer.getUniqueId(), topPlayer.getUniqueId(), "teleport.accept")) {
            if (reset.containsKey(topPlayer.getUniqueId())) {
                reset.get(topPlayer.getUniqueId()).cancel();
            }
            removeRequest(topPlayer.getUniqueId());
        }
    }
}
