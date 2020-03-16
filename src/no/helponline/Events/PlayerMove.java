package no.helponline.Events;

import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import no.helponline.OddJob;
import no.helponline.Utils.Enum.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMove implements Listener {
    @EventHandler
    public void freeze(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // If Player is frozen
        if (OddJob.getInstance().getFreezeManager().get(player.getUniqueId()) != null) {
            player.teleport(new Location(player.getWorld(), event.getFrom().getBlockX(), event.getFrom().getBlockY(), event.getFrom().getBlockZ()));
        }
    }


    @EventHandler
    public void onGuildMove(PlayerMoveEvent event) {
        Chunk movingToChunk = event.getTo().getChunk();
        Chunk movingFromChunk = event.getFrom().getChunk();
        Player player = event.getPlayer();
        UUID movingToGuild;
        UUID movingFromGuild;
        boolean print = false;

        // Have the player changed chunk?
        if (movingFromChunk.equals(movingToChunk)) {
            // Player is within the same guild
            return;
        }

        // Who owns the chunk the Player is going to?
        movingToGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(movingToChunk);

        // Who owns the chunk the Player is going from?
        movingFromGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(movingFromChunk);


        // Auto claiming
        if (!movingFromGuild.equals(movingToGuild) && movingToGuild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
            if (OddJob.getInstance().getGuildManager().hasAutoClaim(player.getUniqueId())) {
                OddJob.getInstance().getGuildManager().autoClaim(player, movingToChunk);
            }
        }

        // Prison break!
        if (OddJob.getInstance().getJailManager().in(player.getUniqueId()) != null) {
            if (movingToGuild != OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.JAIL)) {
                OddJob.getInstance().getJailManager().freeFromJail(player.getUniqueId(), null, true);
            }
        }

        // Actionbar
        if (!OddJob.getInstance().getPlayerManager().in.containsKey(player.getUniqueId())) {
            OddJob.getInstance().getPlayerManager().in.put(player.getUniqueId(), OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(movingToChunk));
            print = true;
        } else if (!OddJob.getInstance().getPlayerManager().in.get(player.getUniqueId()).equals(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(movingToChunk))) {
            OddJob.getInstance().getPlayerManager().in.put(player.getUniqueId(), OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(movingFromChunk));
            print = true;
        }

        if (print) {
            // Printing to Actionbar
            StringBuilder s = new StringBuilder();
            switch (OddJob.getInstance().getGuildManager().getZoneByGuild(movingToGuild)) {
                case GUILD:
                    s.append(ChatColor.DARK_BLUE).append(OddJob.getInstance().getGuildManager().getGuildNameByUUID(movingToGuild)).append(" hails you!");
                    break;
                case ARENA:
                case WAR:
                    s.append(ChatColor.RED).append("Draw your weapon!");
                    break;
                case JAIL:
                    s.append(ChatColor.GOLD).append("Serve your time!");
                    break;
                case SAFE:
                    s.append(ChatColor.GREEN).append("Take a break and prepare!");
                    break;
                default:
                    s.append(ChatColor.YELLOW).append("Welcome to the wild!");
                    break;

            }
            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + s.toString() + "\"}"), 40, 20, 20);
            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(title);
        }
    }
}
