package me.buzz.coralmc.oneblockcore.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Executor {

    private static final ExecutorService dataService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Oneblock Database Thread").build());
    private static final OneblockCore plugin = OneblockCore.get();
    private static boolean shutdown = false, dataShutdown = false;

    public static void sync(Runnable runnable){
        if(shutdown)
            return;

        if(!Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTask(plugin, runnable);
        else
            runnable.run();
    }

    public static void sync(Runnable runnable, long delayedTime){
        if(shutdown)
            return;

        Bukkit.getScheduler().runTaskLater(plugin, runnable, delayedTime);
    }

    public static void async(Runnable runnable){
        if(shutdown)
            return;

        if(!Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static void async(Runnable runnable, long delay){
        if (shutdown)
            return;

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    public static BukkitTask timer(Runnable runnable, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, 0L, period);
    }

    public static BukkitTask asyncTimer(Runnable runnable, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, 0L, period);
    }

    public static void data(Runnable runnable) {
        if (dataShutdown)
            return;

        try {
            dataService.execute(runnable);
        } catch (Exception ignored) {
        }
    }

    public static void stop() {
        shutdown = true;
    }

    public static void stopData(){
        try{
            dataShutdown = true;
            plugin.getLogger().info("Shutting down database executor");
            shutdownAndAwaitTermination();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private static void shutdownAndAwaitTermination() {
        dataService.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!dataService.awaitTermination(60, TimeUnit.SECONDS)) {
                dataService.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!dataService.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            dataService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
