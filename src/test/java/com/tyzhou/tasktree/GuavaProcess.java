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
    
    public GuavaProcess() {
        
        executorService = new ThreadPoolExecutor(400, 400, 1, TimeUnit.MINUTES, 
                new LinkedBlockingQueue<Runnable>(400), new ThreadFactory() {

            private AtomicInteger id = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("TaskExecutor-" + id.addAndGet(1));
                return thread;
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
        
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
        
        ListenableFutureTask<List<Object>> result =  ListenableFutureTask.create(new Callable<List<Object>>(){

            @Override
            public List<Object> call() throws Exception {
                for(int i=0; i<10; i++) {
                    futures[i].get();
                }
                
                return futures[0].get();
                //return Collections.EMPTY_LIST;
            }
            
        });
        
        service.submit(result);
        
        return result;
    }
    
    
    public ListenableFuture<List<Object>> processB() throws Exception {
        
        final ListenableFuture<List<Object>>[] futures = new ListenableFuture[10];
        for(int i=0; i<10; i++) {
            ListenableFuture<List<Object>> future = processC();
            futures[i] = future;
        }
        
        ListenableFutureTask<List<Object>> result = ListenableFutureTask.create(new Callable<List<Object>>(){

            @Override
            public List<Object> call() throws Exception {
                for(int i=0; i<10; i++) {
                    futures[i].get();
                }
                
                return futures[0].get();
                //return Collections.EMPTY_LIST;
            }
            
        });
        
        service.submit(result);

        return result;
    }
    
    public ListenableFuture<List<Object>> processC() throws Exception {
        final List<ListenableFuture<Object>> queries = new ArrayList<>();
        
        for(int i=0; i<10; i++) {
            ListenableFuture<Object> future = service.submit(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    
                    count.incrementAndGet();
                    Thread.sleep(2);
                    return 1;

                }
                
            });
            queries.add(future);
        }
        
        ListenableFutureTask<List<Object>> result = ListenableFutureTask.create(new Callable<List<Object>>(){

            @Override
            public List<Object> call() throws Exception {
                for(int i=0; i<10; i++) {
                    queries.get(i).get();
                }
                
                //return queries.get(0).get();
                return Collections.EMPTY_LIST;
            }
            
        });
        
        //service.submit(result);
        
        ListenableFuture<List<Object>> successfulQueries = Futures.allAsList(queries);

        return successfulQueries;
    }
    
}
