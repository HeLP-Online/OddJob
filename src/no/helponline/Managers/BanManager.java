package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BanManager {
    private static HashMap<UUID, String> bans = new HashMap<>();

    public static void ban(UUID uniqueId, String text) {
        Player player = OddJob.getInstance().getPlayerManager().getPlayer(uniqueId);
        if (player.isOnline()) {
            player.kickPlayer(text);
        }
        bans.put(uniqueId, text);
    }

    public static void unban(UUID uniqueId) {
        bans.remove(uniqueId);
    }
}
