package no.helponline.Utils.Arena;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YMLLoader {
    File rawFile;
    String path, fileName;

    public YMLLoader(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public FileConfiguration getFileConfiguration() {
        rawFile = new File(path, fileName);
        if (!rawFile.exists()) {
            new File(path).mkdirs();
            try {
                rawFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfiguration fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(rawFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return fileConfiguration;
    }
}
