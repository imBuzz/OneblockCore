package me.buzz.coralmc.oneblockcore.server.redis;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.ytnoos.dictation.api.server.BukkitServer;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.islands.object.Island;
import me.buzz.coralmc.oneblockcore.players.PlayerData;
import me.buzz.coralmc.oneblockcore.server.ServerInstance;
import me.buzz.coralmc.oneblockcore.server.ServerType;
import me.buzz.coralmc.oneblockcore.server.service.Service;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisService implements Service {
    public static final String SERVER_PREFIX = "oneblock_gameserver:";
    public static final String PLAYER_LOCATION = "oneblock_playerlocation:";
    public static final String ISLAND_PREFIX = "oneblock_island:";
    public static final String PLAYER_DATA = "oneblock_playerdata:";

    private final OneblockCore core = OneblockCore.get();
    @Getter
    private final ExecutorService redisThread = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Oneblock Redis Thread").build());

    @Override
    public void init() {
        try (Jedis jedis = core.getDictation().getRedisManager().getJedis()) {
            ServerInstance serverInstance = core.getServerInstance();
            jedis.set(SERVER_PREFIX + serverInstance.getServerID(), OneblockCore.GSON.toJson(serverInstance));
        }
    }

    @Override
    public void stop() {
        try (Jedis jedis = core.getDictation().getRedisManager().getJedis()) {
            ServerInstance serverInstance = core.getServerInstance();
            jedis.del(SERVER_PREFIX + serverInstance.getServerID());
        }

        redisThread.shutdownNow();
    }

    public CompletableFuture<Island> getIslandOnRedis(String UUID) {
        return CompletableFuture.supplyAsync(() -> {


            return null;
        }, redisThread);
    }

    public void publishIslandLocation(Island island) {
        redisThread.execute(() -> {
            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()) {
                jedis.set(ISLAND_PREFIX + island.getUuid(), core.getServerInstance().getServerID());
            }
        });
    }

    public void publishIsland(Island island) {
        redisThread.execute(() -> {
            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()) {
                jedis.set(ISLAND_PREFIX + island.getUuid(), OneblockCore.GSON.toJson(island));
            }
        });
    }

    public CompletableFuture<ServerInstance> lookingForServer(ServerType type, String UUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()) {
                ServerInstance returnServer = null;

                if (UUID.isEmpty()) {
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
                        if (returnServer == null || newThreshold > threshold) {
                            returnServer = server;
                            threshold = newThreshold;
                        }
                    }
                } else {
                    if (jedis.exists(ISLAND_PREFIX + UUID)) {
                        String serverID = jedis.get(ISLAND_PREFIX + UUID);
                        returnServer = OneblockCore.GSON.fromJson(jedis.get(SERVER_PREFIX + serverID), ServerInstance.class);
                        ;
                    } else {
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
                            if (returnServer == null || newThreshold < threshold) {
                                returnServer = server;
                                threshold = newThreshold;
                            }
                        }
                    }

                }

                return returnServer;
            }
        }, redisThread);
    }

    public CompletableFuture<PlayerData> getPlayersData(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()) {
                if (!jedis.exists(PLAYER_DATA + playerName)) return null;
                return OneblockCore.GSON.fromJson(jedis.get(PLAYER_DATA + playerName), PlayerData.class);
            }
        }, redisThread);
    }

    public void publishPlayer(Player player) {
        redisThread.execute(() -> {
            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()) {
                jedis.set(PLAYER_LOCATION + player.getName(), core.getServerInstance().getServerID());
            }
        });
    }

    public void removePlayer(Player player) {
        redisThread.execute(() -> {
            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()) {
                jedis.del(PLAYER_LOCATION + player.getName());
            }
        });
    }

    public void publishPlayerData(Player player) {
        redisThread.execute(() -> {
            try (Jedis jedis = core.getDictation().getRedisManager().getJedis()) {
                //TODO: jedis.pexpire(PLAYER_DATA + player.getName(), TimeUnit.MINUTES.toMillis(10));
                jedis.set(PLAYER_DATA + player.getName(), OneblockCore.GSON.toJson(PlayerData.of(player)));
            }
        });
    }

    private double getThreshold(ServerInstance server) {
        Optional<BukkitServer> optionalBukkitServer = core.getDictation().getServerManager().getServer(server.getServerID());
        if (optionalBukkitServer.isEmpty()) return Double.MAX_VALUE;

        BukkitServer bukkitServer = optionalBukkitServer.get();
        return bukkitServer.getOnline();
    }


}
