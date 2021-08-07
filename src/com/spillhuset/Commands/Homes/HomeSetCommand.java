package com.spillhuset.Commands.Homes;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HomeSetCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public boolean allowOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.home;
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Sets a home";
    }

    @Override
    public String getSyntax() {
        return "/home set [name]";
    }

    @Override
    public String getPermission() {
        return "homes.set";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return;
        }

        if (checkArgs(1, 2, args, sender, getPlugin())) {
            return;
        }

        // Default name
        String name = "home";
        UUID target;

        if (args.length == 2) {
            // Defining a home name
            target = ((Player) sender).getUniqueId();
            name = args[1];
        } else {
            // Using default
            target = ((Player) sender).getUniqueId();
        }

        //Set<String> list = OddJob.getInstance().getHomesManager().getList(target);
        List<String> list = OddJob.getInstance().getHomesManager().getList(target);
        if (list != null && list.size() >= getMax(target)) {
            OddJob.getInstance().getMessageManager().errorHomeMaximal(sender);
            return;
        }
        OddJob.getInstance().getHomesManager().add(target, name, ((Player) sender).getLocation());

    }

    private int getMax(UUID target) {
        int config = OddJob.getInstance().getConfig().getInt("homes.max", 5);
        int player = OddJob.getInstance().getPlayerManager().getMaxHomes(target);
        int permission = 0;
        Player bukkit_player = Bukkit.getPlayer(target);
        String[] tracks = {"moderators", "vip", "emerald", "diamond", "gold", "iron", "stone", "wood", "default", "operators"};
        for (String i : tracks) {
            if (OddJob.getInstance().getConfig().isConfigurationSection("homes." + i)) {
                ConfigurationSection cf = OddJob.getInstance().getConfig().getConfigurationSection("homes." + i);
                if (cf != null && bukkit_player != null && bukkit_player.hasPermission("homes." + i)) {
                    permission = Math.max(cf.getInt("maxHomes"), permission);
                }
            }
        }
        return config + player + permission;
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
