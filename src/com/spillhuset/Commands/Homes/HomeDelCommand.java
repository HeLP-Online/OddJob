package com.spillhuset.Commands.Homes;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeDelCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return true;
    }

    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.home;
    }

    @Override
    public String getName() {
        return "del";
    }

    @Override
    public String getDescription() {
        return "Delete a home";
    }

    @Override
    public String getSyntax() {
        return "/home del [name]";
    }

    @Override
    public String getPermission() {
        return "homes.del";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        String home = "home";
        UUID target = null;

        if (!(sender instanceof Player)) {
            if (checkArgs(3, 3, args, sender, getPlugin())) {
                return;
            }

            target = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
            home = args[2];
        } else if (sender.hasPermission("homes.del.others")) {
            if (checkArgs(1, 3, args, sender, getPlugin())) {
                return;
            }
            if (args.length == 3) {
                target = OddJob.getInstance().getPlayerManager().getUUID(args[1]);
                home = args[2];
            } else {
                target = ((Player) sender).getUniqueId();
                if (args.length == 2) {
                    home = args[1];
                }
            }
        } else {
            if (checkArgs(1, 2, args, sender, getPlugin())) {
                return;
            }

            target = ((Player) sender).getUniqueId();
            if (args.length == 2) {
                home = args[1];
            }
        }
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.player, args[0], sender);
            return;
        }

        OddJob.getInstance().getHomesManager().del(target, home);

    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        Player player = (Player) sender;
        for (String name : OddJob.getInstance().getHomesManager().getList(player.getUniqueId())) {
            if (args.length == 2) {
                if (args[1].isEmpty() || name.toLowerCase().startsWith(args[1].toLowerCase())) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
