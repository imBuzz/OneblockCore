package me.buzz.coralmc.oneblockcore.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.ytnoos.dictation.api.redis.DictationSubscriber;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.commands.impl.IslandBase;
import me.buzz.coralmc.oneblockcore.global.PlayerJoinListener;
import me.buzz.coralmc.oneblockcore.players.User;
import me.buzz.coralmc.oneblockcore.server.redis.messages.ChatMessage;
import me.buzz.coralmc.oneblockcore.server.redis.messages.IslandMessage;
import me.buzz.coralmc.oneblockcore.server.redis.messages.ServerMessage;
import me.buzz.coralmc.oneblockcore.server.redis.subscribers.ChatSubscriber;
import me.buzz.coralmc.oneblockcore.server.redis.subscribers.IslandSubscriber;
import me.buzz.coralmc.oneblockcore.server.redis.subscribers.ServerSubscriber;
import me.buzz.coralmc.oneblockcore.structures.maps.ConcurrentStringMap;
import me.buzz.coralmc.oneblockcore.utils.Executor;
import me.buzz.coralmc.oneblockcore.workload.WorkloadThread;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class Game {

    protected final OneblockCore core = OneblockCore.get();
    @Getter
    protected final ConcurrentStringMap<User> users = new ConcurrentStringMap<>();
    private final Map<String, Listener> listeners = Maps.newHashMap();
    private final List<DictationSubscriber> subscribers = Lists.newArrayList();
    @Getter
    private final WorkloadThread workloadThread = new WorkloadThread();

    public void init() {
        registerSubscriber(new ChatSubscriber(), ChatMessage.CHANNEL);
        registerSubscriber(new ServerSubscriber(), ServerMessage.CHANNEL);
        registerSubscriber(new IslandSubscriber(), IslandMessage.CHANNEL);

        registerGlobalListeners();
        core.addCommand(new IslandBase());

        Executor.timer(workloadThread, 5L);
    }

    public void stop() {
        unRegisterSubscribers();
    }

    public abstract void registerCommands();

    public abstract void registerListeners();

    public CompletableFuture<User> getUser(String name) {
        if (users.containsKey(name)) {
            return CompletableFuture.completedFuture(users.get(name));
        } else {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return users.get(name);
            });
        }
    }

    public void registerSubscriber(JedisPubSub listener, String channel) {
        subscribers.add(core.getDictation().getRedisManager().subscribe(listener, channel));
    }

    public void unRegisterSubscribers() {
        subscribers.forEach(thread -> core.getDictation().getRedisManager().unsubscribe(thread));
    }

    public void registerGlobalListeners() {
        registerListener("global_join", new PlayerJoinListener());
    }

    protected void registerListener(String key, Listener listener) {
        listeners.put(key, listener);
        Bukkit.getPluginManager().registerEvents(listener, core);
    }

    public Listener getListenerInstance(String key) {
        if (!listeners.containsKey(key)) return null;
        return listeners.get(key);
    }

}
