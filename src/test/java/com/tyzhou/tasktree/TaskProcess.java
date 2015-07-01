package com.tyzhou.tasktree;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author zhoutianji
 *
 */
public class TaskProcess {

    private TaskExecutor executor = new TaskExecutor();
    
    private AtomicInteger count = new AtomicInteger();
    
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
    
    public TaskNode newTaskA() {
        return new TaskNode(null) {

            @Override
            protected void prepare() {
                for(int i=0; i<10; i++) {
                    this.childrenList.add(newTaskB(this));
                }
            }
            
            @Override
            protected Object run() {
                
                return "A";
            }
            
        };
    }
    
    public TaskNode newTaskB(TaskNode parent) {
        return new TaskNode(parent) {

            @Override
            protected void prepare() {
                for(int i=0; i<10; i++) {
                    this.childrenList.add(newTaskC(this));
                }
            }
            
            @Override
            protected Object run() {
                
                return "B";
            }
            
        };
    }
    
    public TaskNode newTaskC(TaskNode parent) {
        return new TaskNode(parent) {

            @Override
            protected void prepare() {
                for(int i=0; i<10; i++) {
                    this.childrenList.add(newTaskD(this));
                }
            }
            
            @Override
            protected Object run() {
                
                return "C";
            }
            
        };
    }
    
    public TaskNode newTaskD(TaskNode parent) {
        return new TaskNode(parent) {

            @Override
            protected Object run() {
                count.incrementAndGet();
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                return "D";
            }
            
        };
    }
}
