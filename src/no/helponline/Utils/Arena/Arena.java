package no.helponline.Utils.Arena;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public class Arena {
    private Location min, max;
    private int gracePeriod;
    private List<Location> spawns;
    private String name;
    private String game;
    private List<Material> allowedBlocks;
    private double moneyKill,moneyWin;
    private Material chestType;
    private int chestData;
    private boolean deathMatch,refill;
    private List<Location> deathMatchSpawns;
    private int autoDeathMatch,playerDeathMatch;
    private Location domeMiddle;
    private int domeRadius;

    public Arena(Location min, Location max, List<Location> spawns, Material chestType, int chestData, int gracePeriod, String name, String game, boolean deathMatch, List<Location> deathMatchSpawns, List<Material> allowedBlocks, int autoDeathMatch, int playerDeathMatch, double moneyKill, double moneyWin, boolean chestRefill, Location domeMiddle, int domeRadius) {
        this.min = min;
        this.max = max;
        this.spawns = spawns;
        this.gracePeriod = gracePeriod;
        this.name = name;
        this.game = game;
        this.allowedBlocks = allowedBlocks;
        this.chestType = chestType;
        this.chestData = chestData;
        this.moneyKill = moneyKill;
        this.moneyWin = moneyWin;
        this.refill = chestRefill;

        this.deathMatch = deathMatch;
        this.deathMatchSpawns = deathMatchSpawns;

        if (deathMatchSpawns.isEmpty()) this.deathMatch = false;

        this.autoDeathMatch = autoDeathMatch;
        this.playerDeathMatch = playerDeathMatch;
        this.domeRadius = domeRadius;
        if(domeRadius > 0) {
            this.domeMiddle = domeMiddle;
            this.domeMiddle.setY(0);
        }
    }

    public List<Location> getSpawns() {
        return spawns;
    }

    public String getName() {
        return name;
    }
}
