package no.helponline.Utils;

import java.util.List;
import java.util.UUID;

public class OddPlayer {
    private UUID uuid;
    private String name;
    private boolean denyTPA;
    private List<String> whiteList;
    private List<String> blackList;

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

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public boolean request(UUID from) {

    }

    public OddPlayer(UUID uuid, String name, boolean denyTPA, List<String> whiteList, List<String> blackList) {
        this.uuid = uuid;
        this.name = name;
        this.denyTPA = denyTPA;
        this.whiteList = whiteList;
        this.blackList = blackList;
    }
}
