package no.helponline.Commands.Homes;

import no.helponline.OddJob;
import no.helponline.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeDelCommand extends SubCommand {
    @Override
    public String getName() {
        return "del";
    }

    @Override
    public String getDescription() {
        return "Delete a home";
    }

    @Override
    public String getSyntax() {
        return "/home del [name]";
    }

    @Override
    public String getPermission() {
        return "homes.del";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length == 2) {
            OddJob.getInstance().getHomesManager().del(player.getUniqueId(), args[1]);
        }
    }

    @Override
    public List<String> getTab(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        Player player = (Player) sender;
        for (String name : OddJob.getInstance().getHomesManager().getList(player.getUniqueId())) {
            if (args.length == 2) {
                if (args[1].isEmpty()) {
                    list.add(name);
                } else if (name.startsWith(args[1])) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
