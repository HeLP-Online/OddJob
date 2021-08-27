package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;

import java.sql.SQLException;
import java.util.UUID;

public class MessageSQL extends MySQLManager {
    public static void store(String string, UUID receiver) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_messages` (`text`,`destination`,`time`) VALUES (?,?,UNIX_TIMESTAMP())");
                preparedStatement.setString(1, string);
                preparedStatement.setString(2, receiver.toString());
                preparedStatement.executeQuery();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
