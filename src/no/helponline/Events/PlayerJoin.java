package no.helponline.Events;

import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.UUID;

public class PlayerJoin implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        OddJob.getInstance().getMySQLManager().updateWorlds(event.getPlayer().getWorld());

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Making an OddPlayer
        OddJob.getInstance().getPlayerManager().updatePlayer(uuid, player.getName());

        // If banned Player
        if (OddJob.getInstance().getBanManager().getBan(uuid) != null) {
            OddJob.getInstance().getBanManager().kick(player);
        } else {

            // Scoreboad
            OddJob.getInstance().getScoreManager().guild(player);

            if (OddJob.getInstance().getEconManager().hasAccount(uuid)) {
                OddJob.getInstance().getEconManager().setBalance(player.getUniqueId(), 200.0D, false);
                //player.sendMessage("Your first balance is initialized!");
                OddJob.getInstance().log("Initializing account for " + player.getName());
            }

            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"§aWelcome to HeLP\"}"), 40, 20, 20);
            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(title);

            OddJob.getInstance().getLockManager().remove(uuid);

            // has a guild
            player.sendMessage("Hi " + player.getName() + ". We are using our own plugin named OddJob to manage 'homes', 'guild' and 'warp'.\nYou may find more information at our Facebook group: https://www.facebook.com/groups/help.online.minecraft/");
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
            if (guild != null) {
                player.sendMessage("You are a loyal member of " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild));
                List<UUID> pending = OddJob.getInstance().getGuildManager().getGuildPendings(guild);
                if (pending.size() > 0) {
                    player.sendMessage("Your guild has " + pending.size() + " request to join your guild. Use the command '/guild accept' to see the list of players");
                }
            } else {
                player.sendMessage("We are sorry to announce that you are not associated with any guild yet.");
                player.sendMessage("To find more information about how to create a guild, or join an existing guild, you may use the '/guild' command");
                guild = OddJob.getInstance().getGuildManager().getGuildInvitation(player.getUniqueId());
                if (guild != null) {
                    player.sendMessage("You have been invited to join " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild) + " to accept this invitation with '/guild accept' or deny it with '/guild deny'");
                } else {
                    guild = OddJob.getInstance().getGuildManager().getGuildPending(player.getUniqueId());
                    if (guild != null) {
                        player.sendMessage("Your request to join " + OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild) + " has not been answered yet, please be patience.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void init(PlayerChangedWorldEvent event) {
        OddJob.getInstance().getMySQLManager().updateWorlds(event.getPlayer().getWorld());
    }
}
