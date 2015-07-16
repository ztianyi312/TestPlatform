package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * 
 * @author zhoutianji
 *
 */
public class TaskA extends TaskNode<List<Object>>{
    
    private List<Long> userIdList;
    
    private TaskB taskB;
    
    private TaskC taskC;


    public TaskA(long id) {
        
        prepare();
    }
    
    public void prepare() {
        
        System.out.println(Thread.currentThread()+" taskA prepare start");
        
        TaskNode<List<Long>> task = new TaskNode() {

            @Override
            protected List<Long> run() {
                
                System.out.println("task run start:");
                LockSupport.parkNanos(100000000);
                List<Long> userIdList = new ArrayList<>();
                userIdList.add(1L);
                userIdList.add(2L);
                
                System.out.println("task run end:");
                return userIdList;
                
            }
            
        };
        task.init();
        
        
        
        taskB = new TaskB(task);
        
        taskC = new TaskC();
          
        this.addChild(taskB);
        this.addChild(taskC);
        this.init();
        
        System.out.println("taskA prepare end");
    }

    @Override
    public List<Object> run() {
        System.out.println(Thread.currentThread()+" taskA run start:"+childrenList.size());
        System.out.println("taskA run start:"+waitCount.get());
        
        List<Object>result = new ArrayList<>();
        
        List<Object> listB = taskB.getResult();
        List<Long> listC = taskC.getResult();
        
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
