package com.spillhuset.Commands.Homes;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
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
        // homes set
        // homes set <home>
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return;
        }

        if (checkArgs(1, 2, args, sender, getPlugin())) {
            return;
        }

        String name = "home";
        UUID target;

        if (args.length == 2) {
            target = ((Player) sender).getUniqueId();
            name = args[1];
        } else {
            target = ((Player) sender).getUniqueId();
        }
        OddJob.getInstance().log("Max: " + getMax(target));
        Set<String> list = OddJob.getInstance().getHomesManager().getList(target);
        if (list != null && list.size() >= getMax(target)) {
            OddJob.getInstance().getMessageManager().errorHomeMaximal(sender);
        }
        OddJob.getInstance().getHomesManager().add(target, name, ((Player) sender).getLocation());

    }

    private int getMax(UUID target) {
        int config = OddJob.getInstance().getConfig().getInt("homes.max", 5);
        int player = OddJob.getInstance().getPlayerManager().getMaxHomes(target);
        return config + player;
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
