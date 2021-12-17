package me.buzz.coralmc.oneblockcore.lobby;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.common.database.DataService;
import me.buzz.coralmc.oneblockcore.common.game.Game;
import me.buzz.coralmc.oneblockcore.common.players.PlayerData;
import me.buzz.coralmc.oneblockcore.common.server.redis.RedisService;
import me.buzz.coralmc.oneblockcore.common.utils.Executor;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LobbyGame extends Game {
    private final DataService dataService = (DataService) core.getServiceHandler().getService(DataService.class);

    @Override
    public void init() {
        enableAutoSave();
        registerGlobalListeners();


    }

    @Override
    public void stop() {

    }

    @Override
    public void registerCommands() {

    }

    @Override
    public void registerListeners() {

    }

    private void enableAutoSave(){
        Executor.timer(() -> Executor.data(() -> {
            core.getLogger().info("Starting auto-save for players inventories...");
            long time = System.currentTimeMillis();

            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()){
                Set<String> keys = jedis.keys(RedisService.PLAYER_DATA + "*");
                for (String key : keys) {
                    PlayerData playerData = OneblockCore.GSON.fromJson(jedis.get(key), PlayerData.class);
                    dataService.getDatastore().save(playerData);
                    jedis.del(key);
                }

                core.getLogger().info("Saved: " + keys.size() + " players inventories in " + (System.currentTimeMillis() - time) + "ms");
            }
        }), TimeUnit.MINUTES.toSeconds(1) * 20L);
    }

}
