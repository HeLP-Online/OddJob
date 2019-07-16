package no.helponline.Utils;

import no.helponline.OddJob;

import java.util.List;
import java.util.UUID;

public class OddPlayer {
    private UUID uuid;
    private String name;
    private boolean denyTPA;
    private List<UUID> whiteList;
    private List<UUID> blackList;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDenyTPA() {
        return denyTPA;
    }

    public void setDenyTPA(boolean denyTPA) {
        this.denyTPA = denyTPA;
    }

    public List<UUID> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<UUID> whiteList) {
        this.whiteList = whiteList;
    }

    public List<UUID> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<UUID> blackList) {
        this.blackList = blackList;
    }

    public boolean request(UUID from) {
        OddJob.getInstance().log("request: " + !isDenyTPA());
        boolean request = !isDenyTPA();
        if (getWhiteList().contains(from)) {
            OddJob.getInstance().log("whitelist");
            request = true;
        } else if (getBlackList().contains(from)) {
            OddJob.getInstance().log("blacklist");
            request = false;
        } else if (isDenyTPA()) {
            OddJob.getInstance().log("tpa deny");
            OddJob.getInstance().getMessageManager().warning(name + " is denying all request!", from);
            request = false;
        }
        return request;
    }

    public OddPlayer(UUID uuid, String name, boolean denyTPA, List<UUID> whiteList, List<UUID> blackList) {
        this.uuid = uuid;
        this.name = name;
        this.denyTPA = denyTPA;
        this.whiteList = whiteList;
        this.blackList = blackList;
    }

    public void addWhiteList(UUID uniqueId) {
        whiteList.add(uniqueId);
    }

    public void addBlackList(UUID uniqueId) {
        blackList.add(uniqueId);
    }

    public void delWhiteList(UUID uniqueId) {
        whiteList.remove(uniqueId);
    }

    public void delBlackList(UUID uniqueId) {
        blackList.remove(uniqueId);
    }
}
