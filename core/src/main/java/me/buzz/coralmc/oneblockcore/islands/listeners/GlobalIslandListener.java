package me.buzz.coralmc.oneblockcore.islands.listeners;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.database.DataService;
import me.buzz.coralmc.oneblockcore.events.LoadedUserEvent;
import me.buzz.coralmc.oneblockcore.islands.IslandGame;
import me.buzz.coralmc.oneblockcore.islands.workloads.TeleportPlayersOnJoin;
import me.buzz.coralmc.oneblockcore.utils.Executor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

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
        islandGame.getIslandWorld().put(event.getWorld().getName(), event.getWorld());

        islandGame.getRequestedIslands().asMap().forEach((key, value) -> {
            if (value.equalsIgnoreCase(event.getWorld().getName().split("_")[1])) {
                core.getDictation().getPlayerManager().sendToServer(key, core.getServerInstance().getServerID());
                islandGame.getRequestedIslands().invalidate(key);
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        long time = System.currentTimeMillis();
        World world = event.getPlayer().getWorld();

        if ((world.getPlayers().size() - 1) <= 0) {
            Executor.sync(() -> {
                if (Bukkit.unloadWorld(world, true)) {
                    core.getLogger()
                            .info("World: " + world.getName() + " unloaded in " + (System.currentTimeMillis() - time) + " ms");
                }
            }, 20L);
        }
    }

    @EventHandler
    public void worldUnloadEvent(WorldUnloadEvent event) {
        islandGame.getIslandWorld().remove(event.getWorld().getName());
    }


}
