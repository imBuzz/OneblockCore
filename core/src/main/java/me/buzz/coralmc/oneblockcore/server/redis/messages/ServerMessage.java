package me.buzz.coralmc.oneblockcore.server.redis.messages;

import it.ytnoos.dictation.api.redis.RedisMessage;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.OneblockCore;

import java.util.Arrays;

@Getter
public class ServerMessage implements RedisMessage {
    public static final String CHANNEL = "oneblock_server_message";

    private final String sender = OneblockCore.get().getServerInstance().getServerID();
    private final String receiver;
    private final ServerMessage.ServerMessageType type;
    private final String[] container;

    public ServerMessage(String receiver, ServerMessageType type, String... container) {
        this.receiver = receiver;
        this.type = type;
        this.container = container;
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }

    @Override
    public String getMessage() {
        return OneblockCore.GSON.toJson(this);
    }

    @Override
    public String toString() {
        return "ServerMessage{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", type=" + type +
                ", container=" + Arrays.toString(container) +
                '}';
    }

    public enum ServerMessageType {

        ISLAND_REQUEST,
        ISLAND_SUCCESS

    }


}
