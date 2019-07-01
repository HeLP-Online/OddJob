package no.helponline.Guilds;

import no.helponline.OddJob;
import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.UUID;

public class Guild {
    private UUID id;
    private String name;
    private UUID guildMaster;
    private Zone zone;
    private HashMap<UUID, Role> members = new HashMap<>();

    public Guild(UUID id, String name, UUID guildMaster) {
        this.id = id;
        this.name = name;
        this.guildMaster = guildMaster;
        this.zone = Zone.GUILD;
        this.members.put(guildMaster, Role.guildMaster);
    }


    public String getName() {
        return this.name;
    }


    public UUID getGuildMaster() {
        return this.guildMaster;
    }


    public UUID getId() {
        return this.id;
    }


    public boolean join(UUID uuid) {
        Guild guild = OddJob.getInstance().getGuildManager().getGuildByMember(uuid);
        if (guild != null) {
            return false;
        }
        this.members.put(uuid, Role.members);
        return true;
    }


    public boolean isMember(UUID uuid) {
        return this.members.containsKey(uuid);
    }


    public Zone getZone() {
        return this.zone;
    }


    public boolean setZone(Zone zone, UUID guildMaster) {
        boolean b = false;
        if (!zone.equals(Zone.GUILD)) {
            this.members.clear();
            this.members = null;
            b = true;
        } else {
            Guild guild = OddJob.getInstance().getGuildManager().getGuildByMember(guildMaster);
            if (guild == null) {
                this.members.put(guildMaster, Role.guildMaster);
                b = true;
            }
        }
        this.zone = zone;
        return b;
    }

    public void myClaims() {
        int i = 0;
        for (Chunk c : OddJob.getInstance().getGuildManager().getChunksByGuild(this.id)) {
            i++;
            OddJob.getInstance().log("Chunk: " + i + "; X: " + c.getX() + "; Z: " + c.getZ() + "; World: " + c.getWorld().getName() + ";");
        }
    }

    public void setName(String name) {
        this.name = name;
    }
}
