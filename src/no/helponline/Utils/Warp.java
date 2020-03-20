package no.helponline.Utils;

import org.bukkit.Location;

public class Warp {
    String name,password;
    Location location;
    Double cost;
    public Warp(String name, Location location, String password,Double cost) {
        this.name = name;
        this.location = location;
        this.password = password;
        this.cost = cost;
    }

    public boolean pass(String password) {
        return this.password.equals(password);
    }

    public Location get() {
        return location;
    }

    public String getName() {
        return name;
    }
    public boolean hasPassword() {
        return !password.equals("");
    }

    public double getCost() {
        return cost;
    }

    public String getPassword() {
        return password;
    }
}
