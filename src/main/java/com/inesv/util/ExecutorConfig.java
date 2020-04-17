package com.inesv.util;


import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorConfig {

    //创建等待队列
    public static BlockingQueue<Runnable> bqueue = new ArrayBlockingQueue<Runnable>(100);
    //创建线程池，池中保存的线程数为5，允许的最大线程数为8
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,8,50, TimeUnit.SECONDS,bqueue,new ThreadPoolExecutor.CallerRunsPolicy());

}
