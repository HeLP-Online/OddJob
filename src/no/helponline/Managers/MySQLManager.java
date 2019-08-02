package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.OddPlayer;
import no.helponline.Utils.Role;
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

    public synchronized void saveHomes(Location location, String homeName, OddPlayer oddPlayer) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_homes` (`name`,`uuid`,`x`,`y`,`z`,`world`,`yaw`,`pitch`) VALUES (?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, homeName);
            preparedStatement.setString(2, oddPlayer.getUuid().toString());
            preparedStatement.setInt(3, location.getBlockX());
            preparedStatement.setInt(4, location.getBlockY());
            preparedStatement.setInt(5, location.getBlockZ());
            preparedStatement.setString(6, location.getWorld().getUID().toString());
            preparedStatement.setDouble(7, location.getYaw());
            preparedStatement.setDouble(8, location.getPitch());
            preparedStatement.execute();
            close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
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
            if (resultSet.next()) {
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
                for (String uid : resultSet.getString("whitelist").split(",")) {
                    whitelist.add(UUID.fromString(uid));
                }
                player.put("whitelist", whitelist);
                List<UUID> blacklist = new ArrayList<>();
                for (String uid : resultSet.getString("blacklist").split(",")) {
                    blacklist.add(UUID.fromString(uid));
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
            for (UUID uuid : blacklist) {
                list.append(uuid.toString()).append(",");
            }
            preparedStatement.setString(1, list.substring(0, list.length() - 1));
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
            for (UUID uuid : whitelist) {
                list.append(uuid.toString()).append(",");
            }
            preparedStatement.setString(1, list.substring(0, list.length() - 1));
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
                location = new Location(
                        Bukkit.getWorld(UUID.fromString(resultSet.getString("world"))),
                        resultSet.getDouble("x"),
                        resultSet.getDouble("y"),
                        resultSet.getDouble("z"),
                        resultSet.getFloat("yaw"),
                        resultSet.getFloat("pitch")
                );
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
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_secured_armorstands` WHERE `entity` = ?");
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

    public synchronized void createGuild(HashMap<String, Object> memberOfGuild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds` (`uuid`,`name`,`zone`,`invited_only`) VALUES (?,?,?,?)");
            preparedStatement.setString(1, (String) memberOfGuild.get("uuid"));
            preparedStatement.setString(2, (String) memberOfGuild.get("name"));
            preparedStatement.setString(3, (String) memberOfGuild.get("zone"));
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

    public synchronized void addGuildChunks(UUID guild, Chunk chunk) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`uuid`,`world`,`x`,`z`) VALUES (?,?,?,?)");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.setString(2, chunk.getWorld().getUID().toString());
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
                OddJob.getInstance().log("test: " + role.name());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return role;
    }

    public void deleteMemberFromGuild(UUID uniqueId) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_members` WHERE `uuid` = ?");
            preparedStatement.setString(1, uniqueId.toString());
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

    public void deleteGuildChunks(UUID guild, Chunk chunk) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_chunks` WHERE `uuid` = ? AND `world` = ? AND `x` = ? AND `z` = ?");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.setString(2, chunk.getWorld().getUID().toString());
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

    public String getGuildUUIDByName(String name) {
        String uuid = "";
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds` WHERE `name` = ? ");
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                uuid = resultSet.getString("uuid");
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
                GameMode.valueOf(resultSet.getString("gamemode"));
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
}
