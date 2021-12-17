package me.buzz.coralmc.oneblockcore.common.database.queues.impl;

import dev.morphia.query.experimental.filters.Filters;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.common.database.DataService;
import me.buzz.coralmc.oneblockcore.common.database.queues.AbstractQueue;
import me.buzz.coralmc.oneblockcore.common.global.PlayerJoinListener;
import me.buzz.coralmc.oneblockcore.common.players.PlayerData;

public class PlayerDataQueue extends AbstractQueue<String> {
    private final OneblockCore core = OneblockCore.get();
    private final DataService dataService = (DataService) core.getServiceHandler().getService(DataService.class);

    public PlayerDataQueue() {
        super("PlayerData Loader Thread");
    }

    @Override
    public void compute(String player) {
        PlayerData playerData = dataService.getDatastore().find(PlayerData.class)
                .filter(Filters.eq("_id", player)).first();

        if (playerData == null) return;
        ((PlayerJoinListener) core.getGame().getListenerInstance("global_join")).getPlayerCache().put(player, playerData);
    }

}
