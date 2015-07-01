package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
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
    
    private TaskNode parent;
    
    protected List<TaskNode> childrenList = new ArrayList<>();
    
    protected AtomicInteger waitCount;
    
    protected TaskFuture future;
    
    protected T result;
    
    public TaskNode(TaskNode parentNode){
        parent = parentNode;
    }
    
    protected void init() {
        try{
            prepare();
        }catch(Exception e) {
            logger.error("task prepare failed : "+this, e);
        }
        if(childrenList != null && childrenList.size() >0) {
            this.waitCount = new AtomicInteger(childrenList.size());
        }
    }
    
    /**
     * before child complete
     */
    protected void prepare(){};
    
    /**
     * after child complete
     */
    protected abstract T run();
    
    protected int execute() {
        try{
            result = run();
        }catch(Exception e) {
            logger.error("task run failed : "+this, e);
        }
        if(future != null) {
            future.countDown(getResult());
        } 
        
        if(parent == null || parent.waitCount == null) {
            return 0;
        }
        
        return parent.waitCount.decrementAndGet();
    }
    
    
    
    public List<TaskNode> getChildren() {
        return childrenList;
    }
    
    public T getResult() {
        return result;
    }
    
    public TaskNode getParent() {
        return this.parent;
    }
}
