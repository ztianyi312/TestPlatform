package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author zhoutianji
 *
 */
public class SynchProcess {

    private ThreadPoolExecutor executorService;
    
    private AtomicInteger count = new AtomicInteger();
    
    public SynchProcess(ThreadPoolExecutor executorService){
        this.executorService = executorService;
    }
    
    public void run() throws Exception {
        processA();
        //System.out.println(executorService.getLargestPoolSize());
        
        //System.out.println(count.get());
    }
    
    public int getCount() {
        return count.get();
    }
    
    public Object processA() throws Exception {
        
        List<Future> list = new ArrayList<>();
        for(int i=0; i<10; i++) {
            list.add(executorService.submit(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    
                    return processB();
                }
                
            }));
        }
        
        for(Future future : list) {
            future.get();
        }
        
        return "A";
    }
    
    public Object processB() throws Exception {
        
        List<Future> list = new ArrayList<>();
        for(int i=0; i<10; i++) {
            list.add(executorService.submit(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    
                    
                    return processC();

                }
                
            }));
        }
        
        for(Future future : list) {
            future.get();
        }
        
        return "B";
    }
    
    public Object processC() throws Exception {
        
        List<Future> list = new ArrayList<>();
        for(int i=0; i<10; i++) {
            list.add(executorService.submit(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    count.incrementAndGet();
                    Thread.sleep(2);
                    return 1;
                }
                
            }));
        }
        
        for(Future future : list) {
            future.get();
        }
        
        return "C";
    }
}
