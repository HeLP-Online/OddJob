package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.Role;
import no.helponline.Utils.Enum.ScoreBoard;
import no.helponline.Utils.Enum.Zone;
import no.helponline.Utils.Guild;
import no.helponline.Utils.Home;
import no.helponline.Utils.Odd.OddPlayer;
import no.helponline.Utils.Utility;
import no.helponline.Utils.Warp;
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
    protected static PreparedStatement preparedStatement = null;
    protected static PreparedStatement preparedStatementsec = null;
    protected static Connection connection = null;
    protected static Statement statement = null;
    protected static ResultSet resultSet = null;
    protected static ResultSet resultSetsec = null;

    protected static void connect() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:mysql://10.0.4.9/odderik", "odderik", "503504");
        }
    }

    public void init() throws SQLException {
        this.connect();
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
                "`id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,"+
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

    public synchronized void createSecuredArmorStand(UUID uuid, Entity entity) {
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

    public synchronized void deleteSecuredArmorStand(Entity entity) {
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

    public synchronized void createSecuredBlock(UUID uuid, Location location) {
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

    public synchronized void deleteSecuredBlock(Location location) {
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

    public void createWarp(String name, Player player, String password) {
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

    public void saveGuilds(HashMap<UUID, Guild> guilds) {
        int gi = 0, gu = 0, mi = 0, mu = 0;
        for (UUID guildUUID : guilds.keySet()) {
            Guild guild = OddJob.getInstance().getGuildManager().getGuild(guildUUID);
            String name = guild.getName();
            Zone zone = guild.getZone();
            boolean invitedOnly = guild.getInvitedOnly();
            boolean friendlyFire = guild.getFriendlyFire();
            boolean open = guild.getOpen();
            Role permissionInviteRole = guild.getPermissionInvite();
            HashMap<UUID, Role> members = guild.getMembers();

            try {
                connect();
                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds` WHERE `uuid` = ?");
                preparedStatement.setString(1, guildUUID.toString());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_guilds` SET `name` = ?,`zone` = ?,`invited_only` = ?,`friendly_fire` = ?,`invite_permission` = ?,`open` = ? WHERE `uuid` = ?");
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, zone.name());
                    preparedStatement.setInt(3, invitedOnly ? 1 : 0);
                    preparedStatement.setInt(4, friendlyFire ? 1 : 0);
                    preparedStatement.setString(5, permissionInviteRole.name());
                    preparedStatement.setInt(6, open ? 1 : 0);
                    preparedStatement.setString(7, guildUUID.toString());
                    preparedStatement.executeUpdate();
                    gu++;
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds` (`uuid`,`name`,`zone`,`invited_only`,`friendly_fire`,`invite_permission`,`open`) VALUES (?,?,?,?,?,?,?)");
                    preparedStatement.setString(1, guildUUID.toString());
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, zone.name());
                    preparedStatement.setInt(4, invitedOnly ? 1 : 0);
                    preparedStatement.setInt(5, friendlyFire ? 1 : 0);
                    preparedStatement.setString(6, permissionInviteRole.name());
                    preparedStatement.setInt(7, open ? 1 : 0);
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
        OddJob.getInstance().getMessageManager().save("Guilds", gi, gu);
        OddJob.getInstance().getMessageManager().save("Members", mi, mu);
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
                        resultSet.getInt("open") == 1,
                        members
                ));
                g++;

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().load("Guilds", g);
        OddJob.getInstance().getMessageManager().load("Guilds Members", m);
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
        OddJob.getInstance().getMessageManager().save("Chunks", i, u);
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

    public void disbandGuild(UUID guild) {
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

    public void savePlayers(HashMap<UUID, OddPlayer> players) {
        int u = 0;
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
                u++;
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        }
        OddJob.getInstance().getMessageManager().update("Players", u);
    }

    public HashMap<UUID, OddPlayer> loadPlayers() {
        HashMap<UUID, OddPlayer> map = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                OddJob.getInstance().log("Loaded: " + resultSet.getString("name"));
                List<UUID> whitelist = new ArrayList<>();
                if (!resultSet.getString("whitelist").equals("")) {
                    for (String uid : resultSet.getString("whitelist").split(",")) {
                        whitelist.add(UUID.fromString(uid));
                    }
                }
                List<UUID> blacklist = new ArrayList<>();
                if (!resultSet.getString("blacklist").equals("")) {
                    for (String uid : resultSet.getString("blacklist").split(",")) {
                        blacklist.add(UUID.fromString(uid));
                    }
                }
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                ScoreBoard scoreBoard = null;
                try {
                    scoreBoard = ScoreBoard.valueOf(resultSet.getString("scoreboard"));
                } catch (Exception e) {
                    e.printStackTrace();
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
        OddJob.getInstance().getMessageManager().load("Players", map.size());
        return map;
    }



    public void createEconAccount(UUID uuid, double startValue, boolean guild) {
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

    public HashMap<UUID, UUID> loadSecuredArmorStands() {
        HashMap<UUID, UUID> stands = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_secured_armorstands`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                stands.put(UUID.fromString(resultSet.getString("entity")), UUID.fromString(resultSet.getString("player")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().load("Secured ArmorStands", stands.size());
        return stands;
    }

    public HashMap<Location, UUID> loadSecuredBlocks() {
        HashMap<Location, UUID> blocks = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_secured_blocks`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                if (world != null) {

                    Location location = new Location(world, resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                    blocks.put(location, UUID.fromString(resultSet.getString("uuid")));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().load("Secured Blocks", blocks.size());
        return blocks;
    }

    public boolean saveGuildsChunks(Chunk chunk, UUID guild) {
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

    /**
     * @return HashMap UUID of Player and UUID of Guild
     */
    public HashMap<UUID, UUID> loadGuildsPendings() {
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
        OddJob.getInstance().getMessageManager().load("Guilds Pendings", pending.size());
        return pending;
    }

    /**
     * @return HashMap UUID of Player and UUID of Guild
     */
    public HashMap<UUID, UUID> loadGuildsInvites() {
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
        OddJob.getInstance().getMessageManager().load("Guilds Invites", invites.size());
        return invites;
    }

    public HashMap<String, Warp> loadWarps() {
        HashMap<String, Warp> warps = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_warps`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                if (world != null) {
                    Location location = new Location(world, resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"), resultSet.getFloat("yaw"), resultSet.getFloat("pitch"));
                    warps.put(resultSet.getString("name"), new Warp(resultSet.getString("name"), location, resultSet.getString("passw"), resultSet.getDouble("cost")));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().load("Warps", warps.size());
        return warps;
    }

    public void saveWarps(HashMap<String, Warp> warps) {
        int u = 0, i = 0;
        for (String name : warps.keySet()) {
            try {
                Location location = warps.get(name).get();
                connect();
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_warps` WHERE `name` = ?");
                preparedStatement.setString(1, name);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_warps` SET `world` = ?,`cost` = ?,`x` = ?,`y` = ?,`z` = ?,`yaw` = ?,`pitch` = ?,`passw` = ? WHERE `name` = ?");
                    preparedStatement.setString(1, location.getWorld().getUID().toString());
                    preparedStatement.setDouble(2, warps.get(name).getCost());
                    preparedStatement.setDouble(3, location.getBlockX());
                    preparedStatement.setDouble(4, location.getBlockY());
                    preparedStatement.setDouble(5, location.getBlockZ());
                    preparedStatement.setFloat(5, location.getYaw());
                    preparedStatement.setFloat(6, location.getPitch());
                    preparedStatement.setString(7, warps.get(name).getPassword());
                    preparedStatement.setString(8, name);
                    preparedStatement.executeUpdate();
                    u++;
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_warps` (`world`,`cost`,`x`,`y`,`z`,`yaw`,`pitch`,`passw`,`name`) VALUES(?,?,?,?,?,?,?,?)");
                    preparedStatement.setString(1, location.getWorld().getUID().toString());
                    preparedStatement.setDouble(2, warps.get(name).getCost());
                    preparedStatement.setDouble(3, location.getBlockX());
                    preparedStatement.setDouble(4, location.getBlockY());
                    preparedStatement.setDouble(5, location.getBlockZ());
                    preparedStatement.setFloat(5, location.getYaw());
                    preparedStatement.setFloat(6, location.getPitch());
                    preparedStatement.setString(7, warps.get(name).getPassword());
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
        OddJob.getInstance().getMessageManager().save("Warps", i, u);
    }

    public HashMap<UUID, UUID> loadSpirits() {
        HashMap<UUID, UUID> spirits = new HashMap<>();

        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players_spirits`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                spirits.put(UUID.fromString(resultSet.getString("entity")), UUID.fromString(resultSet.getString("uuid")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        OddJob.getInstance().getMessageManager().load("Spirits", spirits.size());
        return spirits;
    }

    public void setSpirit(UUID entity, UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_players_spirits` (`entity`,`uuid`,`time`) VALUES (?,?,UNIX_TIMESTAMP())");
            preparedStatement.setString(1, entity.toString());
            preparedStatement.setString(2, player.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void deleteSpirit(UUID entity) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_players_spirits` WHERE `entity` = ?");
            preparedStatement.setString(1, entity.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void deleteGuildMember(UUID player) {
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

    public UUID getGuildUUIDByChunk(Chunk chunk) {
        UUID uuid = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds_chunks` WHERE `world` = ? AND `x` = ? AND `z` = ?");
            preparedStatement.setString(1, chunk.getWorld().getUID().toString());
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

    public void createGuildClaim(Chunk chunk, UUID guild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`x`,`z`,`world`,`uuid`) VALUES (?,?,?,?)");
            preparedStatement.setInt(1, chunk.getX());
            preparedStatement.setInt(2, chunk.getZ());
            preparedStatement.setString(3, chunk.getWorld().getUID().toString());
            preparedStatement.setString(4, guild.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public void setScoreboard(UUID uuid, ScoreBoard score) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_players` SET `scoreboard` = ? WHERE `uuid` = ?");
            preparedStatement.setString(1, score.name());
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }
}
