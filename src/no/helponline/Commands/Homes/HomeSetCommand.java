package no.helponline.Commands.Homes;

import no.helponline.OddJob;
import no.helponline.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeSetCommand extends SubCommand {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Sets a home";
    }

    @Override
    public String getSyntax() {
        return "/home set [name]";
    }

    @Override
    public String getPermission() {
        return "homes.set";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole();
            return;
        }

        if (args.length < 2) {
            OddJob.getInstance().getMessageManager().errorMissingArgs(sender);
        }

        Player player = (Player) sender;

        if (OddJob.getInstance().getHomesManager().getList(player.getUniqueId()).size() >= 5 && !player.hasPermission("homes.plenty")) {
            OddJob.getInstance().getMessageManager().errorHomeMaximal(player);
        } else if (args.length == 2 && player.hasPermission("homes")) {
            OddJob.getInstance().getHomesManager().add(player.getUniqueId(), args[1], player.getLocation());
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        return null;
    }
}
