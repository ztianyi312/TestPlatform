package com.tyzhou.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 
 * @author zhoutianji
 *
 */
public class MultiToOne {

    //private Queue<List<Long>> queue = new ConcurrentLinkedQueue<>();
    
    private BlockingQueue<List<Long>> queue = new ArrayBlockingQueue<>(10000);
    
    private ExecutorService singleThread = Executors.newFixedThreadPool(1);
    
    private Disruptor<DataEvent> disruptor =
            new Disruptor<DataEvent>(DataEvent.FACTORY, 1024, singleThread, ProducerType.MULTI, new SleepingWaitStrategy());
    
    private List<Long> idList = new ArrayList<>();
    
    private RingBuffer<DataEvent> ringBuffer;
    
    public MultiToOne() {
        disruptor.handleEventsWith(new EventHandler<DataEvent>(){

            @Override
            public void onEvent(DataEvent event, long sequence, boolean endOfBatch) throws Exception {
                
                idList.addAll(event.input);
                
                if(idList.size() > 100) {
                    System.out.println(idList.size()+" "+ Thread.currentThread());
                    idList = new ArrayList<>();
                    
                    
                }
            }
            
        });
        
        //ringBuffer = disruptor.start();
    }
    
    public void  sendRequest1(List<Long> list ) {
        queue.add(list);
    }
    /*
    public void  sendRequest(List<Long> list ) {
        queue.add(list);
        
        singleThread.submit(new Runnable() {

            @Override
            public void run() {
                
                int count = 0;
                while(true) {
                    count++;
                    List<Long> node = queue.poll();
                    if(node == null) {
                        //System.out.println(count);
                        return;
                    }
                    idList.addAll(node);
                    
                    if(idList.size() > 100) {
                        System.out.println(idList.size());
                        idList = new ArrayList<>();
                        
                        
                    }
                }
            }
            
        });
    }
    */
    public void  sendRequest2(final List<Long> list ) {
        ringBuffer.publishEvent(new EventTranslator<DataEvent>() {

            @Override
            public void translateTo(DataEvent event, long sequence) {
        
                event.input=list;
            }
    
        });
    }
    
    
    public void start() {
        singleThread.submit(new Runnable() {

            @Override
            public void run() {
                List<Long> idList = new ArrayList<>();
                int count = 0;
                while(true) {
                    count++;
                    List<Long> node = null;
                    try {
                        node = queue.poll(10, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        return;
                    }
                    if(node == null) {
                        System.out.println(idList.size());
                        idList = new ArrayList<>();
                        continue;
                    }
                    idList.addAll(node);
                    
                    if(idList.size() > 100) {
                        System.out.println(idList.size());
                        idList = new ArrayList<>();
                        
                        
                    }
                }
            }
            
        });
    }
    
    public void await() {
        try {
            singleThread.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static class DataEvent
    {
        List<Long> input;

        public DataEvent()
        {
        }
        
        public void set(List<Long> input) {
            this.input = input;
        }

        public static final EventFactory<DataEvent> FACTORY = new EventFactory<DataEvent>()
        {
            @Override
            public DataEvent newInstance()
            {
                return new DataEvent();
            }
        };
    }
}

 class SleepingWaitStrategy implements WaitStrategy
{
    private static final int DEFAULT_RETRIES = 200;

    private final int retries;

    public SleepingWaitStrategy()
    {
        this(DEFAULT_RETRIES);
    }

    public SleepingWaitStrategy(int retries)
    {
        this.retries = retries;
    }

    @Override
    public long waitFor(final long sequence, Sequence cursor, final Sequence dependentSequence, final SequenceBarrier barrier)
        throws AlertException, InterruptedException
    {
        long availableSequence;
        int counter = retries;

        while ((availableSequence = dependentSequence.get()) < sequence)
        {
            counter = applyWaitMethod(barrier, counter);
        }

        return availableSequence;
    }

    @Override
    public void signalAllWhenBlocking()
    {
    }

    private int applyWaitMethod(final SequenceBarrier barrier, int counter)
        throws AlertException
    {
        barrier.checkAlert();

        if (counter > 100)
        {
            --counter;
        }
        else if (counter > 0)
        {
            --counter;
            Thread.yield();
        }
        else
        {
            LockSupport.parkNanos(1000000L);
        }

        return counter;
    }
}