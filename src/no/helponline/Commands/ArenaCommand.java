package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.Arena;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.List;

public class ArenaCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("arena") && commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (strings[0].equalsIgnoreCase("create")) {
                Arena a = OddJob.getInstance().getArenaManager().editArena.get(player.getUniqueId());
                if (a != null) {
                    player.sendMessage("You are already working with " + a.getName());
                }
                String name = strings[1];

                Arena.Type type = Arena.Type.valueOf(strings[2]);

                int minPlayers = Integer.parseInt(strings[3]);
                int maxPlayers = Integer.parseInt(strings[4]);

                HashMap<Integer, Location> spawn = new HashMap<>();
                spawn.put(1, player.getLocation());

                Arena arena = OddJob.getInstance().getArenaManager().createArena(name, type, minPlayers, maxPlayers, spawn);
                OddJob.getInstance().getArenaManager().editArena.put(player.getUniqueId(), arena);

                if (type == Arena.Type.HungerGames) {
                    player.getInventory().addItem(OddJob.getInstance().getArenaManager().spawnTool);
                    player.sendMessage("Please select " + maxPlayers + " spawn points with a stick");
                }
            } else if (strings[0].equalsIgnoreCase("list")) {
                List<Arena> list = OddJob.getInstance().getArenaManager().listArenas();
                StringBuilder sb = new StringBuilder();
                sb.append("§3List of Arenas you might join:");
                for (Arena arena : list) {
                    // name:hunger type:HungerGames queue:0 min:2 max:10 status:waiting
                    sb.append("\n§6Name: §b")
                            .append(arena.getName())
                            .append("\n    §6type: §b")
                            .append(arena.getType().name())
                            .append("\n    §6Players in queue :§b")
                            .append(arena.getQueue().size())
                            .append("\n    §6Min player to start: §b")
                            .append(arena.getMinPlayers())
                            .append("\n    §6Max player per game: §b")
                            .append(arena.getMaxPlayers())
                            .append("\n    §6Status: §b")
                            .append(arena.getStatus().name());
                }
                player.sendMessage(sb.toString());
            } else if (strings[0].equalsIgnoreCase("load")) {

                commandSender.sendMessage("Loaded " + OddJob.getInstance().getArenaManager().loadArenas() + " arenas");
            } else if (strings[0].equalsIgnoreCase("join")) {
                OddJob.getInstance().getArenaManager().queue(strings[1], player.getUniqueId());
            } else if (strings[0].equalsIgnoreCase("edit")) {
                OddJob.getInstance().getArenaManager().edit(strings[1], player);
            } else if (strings[0].equalsIgnoreCase("remove")) {
                OddJob.getInstance().getArenaManager().removeArena(strings[0]);
            } else if (strings[0].equalsIgnoreCase("setlobby")) {
                OddJob.getInstance().getArenaManager().setLobbySpawn(player);
            } else if (strings[0].equalsIgnoreCase("lobby")) {
                if (OddJob.getInstance().getTeleportManager().teleport(player, OddJob.getInstance().getArenaManager().getLobbySpawn(), 0, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                    OddJob.getInstance().getMessageManager().success("Teleporting to Arena Lobby Spawn", player);
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
