package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager {
    private final HashMap<UUID, UUID> teleportAccept = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> reset = new HashMap<>();
    private final HashMap<UUID, BukkitTask> teleportTimer = new HashMap<>();

    public boolean tpa(UUID from, UUID to) {
        if (!OddJob.getInstance().getPlayerManager().request(to, from)) {
            return false;
        }
        if (hasRequest(from)) {
            OddJob.getInstance().getMessageManager().warning("Rewriting existing TPA request to " + ChatColor.DARK_AQUA + OddJob.getInstance().getPlayerManager().getName(teleportAccept.get(from)), from, false);
        }
        teleportAccept.put(from, to);
        startTimer(from, to);
        return true;
    }
    // player (sends request) // target (teleport to)

    public boolean hasRequest(UUID from) {
        return teleportAccept.containsKey(from);
    }

    // TODO Tpa cost?
    public void accept(UUID to) {
        if (teleportAccept.containsValue(to)) {
            for (UUID from : teleportAccept.keySet()) {
                if (teleportAccept.get(from).equals(to)) {
                    // player (sends request) // target (teleport to)
                    Player target = OddJob.getInstance().getPlayerManager().getPlayer(from);
                    Player destination = OddJob.getInstance().getPlayerManager().getPlayer(teleportAccept.get(from));
                    OddJob.getInstance().getMessageManager().success("Your request has been accepted by " + ChatColor.DARK_AQUA + destination.getName(), target.getUniqueId(), false);
                    OddJob.getInstance().getMessageManager().success("You have accepted the request from " + ChatColor.DARK_AQUA + target.getName(), destination.getUniqueId(), true);
                    remove(from);
                    teleport(target, destination, 0, PlayerTeleportEvent.TeleportCause.COMMAND);
                    if (reset.containsKey(from)) reset.get(from).cancel();
                }
            }
        }
    }

    public void teleport(Player target, Player destination, double cost, PlayerTeleportEvent.TeleportCause cause) {
        boolean test = true;
        if (OddJob.getInstance().getPlayerManager().isInCombat(target.getUniqueId())) test = false;
        else if (OddJob.getInstance().getArenaManager().isInArena(target.getUniqueId())) test = false;
        else if (cost > 0) {
            if (cost > OddJob.getInstance().getEconManager().getBalance(target.getUniqueId())) {
                OddJob.getInstance().getEconManager().subtract(target.getUniqueId(), cost, false);
            } else {
                test = false;
                target.sendMessage("Sorry, but you can't afford the request");
            }
        }
        if (test) {
            OddJob.getInstance().getMySQLManager().updateTeleport(target);
            UUID t = target.getUniqueId();
            teleportTimer.put(target.getUniqueId(), new BukkitRunnable() {
                int i = 10;

                @Override
                public void run() {
                    if (OddJob.getInstance().getPlayerManager().isInCombat(target.getUniqueId())) {
                        if (target.isOnline())
                            OddJob.getInstance().getMessageManager().danger("Interrupted, in combat!", t, true);
                        cancel();
                    }
                    if (i > 0) {
                        if (target.isOnline())
                            OddJob.getInstance().getMessageManager().info("Teleporting in " + ChatColor.WHITE + i, t, false);
                    } else {
                        teleportTimer.remove(target.getUniqueId());
                        if (target.isOnline()) {
                            OddJob.getInstance().getMessageManager().success("Teleporting!", t, true);
                            target.teleport(destination, cause);
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
        if (OddJob.getInstance().getPlayerManager().isInCombat(target.getUniqueId())) test = false;
        else if (OddJob.getInstance().getArenaManager().isInArena(target.getUniqueId())) test = false;
        else if (cost > 0) {
            if (cost > OddJob.getInstance().getEconManager().getBalance(target.getUniqueId())) {
                OddJob.getInstance().getEconManager().subtract(target.getUniqueId(), cost, false);
            } else {
                test = false;
                target.sendMessage("Sorry, but you can't afford the request");
            }
        }
        if (test) {
            OddJob.getInstance().getMySQLManager().updateTeleport(target);
            UUID t = target.getUniqueId();
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
                if (hasRequest(from)) {
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
    }

    public void back(Player player) {
        teleport(player, OddJob.getInstance().getMySQLManager().getBack(player.getUniqueId()), 0, PlayerTeleportEvent.TeleportCause.COMMAND);
    }
}
