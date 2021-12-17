package me.buzz.coralmc.oneblockcore.common.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.common.files.FileService;
import me.buzz.coralmc.oneblockcore.common.files.impl.ConfigDatabase;

import java.util.Locale;

@RequiredArgsConstructor @Getter
public class ServerInstance {

    private final String serverID;
    private final ServerType type;

    public ServerInstance(FileService fileService){
        serverID = OneblockCore.get().getDictation().getServerID();
        type = ServerType.valueOf(fileService.getConfigDatabase().getString(ConfigDatabase.SERVER_TYPE).toUpperCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return "ID: " + serverID + " - Type: " + type;
    }
}
