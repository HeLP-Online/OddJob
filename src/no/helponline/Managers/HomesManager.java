package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Zone;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomesManager {
    private HashMap<UUID, HashMap<String, Location>> homes = new HashMap<>();

    public boolean add(UUID uuid, Location location) {
        return add(uuid, "home", location);
    }

    public boolean del(UUID uuid) {
        return del(uuid, "home");
    }

    public Location get(UUID uuid) {
        return get(uuid, "home");
    }

    public boolean has(UUID uuid) {
        return has(uuid, "home");
    }

    public boolean add(UUID uuid, String name, Location location) {
        Location loc = OddJob.getInstance().getMySQLManager().getHome(uuid, name);
        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(location.getChunk());
        if (guild != null && !guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(uuid)) && !guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
            OddJob.getInstance().getMessageManager().danger("You can't set home inside someone else guild", uuid,false);
            return true;
        }
        if (loc == null) {
            OddJob.getInstance().getMySQLManager().createHome(uuid, name, location);
        } else {
            OddJob.getInstance().getMySQLManager().updateHome(uuid, name, location);
        }
        return true;
    }

    public boolean del(UUID uuid, String name) {
        OddJob.getInstance().getMySQLManager().deleteHome(uuid, name);
        return false;
    }

    public Location get(UUID uuid, String name) {
        return OddJob.getInstance().getMySQLManager().getHome(uuid, name);
    }

    public boolean has(UUID uuid, String name) {
        return (OddJob.getInstance().getMySQLManager().getHome(uuid, name) != null);
    }

    public List<String> list(UUID uuid) {
        return OddJob.getInstance().getMySQLManager().listHomes(uuid);
    }
}
