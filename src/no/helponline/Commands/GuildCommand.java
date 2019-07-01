package no.helponline.Commands;

import no.helponline.Guilds.Guild;
import no.helponline.Managers.MessageManager;
import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuildCommand implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("guild")) {
            if (strings.length == 0) {
                Bukkit.dispatchCommand(commandSender, command.getName() + " help");
                return true;
            }
            if (strings.length == 2 && strings[0].equalsIgnoreCase("create")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    Guild guild = OddJob.getInstance().getGuildManager().create(player.getUniqueId(), strings[1]);
                    if (guild == null) {
                        MessageManager.warning("A Guild with the name `" + strings[1] + "` already exists", commandSender);
                    }
                    MessageManager.success("Guild `" + guild.getName() + "` created", commandSender);
                }

            } else if (strings.length == 1 && strings[0].equalsIgnoreCase("get")) {
                MessageManager.console(OddJob.getInstance().getGuildManager().getGuilds().toString());
            } else if (strings.length == 1 && strings[0].equalsIgnoreCase("claim")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    OddJob.getInstance().getGuildManager().claim(player);
                }
            } else if (strings.length == 1 && strings[0].equalsIgnoreCase("claims") &&
                    commandSender instanceof Player) {
                Player player = (Player) commandSender;
                Chunk chunk = player.getLocation().getChunk();
                Guild guild = OddJob.getInstance().getGuildManager().getGuildByChunk(chunk);
                if (guild != null) {
                    OddJob.getInstance().log("This chunk is owned by " + guild.getName());
                }
                OddJob.getInstance().getGuildManager().getGuildByMember(player.getUniqueId()).myClaims();
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            List<String> list = new ArrayList<>();
            for (Args a : Args.values()) {
                if (a.name().startsWith(strings[0])) {
                    list.add(a.name());
                }
            }
            return list;
        }
        return null;
    }

    enum Args {
        create("Creates a new guild"), join("Join an existing guild"), claim("Claim the chunk to your guild");

        private String help;

        Args(String help) {
            this.help = help;
        }

        public String get() {
            return this.help;
        }
    }
}
