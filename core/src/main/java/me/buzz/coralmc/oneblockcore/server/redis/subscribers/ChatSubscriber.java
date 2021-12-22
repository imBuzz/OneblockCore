package me.buzz.coralmc.oneblockcore.server.redis.subscribers;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.server.redis.messages.ChatMessage;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

public class ChatSubscriber extends JedisPubSub {
    private final OneblockCore core = OneblockCore.get();

    @Override
    public void onMessage(String channel, String message) {
        ChatMessage chatMessage = OneblockCore.GSON.fromJson(message, ChatMessage.class);
        if (core.getServerInstance().getServerID().equalsIgnoreCase(chatMessage.getChatMessage()[0])) return;
        Bukkit.broadcastMessage(chatMessage.getChatMessage()[1]);
    }

}
