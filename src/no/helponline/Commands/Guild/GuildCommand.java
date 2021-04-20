package no.helponline.Commands.Guild;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.Zone;
import no.helponline.Utils.SubCommand;
import no.helponline.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildCommand implements CommandExecutor, TabCompleter, SubCommandInterface {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public GuildCommand() {
        subCommands.add(new GuildCreateCommand());
        subCommands.add(new GuildAcceptCommand());
        subCommands.add(new GuildDenyCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                OddJob.getInstance().getMessageManager().errorMissingArgs(sender);
            } else if (args[0].equalsIgnoreCase("save")) {
                OddJob.getInstance().getGuildManager().save();
            } else if (args[0].equalsIgnoreCase("load")) {
                OddJob.getInstance().getGuildManager().load();
            } else if (args[0].equalsIgnoreCase("create")) {
                UUID safe = OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.SAFE);
                String nameSafe = "SafeZone";
                if (safe == null) {

                    OddJob.getInstance().getGuildManager().create(nameSafe, Zone.SAFE, true, false);
                    OddJob.getInstance().getMessageManager().infoGuildCreated(nameSafe, sender);
                } else {
                    OddJob.getInstance().getMessageManager().infoGuildExists(nameSafe, sender);
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
            } else {
                OddJob.getInstance().getMessageManager().errorWrongArgs(sender);
            }
            return true;

        }
        StringBuilder nameBuilder = new StringBuilder();
        for (SubCommand subcommand : subCommands) {
            String name = subcommand.getName();
            if (args.length >= 1 && name.equalsIgnoreCase(args[0])) {
                subcommand.perform(sender, args);
                return true;
            }
            nameBuilder.append(name).append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.lastIndexOf(","));
        OddJob.getInstance().getMessageManager().infoArgs(nameBuilder.toString(), sender);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args[0].isEmpty()) {
                list.add(name);
            } else if (name.equalsIgnoreCase(args[0]) && args.length > 1) {
                return subCommand.getTab(sender, args);
            } else if (name.startsWith(args[0])) {
                list.add(name);
            }
        }
        return list;
    }
}
