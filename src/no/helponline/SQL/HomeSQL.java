package no.helponline.SQL;

import no.helponline.Managers.MySQLManager;
import no.helponline.OddJob;
import no.helponline.Utils.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class HomeSQL extends MySQLManager {
    public static void delete(UUID uuid, String name) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_homes` WHERE `uuid` = ? AND `name` = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, name);
            preparedStatement.execute();
        } catch (SQLException ignore) {
        } finally {
            close();
        }
    }

    public static HashMap<UUID, Home> load() {
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
        } catch (SQLException ignore) {
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().load("Homes", i);
        return homes;
    }

    public static void save(HashMap<UUID, Home> homes) {
        int i = 0, u = 0;
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
                } catch (SQLException ignore) {
                } finally {
                    close();
                }

            }
        }
        OddJob.getInstance().getMessageManager().save("Homes", i, u);
    }
}