package com.spillhuset.SQL;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.spillhuset.Managers.MySQLManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.AuctionBid;
import com.spillhuset.Utils.AuctionItem;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
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
    public static String serialize(final ItemStack item) {
        NBTTagCompound tag = new NBTTagCompound();
        CraftItemStack.asNMSCopy(item).save(tag);
        return tag.toString();
    }

    public static ItemStack deserialize(final String string) {
        if (string == null || string.equals("empty")) {
            return null;
        }
        try {
            NBTTagCompound comp = MojangsonParser.parse(string);
            net.minecraft.world.item.ItemStack cis = net.minecraft.world.item.ItemStack.a(comp);
            return CraftItemStack.asBukkitCopy(cis);
        } catch (CommandSyntaxException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static int sell(Player player, ItemStack itemInMainHand, double value, double buyout, int expire, double fee) {

        int num = itemInMainHand.getAmount();
        String item = "";
        int result = 0;
        try {
            item = serialize(itemInMainHand);
            if (connect()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_auction` (`seller`,`item`,`num`,`value`,`expire`,`buyout`,`fee`) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, item);
                preparedStatement.setInt(3, num);
                preparedStatement.setDouble(4, value);
                preparedStatement.setInt(5, expire);
                preparedStatement.setDouble(6, buyout);
                preparedStatement.setDouble(7, fee);
                preparedStatement.execute();

                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    result = resultSet.getInt(1);
                }

            }
        } catch (SQLException ex) {
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
                    itemStack = deserialize(resultSet.getString("item"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itemStack;
    }

    public static void addBid(int item, double offer, Player player) {
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
        findExpired();
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
                            deserialize(resultSet.getString("item")),
                            resultSet.getInt("expire"),
                            resultSet.getInt("time"),
                            resultSet.getDouble("value"),
                            resultSet.getDouble("buyout"),
                            resultSet.getInt("picked_up"),
                            resultSet.getInt("sold"),
                            resultSet.getString("buyer"),
                            resultSet.getString("seller"),
                            resultSet.getDouble("fee"),
                            getBids(id),
                            resultSet.getInt("notified") == 1
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return auctionItem;
    }

    public static AuctionBid getBid(int item) {
        AuctionBid bid = null;
        for (AuctionBid auctionBid : getBids(item)) {
            if (bid == null || (bid.getBid() < auctionBid.getBid() && !bid.isRefunded())) {
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
                preparedStatement.setInt(1, auctionBid.isRefunded() ? 1 : 0);
                preparedStatement.setDouble(2, auctionBid.getBid());
                preparedStatement.setString(3, auctionBid.getBidder().toString());
                preparedStatement.setInt(4, auctionBid.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void saveItem(AuctionItem auctionItem) {
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_auction` SET `picked_up` = ?,`sold` = ?,`buyer` = ? WHERE `id` = ?");
                preparedStatement.setInt(1, auctionItem.getPicked_up());
                preparedStatement.setInt(2, auctionItem.getSold());
                preparedStatement.setString(3, (auctionItem.getBuyer() == null) ? "" : auctionItem.getBuyer().toString());
                preparedStatement.setInt(4, auctionItem.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static List<Integer> getUnRetrievedItems(UUID uniqueId) {
        List<Integer> list = new ArrayList<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT `id` FROM `mine_auction` WHERE `picked_up` = ? AND `sold` < 0");
                preparedStatement.setInt(1, 0);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    list.add(resultSet.getInt("id"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static List<Integer> findExpired() {
        List<Integer> notify = new ArrayList<>();
        try {
            if (connect()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_auction` WHERE UNIX_TIMESTAMP() > ((`expire` * 60 * 60) + `time`)");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    if (resultSet.getInt("picked_up") != 0) {
                        continue;
                    }
                    if (resultSet.getInt("notified") == 0) {
                        notify.add(resultSet.getInt("id"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return notify;
    }
}
