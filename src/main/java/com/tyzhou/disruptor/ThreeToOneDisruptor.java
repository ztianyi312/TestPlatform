package com.tyzhou.disruptor;


import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.tyzhou.disruptor.ThreeToOneDisruptor.DataEvent;
import com.tyzhou.tasktree.DisruptorProcess.LongEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreeToOneDisruptor
{
    public static class DataEvent
    {
        Object input;
        Object[] output;

        public DataEvent(int size)
        {
            output = new Object[size];
        }

        public static final EventFactory<DataEvent> FACTORY = new EventFactory<DataEvent>()
        {
            @Override
            public DataEvent newInstance()
            {
                return new DataEvent(3);
            }
        };
    }

    public static class TransformingHandler implements EventHandler<DataEvent>
    {
        private final int outputIndex;

        public TransformingHandler(int outputIndex)
        {
            this.outputIndex = outputIndex;
        }

        @Override
        public void onEvent(DataEvent event, long sequence, boolean endOfBatch) throws Exception
        {
            // Do Stuff.
            event.output[outputIndex] = doSomething(event.input);
        }

        private Object doSomething(Object input)
        {
            // Do required transformation here....
            System.out.println("TransformingHandler:" + Thread.currentThread());
            
            return input;
        }
    }

    public static class CollatingHandler implements EventHandler<DataEvent>
    {
        @Override
        public void onEvent(DataEvent event, long sequence, boolean endOfBatch) throws Exception
        {
            collate(event.output);
            System.out.println(endOfBatch);
        }

        private void collate(Object[] output)
        {
            System.out.println("CollatingHandler:" + Thread.currentThread());
            
            System.out.println(output[0]);
        }
    }

    public static void main(String[] args)
    {
        Executor executor = Executors.newFixedThreadPool(4);
        Disruptor<DataEvent> disruptor = new Disruptor<DataEvent>(DataEvent.FACTORY, 1024, executor);

        TransformingHandler handler1 = new TransformingHandler(0);
        TransformingHandler handler2 = new TransformingHandler(1);
        TransformingHandler handler3 = new TransformingHandler(2);
        CollatingHandler collator = new CollatingHandler();

        disruptor.handleEventsWith(handler1, handler2, handler3).then(collator);

        disruptor.start();
        
        disruptor.publishEvent(new DataTranslator());
        disruptor.publishEvent(new DataTranslator());
        disruptor.publishEvent(new DataTranslator());
        disruptor.publishEvent(new DataTranslator());
        disruptor.publishEvent(new DataTranslator());

    }
}

class DataTranslator implements EventTranslator<DataEvent> {

    @Override
    public void translateTo(DataEvent event, long sequence) {
        
        event.input=sequence;
    }
    
}