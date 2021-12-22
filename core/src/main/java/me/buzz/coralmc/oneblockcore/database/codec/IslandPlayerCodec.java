package me.buzz.coralmc.oneblockcore.database.codec;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.islands.object.player.IslandPlayer;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class IslandPlayerCodec implements Codec<IslandPlayer> {

    @Override
    public IslandPlayer decode(BsonReader reader, DecoderContext context) {
        return OneblockCore.GSON.fromJson(reader.readString(), IslandPlayer.class);
    }

    @Override
    public void encode(BsonWriter writer, IslandPlayer value, EncoderContext context) {
        writer.writeString(OneblockCore.GSON.toJson(value));
    }

    @Override
    public Class<IslandPlayer> getEncoderClass() {
        return IslandPlayer.class;
    }
}
