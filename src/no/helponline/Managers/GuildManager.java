package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;

import java.util.*;

public class GuildManager {
    public DynmapAPI dynmapApi;
    public MarkerAPI markerApi;
    public MarkerSet markerset;
    /*DynmapAPI dapi = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
    MarkerAPI mapi = dapi.getMarkerAPI();
    Set<MarkerIcon> micon = mapi.getMarkerIcons();*/
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

    public Guild getGuild(UUID guild) {
        return guilds.get(guild);
    }

    private HashMap<String, TMarker> createHomes() {
        HashMap<String, TMarker> homes = new HashMap<>();
        for (UUID uuid : OddJob.getInstance().getGuildManager().getGuilds()) {
            Location home = OddJob.getInstance().getHomesManager().get(uuid, "faction");
            if (home == null) continue;
            String markerId = "guilds-" + uuid.toString();
            TMarker marker = new TMarker();
            marker.label = OddJob.getInstance().getGuildManager().getGuildNameByUUID(uuid);
            marker.world = home.getWorld().getName();
            marker.x = home.getBlockX();
            marker.y = home.getBlockY();
            marker.z = home.getBlockZ();
            marker.iconName = "greenflag";
            marker.description = OddJob.getInstance().getGuildManager().getGuildNameByUUID(uuid);

            homes.put(markerId, marker);
        }
        return homes;
    }

    private void updateHomes(HashMap<String, TMarker> homes) {
        HashMap<String, Marker> markers = new HashMap<>();
        for (Marker marker : markerset.getMarkers()) {
            markers.put(marker.getMarkerID(), marker);
        }

        for (Map.Entry<String, TMarker> entry : homes.entrySet()) {
            String markerId = entry.getKey();
            TMarker temp = entry.getValue();

            Marker marker = markers.remove(markerId);
            if (marker == null) {
                Optional<Marker> tMarker = temp.create(markerApi, markerset, markerId);
                if (!tMarker.isPresent()) {
                    OddJob.getInstance().getMessageManager().console("error");
                }
                marker = tMarker.get();
            } else temp.update(markerApi, markerset, marker);
        }
        markers.values().forEach(Marker::deleteMarker);
    }

    private HashMap<String, TAreaMarker> createAreas() {
        HashMap<String, HashMap<UUID, Set<Chunk>>> worldChunks = createWorldChunks();
        return null;//createAreas(worldChunks);
    }

    private HashMap<String, HashMap<UUID, Set<Chunk>>> createWorldChunks() {
        HashMap<String, HashMap<UUID, Set<Chunk>>> worldChunks = new HashMap<>();

        for (World world : Bukkit.getWorlds()) {
            String worldName = world.getName();
            if (!worldChunks.containsKey(worldName)) {
                worldChunks.put(worldName, new HashMap<>());
            }

            HashMap<UUID, Set<Chunk>> worldClaims = worldChunks.get(worldName);

            for (UUID uuid : getGuilds()) {
                if (!worldClaims.containsKey(uuid)) {
                    worldClaims.put(uuid, new HashSet<>());
                }

                Set<Chunk> claims = worldClaims.get(uuid);

                //for (Chunk chunk)
            }
        }
        return worldChunks;
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
        try {
            int i = 0;
            HashMap<UUID, List<Chunk>> map = new HashMap<>();
            DynmapAPI dapi = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
            MarkerAPI mapi = dapi.getMarkerAPI();
            Set<MarkerIcon> micon = mapi.getMarkerIcons();
            MarkerSet markerSet = mapi.createMarkerSet("guilds", "guilds", micon, false);

            for (Chunk chunk : chunkGuild.keySet()) {
                UUID uuid = chunkGuild.get(chunk);
                if (!map.containsKey(uuid)) map.put(uuid, new ArrayList<>());
                map.get(uuid).add(chunk);
                i++;
            }
            int c = 0;
            for (UUID uuid : map.keySet()) {
                List<Chunk> chunks = map.get(uuid);
                for (Chunk chunk : chunks) {
                    c++;
                    OddJob.getInstance().getMessageManager().console("x" + chunk.getX() * 16 + "; z" + chunk.getZ() * 16);
                    AreaMarker areaMarker = markerSet.createAreaMarker(uuid.toString().substring(0, 8) + "-" + c, OddJob.getInstance().getGuildManager().getGuildNameByUUID(uuid), true, chunks.get(0).getWorld().getName(), new double[1000], new double[1000], false);
                    double[] x = {chunk.getX() * 16, (chunk.getX() * 16) + 16};
                    double[] z = {chunk.getZ() * 16, (chunk.getZ() * 16) + 16};
                    areaMarker.setCornerLocations(x, z);
                    areaMarker.setLabel(OddJob.getInstance().getGuildManager().getGuildNameByUUID(uuid));
                    switch (OddJob.getInstance().getGuildManager().getZoneByGuild(uuid)) {
                        case SAFE:
                            areaMarker.setFillStyle(0.5, Integer.parseInt("00FF00", 16));
                            areaMarker.setLineStyle(1, 1.0, Integer.parseInt("00FF00", 16));
                            break;
                        case ARENA:
                            areaMarker.setFillStyle(0.5, Integer.parseInt("FFFF00", 16));
                            areaMarker.setLineStyle(1, 1.0, Integer.parseInt("FF0000", 16));
                            break;
                        case JAIL:
                        case WAR:
                            areaMarker.setFillStyle(0.5, Integer.parseInt("FF0000", 16));
                            areaMarker.setLineStyle(1, 1.0, Integer.parseInt("FF0000", 16));
                            break;
                        default:
                            areaMarker.setFillStyle(0.5, Integer.parseInt("0000FF", 16));
                            areaMarker.setLineStyle(1, 1.0, Integer.parseInt("0000FF", 16));
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Saving the Guild-list to the Database
     */
    public void saveGuilds() {
        for (UUID uuid : guilds.keySet()) {
            OddJob.getInstance().getMySQLManager().saveGuild();
            OddJob.getInstance().getMySQLManager().saveGuildMembers();
        }
    }

    /**
     * Saving the Chunk-list to the Database
     */
    public void saveChunks() {
        OddJob.getInstance().getMySQLManager().saveChunks(chunkGuild);
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
     * @param guild  UUID of the Guild the Player is claiming the Chunk for
     * @param chunk  Chunk to claim to the Guild
     * @param player Player who are claiming
     */
    public void claim(UUID guild, Chunk chunk, Player player) {
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
            guilds.get(chunkGuild).removeChunks();
            unClaim(inChunk, player);
        }
    }


    /**
     * UnClaiming known Chunk
     *
     * @param chunk  Chunk to unClaim
     * @param player Player to message
     */
    public void unClaim(Chunk chunk, Player player) {

        // UnClaim the Chunk
        OddJob.getInstance().getMySQLManager().deleteGuildChunks(chunk);
        chunkGuild.remove(chunk);

        if (player != null)
            OddJob.getInstance().getMessageManager().success("You have unclaimed " + ChatColor.GOLD + "X:" + chunk.getX() + " Z:" + chunk.getZ() + " World:" + chunk.getWorld().getName(), player, true);
    }


    /**
     * Returns the Guild who has claimed the Chunk
     *
     * @param chunk Chunk to check
     * @return UUID of the Guild who has claimed the Chunk
     */
    public UUID getGuildUUIDByChunk(Chunk chunk) {
        return chunkGuild.get(chunk) == null ? getGuildUUIDByZone(Zone.WILD) : chunkGuild.get(chunk);
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
     * @param zone   Zone the Player is claiming for
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
        return getGuildUUIDByZone(Zone.WILD);
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
     * @param guildUUID  UUID of the Guild
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
     * @param name  String new name of the Guild
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
     * @param bol               Boolean inviteOnly
     */
    public void changeInvitedOnly(UUID guildUUIDByMember, boolean bol) {
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
     * @param guild  UUID of the Guild
     * @param player UUID of the Player
     */
    public void inviteToGuild(UUID guild, UUID player) {
        guildInvite.put(player, guild);
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
     * @param guild UUID of the GUild
     * @return Zone
     */
    public Zone getZoneByGuild(UUID guild) {
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
     * Claims the Chunk the Player is standing in to the Zone-Guild
     *
     * @param player UUID of the Player
     * @param zone   Zone the Player is claiming to
     */
    public void claim(Player player, Zone zone) {
        Chunk inChunk = player.getLocation().getChunk();
        UUID zoneGuild = getGuildUUIDByZone(zone);
        UUID chunkGuild = getGuildUUIDByChunk(inChunk);

        if (chunkGuild != null && !chunkGuild.equals(getGuildUUIDByZone(Zone.WILD))) {
            // The Chunk is already claimed by a Guild
            OddJob.getInstance().getMessageManager().danger("This chunk is owned by " + ChatColor.DARK_AQUA + getGuildNameByUUID(getGuildUUIDByChunk(inChunk)), player, false);
        } else {
            // Claiming to Zone
            this.chunkGuild.put(inChunk, zoneGuild);
            this.guilds.get(getGuildUUIDByZone(zone)).addChunks();

            OddJob.getInstance().getMessageManager().success("You have claimed " + ChatColor.GOLD + "X:" + inChunk.getX() + " Z:" + inChunk.getZ() + " World:" + player.getWorld().getName() + ChatColor.RESET + " to " + ChatColor.DARK_AQUA + getGuildNameByUUID(zoneGuild), player, true);
        }
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
     * Makes the Player join the Guild
     *
     * @param guildToJoin   UUID of the Guild
     * @param joiningPlayer UUID of the Player
     */
    public void accept(UUID guildToJoin, UUID joiningPlayer) {
        join(guildToJoin, joiningPlayer);

        OddJob.getInstance().getMessageManager().success("Welcome to " + ChatColor.DARK_AQUA + getGuildNameByUUID(guildToJoin) + ChatColor.RESET + " guild!", joiningPlayer, true);
        OddJob.getInstance().getMessageManager().guild("Please welcome " + ChatColor.GOLD + OddJob.getInstance().getPlayerManager().getName(joiningPlayer) + ChatColor.RESET + " to the guild", guildToJoin);
    }

    /**
     * Denies a Player access to the Guild
     *
     * @param guildToJoin   UUID of the Guild
     * @param joiningPlayer UUID of the Player
     */
    public void deny(UUID guildToJoin, UUID joiningPlayer) {
        guildPending.remove(joiningPlayer);
        guildInvite.remove(joiningPlayer);

        OddJob.getInstance().getMessageManager().guild("Request from " + ChatColor.GOLD + OddJob.getInstance().getPlayerManager().getName(joiningPlayer) + ChatColor.RESET + " to join " + ChatColor.DARK_AQUA + getGuildNameByUUID(guildToJoin) + ChatColor.RESET + " has been declined", guildToJoin);
    }

    /**
     * Return the number of Chunks owned by the Guild
     *
     * @param guild UUID of the Guild
     * @return Integer
     */
    public int getGuildCountClaims(UUID guild) {
        return this.guilds.get(guild).getChunks();
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
        for (Chunk chunk : chunkGuild.keySet()) {
            if (chunkGuild.get(chunk).equals(guild)) unClaim(chunk, null);
            chunkGuild.remove(chunk);
        }
        OddJob.getInstance().getMySQLManager().disbandGuild(guild);
    }

    enum Direction {
        XPLUS, XMINUS, ZPLUS, ZMINUS
    }
}
