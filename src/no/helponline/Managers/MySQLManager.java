package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.Econ;
import no.helponline.Utils.Enum.Role;
import no.helponline.Utils.Enum.ScoreBoard;
import no.helponline.Utils.Enum.Zone;
import no.helponline.Utils.Guild;
import no.helponline.Utils.Home;
import no.helponline.Utils.OddPlayer;
import no.helponline.Utils.Utility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MySQLManager {
    private PreparedStatement preparedStatement = null;
    private PreparedStatement preparedStatementsec = null;
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private ResultSet resultSetsec = null;

    private void connect() throws SQLException {
        if (connection == null)
            connection = DriverManager.getConnection("jdbc:mysql://10.0.4.9/odderik", "odderik", "503504");
    }

    private void close() {
        try {
            if (resultSetsec != null) {
                resultSetsec.close();
                resultSetsec = null;
            }
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (statement != null) {
                statement.close();
                statement = null;
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
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                if (name.equals(resultSet.getString("name"))) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_players` SET `name` = ? WHERE `uuid` = ?");
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, uuid.toString());
                    preparedStatement.executeUpdate();
                    OddJob.getInstance().getMessageManager().console("Player updated");
                }
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_players` (`uuid`,`name`) VALUES (?,?)");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, name);
                preparedStatement.execute();
                OddJob.getInstance().getMessageManager().console("Player inserted");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
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
            if (!blacklist.isEmpty()) {
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
            if (!whitelist.isEmpty()) {
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

    public boolean deleteGuildsChunks(Chunk chunk) {
        boolean ret = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_chunks` WHERE `world` = ? AND `x` = ? AND `z` = ?");
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

    public void addPlayerJail(UUID playerUUID, UUID worldUUID) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_players_jailed` (`world`,`uuid`,`date`,`release`) VALUES (?,?,UNIX_TIMESTAMP(),?)");
            preparedStatement.setString(1, worldUUID.toString());
            preparedStatement.setString(2, playerUUID.toString());
            preparedStatement.setInt(3, Math.toIntExact((System.currentTimeMillis() / 1000)));
            preparedStatement.execute();
        } catch (SQLException ex) {
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
            preparedStatement = connection.prepareStatement("SELECT `jail_" + name + "` FROM `mine_worlds` WHERE `uuid` = ?");
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

    public void updateWorld(World world) {
        UUID worldUUID = world.getUID();
        String name = world.getName();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid`,`name` FROM `mine_worlds` WHERE `uuid` = ?");
            preparedStatement.setString(1, worldUUID.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (name.equalsIgnoreCase(resultSet.getString("name"))) {
                    preparedStatementsec = connection.prepareStatement("UPDATE `mine_worlds` SET `name` = ? WHERE `uuid` = ?");
                    preparedStatementsec.setString(2, worldUUID.toString());
                    preparedStatementsec.setString(1, name);
                    preparedStatementsec.executeUpdate();
                }
            } else {
                preparedStatementsec = connection.prepareStatement("INSERT INTO `mine_worlds` (`uuid`,`name`) VALUES (?,?)");
                preparedStatementsec.setString(1, worldUUID.toString());
                preparedStatementsec.setString(2, name);
                preparedStatementsec.execute();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void addLog(UUID uniqueId, Block block, String action) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_log_DE` (`uuid`,`world`,`x`,`y`,`z`,`action`,`item`,`time`) VALUES (?,?,?,?,?,?,?,UNIX_TIMESTAMP())");
            preparedStatement.setString(1, uniqueId.toString());
            preparedStatement.setString(2, block.getLocation().getWorld().getUID().toString());
            preparedStatement.setInt(3, block.getLocation().getBlockX());
            preparedStatement.setInt(4, block.getLocation().getBlockY());
            preparedStatement.setInt(5, block.getLocation().getBlockZ());
            preparedStatement.setString(6, action);
            preparedStatement.setString(7, block.getType().name());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void addLog(UUID uniqueId, ItemStack stack, String action) {
        try {
            Player player = Bukkit.getPlayer(uniqueId);
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_log_DE` (`uuid`,`world`,`x`,`y`,`z`,`action`,`item`,`count`,`time`) VALUES (?,?,?,?,?,?,?,?,UNIX_TIMESTAMP())");
            preparedStatement.setString(1, uniqueId.toString());
            preparedStatement.setString(2, player.getLocation().getWorld().getUID().toString());
            preparedStatement.setInt(3, player.getLocation().getBlockX());
            preparedStatement.setInt(4, player.getLocation().getBlockY());
            preparedStatement.setInt(5, player.getLocation().getBlockZ());
            preparedStatement.setString(6, action);
            preparedStatement.setString(7, stack.getType().name());
            preparedStatement.setInt(8, stack.getAmount());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public HashMap<UUID, Location> locksInWorld(UUID world) {
        HashMap<UUID, Location> chests = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_secured_blocks` WHERE `world` = ?");
            preparedStatement.setString(1, world.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                while (resultSet.next()) {
                    chests.put(
                            UUID.fromString(resultSet.getString("uuid")),
                            new Location(
                                    Bukkit.getWorld(world),
                                    resultSet.getInt("x"),
                                    resultSet.getInt("y"),
                                    resultSet.getInt("z")
                            )
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return chests;
    }

    public void saveGuild(HashMap<UUID, Guild> guilds) {
        int gi = 0, gu = 0, mi = 0, mu = 0, mn = 0;
        for (UUID guildUUID : guilds.keySet()) {
            Guild guild = OddJob.getInstance().getGuildManager().getGuild(guildUUID);
            String name = guild.getName();
            Zone zone = guild.getZone();
            boolean invitedOnly = guild.getInvitedOnly();
            boolean friendlyFire = guild.getFriendlyFire();
            Role permissionInviteRole = guild.getPermissionInvite();
            HashMap<UUID, Role> members = guild.getMembers();

            try {
                connect();
                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds` WHERE `uuid` = ?");
                preparedStatement.setString(1, guildUUID.toString());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_guilds` SET `name` = ?,`zone` = ?,`invited_only` = ?,`friendly_fire` = ?,`invite_permission` = ? WHERE `uuid` = ?");
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, zone.name());
                    preparedStatement.setInt(3, invitedOnly ? 1 : 0);
                    preparedStatement.setInt(4, friendlyFire ? 1 : 0);
                    preparedStatement.setString(5, permissionInviteRole.name());
                    preparedStatement.setString(6, guildUUID.toString());
                    preparedStatement.executeUpdate();
                    gu++;
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds` (`uuid`,`name`,`zone`,`invited_only`,`friendly_fire`,`invite_permission`) VALUES (?,?,?,?,?,?)");
                    preparedStatement.setString(1, guildUUID.toString());
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, zone.name());
                    preparedStatement.setInt(4, invitedOnly ? 1 : 0);
                    preparedStatement.setInt(5, friendlyFire ? 1 : 0);
                    preparedStatement.setString(6, permissionInviteRole.name());
                    preparedStatement.execute();
                    gi++;
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
                            mu++;
                        } else {
                            mn++;
                        }
                    } else {
                        preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_members` (`uuid`,`player`,`role`) VALUES (?,?,?)");
                        preparedStatement.setString(1, guildUUID.toString());
                        preparedStatement.setString(2, uuid.toString());
                        preparedStatement.setString(3, members.get(uuid).name());
                        preparedStatement.execute();
                        mi++;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        }
        OddJob.getInstance().getMessageManager().console("Saved Guilds: insert:" + gi + "; update:" + gu + ";");
        OddJob.getInstance().getMessageManager().console("Saved Members: insert:" + mi + "; update:" + mu + "; nochange:" + mn + ";");
    }

    public HashMap<UUID, Guild> loadGuilds() {
        HashMap<UUID, Guild> guilds = new HashMap<>();
        int m = 0, g = 0;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                HashMap<UUID, Role> members = new HashMap<>();
                UUID guildUUID = UUID.fromString(resultSet.getString("uuid"));
                preparedStatementsec = connection.prepareStatement("SELECT * FROM `mine_guilds_members` WHERE `uuid` = ?");
                preparedStatementsec.setString(1, resultSet.getString("uuid"));
                resultSetsec = preparedStatementsec.executeQuery();

                if (!resultSetsec.wasNull()) {
                    while (resultSetsec.next()) {
                        m++;
                        members.put(UUID.fromString(resultSetsec.getString("player")), Role.valueOf(resultSetsec.getString("role")));
                    }
                }
                guilds.put(guildUUID, new Guild(
                        guildUUID,
                        resultSet.getString("name"),
                        Zone.valueOf(resultSet.getString("zone")),
                        resultSet.getInt("invited_only") == 1,
                        resultSet.getInt("friendly_fire") == 1,
                        Role.valueOf(resultSet.getString("invite_permission")),
                        members
                ));
                g++;

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().console("Loaded Guilds: guilds:" + g + "; members:" + m + ";");
        return guilds;
    }

    public void saveChunks(HashMap<Chunk, UUID> chunks) {
        int i = 0, u = 0;
        for (Chunk chunk : chunks.keySet()) {

            World world = chunk.getWorld();
            int x = chunk.getX();
            int z = chunk.getZ();
            UUID guild = chunks.get(chunk);

            try {
                connect();
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks` WHERE `world` = ? AND `x` = ? AND `z` = ?");
                preparedStatement.setString(1, world.getUID().toString());
                preparedStatement.setInt(2, x);
                preparedStatement.setInt(3, z);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_guilds_chunks` SET `uuid` = ? WHERE `world` = ? AND `x` = ? AND `z` = ?");
                    preparedStatement.setString(1, guild.toString());
                    preparedStatement.setString(2, world.getUID().toString());
                    preparedStatement.setInt(3, x);
                    preparedStatement.setInt(4, z);
                    preparedStatement.executeUpdate();
                    u++;
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`uuid`,`world`,`x`,`z`) VALUES (?,?,?,?)");
                    preparedStatement.setString(1, guild.toString());
                    preparedStatement.setString(2, world.getUID().toString());
                    preparedStatement.setInt(3, x);
                    preparedStatement.setInt(4, z);
                    preparedStatement.execute();
                    i++;
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        }
        OddJob.getInstance().getMessageManager().console("Saved Chunks: insert:" + i + "; update:" + u + "; total:" + (i + u) + ";");
    }

    public HashMap<Chunk, UUID> loadChunks() {
        HashMap<Chunk, UUID> chunks = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks`");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                if (world != null) {
                    UUID guild = UUID.fromString(resultSet.getString("uuid"));
                    int x = resultSet.getInt("x");
                    int z = resultSet.getInt("z");
                    Chunk chunk = world.getChunkAt(x, z);
                    chunks.put(chunk, guild);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().console("Loaded chunks: total:" + chunks.size() + ";");
        return chunks;
    }

    public void disbandGuild(UUID guild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds` WHERE `uuid` = ?");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.execute();

            OddJob.getInstance().getMessageManager().console("Guild disbanded");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void savePlayers(HashMap<UUID, OddPlayer> players) {
        for (UUID uuid : players.keySet()) {
            OddPlayer data = players.get(uuid);
            StringBuilder white = new StringBuilder();
            for (UUID uw : data.getWhitelist()) {
                white.append(uw.toString()).append(",");
            }
            if (white.length() > 1) white.substring(0, white.length() - 1);
            StringBuilder black = new StringBuilder();
            for (UUID ub : data.getBlacklist()) {
                black.append(ub.toString()).append(",");
            }
            if (black.length() > 1) black.substring(0, black.length() - 1);
            try {
                connect();
                preparedStatement = connection.prepareStatement("UPDATE `mine_players` SET `blacklist` = ?, `whitelist` = ?, `denytpa` = ?, `banned` = ?, `denytrade` = ? WHERE `uuid` = ?");
                preparedStatement.setString(1, white.toString());
                preparedStatement.setString(2, black.toString());
                preparedStatement.setInt(3, data.getDenyTpa() ? 1 : 0);
                preparedStatement.setString(4, data.getBanned());
                preparedStatement.setInt(5, data.getDenyTrade() ? 1 : 0);
                preparedStatement.setString(6, uuid.toString());
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
            } finally {
                close();
            }
        }
    }

    public HashMap<UUID, OddPlayer> loadPlayers() {
        HashMap<UUID, OddPlayer> map = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                List<UUID> whitelist = new ArrayList<>();
                if (resultSet.getString("whitelist") != null) {
                    for (String uid : resultSet.getString("whitelist").split(",")) {
                        whitelist.add(UUID.fromString(uid));
                    }
                }
                List<UUID> blacklist = new ArrayList<>();
                if (resultSet.getString("blacklist") != null) {
                    for (String uid : resultSet.getString("blacklist").split(",")) {
                        blacklist.add(UUID.fromString(uid));
                    }
                }
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                ScoreBoard scoreBoard = null;
                try {
                    scoreBoard = ScoreBoard.valueOf(resultSet.getString("scoreboard"));
                } catch (Exception e) {
                }
                OddPlayer data = new OddPlayer(
                        uuid,
                        blacklist, whitelist,
                        resultSet.getInt("denytpa") == 1,
                        resultSet.getString("name"),
                        resultSet.getString("banned"),
                        scoreBoard,
                        resultSet.getInt("denytrade") == 1

                );
                map.put(uuid, data);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().console("Loaded Players:" + map.size());
        return map;
    }

    public HashMap<UUID, Home> loadHomes() {
        HashMap<UUID, Home> homes = new HashMap<>();
        int i = 0;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_homes`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                if (world != null) {
                    i++;
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    Home home = homes.get(uuid);
                    String name = resultSet.getString("name");
                    Location location = new Location(world, resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"), resultSet.getFloat("yaw"), resultSet.getFloat("pitch"));
                    if (homes.containsKey(uuid)) {
                        home.add(name, location);
                    } else {
                        home = new Home(uuid, location, name);
                        homes.put(uuid, home);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().console("Loaded Homes: total:" + i + ";");
        return homes;
    }

    public void saveHomes(HashMap<UUID, Home> homes) {
        int i = 0;
        int u = 0;
        for (UUID uuid : homes.keySet()) {
            Home home = homes.get(uuid);
            for (String name : home.list()) {
                Location location = home.get(name);

                try {
                    connect();
                    preparedStatement = connection.prepareStatement("SELECT * FROM `mine_homes` WHERE `uuid` = ? AND `name` = ?");
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.setString(2, name);
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        preparedStatement = connection.prepareStatement("UPDATE `mine_homes` SET `world` = ?, `x` = ?,`y` = ?,`z` = ?,`yaw` = ?,`pitch` = ? WHERE `uuid` = ? AND `name` = ?");
                        preparedStatement.setString(1, location.getWorld().getUID().toString());
                        preparedStatement.setInt(2, location.getBlockX());
                        preparedStatement.setInt(3, location.getBlockY());
                        preparedStatement.setInt(4, location.getBlockZ());
                        preparedStatement.setFloat(5, location.getYaw());
                        preparedStatement.setFloat(6, location.getPitch());
                        preparedStatement.setString(7, uuid.toString());
                        preparedStatement.setString(8, name);
                        preparedStatement.executeUpdate();
                        u++;
                    } else {
                        preparedStatement = connection.prepareStatement("INSERT INTO `mine_homes` (`world`, `x`,`y`,`z`,`yaw`,`pitch`,`uuid` ,`name`) VALUES (?,?,?,?,?,?,?,?)");
                        preparedStatement.setString(1, location.getWorld().getUID().toString());
                        preparedStatement.setInt(2, location.getBlockX());
                        preparedStatement.setInt(3, location.getBlockY());
                        preparedStatement.setInt(4, location.getBlockZ());
                        preparedStatement.setFloat(5, location.getYaw());
                        preparedStatement.setFloat(6, location.getPitch());
                        preparedStatement.setString(7, uuid.toString());
                        preparedStatement.setString(8, name);
                        preparedStatement.execute();
                        i++;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    close();
                }

            }
        }
        OddJob.getInstance().getMessageManager().console("Saved Homes: insert:" + i + "; update:" + u);
    }

    public void createAccount(UUID uuid, double startValue, boolean guild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_balances` (`uuid`,`pocket`,`bank`,`guild`) VALUES (?,?,?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setDouble(2, startValue);
            preparedStatement.setDouble(3, startValue);
            preparedStatement.setInt(4, guild ? 1 : 0);
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().console("Created Account");
    }

    public HashMap<String, HashMap<UUID, Double>> loadEcon() {
        HashMap<String, HashMap<UUID, Double>> values = new HashMap<>();
        int g = 0, p = 0, i = 0;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_balances`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                i++;
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                double bank = resultSet.getDouble("bank");
                double pocket = resultSet.getDouble("pocket");
                boolean guild = resultSet.getInt("guild") == 1;
                if (guild) {
                    if (!values.containsKey("guild")) values.put("guild", new HashMap<>());
                    values.get("guild").put(uuid, bank);
                    g++;
                } else {
                    p++;
                    if (!values.containsKey("bank")) values.put("bank", new HashMap<>());
                    values.get("bank").put(uuid, bank);
                    if (!values.containsKey("pocket")) values.put("pocket", new HashMap<>());
                    values.get("pocket").put(uuid, bank);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().console("Loaded Econ: player:" + p + "; guild:" + g + "; total:" + i + ";");
        return values;
    }

    public void saveEcon() {
        int i = 0, p = 0, g = 0;
        try {

            connect();
            for (UUID uuid : OddJob.getInstance().getGuildManager().getGuilds()) {
                if (OddJob.getInstance().getEconManager().hasBankAccount(uuid, true)) {
                    double amount = OddJob.getInstance().getEconManager().getBankBalance(uuid, true);
                    preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `amount` = ? WHERE `uuid` = ?");
                    preparedStatement.setDouble(1, amount);
                    preparedStatement.setString(2, uuid.toString());
                    preparedStatement.executeUpdate();
                    g++;
                    i++;
                }
            }
            for (UUID uuid : OddJob.getInstance().getPlayerManager().getUUIDs()) {
                double pocket = 0.0, bank = 0.0;
                if (OddJob.getInstance().getEconManager().hasBankAccount(uuid, false)) {
                    bank = OddJob.getInstance().getEconManager().getBankBalance(uuid, false);
                }
                if (OddJob.getInstance().getEconManager().hasPocket(uuid)) {
                    pocket = OddJob.getInstance().getEconManager().getPocketBalance(uuid);
                }
                preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `bank` = ?, `pocket` = ? WHERE `uuid` = ?");
                preparedStatement.setDouble(1, bank);
                preparedStatement.setDouble(2, pocket);
                preparedStatement.setString(3, uuid.toString());
                preparedStatement.executeUpdate();
                p++;
                i++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().console("Saved Econ: guild:" + g + "; player:" + p + "; total:" + i + ";");
    }

    public void econSet(UUID uuid, double amount, Econ type) {
        try {
            connect();
            preparedStatementsec = connection.prepareStatement("UPDATE `mine_balances` SET `" + type.name().toLowerCase() + "` = ? WHERE `uuid` = ?");
            preparedStatementsec.setDouble(1, amount);
            preparedStatementsec.setString(2, uuid.toString());
            preparedStatementsec.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public HashMap<UUID, UUID> loadArmorStands() {
        HashMap<UUID, UUID> stands = new HashMap<>();
        int i = 0;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_secured_armorstands`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                i++;
                stands.put(UUID.fromString(resultSet.getString("entity")), UUID.fromString(resultSet.getString("player")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().console("Loaded LockedArmorStands: total:" + i + ";");
        return stands;
    }

    public HashMap<Location, UUID> loadLockedBlocks() {
        HashMap<Location, UUID> blocks = new HashMap<>();
        int i = 0;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_secured_blocks`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                if (world != null) {
                    i++;
                    Location location = new Location(world, resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                    blocks.put(location, UUID.fromString(resultSet.getString("uuid")));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().console("loaded LockedBlocks: total:" + i + ";");
        return blocks;
    }

    public void deleteHome(UUID uuid, String name) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_homes` WHERE `uuid` = ? AND `name` = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, name);
            preparedStatement.execute();
        } catch (SQLException ex) {
        } finally {
            close();
        }
    }

    public boolean insertGuildsChunks(Chunk chunk, UUID guild) {
        boolean ret = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`world`,`x`,`z`,`uuid`) VALUE (?,?,?,?)");
            preparedStatement.setString(1, chunk.getWorld().getUID().toString());
            preparedStatement.setInt(2, chunk.getX());
            preparedStatement.setInt(3, chunk.getZ());
            preparedStatement.setString(4, guild.toString());
            preparedStatement.execute();
            ret = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }
}
