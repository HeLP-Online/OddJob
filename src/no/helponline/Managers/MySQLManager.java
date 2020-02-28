package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Role;
import no.helponline.Utils.Utility;
import no.helponline.Utils.Zone;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MySQLManager {
    private PreparedStatement preparedStatement = null;
    private PreparedStatement preparedStatementsec = null;
    private Connection connection = null;
    private ResultSet resultSet = null;

    private void connect() throws SQLException {
        if (connection == null)
            connection = DriverManager.getConnection("jdbc:mysql://10.0.4.9/odderik", "odderik", "503504");
    }

    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (preparedStatementsec != null) {
                preparedStatementsec.close();
                preparedStatementsec = null;
            }
            if (preparedStatement != null) {
                preparedStatement.close();
                preparedStatement = null;
            }
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public synchronized void updatePlayer(UUID uuid, String name) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_players` (`uuid`,`name`) VALUES (?,?) ON DUPLICATE KEY UPDATE `name` = VALUES(`name`)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, name);
            preparedStatement.execute();
            close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized String getPlayerName(UUID uuid) {
        String name = "";
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `name` FROM `mine_players` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("name");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return name;
    }

    public synchronized UUID getPlayerUUID(String name) {
        UUID uuid = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_players` WHERE `name` = ?");
            preparedStatement.setString(1, name);
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

    public synchronized List<UUID> getPlayerMapUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_players`");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                uuids.add(UUID.fromString(resultSet.getString("uuid")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return uuids;
    }

    public synchronized HashMap<String, Object> getPlayer(UUID uniqueId) {
        HashMap<String, Object> player = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players` WHERE `uuid` = ?");
            preparedStatement.setString(1, uniqueId.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                player.put("uuid", UUID.fromString(resultSet.getString("uuid")));
                player.put("name", resultSet.getString("name"));
                player.put("denytpa", resultSet.getBoolean("denytpa"));
                List<UUID> whitelist = new ArrayList<>();
                if (resultSet.getString("whitelist") != null) {
                    for (String uid : resultSet.getString("whitelist").split(",")) {
                        whitelist.add(UUID.fromString(uid));
                    }
                }
                player.put("whitelist", whitelist);
                List<UUID> blacklist = new ArrayList<>();
                if (resultSet.getString("blacklist") != null) {
                    for (String uid : resultSet.getString("blacklist").split(",")) {
                        blacklist.add(UUID.fromString(uid));
                    }
                }
                player.put("blacklist", blacklist);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return (!player.isEmpty()) ? player : null;
    }

    public synchronized List<String> getPlayerMapNames() {
        List<String> names = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `name` FROM `mine_players`");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return names;
    }

    public synchronized void setPlayerDenyTpa(UUID uniqueId, boolean deny) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_players` SET `denytpa` = ? WHERE `uuid` = ?");
            preparedStatement.setInt(1, ((deny) ? 1 : 0));
            preparedStatement.setString(2, uniqueId.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized void updatePlayerBlacklist(UUID uniqueId, List<UUID> blacklist) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_players` SET `blacklist` = ? WHERE `uuid` = ?");
            StringBuilder list = new StringBuilder();
            if (blacklist.isEmpty()) {
                for (UUID uuid : blacklist) {
                    list.append(uuid.toString()).append(";");
                }
                preparedStatement.setString(1, list.substring(0, list.length() - 1));
            } else {
                preparedStatement.setString(1, null);
            }
            preparedStatement.setString(2, uniqueId.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized void updatePlayerWhitelist(UUID uniqueId, List<UUID> whitelist) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_players` SET `whitelist` = ? WHERE `uuid` = ?");
            StringBuilder list = new StringBuilder();
            if (whitelist.isEmpty()) {
                for (UUID uuid : whitelist) {
                    list.append(uuid.toString()).append(";");
                }
                preparedStatement.setString(1, list.substring(0, list.length() - 1));
            } else {
                preparedStatement.setString(1, null);
            }
            preparedStatement.setString(2, uniqueId.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized Location getHome(UUID uuid, String name) {
        Location location = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_homes` WHERE `uuid` = ? AND `name` = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, name);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                if (world != null) {
                    location = new Location(
                            world,
                            resultSet.getDouble("x"),
                            resultSet.getDouble("y"),
                            resultSet.getDouble("z"),
                            resultSet.getFloat("yaw"),
                            resultSet.getFloat("pitch")
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return location;
    }

    public synchronized void createHome(UUID uuid, String name, Location location) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_homes` (`name`,`uuid`,`world`,`x`,`y`,`z`,`yaw`,`pitch`) VALUES (?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.setString(3, location.getWorld().getUID().toString());
            preparedStatement.setDouble(4, location.getBlockX());
            preparedStatement.setDouble(5, location.getBlockY());
            preparedStatement.setDouble(6, location.getBlockZ());
            preparedStatement.setFloat(7, location.getYaw());
            preparedStatement.setFloat(8, location.getPitch());
            preparedStatement.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized void updateHome(UUID uuid, String name, Location location) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_homes` SET `world` = ?,`x` = ?,`y` = ?,`z` = ?,`yaw` = ?,`pitch` = ? WHERE `name` = ? AND `uuid` = ?");
            preparedStatement.setString(1, location.getWorld().getUID().toString());
            preparedStatement.setDouble(2, location.getBlockX());
            preparedStatement.setDouble(3, location.getBlockY());
            preparedStatement.setDouble(4, location.getBlockZ());
            preparedStatement.setFloat(5, location.getYaw());
            preparedStatement.setFloat(6, location.getPitch());
            preparedStatement.setString(7, name);
            preparedStatement.setString(8, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized void deleteHome(UUID uuid, String name) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_homes` WHERE `name` = ? AND `uuid` = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized List<String> listHomes(UUID uuid) {
        List<String> list = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `name` FROM `mine_homes` WHERE `uuid` = ? ");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                list.add(resultSet.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return list;
    }

    public synchronized void createLock(UUID uuid, Entity entity) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_secured_armorstands` (`entity`,`player`) VALUES (?,?)");
            preparedStatement.setString(1, entity.toString());
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized UUID hasLock(Entity entity) {
        UUID uid = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `player` FROM `mine_secured_armorstands` WHERE `entity` = ?");
            preparedStatement.setString(1, entity.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                uid = UUID.fromString(resultSet.getString("uuid"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return uid;
    }

    public synchronized void deleteLock(Entity entity) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_secured_armorstands` WHERE `entity` = ?");
            preparedStatement.setString(1, entity.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized void createLock(UUID uuid, Location location) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_secured_blocks` (`uuid`,`world`,`x`,`y`,`z`) VALUES (?,?,?,?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, location.getWorld().getUID().toString());
            preparedStatement.setInt(3, location.getBlockX());
            preparedStatement.setInt(4, location.getBlockY());
            preparedStatement.setInt(5, location.getBlockZ());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized UUID hasLock(Location location) {
        UUID uid = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_secured_blocks` WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ? ");
            preparedStatement.setString(1, location.getWorld().getUID().toString());
            preparedStatement.setInt(2, location.getBlockX());
            preparedStatement.setInt(3, location.getBlockY());
            preparedStatement.setInt(4, location.getBlockZ());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                uid = UUID.fromString(resultSet.getString("uuid"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return uid;
    }

    public synchronized void deleteLock(Location location) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_secured_blocks` WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ? ");
            preparedStatement.setString(1, location.getWorld().getUID().toString());
            preparedStatement.setInt(2, location.getBlockX());
            preparedStatement.setInt(3, location.getBlockY());
            preparedStatement.setInt(4, location.getBlockZ());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized HashMap<String, Object> getGuildByPlayer(UUID uniqueId) {
        HashMap<String, Object> guild = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT b.* FROM `mine_guilds_members` a LEFT JOIN `mine_guilds` b ON a.`uuid` = b.`uuid` WHERE `player` = ? ");
            preparedStatement.setString(1, uniqueId.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                guild.put("uuid", resultSet.getString("uuid"));
                guild.put("name", resultSet.getString("name"));
                guild.put("zone", Zone.valueOf(resultSet.getString("zone")));
                guild.put("invited_only", resultSet.getBoolean("invited_only"));
                guild.put("friendly_fire", resultSet.getBoolean("friendly_fire"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return guild;
    }

    public synchronized void createGuild(HashMap<String, String> memberOfGuild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds` (`uuid`,`name`,`zone`,`invited_only`) VALUES (?,?,?,?)");
            preparedStatement.setString(1, memberOfGuild.get("uuid"));
            preparedStatement.setString(2, memberOfGuild.get("name"));
            preparedStatement.setString(3, memberOfGuild.get("zone"));
            preparedStatement.setBoolean(4, false);
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized void addGuildMember(UUID guild, UUID player, Role role) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_members` (`uuid`,`player`,`role`) VALUES (?,?,?)");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.setString(2, player.toString());
            preparedStatement.setString(3, role.name());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized void addGuildChunks(UUID guild, Chunk chunk, Player player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`uuid`,`world`,`x`,`z`) VALUES (?,?,?,?)");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.setString(2, player.getWorld().getUID().toString());
            preparedStatement.setInt(3, chunk.getX());
            preparedStatement.setInt(4, chunk.getZ());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized Role getGuildMemberRole(UUID player) {
        Role role = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `role` FROM `mine_guilds_members` WHERE `player` = ?");
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                role = Role.valueOf(resultSet.getString("role"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return role;
    }

    public void deleteMemberFromGuild(UUID player) {
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

    public void setGuildInvitedOnly(UUID guildUUIDByMember, boolean bol) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_guilds` SET `invited_only` = ? WHERE `uuid` = ?");
            preparedStatement.setBoolean(1, bol);
            preparedStatement.setString(2, guildUUIDByMember.toString());
            preparedStatement.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void setGuildFriendlyFire(UUID guildUUIDByMember, boolean bol) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_guilds` SET `friendly_fire` = ? WHERE `uuid` = ?");
            preparedStatement.setBoolean(1, bol);
            preparedStatement.setString(2, guildUUIDByMember.toString());
            preparedStatement.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public UUID getGuildInvite(UUID player) {
        UUID uid = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds_invites` WHERE `player` = ?");
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                uid = UUID.fromString(resultSet.getString("uuid"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return uid;
    }

    public UUID getGuildPending(UUID player) {
        UUID uid = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds_pendings` WHERE `player` = ?");
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                uid = UUID.fromString(resultSet.getString("uuid"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return uid;
    }

    public void addGuildInvite(UUID guild, UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_invites` (`uuid`,`player`) VALUES (?,?)");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.setString(2, player.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void deleteGuildInvite(UUID guild, UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_invites` WHERE `uuid` = ? AND `player` = ?");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.setString(2, player.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public boolean setGuildName(UUID uniqueId, String string) {
        boolean change = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_guilds` SET `name` = ? WHERE `uuid` = ?");
            preparedStatement.setString(1, string);
            preparedStatement.setString(2, uniqueId.toString());
            preparedStatement.executeQuery();
            change = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return change;
    }

    public UUID getGuildUUIDByChunk(Chunk chunk, World world) {
        UUID uuid = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds_chunks` WHERE `world` = ? AND `x` = ? AND `z` = ? ");
            preparedStatement.setString(1, world.getUID().toString());
            preparedStatement.setInt(2, chunk.getX());
            preparedStatement.setInt(3, chunk.getZ());
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

    public String getZoneByGuild(UUID guild) {
        String zone = "";
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `zone` FROM `mine_guilds` WHERE `uuid` = ? ");
            preparedStatement.setString(1, guild.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                zone = resultSet.getString("zone");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return zone;
    }

    public String getGuildNameByUUID(UUID guild) {
        String name = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `name` FROM `mine_guilds` WHERE `uuid` = ? ");
            preparedStatement.setString(1, guild.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("name");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return name;
    }

    public void deleteGuildChunks(UUID guild, Chunk chunk, Player player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_chunks` WHERE `uuid` = ? AND `world` = ? AND `x` = ? AND `z` = ?");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.setString(2, player.getWorld().getUID().toString());
            preparedStatement.setInt(3, chunk.getX());
            preparedStatement.setInt(4, chunk.getZ());
            preparedStatement.execute();

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public List<Material> getLockableMaterials() {
        List<Material> materials = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_lockable_materials` WHERE `value` = ?");
            preparedStatement.setBoolean(1, true);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                materials.add(Material.valueOf(resultSet.getString("name")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return materials;
    }

    public String getGuildPermission(String permission, String uuid) {
        String ret = "";
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `" + permission + "_permission` FROM `mine_guilds` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ret = resultSet.getString(permission + "_permission");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }

    public UUID getGuildUUIDByMemeber(UUID player) {
        UUID uuid = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT b.`uuid` FROM `mine_guilds_members` a LEFT JOIN `mine_guilds` b ON a.`uuid` = b.`uuid` WHERE `player` = ? ");
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                uuid = UUID.fromString(resultSet.getString("b.uuid"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return uuid;
    }

    public UUID getGuildUUIDByZone(Zone zone) {
        UUID uuid = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds` WHERE `zone` = ? ");
            preparedStatement.setString(1, zone.name());
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

    public UUID getGuildUUIDByName(String name) {
        UUID uuid = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds` WHERE `name` = ? ");
            preparedStatement.setString(1, name);
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

    public List<Chunk> getGuildChunksByUUID(UUID guild) {
        List<Chunk> chunks = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks` WHERE `uuid` = ? ");
            preparedStatement.setString(1, guild.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                chunks.add(Bukkit.getWorld(UUID.fromString(resultSet.getString("world"))).getChunkAt(resultSet.getInt("x"), resultSet.getInt("y")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return chunks;
    }

    public List<UUID> getGuildUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds` ");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                uuids.add(UUID.fromString(resultSet.getString("uuid")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return uuids;
    }

    public boolean getGuildSettings(String settings, UUID guild) {
        boolean bol = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `" + settings + "` FROM `mine_guilds` WHERE `uuid` = ?");
            preparedStatement.setString(1, guild.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                bol = resultSet.getInt(settings) == 1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return bol;
    }

    public List<UUID> getGuildInvitations(UUID guild) {
        List<UUID> uuids = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `player` FROM `mine_guilds_invites` WHERE `uuid` = ?");
            preparedStatement.setString(1, guild.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                uuids.add(UUID.fromString(resultSet.getString("player")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return uuids;
    }

    public List<UUID> getGuildPendings(UUID guild) {
        List<UUID> uuids = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `player` FROM `mine_guilds_pendings` WHERE `uuid` = ?");
            preparedStatement.setString(1, guild.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                uuids.add(UUID.fromString(resultSet.getString("player")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return uuids;
    }

    public void deletePending(UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_pendings` WHERE `player` = ?");
            preparedStatement.setString(1, player.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void deleteInvitation(UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_invites` WHERE `player` = ?");
            preparedStatement.setString(1, player.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void addGuildPending(UUID guild, UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_pendings` (`uuid`,`player`) VALUES (?,?)");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.setString(2, player.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public List<UUID> getGuildMembers(UUID guild) {
        List<UUID> uuids = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `player` FROM `mine_guilds_members` WHERE `uuid` = ?");
            preparedStatement.setString(1, guild.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                uuids.add(UUID.fromString(resultSet.getString("player")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return uuids;
    }

    public GameMode getPlayerMode(Player player, World world) {
        GameMode gameMode = GameMode.SURVIVAL;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `gamemode` FROM `mine_players_gamemodes` WHERE `uuid` = ? AND `world` = ?");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, world.getUID().toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String str = resultSet.getString("gamemode");
                if (str.startsWith("SP")) {
                    gameMode = GameMode.SPECTATOR;
                } else if (str.startsWith("C")) {
                    gameMode = GameMode.CREATIVE;
                } else if (str.startsWith("A")) {
                    gameMode = GameMode.ADVENTURE;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return gameMode;
    }

    public GameMode getWorldMode(World world) {
        GameMode gameMode = GameMode.SURVIVAL;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `gamemode` FROM `mine_worlds` WHERE `uuid` = ?");
            preparedStatement.setString(1, world.getUID().toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                GameMode.valueOf(resultSet.getString("gamemode"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return gameMode;
    }

    public boolean getForceMode(World world) {
        boolean force = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `gamemode` FROM `mine_worlds` WHERE `uuid` = ?");
            preparedStatement.setString(1, world.getUID().toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                force = (resultSet.getInt("gforce") == 1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return force;
    }

    public boolean getPlayerDenyTpa(UUID to) {
        boolean accept = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `denytpa` FROM `mine_players` WHERE `uuid` = ?");
            preparedStatement.setString(1, to.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                accept = (resultSet.getInt("denytpa") == 1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return accept;
    }

    public List<UUID> getPlayerBlackList(UUID to) {
        List<UUID> blackList = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `blacklist` FROM `mine_players` WHERE `uuid` = ?");
            preparedStatement.setString(1, to.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getString("blacklist") != null) {
                    for (String string : resultSet.getString("blacklist").split(";")) {
                        blackList.add(UUID.fromString(string));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return blackList;
    }

    public List<UUID> getPlayerWhiteList(UUID to) {
        List<UUID> whiteList = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `whitelist` FROM `mine_players` WHERE `uuid` = ?");
            preparedStatement.setString(1, to.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getString("whitelist") != null) {
                    for (String string : resultSet.getString("whitelist").split(";")) {
                        whiteList.add(UUID.fromString(string));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return whiteList;
    }

    public void updateTeleport(Player player) {
        boolean exist = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_players_teleport` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                exist = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        if (exist) {
            try {
                connect();
                preparedStatement = connection.prepareStatement("UPDATE `mine_players_teleport` SET `world` = ?, `x` = ?,`y` = ?,`z` = ?,`yaw` = ?,`pitch` = ? WHERE `uuid` = ?");
                preparedStatement.setString(1, player.getWorld().getUID().toString());
                preparedStatement.setDouble(2, player.getLocation().getX());
                preparedStatement.setDouble(3, player.getLocation().getY());
                preparedStatement.setDouble(4, player.getLocation().getZ());
                preparedStatement.setFloat(5, player.getLocation().getYaw());
                preparedStatement.setFloat(6, player.getLocation().getPitch());
                preparedStatement.setString(7, player.getUniqueId().toString());
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        } else {
            try {
                connect();
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_players_teleport` (`world`,`x`,`y`,`z`,`yaw`,`pitch`,`uuid`) VALUES (?,?,?,?,?,?,?)");
                preparedStatement.setString(1, player.getWorld().getUID().toString());
                preparedStatement.setDouble(2, player.getLocation().getX());
                preparedStatement.setDouble(3, player.getLocation().getY());
                preparedStatement.setDouble(4, player.getLocation().getZ());
                preparedStatement.setFloat(5, player.getLocation().getYaw());
                preparedStatement.setFloat(6, player.getLocation().getPitch());
                preparedStatement.setString(7, player.getUniqueId().toString());
                preparedStatement.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        }
    }

    public void setGameMode(Player player, GameMode gameMode) {
        boolean exist = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_players_gamemodes` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                exist = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        if (exist) {
            try {
                connect();
                preparedStatement = connection.prepareStatement("UPDATE `mine_players_gamemodes` SET `world` = ?, `gamemode` = ? WHERE `uuid` = ?");
                preparedStatement.setString(1, player.getWorld().getUID().toString());
                preparedStatement.setString(2, gameMode.name());
                preparedStatement.setString(3, player.getUniqueId().toString());
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        } else {
            try {
                connect();
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_players_gamemodes` (`world`,`gamemode`,`uuid`) VALUES (?,?,?)");
                preparedStatement.setString(1, player.getWorld().getUID().toString());
                preparedStatement.setString(2, gameMode.name());
                preparedStatement.setString(3, player.getUniqueId().toString());
                preparedStatement.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        }
    }

    public void deletePlayerBan(UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_players` SET `banned` = ? WHERE `uuid` = ?");
            preparedStatement.setString(1, null);
            preparedStatement.setString(2, player.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void addPlayerBan(UUID player, String text) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_players` SET `banned` = ? WHERE `uuid` = ?");
            preparedStatement.setString(1, text);
            preparedStatement.setString(2, player.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public List<UUID> getBans() {
        List<UUID> bans = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_players` WHERE `banned` IS NOT NULL ");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                bans.add(UUID.fromString(resultSet.getString("uuid")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return bans;
    }

    public String getBan(UUID uuid) {
        String string = "";
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `banned` FROM `mine_players` WHERE `uuid` = ? ");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                string = resultSet.getString("banned");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return string;
    }

    public int getGuildCountClaims(UUID guild) {
        int c = 0;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT COUNT(DISTINCT `id`) FROM `mine_guilds_chunks` WHERE `uuid` = ? ");
            preparedStatement.setString(1, guild.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                c = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return c;
    }

    public HashMap<UUID, Double> getBalanceMapPlayer() {
        HashMap<UUID, Double> map = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_balances` WHERE `guild` = 0");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                map.put(UUID.fromString(resultSet.getString("uuid")), resultSet.getDouble("balance"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return map;
    }

    public HashMap<UUID, Double> getBalanceMapGuild() {
        HashMap<UUID, Double> map = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_balances` WHERE `guild` = 1");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                map.put(UUID.fromString(resultSet.getString("uuid")), resultSet.getDouble("balance"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return map;
    }

    public boolean hasBalance(UUID player) {
        boolean has = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_balances` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                has = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return has;
    }

    public Double getBalance(UUID player) {
        double has = 0D;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `balance` FROM `mine_balances` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                has = resultSet.getDouble("balance");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return has;
    }

    public void setBalance(UUID uuid, double amount, boolean guild) {
        try {
            createBalance(uuid, amount, guild);
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `balance` = ? WHERE `uuid` = ?");
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void createBalance(UUID uuid, double amount, boolean guild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_balances` WHERE `uuid` =? ");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_balances` (`balance`,`uuid`,`guild`) VALUES (?,?,?)");
                preparedStatement.setDouble(1, amount);
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.setInt(3, (guild ? 1 : 0));
                preparedStatement.execute();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void addFrozen(UUID player, Location location) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_frozen` (`uuid`,`world`,`x`,`y`,`z`) VALUES (?,?,?,?,?)");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, location.getWorld().getUID().toString());
            preparedStatement.setInt(3, location.getBlockX());
            preparedStatement.setInt(4, location.getBlockY());
            preparedStatement.setInt(5, location.getBlockZ());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void updateFrozen(UUID player, Location location) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_frozen` SET `world` = ?, `x` = ?, `y` = ?, `z` = ? WHERE `uuid` = ?");
            preparedStatement.setString(5, player.toString());
            preparedStatement.setString(1, location.getWorld().getUID().toString());
            preparedStatement.setInt(2, location.getBlockX());
            preparedStatement.setInt(3, location.getBlockY());
            preparedStatement.setInt(4, location.getBlockZ());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void deleteFrozen(UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_frozen` WHERE `uuid`?");
            preparedStatement.setString(1, player.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public Location getFrozen(UUID player) {
        Location location = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_frozen` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                location = new Location(Bukkit.getWorld(UUID.fromString(resultSet.getString("world"))), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return location;
    }

    public Location getWarp(String name, String password) {
        Location location = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_warps` WHERE `name` = ? AND `passw` = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                if (world != null) {
                    location = new Location(world, resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"), resultSet.getFloat("yaw"), resultSet.getFloat("pitch"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return location;
    }

    public void addWarp(String name, Player player, String password) {
        try {
            Location location = player.getLocation();
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_warps` (`name`,`world`,`x`,`y`,`z`,`yaw`,`pitch`,`passw`) VALUES (?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, player.getLocation().getWorld().getUID().toString());
            preparedStatement.setDouble(3, location.getX());
            preparedStatement.setDouble(4, location.getY());
            preparedStatement.setDouble(5, location.getZ());
            preparedStatement.setFloat(6, location.getYaw());
            preparedStatement.setFloat(7, location.getPitch());
            preparedStatement.setString(8, password);
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void deleteWarp(String name, String password) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_warps` WHERE `name` = ? AND `passw` = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public boolean getWarp(String name) {
        boolean bol = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_warps` WHERE `name` = ?");
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                bol = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return bol;
    }

    public List<String> listWarps() {
        List<String> list = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_warps`");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return list;
    }

    public Location getBack(UUID player) {
        Location location = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players_teleport` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                location = new Location(Bukkit.getWorld(UUID.fromString(resultSet.getString("world"))),
                        resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"),
                        resultSet.getFloat("yaw"), resultSet.getFloat("pitch"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return location;
    }

    public void addDeathChest(Location location, Material left, Material right, UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_players_death_chests` (`uuid`,`world`,`x`,`y`,`z`,`leftBlock`,`rightBlock`,`time`) VALUES (?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, location.getWorld().getUID().toString());
            preparedStatement.setDouble(3, location.getBlockX());
            preparedStatement.setDouble(4, location.getBlockY());
            preparedStatement.setDouble(5, location.getBlockZ());
            preparedStatement.setString(6, left.toString());
            preparedStatement.setString(7, right.toString());
            preparedStatement.setLong(8, System.currentTimeMillis() / 1000L);
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public HashMap<String, String> getDeathChest(Location location) {
        HashMap<String, String> ret = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players_death_chests` WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
            preparedStatement.setString(1, location.getWorld().getUID().toString());
            preparedStatement.setDouble(2, location.getBlockX());
            preparedStatement.setDouble(3, location.getBlockY());
            preparedStatement.setDouble(4, location.getBlockZ());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ret.put("uuid", resultSet.getString("uuid"));
                ret.put("left", resultSet.getString("leftBlock"));
                ret.put("right", resultSet.getString("rightBlock"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return ret;
    }

    public void deleteDeathChest(Location location) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_players_death_chests` WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
            preparedStatement.setString(1, location.getWorld().getUID().toString());
            preparedStatement.setDouble(2, location.getBlockX());
            preparedStatement.setDouble(3, location.getBlockY());
            preparedStatement.setDouble(4, location.getBlockZ());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public int timeCheck(int t) {
        int i = 0;
        OddJob.getInstance().log(t + "s");
        OddJob.getInstance().log(((System.currentTimeMillis() / 1000L) - t) + "");
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players_death_chests` WHERE `time` < ?");
            preparedStatement.setLong(1, (System.currentTimeMillis() / 1000L) - t);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                OddJob.getInstance().log(resultSet.getLong("time") + "");
                OddJob.getInstance().getDeathManager().replace(
                        UUID.fromString(resultSet.getString("world")),
                        resultSet.getDouble("x"),
                        resultSet.getDouble("y"),
                        resultSet.getDouble("z"),
                        Material.valueOf(resultSet.getString("leftBlock")),
                        Material.valueOf(resultSet.getString("rightBlock")));
                i++;
            }
            OddJob.getInstance().log("chests " + i);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return i;
    }

    public boolean isDeathChest(Location location) {
        boolean is = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players_death_chests` WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
            preparedStatement.setString(1, location.getWorld().getUID().toString());
            preparedStatement.setDouble(2, location.getBlockX());
            preparedStatement.setDouble(3, location.getBlockY());
            preparedStatement.setDouble(4, location.getBlockZ());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                is = true;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return is;
    }

    public void moteGuild(UUID guildUUID, UUID targetUUID, String role) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_guilds_members` SET `role` = ? WHERE `uuid` = ? AND `player` = ?");
            preparedStatement.setString(1, role);
            preparedStatement.setString(2, guildUUID.toString());
            preparedStatement.setString(3, targetUUID.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void addPlayerJail(UUID uuidPlayer, UUID world) {
        try {
            connect();
            // PLAYER ; WORLD ; DATE SET IN JAIL ; IS IN JAIL ; COUNT JAIL TIMES ; SERVED TIME
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_players_jailed` (`uuid`,`world`,`date`) VALUES (?,?,UNIX_TIMESTAMP())");
            preparedStatement.setString(1, uuidPlayer.toString());
            preparedStatement.setString(2, world.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void deletePlayerJail(UUID player) {
        try {
            connect();

            preparedStatement = connection.prepareStatement("DELETE FROM `mine_players_jailed` WHERE `uuid` = ? ");
            preparedStatement.setString(1, player.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public World inPlayerJail(UUID player) {
        World world = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `world` FROM `mine_players_jailed` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                OddJob.getInstance().getMessageManager().console(resultSet.getString("world"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return world;
    }

    public boolean setJail(UUID world, String name, Location location) {
        boolean ret = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_worlds` SET `jail_" + name + "` = ? WHERE `uuid` = ?");
            preparedStatement.setString(1, Utility.serializeLoc(location));
            preparedStatement.setString(2, world.toString());
            preparedStatement.executeUpdate();
            ret = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return ret;
    }

    public Location getJail(UUID world, String name) {
        Location location = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `jail_"+name+"` FROM `mine_worlds` WHERE `uuid` = ?");
            preparedStatement.setString(1, world.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String s = resultSet.getString("jail_" + name);
                location = Utility.deserializeLoc(s);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return location;
    }

    public void updateWorlds(World world) {
        UUID uuid = world.getUID();
        String name = world.getName();

        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_worlds` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                preparedStatementsec = connection.prepareStatement("UPDATE `mine_worlds` SET `name` = ? WHERE `uuid` = ?");
                preparedStatementsec.setString(2, uuid.toString());
                preparedStatementsec.setString(1, name);
                preparedStatementsec.executeUpdate();
                OddJob.getInstance().getMessageManager().console("UPDATING: "+name);
            } else {
                preparedStatementsec = connection.prepareStatement("INSERT INTO `mine_worlds` (`uuid`,`name`) VALUES (?,?)");
                preparedStatementsec.setString(1, uuid.toString());
                preparedStatementsec.setString(2, name);
                preparedStatementsec.execute();
                OddJob.getInstance().getMessageManager().console("INSERTING: "+name);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

    }
}
