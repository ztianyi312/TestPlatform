package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhoutianji
 *
 */
public class TaskC extends TaskNode<List<Object>>{

    private List<Long> userIdList;

    public TaskC(TaskNode parentNode, List<Long> userIdList) {
        super(parentNode);
        
        this.userIdList = userIdList;
    }
    

    @Override
    public List<Object> run() {
        System.out.println(Thread.currentThread()+" taskC out start");
        List<Object> result = new ArrayList<>();
        
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