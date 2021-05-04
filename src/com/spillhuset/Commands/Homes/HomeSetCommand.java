package com.spillhuset.Commands.Homes;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeSetCommand extends SubCommand {
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
            OddJob.getInstance().getMessageManager().errorConsole(Plugin.home);
            return;
        }

        if (!(checkArgs(1,2,args,sender,Plugin.home))) {
            return;
        }

        Player player = (Player) sender;

        if (OddJob.getInstance().getHomesManager().getList(player.getUniqueId()).size() >= 5 && !player.hasPermission("homes.plenty")) {
            OddJob.getInstance().getMessageManager().errorHomeMaximal(player);
        } else if (args.length == 2 && player.hasPermission("homes")) {
            OddJob.getInstance().getHomesManager().add(player.getUniqueId(), args[1], player.getLocation());
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
