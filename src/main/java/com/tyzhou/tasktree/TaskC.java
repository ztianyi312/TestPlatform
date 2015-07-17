package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Longs;

/**
 * 
 * @author zhoutianji
 *
 */
public class TaskC extends TaskNode<List<Long>>{

    public TaskC() {
        TaskNode asyncTask = new AsyncTaskNode() {

            @Override
            protected void run(final Runnable callback) {
                System.out.println("C1 start");
                
                Thread t = new Thread(new Runnable(){

                    @Override
                    public void run() {
                        TaskC.this.result = Longs.asList(1,2);
                        
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.println("C1 callback");
                        callback.run();
                    }
                    
                });
                t.start();
                
            }
            
        };
        this.addChild(asyncTask);
        
        init();
    }

    @Override
    public List<Long> run() {
        System.out.println(Thread.currentThread()+" taskC out start");
        List<Long> result = new ArrayList<>();
        
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        
        System.out.println("taskC out end");

        return result;
    }

}