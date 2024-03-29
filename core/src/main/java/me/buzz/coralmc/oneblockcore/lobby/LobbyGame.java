package me.buzz.coralmc.oneblockcore.lobby;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.database.DataService;
import me.buzz.coralmc.oneblockcore.game.Game;
import me.buzz.coralmc.oneblockcore.players.PlayerData;
import me.buzz.coralmc.oneblockcore.server.redis.RedisService;
import me.buzz.coralmc.oneblockcore.utils.Executor;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LobbyGame extends Game {
    private final DataService dataService = (DataService) core.getServiceHandler().getService(DataService.class);

    @Override
    public void init() {
        super.init();

        enableAutoSave();
    }

    @Override
    public void stop() {
        super.stop();

        Executor.data(() -> {
            core.getLogger().info("Remove everythings on Redis and save on a DB");
            long time = System.currentTimeMillis();

            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()) {
                Set<String> keys = jedis.keys(RedisService.PLAYER_DATA + "*");
                for (String key : keys) {
                    PlayerData playerData = OneblockCore.GSON.fromJson(jedis.get(key), PlayerData.class);
                    dataService.getDatastore().save(playerData);
                    jedis.del(key);
                }

                core.getLogger().info("Saved: " + keys.size() + " players inventories in " + (System.currentTimeMillis() - time) + "ms");
            }
        });
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
