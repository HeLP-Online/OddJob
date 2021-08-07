package com.spillhuset.Commands.Arena;
import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.Game;
import com.spillhuset.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ArenaListCommand extends SubCommand {
    @Override
    public boolean allowConsole() {
        return true;
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
        return "list";
    }

    @Override
    public String getDescription() {
        return "Lists all games";
    }

    @Override
    public String getSyntax() {
        return "/arena list";
    }

    @Override
    public String getPermission() {
        return "arena";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        List<String> names = new ArrayList<>();
        for (Game game: OddJob.getInstance().getArenaManager().getGames().values()) {
            names.add(game.getName());
        }
        OddJob.getInstance().getMessageManager().arenaList(sender,names);
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
