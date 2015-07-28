package com.tyzhou.reactive;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

/**
 * 
 * @author zhoutianji
 *
 */
public class Sample {

    public static void main(String[] args) {
        
        Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(10));
        Observable<String> o = Observable.just("asdasdssa");
        Observable<String> o2 = o.startWith("asd");
        Observable<String> a1 = Async.start(new Func0<String>(){

            @Override
            public String call() {
                return "1";
            }
            
        }, scheduler);
        
        Observable<String> a2 = Async.start(new Func0<String>(){

            @Override
            public String call() {
                LockSupport.parkNanos(10000000);
                
                return "2";
            }
            
        }, scheduler);
        
        Observable.merge(a1, a2, o).observeOn(scheduler).doOnEach(new Observer<String>(){

            @Override
            public void onCompleted() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onError(Throwable e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onNext(String t) {
                LockSupport.parkNanos(10000000);
                System.out.println(Thread.currentThread()+" "+t);
                
            }
            
        }).subscribe(new Action1<String>(){

            @Override
            public void call(String t) {
                System.out.println(t);
                
            }
            
            
        });
        
        Observable.create(new OnSubscribe<String>() {

            @Override
            public void call(final Subscriber<? super String> t) {
                Thread task = new Thread(new Runnable(){

                    @Override
                    public void run() {
                        LockSupport.parkNanos(1000);
                        t.onNext("abc");
                    }
                    
                });
                task.start();
                
            }
            
        }).subscribe(s-> System.out.println(s));

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
