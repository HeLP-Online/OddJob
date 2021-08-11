package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LockSQL extends MySQLManager {
    public static synchronized void createSecuredArmorStand(UUID uuid, Entity entity) {
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

    public static synchronized void deleteSecuredArmorStand(Entity entity) {
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

    public static synchronized void createSecuredBlock(UUID uuid, Location location) {
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

    public static synchronized void deleteSecuredBlock(Location location) {
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

    public static List<Material> getLockableMaterials() {
        List<Material> materials = new ArrayList<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_lockable_materials` WHERE `value` = ?");
                preparedStatement.setBoolean(1, true);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    materials.add(Material.valueOf(resultSet.getString("name")));
                }
            } else {
                for (String string : oddjobConfig.getStringList("lockable_materials")) {
                    materials.add(Material.valueOf(string));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return materials;
    }

    public static HashMap<UUID, UUID> loadSecuredArmorStands() {
        HashMap<UUID, UUID> stands = new HashMap<>();
        int i = 0;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_secured_armorstands`");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    stands.put(UUID.fromString(resultSet.getString("entity")), UUID.fromString(resultSet.getString("player")));
                    i++;
                }
            } else {
                ConfigurationSection cs = oddjobConfig.getConfigurationSection("secured_armorstands");
                if (cs != null) {
                    for (String string : cs.getKeys(false)) {
                        UUID entity = UUID.fromString(string);
                        UUID player = UUID.fromString(cs.getString("player", ""));
                        stands.put(entity, player);
                        i++;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().log("Secured ArmorStands Loaded: " + i);
        return stands;
    }

    public static HashMap<Location, UUID> loadSecuredBlocks() {
        HashMap<Location, UUID> blocks = new HashMap<>();
        int i = 0;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_secured_blocks`");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                    if (world != null) {

                        Location location = new Location(world, resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                        blocks.put(location, UUID.fromString(resultSet.getString("uuid")));
                        i++;
                    }
                }
            } else {
                ConfigurationSection cs = oddjobConfig.getConfigurationSection("secured_blocks");
                if (cs != null) {
                    // world;x;y;z
                    for (String string : cs.getKeys(false)) {
                        World world = Bukkit.getWorld(UUID.fromString(cs.getString("world")));
                        if (world != null) {
                            int x = cs.getInt("x");
                            int y = cs.getInt("y");
                            int z = cs.getInt("z");
                            Location location = new Location(world, x, y, z);
                            blocks.put(location, UUID.fromString(cs.getString("uuid")));
                            i++;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().log("Secured Blocks Loaded: " + i);
        return blocks;
    }

    public static void addMaterial(Material material) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_lockable_materials` WHERE `name` = ?");
                preparedStatement.setString(1, material.name());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_lockable_materials` SET `value` = 1 WHERE `name` = ?");
                    preparedStatement.setString(1, material.name());
                    preparedStatement.executeUpdate();
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_lockable_materials` (`name`,`value`) VALUES (?,?)");
                    preparedStatement.setString(1, material.name());
                    preparedStatement.setInt(2, 1);
                    preparedStatement.execute();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void remove(Material material) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_lockable_materials` SET `value` = 0 WHERE `name` = ?");
                preparedStatement.setString(1, material.name());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
