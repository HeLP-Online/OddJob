package com.spillhuset.Utils.Odd;

import com.spillhuset.Utils.Enum.ScoreBoard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class OddPlayer {
    private final UUID uuid;
    private final List<UUID> blacklist;
    private final List<UUID> whitelist;
    private boolean denyTpa;
    private final String name;
    private String banned;
    private ScoreBoard scoreBoard;
    private boolean denyTrade;
    private int maxHomes;
    private GameMode gameMode;
    public boolean save = false;

    public OddPlayer(UUID uuid, List<UUID> blacklist, List<UUID> whitelist, boolean denyTpa, String name, String banned, ScoreBoard scoreBoard, boolean denyTrade, int maxHomes, GameMode gm) {
        this.uuid = uuid;
        this.blacklist = blacklist;
        this.whitelist = whitelist;
        this.denyTpa = denyTpa;
        this.name = name;
        this.banned = banned;
        this.scoreBoard = scoreBoard;
        this.denyTrade = denyTrade;
        this.maxHomes = maxHomes;
        this.gameMode = gm;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void addBlacklist(UUID uuid) {
        blacklist.add(uuid);
        this.save = true;
    }

    public void removeBlacklist(UUID uuid) {
        blacklist.remove(uuid);
        this.save = true;
    }

    public void addWhitelist(UUID uuid) {
        whitelist.add(uuid);
        this.save = true;
    }

    public void removeWhitelist(UUID uuid) {
        whitelist.remove(uuid);
        this.save = true;
    }

    public List<UUID> getBlacklist() {
        return blacklist;
    }

    public List<UUID> getWhitelist() {
        return whitelist;
    }

    public String getBanned() {
        return banned;
    }

    public String getName() {
        return name;
    }

    public void setBanned(String banned) {
        this.banned = banned;
        this.save = true;
    }

    public void setDenyTpa(boolean denyTpa) {
        this.denyTpa = denyTpa;
        this.save = true;
    }

    public ScoreBoard getScoreboard() {
        return scoreBoard;
    }

    public void setDenyTrade(boolean denyTrade) {
        this.denyTrade = denyTrade;
        this.save = true;
    }

    public boolean getDenyTrade() {
        return denyTrade;
    }

    public boolean getDenyTpa() {
        return denyTpa;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public int getMaxHomes() {
        return maxHomes;
    }

    public void setMaxHomes(int i) {
        maxHomes = i;
        this.save = true;
    }

    public void setScoreboard(ScoreBoard score) {
        scoreBoard = score;
        this.save = true;
    }

    public void setGameMode(GameMode gm) {
        this.gameMode = gm;
        this.save = true;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public boolean isOp() {
        return Bukkit.getOfflinePlayer(uuid).isOp();
    }
}
