package com.tyzhou.tasktree;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author zhoutianji
 *
 */
public class TaskProcess {

    private TaskExecutor executor ;
    
    private AtomicInteger count = new AtomicInteger();
    
    public TaskProcess(ThreadPoolExecutor executorService) {
        executor = new TaskExecutor(executorService);
    }
    
    public void run() {
        TaskFuture future = executor.submit(newTaskA());
        try {
            future.getResult();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //System.out.println(executor.getExecutorService().getLargestPoolSize());
    }
    
    public int getCount() {
        return count.get();
    }
    
    public TaskNode newTaskA() {
        
        
        TaskNode task = new TaskNode() {
            @Override
            protected Object run() {
                return "A";
            }
            
        };
        
        for(int i=0; i<10; i++) {
            task.addChild(newTaskB());
        }

        return task;
    }
    
    public TaskNode newTaskB() {
        TaskNode task = new TaskNode() {
            
            @Override
            protected Object run() {
                
                return "B";
            }
            
        };
        
        for(int i=0; i<10; i++) {
            task.addChild(newTaskC());
        }

        return task;
    }
    
    public TaskNode newTaskC() {
        TaskNode task = new TaskNode() {
            @Override
            protected Object run() {
                
                return "C";
            }
            
        };
        
        for(int i=0; i<10; i++) {
            task.addChild(newTaskD());
        }
        
        return task;
    }
    
    public TaskNode newTaskD() {
        return new TaskNode() {

            @Override
            protected Object run() {
                count.incrementAndGet();
                //LockSupport.parkNanos(1000000);
                Thread.yield();
                return "D";
            }
            
        };
    }
}
