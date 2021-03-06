package com.qworldr.mmorpg.thread;


import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HashDispatcherThreadPool  extends DispatcherExecutor{

    private static HashDispatcherThreadPool hashDispatcherThreadPool;
    @PostConstruct
    public  void init(){
        hashDispatcherThreadPool=this;
    }
    public static DispatcherExecutor getGlobalDispatcherExecutor(){
        return hashDispatcherThreadPool;
    }
    private ScheduledExecutorService[] threadPool;
    public HashDispatcherThreadPool(int threadSize) {
        threadPool=new ScheduledExecutorService[threadSize];
        for(int i=0;i<threadSize;i++){
            threadPool[i]=Executors.newSingleThreadScheduledExecutor();
        }
    }


    public Future submit(DispatcherTask hashDispatcherTask){
        ScheduledExecutorService scheduledExecutorService = getScheduledExecutorService(hashDispatcherTask.getDispatchCode());
        return scheduledExecutorService.submit(hashDispatcherTask);
    }


    public Future scheduleAtFixedRate(DispatcherTask hashDispatcherTask,long initialDelay,long period,TimeUnit unit){
        ScheduledExecutorService scheduledExecutorService = getScheduledExecutorService(hashDispatcherTask.getDispatchCode());
        return scheduledExecutorService.scheduleAtFixedRate(hashDispatcherTask,initialDelay,period, unit);
    }

    public Future scheduleWithFixedDelay(DispatcherTask hashDispatcherTask,long initialDelay,long delay,TimeUnit unit){
        ScheduledExecutorService scheduledExecutorService = getScheduledExecutorService(hashDispatcherTask.getDispatchCode());
        return scheduledExecutorService.scheduleWithFixedDelay(hashDispatcherTask,initialDelay,delay, unit);
    }

    protected ScheduledExecutorService getScheduledExecutorService(int dispathCode) {
        return threadPool[dispathCode % threadPool.length];
    }





}
