package no.helponline.Utils;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.ScoreBoard;

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

    public OddPlayer(UUID uuid, List<UUID> blacklist, List<UUID> whitelist, boolean denyTpa, String name, String banned, ScoreBoard scoreBoard,boolean denyTrade) {
        this.uuid = uuid;
        this.blacklist = blacklist;
        this.whitelist = whitelist;
        this.denyTpa = denyTpa;
        this.name = name;
        this.banned = banned;
        this.scoreBoard = scoreBoard;
        this.denyTrade = denyTrade;
    }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
        OddJob.getInstance().getScoreManager().set(getUuid(),scoreBoard);
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

    public void setDenyTpa(boolean tpaDeny) {
        this.denyTpa = tpaDeny;
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
}
