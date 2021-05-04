package com.spillhuset.Utils;

import com.spillhuset.OddJob;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

import java.io.*;

public class Rollback {
    public static void rollback(World world) {
        OddJob.getInstance().getMessageManager().console("Rollback started");
        for (Player player : world.getPlayers()) {
            player.teleport(OddJob.getInstance().getSpawn());
        }

        OddJob.getInstance().getServer().unloadWorld(world, false);

        String originalName = world.getName();
        String rootDirectory = OddJob.getInstance().getServer().getWorldContainer().getAbsolutePath();

        File srcFolder = new File(rootDirectory + "/" + originalName + "_backup");
        File destFolder = new File(rootDirectory + "/" + originalName);

        delete(destFolder);

        try {
            copyFolder(srcFolder, destFolder);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        OddJob.getInstance().getServer().createWorld(new WorldCreator(originalName));
        OddJob.getInstance().getMessageManager().console("rollback finished");
    }

    private static void delete(File file) {
        if (file.isDirectory()) {
            String[] files = file.list();

            if (files != null) {
                for (String f : files) {
                    File fDelete = new File(f);
                    delete(fDelete);
                }
            }
        } else {
            file.delete();
        }
    }

    public static void backup(World world) {
        OddJob.getInstance().getMessageManager().console("Backup started");
        String rootDirectory = OddJob.getInstance().getServer().getWorldContainer().getAbsolutePath();
        String originalName = world.getName();

        File sourceFolder = new File(rootDirectory + "/" + originalName);
        File destinationFolder = new File(rootDirectory + "/" + originalName + "_backup");

        delete(destinationFolder);

        try {
            FileUtils.copyDirectory(sourceFolder, destinationFolder);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        OddJob.getInstance().getMessageManager().console("Backup finished");
    }

    private static void copyFolder(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (destination.exists()) {
                destination.mkdir();
            }
            String[] files = source.list();

            if (files != null) {
                for (String file : files) {
                    File sourceFile = new File(source, file);
                    File destinationFile = new File(destination, file);
                    copyFolder(sourceFile, destinationFile);
                }
            }
        } else {
            InputStream in = new FileInputStream(source);
            OddJob.getInstance().log(destination.getPath());
            if (!destination.exists()) destination.createNewFile();
            OutputStream out = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }
}
