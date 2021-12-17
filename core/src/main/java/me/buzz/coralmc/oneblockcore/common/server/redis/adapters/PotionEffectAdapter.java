package me.buzz.coralmc.oneblockcore.common.server.redis.adapters;

import com.google.gson.*;
import me.buzz.coralmc.oneblockcore.common.structures.BukkitSerialization;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Type;

public class PotionEffectAdapter implements JsonSerializer<PotionEffect>, JsonDeserializer<PotionEffect> {

    @Override
    public JsonElement serialize(PotionEffect effect, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("effect", context.serialize(BukkitSerialization.serializePotionEffect(effect)));
        return jsonObject;
    }

    @Override
    public PotionEffect deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement element = jsonObject.get("effect");
        if (element == null) return null;

        return BukkitSerialization.deserializePotionEffect(element.getAsString());
    }

}
