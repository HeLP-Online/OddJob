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
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `" + prefix + "players` WHERE `uuid` = ?");
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

                oddPlayer = new OddPlayer(uuid, blacklist, whitelist, denyTpa, name, banned, scoreboard, denyTrade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return oddPlayer;
    }
}
