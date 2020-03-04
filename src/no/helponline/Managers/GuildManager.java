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
        addGuildMember(guild, player, Role.Master);
        return true;
    }

    public void addGuildMember(UUID guild, UUID player, Role role) {
        OddJob.getInstance().getMySQLManager().addGuildMember(guild, player, role);
    }

    public List<UUID> getGuilds() {
        return OddJob.getInstance().getMySQLManager().getGuildUUIDs();
    }

    public void autoClaim(Player player, Chunk chunk) {
        // Is Chunk claimed by Guild
        if (getGuildUUIDByChunk(chunk, player.getWorld()) == null) {
            UUID claimingGuild = autoClaim.get(player.getUniqueId());

            // Claim Chunk to Guild
            claim(claimingGuild, chunk, player);
            OddJob.getInstance().getMessageManager().success("Claiming chunk " + ChatColor.GOLD + "X:" + chunk.getX() + " Y:" + chunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " to " + ChatColor.DARK_AQUA + getZoneByGuild(claimingGuild).name(), player, true);
        }
    }

    private void claim(UUID guild, Chunk chunk, Player player) {
        UUID world = player.getWorld().getUID();
        HashMap<UUID, Location> listLocksInWorld = OddJob.getInstance().getMySQLManager().locksInWorld(world);
        for (UUID owner : listLocksInWorld.keySet()) {
            if (!guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()))) {
                Location location = listLocksInWorld.get(owner);
                if (chunk.equals(location.getChunk())) {
                    OddJob.getInstance().getMessageManager().warning("Your locked " + location.getBlock().getType().name() + " is inside a claimed area and will be unlocked", owner, false);
                    OddJob.getInstance().getLockManager().unlock(location);
                }
            }
        }
        OddJob.getInstance().getMySQLManager().addGuildChunks(guild, chunk, player);
    }

    public void claim(Player player) {
        Chunk inChunk = player.getLocation().getChunk();
        UUID playerGuild = getGuildUUIDByMember(player.getUniqueId());
        UUID chunkGuild = getGuildUUIDByChunk(inChunk, player.getWorld());

        if (chunkGuild != null && !chunkGuild.equals(getGuildUUIDByZone(Zone.WILD))) {
            // Chunk is already claimed by Guild
            OddJob.getInstance().getMessageManager().danger("This chunk is owned by " + ChatColor.DARK_AQUA + getGuildNameByUUID(getGuildUUIDByChunk(inChunk, player.getWorld())), player, false);
        } else {
            // Claiming Chunk to Guild
            claim(playerGuild, inChunk, player);
            OddJob.getInstance().getMessageManager().success("You have claimed " + ChatColor.GOLD + "X:" + inChunk.getX() + " Z:" + inChunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " to " + ChatColor.DARK_AQUA + getGuildNameByUUID(playerGuild), player, true);
        }
    }

    public void unclaim(Player player) {
        Chunk inChunk = player.getLocation().getChunk();
        UUID playerGuild = getGuildUUIDByMember(player.getUniqueId());
        UUID chunkGuild = getGuildUUIDByChunk(inChunk, player.getWorld());

        // Is the Chunk claimed by Players Guild
        if (!chunkGuild.equals(playerGuild)) {
            // Not claimed by the Players Guild
            OddJob.getInstance().getMessageManager().danger("Sorry, you are not associated with the guild who claimed this chunk", player, false);
        } else {
            // Unclaim the Chunk from Guild
            OddJob.getInstance().getMySQLManager().deleteGuildChunks(playerGuild, inChunk, player);
            OddJob.getInstance().getMessageManager().success("You have unclaimed " + ChatColor.GOLD + "X:" + inChunk.getX() + " Z:" + inChunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " from " + ChatColor.DARK_AQUA + getGuildNameByUUID(playerGuild), player, true);
        }
    }

    public UUID getGuildUUIDByChunk(Chunk chunk, World world) {
        return OddJob.getInstance().getMySQLManager().getGuildUUIDByChunk(chunk, world);
    }


    public List<Chunk> getChunksByGuild(UUID guild) {
        return OddJob.getInstance().getMySQLManager().getGuildChunksByUUID(guild);
    }

    public void join(UUID guild, UUID player) {
        // Delete the Players invitation to the Guild
        OddJob.getInstance().getMySQLManager().deleteInvitation(player);
        // Delete the Players pending invitations to Guilds
        OddJob.getInstance().getMySQLManager().deletePending(player);
        // Add a Player to Guild as Member
        OddJob.getInstance().getMySQLManager().addGuildMember(guild, player, Role.Members);
    }

    public UUID getGuildUUIDByName(String name) {
        return OddJob.getInstance().getMySQLManager().getGuildUUIDByName(name);
    }

    public void toggleAutoClaim(Player player, Zone zone) {
        UUID guild;
        if (zone != Zone.GUILD) {
            // Claiming to Zone
            guild = getGuildUUIDByZone(zone);
        } else {
            // Claiming to the Players Guild
            guild = getGuildUUIDByMember(player.getUniqueId());
        }
        if (guild != null) {
            if (autoClaim.containsKey(player.getUniqueId())) {
                if (!guild.equals(autoClaim.get(player.getUniqueId()))) {
                    // Changing Zone to autoClaim for
                    autoClaim.put(player.getUniqueId(), guild);
                    OddJob.getInstance().getMessageManager().warning("Changing Zone auto claim to " + ChatColor.DARK_AQUA + getGuildNameByUUID(guild), player, true);
                } else {
                    // Stops autoClaim
                    autoClaim.remove(player.getUniqueId());
                    OddJob.getInstance().getMessageManager().warning("Turning off Zone auto claim to " + ChatColor.DARK_AQUA + getGuildNameByUUID(guild), player, true);
                }
            } else {
                // Starts autoClaim
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
                newRole = Role.Mods;
                break;
            case 22:
                newRole = Role.Admins;
                break;
            case 33:
                newRole = Role.Master;
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
                newRole = Role.Members;
                break;
            case 33:
                newRole = Role.Mods;
                break;
            case 99:
                newRole = Role.Admins;
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
        Chunk inChunk = player.getLocation().getChunk();
        UUID zoneGuild = getGuildUUIDByZone(zone);
        UUID chunkGuild = getGuildUUIDByChunk(inChunk, player.getWorld());

        if (chunkGuild != null && !chunkGuild.equals(getGuildUUIDByZone(Zone.WILD))) {
            // The Chunk is already claimed by a Guild
            OddJob.getInstance().getMessageManager().danger("This chunk is owned by " + ChatColor.DARK_AQUA + getGuildNameByUUID(getGuildUUIDByChunk(inChunk, player.getWorld())), player, false);
        } else {
            // Claiming to Zone
            OddJob.getInstance().getMySQLManager().addGuildChunks(zoneGuild, inChunk, player);
            OddJob.getInstance().getMessageManager().success("You have claimed " + ChatColor.GOLD + "X:" + inChunk.getX() + " Z:" + inChunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " to " + ChatColor.DARK_AQUA + getGuildNameByUUID(zoneGuild), player, true);
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

    public void accept(UUID guildToJoin, UUID joiningPlayer) {
        join(guildToJoin, joiningPlayer);
        OddJob.getInstance().getMessageManager().success("Welcome to " + ChatColor.DARK_AQUA + getGuildNameByUUID(guildToJoin) + ChatColor.RESET + " guild!", joiningPlayer, true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (OddJob.getInstance().getGuildManager().getGuildMembers(guildToJoin).contains(p.getUniqueId()) && p.getUniqueId() != joiningPlayer) {
                OddJob.getInstance().getMessageManager().success("Please welcome " + ChatColor.GOLD + OddJob.getInstance().getPlayerManager().getName(joiningPlayer) + ChatColor.RESET + " to the guild", p, false);
            }
        }
    }

    public void deny(UUID guildToJoin, UUID joiningPlayer) {
        OddJob.getInstance().getMySQLManager().deletePending(joiningPlayer);
        OddJob.getInstance().getMySQLManager().deleteInvitation(joiningPlayer);
        OddJob.getInstance().getMessageManager().danger("You have declined " + ChatColor.GOLD + OddJob.getInstance().getPlayerManager().getName(joiningPlayer) + ChatColor.RESET + " entrance to guild!", joiningPlayer, true);
        for (UUID member : OddJob.getInstance().getGuildManager().getGuildMembers(guildToJoin)) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(member);
            if (op.isOnline()) {
                OddJob.getInstance().getMessageManager().warning("Request from " + ChatColor.GOLD + OddJob.getInstance().getPlayerManager().getName(joiningPlayer) + ChatColor.RESET + " to join " + ChatColor.DARK_AQUA + getGuildNameByUUID(guildToJoin) + ChatColor.RESET + " has been declined", op.getUniqueId(), false);
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
