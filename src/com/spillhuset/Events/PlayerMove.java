package com.spillhuset.Events;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
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
        if (movingFromChunk.getX() == movingToChunk.getX() && movingFromChunk.getZ() == movingToChunk.getZ()) {
            // Player is within the same chunk
            return;
        }
        OddJob.getInstance().log("Chunk changed");

        // Who owns the chunk the Player is going to?
        movingToGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(movingToChunk);
        if (movingToGuild == null) movingToGuild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD);
        OddJob.getInstance().log("Moving to "+OddJob.getInstance().getGuildManager().getGuildNameByUUID(movingToGuild));
        //OddJob.getInstance().getMessageManager().console("x="+movingToChunk.getX()+" z="+movingToChunk.getZ()+" world="+movingToChunk.getWorld().getName());

        // Who owns the chunk the Player is going from?
        movingFromGuild = OddJob.getInstance().getPlayerManager().in.getOrDefault(player.getUniqueId(), movingToGuild);

        // Is it wild?
        Zone zoneTo = OddJob.getInstance().getGuildManager().getZoneByGuild(movingToGuild);
        OddJob.getInstance().log("Zone is "+zoneTo.name());

        if (movingFromGuild != movingToGuild || movingFromGuild == null) {
            OddJob.getInstance().getPlayerManager().in.put(player.getUniqueId(), movingToGuild);
            OddJob.getInstance().log("Changed Guild");
        } else {
            OddJob.getInstance().log("Not Changing Zone");
            // Zone is the same
            return;
        }

        // Auto claiming
        if ((movingToGuild != null) && (movingFromGuild != null)) {
            OddJob.getInstance().log("Moving to "+OddJob.getInstance().getGuildManager().getGuildNameByUUID(movingToGuild)+"; Moving from "+OddJob.getInstance().getGuildManager().getGuildNameByUUID(movingFromGuild));
            // Moving from not null
                if (movingToGuild == OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD)) {
                    OddJob.getInstance().log("Moving to the Wild, Claimable");
                    // Moving to is WILD
                    if (OddJob.getInstance().getGuildManager().hasAutoClaim(player.getUniqueId())) {
                        OddJob.getInstance().log("Autoclaim is activated");
                        // Auto Claim is on
                        OddJob.getInstance().getGuildManager().autoClaim(player, movingToChunk);
                    }
            }
        }
        OddJob.getInstance().log("move 8");
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
        UUID uuid = OddJob.getInstance().getPlayerManager().in.get(player.getUniqueId());
        OddJob.getInstance().log(OddJob.getInstance().getGuildManager().getZoneByGuild(movingToGuild).name());
        if (print) {
            // Printing to Actionbar
            StringBuilder s = new StringBuilder();
            switch (OddJob.getInstance().getGuildManager().getZoneByGuild(movingToGuild)) {
                case GUILD -> s.append(ChatColor.DARK_BLUE).append(OddJob.getInstance().getGuildManager().getGuildNameByUUID(movingToGuild)).append(" hails you!");
                case ARENA, WAR -> s.append(ChatColor.RED).append("Draw your weapon!");
                case JAIL -> s.append(ChatColor.GOLD).append("Serve your time!");
                case SAFE -> s.append(ChatColor.GREEN).append("Take a break and prepare!");
                default -> s.append(ChatColor.YELLOW).append("Welcome to the wild!");
            }
            /*
            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + s + "\"}"), 40, 20, 20);
            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(title);*/
        }
    }
}
