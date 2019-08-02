package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class BanManager {

    public void ban(UUID player, String text) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        OddJob.getInstance().getMySQLManager().addPlayerBan(player, text);
        if (op.isOnline()) {
            kick(op.getPlayer());
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

    public void kick(Player player, String text) {
        player.kickPlayer(text);
    }
}
