package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Role;
import no.helponline.Utils.Zone;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GuildManager {
    private final HashMap<UUID, UUID> autoClaim;// Player | Guild

    public GuildManager() {
        autoClaim = new HashMap<>();
    }

    public boolean create(UUID player, String name) {
        if (getGuildUUIDByMember(player) != null) {
            // PLAYER HAS GUILD ALREADY
            return false;
        }

        HashMap<String, String> memberOfGuild = new HashMap<>();

        // NEW RANDOM UUID
        UUID guild = UUID.randomUUID();

        memberOfGuild.put("name", name);
        memberOfGuild.put("zone", Zone.GUILD.name());
        memberOfGuild.put("uuid", guild.toString());
        memberOfGuild.put("invited_only", Boolean.toString(false));
        memberOfGuild.put("friendly_fire", Boolean.toString(false));
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

            // CLAIM CHUNK
            OddJob.getInstance().getMySQLManager().addGuildChunks(guild, chunk, player);
            OddJob.getInstance().getMessageManager().success("Claiming chunk " + ChatColor.GOLD + "X:" + chunk.getX() + " Y:" + chunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " to " + ChatColor.DARK_AQUA + getZoneByGuild(guild).name(), player, true);
        }
    }

    public void claim(Player player) {
        // WHAT CHUNK ARE WE IN
        Chunk chunk = player.getLocation().getChunk();
        // DO YOU HAVE A GUILD
        UUID guild = getGuildUUIDByMember(player.getUniqueId());
        if (getGuildUUIDByChunk(chunk, player.getWorld()) != null) {
            // ALREADY CLAIMED
            OddJob.getInstance().getMessageManager().danger("This chunk is owned by " + ChatColor.DARK_AQUA + getGuildNameByUUID(getGuildUUIDByChunk(chunk, player.getWorld())), player, false);
        } else {
            // CLAIM CHUNK
            OddJob.getInstance().getMySQLManager().addGuildChunks(guild, chunk, player);
            OddJob.getInstance().getMessageManager().success("You have claimed " + ChatColor.GOLD + "X:" + chunk.getX() + " Z:" + chunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " to " + ChatColor.DARK_AQUA + getGuildNameByUUID(guild), player, true);
        }
    }

    public void unclaim(Player player) {
        // WHAT CHUNK ARE WE IN
        Chunk chunk = player.getLocation().getChunk();
        // DO YOU HAVE A GUILD
        UUID guild = getGuildUUIDByMember(player.getUniqueId());
        // DO CHUNK HAVE A GUILD
        UUID comp = getGuildUUIDByChunk(chunk, player.getWorld());
        if (!comp.equals(guild)) {
            OddJob.getInstance().getMessageManager().danger("Sorry, you are not associated with the guild who claimed this chunk", player, false);
        } else {
            // UNCLAIM CHUNK
            OddJob.getInstance().getMySQLManager().deleteGuildChunks(guild, chunk, player);
            OddJob.getInstance().getMessageManager().success("You have unclaimed " + ChatColor.GOLD + "X:" + chunk.getX() + " Z:" + chunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " from " + ChatColor.DARK_AQUA + getGuildNameByUUID(guild), player, true);
        }
    }

    public UUID getGuildUUIDByChunk(Chunk chunk, World world) {
        return OddJob.getInstance().getMySQLManager().getGuildUUIDByChunk(chunk, world);
    }


    public List<Chunk> getChunksByGuild(UUID guild) {
        return OddJob.getInstance().getMySQLManager().getGuildChunksByUUID(guild);
    }

    public void join(UUID guild, UUID player) {
        // DELETE INVITATION IF THERE ARE ANY
        OddJob.getInstance().getMySQLManager().deleteInvitation(player);
        // DELETE PENDINGS IF THERE ARE ANY
        OddJob.getInstance().getMySQLManager().deletePending(player);
        // ADD GUILDMEMBER
        OddJob.getInstance().getMySQLManager().addGuildMember(guild, player, Role.members);
    }

    public UUID getGuildUUIDByName(String name) {
        return OddJob.getInstance().getMySQLManager().getGuildUUIDByName(name);
    }

    public void toggleAutoClaim(Player player, Zone zone) {
        UUID guild;
        if (zone != Zone.GUILD) {
            guild = getGuildUUIDByZone(zone);
        } else {
            guild = getGuildUUIDByMember(player.getUniqueId());
        }
        if (guild != null) {
            if (autoClaim.containsKey(player.getUniqueId())) {
                if (!guild.equals(autoClaim.get(player.getUniqueId()))) {
                    autoClaim.put(player.getUniqueId(), guild);
                    OddJob.getInstance().getMessageManager().warning("Changing Zone auto claim to " + ChatColor.DARK_AQUA + getGuildNameByUUID(guild), player, true);
                } else {
                    autoClaim.remove(player.getUniqueId());
                    OddJob.getInstance().getMessageManager().warning("Turning off Zone auto claim to " + ChatColor.DARK_AQUA + getGuildNameByUUID(guild), player, true);
                }
            } else {
                autoClaim.put(player.getUniqueId(), guild);
                OddJob.getInstance().getMessageManager().warning("You are now claiming zones for " + ChatColor.DARK_AQUA + getGuildNameByUUID(guild), player, true);
                OddJob.getInstance().getGuildManager().claim(player);
            }
        }
    }

    public String getGuildNameByUUID(UUID guild) {
        return OddJob.getInstance().getMySQLManager().getGuildNameByUUID(guild);
    }

    public UUID getGuildUUIDByZone(Zone zone) {
        return OddJob.getInstance().getMySQLManager().getGuildUUIDByZone(zone);
    }

    public boolean hasAutoClaim(UUID player) {
        return autoClaim.containsKey(player);
    }

    public UUID getGuildUUIDByMember(UUID player) {
        return OddJob.getInstance().getMySQLManager().getGuildUUIDByMemeber(player);
    }

    public Role getGuildMemberRole(UUID player) {
        return OddJob.getInstance().getMySQLManager().getGuildMemberRole(player);
    }

    public Role promoteMember(UUID guildUUID, UUID targetUUID) {
        Role prevRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(targetUUID);
        Role newRole = null;
        switch (prevRole.level()) {
            case 11:
                newRole = Role.mods;
                break;
            case 22:
                newRole = Role.admins;
                break;
            case 33:
                newRole = Role.guildMaster;
                break;
        }
        if (newRole != null) {
            OddJob.getInstance().getMySQLManager().moteGuild(guildUUID, targetUUID, newRole.name());
        }
        return newRole;
    }

    public Role demoteMember(UUID guildUUID, UUID targetUUID) {
        Role prevRole = OddJob.getInstance().getGuildManager().getGuildMemberRole(targetUUID);
        Role newRole = null;
        switch (prevRole.level()) {
            case 22:
                newRole = Role.members;
                break;
            case 33:
                newRole = Role.mods;
                break;
            case 99:
                newRole = Role.admins;
                break;
        }
        if (newRole != null) {
            OddJob.getInstance().getMySQLManager().moteGuild(guildUUID, targetUUID, newRole.name());
        }
        return newRole;
    }

    public void changeName(UUID guild, String name) {
        OddJob.getInstance().getMySQLManager().setGuildName(guild, name);
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
        if (guild.equals(getGuildUUIDByMember(player))) {
            leave(player);
            OddJob.getInstance().log("left");
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
                ret.add(getGuildNameByUUID(guild));
            } else if (invited != null) {
                if (guild.equals(invited)) {
                    ret.add(getGuildNameByUUID(guild));
                }
            }
        }
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
            OddJob.getInstance().getMessageManager().danger("This chunk is owned by " + ChatColor.DARK_AQUA + getGuildNameByUUID(getGuildUUIDByChunk(chunk, player.getWorld())), player, false);
        } else {
            OddJob.getInstance().getMySQLManager().addGuildChunks(guild, chunk, player);
            OddJob.getInstance().getMessageManager().success("You have claimed " + ChatColor.GOLD + "X:" + chunk.getX() + " Z:" + chunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " to " + ChatColor.DARK_AQUA + getGuildNameByUUID(guild), player, true);
        }
    }

    public void create(String name, Zone zone, boolean invited_only, boolean friendly_fire) {
        HashMap<String, String> memberOfGuild = new HashMap<>();

        UUID guild = UUID.randomUUID();
        memberOfGuild.put("name", name);
        memberOfGuild.put("zone", zone.name());
        memberOfGuild.put("uuid", guild.toString());
        memberOfGuild.put("invited_only", Boolean.toString(invited_only));
        memberOfGuild.put("friendly_fire", Boolean.toString(friendly_fire));
        OddJob.getInstance().getMySQLManager().createGuild(memberOfGuild);
    }

    public void accept(UUID guild, UUID target) {
        join(guild, target);
        OddJob.getInstance().getMySQLManager().deletePending(target);
        OddJob.getInstance().getMySQLManager().deleteInvitation(target);
        OddJob.getInstance().getMessageManager().success("Welcome to " + ChatColor.DARK_AQUA + getGuildNameByUUID(guild) + ChatColor.RESET + " guild!", target, true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (OddJob.getInstance().getGuildManager().getGuildMembers(guild).contains(p.getUniqueId()) && p.getUniqueId() != target) {
                OddJob.getInstance().getMessageManager().success("Please welcome " + ChatColor.GOLD + OddJob.getInstance().getPlayerManager().getName(target) + ChatColor.RESET + " to the guild", p, false);
            }
        }
    }

    public void deny(UUID guild, UUID target) {
        OddJob.getInstance().getMySQLManager().deletePending(target);
        OddJob.getInstance().getMySQLManager().deleteInvitation(target);
        OddJob.getInstance().getMessageManager().danger("You have declined " + ChatColor.GOLD + OddJob.getInstance().getPlayerManager().getName(target) + ChatColor.RESET + " entrance to guild!", target, true);
        for (UUID member : OddJob.getInstance().getGuildManager().getGuildMembers(guild)) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(member);
            if (op.isOnline()) {
                OddJob.getInstance().getMessageManager().warning("Request from " + ChatColor.GOLD + OddJob.getInstance().getPlayerManager().getName(target) + ChatColor.RESET + " to join " + ChatColor.DARK_AQUA + getGuildNameByUUID(guild) + ChatColor.RESET + " has been declined", op.getUniqueId(), false);
            }
        }
    }

    public int getGuildCountClaims(UUID guild) {
        return OddJob.getInstance().getMySQLManager().getGuildCountClaims(guild);
    }

    public List<UUID> getGuildPendings(UUID guild) {
        return OddJob.getInstance().getMySQLManager().getGuildPendings(guild);
    }
}
