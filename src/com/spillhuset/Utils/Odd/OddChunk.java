package com.spillhuset.Utils.Odd;

import java.util.UUID;

public class OddChunk {
    private UUID guild, world;
    private int x,z;

    public OddChunk(UUID world, int x, int z, UUID guild) {
        this.guild = guild;
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public UUID getGuild() {
        return guild;
    }

    public void setGuild(UUID guild) {
        this.guild = guild;
    }

    public UUID getWorld() {
        return world;
    }

    public void setWorld(UUID world) {
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
