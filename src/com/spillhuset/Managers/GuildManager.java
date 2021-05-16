package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.GuildSQL;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Enum.Zone;
import com.spillhuset.Utils.Guild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import javax.annotation.Nonnull;
import java.util.*;

public class GuildManager {
    private MarkerSet markerSet = null;
    private HashMap<String, AreaMarker> markers;

    /**
     * List of Players auto-claiming to a Guild
     */
    private final HashMap<UUID, UUID> autoClaim = new HashMap<>();      // PlayerUUID   | GuildUUID

    /**
     * List of Guild UUIDs with Guild
     */
    private HashMap<UUID, Guild> guilds = new HashMap<>();        // GuildUUID    | GUILD

    /**
     * List of Chunks with Guild UUID
     */
    private HashMap<Chunk, UUID> chunks = new HashMap<>();    // Chunk        | GuildUUID

    /**
     * List of Players with a pending invitation to a Guild
     * <p>
     * HashMap<UUID,UUID> Player , Guild
     */
    private HashMap<UUID, UUID> guildPending = new HashMap<>();   // PlayerUUID   | GuildUUID

    /**
     * List of Players UUID invited to a Guild UUID
     */
    private HashMap<UUID, UUID> guildInvite = new HashMap<>();    // PlayerUUID   | GuildUUID

    public GuildManager() {
        dynmapInit();
    }

    private void dynmapInit() {
        markers = new HashMap<>();
        try {
            DynmapAPI dynmapApi = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
            Set<MarkerIcon> markerIcon;
            if (dynmapApi != null) {
                MarkerAPI markerApi = dynmapApi.getMarkerAPI();
                if (markerApi != null) {
                    markerIcon = markerApi.getMarkerIcons();
                    markerSet = markerApi.createMarkerSet("guilds", "guilds", markerIcon, false);
                }
            }
        } catch (Exception e) {
            OddJob.getInstance().getMessageManager().console("Map Marker disabled");
        }
    }

    /**
     * Getting the Guild by its UUID
     *
     * @param guild UUID of the Guild
     * @return Guild or NULL
     */
    public Guild getGuild(UUID guild) {
        return guilds.get(guild);
    }

    /**
     * Loading the Guild-list from the Database
     */
    public void loadGuilds() {
        guilds = GuildSQL.loadGuilds();
        if (guilds.size() == 0) {
            create("WildZone", Zone.WILD, false, false);
            create("SafeZone", Zone.SAFE, false, false);
            create("JailZone", Zone.JAIL, false, false);
            create("WarZone", Zone.WAR, false, false);
            create("ArenaZone", Zone.ARENA, false, false);
            saveGuilds();
            loadGuilds();
        }
        guildPending = GuildSQL.loadGuildsPending();
        guildInvite = GuildSQL.loadGuildsInvites();
    }

    /**
     * Loading the Chunk-list from the Database
     */
    public void loadChunks() {
        chunks.clear();
        chunks = GuildSQL.loadChunks();
        updateDynmap();
    }

    private void clearDynmap() {
        for (String s : markers.keySet()) {
            markers.get(s).deleteMarker();
        }
    }

    private void updateDynmap() {
        for (Chunk chunk : chunks.keySet()) {
            updateDynmapChunk(chunk);
        }
    }

    private void removeDynmapChunk(Chunk chunk) {
        String coords = chunk.getX() + "x" + chunk.getZ();
        UUID worldUUID = chunk.getWorld().getUID();
        String markerId = worldUUID.toString().substring(0, 8) + "-" + coords;

        AreaMarker areaMarker = markers.get(markerId);
        if (areaMarker != null) areaMarker.deleteMarker();
        markers.remove(markerId);
    }

    /**
     * Updates the Dynmap with claimed Chunks
     */
    public void updateDynmapChunk(Chunk chunk) {
        String coords = chunk.getX() + "x" + chunk.getZ();
        UUID worldUUID = chunk.getWorld().getUID();
        UUID guildUUID = getGuildUUIDByChunk(chunk);
        String markerId = worldUUID.toString().substring(0, 8) + "-" + coords;

        AreaMarker areaMarker = markers.get(markerId);
        if (areaMarker == null)
            areaMarker = markerSet.createAreaMarker(markerId, getGuildNameByUUID(guildUUID), true, chunk.getWorld().getName(), new double[1000], new double[1000], false);
        double[] x = {chunk.getX() * 16, (chunk.getX() * 16) + 16};
        double[] z = {chunk.getZ() * 16, (chunk.getZ() * 16) + 16};
        areaMarker.setCornerLocations(x, z);
        areaMarker.setLabel(OddJob.getInstance().getGuildManager().getGuildNameByUUID(guildUUID));
        switch (OddJob.getInstance().getGuildManager().getZoneByGuild(guildUUID)) {
            case SAFE: {
                areaMarker.setFillStyle(0.5, Integer.parseInt("00FF00", 16));
                areaMarker.setLineStyle(1, 1.0, Integer.parseInt("00FF00", 16));
            }
            break;
            case ARENA: {
                areaMarker.setFillStyle(0.5, Integer.parseInt("FFFF00", 16));
                areaMarker.setLineStyle(1, 1.0, Integer.parseInt("FF0000", 16));
            }
            break;
            case JAIL:
            case WAR: {
                areaMarker.setFillStyle(0.5, Integer.parseInt("FF0000", 16));
                areaMarker.setLineStyle(1, 1.0, Integer.parseInt("FF0000", 16));
            }
            default: {
                areaMarker.setFillStyle(0.5, Integer.parseInt("FF00FF", 16));
                areaMarker.setLineStyle(1, 1.0, Integer.parseInt("FF00FF", 16));
            }
        }
        markers.put(markerId, areaMarker);
    }

    /**
     * Saving the Guild-list to the Database
     */
    public void saveGuilds() {
        GuildSQL.saveGuilds(guilds);
    }

    /**
     * Saving the Chunk-list to the Database
     */
    public void saveChunks() {
        GuildSQL.saveChunks(chunks);
    }

    /**
     * Creating a new Guild
     *
     * @param player UUID of the Player
     * @param name   String name of the Guild
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
        guilds.put(guild, new Guild(
                name,
                Zone.GUILD,
                guild,
                false,
                false,
                player,
                Role.Master));

        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            // Gives the Player the Guild Scoreboard
            //TODO OddJob.getInstance().getScoreManager().guild(p);
        }
        return true;
    }

    /**
     * Returns a list of the Guilds known
     *
     * @return A list of Guilds UUID and GUILD
     */
    public HashMap<UUID, Guild> getGuilds() {
        return guilds;
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
        }
    }

    /**
     * Claiming a Chunk to the specified Guild
     *
     * @param guild  UUID of the Guild the Player is claiming the Chunk for
     * @param chunk  Chunk to claim to the Guild
     * @param player Player who are claiming
     */
    public void claim(UUID guild, Chunk chunk, Player player) {
        if (guild == null || guild.equals(getGuildUUIDByZone(Zone.WILD))) return;
        //TODO LOCKS REMOVE
        /*
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
        }*/

        // Adds the Chunk to the Guild
        if (!chunks.containsKey(chunk)) {
            chunks.put(chunk, guild);
            GuildSQL.createGuildClaim(chunk, guild);
            updateDynmapChunk(chunk);
            OddJob.getInstance().getMessageManager().guildClaiming(chunk.getX(), chunk.getZ(), player, getGuildNameByUUID(guild));
        } else {
            OddJob.getInstance().getMessageManager().guildClaimed(player);
        }
    }

    /**
     * Claiming the Players Location Chunk to it's Guild
     *
     * @param player Player to claim the Chunk
     * @param guild  Zone of the Chunk
     */
    public void claim(Player player, UUID guild) {
        Chunk inChunk = player.getLocation().getChunk();
        UUID chunkGuild = getGuildUUIDByChunk(inChunk);

        if (chunkGuild != null && !chunkGuild.equals(getGuildUUIDByZone(Zone.WILD))) {
            // Chunk is already claimed by Guild
            OddJob.getInstance().getMessageManager().guildOwnedBy(getGuildNameByUUID(getGuildUUIDByChunk(inChunk)), player);
        } else {
            // Claiming Chunk to Guild
            claim(guild, inChunk, player);
        }
    }

    /**
     * UnClaiming the Players Location Chunk from the Guild
     *
     * @param player Player to unClaim the Chunk for
     */
    public void unClaim(@Nonnull Player player) {
        Chunk inChunk = player.getLocation().getChunk();
        UUID playerGuild = getGuildUUIDByMember(player.getUniqueId());
        UUID chunkGuild = getGuildUUIDByChunk(inChunk);

        if (!chunkGuild.equals(playerGuild)) {
            // Chunk is not by the Players Guild
            OddJob.getInstance().getMessageManager().guildNotAssociatedGuild(player);
        } else {
            // UnClaim the Chunk from Guild
            unClaim(inChunk, player);
        }
    }


    /**
     * UnClaiming known Chunk
     *
     * @param chunk  Chunk to unClaim
     * @param player Player to message
     */
    public void unClaim(@Nonnull Chunk chunk, Player player) {
        // UnClaim the Chunk
        if (GuildSQL.deleteGuildsChunks(chunk)) {
            OddJob.getInstance().getGuildManager().chunks.remove(chunk);
            removeDynmapChunk(chunk);
            if (player != null)
                OddJob.getInstance().getMessageManager().guildUnclaimed(chunk.getX(), chunk.getZ(), player);
        } else
            OddJob.getInstance().getMessageManager().guildNotClaimed(player);
    }


    /**
     * Returns the Guild who has claimed the Chunk
     *
     * @param chunk Chunk to check
     * @return UUID of the Guild who has claimed the Chunk
     */
    public UUID getGuildUUIDByChunk(Chunk chunk) {
        UUID uuid = GuildSQL.getGuildUUIDByChunk(chunk);
        return uuid != null ? uuid : getGuildUUIDByZone(Zone.WILD);
        //return chunks.getOrDefault(chunk, getGuildUUIDByZone(Zone.WILD));
    }

    /**
     * Make Player join a specified Guild
     *
     * @param guild  UUID of the Guild to join
     * @param player UUID of the Player joining the Guild
     */
    public void join(UUID guild, UUID player) {
        // Delete the Players invitation to the Guild
        guildInvite.remove(player);

        // Delete the Players pending invitations to Guilds
        guildPending.remove(player);

        // Add a Player to Guild as Member
        guilds.get(guild).getMembers().put(player, Role.Members);

        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            // Give the Player the Guild Scoreboard
            //TODO OddJob.getInstance().getScoreManager().guild(p);
        }
    }

    /**
     * Return the Guild by searching for a name
     *
     * @param name String name of the Guild
     * @return UUID of the Guild
     */
    public UUID getGuildUUIDByName(@Nonnull String name) {
        UUID guild = null;
        for (UUID uuid : guilds.keySet()) {
            // Loop through the Guilds
            if (name.equalsIgnoreCase(ChatColor.stripColor(guilds.get(uuid).getName()))) {
                guild = uuid;
            }
        }
        return guild;
    }

    /**
     * Toggles what Zone/Guild a Player is autoClaiming for
     *
     * @param player Player which is toggling the AutoClaim
     * @param guild  Zone the Player is claiming for
     */
    public void toggleAutoClaim(@Nonnull Player player, @Nonnull UUID guild) {
        if (autoClaim.containsKey(player.getUniqueId())) {
            if (!guild.equals(autoClaim.get(player.getUniqueId()))) {
                // Changing Zone to autoClaim for
                autoClaim.put(player.getUniqueId(), guild);
                OddJob.getInstance().getMessageManager().guildChangingZone(getGuildNameByUUID(guild), player);
            } else {
                // Stops autoClaim
                autoClaim.remove(player.getUniqueId());
                OddJob.getInstance().getMessageManager().guildAutoOff(getGuildNameByUUID(guild), player);
            }
        } else {
            // Starts autoClaim
            autoClaim.put(player.getUniqueId(), guild);
            OddJob.getInstance().getMessageManager().guildAutoOn(getGuildNameByUUID(guild), player);
        }

    }

    /**
     * Returns the name of a Guild
     *
     * @param guild UUID of the Guild
     * @return String name of the Guild
     */
    public String getGuildNameByUUID(UUID guild) {
        if (guild == null) OddJob.getInstance().log("Guild = null");
        return guilds.get(guild).getName();
    }

    /**
     * Returns the Guild searching by Zone
     *
     * @param zone Zone
     * @return UUID of the Guild
     */
    public UUID getGuildUUIDByZone(Zone zone) {
        UUID guild = null;
        for (UUID uuid : guilds.keySet()) {
            // Looping through the Guilds
            if (zone.equals(guilds.get(uuid).getZone())) {
                guild = uuid;
            }
        }
        return guild;
    }

    /**
     * Return if Player is autoClaiming
     *
     * @param player UUID of the Player
     * @return if the AutoClaim is toggled on for this Player
     */
    public boolean hasAutoClaim(@Nonnull UUID player) {
        return autoClaim.get(player) != null;
    }

    /**
     * Return the Guild searching by a Player
     *
     * @param player UUID of the Player
     * @return UUID of the Guild
     */
    public UUID getGuildUUIDByMember(@Nonnull UUID player) {
        UUID guild = null;
        for (UUID uuid : guilds.keySet()) {
            // Looping through the Guilds
            if (guilds.get(uuid).getMembers().get(player) != null) {
                guild = uuid;
            }
        }
        return guild;
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
     * @param guildUUID  UUID of the Guild
     * @param targetUUID UUID of the Player to change the Role of
     * @param master
     * @return Role the new one
     */
    public Role promoteMember(@Nonnull UUID guildUUID, @Nonnull UUID targetUUID, Role master) {
        Role prevRole = getGuildMemberRole(targetUUID);
        Role newRole = null;
        if (master == null) {
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
        } else {
            guilds.get(guildUUID).getMembers().put(targetUUID, Role.Master);
        }
        return newRole;
    }

    /**
     * Demotes a Player of the Guild
     *
     * @param guildUUID  UUID of the Guild
     * @param targetUUID UUID of the Player to change the Role of
     * @return Role the new one
     */
    public Role demoteMember(@Nonnull UUID guildUUID, @Nonnull UUID targetUUID) {
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
     * @param name  String new name of the Guild
     */
    public void changeName(@Nonnull UUID guild, @Nonnull String name) {
        guilds.get(guild).setName(name);
    }

    /**
     * Makes the Player leave the Guild
     *
     * @param player UUID of the Player
     */
    public void leave(@Nonnull UUID player) {
        Role role = OddJob.getInstance().getGuildManager().getGuildMemberRole(player);
        Guild guild = getGuild(getGuildUUIDByMember(player));
        UUID next = null;
        if (role.equals(Role.Master)) {
            for (UUID member : guild.getMembers().keySet()) {
                if (member.equals(player)) continue;
                if (next == null) {
                    next = member;
                }
            }
        }

        guilds.get(getGuildUUIDByMember(player)).getMembers().remove(player);
        GuildSQL.deleteGuildMember(player);
        if (next != null) {
            guild.setMaster(next);
            OddJob.getInstance().getMessageManager().guildNewMaster(guild, player, next);
        }
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            OddJob.getInstance().getScoreManager().clear(p);
        }
    }

    /**
     * Sets a Guilds settings of invitedOnly
     *
     * @param guildUUIDByMember UUID of the Guild
     * @param bol               Boolean inviteOnly
     */
    public void changeInvitedOnly(@Nonnull UUID guildUUIDByMember, boolean bol) {
        guilds.get(guildUUIDByMember).setInvitedOnly(bol);
    }

    /**
     * Sets a Guilds setting of friendlyFire
     *
     * @param guildUUIDByMember UUID of the Guild
     * @param bol               Boolean friendlyFire
     */
    public void changeFriendlyFire(UUID guildUUIDByMember, boolean bol) {
        guilds.get(guildUUIDByMember).setFriendlyFire(bol);
    }

    /**
     * Kicks a Player from the Guild
     *
     * @param guild  UUID of the Guild
     * @param player UUID of the Player
     * @param reason String reason
     */
    public void kickFromGuild(UUID guild, UUID target, UUID player, String reason) {
        leave(player);
        OddJob.getInstance().getMessageManager().guildKickPlayer(getGuild(guild), target, player, reason);
    }

    /**
     * Invites a Player to the Guild
     *
     * @param guild  UUID of the Guild
     * @param player UUID of the Player
     */
    public void inviteToGuild(UUID guild, UUID player, UUID sender) {
        guildInvite.put(player, guild);
        OddJob.getInstance().getMessageManager().guildInvitedToGuild(getGuild(guild), player, sender);
    }

    /**
     * Removes an Guild invitation
     *
     * @param player UUID of the Player
     */
    public void unInviteToGuild(UUID player) {
        guildInvite.remove(player);
    }

    /**
     * Return the Zone a Guild is associated with
     *
     * @param guild UUID of the Guild
     * @return Zone
     */
    @Nonnull
    public Zone getZoneByGuild(UUID guild) {
        if (guild == null) return Zone.WILD;
        return guilds.get(guild).getZone();
    }

    /**
     * Return the Role which are allowed to invite Players to the Guild
     *
     * @param guild UUID of the Guild
     * @return Role
     */
    public Role getGuildPermissionInvite(UUID guild) {
        return guilds.get(guild).getPermissionInvite();
    }

    /**
     * Return the Guild a Player has an invitation to
     *
     * @param player UUID of the Player
     * @return UUID of the Guild
     */
    public UUID getGuildInvitation(UUID player) {
        return guildInvite.get(player);
    }

    /**
     * Return the Guild a Player has an pending invitation to
     *
     * @param player UUID of the Player
     * @return UUID of the Guild
     */
    public UUID getGuildPending(UUID player) {
        return guildPending.get(player);
    }

    /**
     * return a List of Guild UUIDs which a Player could join
     * Guild must have as OPEN
     *
     * @param player UUID of the Player
     * @return Set of Guild UUIDs
     */
    public List<String> listGuildsToJoin(UUID player) {
        UUID invited = getGuildInvitation(player);
        List<String> ret = new ArrayList<>();
        for (UUID guild : guilds.keySet()) {
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

    /**
     * Return if the Guild is OPEN for joining without invitation
     *
     * @param guild UUID of the Guild
     * @return Boolean
     */
    public boolean isGuildOpen(UUID guild) {
        return !guilds.get(guild).getInvitedOnly();
    }

    /**
     * Return a List of Players with invitation to the Guild
     *
     * @param guild UUID of the Guild
     * @return List
     */
    public List<UUID> getGuildInvitations(UUID guild) {
        List<UUID> list = new ArrayList<>();
        for (UUID uuid : guildInvite.keySet()) {
            if (guildInvite.get(uuid).equals(guild))
                list.add(uuid);
        }
        return list;
    }

    /**
     * Adds a new pending invitation from a Player to join the Guild
     *
     * @param guild  UUID of the Guild
     * @param player UUID of the Player
     */
    public void addGuildPending(UUID guild, UUID player) {
        guildPending.put(player, guild);
    }

    /**
     * Return a Set of Players UUIDs which are members of the Guild
     *
     * @param guild UUID of the Guild
     * @return Set
     */
    public Set<UUID> getGuildMembers(UUID guild) {
        return guilds.get(guild).getMembers().keySet();
    }

    /**
     * Creates a Guild by the name of the Zone
     * No Members of the Guild!
     *
     * @param name          String name of the Guild
     * @param zone          Zone the Guild is associated to
     * @param invited_only  Boolean
     * @param friendly_fire Boolean
     */
    public void create(String name, Zone zone, boolean invited_only, boolean friendly_fire) {
        UUID guild = UUID.randomUUID();
        guilds.put(guild, new Guild(name, zone, guild, invited_only, friendly_fire));
    }

    /**
     * Makes the Pending Player join the Guild
     *
     * @param joiningPlayer UUID of the Player
     */
    public void acceptPending(UUID joiningPlayer, UUID uuid) {
        UUID guild = getGuildPending(joiningPlayer);
        join(guild, joiningPlayer);
        OddJob.getInstance().getMessageManager().guildAcceptPending(getGuild(guild), joiningPlayer, uuid);
    }


    public void acceptInvite(CommandSender sender) {
        Player p = (Player) sender;
        UUID guild = getGuildInvitation(p.getUniqueId());
        join(guild, p.getUniqueId());
        OddJob.getInstance().getMessageManager().guildAcceptInvite(getGuild(guild), sender);
    }

    /**
     * Return a List of pending invitations to the Guild
     *
     * @param guild UUID of the Guild
     * @return List
     */
    public List<UUID> getGuildPendingList(UUID guild) {
        List<UUID> list = new ArrayList<>();
        for (UUID uuid : guildPending.keySet()) {
            if (guildPending.get(uuid).equals(guild))
                list.add(uuid);
        }
        return list;
    }


    /**
     * Disband the Guild and freeing owned Chunks
     *
     * @param guild UUID of the Guild
     */
    public void disband(UUID guild) {
        List<Chunk> chunkList = new ArrayList<>();
        for (Chunk chunk : chunks.keySet()) {
            if (chunks.get(chunk).equals(guild)) {
                unClaim(chunk, null);
                chunkList.add(chunk);
            }
        }
        for (Chunk chunk : chunkList) chunks.remove(chunk);
        chunkList.clear();
        guilds.remove(guild);
        GuildSQL.disbandGuild(guild);
    }

    public void changeGuildMaster(UUID target, UUID uuid) {
        Guild guild = getGuild(getGuildUUIDByMember(uuid));
        guild.getMembers().put(target, Role.Master);
        guild.getMembers().put(uuid, Role.Admins);
    }

    public void denyRequest(UUID uuid) {
        guildPending.remove(uuid);
    }

    public void denyInvite(UUID uuid) {
        guildInvite.remove(uuid);
    }

    public void map(Player player) {
        StringBuilder sb = new StringBuilder();
        int minX = 0, maxX = 0, minZ = 0, maxZ = 0;
        BlockFace facing = player.getFacing();
        String dir;
        Chunk chunk = player.getLocation().getChunk();
        World world = player.getWorld();
        switch (facing) {
            case NORTH:
            case NORTH_NORTH_EAST:
            case NORTH_NORTH_WEST:
            case NORTH_EAST:
            case NORTH_WEST:
                dir = "north";
                minZ = chunk.getZ() - 4;
                maxZ = chunk.getZ() + 4;
                minX = chunk.getX() - 9;
                maxX = chunk.getX() + 9;
                break;
            case EAST:
            case EAST_NORTH_EAST:
            case EAST_SOUTH_EAST:
                dir = "east";
                minZ = chunk.getZ() - 9;
                maxZ = chunk.getZ() + 9;
                minX = chunk.getX() - 4;
                maxX = chunk.getX() + 4;
                break;
            case SOUTH_EAST:
            case SOUTH:
            case SOUTH_SOUTH_EAST:
            case SOUTH_SOUTH_WEST:
            case SOUTH_WEST:
                dir = "south";
                minZ = chunk.getZ() - 4;
                maxZ = chunk.getZ() + 4;
                minX = chunk.getX() - 9;
                maxX = chunk.getX() + 9;
                break;
            case WEST:
            case WEST_NORTH_WEST:
            case WEST_SOUTH_WEST:
                dir = "west";
                minZ = chunk.getZ() - 9;
                maxZ = chunk.getZ() + 9;
                minX = chunk.getX() - 4;
                maxX = chunk.getX() + 4;
                break;
            default:
                dir = "";
        }

        String[] chars = new String[]{"W", "A", "J", "S", "#", "%", "&", "/", "+", "-", ":", "!"};
        int c = 4;
        HashMap<UUID, Integer> g = new HashMap<>();
        switch (dir) {
            case "north":
                for (int z = minZ; z <= maxZ; z++) {
                    //OddJob.getInstance().getMessageManager().console("Z:" + z);
                    if (z == minX + 3) sb.append("N ");
                    else if (z == minX + 4) sb.append("^ ");
                    else if (z == minX + 5) sb.append("|| ");
                    else sb.append("  ");
                    for (int x = minX; x <= maxX; x++) {
                        //OddJob.getInstance().getMessageManager().console("Z:" + z);
                        Chunk test = world.getChunkAt(x, z);
                        UUID guild = chunks.get(test);
                        if (chunk.equals(test)) {
                            sb.append(" ").append(ChatColor.GOLD).append("X");
                        } else if (guild != null) {
                            Zone zone = getGuild(guild).getZone();
                            if (!g.containsKey(guild)) {
                                g.put(guild, c);
                                c++;
                            }
                            ChatColor color = color(zone);
                            if (zone != Zone.GUILD) {
                                if (zone.equals(Zone.WILD)) g.put(guild, 0);
                                if (zone.equals(Zone.WAR)) g.put(guild, 1);
                                if (zone.equals(Zone.ARENA)) g.put(guild, 2);
                                if (zone.equals(Zone.JAIL)) g.put(guild, 3);
                                if (zone.equals(Zone.SAFE)) g.put(guild, 4);
                                sb.append(" ").append(color(zone)).append(chars[g.get(guild)]);
                            } else {
                                if (!g.containsKey(guild)) g.put(guild, c);
                                sb.append(" ").append(color).append(chars[g.get(guild)]);
                            }
                        } else sb.append(" ").append(color(Zone.WILD)).append("O");
                    }
                    sb.append("\n");
                }
                break;
            case "south":
                for (int z = maxZ; z >= minZ; z--) {
                    //OddJob.getInstance().getMessageManager().console("Z:" + z);
                    if (z == minZ + 5) sb.append("S ");
                    else if (z == minZ + 4) sb.append("^ ");
                    else if (z == minZ + 3) sb.append("|| ");
                    else sb.append("  ");
                    for (int x = maxX; x >= minX; x--) {
                        //OddJob.getInstance().getMessageManager().console("Z:" + z);
                        Chunk test = world.getChunkAt(x, z);
                        UUID guild = chunks.get(test);
                        if (chunk.equals(test)) {
                            sb.append(" ").append(ChatColor.GOLD).append("X");
                        } else if (guild != null) {
                            Zone zone = getGuild(guild).getZone();
                            if (!g.containsKey(guild)) {
                                g.put(guild, c);
                                c++;
                            }
                            ChatColor color = color(zone);
                            if (zone != Zone.GUILD) {
                                if (zone.equals(Zone.WILD)) g.put(guild, 0);
                                if (zone.equals(Zone.WAR)) g.put(guild, 1);
                                if (zone.equals(Zone.ARENA)) g.put(guild, 2);
                                if (zone.equals(Zone.JAIL)) g.put(guild, 3);
                                if (zone.equals(Zone.SAFE)) g.put(guild, 4);
                                sb.append(" ").append(color(zone)).append(chars[g.get(guild)]);
                            } else {
                                if (!g.containsKey(guild)) g.put(guild, c);
                                sb.append(" ").append(color).append(chars[g.get(guild)]);
                            }
                        } else sb.append(" ").append(color(Zone.WILD)).append("O");
                    }
                    sb.append("\n");
                }
                break;
            case "east":
                for (int x = maxX; x >= minX; x--) {
                    //OddJob.getInstance().getMessageManager().console("X:" + x);
                    if (x == minX + 5) sb.append("E ");
                    else if (x == minX + 4) sb.append("^ ");
                    else if (x == minX + 3) sb.append("|| ");
                    else sb.append("  ");
                    for (int z = minZ; z <= maxZ; z++) {
                        //OddJob.getInstance().getMessageManager().console("Z:" + z);
                        Chunk test = world.getChunkAt(x, z);
                        UUID guild = chunks.get(test);
                        if (chunk.equals(test)) {
                            sb.append(" ").append(ChatColor.GOLD).append("X");
                        } else if (guild != null) {
                            Zone zone = getGuild(guild).getZone();
                            if (!g.containsKey(guild)) {
                                g.put(guild, c);
                                c++;
                            }
                            ChatColor color = color(zone);
                            if (zone != Zone.GUILD) {
                                if (zone.equals(Zone.WILD)) g.put(guild, 0);
                                if (zone.equals(Zone.WAR)) g.put(guild, 1);
                                if (zone.equals(Zone.ARENA)) g.put(guild, 2);
                                if (zone.equals(Zone.JAIL)) g.put(guild, 3);
                                if (zone.equals(Zone.SAFE)) g.put(guild, 4);
                                sb.append(" ").append(color(zone)).append(chars[g.get(guild)]);
                            } else {
                                if (!g.containsKey(guild)) g.put(guild, c);
                                sb.append(" ").append(color).append(chars[g.get(guild)]);
                            }
                        } else sb.append(" ").append(color(Zone.WILD)).append("O");
                    }
                    sb.append("\n");
                }
                break;
            case "west":
                for (int x = minX; x <= maxX; x++) {
                    //OddJob.getInstance().getMessageManager().console("X:" + x);
                    if (x == minX + 3) sb.append("W ");
                    else if (x == minX + 4) sb.append("^ ");
                    else if (x == minX + 5) sb.append("|| ");
                    else sb.append("  ");
                    for (int z = maxZ; z >= minZ; z--) {
                        //OddJob.getInstance().getMessageManager().console("Z:" + z);
                        Chunk test = world.getChunkAt(x, z);
                        UUID guild = chunks.get(test);
                        if (chunk.equals(test)) {
                            sb.append(" ").append(ChatColor.GOLD).append("X");
                        } else if (guild != null) {
                            Zone zone = getGuild(guild).getZone();
                            if (!g.containsKey(guild)) {
                                g.put(guild, c);
                                c++;
                            }
                            ChatColor color = color(zone);
                            if (zone != Zone.GUILD) {
                                if (zone.equals(Zone.WILD)) g.put(guild, 0);
                                if (zone.equals(Zone.WAR)) g.put(guild, 1);
                                if (zone.equals(Zone.ARENA)) g.put(guild, 2);
                                if (zone.equals(Zone.JAIL)) g.put(guild, 3);
                                if (zone.equals(Zone.SAFE)) g.put(guild, 4);
                                sb.append(" ").append(color(zone)).append(chars[g.get(guild)]);
                            } else {
                                if (!g.containsKey(guild)) g.put(guild, c);
                                sb.append(" ").append(color).append(chars[g.get(guild)]);
                            }
                        } else sb.append(" ").append(color(Zone.WILD)).append("O");
                    }
                    sb.append("\n");
                }
                break;
        }

        sb.append("Legend) ");
        sb.append(ChatColor.GOLD).append("X").append(ChatColor.RESET).append(": You are here; ");
        for (UUID uuid : g.keySet()) {
            Zone zone = getGuild(uuid).getZone();
            ChatColor color = color(zone);
            sb.append(color).append(chars[g.get(uuid)]).append(ChatColor.RESET).append("=").append(getGuildNameByUUID(uuid)).append("; ");
        }

        OddJob.getInstance().getMessageManager().guildMap(sb.toString(), player);
    }

    public ChatColor color(Zone zone) {
        switch (zone) {
            case WAR:
            case ARENA:
                return ChatColor.RED;
            case JAIL:
                return ChatColor.YELLOW;
            case SAFE:
                return ChatColor.GREEN;
            case GUILD:
                return ChatColor.BLUE;
            default:
                return ChatColor.RESET;
        }
    }

    public void listGuilds() {
        List<String> list = new ArrayList<>();
        for (UUID guildUUID : getGuilds().keySet()) {
            ChatColor c = ChatColor.YELLOW;
            Guild guild = getGuild(guildUUID);
            if (guild.getInvitedOnly()) c = ChatColor.RED;
            if (guild.getOpen()) c = ChatColor.GREEN;
            list.add(c + guild.getName());
        }
    }

    public int getOnline(UUID guild) {
        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()) == guild) i++;
        }
        return i;
    }

    public void save() {
        saveChunks();
        saveGuilds();
    }

    public void load() {
        loadGuilds();
        loadChunks();
    }

    public int getSumChunks(UUID guild) {

        int i = 0;
        for (UUID uuid : chunks.values()) {
            if (uuid == guild) i++;
        }
        return i;
    }
}
