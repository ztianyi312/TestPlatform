package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhoutianji
 *
 */
public class TaskB extends TaskNode<List<Object>>{
    
    private List<Long> userIdList;

    public TaskB(TaskNode parentNode, List<Long> userIdList) {
        super(parentNode);
        
        this.userIdList = userIdList;
    }
    
    private TaskNode<List<Long>> taskB1;
    
    private TaskNode<List<Long>> taskB2;
    
    @Override
    public void prepare() {
        taskB2 = new TaskNode<List<Long>>(this){

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
                return userIdList;
            }

            
            
        };
        
        
        
        taskB1 = new TaskNode<List<Long>>(this){

            @Override
            protected void prepare(){
                System.out.println(Thread.currentThread()+" taskB1 prepare start");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("taskB1 prepare end");
            }
            
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
                return userIdList;
            }
            
        };
        childrenList.add(taskB1);
        childrenList.add(taskB2);
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