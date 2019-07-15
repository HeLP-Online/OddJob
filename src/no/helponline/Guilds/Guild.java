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
    private HashMap<String, Object> settings = new HashMap<>();

    public Guild(UUID id, String name, UUID guildMaster) {
        this.id = id;
        this.name = name;
        this.zone = Zone.GUILD;
        this.members.put(guildMaster, Role.guildMaster);
        this.settings.put("invitedOnly", false);
        this.settings.put("firendlyFire", false);
    }

    public Guild(UUID id, String name, HashMap<UUID, Role> members, Zone zone, HashMap<String, Object> settings) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.zone = zone;
        this.settings = settings;
    }

    public String getName() {
        return this.name;
    }

    public Role promote(UUID uuid) {
        Role role = null;
        int level = members.get(uuid).level();
        level = (level < Role.admins.level()) ? level + 11 : Role.admins.level();
        for (Role r : Role.values()) {
            if (r.level() == level) {
                role = r;
            }
        }
        members.put(uuid, role);
        return role;
    }

    public Role demote(UUID uuid) {
        Role r = null;
        int level = members.get(uuid).level();
        level = (level >= Role.all.level()) ? level - 11 : Role.all.level();
        for (Role role : Role.values()) {
            if (role.level() == level) {
                r = role;
            }
        }
        members.put(uuid, r);
        return r;
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

    public Role getRole(UUID uuid) {
        return members.get(uuid);
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

    public void leave(UUID uniqueId) {
        members.remove(uniqueId);
    }

    public void setInvitedOnly(boolean bol) {
        settings.put("invitedOnly", bol);
    }

    public boolean getInvitedOnly() {
        return (boolean) settings.get("invitedOnly");
    }

    public void setFriendlyFire(boolean bol) {
        settings.put("Friendlyfire", bol);
    }

    public boolean getFriendlyFire() {
        return (boolean) settings.get("friendlyFire");
    }

    public HashMap<String, Object> getSettings() {
        return settings;
    }
}
