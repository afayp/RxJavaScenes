package com.pfh.rxjavascenes.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;

import static android.R.attr.value;

/**
 * Created by Administrator on 2017/4/9 0009.
 */

public class OperatorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createOperators();
        errorOperators();
        convertOperators();
        filterOperators();
        combineOperators();
    }

    private void combineOperators() {
        //---merge---//
        //混着发射，无序
        final String[] letters = new String[]{"A","B","C","D"};
        Observable<String> letterObservable = Observable.interval(300, TimeUnit.MILLISECONDS)
                .take(letters.length)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        return letters[aLong.intValue()];
                    }
                });
        Observable<Long> numberObservable = Observable.interval(500, TimeUnit.MILLISECONDS).take(letters.length);
        Observable.merge(letterObservable,numberObservable);

        //---concat---//
        //有序，先发射完第一个，再发射第二个
        Observable.concat(letterObservable,numberObservable);

        //----startWith---//
        Observable.just(1,2,3)
                .startWith(-1,0);

        //---zip---//
        Observable.zip(letterObservable, numberObservable, new Func2<String, Long, String>() {
            @Override
            public String call(String s, Long aLong) {
                return s + aLong;
            }
        });

        //---combineLatest---//
        //CombineLatest操作符行为类似于zip，但是zip只有当原始的Observable中的每一个都发射了一条数据时才发射数据。
        //CombineLatest则在原始的Observable中任意一个发射了数据时发射一条数据。
        //当原始Observables的任何一个发射了一条数据时，CombineLatest使用一个函数结合它们最近发射的数据，然后发射这个函数的返回值。


    }

    private void filterOperators() {
        //---filter---//
        Observable.just(1,2,3,4,5)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer % 2 == 0;
                    }
                });

        //---take---//
        Observable.just(1,2,3)
                .take(1);

        //---takeLast---//
        Observable.just(1,2,3)
                .takeLast(1);

        //---takeUntil---//
        Observable.just(1,2,3,4,5)
                .takeUntil(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 2;// 输出1,2,3
                    }
                });
        Observable<Long> observable1 = Observable.interval(300, TimeUnit.MILLISECONDS);
        Observable<Long> observable2 = Observable.interval(800, TimeUnit.MILLISECONDS);
        observable1.takeUntil(observable2)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e(getLocalClassName(),aLong+""); // 输出0 1
                    }
                });

        //---skip/skipLast---//
        Observable.just(1,2,3,4,5)
                .skip(1)
                .skipLast(1);// 2 3 4

        //---ElementAt/elementAtOrDefault---//
        Observable.just(1,2,3).elementAt(1);
        Observable.just(1,2,3).elementAtOrDefault(5,5);

        //---debounce---//
        Observable.interval(1,TimeUnit.SECONDS)
                .debounce(2,TimeUnit.SECONDS);

        //---distinct---//
        Observable.just(1,2,2,1,3)
                .distinct();
        Observable.just(1,2,3,4,5)
                .distinct(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        //返回一个key，根据这个key来判断是否重复
                        return integer / 2 == 0 ? integer : 1;
                    }
                });

        //---distinctUntilChanged---//
        Observable.just(1,2,1,2,2,3,4,2)
                .distinctUntilChanged();

        //---first---//
        Observable.just(1,2,3,4)
                .first(); // 1
        Observable.just(1,2,3,4)
                .first(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer == 2;
                    }
                });

        //---last---//
        //同first

        //---delay---//
        Observable.just(1,2,3)
                .delay(2,TimeUnit.SECONDS);

    }

    private void convertOperators() {
        //---map---//
        Observable.just(1,2,3)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        return integer+1;
                    }
                });

        //---flatmap---//
        Integer[] items = {1,2,3,4,5};
        Observable.from(items)
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(Integer integer) {
                        return Observable.just(integer+"hahaha");
                    }
                });
        //---concatMap---//
        Observable.from(items)
                .concatMap(new Func1<Integer, Observable<?>>() {
                    @Override
                    public Observable<?> call(Integer integer) {
                        return Observable.just(integer+"hahaha");
                    }
                });

        //---scan---//
        Observable.just(1,2,3,4)
                .scan(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer+ integer2;
                    }
                });

        //---groupBy---//
        Observable.interval(1,TimeUnit.SECONDS).take(10)
                .groupBy(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return aLong % 3;
                    }
                }).subscribe(new Action1<GroupedObservable<Long, Long>>() {
            @Override
            public void call(final GroupedObservable<Long, Long> result) {
                result.subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        System.out.println("key:" + result.getKey() +", value:" + value);
                    }
                });
            }
        });
    }

    private void errorOperators() {
        //---retry---//
        Observable.just(1).retry(3);
        Observable.just(1).retry(new Func2<Integer, Throwable, Boolean>() {
            @Override
            public Boolean call(Integer integer, Throwable throwable) {
                // 参数 integer 是订阅的次数; 参数 throwable 是抛出的异常
                // 返回值为 true 表示重试, 返回值为 false 表示不重试
                return false;
            }
        });

        //---retryWhen---//
        Observable.just(1).retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Throwable> observable) {
                return null;
            }
        });

    }

    private void createOperators() {
        //---create---//
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hahaha");
                subscriber.onCompleted();
            }
        });
        //---just---//
        Observable.just(123);

        //---from---//
        Integer[] items = {1,2,3};
        Observable.from(items);

        //---Interval---//
        Observable.interval(1, TimeUnit.SECONDS);

        //---timer---//
        Observable.timer(2,TimeUnit.SECONDS);

        //---range---//
        Observable.range(1,3); // 1,2,3

        //---empty---//
        Observable.empty();

        //---error---//
        Observable.error(new Throwable("hahaha"));

        //---never---//
        Observable.never();

        //---repeat---//
        Observable.just(1).repeat(3);

    }
}
