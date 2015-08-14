package com.tyzhou.tasktree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func2;


/**
 * 
 * @author zhoutianji
 *
 */
public class RxJavaProcess {

    private ThreadPoolExecutor executorService;
    
    private AtomicInteger count = new AtomicInteger();
    
    public RxJavaProcess(ThreadPoolExecutor executorService) {
        
        this.executorService = executorService;
        
    }
    
    public void run() throws Exception {
        Observable<List<Object>> o = processA();
        
        //Futures.addCallback(successfulQueries, callbackOnSuccessfulQueries);
        //System.out.println(executorService.getLargestPoolSize());
        
        //o.subscribe();
        o.toBlocking().last();
    }
    
    public int getCount() {
        return count.get();
    }
    
    public Observable<List<Object>> processA() throws Exception {
        List<Observable<List<Object>>> observerList = new ArrayList<>();
        
        for(int i=0; i<10; i++) {
            Observable<List<Object>> ob = processB();
            observerList.add(ob);
        }
        
        return Observable.merge(observerList).reduce(new ArrayList<Object>(), new Func2<List<Object>, List<Object>, List<Object>>(){

            @Override
            public List<Object> call(List<Object> t1, List<Object> t2) {
                t1.add(t2);
                return t1;
            }

            
            
        });
    }
    
    public Observable<List<Object>> processB() throws Exception {
        List<Observable<List<Object>>> observerList = new ArrayList<>();
        
        for(int i=0; i<10; i++) {
            Observable<List<Object>> ob = processC();
            observerList.add(ob);
        }
        
        return Observable.merge(observerList).reduce(new ArrayList<Object>(), new Func2<List<Object>, List<Object>, List<Object>>(){

            @Override
            public List<Object> call(List<Object> t1, List<Object> t2) {
                t1.add(t2);
                return t1;
            }

            
            
        });
    }
    
    public Observable<List<Object>> processC() throws Exception {
        
        List<Observable<Object>> observerList = new ArrayList<>();
        
        for(int i=0; i<10; i++) {
            observerList.add(Observable.create(new OnSubscribe<Object>() {

                @Override
                public void call(final Subscriber<? super Object> t) {
                    executorService.submit(new Runnable(){

                        @Override
                        public void run() {
                            //LockSupport.parkNanos(10000000);
                            count.incrementAndGet();
                            Thread.yield();
                            t.onNext("abc");
                            t.onCompleted();
                        }
                        
                    });
                    
                }
                
            }));
            
            
        }
        
        

        return Observable.merge(observerList).collect(new Func0<List<Object>>(){

            @Override
            public List<Object> call() {
                return new ArrayList<Object>();
            }
            
        }, new Action2<List<Object>, Object>() {

            @Override
            public void call(List<Object> t1, Object t2) {
                t1.add(t2);
                
            }
            
        });
    }
}
