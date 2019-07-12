package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager {
    private HashMap<UUID, UUID> teleportAccept = new HashMap<>();

    public void tpa(UUID player, UUID target) {
        teleportAccept.put(player, target);
    }
    // player (sends request) // target (teleport to)

    public void accept(UUID uuid) {
        if (teleportAccept.containsValue(uuid)) {
            for (UUID u : teleportAccept.keySet()) {
                if (teleportAccept.get(u).equals(uuid)) {
                    // player (sends request) // target (teleport to)
                    Player player = OddJob.getInstance().getPlayerManager().getPlayer(teleportAccept.get(u));
                    Player target = OddJob.getInstance().getPlayerManager().getPlayer(u);
                    OddJob.getInstance().getMessageManager().success("Your request has been accepted by " + player.getName(), target.getUniqueId());
                    OddJob.getInstance().getMessageManager().success("You have accepted the request from " + target.getName(), player.getUniqueId());
                    target.teleport(player, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    teleportAccept.remove(uuid);
                }
            }
        }
    }

    public void deny(UUID uuid) {
        if (teleportAccept.containsValue(uuid)) {
            for (UUID u : teleportAccept.keySet()) {
                if (teleportAccept.get(u).equals(uuid)) {
                    // player (sends request) // target (teleport to)
                    Player player = OddJob.getInstance().getPlayerManager().getPlayer(teleportAccept.get(u));
                    Player target = OddJob.getInstance().getPlayerManager().getPlayer(u);
                    OddJob.getInstance().getMessageManager().danger("Your request has been denied by " + player.getName(), target.getUniqueId());
                    OddJob.getInstance().getMessageManager().danger("You have denied the request from " + target.getName(), player.getUniqueId());
                    teleportAccept.remove(uuid);
                }
            }
        }
    }
}
