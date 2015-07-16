package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author zhoutianji
 *
 */
public abstract class TaskNode<T> {
    
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public enum TaskStatus {
        wait,
        running,
        complete
    }
    
    private volatile TaskStatus status = TaskStatus.wait;
    
    protected List<TaskNode> parentList = new ArrayList<>();
    
    protected List<TaskNode> childrenList;
    
    protected AtomicInteger waitCount;
    
    private AtomicBoolean startFlag = new AtomicBoolean(false);
    
    protected TaskFuture future;
    
    protected T result;
    
    public TaskNode(){

    }
    
    public void addChild(TaskNode child) {
        if(child == null) {
            return;
        }
        
        if(childrenList == null) {
            childrenList = new ArrayList<>();
        }
        
        childrenList.add(child);
        child.parentList.add(this);
    }
    
    protected void init() {
        
        if(childrenList != null) {
            waitCount = new AtomicInteger(childrenList.size());
        }
    }
    
    protected boolean start() {
        
        return startFlag.compareAndSet(false, true);
    }
    
    protected int increaseWaitCount() {
        return waitCount.incrementAndGet();
    }
    
    /**
     * after child complete
     */
    protected abstract T run();
    
    protected void execute() {
        try{
            result = run();
        }catch(Exception e) {
            logger.error("task run failed : "+this, e);
        }
        if(future != null) {
            future.countDown(getResult());
        } 
    }
    
    
    
    public List<TaskNode> getChildren() {
        return childrenList;
    }
    
    public List<TaskNode> getLeafList() {
        List<TaskNode> leafList = new ArrayList<>();
        if(childrenList != null) {
            for(TaskNode child : childrenList) {
                leafList.addAll(child.getLeafList());
            }
        }
        if(leafList.size() == 0) {
            leafList.add(this);
        }
        this.init();
        
        return leafList;
    }
    
    public T getResult() {
        return result;
    }
    
    public List<TaskNode> getParentList() {
        return this.parentList;
    }
}
