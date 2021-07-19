package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Account.Account;
import com.spillhuset.Utils.Enum.Types;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class CurrencySQL extends MySQLManager {
    public static void save(HashMap<UUID, Account> accounts) {
        try {
            if (connect()) {
                for (Account account : accounts.values()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `bank` = ?, `pocket` = ? WHERE `uuid` = ?");
                    preparedStatement.setDouble(1,account.get(Types.AccountType.bank));
                    preparedStatement.setDouble(2,account.get(Types.AccountType.pocket));
                    preparedStatement.setString(3,account.getUuid().toString());
                    preparedStatement.executeUpdate();
                }
            } else {
                for (Account account : accounts.values()) {
                    String string = account.getUuid().toString();
                    oddjobConfig.set("balances."+string+".bank",account.get(Types.AccountType.bank));
                    oddjobConfig.set("balances."+string+".pocket",account.get(Types.AccountType.pocket));
                }
                oddjobConfig.save(oddjobConfigFile);
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static HashMap<UUID, Account> load() {
        HashMap<UUID, Account> accounts = new HashMap<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_balances`");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    double bank = resultSet.getDouble("bank");
                    double pocket = resultSet.getDouble("pocket");
                    boolean guild = resultSet.getInt("guild") == 1;
                    accounts.put(uuid,new Account(uuid,bank,pocket,guild));
                }
            } else {
                if (oddjobConfig.getConfigurationSection("currency") != null) {
                    for (String string : oddjobConfig.getConfigurationSection("currency").getKeys(false)) {
                        ConfigurationSection user = oddjobConfig.getConfigurationSection("currency." + string);
                        if (user != null) {
                            boolean guild = user.getBoolean("guild");
                            double bank = user.getDouble("bank");
                            double pocket = user.getDouble("pocket");
                            UUID uuid = UUID.fromString(string);
                            accounts.put(uuid,new Account(uuid,bank,pocket,guild));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return accounts;
    }

    /**
     * @param uuid        UUID uuid of guild or player
     * @param pocketStart double Starting pocket value
     * @param bankStart   double Starting bank value
     * @param guild       boolean is a guild?
     */
    public static void createAccount(UUID uuid, double bankStart, double pocketStart, boolean guild) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_balances` (`uuid`,`pocket`,`bank`,`guild`) VALUES (?,?,?,?)");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setDouble(2, pocketStart);
                preparedStatement.setDouble(3, bankStart);
                preparedStatement.setInt(4, guild ? 1 : 0);
                preparedStatement.execute();
            } else {
                oddjobConfig.set("balances." + uuid.toString() + ".bank", bankStart);
                oddjobConfig.set("balances." + uuid.toString() + ".pocket", pocketStart);
                oddjobConfig.set("balances." + uuid.toString() + ".guild", guild);

                oddjobConfig.save(oddjobConfigFile);
                load();
            }
            OddJob.getInstance().getMessageManager().console("Currency created: " + ((guild) ? "guild" : "player") + " " + uuid.toString());
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void setBalance(UUID uuid, double amount, Types.AccountType account) {
        try {
            if (connect()) {
                switch (account) {
                    case bank -> preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `bank` = ? WHERE `uuid` = ?");
                    case pocket -> preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `pocket` = ? WHERE `uuid` = ?");
                }
                preparedStatement.setDouble(1, amount);
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.executeUpdate();
            } else {
                switch (account) {
                    case bank -> oddjobConfig.set("balances." + uuid.toString() + ".bank", amount);
                    case pocket -> oddjobConfig.set("balances." + uuid.toString() + ".pocket", amount);
                }
                oddjobConfig.save(oddjobConfigFile);
            }
            OddJob.getInstance().log("Currency of "+uuid.toString()+" changed with amount");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}
