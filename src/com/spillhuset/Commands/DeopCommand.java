package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DeopCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public boolean allowConsole() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.deop;
    }

    @Override
    public String getPermission() {
        return "deop";
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command,@Nonnull String label, @Nonnull String[] args) {
        if (checkArgs(1, 1, args, sender, getPlugin())) {
            return true;
        }
        boolean self = false;
        Player player = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(args[0]));
        if (player == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[0], sender);
            return true;
        }
        if (sender.getName().equals(player.getName())) {
            self = true;
        }
        if (can(sender, false)) {
            player.setOp(false);
            OddJob.getInstance().getMessageManager().deopSet(player, sender,self);
        } else {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String s, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("admin")) {
                    if (args[0].isEmpty() || player.getName().startsWith(args[0])) {
                        list.add(player.getName());
                    }
                }
            }
        }
        return list;
    }
}
