package me.buzz.coralmc.oneblockcore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grinderwolf.swm.api.SlimePlugin;
import fr.minuskube.inv.InventoryManager;
import it.ytnoos.dictation.api.Dictation;
import it.ytnoos.dictation.api.bukkit.DictationBukkit;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.api.OneblockPlugin;
import me.buzz.coralmc.oneblockcore.commands.OneblockCommand;
import me.buzz.coralmc.oneblockcore.database.DataService;
import me.buzz.coralmc.oneblockcore.files.FileService;
import me.buzz.coralmc.oneblockcore.game.Game;
import me.buzz.coralmc.oneblockcore.server.ServerInstance;
import me.buzz.coralmc.oneblockcore.server.redis.RedisService;
import me.buzz.coralmc.oneblockcore.server.redis.adapters.ItemPair;
import me.buzz.coralmc.oneblockcore.server.redis.adapters.ItemPairAdapter;
import me.buzz.coralmc.oneblockcore.server.redis.adapters.LocationAdapter;
import me.buzz.coralmc.oneblockcore.server.redis.adapters.PotionEffectAdapter;
import me.buzz.coralmc.oneblockcore.server.service.ServiceHandler;
import me.buzz.coralmc.oneblockcore.utils.Executor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;

public final class OneblockCore extends JavaPlugin implements OneblockPlugin {

    private static OneblockCore oneblockCore;

    public static OneblockCore get() {
        return oneblockCore;
    }

    public static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeAdapter(ItemPair.class, new ItemPairAdapter())
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .create();

    @Getter
    private ServerInstance serverInstance;
    @Getter
    private Dictation dictation;
    @Getter
    private ServiceHandler serviceHandler;
    @Getter
    private SlimePlugin slimePlugin;
    @Getter
    private InventoryManager inventoryManager;
    @Getter
    private Game game;

    @Override
    public void onEnable() {
        if (!checkDependencies()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        registerDependencies();
        oneblockCore = this;

        serviceHandler = new ServiceHandler();
        if (!serviceHandler.init()){
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        serviceHandler.getService(FileService.class).init();
        serviceHandler.getService(DataService.class).init();

        serverInstance = new ServerInstance((FileService) serviceHandler.getService(FileService.class));

        serviceHandler.getService(RedisService.class).init();

        game = serverInstance.getType().getGame().get();
        game.init();

        inventoryManager = new InventoryManager(this);
        inventoryManager.init();

        getCommand("servers").setExecutor(this);
        getCommand("saveme").setExecutor(this);
    }

    @Override
    public void onDisable() {
        game.stop();

        Executor.stopData();
        getLogger().info("Stopping executor...");
        Executor.stop();

        serviceHandler.stop();
    }

    private boolean checkDependencies() {
        for (String dependency : getDescription().getDepend()) {
            if (!Bukkit.getPluginManager().isPluginEnabled(dependency)) {
                getLogger().severe(dependency + " not found! OneblockCore stopped!");
                return false;
            }
        }
        return true;
    }

    private void registerDependencies() {
        RegisteredServiceProvider<DictationBukkit> provider = Bukkit.getServicesManager().getRegistration(DictationBukkit.class);
        dictation = provider.getProvider();
        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
    }

    public final void addCommand(OneblockCommand... commands) {
        CommandMap map = null;
        Field field;

        try {
            field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            map = (CommandMap) field.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (OneblockCommand command : commands) {
            assert map != null;
            if (map.getCommand(command.getName()) == null) {
                map.register(command.getName(), command);
            }
        }
    }

}
