package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.Utils.Enum.ScoreBoard;
import com.spillhuset.Utils.Odd.OddPlayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerSQL extends MySQLManager {
    public static OddPlayer load(UUID uuid) {
        List<UUID> blacklist = new ArrayList<>();
        List<UUID> whitelist = new ArrayList<>();
        boolean denyTpa, denyTrade;
        String name, banned;
        ScoreBoard scoreboard;
        OddPlayer oddPlayer = null;
        int maxHomes;
        try {
            connect();
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
                banned = resultSet.getString("banned");

                scoreboard = ScoreBoard.valueOf(resultSet.getString("scoreboard"));
                maxHomes = resultSet.getInt("maxhomes");

                oddPlayer = new OddPlayer(uuid, blacklist, whitelist, denyTpa, name, banned, scoreboard, denyTrade,maxHomes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return oddPlayer;
    }

    public static void save(OddPlayer oddPlayer) {
        try {
            StringBuilder whitelist = new StringBuilder();
            for (UUID uuid:oddPlayer.getWhitelist()) {
                whitelist.append(uuid.toString()).append(",");
            }
            whitelist.deleteCharAt(whitelist.lastIndexOf(","));
            StringBuilder blacklist = new StringBuilder();
            for (UUID uuid:oddPlayer.getBlacklist()) {
                blacklist.append(uuid.toString()).append(",");
            }
            blacklist.deleteCharAt(blacklist.lastIndexOf(","));
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_players` SET `blacklist` = ?,`whitelist` = ?,`scoreboard` = ?,`maxhomes` = ?,`denytpa` = ?,`denytrade` = ? WHERE `uuid` =?");
            preparedStatement.setString(1,blacklist.toString());
            preparedStatement.setString(2,whitelist.toString());
            preparedStatement.setString(3,oddPlayer.getScoreboard().name());
            preparedStatement.setInt(4,oddPlayer.getMaxHomes());
            preparedStatement.setBoolean(5, oddPlayer.getDenyTpa());
            preparedStatement.setBoolean(6, oddPlayer.getDenyTrade());
            preparedStatement.setString(7,oddPlayer.getUuid().toString());
        } catch (SQLException e) {
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
}
