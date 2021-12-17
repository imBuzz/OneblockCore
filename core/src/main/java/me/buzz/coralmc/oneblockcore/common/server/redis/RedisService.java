package me.buzz.coralmc.oneblockcore.common.server.redis;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.ytnoos.dictation.api.server.BukkitServer;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.common.players.PlayerData;
import me.buzz.coralmc.oneblockcore.common.server.ServerInstance;
import me.buzz.coralmc.oneblockcore.common.server.ServerType;
import me.buzz.coralmc.oneblockcore.common.server.service.Service;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisService implements Service {
    public static final String SERVER_PREFIX = "gameserver:";
    public static final String PLAYER_DATA = "playerdata:";

    private final OneblockCore core = OneblockCore.get();
    @Getter private final ExecutorService redisThread = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Oneblock Redis Thread").build());

    //gameserver:serverID, ServerInstance.class --> gameserver:*

    @Override
    public void init() {
        try (Jedis jedis = core.getDictation().getRedisManager().getJedis()){
            ServerInstance serverInstance = core.getServerInstance();
            jedis.set(SERVER_PREFIX + serverInstance.getServerID(), OneblockCore.GSON.toJson(serverInstance));
        }
    }

    @Override
    public void stop() {
        try (Jedis jedis = core.getDictation().getRedisManager().getJedis()){
            ServerInstance serverInstance = core.getServerInstance();
            jedis.del(SERVER_PREFIX + serverInstance.getServerID());
        }

        redisThread.shutdownNow();
    }

    public CompletableFuture<ServerInstance> lookingForServer(ServerType type){
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()){
                ServerInstance returnServer = null;

                List<ServerInstance> servers = Lists.newArrayList();

                for (String key : jedis.keys(SERVER_PREFIX + "*")) {
                    servers.add(OneblockCore.GSON.fromJson(jedis.get(key), ServerInstance.class));
                }

                double threshold = Double.MAX_VALUE;
                for (ServerInstance server : servers) {
                    core.getLogger().info("Server on Redis: " + server);
                    if (server.getType() != type) continue;
                    if (server.getServerID().equalsIgnoreCase(core.getServerInstance().getServerID())) continue;

                    double newThreshold = getThreshold(server);
                    if (returnServer == null || newThreshold > threshold){
                        returnServer = server;
                        threshold = newThreshold;
                    }
                }

                return returnServer;
            }
        }, redisThread);
    }

    public CompletableFuture<PlayerData> getPlayersData(String playerName){
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()){
                if (!jedis.exists(PLAYER_DATA + playerName)) return null;
                return OneblockCore.GSON.fromJson(jedis.get(PLAYER_DATA + playerName), PlayerData.class);
            }
        }, redisThread);
    }

    public void publishPlayerData(Player player){
        redisThread.execute(() -> {
            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()){
                //TODO: jedis.pexpire(PLAYER_DATA + player.getName(), TimeUnit.MINUTES.toMillis(10));
                jedis.set(PLAYER_DATA + player.getName(), OneblockCore.GSON.toJson(PlayerData.of(player)));
            }
        });
    }

    private double getThreshold(ServerInstance server) {
        Optional<BukkitServer> optionalBukkitServer = core.getDictation().getServerManager().getServer(server.getServerID());
        if (optionalBukkitServer.isEmpty()) return Double.MAX_VALUE;

        BukkitServer bukkitServer = optionalBukkitServer.get();
        if (bukkitServer.getOnline() == 0) return bukkitServer.getMax();

        return bukkitServer.getMax() / (float) bukkitServer.getOnline();
    }


}
