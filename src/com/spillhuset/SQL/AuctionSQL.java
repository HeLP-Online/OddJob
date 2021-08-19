package com.spillhuset.SQL;

import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.AuctionBid;
import com.spillhuset.Utils.AuctionItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
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

    public static int sell(Player player, ItemStack itemInMainHand, double value, double buyout, int expire) {

        int num = itemInMainHand.getAmount();
        String item = "";
        int result = 0;
        try {
            item = toBase64(itemInMainHand);
            if (connect()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_auction` (`id`,`seller`,`item`,`num`,`value`,`expire`,`buyout`,`fee`) VALUES ('',?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, item);
                preparedStatement.setInt(3, num);
                preparedStatement.setDouble(4, value);
                preparedStatement.setInt(5, expire);
                preparedStatement.setDouble(6, buyout);
                preparedStatement.executeUpdate();

                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    result = resultSet.getInt(1);
                }

            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static ItemStack buyout(int i) {
        ItemStack itemStack = null;

        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_auction` WHERE `id` = ?");
                preparedStatement.setInt(1, i);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    itemStack = fromBase64(resultSet.getString("item"));
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return itemStack;
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

    public static List<AuctionItem> findExpired() {
        List<AuctionItem> expired = new ArrayList<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_auction` WHERE `sold` = 0");
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    List<AuctionBid> bids = getBids(id);
                    expired.add(new AuctionItem(
                            id,
                            resultSet.getInt("expire"),
                            resultSet.getInt("time"),
                            resultSet.getDouble("value"),
                            resultSet.getInt("num"),
                            resultSet.getDouble("buyout"),
                            resultSet.getInt("picked_up"),
                            resultSet.getInt("sold"),
                            resultSet.getString("buyer"),
                            resultSet.getString("seller"),
                            resultSet.getDouble("fee"),
                            bids));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return expired;
    }

    public static List<AuctionBid> getBids(int item) {
        List<AuctionBid> expired = new ArrayList<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_auction_bids` WHERE `item` = ?");
                preparedStatement.setInt(1, item);
                resultSetSec = preparedStatement.executeQuery();
                while (resultSetSec.next()) {
                    int id = resultSetSec.getInt("id");
                    expired.add(new AuctionBid(id,
                            UUID.fromString(resultSetSec.getString("bidder")),
                            resultSetSec.getInt("bid"),
                            resultSetSec.getInt("time"),
                            resultSetSec.getInt("refunded"))
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return expired;
    }

    public static AuctionItem getAuctionItem(int item) {
        AuctionItem auctionItem = null;
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_auction` WHERE `id` = ?");
                preparedStatement.setInt(1, item);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    auctionItem = new AuctionItem(
                            id,
                            resultSet.getString("item"),
                            resultSet.getInt("expire"),
                            resultSet.getInt("time"),
                            resultSet.getDouble("value"),
                            resultSet.getInt("num"),
                            resultSet.getDouble("buyout"),
                            resultSet.getInt("picked_up"),
                            resultSet.getInt("sold"),
                            resultSet.getString("buyer"),
                            resultSet.getString("seller"),
                            resultSet.getDouble("fee"),
                            getBids(id)
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return auctionItem;
    }

    public static AuctionBid getBid(int item, boolean highest) {
        AuctionBid bid = null;
        for (AuctionBid auctionBid : getBids(item)) {
            if (bid == null) {
                bid = auctionBid;
            } else if (bid.getBid() < auctionBid.getBid()) {
                bid = auctionBid;
            }
        }
        return bid;
    }

    public static List<AuctionItem> getAllItems() {
        List<AuctionItem> list = new ArrayList<>();
        return list;
    }

    public static List<AuctionItem> getListing() {
        List<AuctionItem> list = new ArrayList<>();
        return list;
    }

    public static void saveBid(AuctionBid auctionBid) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_auction_bids` SET `refunded` = ?,`bid` = ?,`bidder` = ? WHERE `id` = ?");
                preparedStatement.setInt(1,auctionBid.isRefunded() ? 1:0);
                preparedStatement.setDouble(2,auctionBid.getBid());
                preparedStatement.setString(3,auctionBid.getBidder().toString());
                preparedStatement.setInt(4,auctionBid.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static AuctionBid getHighestBid(int item) {
        return null;
    }
}
