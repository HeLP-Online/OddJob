package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Home;
import no.helponline.Utils.Enum.Zone;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class HomesManager {
    private HashMap<UUID,Home> homes = new HashMap<>();

    public boolean add(UUID uuid, String name, Location location) {
        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(location.getChunk());
        if (guild != null &&
                !guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(uuid)) &&
                !guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
            OddJob.getInstance().getMessageManager().danger("You can't set home inside someone else guild", uuid,false);
            return true;
        }
        if (homes.containsKey(uuid)) {
            homes.get(uuid).add(name,location);
        }else{
            homes.put(uuid, new Home(uuid, location, name));
        }
        return true;
    }

    public void del(UUID uuid, String name) {
        if (homes.containsKey(uuid)) {
            Home home = homes.get(uuid);
            home.remove(name);
        }
        OddJob.getInstance().getMySQLManager().deleteHome(uuid, name);
    }

    public Location get(UUID uuid, String name) {
        return homes.get(uuid).get(name);
    }

    public boolean has(UUID uuid, String name) {
        return (get(uuid,name) != null);
    }

    public Set<String> list(UUID uuid) {
        return homes.get(uuid).list();
    }

    public void load() {
        homes = OddJob.getInstance().getMySQLManager().loadHomes();
    }
    public void save() {
        OddJob.getInstance().getMySQLManager().saveHomes(homes);
    }
}
