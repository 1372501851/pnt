package com.inesv.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {
    private ThreadUtil() {

    }
    private final static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

    public final static void execute(final MyTaskUtil task, int i, TimeUnit unit) {
        scheduledThreadPool.schedule(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                task.run();
            }
        }, i, unit);
    }

    public final static void close() {
        scheduledThreadPool.shutdown();
    }
}
