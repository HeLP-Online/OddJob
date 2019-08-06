package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager {
    private HashMap<UUID, UUID> teleportAccept = new HashMap<>();
    private HashMap<UUID, BukkitRunnable> reset = new HashMap<>();

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

    public void accept(UUID to) {
        if (teleportAccept.containsValue(to)) {
            for (UUID from : teleportAccept.keySet()) {
                if (teleportAccept.get(from).equals(to)) {
                    // player (sends request) // target (teleport to)
                    Player player = OddJob.getInstance().getPlayerManager().getPlayer(teleportAccept.get(from));
                    Player target = OddJob.getInstance().getPlayerManager().getPlayer(from);
                    OddJob.getInstance().getMessageManager().success("Your request has been accepted by " + player.getName(), target.getUniqueId());
                    OddJob.getInstance().getMessageManager().success("You have accepted the request from " + target.getName(), player.getUniqueId());
                    teleport(target, player);
                    remove(from);
                    if (reset.containsKey(from)) reset.get(from).cancel();
                }
            }
        }
    }

    public void teleport(Player target, Player destination) {
        OddJob.getInstance().getMySQLManager().updateTeleport(target);
        target.teleport(destination, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void teleport(Player player, Location location) {
        OddJob.getInstance().getMySQLManager().updateTeleport(player);
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
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

    public void startTimer(UUID from, UUID to) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                OddJob.getInstance().log("Running task later");
                if (hasRequest(from)) {
                    remove(from);
                    reset.remove(from);
                    Player player = Bukkit.getPlayer(from);
                    if (player.isOnline()) {
                        OddJob.getInstance().getMessageManager().sendMessage(player, "The teleport request has timed out");
                    }
                    player = Bukkit.getPlayer(to);
                    if (player.isOnline()) {
                        OddJob.getInstance().getMessageManager().sendMessage(player, "The teleport request has timed out");
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
}
