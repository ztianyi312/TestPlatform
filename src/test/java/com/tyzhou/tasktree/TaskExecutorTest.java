package com.tyzhou.tasktree;

import static org.junit.Assert.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

public class TaskExecutorTest {

    private ThreadPoolExecutor executorService = new ThreadPoolExecutor(100, 100, 1, TimeUnit.MINUTES, 
            new LinkedBlockingQueue<Runnable>(400), new ThreadFactory() {

        private AtomicInteger id = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("TaskExecutor-" + id.addAndGet(1));
            return thread;
        }
    }, new ThreadPoolExecutor.CallerRunsPolicy());
    
    private TaskExecutor executor; 
    
    @Before
    public void setUp() throws Exception {
        executor = new TaskExecutor(executorService);
    }

    //@Test
    public void testSubmit() throws InterruptedException {
        TaskA task = new TaskA(null, 123L);
        
        long start = System.currentTimeMillis();
        TaskFuture future = executor.submit(task);
        
        future.getResult();
        long end = System.currentTimeMillis();
        
        System.out.println("cost:"+(end-start)+"ms");
        System.out.println("keyNode:"+future.getKeyNode());
        
        executor.visit(task);
        
        System.out.println(executor.getExecutorService().getLargestPoolSize());
    }

    //@Test
    public void testSynchProcess() {
        SynchProcess process = new SynchProcess(executorService);
        
        for(int i=0; i<100; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        long start = System.currentTimeMillis();
        for(int i=0; i<100; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(process.getCount());
        System.out.println("cost:"+(end-start)+"ms");
    }
    
    
    
    @Test
    public void testGuavaProcess() {
        GuavaProcess process = new GuavaProcess(executorService);
        
        for(int i=0; i<200; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        long start = System.currentTimeMillis();
        for(int i=0; i<100; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(process.getCount());
        System.out.println("GuavaProcess cost:"+(end-start)+"ms");
    }
    
    @Test
    public void testTaskProcess() {
        TaskProcess process = new TaskProcess(executorService);
        
        for(int i=0; i<200; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        long start = System.currentTimeMillis();
        for(int i=0; i<100; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(process.getCount());
        System.out.println("TaskProcess cost:"+(end-start)+"ms");
    }
    
    //@Test
    public void testDisruptorProcess() {
        DisruptorProcess process = new DisruptorProcess(executorService);
        
        for(int i=0; i<200; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        long start = System.currentTimeMillis();
        for(int i=0; i<100; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(process.getCount());
        System.out.println("DisruptorProcess cost:"+(end-start)+"ms");
    }
}
