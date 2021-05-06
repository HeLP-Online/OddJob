package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Currency;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class CurrencySQL extends MySQLManager {
    public static void save() {
        try {
            connect();
            for (UUID uuid : OddJob.getInstance().getGuildManager().getGuilds().keySet()) {
                if (OddJob.getInstance().getCurrencyManager().hasBankAccount(uuid, Currency.bank_guild)) {
                    double amount = OddJob.getInstance().getCurrencyManager().getBankBalance(uuid, Currency.bank_guild);
                    preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `bank` = ? WHERE `uuid` = ?");
                    preparedStatement.setDouble(1, amount);
                    preparedStatement.setString(2, uuid.toString());
                    preparedStatement.executeUpdate();
                }
            }
            for (UUID uuid : OddJob.getInstance().getPlayerManager().getUUIDs()) {
                double pocket = 0.0, bank = 0.0;
                if (OddJob.getInstance().getCurrencyManager().hasBankAccount(uuid, Currency.bank_player)) {
                    bank = OddJob.getInstance().getCurrencyManager().getBankBalance(uuid, Currency.pocket);
                }
                if (OddJob.getInstance().getCurrencyManager().hasPocket(uuid)) {
                    pocket = OddJob.getInstance().getCurrencyManager().getPocketBalance(uuid);
                }
                preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `bank` = ?, `pocket` = ? WHERE `uuid` = ?");
                preparedStatement.setDouble(1, bank);
                preparedStatement.setDouble(2, pocket);
                preparedStatement.setString(3, uuid.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }
    public static HashMap<String, HashMap<UUID, Double>> load() {
        HashMap<String, HashMap<UUID, Double>> values = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_balances`");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                double bank = resultSet.getDouble("bank");
                double pocket = resultSet.getDouble("pocket");
                boolean guild = resultSet.getInt("guild") == 1;
                if (guild) {
                    if (!values.containsKey("guild")) values.put("guild", new HashMap<>());
                    values.get("guild").put(uuid, bank);
                } else {
                    if (!values.containsKey("bank")) values.put("bank", new HashMap<>());
                    values.get("bank").put(uuid, bank);
                    if (!values.containsKey("pocket")) values.put("pocket", new HashMap<>());
                    values.get("pocket").put(uuid, bank);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return values;
    }

    public static void createAccount(UUID uuid, double startValue, Currency account) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_balances` (`uuid`,`pocket`,`bank`,`guild`) VALUES (?,?,?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setDouble(2, startValue);
            preparedStatement.setDouble(3, startValue);
            preparedStatement.setInt(4, account.equals(Currency.bank_guild) ? 1 : 0);
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().console("Created Account");
    }

    public static void setBalance(UUID uuid, double amount, Currency account) {
        try{
            connect();
            switch (account) {
                case bank_player:
                case bank_guild:
                    preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `bank` = ? WHERE `uuid` = ?");
                    break;
                case pocket:
                    preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `pocket` = ? WHERE `uuid` = ?");
                    break;
            }
            preparedStatement.setDouble(1,amount);
            preparedStatement.setString(2,uuid.toString());
            preparedStatement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            close();
        }
    }
}
