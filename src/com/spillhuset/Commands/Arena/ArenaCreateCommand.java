package com.spillhuset.Commands.Arena;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.ArenaType;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Game;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaCreateCommand extends SubCommand {
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
        return Plugin.arena;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Creates a new Game";
    }

    @Override
    public String getSyntax() {
        return "/arena create <name> <min_players> <max_players> <ArenaType>";
    }

    @Override
    public String getPermission() {
        return "arena.create";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (checkArgs(5, 5, args, sender, getPlugin())) {
            return;
        }

        if (can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return;
        }

        for (Game game : OddJob.getInstance().getArenaManager().getGames().values()) {
            if (game.getName().equalsIgnoreCase(args[1])) {
                OddJob.getInstance().getMessageManager().arenaNameAlreadyExists(args[1], sender);
                return;
            }
        }

        Player player = (Player) sender;
        String name = args[1];
        int min = Integer.parseInt(args[2]);
        int max = Integer.parseInt(args[3]);
        ArenaType type = null;
        for (ArenaType arenaType : ArenaType.values()) {
            if (arenaType.name().equalsIgnoreCase(args[4])) {
                type = arenaType;
            }
        }
        if (type == null) {
            OddJob.getInstance().getMessageManager().arenaTypeNotFound(args[4], sender);
            return;
        }
        Location lobby = player.getLocation();

        if (OddJob.getInstance().getArenaManager().create(name, min, max, type, lobby)) {
            OddJob.getInstance().getMessageManager().arenaCreateSuccess(name, sender);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            list.add("name");
        } else if (args.length == 3) {
            list.add("min players");
        } else if (args.length == 4) {
            list.add("max players");
        } else if (args.length == 5) {
            for (ArenaType type : ArenaType.values()) {
                list.add(type.name());
            }
        }
        return list;
    }
}
