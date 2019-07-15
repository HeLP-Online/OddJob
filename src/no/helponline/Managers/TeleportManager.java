package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager {
    private HashMap<UUID, UUID> teleportAccept = new HashMap<>();
    private HashMap<UUID, BukkitRunnable> reset = new HashMap<>();

    public void tpa(UUID from, UUID to) {
        teleportAccept.put(from, to);
    }
    // player (sends request) // target (teleport to)

    public boolean hasRequest(UUID uuid) {
        return teleportAccept.containsKey(uuid);
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
                    target.teleport(player, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    teleportAccept.remove(from, to);
                    if (reset.containsKey(from)) reset.get(from).cancel();
                }
            }
        }
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
                    teleportAccept.remove(from, to);
                    if (reset.containsKey(from)) reset.get(from).cancel();
                }
            }
        }
    }

    public void startTimer(UUID uniqueId) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (OddJob.getInstance().getTeleportManager().hasRequest(uniqueId))
                    OddJob.getInstance().getTeleportManager().cancel(uniqueId);
            }
        };
        task.runTaskLater(OddJob.getInstance(), 300000L);
        reset.put(uniqueId, task);
    }

    private void cancel(UUID uniqueId) {
        teleportAccept.remove(uniqueId);
    }
}
