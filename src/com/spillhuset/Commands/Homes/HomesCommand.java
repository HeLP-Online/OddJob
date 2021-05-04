package com.spillhuset.Commands.Homes;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomesCommand implements CommandExecutor, TabCompleter, SubCommandInterface {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public HomesCommand() {
        subCommands.add(new HomeSetCommand());
        subCommands.add(new HomeDelCommand());
        subCommands.add(new HomeListCommand());
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length != 1) {
                OddJob.getInstance().getMessageManager().errorMissingArgs(Plugin.home,sender);
            } else if (args[0].equalsIgnoreCase("save")) {
                OddJob.getInstance().getHomesManager().save();
            } else if (args[0].equalsIgnoreCase("load")) {
                OddJob.getInstance().getHomesManager().load();
            }
            return true;
        }
        HashMap<Integer, String> names = new HashMap<>();
        StringBuilder name = new StringBuilder();
        // Listing SubCommands
        for (int i = 0; i < subCommands.size(); i++) {
            names.put(i, subCommands.get(i).getName());
            name.append(subCommands.get(i).getName()).append(",");
        }
        name.append("[name]");
        if (args.length > 0) {
            // Searching for SubCommand
            for (int i : names.keySet()) {
                if (names.get(i).equalsIgnoreCase(args[0])) {
                    subCommands.get(i).perform(sender, args);
                    return true;
                }
            }
            // Searching for a named home
            if (args.length == 1) {
                Player player = (Player) sender;
                Location location = OddJob.getInstance().getHomesManager().get(player.getUniqueId(), args[0]);
                if (location == null) {
                    OddJob.getInstance().getMessageManager().errorHome(args[0], player);
                    return true;
                }
                OddJob.getInstance().getHomesManager().teleport(player, player.getUniqueId(), args[0]);
                return true;
            }
            // Searching for other players homes
            if (args.length == 2 && sender.hasPermission("homes.others")) {
                try {
                    UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(args[0]);
                    if (uuid == null) {
                        OddJob.getInstance().getMessageManager().errorPlayer(Plugin.ban, args[0], sender);
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("list")) {
                        OddJob.getInstance().getHomesManager().list(uuid);
                        return true;
                    }
                    Location location = OddJob.getInstance().getHomesManager().get(uuid, args[1]);
                    if (location == null) {
                        OddJob.getInstance().getMessageManager().errorHome(args[1], sender);
                        return true;
                    }
                    OddJob.getInstance().getHomesManager().teleport((Player) sender, uuid, args[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        // Fallback
        sender.sendMessage(ChatColor.GOLD + "args: " + ChatColor.RESET + name.toString());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        // Listing SubCommands
        if (args.length >= 1) {
            for (int i = 0; i < subCommands.size(); i++) {
                if (subCommands.get(i).getName().equalsIgnoreCase(args[0])) {
                    return list = subCommands.get(i).getTab(sender, args);
                }
                list.add(subCommands.get(i).getName());
            }
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                // Listing own homes
                for (String name : OddJob.getInstance().getHomesManager().getList(player.getUniqueId())) {
                    if (args[0].isEmpty()) {
                        list.add(name);
                    } else if (name.startsWith(args[0])) {
                        list.add(name);
                    }
                }
                // Listing other players
                if (player.hasPermission("homes.others")) {
                    for (String name : OddJob.getInstance().getPlayerManager().getNames()) {
                        if (name.equals(player.getName())) continue;
                        if (args[0].isEmpty()) {
                            list.add(name);
                        } else if (name.startsWith(args[0])) {
                            list.add(name);
                        }
                    }
                }
            } else if (args.length == 2 && player.hasPermission("homes.others")) {
                UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[0]);
                if (target != null) {
                    for (String name : OddJob.getInstance().getHomesManager().getList(target)) {
                        if (args[1].isEmpty()) {
                            list.add(name);
                        } else if (name.startsWith(args[1])) {
                            list.add(name);
                        }
                    }
                }
            }
        }


        return list;
    }
}
