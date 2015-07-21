package com.tyzhou.reactive;

import java.util.concurrent.locks.LockSupport;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.util.async.Async;

/**
 * 
 * @author zhoutianji
 *
 */
public class Sample {

    public static void main(String[] args) {
        Observable<String> o = Observable.just("asdasdssa");
        Observable<String> o2 = o.startWith("asd");
        Observable<String> a1 = Async.start(new Func0<String>(){

            @Override
            public String call() {
                return "1";
            }
            
        });
        
        Observable<String> a2 = Async.start(new Func0<String>(){

            @Override
            public String call() {
                LockSupport.parkNanos(10000000);
                
                return "2";
            }
            
        });
        
        Observable.merge(a1, a2, o).subscribe(new Action1<String>(){

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
                        LockSupport.parkNanos(10000000);
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
