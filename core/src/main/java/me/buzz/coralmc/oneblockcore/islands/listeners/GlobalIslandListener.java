package me.buzz.coralmc.oneblockcore.islands.listeners;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.database.DataService;
import me.buzz.coralmc.oneblockcore.events.LoadedUserEvent;
import me.buzz.coralmc.oneblockcore.islands.IslandGame;
import me.buzz.coralmc.oneblockcore.islands.workloads.TeleportPlayersOnJoin;
import me.buzz.coralmc.oneblockcore.server.ServerType;
import me.buzz.coralmc.oneblockcore.server.redis.RedisService;
import me.buzz.coralmc.oneblockcore.server.redis.messages.ServerMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.lang.ref.WeakReference;

public class GlobalIslandListener implements Listener {
    private final OneblockCore core = OneblockCore.get();
    private final IslandGame islandGame = (IslandGame) core.getGame();


    @EventHandler
    public void playerJoinEvent(LoadedUserEvent event) {
        ((TeleportPlayersOnJoin) core.getServiceHandler().getQueue(TeleportPlayersOnJoin.class)).getQueue().add(event.getUser());
    }

    @EventHandler
    public void worldPostLoad(WorldLoadEvent event) {
        if (!event.getWorld().getName().startsWith(DataService.ISLAND_WORLD_TAG)) return;
        islandGame.getIslandWorld().put(event.getWorld().getName(), new WeakReference<>(event.getWorld()));

        ((RedisService) core.getServiceHandler().getService(RedisService.class))
                .lookingForServer(ServerType.LOBBY, "").whenComplete((value, ex) -> core.getDictation().getCommandManager().executeCommand(
                new ServerMessage(value.getServerID(),
                        ServerMessage.ServerMessageType.ISLAND_SUCCESS,
                        event.getWorld().getName().split("_")[1])));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        World world = event.getPlayer().getWorld();
        if (world.getPlayers().size() == 0) {
            Bukkit.unloadWorld(world, true);
        }
    }


}
