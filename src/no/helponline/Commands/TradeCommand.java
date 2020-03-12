package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class TradeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            OddJob.getInstance().getMessageManager().console(ChatColor.RED + "No command for you!");
            return true;
        }

        Player player = (Player) commandSender;
        if (strings.length == 2 && strings[0].equalsIgnoreCase("request")) {
            // Trading Player
            UUID tradeWith = OddJob.getInstance().getPlayerManager().getUUID(strings[1]);
            if (tradeWith == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(strings[1], commandSender);
                return true;
            }

            // Adding to the trade request list
            OddJob.getInstance().getPlayerManager().getRequestTrade().put(tradeWith, player.getUniqueId());

            OddJob.getInstance().getMessageManager().success("You have sent a trade request to " + OddJob.getInstance().getPlayerManager().getName(tradeWith), player, true);
            OddJob.getInstance().getMessageManager().info("You got a trade request from " + player.getName(), tradeWith, false);
            return true;
        } else if (strings.length == 1 && strings[0].equalsIgnoreCase("accept")) {
            if (OddJob.getInstance().getPlayerManager().getRequestTrade().containsKey(player.getUniqueId())) {
                // Found a trade request
                UUID tradeWithU = OddJob.getInstance().getPlayerManager().getRequestTrade().get(player.getUniqueId());
                if (tradeWithU != null) {
                    Player tradeWith = OddJob.getInstance().getPlayerManager().getPlayer(tradeWithU);
                    if (tradeWith != null && tradeWith.isOnline()) {
                        Inventory tradeInv = Bukkit.createInventory(null, 27, "TRADE INVENTORY");

                        ItemStack glass = new ItemStack(Material.GLASS_PANE);
                        ItemStack button = new ItemStack(Material.REDSTONE_BLOCK);

                        tradeInv.setItem(9, glass);
                        tradeInv.setItem(10, glass);
                        tradeInv.setItem(11, glass);
                        tradeInv.setItem(12, glass);
                        tradeInv.setItem(13, glass);
                        tradeInv.setItem(14, glass);
                        tradeInv.setItem(15, glass);
                        tradeInv.setItem(16, glass);
                        tradeInv.setItem(17, button);

                        player.openInventory(tradeInv);
                        tradeWith.openInventory(tradeInv);
                        OddJob.getInstance().getPlayerManager().getTradingPlayers().put(player.getUniqueId(), tradeWithU);
                    } else {
                        OddJob.getInstance().getMessageManager().danger("Trade partner is no longer online.", player, false);
                    }
                    OddJob.getInstance().getPlayerManager().getRequestTrade().remove(player.getUniqueId());
                }
            } else {
                OddJob.getInstance().getMessageManager().warning("There is no request to trade with you.", player, false);
            }
            return true;
        }
        return true;
    }
}
