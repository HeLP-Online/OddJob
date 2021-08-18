package com.spillhuset.Commands.Guild.Set;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.Guild;
import com.spillhuset.Utils.GuildRole;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuildSetOpenCommand extends SubCommand implements GuildRole {
    @Override
    public Role getRole() {
        return null;
    }

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
        return Plugin.guilds;
    }

    @Override
    public String getName() {
        return "open";
    }

    @Override
    public String getDescription() {
        return "Sets if a guild is open to join freely";
    }

    @Override
    public String getSyntax() {
        return "/guild set open <true|false>";
    }

    @Override
    public String getPermission() {
        return "guild.use";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }
        if (checkArgs(3, 3, args, sender, getPlugin())) {
            return;
        }
        Player player = (Player) sender;
        Guild guild = OddJob.getInstance().getGuildManager().getGuild(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId()));
        boolean value = (args[2].equalsIgnoreCase("1") || args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("on"));

        if (guild.setOpen(value)) {
            OddJob.getInstance().getMessageManager().guildsSet(getName(), guild, value, sender);
        } else OddJob.getInstance().getMessageManager().errorGuild(guild.getName(), sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        String[] bool = {"true","false","on","off","1","0"};
        for (String bol :bool) {
            if (args[2].isEmpty()) {
                list.add(bol);
            } else if (args.length == 3 && bol.toLowerCase().startsWith(args[2].toLowerCase())) {
                list.add(bol);
            }
        }
        return list;
    }

    @Override
    public boolean needNoGuild() {
        return false;
    }

    @Override
    public boolean needGuild() {
        return true;
    }
}
