package no.helponline.Managers;

import no.helponline.Guilds.Guild;
import no.helponline.Guilds.Role;
import no.helponline.Guilds.Zone;
import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GuildManager {
    private HashMap<UUID, Guild> guilds;// Guild | Guild<>
    private HashMap<Chunk, UUID> chunks;// Chunk | Guild
    private HashMap<UUID, UUID> autoClaim;// Player | Guild

    public GuildManager() {
        guilds = new HashMap<>();
        chunks = new HashMap<>();
        autoClaim = new HashMap<>();
    }

    public boolean create(UUID uniqueId, String string) {
        /*
        Guild guild = getGuildByMember(uniqueId);
        if (guild != null) {
            return guild;
        }
        UUID uuid = UUID.randomUUID();
        guild = new Guild(uuid, string, uniqueId);
        guilds.put(uuid, guild);
        return guild;
        */
        HashMap<String, Object> memberOfGuild = OddJob.getInstance().getMySQLManager().getGuildByPlayer(uniqueId);
        if (memberOfGuild.isEmpty()) {
            String guildUUIDString = UUID.randomUUID().toString();
            memberOfGuild.put("name", string);
            memberOfGuild.put("zone", Zone.GUILD.name());
            memberOfGuild.put("uuid", guildUUIDString);
            memberOfGuild.put("invited_only", false);
            OddJob.getInstance().getMySQLManager().createGuild(memberOfGuild);
            addGuildMember(guildUUIDString, uniqueId, Role.guildMaster);
            return true;
        }
        return false;
    }

    public void addGuildMember(String guildUUIDString, UUID uniqueId, Role role) {
        OddJob.getInstance().getMySQLManager().addGuildMember(guildUUIDString, uniqueId, role);
    }

    public HashMap<UUID, Guild> getGuilds() {
        return guilds;
    }


    public Guild getGuildByMember(UUID uuid) {
        for (Guild guild : guilds.values()) {
            if (guild.isMember(uuid)) {
                return guild;
            }
        }
        return null;
    }


    public Guild getGuild(UUID uuid) {
        return guilds.get(uuid);
    }

    public void autoClaim(UUID uuid, Chunk chunk) {
        if (!chunks.containsKey(chunk)) {
            // NOT CLAIMED
            Guild g = getGuild(autoClaim.get(uuid));

            chunks.put(chunk, g.getId());
            OddJob.getInstance().getMessageManager().sendMessage(uuid, "Claiming chunk X: " + chunk.getX() + "; Z: " + chunk.getZ() + "; to " + g.getZone().name());
        }
    }

    public boolean claim(Player player) {
        boolean b = false;
        Chunk c = player.getLocation().getChunk();
        UUID guild = getGuildByMember(player.getUniqueId()).getId();
        if (!chunks.containsKey(c)) {
            chunks.put(c, guild);
            b = true;
            player.sendMessage("You have claimed " + c.toString() + " to " + getGuild(guild).getName());
        }
        return b;
    }


    public Guild getGuildByChunk(Chunk chunk) {
        return getGuild(chunks.get(chunk));
    }


    public List<Chunk> getChunksByGuild(UUID guild) {
        List<Chunk> chunks = new ArrayList<>();
        for (Chunk c : this.chunks.keySet()) {
            if (this.chunks.get(c).equals(guild)) {
                chunks.add(c);
            }
        }
        return chunks;
    }

    public void set(UUID uuid, String name, HashMap<UUID, Role> members, List<Chunk> chunks, Zone zone, HashMap<String, Object> settings) {
        Guild guild = new Guild(uuid, name, members, zone, settings);
        guilds.put(uuid, guild);
        for (Chunk chunk : chunks) {
            this.chunks.put(chunk, uuid);
        }
    }

    public List<Chunk> getChunks(UUID uuid) {
        List<Chunk> chunks = new ArrayList<>();
        for (Chunk chunk : this.chunks.keySet()) {
            if (this.chunks.containsValue(uuid)) chunks.add(chunk);
        }
        return chunks;
    }

    public void join(UUID guild, UUID player) {
        OddJob.getInstance().getMySQLManager().addGuildMember(guild.toString(), player, Role.members);
    }

    public UUID getGuildByName(String string) {
        for (UUID uuid : guilds.keySet()) {
            if (ChatColor.stripColor(string).equalsIgnoreCase(guilds.get(uuid).getName())) {
                return uuid;
            }
        }
        return null;
    }

    public void toggleAutoClaim(UUID uuid, Zone zone) {
        Guild guild = null;
        if (zone != Zone.GUILD) {
            for (UUID uid : guilds.keySet()) {
                Guild g = guilds.get(uid);
                if (g.getZone() == zone) {
                    guild = g;
                }
            }
        } else {
            guild = OddJob.getInstance().getGuildManager().getGuildByMember(uuid);
        }
        if (guild != null) {
            if (autoClaim.containsKey(uuid)) {
                if (autoClaim.get(uuid) != guild.getId()) {
                    autoClaim.put(uuid, guild.getId());
                    OddJob.getInstance().getMessageManager().sendMessage(uuid, "Changing Zone auto claim to " + guild.getName());
                } else {
                    autoClaim.remove(uuid);
                    OddJob.getInstance().getMessageManager().sendMessage(uuid, "Turning off Zone auto claim to " + guild.getName());
                }
            } else {
                autoClaim.put(uuid, guild.getId());
                OddJob.getInstance().getMessageManager().sendMessage(uuid, "You are now claiming zones for " + guild.getName());
            }
        }
    }

    public boolean hasAutoClaim(UUID uniqueId) {
        return autoClaim.containsKey(uniqueId);
    }

    public Guild getAutoCLaim(UUID uniqueId) {
        return OddJob.getInstance().getGuildManager().getGuild(autoClaim.get(uniqueId));
    }

    public UUID getGuildUUIDByMember(UUID targetUUID) {
        return UUID.fromString((String) OddJob.getInstance().getMySQLManager().getGuildByPlayer(targetUUID).get("uuid"));
    }

    public Role getGuildMemberRole(UUID targetUUID) {
        return OddJob.getInstance().getMySQLManager().getGuildMemberRole(targetUUID);
    }

    public Role promoteMember(UUID guildUUID, UUID targetUUID) {
        //TODO
        return null;
    }

    public Role demoteMember(UUID guildUUID, UUID targetUUID) {
        //TODO
        return null;
    }

    public boolean changeName(UUID uniqueId, String string) {
        return OddJob.getInstance().getMySQLManager().setGuildName(uniqueId, string);
    }

    public void leave(UUID uniqueId) {
        OddJob.getInstance().getMySQLManager().deleteMemberFromGuild(uniqueId);
    }

    public void changeInvitedOnly(UUID guildUUIDByMember, boolean bol) {
        OddJob.getInstance().getMySQLManager().setGuildInvitedOnly(guildUUIDByMember, bol);
    }

    public void changeFriendlyFire(UUID guildUUIDByMember, boolean bol) {
        OddJob.getInstance().getMySQLManager().setGuildFriendlyFire(guildUUIDByMember, bol);
    }

    public void kickFromGuild(UUID guild, UUID player) {
        if (getGuildUUIDByMember(player) == guild) {
            leave(player);
        }
    }

    public void inviteToGuild(UUID guild, String string) {
        UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(string);
        if (OddJob.getInstance().getMySQLManager().getGuildByPlayer(uuid).isEmpty() && OddJob.getInstance().getMySQLManager().getGuildInvite(uuid) == null) {
            OddJob.getInstance().getMySQLManager().addGuildInvite(guild, uuid);
        }
    }

    public void uninviteToGuild(UUID guild, String string) {
        UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(string);
        if (OddJob.getInstance().getMySQLManager().getGuildInvite(uuid) == guild) {
            OddJob.getInstance().getMySQLManager().deleteGuildInvite(guild, uuid);
        }
    }
}
