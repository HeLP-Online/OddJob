package no.helponline.Commands;

import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
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
                OddJob.getInstance().getMessageManager().danger("Use: /tpa <name>", commandSender,false);
                return true;
            }
            Player destinationPlayer = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (destinationPlayer == null || !destinationPlayer.isOnline()) {
                OddJob.getInstance().getMessageManager().errorPlayer(strings[0],commandSender);
                return true;
            }
            Player movingPlayer = (Player) commandSender;
            if (OddJob.getInstance().getTeleportManager().hasRequest(movingPlayer.getUniqueId())) {
                OddJob.getInstance().getMessageManager().danger("You have already sent an request to " + strings[0], movingPlayer.getUniqueId(),false);
                return true;
            }
            if (OddJob.getInstance().getTeleportManager().request(movingPlayer.getUniqueId(), destinationPlayer.getUniqueId())) { // player (sends request) // target (teleport to)
                OddJob.getInstance().getMessageManager().success("You have requested to be teleported to " + ChatColor.DARK_AQUA+destinationPlayer.getName(), movingPlayer.getUniqueId(),false);
                OddJob.getInstance().getMessageManager().warning(ChatColor.DARK_AQUA+movingPlayer.getName() + ChatColor.YELLOW+" want to be teleported to you. To accept this, you can click on 'ACCEPT'", destinationPlayer.getUniqueId(),false);
                PlayerConnection connection = ((CraftPlayer) destinationPlayer).getHandle().playerConnection;
                PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"ACCEPT\",\"color\":\"dark_green\",\"bold\":true,\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpaccept " + movingPlayer.getUniqueId().toString() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Accepting the teleport request\",\"color\":\"gold\"}]}}}"));
                connection.sendPacket(packet);
                packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"DENY\",\"color\":\"dark_red\",\"bold\":true,\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpdeny " + movingPlayer.getUniqueId().toString() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Declines the teleport request\",\"color\":\"gold\"}]}}}"));
                connection.sendPacket(packet);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length <= 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (strings.length == 1) {
                    if (player.getName().startsWith(strings[0]) && !player.isOp()) {
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
