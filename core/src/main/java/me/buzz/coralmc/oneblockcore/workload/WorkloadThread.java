package me.buzz.coralmc.oneblockcore.workload;

import lombok.SneakyThrows;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentLinkedDeque;

public class WorkloadThread extends BukkitRunnable {

    private static final byte MAX_MS_PER_TICK = 2;
    private final ConcurrentLinkedDeque<IWorkload> workloadDeque = new ConcurrentLinkedDeque<>();

    public void addLoad(IWorkload workload) {
        workloadDeque.add(workload);
    }

    public void removeWorkLoad(IWorkload workload) {
        workloadDeque.remove(workload);
    }

    @Override
    @SneakyThrows
    public void run() {
        long stopTime = System.currentTimeMillis() + MAX_MS_PER_TICK;
        while (!workloadDeque.isEmpty() && System.currentTimeMillis() <= stopTime) {
            IWorkload workload = workloadDeque.poll();
            if (workload == null) continue;
            workload.compute();
        }
    }


}