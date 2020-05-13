package no.helponline.Utils.Arena;

import no.helponline.OddJob;
import org.bukkit.command.CommandSender;

public class PermissionHandler {
    private static boolean usePermission = OddJob.getInstance().getConfig().getBoolean("use-permissions");

    public static void reinitializeDatabase() {
        usePermission = OddJob.getInstance().getConfig().getBoolean("use-permissions");
    }

    public boolean hasPermission(CommandSender sender, Permission permission) {
        if (usePermission) {
            return sender.hasPermission(permission.getPermission());
        } else {
            if (sender.isOp()) return true;
            else return permission == Permission.JOIN || permission == Permission.LIST || permission == Permission.SPECTATE || permission == Permission.STATS;
        }
    }


}
