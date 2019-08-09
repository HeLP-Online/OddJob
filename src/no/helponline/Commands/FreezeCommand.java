package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class FreezeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("freeze")) {
            if (strings.length == 1) {
                Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (target == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }
                if (OddJob.getInstance().getFreezeManager().get(target.getUniqueId()) != null) {
                    commandSender.sendMessage(strings[0] + " has already been frozen");
                    return true;
                }
                OddJob.getInstance().getFreezeManager().add(target.getUniqueId(), target.getLocation());
            }
        } else if (command.getName().equalsIgnoreCase("unfreeze")) {
            if (strings.length == 1) {
                Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (target == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }
                if (OddJob.getInstance().getFreezeManager().get(target.getUniqueId()) == null) {
                    commandSender.sendMessage(strings[0] + " was not frozen");
                    return true;
                }
                OddJob.getInstance().getFreezeManager().del(target.getUniqueId());
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
