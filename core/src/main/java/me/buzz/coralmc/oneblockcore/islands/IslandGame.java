package me.buzz.coralmc.oneblockcore.islands;

import com.google.common.collect.Maps;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.database.DataService;
import me.buzz.coralmc.oneblockcore.game.Game;
import me.buzz.coralmc.oneblockcore.islands.listeners.GlobalIslandListener;
import me.buzz.coralmc.oneblockcore.islands.workloads.TeleportPlayersOnJoin;
import me.buzz.coralmc.oneblockcore.utils.Executor;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.ref.WeakReference;
import java.util.Map;

public class IslandGame extends Game {
    @Getter
    private final Map<String, WeakReference<World>> islandWorld = Maps.newHashMap();

    @Override
    public void init() {
        super.init();
        core.getServiceHandler().addNewQueue(TeleportPlayersOnJoin.class, true);
        registerListeners();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void registerCommands() {

    }

    @Override
    public void registerListeners() {
        registerListener("island_global_listener", new GlobalIslandListener());
    }

    public void loadNewIsland(String UUID) {
        if (!Bukkit.isPrimaryThread()) Executor.sync(() -> loadNewIsland(UUID), 1L);
        else {
            try {
                SlimePlugin slimePlugin = core.getSlimePlugin();
                core.getLogger().info("Primary Thread: " + Bukkit.isPrimaryThread());

                SlimeWorld islandWorld;
                try {
                    islandWorld = core.getSlimePlugin().loadWorld(core.getSlimePlugin().getLoader("mongodb"), DataService.ISLAND_WORLD_TAG + UUID, false, getWorldProperty());
                } catch (UnknownWorldException e) {
                    islandWorld = core.getSlimePlugin()
                            .createEmptyWorld(core.getSlimePlugin().getLoader("mongodb"), DataService.ISLAND_WORLD_TAG + UUID, false, getWorldProperty());
                }

                slimePlugin.generateWorld(islandWorld);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SlimePropertyMap getWorldProperty() {
        SlimePropertyMap property = new SlimePropertyMap();
        property.setString(SlimeProperties.WORLD_TYPE, "flat");
        property.setBoolean(SlimeProperties.ALLOW_ANIMALS, true);
        property.setBoolean(SlimeProperties.ALLOW_MONSTERS, true);
        property.setString(SlimeProperties.ENVIRONMENT, "normal");
        property.setString(SlimeProperties.DIFFICULTY, "normal");
        property.setBoolean(SlimeProperties.PVP, false);
        return property;
    }

}
