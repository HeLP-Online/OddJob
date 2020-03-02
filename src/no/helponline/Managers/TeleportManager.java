package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager {
    private final HashMap<UUID, UUID> teleportAccept = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> reset = new HashMap<>();
    private final HashMap<UUID, BukkitTask> teleportTimer;
    private final double cost_player_to_player = OddJob.getInstance().getEconManager().cost("teleport_player_to_player");
    private final double cost_player_to_location = OddJob.getInstance().getEconManager().cost("teleport_player_to_location");
    private final double cost_back = OddJob.getInstance().getEconManager().cost("teleport_back");
    private final double cost_spawn = OddJob.getInstance().getEconManager().cost("teleport_spawn");

    public TeleportManager() {
        teleportTimer = new HashMap<>();
    }

    public boolean request(UUID movingPlayer, UUID destinationPlayer) {
        // Is the receiver denying all requests
        if (!OddJob.getInstance().getPlayerManager().request(movingPlayer, destinationPlayer)) return false;
        if (hasRequest(movingPlayer)) {
            // The Player already sent a request
            OddJob.getInstance().getMessageManager().warning("Rewriting existing TPA request to " + ChatColor.DARK_AQUA + OddJob.getInstance().getPlayerManager().getName(teleportAccept.get(destinationPlayer)), movingPlayer, false);
        }

        teleportAccept.put(movingPlayer, destinationPlayer);
        startTimer(movingPlayer, destinationPlayer);
        return true;
    }
    // player (sends request) | target (teleport to)

    public boolean hasRequest(UUID from) {
        OddJob.getInstance().getMessageManager().console("teleportAccept check");
        return teleportAccept.containsKey(from);
    }

    // TODO Tpa cost?
    public void accept(UUID movingUUID) {
        if (hasRequest(movingUUID)) {
            // Player got a request
            UUID destinationUUID = teleportAccept.get(movingUUID);
            // player (sends request) // target (teleport to)
            Player movingPlayer = OddJob.getInstance().getPlayerManager().getPlayer(movingUUID);
            Player destinationPlayer = OddJob.getInstance().getPlayerManager().getPlayer(destinationUUID);
            OddJob.getInstance().getMessageManager().success("Your request has been accepted by " + ChatColor.DARK_AQUA + destinationPlayer.getName(), movingUUID, false);
            OddJob.getInstance().getMessageManager().success("You have accepted the request from " + ChatColor.DARK_AQUA + movingPlayer.getName(), destinationUUID, true);
            teleport(movingPlayer, destinationPlayer, PlayerTeleportEvent.TeleportCause.COMMAND);
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

    public void teleport(Player movingPlayer, Player destinationPlayer, PlayerTeleportEvent.TeleportCause cause) {
        boolean test = true;
        if (OddJob.getInstance().getPlayerManager().isInCombat(movingPlayer.getUniqueId())) {
            // Player is in combat
            OddJob.getInstance().getMessageManager().console("aborted combat");
            test = false;
        } else if (OddJob.getInstance().getArenaManager().isInArena(movingPlayer.getUniqueId())) {
            // Player is in Arena
            OddJob.getInstance().getMessageManager().console("aborted arena");
            test = false;
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
                OddJob.getInstance().getMessageManager().success("Teleporting now!", movingUUID, true);
                movingPlayer.teleport(destinationPlayer, cause);
                return;
            }
            if (teleportTimer.containsKey(movingUUID)) {
                // Changing teleport location
                teleportTimer.get(movingUUID).cancel();
                teleportTimer.remove(movingUUID);
                OddJob.getInstance().getMessageManager().warning("Changing teleport location.", movingPlayer, false);
            }
            // Start timer
            teleportTimer.put(movingUUID, new BukkitRunnable() {
                int i = 10;

                @Override
                public void run() {
                    if (!movingPlayer.isOnline()) cancel();

                    if (OddJob.getInstance().getPlayerManager().isInCombat(movingUUID)) {
                        // In combat, cancel
                        OddJob.getInstance().getMessageManager().danger("Interrupted, in combat!", movingPlayer, true);
                        cancel();
                    }
                    if (i > 0) {
                        OddJob.getInstance().getMessageManager().info("Teleporting in " + ChatColor.WHITE + i, movingPlayer, false);
                    } else {
                        OddJob.getInstance().getMessageManager().success("Teleporting!", movingPlayer, true);
                        movingPlayer.teleport(destinationPlayer, cause);

                        cancel();
                    }
                    i--;
                }
            }.runTaskTimer(OddJob.getInstance(), 20, 20));
        }
    }

    public boolean teleport(Player movingPlayer, Location destination, double cost, PlayerTeleportEvent.TeleportCause cause) {
        boolean test = true;
        if (OddJob.getInstance().getPlayerManager().isInCombat(movingPlayer.getUniqueId())) {
            // Player is in combat
            OddJob.getInstance().getMessageManager().console("aborted combat");
            test = false;
        } else if (OddJob.getInstance().getArenaManager().isInArena(movingPlayer.getUniqueId())) {
            // Player is in Arena
            OddJob.getInstance().getMessageManager().console("aborted arena");
            test = false;
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
                OddJob.getInstance().getMessageManager().success("Teleporting now!", movingUUID, true);
                movingPlayer.teleport(destination, cause);
                return true;
            } else {
                if (teleportTimer.containsKey(movingUUID)) {
                    teleportTimer.get(movingUUID).cancel();
                    teleportTimer.remove(movingUUID);
                    OddJob.getInstance().getMessageManager().warning("Changing teleport location.", movingUUID, false);
                }
                teleportTimer.put(movingPlayer.getUniqueId(), new BukkitRunnable() {
                    int i = 10;

                    @Override
                    public void run() {
                        if (!movingPlayer.isOnline()) cancel();
                        if (OddJob.getInstance().getPlayerManager().isInCombat(movingPlayer.getUniqueId())) {
                            OddJob.getInstance().getMessageManager().danger("Interrupted, in combat!", movingUUID, true);
                            cancel();
                            return;
                        }
                        if (i > 0) {
                            OddJob.getInstance().getMessageManager().info("Teleporting in " + ChatColor.WHITE + i, movingUUID, false);
                        } else {
                            teleportTimer.remove(movingUUID);
                            OddJob.getInstance().getMessageManager().success("Teleporting now!", movingUUID, true);
                            movingPlayer.teleport(destination, cause);

                            cancel();
                        }
                        i--;
                    }
                }.runTaskTimer(OddJob.getInstance(), 20, 20));
            }
        }
        return test;
    }

    public void deny(UUID to) {
        if (teleportAccept.containsValue(to)) {
            for (UUID from : teleportAccept.keySet()) {
                if (teleportAccept.get(from).equals(to)) {
                    // player (sends request) // target (teleport to)
                    Player player = OddJob.getInstance().getPlayerManager().getPlayer(teleportAccept.get(from));
                    Player target = OddJob.getInstance().getPlayerManager().getPlayer(from);
                    OddJob.getInstance().getMessageManager().danger("Your request has been denied by " + ChatColor.DARK_AQUA + player.getName(), target.getUniqueId(), false);
                    OddJob.getInstance().getMessageManager().danger("You have denied the request from " + ChatColor.DARK_AQUA + target.getName(), player.getUniqueId(), true);
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
                        OddJob.getInstance().getMessageManager().danger("The teleport request has timed out", player.getUniqueId(), false);
                    }
                    player = Bukkit.getOfflinePlayer(destinationUUID);
                    if (player.isOnline()) {
                        OddJob.getInstance().getMessageManager().danger("The teleport request has timed out", player.getUniqueId(), false);
                    }
                }
            }
        };
        task.runTaskLater(OddJob.getInstance(), 1200L);
        reset.put(movingUUID, task);
    }

    private void removeRequest(UUID from) {
        teleportAccept.remove(from);
        reset.remove(from);
    }

    // back <player>
    public void back(Player player) {
        teleport(player, OddJob.getInstance().getMySQLManager().getBack(player.getUniqueId()), cost_back, PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    // tp <player> <player>
    public void teleport(CommandSender commandSender, String movingName, String destinationName) {
        Player movingPlayer = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(movingName));
        Player destinationPlayer = null;
        if (movingPlayer == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(movingName, commandSender);
            return;
        }
        if (!destinationName.equals("")) {
            destinationPlayer = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(destinationName));
            if (destinationPlayer == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(destinationName, commandSender);
                return;
            }
        } else if (commandSender instanceof Player) {
            destinationPlayer = ((Player) commandSender);
        }

        if (destinationPlayer != null) {
            OddJob.getInstance().getTeleportManager().teleport(movingPlayer, destinationPlayer.getLocation(), 0, PlayerTeleportEvent.TeleportCause.COMMAND);
        } else {
            OddJob.getInstance().getMessageManager().danger("Something went wrong when trying to teleport.", commandSender, false);
        }

    }

    public void teleport(CommandSender commandSender, String movingName, String locationX, String locationY, String locationZ, World world) {
        int x = 0, y = 0, z = 0;
        Player movingPlayer = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(movingName));
        if (movingPlayer == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(movingName, commandSender);
            return;
        }
        try {
            x = Integer.parseInt(locationX);
            y = Integer.parseInt(locationY);
            z = Integer.parseInt(locationZ);
        } catch (Exception e) {
            OddJob.getInstance().getMessageManager().warning("Invalid x, y or z", commandSender, false);
        }

        Location destination = new Location(world, x, y, z);
        teleport(movingPlayer, destination, cost_player_to_location, PlayerTeleportEvent.TeleportCause.COMMAND);
        OddJob.getInstance().getMessageManager().success("You have been teleported to a specific location", movingPlayer.getUniqueId(), true);
    }

    public void teleport(CommandSender commandSender, String destinationName) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player movingPlayer = (Player) commandSender;
        Player destinationPlayer = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(destinationName));
        if (destinationPlayer == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(destinationName, commandSender);
            return;
        }
        teleport(movingPlayer, destinationPlayer.getLocation(), cost_player_to_player, PlayerTeleportEvent.TeleportCause.COMMAND);
        OddJob.getInstance().getMessageManager().success("You have been teleported to " + destinationPlayer.getName(), movingPlayer.getUniqueId(), true);
    }

    public void spawn(Player player, Location location) {
        teleport(player, location, cost_spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
        OddJob.getInstance().getMessageManager().success("You have been teleported to spawn", player.getUniqueId(), true);
    }

    public void jail(Player player, Location lobby) {
        player.teleport(lobby, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
}
