package me.buzz.coralmc.oneblockcore.common.server.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.common.database.DataService;
import me.buzz.coralmc.oneblockcore.common.database.queues.AbstractQueue;
import me.buzz.coralmc.oneblockcore.common.database.queues.impl.PlayerDataQueue;
import me.buzz.coralmc.oneblockcore.common.files.FileService;
import me.buzz.coralmc.oneblockcore.common.server.redis.RedisService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class ServiceHandler {
    private final static List<Class<? extends Service>> BASIC_SERVICES = ImmutableList.of(FileService.class, DataService.class, RedisService.class);
    private final static List<Class<? extends AbstractQueue<?>>> BASIC_QUEUE = ImmutableList.of(PlayerDataQueue.class);

    private final OneblockCore core = OneblockCore.get();
    private final Map<Class<? extends Service>, Service> services = Maps.newHashMap();
    private final Map<Class<? extends AbstractQueue<?>>, AbstractQueue<?>> queues = Maps.newHashMap();

    public boolean init() {
        try {
            for (Class<? extends Service> basicService : BASIC_SERVICES) addNewService(basicService, false);
            for (Class<? extends AbstractQueue<?>> basicQueue : BASIC_QUEUE) addNewQueue(basicQueue, true);

            core.getLogger().info("Loaded: " + services.size() + " services and " + queues.size() + " queues");

        }
        catch (Exception e){
            e.printStackTrace();
            core.getLogger().severe("Unable to init ServiceHandler stopping...");
            return false;
        }

        return true;
    }

    public void addNewService(Class<? extends Service> clazz, boolean init){
        try {
            Service service = clazz.getConstructor().newInstance();
            if (init) service.init();
            services.put(clazz, service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addNewQueue(Class<? extends AbstractQueue<?>> clazz, boolean start){
        try {
            AbstractQueue<?> queue = clazz.getConstructor().newInstance();
            if (start) queue.start();
            queues.put(clazz, queue);
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

    public void stop() {
        for (AbstractQueue<?> value : queues.values()) value.stop();
        for (Service value : services.values()) value.stop();
    }

}
