package me.buzz.coralmc.oneblockcore.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.database.codec.*;
import me.buzz.coralmc.oneblockcore.files.FileService;
import me.buzz.coralmc.oneblockcore.files.impl.ConfigDatabase;
import me.buzz.coralmc.oneblockcore.server.service.Service;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public class DataService implements Service {
    public final static String ISLAND_WORLD_TAG = "island_";
    private final OneblockCore core = OneblockCore.get();

    @Getter
    private Datastore datastore;

    @Override
    public void init() {
        ConfigDatabase config = ((FileService) core.getServiceHandler().getService(FileService.class)).getConfigDatabase();

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new PlayerDataCodec(), new ItemPairCodec(), new IslandPlayerCodec(),
                        new PotionEffectsCodec(), new WorldPositionCodec(), new UpgradeCodec(), new BannedIslandPlayerCodec()),
                CodecRegistries.fromProviders(com.mongodb.MongoClient.getDefaultCodecRegistry())
        );

        datastore = Morphia.createDatastore(MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString("mongodb://" + config.getString(ConfigDatabase.DATABASE_ADDRESS)))
                        .codecRegistry(codecRegistry)
                        .credential(
                                MongoCredential.createCredential(config.getString(ConfigDatabase.DATABASE_USER),
                                        "admin",
                                        config.getString(ConfigDatabase.DATABASE_PASSWORD).toCharArray()))
                        .build()),
                config.getString(ConfigDatabase.DATABASE_COLLECTION_NAME),
                MapperOptions.builder()
                        .classLoader(OneblockCore.class.getClassLoader())
                        .mapSubPackages(true)
                        .build());

        datastore.getMapper().mapPackage("me.buzz.coralmc.oneblockcore");
        datastore.ensureIndexes();
    }

    @Override
    public void stop() {
    }

}
