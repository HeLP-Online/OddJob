package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.Arena.Arena;
import no.helponline.Utils.Arena.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class ArenaCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        int id = 0;
        Player player = (Player) commandSender;
        if (strings.length >= 1) {
            switch (strings[0]) {
                case "set" : {
                    switch (strings[1]) {
                        case "type": OddJob.getInstance().getArenaManager().getArena(id).setGameType(ArenaManager.GameType.valueOf(strings[2]));
                        case "lobby": {
                            if (strings.length == 3 && strings[2].equalsIgnoreCase("remove")) {
                                OddJob.getInstance().getArenaManager().getArena(id).removeLobbySpawn();
                            }else {
                                OddJob.getInstance().getArenaManager().getArena(id).setLobbySpawn(player.getLocation());
                            }
                        }
                        case "spawn": {
                            if (strings.length == 4 && strings[2].equalsIgnoreCase("remove")) {
                                OddJob.getInstance().getArenaManager().getArena(id).removeGameSpawn(Integer.parseInt(strings[3]));
                            } else {
                                OddJob.getInstance().getArenaManager().getArena(id).setGameSpawn(player.getLocation());
                            }
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

                    OddJob.getInstance().getArenaManager().createArena(player);
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

                    }
                }
                case "leave": {
                    // Console cannot execute this command
                    if (!(commandSender instanceof Player)) {
                        OddJob.getInstance().getMessageManager().errorConsole();
                        return true;
                    }


                    OddJob.getInstance().getArenaManager().removePlayer(player);
                }
                break;
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
                }
                break;
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
