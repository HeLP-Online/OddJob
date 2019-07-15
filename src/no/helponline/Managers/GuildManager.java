package no.helponline.Managers;

import no.helponline.Guilds.Guild;
import no.helponline.Guilds.Role;
import no.helponline.Guilds.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GuildManager {
    private HashMap<UUID, Guild> guilds;
    private HashMap<Chunk, UUID> chunks;

    public GuildManager() {
        guilds = new HashMap<>();
        chunks = new HashMap<>();
    }

    public Guild create(UUID uniqueId, String string) {
        Guild guild = getGuildByMember(uniqueId);
        if (guild != null) {
            return guild;
        }
        UUID uuid = UUID.randomUUID();
        guild = new Guild(uuid, string, uniqueId);
        guilds.put(uuid, guild);
        return guild;
    }


    public HashMap<UUID, Guild> getGuilds() {
        return guilds;
    }


    public Guild getGuildByMember(UUID uuid) {
        for (Guild guild : guilds.values()) {
            if (guild.isMember(uuid)) {
                return guild;
            }
        }
        return null;
    }


    public Guild getGuild(UUID uuid) {
        return guilds.get(uuid);
    }


    public boolean claim(Player player) {
        boolean b = false;
        Chunk c = player.getLocation().getChunk();
        UUID guild = getGuildByMember(player.getUniqueId()).getId();
        if (!chunks.containsKey(c)) {
            chunks.put(c, guild);
            b = true;
            player.sendMessage("You have claimed " + c.toString() + " to " + getGuild(guild).getName());
        }
        return b;
    }


    public Guild getGuildByChunk(Chunk chunk) {
        return getGuild(chunks.get(chunk));
    }


    public List<Chunk> getChunksByGuild(UUID guild) {
        List<Chunk> chunks = new ArrayList<>();
        for (Chunk c : this.chunks.keySet()) {
            if (this.chunks.get(c).equals(guild)) {
                chunks.add(c);
            }
        }
        return chunks;
    }

    public void set(UUID uuid, String name, HashMap<UUID, Role> members, List<Chunk> chunks, Zone zone, HashMap<String, Object> settings) {
        Guild guild = new Guild(uuid, name, members, zone, settings);
        guilds.put(uuid, guild);
        for (Chunk chunk : chunks) {
            this.chunks.put(chunk, uuid);
        }
    }

    public List<Chunk> getChunks(UUID uuid) {
        List<Chunk> chunks = new ArrayList<>();
        for (Chunk chunk : this.chunks.keySet()) {
            if (this.chunks.containsValue(uuid)) chunks.add(chunk);
        }
        return chunks;
    }

    public void join(UUID guild, UUID player) {
        Guild g = getGuild(guild);
        g.setMember(player);
    }

    public UUID getGuildByName(String string) {
        for (UUID uuid : guilds.keySet()) {
            if (ChatColor.stripColor(string).equalsIgnoreCase(guilds.get(uuid).getName())) {
                return uuid;
            }
        }
        return null;
    }
}
