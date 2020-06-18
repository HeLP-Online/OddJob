package no.helponline.Commands;

import no.helponline.OddJob;
import no.helponline.Utils.Arena.Arena;
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
        if (strings.length >= 1) {
            switch (strings[0]) {
                case "leave" -> {
                    if (!(commandSender instanceof Player)) {
                        OddJob.getInstance().getMessageManager().errorConsole();
                        return true;
                    }
                    Player player = (Player) commandSender;

                    OddJob.getInstance().getArenaManager().removePlayer(player);
                }
                case "join" -> {
                    if (!(commandSender instanceof Player)) {
                        OddJob.getInstance().getMessageManager().errorConsole();
                        return true;
                    }
                    Player player = (Player) commandSender;
                    if (strings.length == 2) {
                        try {
                            id = Integer.parseInt(strings[1]);
                        } catch (Exception ex) {

                            ex.printStackTrace();
                        }

                        if (id == 0) {
                            OddJob.getInstance().getMessageManager().errorArena(strings[1],player.getUniqueId());
                            return true;
                        }
                        OddJob.getInstance().getArenaManager().addPlayer(player,id);
                    }
                }
                default -> {
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
