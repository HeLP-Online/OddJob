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

    public boolean tpa(UUID moving, UUID destination) {
        if (!OddJob.getInstance().getPlayerManager().request(moving, destination)) return false;
        if (has(moving))
            OddJob.getInstance().getMessageManager().warning("Rewriting existing TPA request to " + ChatColor.DARK_AQUA + OddJob.getInstance().getPlayerManager().getName(teleportAccept.get(destination)), moving, false);

        teleportAccept.put(moving, destination);
        startTimer(moving, destination);
        return true;
    }
    // player (sends request) // target (teleport to)

    public boolean has(UUID from) {
        return teleportAccept.containsKey(from);
    }

    // TODO Tpa cost?
    public void accept(UUID request) {
        if (has(request)) {
            for (UUID moving : teleportAccept.keySet()) {
                if (teleportAccept.get(moving).equals(request)) {
                    // player (sends request) // target (teleport to)
                    Player target = OddJob.getInstance().getPlayerManager().getPlayer(moving);
                    Player destination = OddJob.getInstance().getPlayerManager().getPlayer(teleportAccept.get(moving));
                    OddJob.getInstance().getMessageManager().success("Your request has been accepted by " + ChatColor.DARK_AQUA + destination.getName(), target.getUniqueId(), false);
                    OddJob.getInstance().getMessageManager().success("You have accepted the request from " + ChatColor.DARK_AQUA + target.getName(), destination.getUniqueId(), true);
                    remove(moving);
                    teleport(target, destination, PlayerTeleportEvent.TeleportCause.COMMAND);
                    if (reset.containsKey(moving)) reset.get(moving).cancel();
                }
            }
        }
    }

    public void cancel(UUID uuid) {
        if (teleportTimer.containsKey(uuid)) {
            BukkitTask task = teleportTimer.get(uuid);
            task.cancel();
            teleportTimer.remove(uuid);
        }
    }

    public void teleport(Player moving, Player destination, PlayerTeleportEvent.TeleportCause cause) {
        boolean test = true;
        if (OddJob.getInstance().getPlayerManager().isInCombat(moving.getUniqueId())) {
            test = false;
        } else if (OddJob.getInstance().getArenaManager().isInArena(moving.getUniqueId())) {
            test = false;
        } else if (OddJob.getInstance().getJailManager().in(moving.getUniqueId()) != null) {
            test = false;
        } else if (OddJob.getInstance().getFreezeManager().get(moving.getUniqueId()) != null) {
            test = false;
        }

        if (test) {
            OddJob.getInstance().getMySQLManager().updateTeleport(moving);
            if (teleportTimer.containsKey(moving.getUniqueId())) {
                teleportTimer.get(moving.getUniqueId()).cancel();
                teleportTimer.remove(moving.getUniqueId());
                OddJob.getInstance().getMessageManager().warning("Changing teleport location.",moving,false);
            }
            teleportTimer.put(moving.getUniqueId(), new BukkitRunnable() {
                int i = 10;

                @Override
                public void run() {
                    if (OddJob.getInstance().getPlayerManager().isInCombat(moving.getUniqueId())) {
                        if (moving.isOnline())
                            OddJob.getInstance().getMessageManager().danger("Interrupted, in combat!", moving, true);
                        cancel();
                    }
                    if (i > 0) {
                        if (moving.isOnline())
                            OddJob.getInstance().getMessageManager().info("Teleporting in " + ChatColor.WHITE + i, moving, false);
                    } else {
                        teleportTimer.remove(moving.getUniqueId());
                        if (moving.isOnline()) {
                            OddJob.getInstance().getMessageManager().success("Teleporting!", moving, true);
                            moving.teleport(destination, cause);
                        }
                        cancel();
                    }
                    i--;
                }
            }.runTaskTimer(OddJob.getInstance(), 20, 20));
        }
    }

    public boolean teleport(Player target, Location destination, double cost, PlayerTeleportEvent.TeleportCause cause) {
        boolean test = true;
        if (OddJob.getInstance().getPlayerManager().isInCombat(target.getUniqueId())) {
            test = false;
        } else if (OddJob.getInstance().getArenaManager().isInArena(target.getUniqueId())) {
            test = false;
        } else if (OddJob.getInstance().getJailManager().in(target.getUniqueId()) != null) {
            test = false;
        } else if (OddJob.getInstance().getFreezeManager().get(target.getUniqueId()) != null) {
            test = false;
        }

        if (test) {
            OddJob.getInstance().getMySQLManager().updateTeleport(target);
            UUID t = target.getUniqueId();
            if (target.hasPermission("oddjob.teleport.now")) {
                OddJob.getInstance().getMessageManager().success("Teleporting now!", t, true);
                target.teleport(destination, cause);
            } else {
                if (teleportTimer.containsKey(t)) {
                    teleportTimer.get(t).cancel();
                    teleportTimer.remove(t);
                    OddJob.getInstance().getMessageManager().warning("Changing teleport location.",t,false);
                }
                teleportTimer.put(target.getUniqueId(), new BukkitRunnable() {
                    int i = 10;

                    @Override
                    public void run() {
                        if (OddJob.getInstance().getPlayerManager().isInCombat(target.getUniqueId())) {
                            if (target.isOnline())
                                OddJob.getInstance().getMessageManager().danger("Interrupted, in combat!", t, true);
                            cancel();
                            return;
                        }
                        if (i > 0) {
                            if (target.isOnline())
                                OddJob.getInstance().getMessageManager().info("Teleporting in " + ChatColor.WHITE + i, t, false);
                        } else {
                            teleportTimer.remove(t);

                            if (target.isOnline()) {
                                OddJob.getInstance().getMessageManager().success("Teleporting now!", t, true);
                                target.teleport(destination, cause);
                            }
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
                    remove(from);
                    if (reset.containsKey(from)) reset.get(from).cancel();
                }
            }
        }
    }

    private void startTimer(UUID from, UUID to) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (has(from)) {
                    remove(from);
                    reset.remove(from);
                    OfflinePlayer player = Bukkit.getOfflinePlayer(from);
                    if (player.isOnline()) {
                        OddJob.getInstance().getMessageManager().danger("The teleport request has timed out", player.getUniqueId(), false);
                    }
                    player = Bukkit.getOfflinePlayer(to);
                    if (player.isOnline()) {
                        OddJob.getInstance().getMessageManager().danger("The teleport request has timed out", player.getUniqueId(), false);
                    }
                }
            }
        };
        task.runTaskLater(OddJob.getInstance(), 1200L);
        reset.put(from, task);
    }

    private void remove(UUID from) {
        teleportAccept.remove(from);
        reset.remove(from);
    }

    // back <player>
    public void back(Player player) {
        teleport(player, OddJob.getInstance().getMySQLManager().getBack(player.getUniqueId()), cost_back, PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    // tp <player> <player>
    public void teleport(CommandSender commandSender, String to, String from) {
        Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(to));
        Player destination = null;
        if (target == null) {
            OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + ChatColor.GRAY + to, commandSender, false);
            return;
        }
        if (from != null) {
            destination = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(from));
            if (destination == null) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + ChatColor.GRAY + from, commandSender, false);
                return;
            }
        } else if (commandSender instanceof Player) {
            destination = ((Player) commandSender);
        }

        if (destination != null) {
            OddJob.getInstance().getTeleportManager().teleport(target, destination.getLocation(), 0, PlayerTeleportEvent.TeleportCause.COMMAND);
        } else {
            OddJob.getInstance().getMessageManager().danger("Something went wrong when trying to teleport.", commandSender, false);
        }

    }

    public void teleport(CommandSender commandSender, String player_x, String xx, String yy, String zz, World world) {
        int x = 0, y = 0, z = 0;
        Player player = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(player_x));
        if (player == null) {
            OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + ChatColor.GRAY + player_x, commandSender, false);
            return;
        }
        try {
            x = Integer.parseInt(xx);
            y = Integer.parseInt(yy);
            z = Integer.parseInt(zz);
        } catch (Exception e) {
            OddJob.getInstance().getMessageManager().warning("Invalid x, y or z", commandSender, false);
        }

        Location location = new Location(world, x, y, z);
        teleport(player, location, cost_player_to_location, PlayerTeleportEvent.TeleportCause.COMMAND);
        OddJob.getInstance().getMessageManager().success("You have been teleported to a specific location", player.getUniqueId(), true);
    }

    public void teleport(CommandSender commandSender, String player_x) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player target = (Player) commandSender;
        Player destination = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(player_x));
        if (destination == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(player_x, commandSender);
            return;
        }
        teleport(target, destination.getLocation(), cost_player_to_player, PlayerTeleportEvent.TeleportCause.COMMAND);
        OddJob.getInstance().getMessageManager().success("You have been teleported to " + destination.getName(), target.getUniqueId(), true);
    }
    public void spawn(Player player,Location location) {
        teleport(player,location,cost_spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
        OddJob.getInstance().getMessageManager().success("You have been teleported to spawn", player.getUniqueId(), true);
    }

    public void jail(Player player, Location lobby) {
        player.teleport(lobby, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
}
