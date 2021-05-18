package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.UUID;

public class MySQLManager {
    protected static PreparedStatement preparedStatement = null;
    protected static Connection connection = null;
    protected static Statement statement = null;
    protected static ResultSet resultSet = null;
    protected static ResultSet resultSetSec = null;
    protected static String prefix = "";

    protected static void connect() throws SQLException {


        if (connection == null) {
            String hostname = OddJob.getInstance().getConfig().getString("SQL.Hostname");
            String database = OddJob.getInstance().getConfig().getString("SQL.Database");
            String username = OddJob.getInstance().getConfig().getString("SQL.Username");
            String password = OddJob.getInstance().getConfig().getString("SQL.Password");
            int port = OddJob.getInstance().getConfig().getInt("SQL.Port");
            String type = OddJob.getInstance().getConfig().getString("SQL.Type");
            prefix = OddJob.getInstance().getConfig().getString("SQL.Prefix");
                connection = DriverManager.getConnection("jdbc:" + type + "://" + hostname + ":"+port+"/" + database, username, password);

        }
    }

    public void init() throws SQLException {
        connect();
        statement = connection.createStatement();
        String sting = "CREATE TABLE IF NOT EXISTS mine_secured_armorstands (" +
                "`id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "`player` VARCHAR(64) NOT NULL," +
                "`entity` VARCHAR(64) NOT NULL)";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_players (" +
                "`uuid` VARCHAR (64) NOT NULL PRIMARY KEY," +
                "`name` VARCHAR(64) NOT NULL," +
                "`whitelist` TEXT DEFAULT ''," +
                "`blacklist` TEXT DEFAULT ''," +
                "`banned` TEXT DEFAULT ''," +
                "`denytpa` TINYINT(1) DEFAULT 0," +
                "`denytrade` TINYINT(1) DEFAULT 0," +
                "`scoreboard` VARCHAR(16) DEFAULT 'Player')";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_secured_blocks (" +
                "`id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "`uuid` VARCHAR (64) NOT NULL," +
                "`world` VARCHAR (64) NOT NULL," +
                "`x` INTEGER NOT NULL ," +
                "`y` INTEGER NOT NULL ," +
                "`z` INTEGER NOT NULL)";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_guilds_chunks (" +
                "`id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "`uuid` VARCHAR (64) NOT NULL," +
                "`world` VARCHAR (64) NOT NULL," +
                "`x` INTEGER NOT NULL ," +
                "`z` INTEGER NOT NULL)";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_lockable_blocks (" +
                "`name` VARCHAR (32) NOT NULL PRIMARY KEY ," +
                "`value` TINYINT(1) DEFAULT 1)";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_lockable_materials (" +
                "`name` VARCHAR (32) NOT NULL PRIMARY KEY ," +
                "`value` TINYINT(1) DEFAULT 1)";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_players_gamemodes (" +
                "`uuid` VARCHAR (64) NOT NULL PRIMARY KEY," +
                "`world` VARCHAR (64) NOT NULL," +
                "`gamemode` VARCHAR (16) NOT NULL)";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_players_teleport (" +
                "`uuid` VARCHAR (64) NOT NULL PRIMARY KEY ," +
                "`world` VARCHAR (64) NOT NULL," +
                "`x` INTEGER NOT NULL ," +
                "`y` INTEGER NOT NULL ," +
                "`z` INTEGER NOT NULL," +
                "`yaw` FLOAT NOT NULL ," +
                "`pitch` FLOAT NOT NULL )";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_warps (" +
                "`id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "`name` VARCHAR (16) NOT NULL," +
                "`password` VARCHAR (32) DEFAULT NULL," +
                "`cost` INTEGER NOT NULL DEFAULT 0," +
                "`world` VARCHAR (64) NOT NULL," +
                "`x` INTEGER NOT NULL ," +
                "`y` INTEGER NOT NULL ," +
                "`z` INTEGER NOT NULL," +
                "`yaw` FLOAT NOT NULL ," +
                "`pitch` FLOAT NOT NULL )";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_players_jailed (" +
                "`uuid` VARCHAR (64) NOT NULL PRIMARY KEY ," +
                "`world` VARCHAR (64) NOT NULL)";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_worlds (" +
                "`uuid` VARCHAR (64) NOT NULL PRIMARY KEY," +
                "`name` VARCHAR (16) NOT NULL)";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_log_DE (" +
                "`id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "`uuid` VARCHAR (64) NOT NULL," +
                "`world` VARCHAR (64) NOT NULL," +
                "`x` INTEGER NOT NULL ," +
                "`y` INTEGER NOT NULL ," +
                "`z` INTEGER NOT NULL," +
                "`action` VARCHAR (32)," +
                "`item` VARCHAR (32)," +
                "`count` INTEGER DEFAULT 1," +
                "`time` INTEGER NOT NULL)";

        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_guilds (" +
                "`uuid` VARCHAR (64) NOT NULL PRIMARY KEY," +
                "`name` VARCHAR (32) NOT NULL ," +
                "`zone` VARCHAR (16) NOT NULL," +
                "`invited_only` TINYINT(1) DEFAULT 0," +
                "`friendly_fire` TINYINT(1) DEFAULT 0," +
                "`invite_permission` VARCHAR (16)," +
                "`open` TINYINT(1) DEFAULT 0)";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_guilds_members (" +
                "`id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "`uuid` VARCHAR (64) NOT NULL," +
                "`player` VARCHAR (64) NOT NULL," +
                "`role` VARCHAR (16) NOT NULL)";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_guilds_chunks (" +
                "`uuid` VARCHAR (64) NOT NULL PRIMARY KEY," +
                "`world` VARCHAR (64) NOT NULL," +
                "`x` INTEGER NOT NULL ," +
                "`z` INTEGER NOT NULL )";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_homes (" +
                "`id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "`uuid` VARCHAR (64) NOT NULL," +
                "`name` VARCHAR (16) NOT NULL," +
                "`world` VARCHAR (64) NOT NULL," +
                "`x` INTEGER NOT NULL ," +
                "`y` INTEGER NOT NULL ," +
                "`z` INTEGER NOT NULL," +
                "`yaw` FLOAT NOT NULL ," +
                "`pitch` FLOAT NOT NULL )";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_balances (" +
                "`uuid` VARCHAR (64) NOT NULL PRIMARY KEY," +
                "`pocket` INTEGER DEFAULT 0," +
                "`bank` INTEGER DEFAULT 0," +
                "`guild` TINYINT(1) DEFAULT 0)";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_players_spirits (" +
                "`id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "`uuid` VARCHAR (64) NOT NULL ," +
                "`entity` VARCHAR (64) NOT NULL," +
                "`time` INTEGER  NOT NULL )";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_guilds_pendings (" +
                "`player` VARCHAR (64) NOT NULL PRIMARY KEY," +
                "`uuid` VARCHAR (64) NOT NULL )";
        statement.execute(sting);
        sting = "CREATE TABLE IF NOT EXISTS mine_guilds_invites (" +
                "`player` VARCHAR (64) NOT NULL PRIMARY KEY," +
                "`uuid` VARCHAR (64) NOT NULL  )";
        statement.execute(sting);
        OddJob.getInstance().log("YEH!!!!");
    }

    protected static void close() {
        close(true);
    }

    protected static void close(boolean force) {
        try {
            if (resultSetSec != null) {
                resultSetSec.close();
                resultSetSec = null;
            }
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (statement != null) {
                statement.close();
                statement = null;
            }

            if (preparedStatement != null) {
                preparedStatement.close();
                preparedStatement = null;
            }
            if (connection != null && force) {
                connection.close();
                connection = null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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

    public void updateTeleport(Player player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_players_teleport` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_players_teleport` SET `world` = ?, `x` = ?,`y` = ?,`z` = ?,`yaw` = ?,`pitch` = ? WHERE `uuid` = ?");
                preparedStatement.setString(1, player.getWorld().getUID().toString());
                preparedStatement.setDouble(2, player.getLocation().getX());
                preparedStatement.setDouble(3, player.getLocation().getY());
                preparedStatement.setDouble(4, player.getLocation().getZ());
                preparedStatement.setFloat(5, player.getLocation().getYaw());
                preparedStatement.setFloat(6, player.getLocation().getPitch());
                preparedStatement.setString(7, player.getUniqueId().toString());
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_players_teleport` (`world`,`x`,`y`,`z`,`yaw`,`pitch`,`uuid`) VALUES (?,?,?,?,?,?,?)");
                preparedStatement.setString(1, player.getWorld().getUID().toString());
                preparedStatement.setDouble(2, player.getLocation().getX());
                preparedStatement.setDouble(3, player.getLocation().getY());
                preparedStatement.setDouble(4, player.getLocation().getZ());
                preparedStatement.setFloat(5, player.getLocation().getYaw());
                preparedStatement.setFloat(6, player.getLocation().getPitch());
                preparedStatement.setString(7, player.getUniqueId().toString());
                preparedStatement.execute();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
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

    public void setPlayerInJail(UUID playerUUID, UUID worldUUID) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_players_jailed` (`world`,`uuid`,`caught`,`free`) VALUES (?,?,UNIX_TIMESTAMP(),?)");
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

    public World playerInJail(UUID player) {
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

    public void setJail(UUID world, String name, Location location) {
        boolean ret = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_worlds` SET `mine_jail` = ? WHERE `uuid` = ?");
            preparedStatement.setString(1, Utility.serializeLoc(location));
            preparedStatement.setString(2, world.toString());
            preparedStatement.executeUpdate();
            ret = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

    }

    public Location getJail(UUID world, String name) {
        Location location = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `mine_jail` FROM `mine_worlds` WHERE `uuid` = ?");
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
                    preparedStatement = connection.prepareStatement("UPDATE `mine_worlds` SET `name` = ? WHERE `uuid` = ?");
                    preparedStatement.setString(2, worldUUID.toString());
                    preparedStatement.setString(1, name);
                    preparedStatement.executeUpdate();
                }
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_worlds` (`uuid`,`name`) VALUES (?,?)");
                preparedStatement.setString(1, worldUUID.toString());
                preparedStatement.setString(2, name);
                preparedStatement.execute();
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


}
