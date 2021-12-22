package me.buzz.coralmc.oneblockcore.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Config {

    private final File file;
    protected FileConfiguration config;

    public Config(JavaPlugin plugin, String fileName) {
        file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                plugin.saveResource(fileName, true);
                config = YamlConfiguration.loadConfiguration(file);
                createDefault();
                config.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().severe("Can't save configuration file \"" + file.getPath() + "\"!");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            FileConfiguration tmp = YamlConfiguration.loadConfiguration(file);
            config = YamlConfiguration.loadConfiguration(file);
            createDefault();
            if (shouldUpdateConfig()) {
                updateConfig(config, tmp);
                try {
                    file.createNewFile();
                    config = YamlConfiguration.loadConfiguration(file);
                    createDefault();
                    config.save(file);
                } catch (IOException e) {
                    Bukkit.getLogger().severe("Can't save configuration file \"" + file.getPath() + "\"!");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            reload();
        }
    }

    private void updateConfig(FileConfiguration defaultConfig, FileConfiguration old) {
        Map<String, Object> defaultMap = defaultConfig.getValues(true);
        Set<String> defaultKeySet = defaultMap.keySet();
        Set<String> oldKeySet = old.getValues(true).keySet();
        String firstVoice = null;
        boolean addedFirstVoice = false;
        for (String defaultKey : defaultKeySet) {
            boolean contains = false;
            for (String oldKey : oldKeySet) {
                if (oldKey.equals(defaultKey)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                old.set(defaultKey, defaultMap.get(defaultKey));
                if (!addedFirstVoice) {
                    firstVoice = defaultKey;
                    addedFirstVoice = true;
                }
            }
        }
        if (firstVoice != null) {
            try {
                old.save(file);
                Bukkit.getLogger().info("Added new configuration entries in file \"" + file.getPath() + "\" from \"" + firstVoice + "\" to below");
            } catch (IOException e) {
                Bukkit.getLogger().severe("Can't save configuration file \"" + file.getPath() + "\"!");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    protected boolean shouldUpdateConfig() {
        return true;
    }

    public boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public List<String> getList(String key) {
        List<String> result = config.getStringList(key);
        for (int i = 0; i < result.size(); i++) {
            result.set(i, result.get(i));
        }
        return result;
    }

    public void set(String key, Object obj) {
        config.set(key, obj);
    }

    public void save(){
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void createDefault();
}