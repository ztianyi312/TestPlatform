package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * 
 * @author zhoutianji
 *
 */
public class GuavaProcess {

    private ListeningExecutorService service;
    
    private ThreadPoolExecutor executorService;
    
    private AtomicInteger count = new AtomicInteger();
    
    public GuavaProcess(ThreadPoolExecutor executorService) {
        
        this.executorService = executorService;
        
        service = MoreExecutors.listeningDecorator(executorService);
    }
    
    public void run() throws Exception {
        ListenableFuture<List<Object>> future = processA();
        
        //Futures.addCallback(successfulQueries, callbackOnSuccessfulQueries);
        //System.out.println(executorService.getLargestPoolSize());
        future.get();
        
    }
    
    public int getCount() {
        return count.get();
    }
    
    public ListenableFuture<List<Object>> processA() throws Exception {
        
        
        final ListenableFuture<List<Object>>[] futures = new ListenableFuture[10];
        for(int i=0; i<10; i++) {
            ListenableFuture<List<Object>> future = processB();
            futures[i] = future;
        }

        AsyncFunction<List<List<Object>>, List<Object>> function = new AsyncFunction<List<List<Object>>,List<Object>>(){

            @Override
            public ListenableFuture<List<Object>> apply(List<List<Object>> input) {
                return futures[0];
            }
            
        };
        ListenableFuture<List<List<Object>>> successfulQueries = Futures.allAsList(futures);
        return Futures.transform(successfulQueries, function);
        
    }
    
    
    public ListenableFuture<List<Object>> processB() throws Exception {
        
        final ListenableFuture<List<Object>>[] futures = new ListenableFuture[10];
        
        for(int i=0; i<10; i++) {
            ListenableFuture<List<Object>> future = processC();
            futures[i] = future;
        }
       
       
        AsyncFunction<List<List<Object>>, List<Object>> function = new AsyncFunction<List<List<Object>>,List<Object>>(){

            @Override
            public ListenableFuture<List<Object>> apply(List<List<Object>> input) {
                // TODO Auto-generated method stub
                return futures[0];
            }
            
        };
        ListenableFuture<List<List<Object>>> successfulQueries = Futures.allAsList(futures);
        return Futures.transform(successfulQueries, function);
        
    }
    
    public ListenableFuture<List<Object>> processC() throws Exception {
        final List<ListenableFuture<Object>> queries = new ArrayList<>();
        
        for(int i=0; i<10; i++) {
            ListenableFuture<Object> future = service.submit(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    
                    count.incrementAndGet();
                    LockSupport.parkNanos(1000000);
                    //Thread.yield();
                    return 1;

                }
                
            });
            queries.add(future);
        }
        
        ListenableFuture<List<Object>> successfulQueries = Futures.allAsList(queries);

        return successfulQueries;
    }
    
}
