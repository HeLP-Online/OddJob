package no.helponline.Events;

import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import no.helponline.OddJob;
import no.helponline.Utils.Zone;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public class PlayerJoin implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Update the list of World
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
            if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(uuid) != null)
                OddJob.getInstance().getScoreManager().guild(player);

            // Economy
            if (!OddJob.getInstance().getEconManager().hasAccount(uuid)) {
                OddJob.getInstance().getEconManager().createAccount(uuid, 200.0D, false);
                OddJob.getInstance().log("Initializing account for " + player.getName());
            }

            // Welcome message
            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"§aWelcome to HeLP\"}"), 40, 20, 20);
            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(title);

            // Remove locking items if the player has any
            OddJob.getInstance().getLockManager().remove(uuid);

            // Player is in a guild
            player.sendMessage("Hi " + player.getName() + ". We are using our own plugin named OddJob to manage 'homes', 'guild' and 'warp'.\nYou may find more information at our Facebook group: https://www.facebook.com/groups/help.online.minecraft/");
            UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());
            if (guild != null && !guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
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




}
