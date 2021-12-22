package me.buzz.coralmc.oneblockcore.database.codec;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.islands.object.upgrades.AbstractUpgrade;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class UpgradeCodec implements Codec<AbstractUpgrade> {

    @Override
    public void encode(BsonWriter writer, AbstractUpgrade value, EncoderContext context) {
        writer.writeString(OneblockCore.GSON.toJson(value));
    }

    @Override
    public AbstractUpgrade decode(BsonReader reader, DecoderContext context) {
        return OneblockCore.GSON.fromJson(reader.readString(), AbstractUpgrade.class);
    }

    @Override
    public Class<AbstractUpgrade> getEncoderClass() {
        return AbstractUpgrade.class;
    }
}
