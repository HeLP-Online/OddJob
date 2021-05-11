package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class InvseeCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.invsee;
    }

    @Override
    public String getPermission() {
        return "invsee";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return true;
        }
        if (checkArgs(1, 1, args, sender, getPlugin())) {
            return true;
        }


        Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(args[0]));
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.invsee, args[0], sender);
            return true;
        }
        Player player = (Player) sender;
        if (can(sender,false)) {
            player.openInventory(target.getInventory());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        //TODO
        return null;
    }
}
