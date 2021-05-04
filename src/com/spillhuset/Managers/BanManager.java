package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanManager {

    public void ban(UUID playerUUID, String text) {
        OddJob.getInstance().getPlayerManager().getOddPlayer(playerUUID).setBanned(text);
        Player player = OddJob.getInstance().getPlayerManager().getPlayer(playerUUID);
        if (player != null && player.isOnline()) kick(player, text);
    }

    public void unban(UUID player) {
        OddJob.getInstance().getPlayerManager().getOddPlayer(player).setBanned(null);
    }

    public List<UUID> getBans() {
        List<UUID> bans = new ArrayList<>();
        for (UUID uuid : OddJob.getInstance().getPlayerManager().getUUIDs()) {
            if (getBan(uuid) != null) {
                bans.add(uuid);
            }
        }
        return bans;
    }

    public String getBan(UUID uuid) {
        return OddJob.getInstance().getPlayerManager().getOddPlayer(uuid).getBanned();
    }

    public void kick(Player player) {
        player.kickPlayer(getBan(player.getUniqueId()));
    }

    public void kick(Player player, String text) {
        player.kickPlayer(text);
    }
}
