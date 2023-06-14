package com.andavin.util;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class FoliaScheduledTask implements ScheduledTaskCompat {

    private final ScheduledTask task;

    private FoliaScheduledTask(ScheduledTask task) {
        this.task = task;
    }

    public static FoliaScheduledTask wrap(ScheduledTask task) {
        return new FoliaScheduledTask(task);
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }
}
