package com.spillhuset.Commands.Homes;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeListCommand extends SubCommand {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "List homes";
    }

    @Override
    public String getSyntax() {
        return "/homes list";
    }

    @Override
    public String getPermission() {
        return "homes";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        OddJob.getInstance().getHomesManager().list(player.getUniqueId());
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
