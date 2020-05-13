package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole();
            return true;
        }
        Player player = (Player) commandSender;
        if (strings.length == 0) {
            OddJob.getInstance().getShopManager().menu(player);
            return true;
        }
        if (strings[0].equalsIgnoreCase("sell")) {
            if (strings.length == 1) {
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                int amount = itemStack.getAmount();
                Material material = itemStack.getType();
                OddJob.getInstance().getMessageManager().console("selling");
                OddJob.getInstance().getShopManager().sell(material, amount, player);
            }
            if (strings.length == 2) {
                Material material;
                try {
                    material = Material.valueOf(strings[1]);
                    OddJob.getInstance().getShopManager().sell(material, 1, player);
                } catch (Exception ex) {
                    OddJob.getInstance().getMessageManager().errorMaterial(strings[1], player);
                    return true;
                }
            }
            if (strings.length == 3) {
                Material material;
                int amount;
                try {
                    amount = Integer.parseInt(strings[2]);
                    material = Material.valueOf(strings[1]);
                    OddJob.getInstance().getShopManager().sell(material, amount, player);
                } catch (NumberFormatException e) {
                    OddJob.getInstance().getMessageManager().errorNumber(strings[2], player);
                    return true;
                } catch (Exception ex) {
                    OddJob.getInstance().getMessageManager().errorMaterial(strings[1], player);
                    return true;
                }
            }
        }
        if (strings[0].equalsIgnoreCase("buy")) {
            if (strings.length == 2) {
                Material material;
                try {
                    material = Material.valueOf(strings[1]);
                    OddJob.getInstance().getShopManager().buy(material, 1, player);
                } catch (Exception ex) {
                    OddJob.getInstance().getMessageManager().errorMaterial(strings[1], player);
                    return true;
                }
            }
            if (strings.length == 3) {
                Material material;
                int amount;
                try {
                    amount = Integer.parseInt(strings[2]);
                    material = Material.valueOf(strings[1]);
                    OddJob.getInstance().getShopManager().buy(material, amount, player);
                } catch (NumberFormatException e) {
                    OddJob.getInstance().getMessageManager().errorNumber(strings[2], player);
                    return true;
                } catch (Exception ex) {
                    OddJob.getInstance().getMessageManager().errorMaterial(strings[1], player);
                    return true;
                }
            }
        }
        if (strings[0].equalsIgnoreCase("save")) {
            OddJob.getInstance().getShopManager().save();
        }
        return true;
    }
}
