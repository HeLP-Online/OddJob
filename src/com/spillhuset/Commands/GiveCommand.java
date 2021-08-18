package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GiveCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {

    @Override
    public boolean denyConsole() {
        return false;
    }

    @Override
    public boolean onlyConsole() {
        return false;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public boolean onlyOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.give;
    }

    @Override
    public String getPermission() {
        return "give";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // give <player> <material> [amount]
        if (checkArgs(2, 3, args, sender, getPlugin())) {
            return true;
        }

        UUID targetUUID = OddJob.getInstance().getPlayerManager().getUUID(args[0]);
        if (targetUUID == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[0], sender);
            return true;
        }
        int amount = 1;
        Material material = Material.getMaterial(args[1]);
        if (material == null) {
            OddJob.getInstance().getMessageManager().errorMaterial(getPlugin(), args[1], sender);
            return true;
        }
        if (args.length == 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                OddJob.getInstance().getMessageManager().invalidNumber(getPlugin(), args[2], sender);
            }

            Player target = OddJob.getInstance().getPlayerManager().getPlayer(targetUUID);
            for (int i = amount; i >= material.getMaxStackSize(); i -= material.getMaxStackSize()) {
                if (target.getInventory().firstEmpty() >= 1) {

                    amount -= material.getMaxStackSize();
                    target.getInventory().setItem(target.getInventory().firstEmpty(), new ItemStack(material, material.getMaxStackSize()));
                    break;
                }
            }
            target.getInventory().addItem(new ItemStack(material, amount));

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (args[0].isEmpty()) list.add(player.getName());
                if (player.getName().startsWith(args[0])) list.add(player.getName());
            }
        } else if (args.length == 2) {
            for (Material material : Material.values()) {
                if (args[1].isEmpty()) list.add(material.name());
                if (material.name().startsWith(args[1])) list.add(material.name());
            }
        } else if (args.length == 3) {
            list.add("Amount");
        }
        return list;
    }
}
