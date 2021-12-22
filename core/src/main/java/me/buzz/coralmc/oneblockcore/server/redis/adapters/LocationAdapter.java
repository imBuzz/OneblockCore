package me.buzz.coralmc.oneblockcore.server.redis.adapters;

import com.google.gson.*;
import me.buzz.coralmc.oneblockcore.structures.BukkitSerialization;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("location", context.serialize(BukkitSerialization.serializeLocation(location)));

        return jsonObject;
    }

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return BukkitSerialization.deserializeLocation(jsonObject.get("location").getAsString());
    }
}
