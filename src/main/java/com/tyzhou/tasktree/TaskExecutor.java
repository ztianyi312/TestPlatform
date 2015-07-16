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

    private ThreadPoolExecutor executorService;
    
    public TaskExecutor(ThreadPoolExecutor executorService) {
        this.executorService = executorService;
    }
    
    //private static ThreadLocal<TaskNode> context = new ThreadLocal<>();
    
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
            TaskNode last = queue.removeFirst();
            System.out.println(last);
            
            if(last.getChildren() != null) {
                queue.addAll(last.getChildren());
            }
            
            if(--parentCount == 0) {
                System.out.println();
                parentCount = queue.size();
            }
        }
    }
    
    protected void prepareTask(final TaskNode taskNode) {
        List<TaskNode> leafList = taskNode.getLeafList();
        for(int i=0; i<leafList.size()-1; i++) {
            
            runTask(leafList.get(i));
        }
        TaskNode last = leafList.get(leafList.size()-1);
        if(last.start()) {
            doTask(last);
        }
    }
    
    protected void runTask(final TaskNode taskNode) {
        
        if(!taskNode.start()) {
            return;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                doTask(taskNode);
            }
        
        });
    }
    
    protected void doTask(final TaskNode taskNode) {
        taskNode.execute();
        if(taskNode.getParentList().size() > 0) {
            List<TaskNode> parentList = taskNode.getParentList();
            TaskNode currentTask = null;
            
            for(final TaskNode parentNode : parentList) {  
                
                if(parentNode.waitCount.decrementAndGet() == 0) {
                    if(currentTask == null) {
                        currentTask = parentNode;
                    }else {
                        runTask(parentNode);
                    }
                } 
            }
            
            if(currentTask != null && currentTask.start()) {
                doTask(currentTask);
            }
            
        } else {
            //taskNode.future.setKeyNode(context.get());
            //context.remove();
        }
    }

    public ThreadPoolExecutor getExecutorService() {
        return executorService;
    }
    
    
}
