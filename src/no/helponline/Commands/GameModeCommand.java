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

            if (str.startsWith("sp") || str.startsWith("SP") || str.equals("2")) {
                gm = GameMode.SPECTATOR;
            } else if (str.startsWith("c") || str.startsWith("C") || str.equals("1")) {
                gm = GameMode.CREATIVE;
            } else if (str.startsWith("a") || str.startsWith("A") || str.equals("3")) {
                gm = GameMode.ADVENTURE;
            }

            if (strings.length == 2) {
                target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[1]));
                if (target == null || !target.isOnline()) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender,false);
                    return true;
                }
            } else if (strings.length == 1 && commandSender instanceof Player) {
                target = (Player) commandSender;
            }

            if (target != null) {
                OddJob.getInstance().getPlayerManager().setGameMode(target, gm);
                OddJob.getInstance().getMessageManager().success("Gamemode changed to " + gm.name(), target.getUniqueId(),true);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        String name = commandSender.getName();
        List<String> list = new ArrayList<>();
        if (strings.length == 0) {
            for (GameMode gm : GameMode.values()) {
                list.add(gm.name().toLowerCase());
            }
        } else if (strings.length == 1) {
            for (GameMode gm : GameMode.values()) {
                if (gm.name().toLowerCase().startsWith(strings[0]) || gm.name().startsWith(strings[0])) {
                    list.add(gm.name());
                }
            }
        } else if (strings.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(strings[1].toLowerCase()) && !player.getName().equals(name)) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
