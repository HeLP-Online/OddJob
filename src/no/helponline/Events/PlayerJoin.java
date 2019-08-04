package no.helponline.Events;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
import no.helponline.OddJob;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class PlayerJoin implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Making an OddPlayer
        OddJob.getInstance().getPlayerManager().updatePlayer(uuid, player.getName());
        if (OddJob.getInstance().getBanManager().getBan(uuid) != null) {
            OddJob.getInstance().getBanManager().kick(player);
        } else {

            if (OddJob.getInstance().getEconManager().hasAccount(uuid)) {
                OddJob.getInstance().getEconManager().setBalance(player.getUniqueId(), 200.0D);
                //player.sendMessage("Your first balance is initialized!");
                OddJob.getInstance().log("Initializing account for " + player.getName());
            }

            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"§aWelcome to HeLP\"}"), 40, 20, 20);
            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(title);

            OddJob.getInstance().getLockManager().remove(uuid);

            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
            if (guild != null) {
                Team team = OddJob.getInstance().getGuildManager().getTeam(guild);
                if (team == null) {
                    team = OddJob.getInstance().getGuildManager().addTeam(guild, player.getUniqueId());
                } else {
                    team = OddJob.getInstance().getGuildManager().addTeamMember(guild, player.getUniqueId());
                }
                player.setScoreboard(OddJob.getInstance().getGuildManager().getScoreboard());
                //OddJob.getInstance().log("Teams: " + OddJob.getInstance().getGuildManager().getScoreboard().getTeams().size());
            }
        }
    }
}
