package com.tyzhou.tasktree;

/**
 * 
 * @author zhoutianji
 *
 */
public abstract class AsyncTaskNode<T> extends TaskNode<T>{

    protected T run(){return null;};
    
    protected void execute(final Runnable callback) {
        try{
            run(callback);
        }catch(Exception e) {
            logger.error("task run failed : "+this, e);
        }
        if(future != null) {
            future.countDown(getResult());
        } 
    }
    
    protected abstract void run(final Runnable callback);
    
    public boolean isAsync() {
        return true;
    }
}
