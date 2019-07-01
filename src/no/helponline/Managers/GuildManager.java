package no.helponline.Managers;

import no.helponline.Guilds.Guild;
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
        this.guilds = new HashMap<>();
        this.chunks = new HashMap<>();
    }

    public Guild create(UUID uniqueId, String string) {
        Guild guild = getGuildByMember(uniqueId);
        if (guild != null) {
            return guild;
        }
        UUID uuid = UUID.randomUUID();
        guild = new Guild(uuid, string, uniqueId);
        this.guilds.put(uuid, guild);
        return guild;
    }


    public HashMap<UUID, Guild> getGuilds() {
        return this.guilds;
    }


    public Guild getGuildByMember(UUID uuid) {
        for (Guild guild : this.guilds.values()) {
            if (guild.isMember(uuid)) {
                return guild;
            }
        }
        return null;
    }


    public Guild getGuild(UUID uuid) {
        return this.guilds.get(uuid);
    }


    public boolean claim(Player player) {
        boolean b = false;
        Chunk c = player.getLocation().getChunk();
        if (!this.chunks.containsKey(c)) {
            this.chunks.put(c, getGuildByMember(player.getUniqueId()).getId());
            b = true;
        }
        return b;
    }


    public Guild getGuildByChunk(Chunk chunk) {
        return getGuild(this.chunks.get(chunk));
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
}
