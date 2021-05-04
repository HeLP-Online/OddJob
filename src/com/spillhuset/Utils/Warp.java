package com.spillhuset.Utils;

import org.bukkit.Location;

import java.util.UUID;

public class Warp {
    String name, password;
    Location location;
    Double cost;
    UUID uuid;

    public Warp(String name, Location location, String password, Double cost, UUID uuid) {
        this.uuid = uuid;
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

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setPasswd(String n) {
        this.password = n;
    }

    public void setName(String n) {
        this.name = n;
    }

    public Location getLocation() {
        return location;
    }
}
