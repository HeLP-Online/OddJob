package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GiveCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings[0] != null) {
            Player target = Bukkit.getPlayer(strings[0]);
            if (target == null) {
                OddJob.getInstance().log("Error player");
                return true;
            }
            if (strings.length >= 2 && strings[1] != null) {
                Material material = Material.valueOf(strings[1]);
                if (material == null) {
                    OddJob.getInstance().log("Error material");
                    return true;
                }
                int count = 1;
                if (strings.length == 3 && strings[2] != null) count = Integer.parseInt(strings[2]);

                OddJob.getInstance().log("start " + count);
                for (int i = count; i >= material.getMaxStackSize(); i -= material.getMaxStackSize()) {
                    if (target.getInventory().firstEmpty() >= 1) {

                        count -= material.getMaxStackSize();
                        OddJob.getInstance().log("new value " + count);
                        target.getInventory().setItem(target.getInventory().firstEmpty(), new ItemStack(material, material.getMaxStackSize()));
                        break;
                    }
                }
                OddJob.getInstance().log("rest " + count);
                target.getInventory().addItem(new ItemStack(material, count));

            } else {
                OddJob.getInstance().log("Error material nil");
            }
        } else {
            OddJob.getInstance().log("Error player nil");
        }
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
