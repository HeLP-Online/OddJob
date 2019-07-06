package no.helponline.Managers;

import no.helponline.OddJob;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager {
    private HashMap<UUID, UUID> teleportAccept = new HashMap<>();

    public void tpa(UUID player, UUID target) {
        teleportAccept.put(target, player);
    }

    public void accept(UUID uuid) {
        if (teleportAccept.containsKey(uuid)) {
            OddJob.getInstance().getPlayerManager().getPlayer(uuid).teleport(OddJob.getInstance().getPlayerManager().getPlayer(teleportAccept.get(uuid)).getLocation());
            teleportAccept.remove(uuid);
        }
    }

    public void deny(UUID uuid) {
        teleportAccept.remove(uuid);
    }
}
