package me.buzz.coralmc.oneblockcore.server.redis.messages;

import it.ytnoos.dictation.api.redis.RedisMessage;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.OneblockCore;

public class ChatMessage implements RedisMessage {
    public static final String CHANNEL = "oneblock_chat_message";
    @Getter
    private final String[] chatMessage;
    @Getter
    private final boolean islandMessage;

    public ChatMessage(boolean isIsland, String... messages) {
        this.islandMessage = isIsland;
        this.chatMessage = messages;
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }

    @Override
    public String getMessage() {
        return OneblockCore.GSON.toJson(this);
    }

}
