package me.buzz.coralmc.oneblockcore.files.impl;

import me.buzz.coralmc.oneblockcore.files.Config;
import me.buzz.coralmc.oneblockcore.server.ServerType;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigDatabase extends Config {

    public static String SERVER_TYPE = "server.type";
    public static String CHAT_FORMAT = "chat.format";
    public static String DATABASE_ADDRESS = "mongo.address";
    public static String DATABASE_COLLECTION_NAME = "mongo.collectionName";
    public static String DATABASE_USER = "mongo.user";
    public static String DATABASE_PASSWORD = "mongo.password";

    public ConfigDatabase(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    protected void createDefault() {
        if (config.get(SERVER_TYPE) == null) config.set(SERVER_TYPE, ServerType.ISLANDS.toString());
        if (config.get(DATABASE_ADDRESS) == null) config.set(DATABASE_ADDRESS, "localhost:27017");
        if (config.get(DATABASE_COLLECTION_NAME) == null) config.set(DATABASE_COLLECTION_NAME, "OneblockGame");
        if (config.get(DATABASE_USER) == null) config.set(DATABASE_USER, "root");
        if (config.get(DATABASE_PASSWORD) == null) config.set(DATABASE_PASSWORD, "insert");
        if (config.get(CHAT_FORMAT) == null) config.set(CHAT_FORMAT, "%1$s: %2$s");
    }


}
