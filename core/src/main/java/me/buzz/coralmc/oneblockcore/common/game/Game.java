package me.buzz.coralmc.oneblockcore.common.game;

import com.google.common.collect.Maps;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.common.global.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Map;

public abstract class Game {
    protected final OneblockCore core = OneblockCore.get();
    private final Map<String, Listener> listeners = Maps.newHashMap();

    public abstract void init();
    public abstract void stop();

    public abstract void registerCommands();
    public abstract void registerListeners();


    public void registerGlobalListeners(){
        registerListener("global_join", new PlayerJoinListener());
    }

    protected void registerListener(String key, Listener listener){
        listeners.put(key, listener);
        Bukkit.getPluginManager().registerEvents(listener, core);
    }



    public Listener getListenerInstance(String key) {
        if (!listeners.containsKey(key)) return null;
        return listeners.get(key);
    }

}
