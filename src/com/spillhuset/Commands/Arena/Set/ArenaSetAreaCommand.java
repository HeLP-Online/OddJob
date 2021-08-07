package com.spillhuset.Commands.Arena.Set;

import com.spillhuset.Managers.ArenaManager;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Game;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ArenaSetAreaCommand extends SubCommand {
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
        return "area";
    }

    @Override
    public String getDescription() {
        return "Sets an area dedicated to an Arena";
    }

    @Override
    public String getSyntax() {
        return "/arena set area <name>";
    }

    @Override
    public String getPermission() {
        return "arena.admin";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        OddJob.getInstance().log("ok");
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 3) {
            for (Game game : OddJob.getInstance().getArenaManager().getGames().values()) {
                if (game.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    list.add(game.getName());
                }
            }
        }
        return list;
    }
}
