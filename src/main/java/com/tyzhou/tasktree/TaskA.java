package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author zhoutianji
 *
 */
public class TaskA extends TaskNode<List<Object>>{
    
    private List<Long> userIdList;
    
    private TaskB taskB;
    
    private TaskC taskC;


    public TaskA(TaskNode parentNode, long id) {
        super(parentNode);
        
        
    }
    
    @Override
    public void prepare() {
        
        System.out.println(Thread.currentThread()+" taskA prepare start");
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        userIdList = new ArrayList<>();
        userIdList.add(1L);
        userIdList.add(2L);
        
        taskB = new TaskB(this, userIdList);
        
        taskC = new TaskC(this, userIdList);
          
        childrenList.add(taskB);
        childrenList.add(taskC);
        
        System.out.println("taskA prepare end");
    }

    @Override
    public List<Object> run() {
        System.out.println(Thread.currentThread()+" taskA run start:"+childrenList.size());
        System.out.println("taskA run start:"+waitCount.get());
        
        List<Object>result = new ArrayList<>();
        
        List<Object> listB = taskB.getResult();
        List<Object> listC = taskC.getResult();
        
        result.add(listB);
        result.add(listC);
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("taskA run end");
        
        return result;
    }

}
