package me.buzz.coralmc.oneblockcore.common.database.codec;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.common.players.PlayerData;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class PlayerDataCodec implements Codec<PlayerData> {

    @Override
    public PlayerData decode(BsonReader reader, DecoderContext context) {
        return OneblockCore.GSON.fromJson(reader.readString(), PlayerData.class);
    }

    @Override
    public void encode(BsonWriter writer, PlayerData value, EncoderContext encoderContext) {
        writer.writeString(OneblockCore.GSON.toJson(value));
    }

    @Override
    public Class<PlayerData> getEncoderClass() {
        return PlayerData.class;
    }
}
