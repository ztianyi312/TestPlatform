package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhoutianji
 *
 */
public class TaskB extends TaskNode<List<Object>>{

    
    private TaskNode<List<Long>> taskB1;
    
    private TaskNode<List<Long>> taskB2;
    
    public TaskB(final TaskNode<List<Long>> child) {
        taskB2 = new TaskNode<List<Long>>(){

            @Override
            protected List<Long> run() {
                System.out.println(Thread.currentThread()+" taskB2 run start");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("taskB2 run end");
                return child.getResult();
            }

            
            
        };
        
        
        
        taskB1 = new TaskNode<List<Long>>(){
            
            @Override
            protected List<Long> run() {
                System.out.println(Thread.currentThread()+" taskB1 run start");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("taskB1 run end");
                return child.getResult();
            }
            
        };
        this.addChild(taskB1);
        this.addChild(taskB2);
        
        taskB1.addChild(child);
        taskB2.addChild(child);
        
        this.init();
        taskB1.init();
        taskB2.init();
        
    }

    @Override
    public List<Object> run() {
        System.out.println(Thread.currentThread()+" taskB run start");
        List<Object> result = new ArrayList<>();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        result.addAll(taskB1.getResult());
        result.addAll(taskB2.getResult());
        
        System.out.println("taskB run end");
        
        return result;
    }

}