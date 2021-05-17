package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Enum.ScoreBoard;
import com.spillhuset.Utils.Enum.Zone;
import com.spillhuset.Utils.Guild;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class GuildSQL extends MySQLManager {
    public static HashMap<UUID, Guild> loadGuilds() {
        HashMap<UUID, Guild> guilds = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds` WHERE `server` = ?");
            preparedStatement.setString(1, OddJob.getInstance().getServerId().toString());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                OddJob.getInstance().log("Nope--------------------------------");
                HashMap<UUID, Role> members = new HashMap<>();
                UUID guildUUID = UUID.fromString(resultSet.getString("uuid"));
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_members` WHERE `uuid` = ?");
                preparedStatement.setString(1, resultSet.getString("uuid"));
                resultSetSec = preparedStatement.executeQuery();

                while (resultSetSec.next()) {
                    members.put(UUID.fromString(resultSetSec.getString("player")), Role.valueOf(resultSetSec.getString("role")));
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
                        resultSet.getInt("maxclaims")
                ));

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return guilds;
    }

    public static void saveGuilds(HashMap<UUID, Guild> guilds) {
        for (UUID guildUUID : guilds.keySet()) {
            Guild guild = OddJob.getInstance().getGuildManager().getGuild(guildUUID);
            String name = guild.getName();
            Zone zone = guild.getZone();
            boolean invitedOnly = guild.getInvitedOnly();
            boolean friendlyFire = guild.getFriendlyFire();
            boolean open = guild.getOpen();
            Role permissionInviteRole = guild.getPermissionInvite();
            Role permissionKickRole = guild.getPermissionKick();
            HashMap<UUID, Role> members = guild.getMembers();
            int maxClaims = guild.getMaxClaims();

            try {
                connect();
                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds` WHERE `uuid` = ? AND `server` = ?");
                preparedStatement.setString(1, guildUUID.toString());
                preparedStatement.setString(2, OddJob.getInstance().getServerId().toString());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_guilds` SET `name` = ?,`zone` = ?,`invited_only` = ?,`friendly_fire` = ?,`permission_invite` = ?,`permission_kick` = ?,`open` = ? ,`maxclaims` = ? WHERE `uuid` = ?");
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, zone.name());
                    preparedStatement.setInt(3, invitedOnly ? 1 : 0);
                    preparedStatement.setInt(4, friendlyFire ? 1 : 0);
                    preparedStatement.setString(5, permissionInviteRole.name());
                    preparedStatement.setString(6, permissionKickRole.name());
                    preparedStatement.setInt(7, open ? 1 : 0);
                    preparedStatement.setInt(8,maxClaims);
                    preparedStatement.setString(9, guildUUID.toString());
                    preparedStatement.executeUpdate();
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds` (`uuid`,`name`,`zone`,`invited_only`,`friendly_fire`,`permission_invite`,`permission_kick`,`open`,`server`,`maxclaims`) VALUES (?,?,?,?,?,?,?,?,?,?)");
                    preparedStatement.setString(1, guildUUID.toString());
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, zone.name());
                    preparedStatement.setInt(4, invitedOnly ? 1 : 0);
                    preparedStatement.setInt(5, friendlyFire ? 1 : 0);
                    preparedStatement.setString(6, permissionInviteRole.name());
                    preparedStatement.setString(7, permissionKickRole.name());
                    preparedStatement.setInt(8, open ? 1 : 0);
                    preparedStatement.setString(9, OddJob.getInstance().getServerId().toString());
                    preparedStatement.setInt(10,maxClaims);
                    preparedStatement.execute();
                }

                for (UUID uuid : members.keySet()) {
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
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
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
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks` WHERE `server` = ?");
            preparedStatement.setString(1,OddJob.getInstance().getConfig().getString("server_unique_id"));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                if (world != null) {
                    UUID guild = UUID.fromString(resultSet.getString("uuid"));
                    int x = resultSet.getInt("x");
                    int z = resultSet.getInt("z");
                    Chunk chunk = world.getChunkAt(x, z);
                    chunk.load();
                    chunks.put(chunk, guild);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().load("Chunks", chunks.size());
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
        for (Chunk chunk : chunks.keySet()) {

            World world = chunk.getWorld();
            int x = chunk.getX();
            int z = chunk.getZ();
            UUID guild = chunks.get(chunk);

            try {
                connect();
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks` WHERE `world` = ? AND `x` = ? AND `z` = ? AND `server` = ?");
                preparedStatement.setString(1, world.getUID().toString());
                preparedStatement.setInt(2, x);
                preparedStatement.setInt(3, z);
                preparedStatement.setString(4,OddJob.getInstance().getServerId().toString());
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_guilds_chunks` SET `uuid` = ? WHERE `world` = ? AND `x` = ? AND `z` = ? ");
                    preparedStatement.setString(1, guild.toString());
                    preparedStatement.setString(2, world.getUID().toString());
                    preparedStatement.setInt(3, x);
                    preparedStatement.setInt(4, z);
                    preparedStatement.executeUpdate();
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`uuid`,`world`,`x`,`z`,`server`) VALUES (?,?,?,?,?)");
                    preparedStatement.setString(1, guild.toString());
                    preparedStatement.setString(2, world.getUID().toString());
                    preparedStatement.setInt(3, x);
                    preparedStatement.setInt(4, z);
                    preparedStatement.setString(5,OddJob.getInstance().getServerId().toString());
                    preparedStatement.execute();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        }
    }
    /**
     * @return HashMap UUID of Player and UUID of Guild
     */
    public static HashMap<UUID, UUID> loadGuildsPending() {
        HashMap<UUID, UUID> pending = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_pendings`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                pending.put(UUID.fromString(resultSet.getString("player")), UUID.fromString(resultSet.getString("uuid")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return pending;
    }

    /**
     * @return HashMap UUID of Player and UUID of Guild
     */
    public static HashMap<UUID, UUID> loadGuildsInvites() {
        HashMap<UUID, UUID> invites = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_invites`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                invites.put(UUID.fromString(resultSet.getString("player")), UUID.fromString(resultSet.getString("uuid")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
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
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds_chunks` WHERE `world` = ? AND `x` = ? AND `z` = ? AND `server` = ?");
            preparedStatement.setString(1, chunk.getWorld().getUID().toString());
            preparedStatement.setInt(2, chunk.getX());
            preparedStatement.setInt(3, chunk.getZ());
            preparedStatement.setString(4,OddJob.getInstance().getServerId().toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                uuid = UUID.fromString(resultSet.getString("uuid"));
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
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`x`,`z`,`world`,`uuid`,`server`) VALUES (?,?,?,?,?)");
            preparedStatement.setInt(1, chunk.getX());
            preparedStatement.setInt(2, chunk.getZ());
            preparedStatement.setString(3, chunk.getWorld().getUID().toString());
            preparedStatement.setString(4, guild.toString());
            preparedStatement.setString(5,OddJob.getInstance().getServerId().toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

}
