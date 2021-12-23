package me.buzz.coralmc.oneblockcore.global;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.database.queues.impl.LoadUserQueue;
import me.buzz.coralmc.oneblockcore.database.queues.impl.PlayerDataQueue;
import me.buzz.coralmc.oneblockcore.files.FileService;
import me.buzz.coralmc.oneblockcore.files.impl.ConfigDatabase;
import me.buzz.coralmc.oneblockcore.players.PlayerData;
import me.buzz.coralmc.oneblockcore.players.User;
import me.buzz.coralmc.oneblockcore.server.redis.RedisService;
import me.buzz.coralmc.oneblockcore.server.redis.messages.ChatMessage;
import me.buzz.coralmc.oneblockcore.structures.actions.OperationAction;
import me.buzz.coralmc.oneblockcore.utils.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;

public class PlayerJoinListener implements Listener {
    public final static String ISLAND_CHAT_PREFIX = "&b&lISLAND";

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
            } catch (Exception e) {
                e.printStackTrace();
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage("An error has occurred while loading inventories!");
            }
        });
        ((LoadUserQueue) core.getServiceHandler().getQueue(LoadUserQueue.class)).add(new OperationAction<>(event.getName(), OperationAction.OperationType.LOADING));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        PlayerData playerData = playerCache.getIfPresent(event.getPlayer().getName());
        if (playerData == null) return;

        playerData.load(event.getPlayer());
        redisService.publishPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent event) {
        redisService.publishPlayerData(event.getPlayer());
        redisService.removePlayer(event.getPlayer());

        ((LoadUserQueue) core.getServiceHandler().getQueue(LoadUserQueue.class))
                .add(new OperationAction<>(event.getPlayer().getName(), OperationAction.OperationType.SAVING));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void chatEvent(AsyncPlayerChatEvent event) {
        event.setFormat(((FileService) core.getServiceHandler().getService(FileService.class)).getConfigDatabase().getString(ConfigDatabase.CHAT_FORMAT));

        Player player = event.getPlayer();
        User user = core.getGame().getUsers().get(event.getPlayer().getName());

        String chatMessage = Strings.translate(event.getFormat().replace("%1$s", player.getDisplayName())).replace("%2$s", event.getMessage());

        core.getDictation().getCommandManager()
                .executeCommand(new ChatMessage(user.isTalkingInIsland(), (user.isTalkingInIsland() ? ISLAND_CHAT_PREFIX : "") + core.getServerInstance().getServerID(), chatMessage));
    }


}
