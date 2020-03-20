package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

public class HomesCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole();
            return true;
        }

        Player player = (Player) commandSender;

        if (command.getName().equalsIgnoreCase("homes")) {
            switch (strings[0].toLowerCase()) {
                // Command /homes set <name>
                case "set":
                    if (strings.length == 2)
                        // TODO : Cost
                        // TODO : Permission
                        OddJob.getInstance().getHomesManager().add(player.getUniqueId(), strings[1], player.getLocation());
                    break;
                case "del":
                    if (strings.length == 2)
                        OddJob.getInstance().getHomesManager().del(player.getUniqueId(), strings[1]);
                    break;
                case "list":
                    OddJob.getInstance().getHomesManager().list(player.getUniqueId());
                    break;
                default:
                    if (player.hasPermission("oddjob.homes.others")) {
                        //Command /homes <player> <name>
                        try {
                            UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(strings[0]);
                            if (uuid != null) {
                                Location location = OddJob.getInstance().getHomesManager().get(uuid, strings[1]);
                                if (location != null) {
                                    OddJob.getInstance().getHomesManager().teleport(player, uuid,strings[1]);
                                    return true;
                                }
                                OddJob.getInstance().getMessageManager().errorHome(strings[1], player);
                                return true;
                            }
                        } catch (Exception ex) {
                        }
                    }
                    Location location = OddJob.getInstance().getHomesManager().get(player.getUniqueId(), strings[0]);
                    if (location == null) {
                        OddJob.getInstance().getMessageManager().errorHome(strings[0], player);
                        return true;
                    }
                    OddJob.getInstance().getHomesManager().teleport(player,player.getUniqueId(),strings[0]);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (!(commandSender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole();
            return list;
        }
        Player player = (Player) commandSender;
        String[] sub = {"set", "del", "list"};
        if (strings.length == 1) {
            if (player.hasPermission("oddjob.homes.others")) {
                for (String name : OddJob.getInstance().getPlayerManager().getNames()) {
                    if (name.toLowerCase().startsWith(strings[0].toLowerCase()) || strings[0].isEmpty()) {
                        list.add(name);
                    }
                }
            }
            Set<String> homes = OddJob.getInstance().getHomesManager().getList(player.getUniqueId());
            if (homes.size() > 0) {
                for (String name : homes) {
                    if (name.toLowerCase().startsWith(strings[0].toLowerCase()) || strings[0].isEmpty()) {
                        list.add(name);
                    }
                }
            }
            for (String st : sub) {
                if (st.startsWith(strings[0].toLowerCase()) || strings[0].isEmpty()) {
                    list.add(st);
                }
            }
        } else if (strings.length == 2 && strings[0].equalsIgnoreCase("del")) {
            for (String name : OddJob.getInstance().getHomesManager().getList(player.getUniqueId())) {
                if (name.toLowerCase().startsWith(strings[1].toLowerCase()) || strings[1].isEmpty()) {
                    list.add(name);
                }
            }
        } else if (strings.length == 2 && !strings[0].equalsIgnoreCase("del") && player.hasPermission("oddjob.homes.others")) {
            try {
                UUID uuid = OddJob.getInstance().getPlayerManager().getUUID(strings[0]);
                if (uuid != null) {
                    if (OddJob.getInstance().getHomesManager().getList(uuid).size() > 0) {
                        for (String name : OddJob.getInstance().getHomesManager().getList(uuid)) {
                            if (name.toLowerCase().startsWith(strings[1].toLowerCase()) || strings[1].isEmpty()) {
                                list.add(name);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
            }
        }
        return list;
    }

    enum Args {
        tp("Teleport you to your home"),
        list("Lists your homes"),
        set("Sets a new home"),
        del("Deletes a home");

        private String s;

        Args(String s) {
            this.s = s;
        }

        public String get() {
            return this.s;
        }
    }
}
