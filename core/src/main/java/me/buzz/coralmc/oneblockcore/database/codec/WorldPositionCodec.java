package me.buzz.coralmc.oneblockcore.database.codec;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.structures.WorldPosition;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class WorldPositionCodec implements Codec<WorldPosition> {

    @Override
    public void encode(BsonWriter writer, WorldPosition value, EncoderContext context) {
        writer.writeString(OneblockCore.GSON.toJson(value));
    }

    @Override
    public WorldPosition decode(BsonReader reader, DecoderContext context) {
        return OneblockCore.GSON.fromJson(reader.readString(), WorldPosition.class);
    }

    @Override
    public Class<WorldPosition> getEncoderClass() {
        return WorldPosition.class;
    }
}
