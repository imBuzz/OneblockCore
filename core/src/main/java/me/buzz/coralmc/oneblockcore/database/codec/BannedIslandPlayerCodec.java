package me.buzz.coralmc.oneblockcore.database.codec;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.islands.object.player.internal.BannedIslandPlayer;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BannedIslandPlayerCodec implements Codec<BannedIslandPlayer> {

    @Override
    public BannedIslandPlayer decode(BsonReader reader, DecoderContext context) {
        return OneblockCore.GSON.fromJson(reader.readString(), BannedIslandPlayer.class);
    }

    @Override
    public void encode(BsonWriter writer, BannedIslandPlayer value, EncoderContext context) {
        writer.writeString(OneblockCore.GSON.toJson(value));
    }

    @Override
    public Class<BannedIslandPlayer> getEncoderClass() {
        return BannedIslandPlayer.class;
    }
}
