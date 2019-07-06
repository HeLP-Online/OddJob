package no.helponline.Commands;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import no.helponline.Managers.BanManager;
import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class EssentialsCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0 && command.getName().equalsIgnoreCase("suicide")) {
            // COMMAND SUICIDE
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                player.setHealth(0D);
            }
            //TODO permissions
        } else if (strings.length == 1 && command.getName().equalsIgnoreCase("kill")) {
            // COMMAND KILL
            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target != null) {
                target.setHealth(0D);
                OddJob.getInstance().log(commandSender.getName() + " killed " + strings[0]);
            } else {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
            }
            //TODO permissions
        } else if (strings.length > 0 && command.getName().equalsIgnoreCase("tp")) {
            // COMMAND TP
            if (strings.length == 2) {
                Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[1]));
                if (target == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[1], commandSender);
                    return true;
                }
                Player destination = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (destination == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }

                target.teleport(destination.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                OddJob.getInstance().getMessageManager().success("You have been teleported to " + destination.getName(), target.getUniqueId());
            } else if (strings.length == 4 || strings.length == 5) {
                Player player = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (player == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }
                int x = Integer.parseInt(strings[1]);
                int y = Integer.parseInt(strings[2]);
                int z = Integer.parseInt(strings[3]);
                World world = (strings.length == 5) ? Bukkit.getWorld(strings[4]) : player.getWorld();

                player.teleport(new Location(world, x, y, z), PlayerTeleportEvent.TeleportCause.COMMAND);
                OddJob.getInstance().getMessageManager().success("You have been teleported to a specific location", player.getUniqueId());
            } else if (commandSender instanceof Player && strings.length == 1) {
                Player target = (Player) commandSender;
                Player destination = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (destination == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }
                target.teleport(destination.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                OddJob.getInstance().getMessageManager().success("You have been teleported to " + destination.getName(), target.getUniqueId());
            }
            //TODO permissions
        } else if (command.getName().equalsIgnoreCase("clear")) {
            // COMMAND CLEAR
            Player target = null;
            if (strings.length == 1) {
                target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
                if (target == null) {
                    OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                    return true;
                }
            } else if (strings.length == 0 && commandSender instanceof Player) {
                target = (Player) commandSender;
            }
            if (target != null) {
                target.getInventory().clear();
            }
            //todo permission
        } else if (command.getName().equalsIgnoreCase("tpall")) {
            // COMMAND TPALL
            if (!(commandSender instanceof Player)) return true;
            Player sender = (Player) commandSender;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.equals(sender)) player.teleport(sender);
                player.sendMessage("Everyone was teleported!");
            }
            //todo permission
        } else if (command.getName().equalsIgnoreCase("kick")) {
            // COMMAND KICK
            String message = OddJob.getInstance().getConfigManager().getString("default.kick_message");
            if (strings.length >= 2) {
                int length = strings.length;
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= length; i++) {
                    sb.append(strings[i]);
                }
                message = sb.toString();
            }
            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                return true;
            }
            target.kickPlayer(message);
            commandSender.sendMessage("Player " + target.getName() + " kicked, reason: " + message);
            // todo permission
        } else if (command.getName().equalsIgnoreCase("invsee")) {
            // COMMAND INVSEE
            if (!(commandSender instanceof Player)) return true;
            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                return true;
            }
            Player player = (Player) commandSender;
            player.openInventory(target.getInventory());
            //todo permission
        } else if (command.getName().equalsIgnoreCase("tpa")) {
            if (!(commandSender instanceof Player)) return true;
            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                return true;
            }
            Player player = (Player) commandSender;
            OddJob.getInstance().getTeleportManager().tpa(player.getUniqueId(), target.getUniqueId());
            PlayerConnection connection = ((CraftPlayer) target).getHandle().playerConnection;
            PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"ACCEPT\",\"color\":\"dark_green\",\"bold\":true,\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpaccept\"}},{\"text\":\" || \",\"color\":\"none\",\"bold\":false},{\"text\":\"DENY\",\"color\":\"dark_red\",\"bold\":true,\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpdeny\"}}"));
            connection.sendPacket(packet);
        } else if (command.getName().equalsIgnoreCase("tpaccept")) {
            if (!(commandSender instanceof Player)) return true;
            Player player = (Player) commandSender;
            OddJob.getInstance().getTeleportManager().accept(player.getUniqueId());
        } else if (command.getName().equalsIgnoreCase("tpdeny")) {
            if (!(commandSender instanceof Player)) return true;
            Player player = (Player) commandSender;
            OddJob.getInstance().getTeleportManager().deny(player.getUniqueId());
        } else if (command.getName().equalsIgnoreCase("ban")) {
            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                return true;
            }
            String text = OddJob.getInstance().getConfigManager().getString("default.kick_message");
            if (strings.length >= 2) {
                int length = strings.length;
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= length; i++) {
                    sb.append(strings[i]);
                }
                text = sb.toString();
            }
            BanManager.ban(target.getUniqueId(), text);
        } else if (command.getName().equalsIgnoreCase("unban")) {

            Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(strings[0]));
            if (target == null) {
                OddJob.getInstance().getMessageManager().warning("Sorry, we can't find " + strings[0], commandSender);
                return true;
            }

            BanManager.unban(target.getUniqueId());
        }
        //TODO
        //tphere,ban,unban
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
