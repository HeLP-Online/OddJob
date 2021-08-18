package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;

public class PriceSQL extends MySQLManager {
    public static double get(String name, boolean friendly) {
        double price = 0.0;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_price` WHERE `name` = ?");
                preparedStatement.setString(1, name);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    price = (friendly) ? resultSet.getDouble("friendly") : resultSet.getDouble("price");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return price;
    }
}
