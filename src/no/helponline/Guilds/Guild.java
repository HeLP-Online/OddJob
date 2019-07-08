package no.helponline.Guilds;

import no.helponline.OddJob;
import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.UUID;

public class Guild {
    private UUID id;
    private String name;
    private Zone zone;
    private HashMap<UUID, Role> members = new HashMap<>();

    public Guild(UUID id, String name, UUID guildMaster) {
        this.id = id;
        this.name = name;
        this.zone = Zone.GUILD;
        this.members.put(guildMaster, Role.guildMaster);
    }

    public Guild(UUID id, String name, HashMap<UUID, Role> members, Zone zone) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.zone = zone;
    }

    public String getName() {
        return this.name;
    }


    public UUID getGuildMaster() {
        for (UUID uuid : members.keySet()) {
            if (members.get(uuid).equals(Role.guildMaster)) return uuid;
        }
        return null;
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

    public HashMap<UUID, Role> getMembers() {
        return members;
    }

    public boolean getConfig(String plugin, String string, UUID guildId, boolean def) {
        return OddJob.getInstance().getConfigManager().getBoolean(plugin, guildId, string, def);
    }

    public void setMember(UUID player) {
        members.put(player, Role.members);
        OddJob.getInstance().getMessageManager().success("You have successfully joined " + name, player);
        for (UUID uuid : members.keySet()) {
            if (uuid.equals(player)) return;
            OddJob.getInstance().getMessageManager().success("Welcome " + OddJob.getInstance().getPlayerManager().getName(player) + " to the guild!", uuid);
        }
    }
}
