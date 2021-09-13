package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Enum.Zone;
import com.spillhuset.Utils.Guild;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class GuildSQL extends MySQLManager {
    public static HashMap<UUID, Guild> loadGuilds() {
        HashMap<UUID, Guild> guilds = new HashMap<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds` WHERE `server` = ?");
                preparedStatement.setString(1, OddJob.getInstance().getServerId().toString());
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    HashMap<UUID, Role> members = new HashMap<>();
                    Location location = null;
                    UUID guildUUID = UUID.fromString(resultSet.getString("uuid"));
                    preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_members` WHERE `uuid` = ?");
                    preparedStatement.setString(1, resultSet.getString("uuid"));
                    resultSetSec = preparedStatement.executeQuery();

                    while (resultSetSec.next()) {
                        members.put(UUID.fromString(resultSetSec.getString("player")), Role.valueOf(resultSetSec.getString("role")));
                    }

                    if (!resultSet.getString("world").equals("")) {
                        UUID worldUUID = UUID.fromString(resultSet.getString("world"));
                        World world = Bukkit.getWorld(worldUUID);
                        if (world != null) {
                            location = new Location(world, resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"), resultSet.getFloat("yaw"), resultSet.getFloat("pitch"));
                        }
                    }


                    guilds.put(guildUUID, new Guild(
                            guildUUID,
                            resultSet.getString("name"),
                            Zone.valueOf(resultSet.getString("zone")),
                            resultSet.getInt("invited_only") == 1,
                            resultSet.getInt("friendly_fire") == 1,
                            resultSet.getInt("open") == 1,
                            Role.valueOf(resultSet.getString("permission_invite")),
                            Role.valueOf(resultSet.getString("permission_kick")),
                            members,
                            resultSet.getInt("spawnmobs") == 1,
                            location
                    ));
                    OddJob.getInstance().log(resultSet.getString("name"));
                }
            } else {
                if (oddjobConfig.getConfigurationSection("guilds") != null) {
                    for (String string : oddjobConfig.getConfigurationSection("guilds").getKeys(false)) {
                        UUID uuid = UUID.fromString(string);
                        HashMap<UUID, Role> members = new HashMap<>();
                        Location location = null;
                        OddJob.getInstance().log("here");
                        if (oddjobConfig.getConfigurationSection("guilds." + string + ".members") != null) {

                            for (String player : oddjobConfig.getConfigurationSection("guilds." + string + ".members").getKeys(false)) {
                                OddJob.getInstance().log(player);
                                members.put(UUID.fromString(player), Role.valueOf(oddjobConfig.getString("guilds." + string + ".members." + player)));
                            }
                        }
                        if (!Objects.equals(oddjobConfig.getString("guilds." + string + ".world"), "")) {
                            UUID worldUUID = UUID.fromString(oddjobConfig.getString("guilds." + string + ".world"));
                            World world = Bukkit.getWorld(worldUUID);
                            if (world != null) {
                                location = new Location(world, oddjobConfig.getDouble("guilds." + string + ".x"), oddjobConfig.getDouble("guilds." + string + ".y"), oddjobConfig.getDouble("guilds." + string + ".z"), oddjobConfig.getInt("guilds." + string + ".yaw"), oddjobConfig.getInt("guilds." + string + ".pitch"));
                            }
                        }
                        guilds.put(uuid, new Guild(
                                uuid,
                                oddjobConfig.getString("guilds." + string + ".name"),
                                Zone.valueOf(oddjobConfig.getString("guilds." + string + ".zone")),
                                oddjobConfig.getBoolean("guilds." + string + ".invited_only"),
                                oddjobConfig.getBoolean("guilds." + string + ".friendly_fire"),
                                oddjobConfig.getBoolean("guilds." + string + ".open"),
                                Role.valueOf(oddjobConfig.getString("guilds." + string + ".permission_invite")),
                                Role.valueOf(oddjobConfig.getString("guilds." + string + ".permission_kick")),
                                members,
                                oddjobConfig.getBoolean("guilds." + string + ".spawnmobs"),
                                location
                        ));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().log("Guilds Loaded: " + guilds.size());
        return guilds;
    }

    public static void saveGuild(Guild guild) {
        UUID guildUUID = guild.getGuildUUID();
        String name = guild.getName();
        Zone zone = guild.getZone();
        boolean invitedOnly = guild.getInvitedOnly();
        boolean friendlyFire = guild.getFriendlyFire();
        boolean open = guild.isOpen();
        boolean spawns = guild.getSpawns();
        Role permissionInviteRole = guild.getPermissionInvite();
        Role permissionKickRole = guild.getPermissionKick();
        HashMap<UUID, Role> members = guild.getMembers();
        int maxClaims = guild.getMaxClaims();
        UUID worldUUID = null;
        double x = 0;
        double y = 0;
        double z = 0;
        float yaw = 0;
        float pitch = 0;
        if (guild.getSpawn() != null) {
            Location spawn = guild.getSpawn();
            worldUUID = spawn.getWorld() != null ? spawn.getWorld().getUID() : null;
            x = spawn.getX();
            y = spawn.getY();
            z = spawn.getZ();
            yaw = spawn.getYaw();
            pitch = spawn.getPitch();
        }
        try {
            if (connect()) {

                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds` WHERE `uuid` = ? AND `server` = ?");
                preparedStatement.setString(1, guildUUID.toString());
                preparedStatement.setString(2, OddJob.getInstance().getServerId().toString());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_guilds` SET `name` = ?,`zone` = ?,`invited_only` = ?,`friendly_fire` = ?,`permission_invite` = ?,`permission_kick` = ?,`open` = ? ,`maxclaims` = ?,`world` = ?,`x` = ?,`y` = ?,`z` = ?,`yaw` = ?,`pitch` = ?,`spawnmobs` = ? WHERE `uuid` = ?");
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, zone.name());
                    preparedStatement.setInt(3, invitedOnly ? 1 : 0);
                    preparedStatement.setInt(4, friendlyFire ? 1 : 0);
                    preparedStatement.setString(5, permissionInviteRole.name());
                    preparedStatement.setString(6, permissionKickRole.name());
                    preparedStatement.setInt(7, open ? 1 : 0);
                    preparedStatement.setInt(8, maxClaims);
                    preparedStatement.setString(9, worldUUID != null ? worldUUID.toString() : "");
                    preparedStatement.setDouble(10, x);
                    preparedStatement.setDouble(11, y);
                    preparedStatement.setDouble(12, z);
                    preparedStatement.setFloat(13, yaw);
                    preparedStatement.setFloat(14, pitch);
                    preparedStatement.setInt(15, spawns ? 1 : 0);
                    preparedStatement.setString(16, guildUUID.toString());
                    preparedStatement.executeUpdate();
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds` (`uuid`,`name`,`zone`,`invited_only`,`friendly_fire`,`permission_invite`,`permission_kick`,`open`,`server`,`maxclaims`,`world`,`x`,`y`,`z`,`yaw`,`pitch`,`spawnmobs`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                    preparedStatement.setString(1, guildUUID.toString());
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, zone.name());
                    preparedStatement.setInt(4, invitedOnly ? 1 : 0);
                    preparedStatement.setInt(5, friendlyFire ? 1 : 0);
                    preparedStatement.setString(6, permissionInviteRole.name());
                    preparedStatement.setString(7, permissionKickRole.name());
                    preparedStatement.setInt(8, open ? 1 : 0);
                    preparedStatement.setString(9, OddJob.getInstance().getServerId().toString());
                    preparedStatement.setInt(10, maxClaims);
                    preparedStatement.setString(11, worldUUID != null ? worldUUID.toString() : "");
                    preparedStatement.setDouble(12, x);
                    preparedStatement.setDouble(13, y);
                    preparedStatement.setDouble(14, z);
                    preparedStatement.setFloat(15, yaw);
                    preparedStatement.setFloat(16, pitch);
                    preparedStatement.setInt(17, spawns ? 1 : 0);
                    preparedStatement.execute();
                }

                for (UUID uuid : members.keySet()) {
                    connect();
                    preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_members` WHERE `player` = ?");
                    preparedStatement.setString(1, uuid.toString());
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        if (!UUID.fromString(resultSet.getString("uuid")).equals(guildUUID) || !Role.valueOf(resultSet.getString("role")).equals(members.get(uuid))) {
                            preparedStatement = connection.prepareStatement("UPDATE `mine_guilds_members` SET `uuid` = ?, `role` = ? WHERE `player` = ?");
                            preparedStatement.setString(1, guildUUID.toString());
                            preparedStatement.setString(2, members.get(uuid).name());
                            preparedStatement.setString(3, uuid.toString());
                            preparedStatement.executeUpdate();
                        }
                    } else {
                        preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_members` (`uuid`,`player`,`role`) VALUES (?,?,?)");
                        preparedStatement.setString(1, guildUUID.toString());
                        preparedStatement.setString(2, uuid.toString());
                        preparedStatement.setString(3, members.get(uuid).name());
                        preparedStatement.execute();
                    }
                    close();
                }
            } else {
                oddjobConfig.set("guilds." + guildUUID.toString() + ".name", name);
                oddjobConfig.set("guilds." + guildUUID + ".zone", zone.name());
                oddjobConfig.set("guilds." + guildUUID + ".invited_only", invitedOnly);
                oddjobConfig.set("guilds." + guildUUID + ".friendly_fire", friendlyFire);
                oddjobConfig.set("guilds." + guildUUID + ".permission_invite", permissionInviteRole.name());
                oddjobConfig.set("guilds." + guildUUID + ".permission_kick", permissionKickRole.name());
                oddjobConfig.set("guilds." + guildUUID + ".open", open);
                oddjobConfig.set("guilds." + guildUUID + ".maxclaims", maxClaims);

                for (UUID uuid : members.keySet()) {
                    oddjobConfig.set("guilds." + guildUUID + ".members." + uuid.toString(), members.get(uuid).name());
                }
            }
        } catch (SQLException ignored) {
        } finally {
            close();
        }

    }

    public static boolean deleteGuildsChunks(Chunk chunk) {
        boolean ret = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_chunks` WHERE `world` = ? AND `x` = ? AND `z` = ? ");
            preparedStatement.setString(1, chunk.getWorld().getUID().toString());
            preparedStatement.setInt(2, chunk.getX());
            preparedStatement.setInt(3, chunk.getZ());
            preparedStatement.execute();
            ret = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }

    public static HashMap<Chunk, UUID> loadChunks() {
        HashMap<Chunk, UUID> chunks = new HashMap<>();
        int i = 0;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks` WHERE (`server` = ?)");
                preparedStatement.setString(1, OddJob.getInstance().getConfig().getString("server_unique_id"));
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                    if (world != null) {
                        UUID guild = UUID.fromString(resultSet.getString("uuid"));
                        if (OddJob.getInstance().getGuildManager().getGuild(guild) == null) continue;
                        int x = resultSet.getInt("x");
                        int z = resultSet.getInt("z");
                        Chunk chunk = world.getChunkAt(x, z);
                        chunk.load();
                        chunks.put(chunk, guild);
                        i++;
                    }
                }
            } else {
                // W;X;Z;
                if (oddjobConfig.getConfigurationSection("chunks") != null) {
                    for (String string : oddjobConfig.getConfigurationSection("chunks").getKeys(false)) {

                        String[] strings = string.split(";");
                        World world = Bukkit.getWorld(UUID.fromString(strings[0]));
                        if (world != null) {
                            int x = Integer.parseInt(strings[1]);
                            int z = Integer.parseInt(strings[2]);
                            Chunk chunk = world.getChunkAt(x, z);
                            chunk.load();
                            chunks.put(chunk, UUID.fromString(oddjobConfig.getString("chunks." + string)));
                            i++;
                        }
                    }
                }
            }
        } catch (SQLException ignored) {
        } finally {
            close();
        }
        OddJob.getInstance().log("Guild Chunks Loaded: " + i);
        return chunks;
    }

    public static void disbandGuild(UUID guild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds` WHERE `uuid` = ?");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_members` WHERE `uuid` = ?");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_chunks` WHERE `uuid` = ?");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_pendings` WHERE `uuid` = ?");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_invites` WHERE `uuid` = ?");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.execute();

            OddJob.getInstance().getMessageManager().console("Guild disbanded");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void saveChunks(HashMap<Chunk, UUID> chunks) {
        int i = 0;
        for (Chunk chunk : chunks.keySet()) {
            World world = chunk.getWorld();
            int x = chunk.getX();
            int z = chunk.getZ();
            UUID guild = chunks.get(chunk);

            try {
                if (connect()) {
                    preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks` WHERE `world` = ? AND `x` = ? AND `z` = ? AND `server` = ?");
                    preparedStatement.setString(1, world.getUID().toString());
                    preparedStatement.setInt(2, x);
                    preparedStatement.setInt(3, z);
                    preparedStatement.setString(4, OddJob.getInstance().getServerId().toString());
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        preparedStatement = connection.prepareStatement("UPDATE `mine_guilds_chunks` SET `uuid` = ? WHERE `world` = ? AND `x` = ? AND `z` = ? ");
                        preparedStatement.setString(1, guild.toString());
                        preparedStatement.setString(2, world.getUID().toString());
                        preparedStatement.setInt(3, x);
                        preparedStatement.setInt(4, z);
                        preparedStatement.executeUpdate();
                        i++;
                    } else {
                        preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`uuid`,`world`,`x`,`z`,`server`) VALUES (?,?,?,?,?)");
                        preparedStatement.setString(1, guild.toString());
                        preparedStatement.setString(2, world.getUID().toString());
                        preparedStatement.setInt(3, x);
                        preparedStatement.setInt(4, z);
                        preparedStatement.setString(5, OddJob.getInstance().getServerId().toString());
                        preparedStatement.execute();
                        i++;
                    }
                } else {
                    oddjobConfig.set("guilds_chunks." + world.getUID() + "." + x + "." + z, guild.toString());
                    i++;
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
            OddJob.getInstance().log("Guild Chunks Saved: " + i);
        }
    }

    /**
     * @return HashMap UUID of Player and UUID of Guild
     */
    public static HashMap<UUID, UUID> loadGuildsPending() {
        HashMap<UUID, UUID> pending = new HashMap<>();
        int i = 0;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_pendings`");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    pending.put(UUID.fromString(resultSet.getString("player")), UUID.fromString(resultSet.getString("uuid")));
                    i++;
                }
            } else {
                for (String string : oddjobConfig.getStringList("pending")) {
                    pending.put(UUID.fromString(string), UUID.fromString(oddjobConfig.getString("pending." + string, "")));
                    i++;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().log("Guild Pending Loaded: " + i);
        return pending;
    }

    /**
     * @return HashMap UUID of Player and UUID of Guild
     */
    public static HashMap<UUID, UUID> loadGuildsInvites() {
        int i = 0;
        HashMap<UUID, UUID> invites = new HashMap<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_invites`");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    invites.put(UUID.fromString(resultSet.getString("player")), UUID.fromString(resultSet.getString("uuid")));
                    i++;
                }
            } else {
                for (String string : oddjobConfig.getStringList("invites")) {
                    invites.put(UUID.fromString(string), UUID.fromString(oddjobConfig.getString("invites" + string, "")));
                    i++;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().log("Guild Invites Loaded: " + i);
        return invites;
    }

    public static void deleteGuildMember(UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_members` WHERE `player` = ?");
            preparedStatement.setString(1, player.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static UUID getGuildUUIDByChunk(Chunk chunk) {
        UUID uuid = null;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds_chunks` WHERE `world` = ? AND `x` = ? AND `z` = ? AND `server` = ?");
                preparedStatement.setString(1, chunk.getWorld().getUID().toString());
                preparedStatement.setInt(2, chunk.getX());
                preparedStatement.setInt(3, chunk.getZ());
                preparedStatement.setString(4, OddJob.getInstance().getServerId().toString());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    uuid = UUID.fromString(resultSet.getString("uuid"));
                }
            } else {
                if (oddjobConfig.getConfigurationSection("guild_chunks") != null) {
                    ConfigurationSection cs = oddjobConfig.getConfigurationSection("guild_chunks");
                    String world = chunk.getWorld().getUID().toString();
                    String x = String.valueOf(chunk.getX());
                    String z = String.valueOf(chunk.getX());
                    cs.get(world + "." + x + "." + z + ".uuid");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return uuid;
    }

    public static void createGuildClaim(@Nonnull Chunk chunk, @Nonnull UUID guild) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`x`,`z`,`world`,`uuid`,`server`) VALUES (?,?,?,?,?)");
                preparedStatement.setInt(1, chunk.getX());
                preparedStatement.setInt(2, chunk.getZ());
                preparedStatement.setString(3, chunk.getWorld().getUID().toString());
                preparedStatement.setString(4, guild.toString());
                preparedStatement.setString(5, OddJob.getInstance().getServerId().toString());
                preparedStatement.execute();
            } else {

                oddjobConfig.set("guilds_chunks." + chunk.getWorld().getUID() + "." + chunk.getX() + "." + chunk.getZ(), guild.toString());

                oddjobConfig.save(oddjobConfigFile);
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void addPending(UUID guild, UUID player) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_pendings` (`player`,`uuid`) VALUES (?,?)");
                preparedStatement.setString(1, player.toString());
                preparedStatement.setString(2, guild.toString());
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static void deleteInvite(UUID guild, UUID player) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_invites` WHERE `player` = ? AND `uuid` = ?");
                preparedStatement.setString(1, player.toString());
                preparedStatement.setString(2, guild.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static void deletePending(UUID guild, UUID player) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_pendings` WHERE `player` = ? AND `uuid` = ?");
                preparedStatement.setString(1, player.toString());
                preparedStatement.setString(2, guild.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static UUID getPendingGuild(UUID player) {
        UUID pending = null;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds_pendings` WHERE `player` = ? ");
                preparedStatement.setString(1, player.toString());
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    pending = UUID.fromString(resultSet.getString("uuid"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return pending;
    }

    public static List<UUID> listPendingPlayers(UUID guild) {
        List<UUID> pending = new ArrayList<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT `player` FROM `mine_guilds_pendings` WHERE `uuid` = ? ");
                preparedStatement.setString(1, guild.toString());
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    pending.add(UUID.fromString(resultSet.getString("player")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return pending;
    }

    public static UUID getPendingPlayer(UUID guild) {
        UUID pending = null;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT `player` FROM `mine_guilds_pendings` WHERE `uuid` = ? ");
                preparedStatement.setString(1, guild.toString());
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    pending = UUID.fromString(resultSet.getString("player"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return pending;
    }

    public static void addInvite(UUID player, UUID guild) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_invites` (`player`,`uuid`) VALUES (?,?)");
                preparedStatement.setString(1, player.toString());
                preparedStatement.setString(2, guild.toString());
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static UUID getInvitationGuild(UUID player) {
        UUID pending = null;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds_invites` WHERE `player` = ? ");
                preparedStatement.setString(1, player.toString());
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    pending = UUID.fromString(resultSet.getString("uuid"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return pending;
    }

    public static List<UUID> listInvitedPlayers(UUID guild) {
        List<UUID> pending = new ArrayList<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT `player` FROM `mine_guilds_invites` WHERE `uuid` = ? ");
                preparedStatement.setString(1, guild.toString());
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    pending.add(UUID.fromString(resultSet.getString("player")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return pending;
    }

    public static List<UUID> listInvitingGuilds(UUID playerUUID) {
        List<UUID> pending = new ArrayList<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds_invites` WHERE `player` = ? ");
                preparedStatement.setString(1, playerUUID.toString());
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    pending.add(UUID.fromString(resultSet.getString("uuid")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return pending;
    }

}
