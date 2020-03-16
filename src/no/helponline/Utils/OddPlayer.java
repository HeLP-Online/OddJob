package no.helponline.Utils;

import no.helponline.Utils.Enum.ScoreBoard;

import java.util.List;
import java.util.UUID;

public class OddPlayer {
    UUID uuid;
    List<UUID> blacklist, whitelist;
    boolean tpa_deny;
    String name, banned;
    private ScoreBoard scoreBoard;

    public OddPlayer(UUID uuid, List<UUID> blacklist, List<UUID> whitelist, boolean tpa_deny, String name, String banned, ScoreBoard scoreBoard) {
        this.uuid = uuid;
        this.blacklist = blacklist;
        this.whitelist = whitelist;
        this.tpa_deny = tpa_deny;
        this.name = name;
        this.banned = banned;
        this.scoreBoard = scoreBoard;
    }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
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

    public boolean isDeny_tpa() {
        return tpa_deny;
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

    public void setDeny_tpa(boolean tpa_deny) {
        this.tpa_deny = tpa_deny;
    }

    public ScoreBoard getScoreboard() {
        return scoreBoard;
    }
}
