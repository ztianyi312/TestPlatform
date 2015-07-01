package com.tyzhou.tasktree;

import java.util.concurrent.CountDownLatch;


/**
 * 
 * @author zhoutianji
 *
 */
public class  TaskFuture<T> {

    private CountDownLatch latch = new CountDownLatch(1);
    
    private T result;
    
    private TaskNode keyNode;
    
    protected void countDown(T result) {
        this.result = result; 
        latch.countDown();
    }
    
    public  T getResult() throws InterruptedException {
        latch.await();
        
        return result;
    }

    public TaskNode getKeyNode() {
        return keyNode;
    }

    protected void setKeyNode(TaskNode keyNode) {
        this.keyNode = keyNode;
    }
    
    
}
