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
            ((IslandGame) core.getGame()).loadNewIsland(serverMessage.getContainer()[0]);
            return;
        }

        if (serverMessage.getType() == ServerMessage.ServerMessageType.ISLAND_SUCCESS) {
            String islandUUID = serverMessage.getContainer()[0];
            core.getGame().getRequestedIslands().asMap().forEach((key, value) -> {
                if (value.equalsIgnoreCase(islandUUID)) {
                    core.getDictation().getPlayerManager().sendToServer(key, serverMessage.getSender());
                    core.getGame().getRequestedIslands().invalidate(key);
                }
            });
        }
    }

}
