package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhoutianji
 *
 */
public class TaskC extends TaskNode<List<Long>>{

    public TaskC() {
        init();
    }

    @Override
    public List<Long> run() {
        System.out.println(Thread.currentThread()+" taskC out start");
        List<Long> result = new ArrayList<>();
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        
        System.out.println("taskC out end");

        return result;
    }

}