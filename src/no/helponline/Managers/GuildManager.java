package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Guild;
import no.helponline.Utils.Role;
import no.helponline.Utils.Zone;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class GuildManager {
    /**
     * List of Players auto-claiming to a Guild
     */
    private HashMap<UUID, UUID> autoClaim = new HashMap<>();      // PlayerUUID   | GuildUUID

    /**
     * List of Guilds
     */
    private HashMap<UUID, Guild> guilds = new HashMap<>();        // GuildUUID    | GUILD

    /**
     * List of Chunks
     */
    private HashMap<Chunk, UUID> chunkGuild = new HashMap<>();    // Chunk        | GuildUUID

    /**
     * List of Players with a pending invitation to a Guild
     */
    private HashMap<UUID, UUID> guildPending = new HashMap<>();   // PlayerUUID   | GuildUUID

    /**
     * List of Players invited to a Guild
     */
    private HashMap<UUID, UUID> guildInvite = new HashMap<>();    // PlayerUUID   | GuildUUID

    /**
     * List of Players with corresponding Guild
     */
    private HashMap<UUID, UUID> members = new HashMap<>();        // PlayerUUID   | GuildUUID

    public GuildManager() {
    }

    /**
     * Loading the Guild-list from the Database
     */
    public void loadGuilds() {
        guilds = OddJob.getInstance().getMySQLManager().loadGuilds();
    }

    /**
     * Loading the Chunk-list from the Database
     */
    public void loadChunks() {
        chunkGuild = OddJob.getInstance().getMySQLManager().loadChunks();
    }

    /**
     * Saving the Guild-list to the Database
     */
    public void saveGuilds() {
        for (UUID uuid : guilds.keySet()) {
            OddJob.getInstance().getMySQLManager().saveGuild(uuid.toString(), guilds.get(uuid).getName(), guilds.get(uuid).getZone().name(), guilds.get(uuid).getInvitedOnly(), guilds.get(uuid).getFriendlyFire(), guilds.get(uuid).getPermissionInvite().name());
            for (UUID player : guilds.get(uuid).getMembers().keySet()) {
                OddJob.getInstance().getMySQLManager().saveGuildMembers(uuid, player, guilds.get(uuid).getMembers().get(player));
            }
        }
    }

    /**
     * Saving the Chunk-list to the Database
     */
    public void saveChunks() {
        for (Chunk chunk : chunkGuild.keySet()) {
            OddJob.getInstance().getMySQLManager().saveChunks(chunkGuild.get(chunk), chunk.getWorld(), chunk.getX(), chunk.getZ());
        }
    }

    /**
     * Creating a new Guild
     *
     * @param player UUID of the Player
     * @param name String name of the Guild
     * @return Boolean success
     */
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
            // Gives the Player the Guild Scoreboard
            OddJob.getInstance().getScoreManager().guild(p);
        }
        return true;
    }

    /**
     * Returns a list of the Guilds known
     *
     * @return A list of Guild UUIDs
     */
    public Set<UUID> getGuilds() {
        return guilds.keySet();
    }


    /**
     * Triggered autoClaim
     *
     * @param player Player UUID
     * @param chunk  Chunk the Player is in
     */
    public void autoClaim(Player player, Chunk chunk) {
        if (getGuildUUIDByChunk(chunk).equals(getGuildUUIDByZone(Zone.WILD))) {
            // Chunk is not already claimed
            UUID claimingGuild = autoClaim.get(player.getUniqueId());

            // Claim Chunk to Guild
            claim(claimingGuild, chunk, player);

            OddJob.getInstance().getMessageManager().success("Claiming chunk " + ChatColor.GOLD + "X:" + chunk.getX() + " Y:" + chunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " to " + ChatColor.DARK_AQUA + getZoneByGuild(claimingGuild).name(), player, true);
        }
    }

    /**
     * Claiming a Chunk to the specified Guild
     *
     * @param guild UUID of the Guild the Player is claiming the Chunk for
     * @param chunk Chunk to claim to the Guild
     * @param player Player who are claiming
     */
    private void claim(UUID guild, Chunk chunk, Player player) {
        UUID world = player.getWorld().getUID();
        HashMap<UUID, Location> listLocksInWorld = OddJob.getInstance().getMySQLManager().locksInWorld(world);
        for (UUID owner : listLocksInWorld.keySet()) {
            // Loop through all the Locks in this World
            if (!guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()))) {
                // The Player is not in the Guild claiming to
                Location location = listLocksInWorld.get(owner);
                if (chunk.equals(location.getChunk())) {
                    // The Lock is inside the claiming Chunk
                    // Remove the lock
                    OddJob.getInstance().getLockManager().unlock(location);

                    OddJob.getInstance().getMessageManager().warning("Your locked " + location.getBlock().getType().name() + " is inside a claimed area and will be unlocked", owner, false);
                }
            }
        }

        // Adds the Chunk to the Guild
        this.chunkGuild.put(chunk, guild);
        this.guilds.get(guild).addChunks();
    }

    /**
     * Claiming the Players Location Chunk to it's Guild
     *
     * @param player Player to claim the Chunk
     */
    public void claim(Player player) {
        Chunk inChunk = player.getLocation().getChunk();
        UUID playerGuild = getGuildUUIDByMember(player.getUniqueId());
        UUID chunkGuild = getGuildUUIDByChunk(inChunk);

        if (!chunkGuild.equals(getGuildUUIDByZone(Zone.WILD))) {
            // Chunk is already claimed by Guild
            OddJob.getInstance().getMessageManager().danger("This chunk is owned by " + ChatColor.DARK_AQUA + getGuildNameByUUID(getGuildUUIDByChunk(inChunk)), player, false);
        } else {
            // Claiming Chunk to Guild
            claim(playerGuild, inChunk, player);

            OddJob.getInstance().getMessageManager().success("You have claimed " + ChatColor.GOLD + "X:" + inChunk.getX() + " Z:" + inChunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " to " + ChatColor.DARK_AQUA + getGuildNameByUUID(playerGuild), player, true);
        }
    }

    /**
     * UnCLaiming the Players Location Chunk from the Guild
     *
     * @param player Player to unClaim the Chunk for
     */
    public void unClaim(Player player) {
        Chunk inChunk = player.getLocation().getChunk();
        UUID playerGuild = getGuildUUIDByMember(player.getUniqueId());
        UUID chunkGuild = getGuildUUIDByChunk(inChunk);

        if (!chunkGuild.equals(playerGuild)) {
            // Chunk is not by the Players Guild
            OddJob.getInstance().getMessageManager().danger("Sorry, you are not associated with the guild who claimed this chunk", player, false);
        } else {
            // UnClaim the Chunk from Guild
            OddJob.getInstance().getMySQLManager().deleteGuildChunks(playerGuild, inChunk, player);
            this.chunkGuild.remove(player.getLocation().getChunk());
            this.guilds.get(chunkGuild).removeChunks();

            OddJob.getInstance().getMessageManager().success("You have unclaimed " + ChatColor.GOLD + "X:" + inChunk.getX() + " Z:" + inChunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " from " + ChatColor.DARK_AQUA + getGuildNameByUUID(playerGuild), player, true);
        }
    }

    /**
     * Returns the Guild who has claimed the Chunk
     *
     * @param chunk Chunk to check
     * @return UUID of the Guild who has claimed the Chunk
     */
    public UUID getGuildUUIDByChunk(Chunk chunk) {
        return chunkGuild.get(chunk) == null ? OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD) : chunkGuild.get(chunk);
    }

    /**
     * Make Player join a specified Guild
     *
     * @param guild UUID of the Guild to join
     * @param player UUID of the Player joining the Guild
     */
    public void join(UUID guild, UUID player) {
        // Delete the Players invitation to the Guild
        guildInvite.remove(player);

        // Delete the Players pending invitations to Guilds
        guildPending.remove(player);

        // Add a Player to Guild as Member
        guilds.get(guild).getMembers().put(player, Role.Members);
        members.put(player, guild);

        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            // Give the Player the Guild Scoreboard
            OddJob.getInstance().getScoreManager().guild(p);
        }
    }

    /**
     * Return the Guild by searching for a name
     *
     * @param name String name of the Guild
     * @return UUID of the Guild
     */
    public UUID getGuildUUIDByName(String name) {
        for (UUID uuid : guilds.keySet()) {
            // Loop through the Guilds
            if (name.equalsIgnoreCase(ChatColor.stripColor(guilds.get(uuid).getName()))) {
                return uuid;
            }
        }
        return null;
    }

    /**
     * Toggles what Zone/Guild a Player is autoClaiming for
     *
     * @param player Player which is toggling the AutoClaim
     * @param zone Zone the Player is claiming for
     */
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

                // Claiming
                claim(player);
            }
        }
    }

    /**
     * Returns the name of a Guild
     *
     * @param guild UUID of the Guild
     * @return String name of the Guild
     */
    public String getGuildNameByUUID(UUID guild) {
        return guilds.get(guild).getName();
    }

    /**
     * Returns the Guild searching by Zone
     *
     * @param zone Zone
     * @return UUID of the Guild
     */
    public UUID getGuildUUIDByZone(Zone zone) {
        for (UUID uuid : guilds.keySet()) {
            // Looping through the Guilds
            if (zone.equals(guilds.get(uuid).getZone())) {
                return uuid;
            }
        }
        return null;
    }

    /**
     * Return if Player is autoClaiming
     *
     * @param player UUID of the Player
     * @return if the AutoClaim is toggled on for this Player
     */
    public boolean hasAutoClaim(UUID player) {
        return autoClaim.containsKey(player);
    }

    /**
     * Return the Guild searching by a Player
     *
     * @param player UUID of the Player
     * @return UUID of the Guild
     */
    public UUID getGuildUUIDByMember(UUID player) {
        for (UUID uuid : guilds.keySet()) {
            // Looping through the Guilds
            if (guilds.get(uuid).getMembers().containsKey(player)) {
                return uuid;
            }
        }
        return null;
    }

    /**
     * Return the Guild Role a Player has
     *
     * @param player UUID of a Player
     * @return Role in the Guild
     */
    public Role getGuildMemberRole(UUID player) {
        return guilds.get(getGuildUUIDByMember(player)).getMembers().get(player);
    }

    /**
     * Promotes a Player of the Guild
     *
     * @param guildUUID UUID of the Guild
     * @param targetUUID UUID of the Player to change the Role of
     * @return Role the new one
     */
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
            guilds.get(guildUUID).getMembers().put(targetUUID, newRole);
        }
        return newRole;
    }

    /**
     * Demotes a Player of the Guild
     *
     * @param guildUUID UUID of the Guild
     * @param targetUUID UUID of the Player to change the Role of
     * @return Role the new one
     */
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
            guilds.get(guildUUID).getMembers().put(targetUUID, newRole);
        }
        return newRole;
    }

    /**
     * Changes the name of the Guild
     *
     * @param guild UUID of the Guild
     * @param name String new name of the Guild
     */
    public void changeName(UUID guild, String name) {
        guilds.get(guild).setName(name);
    }

    /**
     * Makes the Player leave the Guild
     *
     * @param player UUID of the Player
     */
    public void leave(UUID player) {
        guilds.get(getGuildUUIDByMember(player)).getMembers().remove(player);
        members.remove(player);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            OddJob.getInstance().getScoreManager().clear(p);
        }
    }

    /**
     * Sets a Guilds settings of invitedOnly
     *
     * @param guildUUIDByMember UUID of the Guild
     * @param bol Boolean inviteOnly
     */
    public void changeInvitedOnly(UUID guildUUIDByMember, boolean bol) {
        guilds.get(guildUUIDByMember).setInvitedOnly(bol);
    }

    /**
     * Sets a Guilds setting of friendlyFire
     *
     * @param guildUUIDByMember UUID of the Guild
     * @param bol Boolean friendlyFire
     */
    public void changeFriendlyFire(UUID guildUUIDByMember, boolean bol) {
        guilds.get(guildUUIDByMember).setFriendlyFire(bol);
    }

    /**
     * Kicks a Player from the Guild
     *
     * @param guild UUID of the Guild
     * @param player UUID of the Player
     * @param reason String reason
     */
    public void kickFromGuild(UUID guild, UUID player, String reason) {
        if (guild.equals(getGuildUUIDByMember(player))) {
            leave(player);
            OddJob.getInstance().log("left");
            //TODO print reason
        }
    }

    /**
     * Invites a Player to the Guild
     *
     * @param guild UUID of the Guild
     * @param player UUID of the Player
     */
    public void inviteToGuild(UUID guild, UUID player) {
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
        UUID chunkGuild = getGuildUUIDByChunk(inChunk);

        if (chunkGuild != null && !chunkGuild.equals(getGuildUUIDByZone(Zone.WILD))) {
            // The Chunk is already claimed by a Guild
            OddJob.getInstance().getMessageManager().danger("This chunk is owned by " + ChatColor.DARK_AQUA + getGuildNameByUUID(getGuildUUIDByChunk(inChunk)), player, false);
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
