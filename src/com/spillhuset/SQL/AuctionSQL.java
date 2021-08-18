package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AuctionSQL extends MySQLManager {
    public static String toBase64(ItemStack item) throws IOException {
        StringBuilder sb = new StringBuilder();
        ItemMeta im = item.getItemMeta();
        if (im != null) {
            sb.append("material").append("=").append(item.getType().name()).append(";;");
            if (im.hasDisplayName()) sb.append("name").append("=").append(im.getDisplayName()).append(";;");
            if (im.hasLore() && im.getLore() != null) {
                sb.append("lore").append("={");
                int i = 0;
                for (String st : im.getLore()) {
                    sb.append(i++).append("=").append(st).append(";");
                }
                sb.append("};;");
            }
            sb.append("enchantment").append("={");
            if (item.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta enchantmentMeta = (EnchantmentStorageMeta) im;
                for (Enchantment enchantment : enchantmentMeta.getStoredEnchants().keySet()) {
                    sb.append(enchantment.toString()).append("=").append(enchantmentMeta.getStoredEnchants().get(enchantment));
                }
            } else {
                for (Enchantment enchantment : im.getEnchants().keySet()) {
                    sb.append(enchantment.toString()).append("=").append(im.getEnchants().get(enchantment)).append(";");
                }
            }
            sb.append("};;");
            if (im instanceof Damageable) {
                sb.append("damage").append("=").append(((Damageable) item.getItemMeta()).getDamage()).append(";;");
            }
        }
        return sb.toString();
    }

    public static ItemStack fromBase64(String data) throws IOException {
        String[] st = data.split(";;");
        Material material = Material.AIR;
        String name = "";
        List<String> lore = new ArrayList<>();
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        int damage = 0;
        for (String string : st) {
            if (string.startsWith("material=")) material = Material.valueOf(string.substring(string.indexOf("=") + 1));
            if (string.startsWith("name=")) name = string.split("=")[1];
            if (string.startsWith("lore=")) {
                string = string.substring(string.indexOf("{"), string.lastIndexOf("}"));
                String[] lorries = string.split(":");
                for (String lor : lorries) {
                    lore.add(lor.split("=")[1]);
                }
            }
            if (string.startsWith("enchantment=")) {
                OddJob.getInstance().log(string);
                string = string.substring(string.indexOf("{") + 1, string.lastIndexOf("}"));
                String[] encase = string.split(";");
                for (String enc : encase) {
                    OddJob.getInstance().log(enc);
                    String[] e = enc.split("=");
                    for (Enchantment en : Enchantment.values()) {
                        OddJob.getInstance().log(en.toString());
                        if (en.toString().equals(e[0])) {
                            OddJob.getInstance().log("enchant: " + e[0] + " level: " + e[1]);
                            enchantments.put(en, Integer.parseInt(e[1]));
                        }
                    }
                }
            }
            if (string.startsWith("damage=")) {
                damage = Integer.parseInt(string.split("=")[1]);
            }
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (material.equals(Material.ENCHANTED_BOOK)) {
                EnchantmentStorageMeta enchantmentMeta = (EnchantmentStorageMeta) meta;
                for (Enchantment enchantment : enchantments.keySet()) {
                    if (enchantment == null || enchantments.get(enchantment) == null) continue;
                    enchantmentMeta.addStoredEnchant(enchantment, enchantments.get(enchantment), false);
                }
            } else {
                for (Enchantment enchantment : enchantments.keySet()) {
                    if (enchantment == null || enchantments.get(enchantment) == null) continue;
                    meta.addEnchant(enchantment, enchantments.get(enchantment), false);
                }
            }
            if (!name.equals("")) meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
            if (meta instanceof Damageable) {
                meta = item.getItemMeta();
                ((Damageable) meta).setDamage(damage);
                item.setItemMeta(meta);
            }
        }

        return item;
    }

    public static void sell(Player player, ItemStack itemInMainHand, double value, double buyout, int expire) {

        int num = itemInMainHand.getAmount();
        String item = "";
        try {
            item = toBase64(itemInMainHand);
            if (connect()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_auction` (`seller`,`item`,`num`,`value`,`expire`,`buyout`) VALUES (?,?,?,?,?,?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, item);
                preparedStatement.setInt(3, num);
                preparedStatement.setDouble(4, value);
                preparedStatement.setInt(5, expire);
                preparedStatement.setDouble(6, buyout);
                preparedStatement.execute();
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public static HashMap<String, Object> buyout(int i) {
        HashMap<String, Object> ret = new HashMap<>();

        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_auction` WHERE `id` = ?");
                preparedStatement.setInt(1, i);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    ret.put("seller", Bukkit.getPlayer(UUID.fromString(resultSet.getString("seller"))));
                    ret.put("item", fromBase64(resultSet.getString("item")));
                    ret.put("num", resultSet.getInt("num"));
                    ret.put("buyout", resultSet.getDouble("buyout"));
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static double getHighestBid(int item) {
        double bid = 0;

        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_auction_bids` WHERE `item` = ?");
                preparedStatement.setInt(1, item);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    bid = Double.max(bid, resultSet.getInt("bid"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return bid;
    }

    public static void placeBid(int item, double offer, Player player) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_auction_bids` (`item`,`bid`,`bidder`,`time`) VALUES (?,?,?,?)");
                preparedStatement.setInt(1, item);
                preparedStatement.setDouble(2, offer);
                preparedStatement.setString(3, player.getUniqueId().toString());
                preparedStatement.setLong(4, System.currentTimeMillis() / 1000);
                preparedStatement.execute();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isSold(int item) {
        boolean sold = false;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT `sold` FROM `mine_auction` WHERE `item` = ?");
                preparedStatement.setInt(1, item);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    sold = !(resultSet.getInt("sold") == 0);
                    sold = sold && !resultSet.wasNull();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return sold;
    }

    public static List<Integer> findExpired(long exp) {
        List<Integer> expired = new ArrayList<>();
        try {
            if (connect()) {
                OddJob.getInstance().log("test");
                preparedStatement = connection.prepareStatement("SELECT `id`,`expire`,`time` FROM `mine_auction` WHERE `sold` IS NULL OR `sold` = 0");
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    OddJob.getInstance().log("t");
                    int expire = resultSet.getInt("expire");
                    int time = resultSet.getInt("time");
                    int i = expire + (time * 60 * 60);
                    int n = i - (int) exp;
                    if (n <= 0) expired.add(resultSet.getInt("id"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return expired;
    }
}
