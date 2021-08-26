package com.spillhuset.Utils.Odd;

import com.spillhuset.SQL.PlayerSQL;
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

    public OddPlayer(UUID uuid, List<UUID> blacklist, List<UUID> whitelist, boolean denyTpa, String name, String banned, ScoreBoard scoreBoard, boolean denyTrade, int maxHomes,GameMode gm) {
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
    }

    public void removeBlacklist(UUID uuid) {
        blacklist.remove(uuid);
    }

    public void addWhitelist(UUID uuid) {
        whitelist.add(uuid);
    }

    public void removeWhitelist(UUID uuid) {
        whitelist.remove(uuid);
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
    }

    public void setDenyTpa(boolean denyTpa) {
        this.denyTpa = denyTpa;
    }

    public ScoreBoard getScoreboard() {
        return scoreBoard;
    }

    public void setDenyTrade(boolean denyTrade) {
        this.denyTrade = denyTrade;
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
    }

    public void setScoreboard(ScoreBoard score) {
        scoreBoard = score;
        PlayerSQL.setScoreboard(uuid, score);
    }

    public void setGameMode(GameMode gm) {
        this.gameMode = gm;
        PlayerSQL.setGameMode(uuid,gm);
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }
    public boolean isOp() {
        return getPlayer().isOp();
    }
}
