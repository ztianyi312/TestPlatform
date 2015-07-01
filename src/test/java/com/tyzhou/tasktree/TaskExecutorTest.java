package com.tyzhou.tasktree;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TaskExecutorTest {

    private TaskExecutor executor = new TaskExecutor();
    
    @Before
    public void setUp() throws Exception {
    }

    //@Test
    public void testSubmit() throws InterruptedException {
        TaskA task = new TaskA(null, 123L);
        
        long start = System.currentTimeMillis();
        TaskFuture future = executor.submit(task);
        
        future.getResult();
        long end = System.currentTimeMillis();
        
        System.out.println("cost:"+(end-start)+"ms");
        System.out.println("keyNode:"+future.getKeyNode());
        
        executor.visit(task);
        
        System.out.println(executor.getExecutorService().getLargestPoolSize());
    }

    @Test
    public void testSynchProcess() {
        SynchProcess process = new SynchProcess();
        
        for(int i=0; i<100; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        long start = System.currentTimeMillis();
        for(int i=0; i<100; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("cost:"+(end-start)+"ms");
    }
    
    @Test
    public void testTaskProcess() {
        TaskProcess process = new TaskProcess();
        
        for(int i=0; i<100; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        long start = System.currentTimeMillis();
        for(int i=0; i<100; i++) {
            try {
                //System.out.println(i);
                process.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("cost:"+(end-start)+"ms");
    }
}
