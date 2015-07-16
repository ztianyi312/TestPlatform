package com.tyzhou.reactive;

import rx.Observable;
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
                return "2";
            }
            
        });
        
        Observable.merge(a1, a2).subscribe(new Action1<String>(){

            @Override
            public void call(String t) {
                System.out.println(t);
                
            }
            
            
        });

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
