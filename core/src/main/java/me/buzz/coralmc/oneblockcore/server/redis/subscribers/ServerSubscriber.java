package me.buzz.coralmc.oneblockcore.server.redis.subscribers;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.islands.IslandGame;
import me.buzz.coralmc.oneblockcore.server.ServerType;
import me.buzz.coralmc.oneblockcore.server.redis.messages.ServerMessage;
import redis.clients.jedis.JedisPubSub;

public class ServerSubscriber extends JedisPubSub {
    private final OneblockCore core = OneblockCore.get();

    @Override
    public void onMessage(String channel, String m) {
        ServerMessage serverMessage = OneblockCore.GSON.fromJson(m, ServerMessage.class);
        if (!serverMessage.getReceiver().equals(core.getServerInstance().getServerID())) return;

        if (serverMessage.getType() == ServerMessage.ServerMessageType.ISLAND_REQUEST) {
            if (core.getServerInstance().getType() != ServerType.ISLANDS) return;
            IslandGame islandGame = ((IslandGame) core.getGame());
            islandGame.getRequestedIslands().put(serverMessage.getContainer()[1], serverMessage.getContainer()[0]);
            islandGame.requestIsland(serverMessage.getContainer()[0]);
        }
    }

}
