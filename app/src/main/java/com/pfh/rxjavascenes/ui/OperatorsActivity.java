package com.pfh.rxjavascenes.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;

/**
 * 操作符
 */

public class OperatorsActivity extends AppCompatActivity {

    private String TAG = "RxJava";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createOperators();
        errorOperators();
        convertOperators();
        filterOperators();
        combineOperators();
        InspectionOperators();
        utilityOperators();
    }

    private void utilityOperators() {
        /**
         * do系列
         * doOnEach：为 Observable注册这样一个回调，当Observable每发射一项数据就会调用它一次，包括onNext、onError和 onCompleted
         * doOnNext：只有执行onNext的S时候会被调用
         * doOnSubscribe： 当观察者订阅Observable时就会被调用
         * doOnUnSubscribe： 当观察者取消订阅Observable时就会被调用；Observable通过onError或者onCompleted结束时，会反订阅所有的Subscriber
         * doOnCompleted：当Observable 正常终止调用onCompleted时会被调用。
         * doOnError： 当Observable 异常终止调用onError时会被调用。
         * doOnTerminate： 当Observable 终止之前会被调用，无论是正常还是异常终止
         * finallyDo： 当Observable 终止之后会被调用，无论是正常还是异常终止。
         */

        //---timestamp---// 把数据转换为 Timestamped 类型，里面包含了原始的数据和一个原始数据是何时发射的时间戳。
        Observable.range(0,5)
                .timestamp();
        //---timeInterval---//一个数据和当前数据发射直接的时间间隔
        Observable.interval(100,TimeUnit.MILLISECONDS)
                .timeInterval();

        //---timeout---//
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i <= 3; i++) {
                    try {
                        Thread.sleep(i * 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).timeout(200, TimeUnit.MILLISECONDS, Observable.just(10,20)); //发射数据时间间隔超过200ms超时,超时后开启备用Observable
    }

    private void InspectionOperators() {
        //---all---//判断 observable 中发射的所有数据是否都满足一个条件。只要遇到一个不满足条件的数据，all 函数就立刻返回 false。
        // 只有当源 Observable 结束发射并且所发射的所有数据都满足条件的时候才会产生 true。
        Observable.just(1,2,3,4)
                .all(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer < 4;
                    }
                });
        //---exists---// 有一个满足条件，则 exists 就返回 true
        Observable.just(1,2,3,4)
                .exists(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer < 4;
                    }
                });

        //---isEmpty---// 只要源 Observable 发射了一个数据，isEmpty 就立刻返回 false， 只有当源 Observable 完成了并且没有发射数据，isEmpty 才返回 true。

        //---contains---// contains 使用 Object.equals 函数来判断源 Observable 是否发射了相同的数据。只要遇到相同的数据，则 contains 就立刻返回
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .contains(4L); // 这里注意用4L
    }

    private void combineOperators() {
        //---merge---//
        /**
         * 混着发射，无序,可能这个Observable发射一个，那个Observable发射两个(按照事件产生的顺序发送给订阅者)。但是最后会把所有Observable发射完。
         * 类似的有mergeDelayError
         */
        final String[] letters = new String[]{"A","B","C","D"};
        Observable<String> letterObservable = Observable.interval(300, TimeUnit.MILLISECONDS)
                .take(letters.length)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        return letters[aLong.intValue()];
                    }
                });
        Observable<Long> numberObservable = Observable.interval(500, TimeUnit.MILLISECONDS).take(5);
        Observable.merge(letterObservable,numberObservable)
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        Log.e("TAG",serializable.toString()+""); // A 0 B C 1 D 2 3 4
                    }
                });

        /**
         * concat 和 merge 的区别是，merge 不会等到前面一个 Observable 结束才会发射下一个 Observable 的数据，
         * 而merge 订阅到所有的 Observable 上，如果有任何一个 Observable 发射了数据，就把该数据发射出来。
         * concat是有序的，先发射完第一个Observable，再发射第二个Observable。若中间发生错误，则调用onError结束执行，不再发射后面的数据
         */
        Observable.concat(letterObservable,numberObservable);
        //---concatWith---// 可以通过串联的方法来一个一个的组合数据流
        numberObservable.concatWith(Observable.interval(1,TimeUnit.SECONDS).take(10));
        /**
         * concat 类似的还有：
         * concatDelayError:若其中的某个Observable发生错误，则继续执行完后面的Observable，将发生错误的Observable放在最后。
         * concatEager：缓存每个Observable发射出来的值，所有Observable发射完成之后，再将所有缓存的值按顺序一次性排放出来。
         */

        //---zip zipWith---//
        //按照顺序先从第一个Observable取出一个值，再从第二个Observable取出一个值，将两个值根据函数组合；如果中途一个Observable发射完了，直接onCompleted。
        Observable.zip(letterObservable, numberObservable, new Func2<String, Long, String>() {
            @Override
            public String call(String s, Long aLong) {
                return s + aLong;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG,""+s); // A0 B1 C2 D3 onCompleted!
            }
        });

        //---combineLatest---//
        //CombineLatest操作符行为类似于zip，但是zip只有当原始的Observable中的每一个都发射了一条数据时才发射数据。
        //CombineLatest则在原始的Observable中任意一个发射了数据时就发射一条数据。
        //当原始Observables的任何一个发射了一条数据时，CombineLatest使用一个函数结合它们最近发射的数据，然后发射这个函数的返回值。


        //----startWith---//
        Observable.just(1,2,3)
                .startWith(-1,0);

        //---amb ambWith---// 参数为多个 Observable，使用第一个先发射数据的 Observable ，其他的 Observable 被丢弃。
        Observable<String> first = Observable.timer(1, TimeUnit.SECONDS).map(new Func1<Long, String>() {
            @Override
            public String call(Long aLong) {
                return "first";
            }
        });
        Observable<String> second = Observable.timer(1, TimeUnit.SECONDS).map(new Func1<Long, String>() {
            @Override
            public String call(Long aLong) {
                return "first";
            }
        });
        Observable.amb(first,second);
        first.ambWith(second);

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
                        Log.e("TAG",aLong+""); // 输出0 1
                    }
                });

        //---skip/skipLast---//
        Observable.just(1,2,3,4,5)
                .skip(1)
                .skipLast(1);// 2 3 4

        //---ElementAt/elementAtOrDefault---//
        Observable.just(1,2,3).elementAt(1);
        Observable.just(1,2,3).elementAtOrDefault(5,5);

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

        //---distinctUntilChanged---// 只过滤相邻的 key 一样的数据。
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

        //---sample---// 把一个数据流分割为一个一个的时间窗口，当每个时间窗口结束的时候，发射该时间窗口中的最后一个数据。
        Observable.interval(150, TimeUnit.MILLISECONDS)
                .sample(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e(TAG,": "+aLong);
                    }
                });

        //---throttleFirst---// throttleFirst 操作函数接收到第一个数据后，就开启一个时间窗口，在规定的时间窗口内发射第一个数据，后面的数据丢弃直到时间窗口结束。当时间窗口结束后，下一个数据发射后将开启下一个时间窗口。
        Observable.interval(150, TimeUnit.MILLISECONDS)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e(TAG,": "+aLong);
                    }
                });
        //---throttleLast---//与throttleFirst相反

        //---debounce---// 当一个数据发射的时候，就开始一个时间窗口计时，当这个时间窗口结束了还没有新的数据发射，则就发射这数据。如果在这个时间窗口内，又发射了一个新的数据，则当前数据丢弃，从新开始时间窗口计时。
        Observable.interval(1,TimeUnit.SECONDS).take(2)
                .debounce(2,TimeUnit.SECONDS);

        //---count---// 用来统计源 Observable 完成的时候一共发射了多少个数据。类似的有countLong
        Observable.range(1,5)
                .count();

        //---single---// 检查数据流中是否有且仅有一个符合条件的数据,如果有发射它，如果没有则走error
        Observable.interval(100, TimeUnit.MILLISECONDS).take(10)
        .single(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                return aLong == 4L;
            }
        })
        .subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Log.e(TAG,"result: "+aLong);
            }
        });


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

        //---reduce---//
        Observable.just(1,2,3,4)
                .reduce(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer sum, Integer integer2) {
                        return sum+ integer2;
                    }
                });

        //---scan(accumulator 蓄能器)---// scan 和 reduce 很像，不一样的地方在于 scan会发射所有中间的结算结果。
        Observable.just(1,2,3,4)
                .scan(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer sum, Integer integer2) {
                        return sum+ integer2;
                    }
                });

        /** groupBy
         * 将原始Observable发射的数据按照key来拆分成一些小的Observable，然后这些小的Observable分别发射其所包含的的数据，类似于sql里面的groupBy。
         * 我们需要提供一个生成key的规则，所有key相同的数据会包含在同一个小的Observable种。另外我们还可以提供一个函数来对这些数据进行转化
         */
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
                            public void call(Long value) {
                                Log.e(TAG,"key:" + result.getKey() +", value:" + value);
                            }
                        });
                    }
                });
        //---buffer---// buffer 可以收集数据并缓存起来，等缓存到固定的数目后一起发射(生成一个List缓存)，而不是来一个发射一个。
        //按个数缓存
        Observable.range(0, 10)
                .buffer(4)
                .subscribe(new Action1<List<Integer>>() {
                    @Override
                    public void call(List<Integer> integers) {
                        Log.e(TAG,": "+integers.toString());
                    }
                });
        //按时间缓存
        Observable.interval(100, TimeUnit.MILLISECONDS).take(10)
                .buffer(250, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<List<Long>>() {
                    @Override
                    public void call(List<Long> longs) {
                        Log.e(TAG,": "+longs.toString());
                    }
                });
        //同时用数目和时间作为缓冲条件，任意一个条件满足了（缓冲的个数达到了或者当前时间窗口结束了），就发射缓冲到的数据。
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(10)
                .buffer(250, TimeUnit.MILLISECONDS, 2)
                .subscribe(new Action1<List<Long>>() {
                    @Override
                    public void call(List<Long> longs) {
                        Log.e(TAG,": "+longs.toString());
                    }
                });
        //还可以指定间隔多少个数据开始下一个缓冲
        Observable.range(0,10)
                .buffer(4, 3) //每隔 3 个数据开始一个缓冲，每次缓冲 4 个数据(有重叠)
                .subscribe(new Action1<List<Integer>>() {
                    @Override
                    public void call(List<Integer> integers) {
                        Log.e(TAG,": "+integers.toString());
                    }
                });

        //---window---//
        //window操作符非常类似于buffer操作符，区别在于buffer操作符产生的结果是一个List缓存，而window操作符产生的结果是一个Observable，订阅者可以对这个结果Observable重新进行订阅处理。
        Observable.interval(1, TimeUnit.SECONDS).take(12)
                .window(3, TimeUnit.SECONDS)
                .subscribe(new Action1<Observable<Long>>() {
                    @Override
                    public void call(Observable<Long> observable) {
                        Log.e(TAG,"begin...");
                        observable.subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                Log.e(TAG,"next: "+aLong);
                            }
                        });
                    }
                });

        //---toList---//
        Observable.range(0,5)
                .toList();
        //---toSortedList---//
        Observable.range(0,5)
                .toSortedList(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer i1, Integer i2) {
                        return i2 - i1;
                    }
                });

        //---cast---//
        Observable.just(1,2,3,4,5)
                .cast(Integer.class); // 如果强转失败则会error
        //---ofType---//
        Observable.just(1,2,3,"4","5",6)
                .ofType(Integer.class);
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

        //----defer---// 直到有观察者订阅时才创建Observable，并且为每个观察者创建一个新的Observable
        Observable<Long> now = Observable.defer(new Func0<Observable<Long>>() {
            @Override
            public Observable<Long> call() {
                return Observable.just(System.currentTimeMillis());
            }
        });
        now.subscribe();
        //Thread.sleep(1000);
        now.subscribe();


    }
}
