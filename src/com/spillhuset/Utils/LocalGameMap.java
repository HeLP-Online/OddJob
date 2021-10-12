package com.spillhuset.Utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;

public class LocalGameMap implements GameMap {
    private final File sourceWorldFolder;
    private File activeWorldFolder;

    private World bukkitWorld;

    public LocalGameMap(File worldFolder, String worldName, boolean loadOnInit) {
        this.sourceWorldFolder = new File(worldFolder, worldName);

        if (loadOnInit) load();
    }

    public boolean load() {
        if (isLoaded()) return true;

        this.activeWorldFolder = new File(Bukkit.getWorldContainer().getParentFile(), sourceWorldFolder.getName() + "_active_" + System.currentTimeMillis());

        try {
            FileUtil.copy(sourceWorldFolder, activeWorldFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        this.bukkitWorld = Bukkit.createWorld(new WorldCreator(activeWorldFolder.getName()));

        if (bukkitWorld != null) this.bukkitWorld.setAutoSave(false);
        return isLoaded();
    }


    public void unload() {
        if (bukkitWorld != null) Bukkit.unloadWorld(bukkitWorld, false);
        if (activeWorldFolder != null) FileUtil.delete(activeWorldFolder);

        bukkitWorld = null;
        activeWorldFolder = null;
    }

    public boolean restoreFromSource() {
        unload();
        return load();
    }


    public boolean isLoaded() {
        return getWorld() != null;

    }

    public World getWorld() {
        return bukkitWorld;
    }
}
