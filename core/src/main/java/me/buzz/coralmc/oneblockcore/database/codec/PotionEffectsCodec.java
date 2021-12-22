package me.buzz.coralmc.oneblockcore.database.codec;

import me.buzz.coralmc.oneblockcore.structures.BukkitSerialization;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.potion.PotionEffect;

public class PotionEffectsCodec implements Codec<PotionEffect> {

    @Override
    public void encode(BsonWriter writer, PotionEffect value, EncoderContext context) {
        writer.writeString(BukkitSerialization.serializePotionEffect(value));
    }

    @Override
    public PotionEffect decode(BsonReader reader, DecoderContext context) {
        return BukkitSerialization.deserializePotionEffect(reader.readString());
    }

    @Override
    public Class<PotionEffect> getEncoderClass() {
        return PotionEffect.class;
    }
}
