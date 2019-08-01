package no.helponline.Managers;

import no.helponline.Guilds.Role;
import no.helponline.Guilds.Zone;
import no.helponline.OddJob;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GuildManager {
    private HashMap<UUID, UUID> autoClaim;// Player | Guild

    public GuildManager() {
        autoClaim = new HashMap<>();
    }

    public boolean create(UUID player, String name) {
        if (getGuildUUIDByMember(player) != null) {
            return false;
        }
        HashMap<String, Object> memberOfGuild = new HashMap<>();

        UUID guild = UUID.randomUUID();
        memberOfGuild.put("name", name);
        memberOfGuild.put("zone", Zone.GUILD.name());
        memberOfGuild.put("uuid", guild.toString());
        memberOfGuild.put("invited_only", false);
        memberOfGuild.put("friendly_fire", false);
        OddJob.getInstance().getMySQLManager().createGuild(memberOfGuild);
        addGuildMember(guild, player, Role.guildMaster);
        return true;

    }

    public void addGuildMember(UUID guild, UUID player, Role role) {
        OddJob.getInstance().getMySQLManager().addGuildMember(guild, player, role);
    }

    public List<UUID> getGuilds() {
        return OddJob.getInstance().getMySQLManager().getGuildUUIDs();
    }

    public void autoClaim(UUID player, Chunk chunk) {
        if (getGuildUUIDByChunk(chunk) == null) {
            // NOT CLAIMED
            UUID guild = autoClaim.get(player);

            OddJob.getInstance().getMySQLManager().addGuildChunks(guild, chunk);
            OddJob.getInstance().getMessageManager().sendMessage(player, "Claiming chunk X: " + chunk.getX() + "; Z: " + chunk.getZ() + "; to " + getZoneByGuild(guild).name());
        }
    }

    public boolean claim(Player player) {
        boolean b = false;
        Chunk chunk = player.getLocation().getChunk();
        UUID guild = getGuildUUIDByMember(player.getUniqueId());
        if (getGuildUUIDByChunk(chunk) != null) {
            player.sendMessage("This chunk is owned by " + getGuildNameByUUID(getGuildUUIDByChunk(chunk)));
        } else {
            OddJob.getInstance().getMySQLManager().addGuildChunks(guild, chunk);
            b = true;
            player.sendMessage("You have claimed " + chunk.toString() + " to " + getGuildNameByUUID(guild));
        }
        return b;
    }

    public void unclaim(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        UUID guild = getGuildUUIDByMember(player.getUniqueId());
        if (getGuildUUIDByChunk(chunk) != guild) {
            player.sendMessage("Sorry, you are not associated with the guild who claimed this chunk");
        } else {
            OddJob.getInstance().getMySQLManager().deleteGuildChunks(guild, chunk);
            player.sendMessage("You have unclaimed " + chunk.toString() + " to " + getGuildNameByUUID(guild));
        }
    }


    public UUID getGuildUUIDByChunk(Chunk chunk) {
        return UUID.fromString(OddJob.getInstance().getMySQLManager().getGuildUUIDByChunk(chunk));
    }


    public List<Chunk> getChunksByGuild(UUID guild) {
        return OddJob.getInstance().getMySQLManager().getGuildChunksByUUID(guild);
    }

    public void join(UUID guild, UUID player) {
        OddJob.getInstance().getMySQLManager().addGuildMember(guild, player, Role.members);
    }

    public UUID getGuildUUIDByName(String name) {
        return UUID.fromString(OddJob.getInstance().getMySQLManager().getGuildUUIDByName(name));
    }

    public void toggleAutoClaim(UUID player, Zone zone) {
        UUID guild;
        if (zone != Zone.GUILD) {
            guild = getGuildUUIDByZone(zone);
        } else {
            guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player);
        }
        if (guild != null) {
            if (autoClaim.containsKey(player)) {
                if (autoClaim.get(player) != guild) {
                    autoClaim.put(player, guild);
                    OddJob.getInstance().getMessageManager().sendMessage(player, "Changing Zone auto claim to " + getGuildNameByUUID(guild));
                } else {
                    autoClaim.remove(player);
                    OddJob.getInstance().getMessageManager().sendMessage(player, "Turning off Zone auto claim to " + getGuildNameByUUID(guild));
                }
            } else {
                autoClaim.put(player, guild);
                OddJob.getInstance().getMessageManager().sendMessage(player, "You are now claiming zones for " + getGuildNameByUUID(guild));
            }
        }
    }

    public String getGuildNameByUUID(UUID guild) {
        return OddJob.getInstance().getMySQLManager().getGuildNameByUUID(guild);
    }

    public UUID getGuildUUIDByZone(Zone zone) {
        return UUID.fromString(OddJob.getInstance().getMySQLManager().getGuildUUIDByZone(zone));
    }

    public boolean hasAutoClaim(UUID uniqueId) {
        return autoClaim.containsKey(uniqueId);
    }

    public UUID getGuildUUIDByMember(UUID player) {
        return UUID.fromString(OddJob.getInstance().getMySQLManager().getGuildUUIDByMemeber(player));
    }

    public Role getGuildMemberRole(UUID player) {
        return Role.valueOf(OddJob.getInstance().getMySQLManager().getGuildMemberRole(player));
    }

    public Role promoteMember(UUID guildUUID, UUID targetUUID) {
        //TODO
        return null;
    }

    public Role demoteMember(UUID guildUUID, UUID targetUUID) {
        //TODO
        return null;
    }

    public boolean changeName(UUID guild, String name) {
        return OddJob.getInstance().getMySQLManager().setGuildName(guild, name);
    }

    public void leave(UUID player) {
        OddJob.getInstance().getMySQLManager().deleteMemberFromGuild(player);
    }

    public void changeInvitedOnly(UUID guildUUIDByMember, boolean bol) {
        OddJob.getInstance().getMySQLManager().setGuildInvitedOnly(guildUUIDByMember, bol);
    }

    public void changeFriendlyFire(UUID guildUUIDByMember, boolean bol) {
        OddJob.getInstance().getMySQLManager().setGuildFriendlyFire(guildUUIDByMember, bol);
    }

    public void kickFromGuild(UUID guild, UUID player, String reason) {
        if (getGuildUUIDByMember(player) == guild) {
            leave(player);
            //TODO print reason
        }
    }

    public void inviteToGuild(UUID guild, UUID player) {
        OddJob.getInstance().getMySQLManager().addGuildInvite(guild, player);
    }

    public void uninviteToGuild(UUID guild, UUID player) {
        OddJob.getInstance().getMySQLManager().deleteGuildInvite(guild, player);
    }

    public Zone getZoneByGuild(UUID guild) {
        return Zone.valueOf(OddJob.getInstance().getMySQLManager().getZoneByGuild(guild));
    }

    public Role getGuildPermissionInvite(UUID guild) {
        return Role.valueOf(OddJob.getInstance().getMySQLManager().getGuildPermission("invite", guild.toString()));
    }

    public UUID getGuildInvitation(UUID player) {
        return OddJob.getInstance().getMySQLManager().getGuildInvite(player);
    }
}
