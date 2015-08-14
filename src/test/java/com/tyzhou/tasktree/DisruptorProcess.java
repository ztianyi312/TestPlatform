/**
 * 
 */
package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * @author zhoutianji
 *
 */
public class DisruptorProcess {

    private ThreadPoolExecutor executorService;
    
    private Disruptor<LongEvent> disruptor;
    
    private AtomicInteger count = new AtomicInteger();
    
    private CountDownLatch cdl;
    
    public DisruptorProcess(ThreadPoolExecutor executorService) {
        
        this.executorService = executorService;
       
        disruptor = new Disruptor<LongEvent>(new LongEventFactory(), 1024, executorService);
        
        for(int i=0; i<10; i++) {
            List<TransformingHandler> tranList = new ArrayList<>();
            for(int j=0; j<10; j++) {
                tranList.add(new TransformingHandler(count));
            }
            disruptor.handleEventsWith(tranList.toArray(new TransformingHandler[0])).then(new WorkHandler());  
        }
        
        disruptor.start();
    }
    
    public void run() throws Exception {
        
        cdl = new CountDownLatch(100);
        disruptor.publishEvent(new EventTranslator<LongEvent>(){

            @Override
            public void translateTo(LongEvent event, long sequence) {
                event.setValue(sequence);
                
            }
            
        });
        
        cdl.await();
    }
    
    
    
    
    public int getCount() {
        return count.get();
    }
    
    public class LongEvent { 
        private long value;
        public long getValue() { 
            return value; 
        } 
     
        public void setValue(long value) { 
            this.value = value; 
        } 
    } 
    
    public class LongEventFactory implements EventFactory<LongEvent> { 
        @Override 
        public LongEvent newInstance() { 
            return new LongEvent(); 
        } 
    } 
    
    public  class TransformingHandler implements EventHandler<LongEvent>
    {

        private AtomicInteger count;
        
        public TransformingHandler(AtomicInteger count) {
            this.count = count;
        }
        
        @Override
        public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception
        {
            doSomething(event.value);
        }

        private Object doSomething(Object input)
        {
            LockSupport.parkNanos(1000000);
            
            
            cdl.countDown();
            count.incrementAndGet();
            return input;
        }
    }
    
    public static class WorkHandler implements EventHandler<LongEvent>
    {

        @Override
        public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception
        {
            doSomething(event.value);
        }

        private Object doSomething(Object input)
        {      
            return input;
        }
    }
    
}
