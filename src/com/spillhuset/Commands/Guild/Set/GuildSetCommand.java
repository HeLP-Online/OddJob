package com.spillhuset.Commands.Guild.Set;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Role;
import com.spillhuset.Utils.GuildRole;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildSetCommand extends SubCommand implements GuildRole {
    private List<SubCommand> subCommands = new ArrayList<>();

    public GuildSetCommand() {
        subCommands.add(new GuildSetOpenCommand());
        subCommands.add(new GuildSetSpawnMobsCommand());
        subCommands.add(new GuildSetSpawnCommand());
    }

    @Override
    public Role getRole() {
        return Role.all;
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
        return "set";
    }

    @Override
    public String getDescription() {
        return "Sets";
    }

    @Override
    public String getSyntax() {
        return "/guild set";
    }

    @Override
    public String getPermission() {
        return "guild.use";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // SubCommands
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subcommand : subCommands) {
            if (subcommand.can(sender, false)) {
                String name = subcommand.getName();
                if (args.length >= 1 && name.equalsIgnoreCase(args[1])) {
                    subcommand.perform(sender, args);
                    return;
                }
                nameBuilder.append(name).append(",");
            }
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
        OddJob.getInstance().getMessageManager().infoArgs(getPlugin(), nameBuilder.toString(), sender);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(((Player) sender).getUniqueId());
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (subCommand.can(sender, false)) {
                OddJob.getInstance().log("sender can");
                if ((subCommand.needGuild() && guild != null) || (subCommand.needNoGuild() && guild == null)) {
                    OddJob.getInstance().log("can or can't");
                    if (args[0].isEmpty()) {
                        list.add(name);
                    } else if (name.equals(args[0].toLowerCase()) && args.length > 1) {
                        return subCommand.getTab(sender, args);
                    } else if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                        list.add(name);
                    }
                }
            }
        }
        return list;
    }

    public boolean needNoGuild() {
        return false;
    }

    public boolean needGuild() {
        return true;
    }
}
