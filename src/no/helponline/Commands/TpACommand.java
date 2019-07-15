package no.helponline.Commands;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TpACommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("tpa")) {
            if (!(commandSender instanceof Player)) return true;
            if (strings.length != 1) {
                Bukkit.dispatchCommand(commandSender, command.getName() + " help");
                return true;
            }
            if (strings[0].equalsIgnoreCase("help")) {
                OddJob.getInstance().getMessageManager().danger("Use: /tpa <name>", commandSender);
                return true;
            }
            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null || !target.isOnline()) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                return true;
            }
            Player player = (Player) commandSender;
            if (OddJob.getInstance().getTeleportManager().hasRequest(player.getUniqueId())) {
                OddJob.getInstance().getMessageManager().danger("You have already sent an request to " + strings[0], player.getUniqueId());
                return true;
            }
            OddJob.getInstance().getTeleportManager().startTimer(player.getUniqueId());
            OddJob.getInstance().getTeleportManager().tpa(player.getUniqueId(), target.getUniqueId()); // player (sends request) // target (teleport to)
            OddJob.getInstance().getMessageManager().warning("You have requested to be teleported to " + target.getName(), player.getUniqueId());
            OddJob.getInstance().getMessageManager().warning(player.getName() + " want to be teleported to you. To accept this, you can click on 'ACCEPT' or use the command '/tpaccept'", target.getUniqueId());
            PlayerConnection connection = ((CraftPlayer) target).getHandle().playerConnection;
            PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"ACCPET\",\"color\":\"dark_green\",\"bold\":true,\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpaccept\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Accepting the teleport request\",\"color\":\"gold\"}]}}}"));
            connection.sendPacket(packet);
            packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"DENY\",\"color\":\"dark_red\",\"bold\":true,\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpdeny\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Declines the teleport request\",\"color\":\"gold\"}]}}}"));
            connection.sendPacket(packet);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length <= 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (strings.length == 1) {
                    if (player.getName().startsWith(strings[0])) {
                        list.add(player.getName());
                    }
                } else {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
