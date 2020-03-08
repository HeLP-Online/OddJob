package no.helponline.Utils;

import java.util.HashMap;
import java.util.UUID;

public class Guild {
    String name;
    Zone zone;
    UUID guildUUID;
    boolean invited_only, friendly_fire;
    HashMap<UUID, Role> members = new HashMap<>();
    Role permissionInvite;
    private int chunks;

    public Guild(String name, Zone zone, UUID guildUUID, boolean invited_only, boolean friendly_fire, UUID playerUUID, Role role) {
        this.name = name;
        this.zone = zone;
        this.guildUUID = guildUUID;
        this.invited_only = invited_only;
        this.friendly_fire = friendly_fire;
        this.permissionInvite = Role.Members;
        this.members.put(playerUUID, role);
    }

    public Guild(String name, Zone zone, UUID guildUUID, boolean invited_only, boolean friendly_fire) {
        this.name = name;
        this.zone = zone;
        this.guildUUID = guildUUID;
        this.invited_only = invited_only;
        this.friendly_fire = friendly_fire;
        this.permissionInvite = Role.Members;
    }

    public Guild(UUID guildUUID, String name, Zone zone, boolean invite_only, boolean friendly_fire, Role invite_permission, HashMap<UUID, Role> members) {
        this.guildUUID = guildUUID;
        this.name = name;
        this.zone = zone;
        this.invited_only = invite_only;
        this.friendly_fire = friendly_fire;
        this.permissionInvite = invite_permission;
        this.members = members;
    }

    public UUID getGuildUUID() {
        return guildUUID;
    }

    public String getName() {
        return name;
    }

    public Zone getZone() {
        return zone;
    }

    public HashMap<UUID, Role> getMembers() {
        return members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInvitedOnly(boolean bol) {
        this.invited_only = bol;
    }

    public void setFriendlyFire(boolean bol) {
        this.friendly_fire = bol;
    }

    public Role getPermissionInvite() {
        return permissionInvite;
    }

    public boolean getInvitedOnly() {
        return invited_only;
    }

    public int getChunks() {
        return chunks;
    }

    public void addChunks() {
        chunks++;
    }

    public void removeChunks() {
        chunks--;
    }

    public boolean getFriendlyFire() {
        return friendly_fire;
    }
}
