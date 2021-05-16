package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomeSQL extends MySQLManager {
    public static void delete(UUID uuid, String name) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_homes` WHERE `uuid` = ? AND `name` = ? AND `server` = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, OddJob.getInstance().getConfig().get("server_unique_id").toString());
            preparedStatement.execute();
        } catch (SQLException ignore) {
        } finally {
            close();
        }
    }

    public static void change(UUID uuid, String name, Location location) {
        try {
            connect();
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
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static void add(UUID uuid, String name, Location location) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_homes` (`world`, `x`,`y`,`z`,`yaw`,`pitch`,`uuid` ,`name`,`server`) VALUES (?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, location.getWorld().getUID().toString());
            preparedStatement.setInt(2, location.getBlockX());
            preparedStatement.setInt(3, location.getBlockY());
            preparedStatement.setInt(4, location.getBlockZ());
            preparedStatement.setFloat(5, location.getYaw());
            preparedStatement.setFloat(6, location.getPitch());
            preparedStatement.setString(7, uuid.toString());
            preparedStatement.setString(8, name);
            preparedStatement.setString(9, OddJob.getInstance().getConfig().get("server_unique_id").toString());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static List<String> getList(UUID player) {
        List<String> names = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_homes` WHERE `uuid` = ? AND `server` = ?");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, OddJob.getInstance().getConfig().getString("server_unique_id"));
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return names;
    }

    public static Location get(UUID uuid, String name) {
        Location location = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_homes` WHERE `uuid` = ? AND `name` = ? AND `server` = ? ");
            preparedStatement.setString(1,uuid.toString());
            preparedStatement.setString(2,name);
            preparedStatement.setString(3,OddJob.getInstance().getConfig().getString("server_unique_id"));
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                UUID world = UUID.fromString(resultSet.getString("world"));
                location = new Location(Bukkit.getWorld(world),resultSet.getDouble("x"),resultSet.getDouble("y"),resultSet.getDouble("z"),resultSet.getFloat("yaw"),resultSet.getFloat("pitch"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            close();
        }
        return location;
    }
}