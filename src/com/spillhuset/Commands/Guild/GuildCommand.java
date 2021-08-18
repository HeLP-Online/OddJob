package com.spillhuset.Commands.Guild;

import com.spillhuset.Commands.Guild.Set.GuildSetCommand;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Enum.Zone;
import com.spillhuset.Utils.SubCommand;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public GuildCommand() {
        subCommands.add(new GuildCreateCommand());
        subCommands.add(new GuildAcceptCommand());
        subCommands.add(new GuildDenyCommand());
        subCommands.add(new GuildJoinCommand());
        subCommands.add(new GuildClaimCommand());
        subCommands.add(new GuildUnclaimCommand());
        subCommands.add(new GuildDisbandCommand());
        subCommands.add(new GuildLeaveCommand());
        subCommands.add(new GuildMapCommand());
        subCommands.add(new GuildSetCommand());
        subCommands.add(new GuildInviteCommand());
        subCommands.add(new GuildUninviteCommand());
    }

    @Override
    public boolean denyConsole() {
        return false;
    }

    @Override
    public boolean onlyConsole() {
        return false;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public boolean onlyOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.guilds;
    }

    @Override
    public String getPermission() {
        return "guild";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            if (args.length == 0) {
                OddJob.getInstance().getMessageManager().errorMissingArgs(getPlugin(), sender);
            } else if (args[0].equalsIgnoreCase("save")) {
                OddJob.getInstance().getGuildManager().save();
                OddJob.getInstance().getMessageManager().saved(getPlugin(), sender);
            } else if (args[0].equalsIgnoreCase("load")) {
                OddJob.getInstance().getGuildManager().load();
                OddJob.getInstance().getMessageManager().loaded(getPlugin(), sender);
            } else if (args[0].equalsIgnoreCase("create")) {
                UUID safe = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.SAFE);
                String nameSafe = "SafeZone";
                if (safe == null) {
                    OddJob.getInstance().getGuildManager().create(nameSafe, Zone.SAFE, true, false);
                    OddJob.getInstance().getMessageManager().infoGuildCreated(nameSafe, sender);
                } else {
                    OddJob.getInstance().getMessageManager().infoGuildExists(nameSafe, sender);
                }
                UUID quest = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.SAFE);
                String nameQuest = "QuestZone";
                if (quest == null) {
                    OddJob.getInstance().getGuildManager().create(nameQuest, Zone.QUEST, true, false);
                    OddJob.getInstance().getMessageManager().infoGuildCreated(nameQuest, sender);
                } else {
                    OddJob.getInstance().getMessageManager().infoGuildExists(nameQuest, sender);
                }
                UUID war = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WAR);
                String nameWar = "WarZone";
                if (war == null) {
                    OddJob.getInstance().getGuildManager().create(nameWar, Zone.WAR, true, false);
                    OddJob.getInstance().getMessageManager().infoGuildCreated(nameWar, sender);
                } else {
                    OddJob.getInstance().getMessageManager().infoGuildExists(nameWar, sender);
                }
                UUID jail = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.JAIL);
                String nameJail = "JailZone";
                if (jail == null) {
                    OddJob.getInstance().getGuildManager().create(nameJail, Zone.JAIL, true, false);
                    OddJob.getInstance().getMessageManager().infoGuildCreated(nameJail, sender);
                } else {
                    OddJob.getInstance().getMessageManager().infoGuildExists(nameJail, sender);
                }
                UUID arena = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.ARENA);
                String nameArena = "ArenaZone";
                if (arena == null) {
                    OddJob.getInstance().getGuildManager().create(nameArena, Zone.ARENA, true, false);
                    OddJob.getInstance().getMessageManager().infoGuildCreated(nameArena, sender);
                } else {
                    OddJob.getInstance().getMessageManager().infoGuildExists(nameArena, sender);
                }
                UUID wild = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD);
                String nameWild = "WildZone";
                if (wild == null) {
                    OddJob.getInstance().getGuildManager().create(nameWild, Zone.WILD, true, false);
                    OddJob.getInstance().getMessageManager().infoGuildCreated(nameWild, sender);
                } else {
                    OddJob.getInstance().getMessageManager().infoGuildExists(nameWild, sender);
                }
            }
            return true;

        }

        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(player.getUniqueId());

        // SubCommands
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subcommand : subCommands) {
            if (subcommand.can(sender, false)) {
                String name = subcommand.getName();
                if (args.length >= 1 && name.equalsIgnoreCase(args[0])) {
                    subcommand.perform(sender, args);
                    return true;
                }
                nameBuilder.append(name).append(",");
            }
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
        if (args.length == 0) {
            if (guild != null) {
                OddJob.getInstance().getMessageManager().guildMenu(OddJob.getInstance().getGuildManager().getGuild(guild), OddJob.getInstance().getGuildManager().getGuildNameByUUID(guild), OddJob.getInstance().getGuildManager().getGuildMemberRole(player.getUniqueId()), sender);
            } else {
                OddJob.getInstance().getMessageManager().guildNotAssociated(player.getUniqueId());
            }
        }

        OddJob.getInstance().getMessageManager().infoArgs(getPlugin(), nameBuilder.toString(), sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByMember(((Player) sender).getUniqueId());
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (subCommand.can(sender, false) && ((subCommand.needGuild() && guild != null) || (subCommand.needNoGuild() && guild == null) || !subCommand.needNoGuild() && !subCommand.needGuild())) {
                if (args[0].isEmpty()) {
                    list.add(name);
                } else if (name.equals(args[0].toLowerCase()) && args.length > 1) {
                    return subCommand.getTab(sender, args);
                } else if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
