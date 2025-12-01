package dev.cxd.dominium.utils;

import net.minecraft.server.MinecraftServer;

import java.util.*;

public class DelayedTaskScheduler {
    private static final Map<MinecraftServer, List<ScheduledTask>> tasks = new HashMap<>();

    public static void schedule(MinecraftServer server, int delayTicks, Runnable task) {
        tasks.computeIfAbsent(server, s -> new ArrayList<>())
                .add(new ScheduledTask(delayTicks, task));
    }

    public static void tick(MinecraftServer server) {
        List<ScheduledTask> serverTasks = tasks.get(server);
        if (serverTasks == null) return;

        Iterator<ScheduledTask> iterator = serverTasks.iterator();
        while (iterator.hasNext()) {
            ScheduledTask t = iterator.next();
            if (--t.ticksRemaining <= 0) {
                t.task.run();
                iterator.remove();
            }
        }

        if (serverTasks.isEmpty()) {
            tasks.remove(server);
        }
    }

    private static class ScheduledTask {
        int ticksRemaining;
        final Runnable task;

        ScheduledTask(int ticks, Runnable task) {
            this.ticksRemaining = ticks;
            this.task = task;
        }
    }
}

