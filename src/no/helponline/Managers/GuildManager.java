package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Guild;
import no.helponline.Utils.Role;
import no.helponline.Utils.Zone;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class GuildManager {
    private HashMap<UUID, UUID> autoClaim = new HashMap<>();      // PlayerUUID   | GuildUUID
    private HashMap<UUID, Guild> guilds = new HashMap<>();        // GuildUUID    | GUILD
    private HashMap<Chunk, UUID> chunkGuild = new HashMap<>();    // Chunk        | GuildUUID
    private HashMap<UUID, UUID> guildPending = new HashMap<>();   // PlayerUUID   | GuildUUID
    private HashMap<UUID, UUID> guildInvite = new HashMap<>();    // PlayerUUID   | GuildUUID
    private HashMap<UUID, UUID> members = new HashMap<>();        // PlayerUUID   | GuildUUID

    public GuildManager() {
    }

    public void loadGuilds() {
        guilds = OddJob.getInstance().getMySQLManager().loadGuilds();
    }

    public void loadChunks() {
        chunkGuild = OddJob.getInstance().getMySQLManager().loadChunks();
    }

    public void saveGuilds() {
        for (UUID uuid : guilds.keySet()) {
            OddJob.getInstance().getMySQLManager().saveGuild(uuid.toString(), guilds.get(uuid).getName(), guilds.get(uuid).getZone().name(), guilds.get(uuid).getInvitedOnly(), guilds.get(uuid).getFriendlyFire(), guilds.get(uuid).getPermissionInvite().name());
            for (UUID player : guilds.get(uuid).getMembers().keySet()) {
                OddJob.getInstance().getMySQLManager().saveGuildMembers(uuid, player, guilds.get(uuid).getMembers().get(player));
            }
        }
    }

    public void saveChunks() {
        for (Chunk chunk : chunkGuild.keySet()) {
            OddJob.getInstance().getMySQLManager().saveChunks(chunkGuild.get(chunk), chunk.getWorld(), chunk.getX(), chunk.getZ());
        }
    }

    //NEW
    public boolean create(UUID player, String name) {
        if (getGuildUUIDByMember(player) != null) {
            // Player already is associated with a Guild
            return false;
        }

        // New random UUID
        UUID guild = UUID.randomUUID();

        // Add the new Guild
        guilds.put(guild, new Guild(name, Zone.GUILD, guild, false, false, player, Role.Master));
        members.put(player, guild);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            OddJob.getInstance().getScoreManager().guild(p);
        }
        //HashMap<String, String> memberOfGuild = new HashMap<>();
        //memberOfGuild.put("name", name);
        //memberOfGuild.put("zone", Zone.GUILD.name());
        //memberOfGuild.put("uuid", guild.toString());
        //memberOfGuild.put("invited_only", Boolean.toString(false));
        //memberOfGuild.put("friendly_fire", Boolean.toString(false));
        //OddJob.getInstance().getMySQLManager().createGuild(memberOfGuild);
        //addGuildMember(guild, player, Role.Master);
        return true;
    }

    /*
    public void addGuildMember(UUID guild, UUID player, Role role) {
        OddJob.getInstance().getMySQLManager().addGuildMember(guild, player, role);
    }
     */

    // NEW
    public Set<UUID> getGuilds() {
        //return OddJob.getInstance().getMySQLManager().getGuildUUIDs();
        return guilds.keySet();
    }

    // CONTINUE
    public void autoClaim(Player player, Chunk chunk) {
        // Is Chunk claimed by Guild
        if (getGuildUUIDByChunk(chunk, player.getWorld()) == null) {
            UUID claimingGuild = autoClaim.get(player.getUniqueId());

            // Claim Chunk to Guild
            claim(claimingGuild, chunk, player);
            OddJob.getInstance().getMessageManager().success("Claiming chunk " + ChatColor.GOLD + "X:" + chunk.getX() + " Y:" + chunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " to " + ChatColor.DARK_AQUA + getZoneByGuild(claimingGuild).name(), player, true);
        }
    }

    // NEW
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
        //OddJob.getInstance().getMySQLManager().addGuildChunks(guild, chunk, player);
        this.chunkGuild.put(chunk, guild);
        this.guilds.get(guild).addChunks();
    }

    // CONTINUE
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

    // NEW
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
            this.chunkGuild.remove(player.getLocation().getChunk());
            this.guilds.get(chunkGuild).removeChunks();
            OddJob.getInstance().getMessageManager().success("You have unclaimed " + ChatColor.GOLD + "X:" + inChunk.getX() + " Z:" + inChunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " from " + ChatColor.DARK_AQUA + getGuildNameByUUID(playerGuild), player, true);
        }
    }

    // NEW
    public UUID getGuildUUIDByChunk(Chunk chunk, World world) {
        //return OddJob.getInstance().getMySQLManager().getGuildUUIDByChunk(chunk, world);
        return chunkGuild.get(chunk) == null ? OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD) : chunkGuild.get(chunk);
    }

    // NEW
    public void join(UUID guild, UUID player) {
        // Delete the Players invitation to the Guild
        //OddJob.getInstance().getMySQLManager().deleteInvitation(player);
        guildInvite.remove(player);
        // Delete the Players pending invitations to Guilds
        //OddJob.getInstance().getMySQLManager().deletePending(player);
        guildPending.remove(player);
        // Add a Player to Guild as Member
        //OddJob.getInstance().getMySQLManager().addGuildMember(guild, player, Role.Members);
        guilds.get(guild).getMembers().put(player, Role.Members);
        members.put(player, guild);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            OddJob.getInstance().getScoreManager().guild(p);
        }
    }

    // NEW
    public UUID getGuildUUIDByName(String name) {
        //return OddJob.getInstance().getMySQLManager().getGuildUUIDByName(name);
        for (UUID uuid : guilds.keySet()) {
            if (name.equalsIgnoreCase(ChatColor.stripColor(guilds.get(uuid).getName()))) {
                return uuid;
            }
        }
        return null;
    }

    // CONTINUE
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
                claim(player);
            }
        }
    }

    //NEW
    public String getGuildNameByUUID(UUID guild) {
        //return OddJob.getInstance().getMySQLManager().getGuildNameByUUID(guild);
        return guilds.get(guild).getName();
    }

    //NEW
    public UUID getGuildUUIDByZone(Zone zone) {
        //return OddJob.getInstance().getMySQLManager().getGuildUUIDByZone(zone);
        for (UUID uuid : guilds.keySet()) {
            if (zone.equals(guilds.get(uuid).getZone())) {
                return uuid;
            }
        }
        return null;
    }

    // CONTINUE
    public boolean hasAutoClaim(UUID player) {
        return autoClaim.containsKey(player);
    }

    // NEW
    public UUID getGuildUUIDByMember(UUID player) {
        //return OddJob.getInstance().getMySQLManager().getGuildUUIDByMember(player);
        for (UUID uuid : guilds.keySet()) {
            if (guilds.get(uuid).getMembers().containsKey(player)) {
                return uuid;
            }
        }
        return null;
    }

    // NEW
    public Role getGuildMemberRole(UUID player) {
        //return OddJob.getInstance().getMySQLManager().getGuildMemberRole(player);
        return guilds.get(getGuildUUIDByMember(player)).getMembers().get(player);
    }

    // NEW
    public Role promoteMember(UUID guildUUID, UUID targetUUID) {
        Role prevRole = getGuildMemberRole(targetUUID);
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
            //OddJob.getInstance().getMySQLManager().moteGuild(guildUUID, targetUUID, newRole.name());
            guilds.get(guildUUID).getMembers().put(targetUUID, newRole);
        }
        return newRole;
    }

    // NEW
    public Role demoteMember(UUID guildUUID, UUID targetUUID) {
        Role prevRole = getGuildMemberRole(targetUUID);
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
            //OddJob.getInstance().getMySQLManager().moteGuild(guildUUID, targetUUID, newRole.name());
            guilds.get(guildUUID).getMembers().put(targetUUID, newRole);
        }
        return newRole;
    }

    // NEW
    public void changeName(UUID guild, String name) {
        //OddJob.getInstance().getMySQLManager().setGuildName(guild, name);
        guilds.get(guild).setName(name);
    }

    // NEW
    public void leave(UUID player) {
        //OddJob.getInstance().getMySQLManager().deleteMemberFromGuild(player);
        guilds.get(getGuildUUIDByMember(player)).getMembers().remove(player);
        members.remove(player);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            OddJob.getInstance().getScoreManager().clear(p);
        }
    }

    // NEW
    public void changeInvitedOnly(UUID guildUUIDByMember, boolean bol) {
        //OddJob.getInstance().getMySQLManager().setGuildInvitedOnly(guildUUIDByMember, bol);
        guilds.get(guildUUIDByMember).setInvitedOnly(bol);
    }

    // NEW
    public void changeFriendlyFire(UUID guildUUIDByMember, boolean bol) {
        //OddJob.getInstance().getMySQLManager().setGuildFriendlyFire(guildUUIDByMember, bol);
        guilds.get(guildUUIDByMember).setFriendlyFire(bol);
    }

    // CONTINUE
    public void kickFromGuild(UUID guild, UUID player, String reason) {
        if (guild.equals(getGuildUUIDByMember(player))) {
            leave(player);
            OddJob.getInstance().log("left");
            //TODO print reason
        }
    }

    // NEW
    public void inviteToGuild(UUID guild, UUID player) {
        //OddJob.getInstance().getMySQLManager().addGuildInvite(guild, player);
        guildInvite.put(player, guild);
    }

    // NEW
    public void uninviteToGuild(UUID player) {
        //OddJob.getInstance().getMySQLManager().deleteInvitation(player);
        guildInvite.remove(player);
    }

    // NEW
    public Zone getZoneByGuild(UUID guild) {
        //return Zone.valueOf(OddJob.getInstance().getMySQLManager().getZoneByGuild(guild));
        return guilds.get(guild).getZone();
    }

    // NEW
    public Role getGuildPermissionInvite(UUID guild) {
        //return Role.valueOf(OddJob.getInstance().getMySQLManager().getGuildPermission("invite", guild.toString()));
        return guilds.get(guild).getPermissionInvite();
    }

    // NEW
    public UUID getGuildInvitation(UUID player) {
        //return OddJob.getInstance().getMySQLManager().getGuildInvite(player);
        return guildInvite.get(player);
    }

    // NEW
    public UUID getGuildPending(UUID player) {
        //return OddJob.getInstance().getMySQLManager().getGuildPending(player);
        return guildPending.get(player);
    }

    // NEW
    public List<String> listGuildsToJoin(UUID player) {
        //List<UUID> guilds = OddJob.getInstance().getMySQLManager().getGuildUUIDs();
        Set<UUID> guilds = this.guilds.keySet();
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

    // NEW
    private boolean isGuildOpen(UUID guild) {
        //return !OddJob.getInstance().getMySQLManager().getGuildSettings("invited_only", guild);
        return !guilds.get(guild).getInvitedOnly();
    }

    // NEW
    public List<UUID> getGuildInvitations(UUID guild) {
        //return OddJob.getInstance().getMySQLManager().getGuildInvitations(guild);
        List<UUID> list = new ArrayList<>();
        for (UUID uuid : guildInvite.keySet()) {
            if (guildInvite.get(uuid).equals(guild))
                list.add(uuid);
        }
        return list;
    }

    // NEW
    public void addGuildPending(UUID guild, UUID player) {
        //OddJob.getInstance().getMySQLManager().addGuildPending(guild, player);
        guildPending.put(player, guild);
    }

    // NEW
    public Set<UUID> getGuildMembers(UUID guild) {
        //return OddJob.getInstance().getMySQLManager().getGuildMembers(guild);
        return guilds.get(guild).getMembers().keySet();
    }

    //NEW
    public void claim(Player player, Zone zone) {
        Chunk inChunk = player.getLocation().getChunk();
        UUID zoneGuild = getGuildUUIDByZone(zone);
        UUID chunkGuild = getGuildUUIDByChunk(inChunk, player.getWorld());

        if (chunkGuild != null && !chunkGuild.equals(getGuildUUIDByZone(Zone.WILD))) {
            // The Chunk is already claimed by a Guild
            OddJob.getInstance().getMessageManager().danger("This chunk is owned by " + ChatColor.DARK_AQUA + getGuildNameByUUID(getGuildUUIDByChunk(inChunk, player.getWorld())), player, false);
        } else {
            // Claiming to Zone
            //OddJob.getInstance().getMySQLManager().addGuildChunks(zoneGuild, inChunk, player);
            this.chunkGuild.put(inChunk, zoneGuild);
            this.guilds.get(getGuildUUIDByZone(zone)).addChunks();
            OddJob.getInstance().getMessageManager().success("You have claimed " + ChatColor.GOLD + "X:" + inChunk.getX() + " Z:" + inChunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " to " + ChatColor.DARK_AQUA + getGuildNameByUUID(zoneGuild), player, true);
        }
    }

    // NEW
    public void create(String name, Zone zone, boolean invited_only, boolean friendly_fire) {
        //HashMap<String, String> memberOfGuild = new HashMap<>();

        UUID guild = UUID.randomUUID();
        guilds.put(guild, new Guild(name, zone, guild, invited_only, friendly_fire));
        //memberOfGuild.put("name", name);
        //memberOfGuild.put("zone", zone.name());
        //memberOfGuild.put("uuid", guild.toString());
        //memberOfGuild.put("invited_only", Boolean.toString(invited_only));
        //memberOfGuild.put("friendly_fire", Boolean.toString(friendly_fire));
        //OddJob.getInstance().getMySQLManager().createGuild(memberOfGuild);
    }

    // NEW
    public void accept(UUID guildToJoin, UUID joiningPlayer) {
        join(guildToJoin, joiningPlayer);
        OddJob.getInstance().getMessageManager().success("Welcome to " + ChatColor.DARK_AQUA + getGuildNameByUUID(guildToJoin) + ChatColor.RESET + " guild!", joiningPlayer, true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            //if (OddJob.getInstance().getGuildManager().getGuildMembers(guildToJoin).contains(p.getUniqueId()) && p.getUniqueId() != joiningPlayer) {
            if (guilds.get(guildToJoin).getMembers().containsKey(p.getUniqueId()) && !p.getUniqueId().equals(joiningPlayer)) {
                OddJob.getInstance().getMessageManager().success("Please welcome " + ChatColor.GOLD + OddJob.getInstance().getPlayerManager().getName(joiningPlayer) + ChatColor.RESET + " to the guild", p, false);
            }
        }
    }

    // NEW
    public void deny(UUID guildToJoin, UUID joiningPlayer) {
        //OddJob.getInstance().getMySQLManager().deletePending(joiningPlayer);
        guildPending.remove(joiningPlayer);
        //OddJob.getInstance().getMySQLManager().deleteInvitation(joiningPlayer);
        guildInvite.remove(joiningPlayer);
        OddJob.getInstance().getMessageManager().danger("You have declined " + ChatColor.GOLD + OddJob.getInstance().getPlayerManager().getName(joiningPlayer) + ChatColor.RESET + " entrance to guild!", joiningPlayer, true);
        //for (UUID member : OddJob.getInstance().getGuildManager().getGuildMembers(guildToJoin)) {
        for (UUID member : guilds.get(guildToJoin).getMembers().keySet()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(member);
            if (op.isOnline()) {
                OddJob.getInstance().getMessageManager().warning("Request from " + ChatColor.GOLD + OddJob.getInstance().getPlayerManager().getName(joiningPlayer) + ChatColor.RESET + " to join " + ChatColor.DARK_AQUA + getGuildNameByUUID(guildToJoin) + ChatColor.RESET + " has been declined", op.getUniqueId(), false);
            }
        }
    }

    public int getGuildCountClaims(UUID guild) {
        //return OddJob.getInstance().getMySQLManager().getGuildCountClaims(guild);
        return this.guilds.get(guild).getChunks();
    }

    // NEW
    public List<UUID> getGuildPendings(UUID guild) {
        //return OddJob.getInstance().getMySQLManager().getGuildPendings(guild);
        List<UUID> list = new ArrayList<>();
        for (UUID uuid : guildPending.keySet()) {
            if (guildPending.get(uuid).equals(guild))
                list.add(uuid);
        }
        return list;
    }


    public void disband(UUID guild) {
        for (Chunk chunk : chunkGuild.keySet()) {
            if (chunkGuild.get(chunk).equals(guild)) chunkGuild.remove(chunk);
        }
        OddJob.getInstance().getMySQLManager().disbandGuild(guild);
    }
}
