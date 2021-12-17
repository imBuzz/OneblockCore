package me.buzz.coralmc.oneblockcore.common.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.common.database.codec.ItemPairCodec;
import me.buzz.coralmc.oneblockcore.common.database.codec.PlayerDataCodec;
import me.buzz.coralmc.oneblockcore.common.database.codec.PotionEffectsCodec;
import me.buzz.coralmc.oneblockcore.common.files.FileService;
import me.buzz.coralmc.oneblockcore.common.files.impl.ConfigDatabase;
import me.buzz.coralmc.oneblockcore.common.server.service.Service;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public class DataService implements Service {

    private final OneblockCore core = OneblockCore.get();
    @Getter private Datastore datastore;

    @Override
    public void init() {
        ConfigDatabase config = ((FileService) core.getServiceHandler().getService(FileService.class)).getConfigDatabase();

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new PlayerDataCodec(), new ItemPairCodec(), new PotionEffectsCodec()),
                CodecRegistries.fromProviders(com.mongodb.MongoClient.getDefaultCodecRegistry())
        );


        datastore = Morphia.createDatastore(MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString("mongodb://" + config.getString(ConfigDatabase.DATABASE_ADDRESS)))
                        .codecRegistry(codecRegistry)
                        /*.credential(
                                MongoCredential.createCredential(config.getString(ConfigDatabase.DATABASE_USER),
                                        config.getString(ConfigDatabase.DATABASE_COLLECTION_NAME),
                                        config.getString(ConfigDatabase.DATABASE_PASSWORD).toCharArray()))*/
                        .build()),
                config.getString(ConfigDatabase.DATABASE_COLLECTION_NAME));
        datastore.ensureIndexes();
    }

    @Override
    public void stop() {
        try {
            datastore.getSession().close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



}
