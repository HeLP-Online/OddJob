package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameModeCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("gamemode")) {
            if (strings.length == 0) {
                commandSender.sendMessage("Usage: /gamemode <GameMode> [Player]");
                return true;
            }
            GameMode gm = GameMode.SURVIVAL;
            Player target = null;

            String str = strings[0];

            if (str.startsWith("sp")) {
                gm = GameMode.SPECTATOR;
            } else if (str.startsWith("c")) {
                gm = GameMode.CREATIVE;
            } else if (str.startsWith("a")) {
                gm = GameMode.ADVENTURE;
            }

            if (strings.length == 2) {
                target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[1]));
                if (target == null || !target.isOnline()) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                    return true;
                }
            } else if (strings.length == 1) {
                target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            }

            if (target != null) {
                target.setGameMode(gm);
                OddJob.getInstance().getMessageManager().success("Gamemode changed to " + gm.name(), target.getUniqueId());
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length == 0) {
            for (GameMode gm : GameMode.values()) {
                list.add(gm.name());
            }
        } else if (strings.length == 1) {
            for (GameMode gm : GameMode.values()) {
                if (gm.name().startsWith(strings[0])) {
                    list.add(gm.name());
                }
            }
        } else if (strings.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().startsWith(strings[1])) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
