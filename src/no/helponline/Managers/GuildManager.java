package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Role;
import no.helponline.Utils.Zone;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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

    public void autoClaim(Player player, Chunk chunk) {
        if (getGuildUUIDByChunk(chunk, player.getWorld()) == null) {
            // NOT CLAIMED
            UUID guild = autoClaim.get(player.getUniqueId());

            OddJob.getInstance().getMySQLManager().addGuildChunks(guild, chunk);
            OddJob.getInstance().getMessageManager().sendMessage(player, "Claiming chunk X: " + chunk.getX() + "; Z: " + chunk.getZ() + "; to " + getZoneByGuild(guild).name());
        }
    }

    public void claim(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        UUID guild = getGuildUUIDByMember(player.getUniqueId());
        if (getGuildUUIDByChunk(chunk, player.getWorld()) != null) {
            player.sendMessage("This chunk is owned by " + getGuildNameByUUID(getGuildUUIDByChunk(chunk, player.getWorld())));
        } else {
            OddJob.getInstance().getMySQLManager().addGuildChunks(guild, chunk);
            player.sendMessage("You have claimed " + chunk.toString() + " to " + getGuildNameByUUID(guild));
        }
    }

    public void unclaim(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        UUID guild = getGuildUUIDByMember(player.getUniqueId());
        UUID comp = getGuildUUIDByMember(player.getUniqueId());
        if (!comp.equals(guild)) {
            player.sendMessage("Sorry, you are not associated with the guild who claimed this chunk");
        } else {
            OddJob.getInstance().getMySQLManager().deleteGuildChunks(guild, chunk);
            player.sendMessage("You have unclaimed " + chunk.toString() + " to " + getGuildNameByUUID(guild));
        }
    }


    public UUID getGuildUUIDByChunk(Chunk chunk, World world) {
        return OddJob.getInstance().getMySQLManager().getGuildUUIDByChunk(chunk, world);
    }


    public List<Chunk> getChunksByGuild(UUID guild) {
        return OddJob.getInstance().getMySQLManager().getGuildChunksByUUID(guild);
    }

    public void join(UUID guild, UUID player) {
        OddJob.getInstance().getMySQLManager().deleteInvitation(player);
        OddJob.getInstance().getMySQLManager().deletePending(player);
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
            guild = getGuildUUIDByMember(player);
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
        return OddJob.getInstance().getMySQLManager().getGuildUUIDByZone(zone);
    }

    public boolean hasAutoClaim(UUID uniqueId) {
        return autoClaim.containsKey(uniqueId);
    }

    public UUID getGuildUUIDByMember(UUID player) {
        return OddJob.getInstance().getMySQLManager().getGuildUUIDByMemeber(player);
    }

    public Role getGuildMemberRole(UUID player) {
        OddJob.getInstance().log("role");
        return OddJob.getInstance().getMySQLManager().getGuildMemberRole(player);
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

    public void uninviteToGuild(UUID player) {
        OddJob.getInstance().getMySQLManager().deleteInvitation(player);
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

    public UUID getGuildPending(UUID player) {
        return OddJob.getInstance().getMySQLManager().getGuildPending(player);
    }

    public List<String> listGuildsToJoin(UUID player) {
        List<UUID> guilds = OddJob.getInstance().getMySQLManager().getGuildUUIDs();
        UUID invited = getGuildInvitation(player);
        List<String> ret = new ArrayList<>();
        for (UUID guild : guilds) {
            if (isGuildOpen(guild)) {
                OddJob.getInstance().log("open");
                ret.add(getGuildNameByUUID(guild));
            } else if (invited != null) {
                if (guild.equals(invited)) {
                    OddJob.getInstance().log("invited");
                    ret.add(getGuildNameByUUID(guild));
                }
            }
        }
        OddJob.getInstance().log("ret: " + ret.size());
        return ret;
    }

    private boolean isGuildOpen(UUID guild) {
        return !OddJob.getInstance().getMySQLManager().getGuildSettings("invited_only", guild);
    }

    public List<UUID> getGuildInvitations(UUID guild) {
        return OddJob.getInstance().getMySQLManager().getGuildInvitations(guild);
    }

    public void addGuildPending(UUID guild, UUID player) {
        OddJob.getInstance().getMySQLManager().addGuildPending(guild, player);
    }

    public List<UUID> getGuildMembers(UUID guild) {
        return OddJob.getInstance().getMySQLManager().getGuildMembers(guild);
    }

    public void claim(Player player, Zone zone) {
        Chunk chunk = player.getLocation().getChunk();
        UUID guild = getGuildUUIDByZone(zone);
        if (getGuildUUIDByChunk(chunk, player.getWorld()) != null) {
            player.sendMessage("This chunk is owned by " + getGuildNameByUUID(getGuildUUIDByChunk(chunk, player.getWorld())));
        } else {
            OddJob.getInstance().getMySQLManager().addGuildChunks(guild, chunk);
            player.sendMessage("You have claimed " + chunk.toString() + " to " + getGuildNameByUUID(guild));
        }
    }

    public void create(String name, Zone zone, boolean invited_only, boolean friendly_fire) {
        HashMap<String, Object> memberOfGuild = new HashMap<>();

        UUID guild = UUID.randomUUID();
        memberOfGuild.put("name", name);
        memberOfGuild.put("zone", zone.name());
        memberOfGuild.put("uuid", guild.toString());
        memberOfGuild.put("invited_only", invited_only);
        memberOfGuild.put("friendly_fire", friendly_fire);
        OddJob.getInstance().getMySQLManager().createGuild(memberOfGuild);
    }
}
