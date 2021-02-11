package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.Arena.Arena;
import no.helponline.Utils.Arena.ArenaManager;
import no.helponline.Utils.Enum.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class ArenaCommand implements CommandExecutor, TabCompleter {
    private HashMap<Integer, HashMap<String, Block>> arenas;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;
        int id = 0;
        if (strings.length >= 1) {
            switch (strings[0]) {
                case "edit": {

                    try {
                        id = Integer.parseInt(strings[1]);
                        OddJob.getInstance().getArenaManager().editArena(player, id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case "set": {
                    id = OddJob.getInstance().getArenaManager().getEditor(player.getUniqueId());
                    switch (strings[1]) {
                        case "type":
                            OddJob.getInstance().getArenaManager().getArena(id).setGameType(ArenaManager.GameType.valueOf(strings[2]));
                            return true;
                        case "lobby": {
                            if (strings.length == 3 && strings[2].equalsIgnoreCase("remove")) {
                                OddJob.getInstance().getArenaManager().getArena(id).removeLobbySpawn();
                            } else {
                                OddJob.getInstance().getArenaManager().getArena(id).setLobbySpawn(player.getLocation());
                            }
                            return true;
                        }
                        case "spawn": {
                            if (strings.length == 4 && strings[2].equalsIgnoreCase("tp")) {
                                player.teleport(OddJob.getInstance().getArenaManager().getArena(id).tpGameSpawn(Integer.parseInt(strings[3])));
                                OddJob.getInstance().getMessageManager().arenaSetSpawnTeleport("Teleported to spawn "+strings[3],player.getUniqueId());
                            } else if (strings.length == 4 && strings[2].equalsIgnoreCase("remove")) {
                                OddJob.getInstance().getArenaManager().getArena(id).removeGameSpawn(Integer.parseInt(strings[3]));
                                OddJob.getInstance().getMessageManager().arenaSetSpawnRemove("Removed spawn "+strings[3],player.getUniqueId());
                            } else {
                                OddJob.getInstance().getArenaManager().getArena(id).setGameSpawn(player.getLocation());
                                OddJob.getInstance().getMessageManager().arenaSetSpawnTeleport("Spawn "+OddJob.getInstance().getArenaManager().getArena(id).getGameSpawns().size()+" set!",player.getUniqueId());
                            }
                            return true;
                        }
                    }

                }
                break;
                case "create": {
                    // Console cannot execute this command
                    if (!(commandSender instanceof Player)) {
                        OddJob.getInstance().getMessageManager().errorConsole();
                        return true;
                    }

                    id = OddJob.getInstance().getArenaManager().createArena(player);
                }
                break;

                case "area": {
                    // Console cannot execute this command
                    if (!(commandSender instanceof Player)) {
                        OddJob.getInstance().getMessageManager().errorConsole();
                        return true;
                    }

                    int arena = OddJob.getInstance().getArenaManager().getEditor(player.getUniqueId());
                    if (arena != 0) {
                        HashMap<String, Block> blocks = arenas.getOrDefault(arena, new HashMap<>());
                        Block block = player.getLocation().subtract(0, 1, 0).getBlock();
                        if (strings[1].equalsIgnoreCase("a")) {
                            blocks.put("a", block);
                        }
                        if (strings[1].equalsIgnoreCase("b")) {
                            blocks.put("b", block);
                        }
                        arenas.put(arena, blocks);
                        block.setType(Material.GOLD_BLOCK);

                        if (blocks.get("a") != null && blocks.get("b") != null) {
                            Chunk a = blocks.get("a").getLocation().getChunk();
                            Chunk b = blocks.get("b").getLocation().getChunk();

                            int maxX = Math.max(a.getX(), b.getX());
                            int maxZ = Math.max(a.getZ(), b.getZ());
                            int minX = Math.min(a.getX(), b.getX());
                            int minZ = Math.min(a.getZ(), b.getZ());

                            boolean e = false;

                            for (int i = minX; i <= maxX; i++) {
                                for (int j = minZ; j <= maxZ; j++) {
                                    if (OddJob.getInstance().getGuildManager().getZoneByGuild(OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(player.getWorld().getChunkAt(i, j))) == Zone.GUILD) {
                                        e = true;
                                    }
                                }
                            }

                            if (e) {
                                player.sendMessage("One or more Chunks owned by a Guild");
                                return true;
                            }

                            for (int i = minX; i <= maxX; i++) {
                                for (int j = minZ; j <= maxZ; j++) {
                                    OddJob.getInstance().getGuildManager().claim(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.ARENA), player.getWorld().getChunkAt(i, j), player);
                                }
                            }
                        }
                    }
                    return true;
                }
                case "leave": {
                    // Console cannot execute this command
                    if (!(commandSender instanceof Player)) {
                        OddJob.getInstance().getMessageManager().errorConsole();
                        return true;
                    }


                    OddJob.getInstance().getArenaManager().removePlayer(player);
                    return true;
                }
                case "join": {
                    // Console cannot execute this command
                    if (!(commandSender instanceof Player)) {
                        OddJob.getInstance().getMessageManager().errorConsole();
                        return true;
                    }


                    // Player must specify an arena to join
                    if (strings.length == 2) {
                        // Check for a valid number
                        try {
                            id = Integer.parseInt(strings[1]);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        if (id == 0) {
                            OddJob.getInstance().getMessageManager().errorArena(strings[1], player.getUniqueId());
                            return true;
                        }
                        OddJob.getInstance().getArenaManager().addPlayer(player, id);
                    }
                    return true;
                }
                default: {
                    // Command sender did not apply any arena to join, returns a list.
                    StringBuilder sb = new StringBuilder();
                    for (int i : OddJob.getInstance().getArenaManager().getList().keySet()) {
                        Arena a = OddJob.getInstance().getArenaManager().getList().get(i);
                        sb.append(a.isDisabled() ? ChatColor.RED : ChatColor.GREEN).append(a.getId()).append(" ").append(a.getGameType()).append(" ")
                                .append("queue:").append(a.getPlayers().size()).append("/").append(a.getRequiredPlayers())
                                .append("\n");
                    }
                    commandSender.sendMessage(sb.toString());
                    return true;
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
