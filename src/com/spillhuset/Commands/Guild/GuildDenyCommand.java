package com.spillhuset.Commands.Guild;

import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class GuildDenyCommand extends SubCommand {
    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
