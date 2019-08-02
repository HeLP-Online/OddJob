package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class BanManager {

    public void ban(Player player, String text) {
        if (player.isOnline()) {
            player.kickPlayer(text);
            OddJob.getInstance().getMySQLManager().addPlayerBan(player.getUniqueId(), text);
        }
    }

    public void unban(UUID player) {
        OddJob.getInstance().getMySQLManager().deletePlayerBan(player);
    }

    public List<UUID> getBans() {
        return OddJob.getInstance().getMySQLManager().getBans();
    }

    public String getBan(UUID uuid) {
        return OddJob.getInstance().getMySQLManager().getBan(uuid);
    }

    public void kick(Player player) {
        player.kickPlayer(getBan(player.getUniqueId()));
    }
}
