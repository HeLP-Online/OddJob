package com.spillhuset.Commands.Warp;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.SubCommandInterface;
import com.spillhuset.Utils.Warp;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public WarpCommand() {
        subCommands.add(new WarpAddCommand());
        subCommands.add(new WarpDelCommand());
        subCommands.add(new WarpSetCommand());
        subCommands.add(new WarpListCommand());
    }

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
        return Plugin.warp;
    }

    @Override
    public String getPermission() {
        return "warp.use";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                OddJob.getInstance().getMessageManager().errorMissingArgs(Plugin.warp,sender);
                return true;
            } else if (args[0].equalsIgnoreCase("save")) {
                OddJob.getInstance().getWarpManager().save();
                return true;
            } else if (args[0].equalsIgnoreCase("load")) {
                OddJob.getInstance().getWarpManager().load();
                return true;
            }
        }

        if (sender instanceof Player player) {
            for (UUID uuid : OddJob.getInstance().getWarpManager().listWarps().keySet()) {
                Warp warp = OddJob.getInstance().getWarpManager().listWarps().get(uuid);
                if (warp.getName().equalsIgnoreCase(args[0])) {
                    if (can(sender,false)) {
                        if (args.length == 1) {
                            OddJob.getInstance().getWarpManager().pass(player, uuid);
                        } else if (args.length == 2) {
                            String pass = args[1];
                            OddJob.getInstance().getWarpManager().pass(player, uuid, pass);
                        } else {
                            OddJob.getInstance().getMessageManager().warpError(args[0], sender);
                        }
                        return true;
                    }
                }
            }
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (name.equalsIgnoreCase(args[0])) {

                subCommand.perform(sender, args);

                return true;
            }
            nameBuilder.append(name).append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
        // Fallback
        sender.sendMessage(ChatColor.GOLD + "args: " + ChatColor.RESET + nameBuilder.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (sender.hasPermission(subCommand.getPermission())) {
                if (args[0].isEmpty()) {
                    list.add(name);
                } else if (name.equalsIgnoreCase(args[0]) && args.length > 1) {
                    return subCommand.getTab(sender, args);
                } else if (name.startsWith(args[0])) {
                    list.add(name);
                }
            }
        }
        for (UUID uuid : OddJob.getInstance().getWarpManager().getAll().keySet()) {
            Warp warp = OddJob.getInstance().getWarpManager().get(uuid);
            String name = warp.getName();
            if (args[0].isEmpty()) {
                list.add(name);
            } else if (name.toLowerCase().startsWith(args[0].toLowerCase()) && args.length == 1) {
                list.add(name);
            }
        }
        return list;
    }
}
