package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.ScoreBoard;
import com.spillhuset.Utils.Odd.OddPlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class PlayerSQL extends MySQLManager {
    public static OddPlayer load(UUID uuid) {
        List<UUID> blacklist = new ArrayList<>();
        List<UUID> whitelist = new ArrayList<>();
        boolean denyTpa = false, denyTrade = false;
        String name = "", banned = "";
        ScoreBoard scoreboard = ScoreBoard.Player;
        OddPlayer oddPlayer = null;
        int maxHomes = 0;
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

                }
            } else {
                if (oddjobConfig.getConfigurationSection("players") != null) {
                    for (String string : oddjobConfig.getConfigurationSection("players").getKeys(false)) {
                        ConfigurationSection cs = oddjobConfig.getConfigurationSection("players."+string);
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
                            banned = !cs.getString("banned").equals("") ? cs.getString("banned") : null;

                            scoreboard = ScoreBoard.valueOf(cs.getString("scoreboard"));
                            maxHomes = cs.getInt("maxhomes");


                        }
                    }
                }
            }
            oddPlayer = new OddPlayer(uuid,blacklist,whitelist,denyTpa,name,banned,scoreboard,denyTrade,maxHomes);
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

                preparedStatement = connection.prepareStatement("UPDATE `mine_players` SET `blacklist` = ?,`whitelist` = ?,`scoreboard` = ?,`maxhomes` = ?,`denytpa` = ?,`denytrade` = ?,`banned` =? WHERE `uuid` =?");
                preparedStatement.setString(1, blacklist.toString());
                preparedStatement.setString(2, whitelist.toString());
                preparedStatement.setString(3, oddPlayer.getScoreboard().name());
                preparedStatement.setInt(4, oddPlayer.getMaxHomes());
                preparedStatement.setBoolean(5, oddPlayer.getDenyTpa());
                preparedStatement.setBoolean(6, oddPlayer.getDenyTrade());
                preparedStatement.setString(7, oddPlayer.getBanned());
                preparedStatement.setString(8, oddPlayer.getUuid().toString());
                preparedStatement.executeUpdate();
            }else {
                String string = oddPlayer.getUuid().toString();
                oddjobConfig.set("players."+string+".blacklist",oddPlayer.getBlacklist());
                oddjobConfig.set("players."+string+".whitelist",oddPlayer.getWhitelist());
                oddjobConfig.set("players."+string+".scoreboard",oddPlayer.getScoreboard().name());
                oddjobConfig.set("players."+string+".maxhomes",oddPlayer.getMaxHomes());
                oddjobConfig.set("players."+string+".denytpa",oddPlayer.getDenyTpa());
                oddjobConfig.set("players."+string+".denytrade",oddPlayer.getDenyTrade());
                oddjobConfig.set("players."+string+".banned",oddPlayer.getBanned());
                oddjobConfig.set("players."+string+".name",oddPlayer.getName());

                oddjobConfig.save(oddjobConfigFile);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
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
        for (UUID uuid : players.keySet()) {
            save(players.get(uuid));
        }
    }

    public static HashMap<UUID, OddPlayer> load() {
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
                    player.addAll(Collections.singleton(UUID.fromString(String.valueOf(oddjobConfig.getStringList("players")))));
                    OddJob.getInstance().log("players: " + player.size());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }

        for (UUID uuid : player) {
            players.put(uuid, load(uuid));
        }
        return players;
    }
}
