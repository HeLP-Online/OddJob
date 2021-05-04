package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager {
    private final HashMap<UUID, UUID> request = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> reset = new HashMap<>();
    private final HashMap<UUID, BukkitTask> teleportTimer;

    public TeleportManager() {
        teleportTimer = new HashMap<>();
    }

    public boolean request(UUID movingPlayer, UUID destinationPlayer) {
        request.put(movingPlayer, destinationPlayer);
        startTimer(movingPlayer, destinationPlayer);
        return true;
    }
    // player (sends request) | target (teleport to)

    public boolean hasRequest(UUID from) {
        return request.containsKey(from);
    }

    // TODO Tpa cost?
    public void accept(UUID movingUUID) {
        if (hasRequest(movingUUID)) {
            // Player got a request
            UUID destinationUUID = request.get(movingUUID);
            // player (sends request) // target (teleport to)
            Player movingPlayer = OddJob.getInstance().getPlayerManager().getPlayer(movingUUID);
            Player destinationPlayer = OddJob.getInstance().getPlayerManager().getPlayer(destinationUUID);
            OddJob.getInstance().getMessageManager().tpAccepted(destinationUUID, destinationPlayer.getName(), movingUUID, movingPlayer.getName());
            teleport(movingPlayer, destinationPlayer, PlayerTeleportEvent.TeleportCause.COMMAND,true);
            if (reset.containsKey(movingUUID)) {
                reset.get(movingUUID).cancel();
            }
            removeRequest(movingUUID);
        }

    }


    public void cancel(UUID uuid) {
        if (teleportTimer.containsKey(uuid)) {
            BukkitTask task = teleportTimer.get(uuid);
            task.cancel();
            teleportTimer.remove(uuid);
        }
    }

    public void teleport(Player movingPlayer, Player destinationPlayer, PlayerTeleportEvent.TeleportCause cause, boolean countdown) {
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
                movingPlayer.teleport(destinationPlayer, cause);
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
                            movingPlayer.teleport(destinationPlayer, cause);

                            cancel();
                        }
                        i--;
                    }
                }.runTaskTimer(OddJob.getInstance(), 20, 10));
            } else {
                movingPlayer.teleport(destinationPlayer, cause);
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

    public void deny(UUID to) {
        if (request.containsValue(to)) {
            for (UUID from : request.keySet()) {
                if (request.get(from).equals(to)) {
                    // player (sends request) // target (teleport to)
                    Player player = OddJob.getInstance().getPlayerManager().getPlayer(request.get(from));
                    Player target = OddJob.getInstance().getPlayerManager().getPlayer(from);
                    OddJob.getInstance().getMessageManager().tpDeny(player.getUniqueId(), player.getName(), target.getUniqueId(), target.getName());
                    removeRequest(from);
                    if (reset.containsKey(from)) reset.get(from).cancel();
                }
            }
        }
    }

    private void startTimer(UUID movingUUID, UUID destinationUUID) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (hasRequest(movingUUID)) {
                    removeRequest(movingUUID);
                    reset.remove(movingUUID);
                    OfflinePlayer player = Bukkit.getOfflinePlayer(movingUUID);
                    if (player.isOnline()) {
                        OddJob.getInstance().getMessageManager().tpTimedOut(movingUUID);
                    }
                    player = Bukkit.getOfflinePlayer(destinationUUID);
                    if (player.isOnline()) {
                        OddJob.getInstance().getMessageManager().tpTeleporting(destinationUUID);
                    }
                }
            }
        };
        task.runTaskLater(OddJob.getInstance(), 1200L);
        reset.put(movingUUID, task);
    }

    private void removeRequest(UUID from) {
        request.remove(from);
        reset.remove(from);
    }

    // back <player>
    public void back(Player player) {
        //teleport(player, OddJob.getInstance().getMySQLManager().getBack(player.getUniqueId()), PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    // tp <player> <player>
    public void teleport(CommandSender commandSender, String movingName, String destinationName) {
        Player movingPlayer = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(movingName));
        Player destinationPlayer = null;
        if (movingPlayer == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.tp, movingName, commandSender);
            return;
        }
        if (!destinationName.equals("")) {
            destinationPlayer = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(destinationName));
            if (destinationPlayer == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(Plugin.tp, destinationName, commandSender);
                return;
            }
        } else if (commandSender instanceof Player) {
            destinationPlayer = ((Player) commandSender);
        }

        if (destinationPlayer != null) {
            OddJob.getInstance().getTeleportManager().teleport(movingPlayer, destinationPlayer.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND,true);
        } else {
            OddJob.getInstance().getMessageManager().tpError(commandSender);
        }

    }

    public void teleport(Player target, int x, int y, int
            z, World world,boolean countdown) {
        Location destination = new Location(world, x, y, z);
        teleport(target, destination, PlayerTeleportEvent.TeleportCause.COMMAND,countdown);
        OddJob.getInstance().getMessageManager().tpPos(target.getUniqueId());
    }

    public void teleport(CommandSender commandSender, String destinationName) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player movingPlayer = (Player) commandSender;
        Player destinationPlayer = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(destinationName));
        if (destinationPlayer == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.tp, destinationName, commandSender);
            return;
        }
        teleport(movingPlayer, destinationPlayer.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND,true);
        OddJob.getInstance().getMessageManager().tpPlayer(destinationName, movingPlayer);
    }

    public void spawn(Player player, Location location) {
        teleport(player, location, PlayerTeleportEvent.TeleportCause.COMMAND,true);
        OddJob.getInstance().getMessageManager().tpSpawn(player);
    }

    public void jail(Player player, Location lobby) {
        player.teleport(lobby, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public Location getSpawn() {
        return Bukkit.getWorld("world").getSpawnLocation();
    }
}
