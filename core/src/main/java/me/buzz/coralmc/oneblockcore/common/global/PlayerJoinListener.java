package me.buzz.coralmc.oneblockcore.common.global;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.morphia.query.experimental.filters.Filters;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.common.database.DataService;
import me.buzz.coralmc.oneblockcore.common.database.queues.impl.PlayerDataQueue;
import me.buzz.coralmc.oneblockcore.common.players.PlayerData;
import me.buzz.coralmc.oneblockcore.common.server.redis.RedisService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;

public class PlayerJoinListener implements Listener {

    private final OneblockCore core = OneblockCore.get();
    private final RedisService redisService = (RedisService) core.getServiceHandler().getService(RedisService.class);

    @Getter private final Cache<String, PlayerData> playerCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(20))
            .build();


    @EventHandler
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent event){
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        redisService.getPlayersData(event.getName()).whenComplete((value, ex) -> {
           if (ex != null) {
               ex.printStackTrace();
               return;
           }

           try {
               if (value == null) {
                   ((PlayerDataQueue) core.getServiceHandler().getQueue(PlayerDataQueue.class)).add(event.getName());
                   return;
               }

               playerCache.put(event.getName(), value);
           }
           catch (Exception e){
               e.printStackTrace();
               event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
               event.setKickMessage("An error has occurred while loading inventories!");
           }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        PlayerData playerData = playerCache.getIfPresent(event.getPlayer().getName());
        if (playerData == null) return;
        playerData.load(event.getPlayer());
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent event){
        redisService.publishPlayerData(event.getPlayer());
    }

}
