package me.buzz.coralmc.oneblockcore.files;

import lombok.Getter;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.files.impl.ConfigDatabase;
import me.buzz.coralmc.oneblockcore.server.service.Service;

public class FileService implements Service {

    private final OneblockCore core = OneblockCore.get();
    @Getter private ConfigDatabase configDatabase;

    @Override
    public void init() {
        configDatabase = new ConfigDatabase(core, "config.yml");
    }

    @Override
    public void stop() {

    }
}
