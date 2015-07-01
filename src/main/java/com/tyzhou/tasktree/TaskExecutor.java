package com.tyzhou.tasktree;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
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
public class TaskExecutor {

    private ThreadPoolExecutor executorService = new ThreadPoolExecutor(50, 150, 1, TimeUnit.MINUTES, 
            new LinkedBlockingQueue<Runnable>(100), new ThreadFactory() {

        private AtomicInteger id = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("TaskExecutor-" + id.addAndGet(1));
            return thread;
        }
    }, new ThreadPoolExecutor.CallerRunsPolicy());
    
    private static ThreadLocal<TaskNode> context = new ThreadLocal<>();
    
    public TaskFuture submit(final TaskNode taskNode) {
        if(taskNode == null) {
            return null;
        }
        
        TaskFuture future = new TaskFuture();
        taskNode.future = future;
        prepareTask(taskNode);
        
        return future;
    }
    
    public void visit(final TaskNode taskNode) {
        LinkedList<TaskNode> queue = new LinkedList<>();
        int parentCount = 1;
        queue.addFirst(taskNode);
        
        while(parentCount > 0) {
            TaskNode last = queue.removeLast();
            System.out.println(last);
            queue.addAll(last.getChildren());
            
            if(--parentCount == 0) {
                System.out.println();
                parentCount = queue.size();
            }
        }
    }
    
    protected void prepareTask(final TaskNode taskNode) {
        taskNode.init();
        List<TaskNode> children = taskNode.getChildren();
        int size = children.size();
        if(children != null && size > 0) {
            int count = 0;
            for(final TaskNode task : children) {
                if(++count == size) {
                    prepareTask(task);
                }else {
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            context.set(task);
                            prepareTask(task);
                        }
                    
                    });
                }
                
            }
        } else {
            doTask(taskNode);
        }
    }
    
    protected void runTask(final TaskNode taskNode) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                doTask(taskNode);
            }
        
        });
    }
    
    protected void doTask(final TaskNode taskNode) {
        if(taskNode.execute() == 0 && taskNode.getParent() != null) {
            doTask(taskNode.getParent());
        } else {
            if(taskNode.getParent() == null) {
                taskNode.future.setKeyNode(context.get());
            }
            context.remove();
        }
    }

    public ThreadPoolExecutor getExecutorService() {
        return executorService;
    }
    
    
}
