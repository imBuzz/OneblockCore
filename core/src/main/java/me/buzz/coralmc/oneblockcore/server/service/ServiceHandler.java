package me.buzz.coralmc.oneblockcore.server.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.database.DataService;
import me.buzz.coralmc.oneblockcore.database.queues.AbstractQueue;
import me.buzz.coralmc.oneblockcore.database.queues.impl.LoadUserQueue;
import me.buzz.coralmc.oneblockcore.database.queues.impl.PlayerDataQueue;
import me.buzz.coralmc.oneblockcore.files.FileService;
import me.buzz.coralmc.oneblockcore.server.redis.RedisService;

import java.util.List;
import java.util.Map;

public class ServiceHandler {
    private final static List<Class<? extends Service>> BASIC_SERVICES = ImmutableList.of(FileService.class, DataService.class, RedisService.class);
    private final static List<Class<? extends AbstractQueue<?>>> BASIC_QUEUE = ImmutableList.of(PlayerDataQueue.class, LoadUserQueue.class);

    private final OneblockCore core = OneblockCore.get();
    private final Map<Class<? extends Service>, Service> services = Maps.newHashMap();
    private final Map<Class<? extends AbstractQueue<?>>, AbstractQueue<?>> queues = Maps.newHashMap();

    public boolean init() {
        try {
            for (Class<? extends Service> basicService : BASIC_SERVICES) addNewService(basicService, false);
            for (Class<? extends AbstractQueue<?>> basicQueue : BASIC_QUEUE) addNewQueue(basicQueue, true);

            core.getLogger().info("Loaded: " + services.size() + " services and " + queues.size() + " queues by default");

        } catch (Exception e) {
            e.printStackTrace();
            core.getLogger().severe("Unable to init ServiceHandler stopping...");
            return false;
        }

        return true;
    }

    public void stop() {
        for (AbstractQueue<?> value : queues.values()) value.stop();
        for (Service value : services.values()) value.stop();
    }

    public void addNewService(Class<? extends Service> clazz, boolean start) {
        try {
            Service service = clazz.getConstructor().newInstance();
            if (start) service.init();
            services.put(clazz, service);
            core.getLogger().info("Registered a new Service: " + clazz.getName() + " enabled: " + start);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addNewQueue(Class<? extends AbstractQueue<?>> clazz, boolean start){
        try {
            AbstractQueue<?> queue = clazz.getConstructor().newInstance();
            if (start) queue.start();
            queues.put(clazz, queue);
            core.getLogger().info("Registered a new Queue: " + clazz.getName() + " enabled: " + start);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Service getService(Class<? extends Service> clazz){
        return services.get(clazz);
    }
    public AbstractQueue<?> getQueue(Class<? extends AbstractQueue<?>> clazz){
        return queues.get(clazz);
    }

}
