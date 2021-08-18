package com.spillhuset.Commands.Homes;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomesCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public HomesCommand() {
        subCommands.add(new HomeSetCommand());
        subCommands.add(new HomeDelCommand());
        subCommands.add(new HomeListCommand());
    }


    @Override
    public boolean denyConsole() {
        return true;
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
        return Plugin.home;
    }

    @Override
    public String getPermission() {
        return "homes";
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args.length >= 1 && name.equalsIgnoreCase(args[0]) && can(sender, false)) {
                subCommand.perform(sender, args);
                return true;
            }
            if (can(sender, false)) {
                nameBuilder.append(name).append(",");
            }
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().sendSyntax(getPlugin(), nameBuilder.toString(), sender);
            return true;
        }

        if (args.length == 0) {
            UUID target = ((Player) sender).getUniqueId();
            List<String> test = OddJob.getInstance().getHomesManager().getList(target);
            if (test == null || test.isEmpty()) {
                OddJob.getInstance().getMessageManager().homesNotSet(sender);
                return true;
            }
            OddJob.getInstance().getMessageManager().homesCount(test, sender.getName(), OddJob.getInstance().getHomesManager().getMax(target), sender, true);
            return true;
        }

        String name = "home";
        UUID target;
        // homes <home>
        // homes <player> <home>

        // Searching for a named home
        if (args.length == 1) {
            name = args[0];
            Player player = (Player) sender;
            target = player.getUniqueId();
            Location location = OddJob.getInstance().getHomesManager().get(target, name);
            if (location == null) {
                OddJob.getInstance().getMessageManager().errorHome(name, player);
                return true;
            }
            OddJob.getInstance().getHomesManager().teleport(player, target, name);
            return true;
        }

        // Searching for other players homes
        if (args.length == 2 && sender.hasPermission("homes.others")) {
            name = args[1];

            // Check Target Player
            target = OddJob.getInstance().getPlayerManager().getUUID(args[0]);
            if (target == null) {
                OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[0], sender);
                return true;
            }

            // Check Home Location
            Location location = OddJob.getInstance().getHomesManager().get(target, name);
            if (location == null) {
                OddJob.getInstance().getMessageManager().errorHome(name, sender);
                return true;
            }

            OddJob.getInstance().getHomesManager().teleport((Player) sender, target, name);

            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        // Listing SubCommands
        if (args.length >= 1) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(args[0]) && args.length > 1) {
                    return subCommand.getTab(sender, args);
                } else if (args[0].isEmpty() || subCommand.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(subCommand.getName());
                }
            }
        }
        // homes <home>
        // homes <player> <home>
        if (sender instanceof Player player) {
            if (args.length == 1) {
                // Listing own homes
                if (OddJob.getInstance().getHomesManager().getList(player.getUniqueId()) != null) {
                    for (String name : OddJob.getInstance().getHomesManager().getList(player.getUniqueId())) {
                        if (args[0].isEmpty() || name.toLowerCase().startsWith(args[0].toLowerCase())) {
                            list.add(name);
                        }
                    }
                }
                // Listing other players
                if (can(sender, true)) {
                    for (String name : OddJob.getInstance().getPlayerManager().getNames()) {
                        if (args[0].isEmpty() || name.toLowerCase().startsWith(args[0].toLowerCase())) {
                            list.add(name);
                        }
                    }
                }
            } else if (args.length == 2 && can(sender, true)) {
                UUID target = OddJob.getInstance().getPlayerManager().getUUID(args[0]);
                if (target != null) {
                    if (OddJob.getInstance().getHomesManager().getList(target) != null) {
                        for (String name : OddJob.getInstance().getHomesManager().getList(target)) {
                            if (args[1].isEmpty() || name.toLowerCase().startsWith(args[1].toLowerCase())) {
                                list.add(name);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
}
