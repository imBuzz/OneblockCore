package me.buzz.coralmc.oneblockcore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.ytnoos.dictation.api.Dictation;
import it.ytnoos.dictation.api.bukkit.DictationBukkit;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.api.OneblockPlugin;
import me.buzz.coralmc.oneblockcore.common.database.DataService;
import me.buzz.coralmc.oneblockcore.common.files.FileService;
import me.buzz.coralmc.oneblockcore.common.game.Game;
import me.buzz.coralmc.oneblockcore.common.server.ServerInstance;
import me.buzz.coralmc.oneblockcore.common.server.ServerType;
import me.buzz.coralmc.oneblockcore.common.server.redis.RedisService;
import me.buzz.coralmc.oneblockcore.common.server.redis.adapters.ItemPair;
import me.buzz.coralmc.oneblockcore.common.server.redis.adapters.ItemPairAdapter;
import me.buzz.coralmc.oneblockcore.common.server.redis.adapters.PotionEffectAdapter;
import me.buzz.coralmc.oneblockcore.common.server.service.ServiceHandler;
import me.buzz.coralmc.oneblockcore.common.utils.Executor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public final class OneblockCore extends JavaPlugin implements OneblockPlugin, CommandExecutor {

    private static OneblockCore oneblockCore;
    public static OneblockCore get() {
        return oneblockCore;
    }

    public static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeAdapter(ItemPair.class, new ItemPairAdapter())
            .create();

    @Getter private ServerInstance serverInstance;
    @Getter private Dictation dictation;
    @Getter private ServiceHandler serviceHandler;

    @Getter private Game game;

    @Override
    public void onEnable() {
        if (!checkDependencies()){
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

        getCommand("servers").setExecutor(this);
        getCommand("saveme").setExecutor(this);
    }

    @Override
    public void onDisable() {
        serviceHandler.stop();
        Executor.stopData();
        getLogger().info("Stopping executor...");
        Executor.stop();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        RedisService redisService = ((RedisService) serviceHandler.getService(RedisService.class));

        if (sender instanceof Player && command.getName().equalsIgnoreCase("saveme")){
            Player player = (Player) sender;
            redisService.publishPlayerData(player);
        }

        if (command.getName().equalsIgnoreCase("servers")){
            redisService.lookingForServer(ServerType.ISLANDS).whenComplete((value, ex) -> {
                getLogger().info("Found server: " + value);
            });
        }

        return true;
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
    private void registerDependencies(){
        RegisteredServiceProvider<DictationBukkit> provider = Bukkit.getServicesManager().getRegistration(DictationBukkit.class);
        dictation = provider.getProvider();
    }

}
