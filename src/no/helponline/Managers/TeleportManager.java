package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager {
    private HashMap<UUID, UUID> teleportAccept = new HashMap<>();
    private HashMap<UUID, BukkitRunnable> reset = new HashMap<>();
    private HashMap<UUID, BukkitTask> teleportTimer = new HashMap<>();

    public boolean tpa(UUID from, UUID to) {
        if (!OddJob.getInstance().getPlayerManager().request(to, from)) { // false
            OddJob.getInstance().log("deny request");
            return false;
        }
        OddJob.getInstance().log("is oddplayer");
        if (hasRequest(from)) {
            OddJob.getInstance().log("exists");
            OddJob.getInstance().getMessageManager().warning("Rewriting existing TPA request to " + OddJob.getInstance().getPlayerManager().getName(teleportAccept.get(from)), from);
        }
        OddJob.getInstance().log("Making teleport request & task later");
        teleportAccept.put(from, to);
        startTimer(from, to);
        return true;
    }
    // player (sends request) // target (teleport to)

    public boolean hasRequest(UUID from) {
        OddJob.getInstance().log("contains: " + teleportAccept.containsKey(from));
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
                    OddJob.getInstance().getMessageManager().success("Your request has been accepted by " + destination.getName(), target.getUniqueId());
                    OddJob.getInstance().getMessageManager().success("You have accepted the request from " + target.getName(), destination.getUniqueId());
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
                OddJob.getInstance().getEconManager().subtract(target.getUniqueId(), cost);
            } else {
                test = false;
                target.sendMessage("Sorry, but you can't afford the request");
            }
        }
        if (test) {
            OddJob.getInstance().getMySQLManager().updateTeleport(target);
            teleportTimer.put(target.getUniqueId(), new BukkitRunnable() {
                int i = 10;

                @Override
                public void run() {
                    if (OddJob.getInstance().getPlayerManager().isInCombat(target.getUniqueId())) {
                        target.sendMessage("Interrupted, in combat");
                        cancel();
                    }
                    if (i > 0) {
                        target.sendMessage("Teleporting in " + i);
                    } else {
                        teleportTimer.remove(target.getUniqueId());
                        target.teleport(destination, cause);
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
                OddJob.getInstance().getEconManager().subtract(target.getUniqueId(), cost);
            } else {
                test = false;
                target.sendMessage("Sorry, but you can't afford the request");
            }
        }
        if (test) {
            OddJob.getInstance().getMySQLManager().updateTeleport(target);
            teleportTimer.put(target.getUniqueId(), new BukkitRunnable() {
                int i = 10;

                @Override
                public void run() {
                    if (OddJob.getInstance().getPlayerManager().isInCombat(target.getUniqueId())) {
                        target.sendMessage("Interrupted, in combat");
                        cancel();
                    }
                    if (i > 0) {
                        target.sendMessage("Teleporting in " + i);
                    } else {
                        teleportTimer.remove(target.getUniqueId());
                        target.teleport(destination, cause);
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
                    OddJob.getInstance().getMessageManager().danger("Your request has been denied by " + player.getName(), target.getUniqueId());
                    OddJob.getInstance().getMessageManager().danger("You have denied the request from " + target.getName(), player.getUniqueId());
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
                        OddJob.getInstance().getMessageManager().sendMessage(player.getUniqueId(), "The teleport request has timed out");
                    }
                    player = Bukkit.getOfflinePlayer(to);
                    if (player.isOnline()) {
                        OddJob.getInstance().getMessageManager().sendMessage(player.getUniqueId(), "The teleport request has timed out");
                    }
                }
            }
        };
        task.runTaskLater(OddJob.getInstance(), 1200L);
        reset.put(from, task);
    }

    private void remove(UUID from) {
        OddJob.getInstance().log("Removing teleport request");
        teleportAccept.remove(from);
    }

    public void back(Player player) {
        teleport(player, OddJob.getInstance().getMySQLManager().getBack(player.getUniqueId()), 0, PlayerTeleportEvent.TeleportCause.COMMAND);
    }
}
