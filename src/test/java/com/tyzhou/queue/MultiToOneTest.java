package com.tyzhou.queue;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

import org.junit.Before;
import org.junit.Test;

import com.google.common.primitives.Longs;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.tyzhou.disruptor.ThreeToOneDisruptor.DataEvent;

public class MultiToOneTest {

    private MultiToOne adapter = new MultiToOne();
    
    private ExecutorService service = Executors.newCachedThreadPool(); 
    
    
    
    @Before
    public void setUp() throws Exception {
        
    }

    //@Test
    public void testStart() {
        //adapter.start();
        //adapter.sendRequest(Longs.asList(1,2,3));
        
        adapter.await();
        
        
    }
    
    //@Test
    public void testStart2() {
        //adapter.start();
        adapter.sendRequest2(Longs.asList(1,2,3));
        
        service.submit(new Runnable() {

            @Override
            public void run() {
                
                while(true) {
                    adapter.sendRequest2(Longs.asList(1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,10));
                    LockSupport.parkNanos(10000000);
                }
            }
            
        });
        
        service.submit(new Runnable() {

            @Override
            public void run() {
                while(true) {
                    adapter.sendRequest2(Longs.asList(1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,10));
                    LockSupport.parkNanos(10000000);
                }
                
            }
            
        });
        
        adapter.await();
    }

    @Test
    public void testStart1() {
        adapter.start();
        adapter.sendRequest1(Longs.asList(1,2,3));
        
        service.submit(new Runnable() {

            @Override
            public void run() {
                
                while(true) {
                    adapter.sendRequest1(Longs.asList(1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,10));
                    LockSupport.parkNanos(10000000);
                }
            }
            
        });
        
        service.submit(new Runnable() {

            @Override
            public void run() {
                while(true) {
                    adapter.sendRequest1(Longs.asList(1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,10));
                    LockSupport.parkNanos(10000000);
                }
                
            }
            
        });
        
        adapter.await();
    }
}
