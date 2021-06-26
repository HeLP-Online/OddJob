package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class WarpSQL extends MySQLManager {
    public static void add(String name, Player player, String password, double cost, UUID uuid) {
        try {
            Location location = player.getLocation();
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_warps` (`name`,`world`,`x`,`y`,`z`,`yaw`,`pitch`,`passwd`,`server`,`cost`,`uuid`) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, player.getLocation().getWorld().getUID().toString());
            preparedStatement.setDouble(3, location.getX());
            preparedStatement.setDouble(4, location.getY());
            preparedStatement.setDouble(5, location.getZ());
            preparedStatement.setFloat(6, location.getYaw());
            preparedStatement.setFloat(7, location.getPitch());
            preparedStatement.setString(8, password);
            preparedStatement.setString(9, OddJob.getInstance().getServerId().toString());
            preparedStatement.setDouble(10, cost);
            preparedStatement.setString(11, uuid.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static boolean del(UUID uuid, String password) {
        boolean a = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_warps` WHERE `uuid` = ? AND `passwd` = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, password);
            preparedStatement.execute();
            a = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return a;
    }

    public static HashMap<UUID, Warp> loadWarps() {
        HashMap<UUID, Warp> warps = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_warps` WHERE `server` = ?");
            preparedStatement.setString(1, OddJob.getInstance().getServerId().toString());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                UUID worldUUID = UUID.fromString(resultSet.getString("world"));
                World world = Bukkit.getWorld(worldUUID);
                if(world == null) continue;
                Location location = new Location(
                        world,
                        resultSet.getDouble("x"),
                        resultSet.getDouble("y"),
                        resultSet.getDouble("z"),
                        resultSet.getFloat("yaw"),
                        resultSet.getFloat("pitch"));
                Warp warp = new Warp(
                        resultSet.getString("name"),
                        location,
                        resultSet.getString("passwd"),
                        resultSet.getDouble("cost"),
                        uuid);
                warps.put(uuid, warp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return warps;
    }

    public static void saveWarps(HashMap<UUID, Warp> warps) {
        try {
            connect();

            for (UUID uuid : warps.keySet()) {
                Warp warp = warps.get(uuid);
                Location location = warp.getLocation();
                UUID world = location.getWorld().getUID();

                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_warps` WHERE `uuid` = ?");
                preparedStatement.setString(1,uuid.toString());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_warps` SET `name` = ?,`passwd` = ?,`cost` = ?,`world` = ?,`x` = ?,`y` = ?,`z` = ?,`yaw` = ?,`pitch` = ? WHERE `uuid` = ?");
                    preparedStatement.setString(1,warp.getName());
                    preparedStatement.setString(2,warp.getPassword());
                    preparedStatement.setDouble(3,warp.getCost());
                    preparedStatement.setString(4,world.toString());
                    preparedStatement.setDouble(5,location.getX());
                    preparedStatement.setDouble(6,location.getY());
                    preparedStatement.setDouble(7,location.getZ());
                    preparedStatement.setFloat(8,location.getYaw());
                    preparedStatement.setFloat(9,location.getPitch());
                    preparedStatement.setString(10,uuid.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}
