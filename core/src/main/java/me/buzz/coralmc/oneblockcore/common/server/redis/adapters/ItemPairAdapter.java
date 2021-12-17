package me.buzz.coralmc.oneblockcore.common.server.redis.adapters;

import com.google.gson.*;
import lombok.SneakyThrows;
import me.buzz.coralmc.oneblockcore.common.structures.BukkitSerialization;

import java.lang.reflect.Type;

public class ItemPairAdapter implements JsonSerializer<ItemPair>, JsonDeserializer<ItemPair> {

    @Override
    public JsonElement serialize(ItemPair pair, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("slot", context.serialize(pair.getSlot()));
        jsonObject.add("item", context.serialize(BukkitSerialization.itemStackToBase64(pair.getItem())));

        return jsonObject;
    }

    @SneakyThrows
    @Override
    public ItemPair deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return new ItemPair(jsonObject.get("slot").getAsInt(),
                BukkitSerialization.itemStackFromBase64(jsonObject.get("item").getAsString()));
    }
}
