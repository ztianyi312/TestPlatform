package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

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
    
    public List<TaskNode> findLeafNode(final TaskNode taskNode, List<TaskNode> leafList) {
        taskNode.init();
        List<TaskNode> children = taskNode.getChildren();
        if(children == null || children.size() == 0) {
            leafList.add(taskNode);
        } else {
            for(final TaskNode child : children) {
                findLeafNode(child, leafList);
            }
        }
        
        return leafList;
    }
    
    protected void prepareTask(final TaskNode taskNode) {
        List<TaskNode> leafList = findLeafNode(taskNode, new ArrayList<TaskNode>());
        
        TaskNode lastSyncNode = null;
        
        for(int i=0; i<leafList.size(); i++) {
            
            TaskNode currentNode = leafList.get(i);
            if(!currentNode.start()) {
                continue;
            }
            
            if(leafList.get(i).isAsync()) {
                doTask(currentNode);
            } else if(lastSyncNode != null) {
                runTask(currentNode);
            } else {
                lastSyncNode = currentNode;
            }
            
        }

        if(lastSyncNode != null) {
            context.set(lastSyncNode);
            doTask(lastSyncNode);
        }
    }
    
    protected void runTask(final TaskNode taskNode) {

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                context.set(taskNode);
                doTask(taskNode);
            }
        
        });
    }
    
    protected void doTask(final TaskNode taskNode) {
        Runnable callback = new Runnable(){
            @Override
            public void run() {
            
                if(taskNode.getParentList().size() > 0) {
                    List<TaskNode> parentList = taskNode.getParentList();
                    TaskNode currentTask = null;
                    
                    for(final TaskNode parentNode : parentList) {  
                        
                        if(parentNode.waitCount.decrementAndGet() == 0) {
                            if(currentTask == null && !taskNode.isAsync()) {
                                currentTask = parentNode;
                            }else {
                                runTask(parentNode);
                            }
                        } 
                    }
                    
                    if(currentTask != null) {
                        doTask(currentTask);
                    } else {
                        context.remove();
                    }
                    
                } else {
                    taskNode.future.setKeyNode(context.get());
                    context.remove();
                }
            }
            
        };
        
        taskNode.execute(callback);
        
    }

    public ThreadPoolExecutor getExecutorService() {
        return executorService;
    }
    
    
}
