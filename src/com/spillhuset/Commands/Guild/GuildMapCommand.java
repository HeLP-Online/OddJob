package com.spillhuset.Commands.Guild;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GuildMapCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return false;
    }

    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.guild;
    }

    @Override
    public String getName() {
        return "map";
    }

    @Override
    public String getDescription() {
        return "Show a map of guilds";
    }

    @Override
    public String getSyntax() {
        return "/guild map";
    }

    @Override
    public String getPermission() {
        return "guild";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
            return;
        }

        Player player = (Player) sender;
        OddJob.getInstance().getGuildManager().map(player);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
    @Override
    public boolean needGuild() {
        return true;
    }
}
