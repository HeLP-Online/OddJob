package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Currency;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class CurrencySQL extends MySQLManager {
    public static void save() {
        try {
            if (connect()) {
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
                        bank = OddJob.getInstance().getCurrencyManager().getBankBalance(uuid, Currency.bank_player);
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
            } else {
                for (UUID uuid : OddJob.getInstance().getGuildManager().getGuilds().keySet()) {
                    String string = uuid.toString();
                    oddjobConfig.set("balances."+string+".bank",OddJob.getInstance().getCurrencyManager().getBankBalance(uuid,Currency.bank_guild));
                }
                for (UUID uuid : OddJob.getInstance().getPlayerManager().getUUIDs()) {
                    String string = uuid.toString();
                    if (OddJob.getInstance().getCurrencyManager().hasBankAccount(uuid,Currency.bank_player)) {
                        oddjobConfig.set("balances."+string+".bank",OddJob.getInstance().getCurrencyManager().getBankBalance(uuid,Currency.bank_player));
                    }
                    if (OddJob.getInstance().getCurrencyManager().hasPocket(uuid)) {
                        oddjobConfig.set("balances."+string+".pocket",OddJob.getInstance().getCurrencyManager().getPocketBalance(uuid));
                    }
                }
                oddjobConfig.save(oddjobConfigFile);
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static HashMap<String, HashMap<UUID, Double>> load() {
        HashMap<String, HashMap<UUID, Double>> values = new HashMap<>();
        try {
            if (connect()) {
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
                        values.get("pocket").put(uuid, pocket);
                    }
                }
            } else {
                if (oddjobConfig.getConfigurationSection("currency") != null) {
                    for (String string : oddjobConfig.getConfigurationSection("currency").getKeys(false)) {
                        ConfigurationSection user = oddjobConfig.getConfigurationSection("currency."+string);
                        if (user != null) {
                            boolean guild = user.getBoolean("guild");
                            double bank = user.getDouble("bank");
                            double pocket = user.getDouble("pocket");
                            UUID uuid = UUID.fromString(string);
                            if (guild) {
                                if (!values.containsKey("guild")) values.put("guild", new HashMap<>());
                                values.get("guild").put(uuid, bank);
                            } else {
                                if (!values.containsKey("bank")) values.put("bank", new HashMap<>());
                                values.get("bank").put(uuid, bank);
                                if (!values.containsKey("pocket")) values.put("pocket", new HashMap<>());
                                values.get("pocket").put(uuid, pocket);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return values;
    }

    /**
     *
     * @param uuid UUID uuid of guild or player
     * @param pocketStart double Starting pocket value
     * @param bankStart double Starting bank value
     * @param account Account Type of account
     */
    public static void createAccount(UUID uuid, double pocketStart,double bankStart, Currency account) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_balances` (`uuid`,`pocket`,`bank`,`guild`) VALUES (?,?,?,?)");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setDouble(2, pocketStart);
                preparedStatement.setDouble(3, bankStart);
                preparedStatement.setInt(4, account.equals(Currency.bank_guild) ? 1 : 0);
                preparedStatement.execute();
            } else {
                oddjobConfig.set("balances."+uuid.toString()+".bank",bankStart);
                oddjobConfig.set("balances."+uuid.toString()+".pocket",pocketStart);
                oddjobConfig.set("balances."+uuid.toString()+".guild",(account == Currency.bank_guild));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().getMessageManager().console("Created Account");
    }

    public static void setBalance(UUID uuid, double amount, Currency account) {
        try {
            if (connect()) {
                switch (account) {
                    case bank_player, bank_guild -> preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `bank` = ? WHERE `uuid` = ?");
                    case pocket -> preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `pocket` = ? WHERE `uuid` = ?");
                }
                preparedStatement.setDouble(1, amount);
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.executeUpdate();
            } else {
                switch (account) {
                    case bank_player, bank_guild -> oddjobConfig.set("balances." + uuid.toString() + ".bank", amount);
                    case pocket -> oddjobConfig.set("balances." + uuid.toString() + ".pocket", amount);
                }
                oddjobConfig.save(oddjobConfigFile);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}
