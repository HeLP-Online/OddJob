package com.spillhuset.Utils;

import com.spillhuset.OddJob;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.*;

public class RollbackHandler {

    private RollbackHandler() {
    }

    public static void rollback(World world) {
        for (Player player : world.getPlayers()) {
            player.teleport(OddJob.getInstance().getArenaManager().getLobbyPoint());
        }

        OddJob.getInstance().getServer().unloadWorld(world, false);

        String originalName = world.getName().split("_")[0];

        rollback(originalName);
    }

    public static void rollback(String name) {
        String directory = OddJob.getInstance().getServer().getWorldContainer().getAbsolutePath();

        File source = new File(directory + "/" + name);
        File destination = new File(directory + "/" + name + "_active");

        delete(destination);

        try {
            copyFolder(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFolder(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
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
            InputStream input = new FileInputStream(source);
            OutputStream output = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            input.close();
            output.close();
        }
    }
}
