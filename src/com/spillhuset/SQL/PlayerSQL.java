package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.ScoreBoard;
import com.spillhuset.Utils.Odd.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class PlayerSQL extends MySQLManager {
    public static OddPlayer load(UUID uuid) {
        List<UUID> blacklist = new ArrayList<>();
        List<UUID> whitelist = new ArrayList<>();
        boolean denyTpa = false, denyTrade = false;
        String name = "", banned = null;
        ScoreBoard scoreboard = ScoreBoard.Player;
        OddPlayer oddPlayer = null;
        int maxHomes = 0;
        GameMode gameMode = GameMode.SURVIVAL;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players` WHERE `uuid` = ?");
                preparedStatement.setString(1, uuid.toString());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    if (!resultSet.getString("blacklist").equals("")) {
                        for (String string : resultSet.getString("blacklist").split(";")) {
                            blacklist.add(UUID.fromString(string));
                        }
                    }
                    if (!resultSet.getString("whitelist").equals("")) {
                        for (String string : resultSet.getString("whitelist").split(";")) {
                            whitelist.add(UUID.fromString(string));
                        }
                    }
                    denyTpa = resultSet.getBoolean("denytpa");
                    denyTrade = resultSet.getBoolean("denytrade");

                    name = resultSet.getString("name");
                    banned = resultSet.getString("banned") != null ? resultSet.getString("banned") : null;

                    scoreboard = ScoreBoard.valueOf(resultSet.getString("scoreboard"));
                    maxHomes = resultSet.getInt("maxhomes");

                    gameMode = GameMode.valueOf(resultSet.getString("gm"));

                }
            } else {
                if (oddjobConfig.getConfigurationSection("players") != null) {
                    for (String string : oddjobConfig.getConfigurationSection("players").getKeys(false)) {
                        ConfigurationSection cs = oddjobConfig.getConfigurationSection("players." + string);
                        if (cs != null) {
                            // Blacklist
                            List<String> black = cs.getStringList("blacklist");
                            if (black.size() > 0) {
                                for (int i = 0; i <= black.size(); i++) {
                                    blacklist.add(UUID.fromString(black.get(i)));
                                }
                            }

                            // Whitelist
                            List<String> white = cs.getStringList("whitelist");
                            if (white.size() > 0) {
                                for (int i = 0; i <= white.size(); i++) {
                                    whitelist.add(UUID.fromString(white.get(i)));
                                }
                            }

                            denyTpa = cs.getBoolean("denytpa");
                            denyTrade = cs.getBoolean("denytrade");

                            name = cs.getString("name");
                            banned = !Objects.equals(cs.getString("banned"), "") ? cs.getString("banned") : null;

                            scoreboard = ScoreBoard.valueOf(cs.getString("scoreboard"));
                            maxHomes = cs.getInt("maxhomes");

                            gameMode = GameMode.valueOf(cs.getString("gm", GameMode.SURVIVAL.name()));
                        }
                    }
                } else {
                    name = Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName();
                }
            }
            oddPlayer = new OddPlayer(uuid, blacklist, whitelist, denyTpa, name, banned, scoreboard, denyTrade, maxHomes, gameMode);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return oddPlayer;
    }

    public static void save(OddPlayer oddPlayer) {
        try {
            if (connect()) {
                StringBuilder whitelist = new StringBuilder();
                if (oddPlayer.getWhitelist().size() >= 1) {
                    for (UUID uuid : oddPlayer.getWhitelist()) {
                        whitelist.append(uuid.toString()).append(",");
                    }
                    whitelist.deleteCharAt(whitelist.lastIndexOf(","));
                }
                StringBuilder blacklist = new StringBuilder();
                if (oddPlayer.getBlacklist().size() >= 1) {
                    for (UUID uuid : oddPlayer.getBlacklist()) {
                        blacklist.append(uuid.toString()).append(",");
                    }
                    blacklist.deleteCharAt(blacklist.lastIndexOf(","));
                }

                preparedStatement = connection.prepareStatement("UPDATE `mine_players` SET `blacklist` = ?,`whitelist` = ?,`scoreboard` = ?,`maxhomes` = ?,`denytpa` = ?,`denytrade` = ?,`banned` =?,`gm` =? WHERE `uuid` =?");
                preparedStatement.setString(1, blacklist.toString());
                preparedStatement.setString(2, whitelist.toString());
                preparedStatement.setString(3, oddPlayer.getScoreboard().name());
                preparedStatement.setInt(4, oddPlayer.getMaxHomes());
                preparedStatement.setBoolean(5, oddPlayer.getDenyTpa());
                preparedStatement.setBoolean(6, oddPlayer.getDenyTrade());
                preparedStatement.setString(7, oddPlayer.getBanned());
                preparedStatement.setString(8, oddPlayer.getGameMode().name());
                preparedStatement.setString(9, oddPlayer.getUuid().toString());
                preparedStatement.executeUpdate();
            } else {
                String string = oddPlayer.getUuid().toString();
                oddjobConfig.set("players." + string + ".blacklist", oddPlayer.getBlacklist());
                oddjobConfig.set("players." + string + ".whitelist", oddPlayer.getWhitelist());
                oddjobConfig.set("players." + string + ".scoreboard", oddPlayer.getScoreboard().name());
                oddjobConfig.set("players." + string + ".maxhomes", oddPlayer.getMaxHomes());
                oddjobConfig.set("players." + string + ".denytpa", oddPlayer.getDenyTpa());
                oddjobConfig.set("players." + string + ".denytrade", oddPlayer.getDenyTrade());
                oddjobConfig.set("players." + string + ".banned", oddPlayer.getBanned());
                oddjobConfig.set("players." + string + ".name", oddPlayer.getName());
                oddjobConfig.set("players." + string + ".gm", oddPlayer.getGameMode());

                oddjobConfig.save(oddjobConfigFile);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static void setGameMode(UUID uuid, UUID world, GameMode gameMode,UUID server) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players_gamemodes` WHERE `uuid`  = ? AND `world` = ? AND `server` = ?");
            preparedStatement.setString(2, world.toString());
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(3, server.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_players_gamemodes` SET `gamemode` = ? WHERE `uuid` = ? AND `world` = ? AND `server` = ?");
                preparedStatement.setString(1, gameMode.name());
                preparedStatement.setString(3, world.toString());
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.setString(4, server.toString());
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connection.prepareStatement("INSERT `mine_players_gamemodes` (`uuid`,`world`,`gamemode`,`server`) VALUES (?,?,?,?)");
                preparedStatement.setString(3, gameMode.name());
                preparedStatement.setString(2, world.toString());
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(4, server.toString());
                preparedStatement.execute();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void setScoreboard(UUID uuid, ScoreBoard score) {
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

    public static void save(HashMap<UUID, OddPlayer> players) {
        int i = 0;
        for (UUID uuid : players.keySet()) {
            if (players.get(uuid).save) {
                save(players.get(uuid));
                i++;
                players.get(uuid).save = false;
            }
        }
        OddJob.getInstance().log("Players Saved: " + i);
    }

    public static HashMap<UUID, OddPlayer> load() {
        int i = 0;
        HashMap<UUID, OddPlayer> players = new HashMap<>();
        List<UUID> player = new ArrayList<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_players`");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    player.add(UUID.fromString(resultSet.getString("uuid")));
                }
            } else {
                if (oddjobConfig != null && !oddjobConfig.getStringList("players").isEmpty()) {
                    for (String string : oddjobConfig.getConfigurationSection("players").getKeys(false)) {
                        player.add(UUID.fromString(string));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }

        for (UUID uuid : player) {
            players.put(uuid, load(uuid));
            i++;
        }
        OddJob.getInstance().log("Players Loaded: " + i);
        return players;
    }

    public static GameMode getGameMode(UUID player, UUID world, UUID server) {
        GameMode gameMode = GameMode.SURVIVAL;

        try {
            if(connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_players_gamemodes` WHERE `uuid` = ? AND `world` = ? AND `server` = ?");
                preparedStatement.setString(1,player.toString());
                preparedStatement.setString(2,world.toString());
                preparedStatement.setString(3,server.toString());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    gameMode = GameMode.valueOf(resultSet.getString("gamemode"));
                    OddJob.getInstance().log("Loaded:"+resultSet.getString("gamemode"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return gameMode;
    }
}
