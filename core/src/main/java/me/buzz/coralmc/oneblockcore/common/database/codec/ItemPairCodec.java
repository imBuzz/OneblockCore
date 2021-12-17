package me.buzz.coralmc.oneblockcore.common.database.codec;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.common.server.redis.adapters.ItemPair;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class ItemPairCodec implements Codec<ItemPair> {

    @Override
    public ItemPair decode(BsonReader reader, DecoderContext context) {
        return OneblockCore.GSON.fromJson(reader.readString(), ItemPair.class);
    }

    @Override
    public void encode(BsonWriter writer, ItemPair value, EncoderContext context) {
        writer.writeString(OneblockCore.GSON.toJson(value));
    }

    @Override
    public Class<ItemPair> getEncoderClass() {
        return ItemPair.class;
    }
}
