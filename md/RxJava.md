## cold&hot
Cold Observable:只有当有订阅者订阅的时候，才开始执行发射数据流的代码。并且每个订阅者订阅的时候都独立的执行一遍数据流代码。  
Observable.interval 就是一个 Cold Observable。每一个订阅者都会独立的收到他们的数据流。
```java
Observable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS);

cold.subscribe(i -> System.out.println("First: " + i));
Thread.sleep(500);
cold.subscribe(i -> System.out.println("Second: " + i));
```
结果：
```
First: 0
First: 1
First: 2
Second: 0
First: 3
Second: 1
First: 4
Second: 2
...
```
虽然这两个 Subscriber 订阅到同一个Observable 上，只是订阅的时间不同，他们都收到同样的数据流，但是同一时刻收到的数据是不同的。

大部分Observable都是Cold Observable。如Observable.create 创建的也是 Cold Observable，而 just, range, timer 和 from 这些创建的同样是 Cold Observable。

Hot observable 则不管有没有订阅者订阅，他们创建后就开发发射数据流。 一个比较好的示例就是 鼠标事件。 不管系统有没有订阅者监听鼠标事件，鼠标事件一直在发生，当有订阅者订阅后，从订阅后的事件开始发送给这个订阅者，之前的事件这个订阅者是接受不到的；如果订阅者取消订阅了，鼠标事件依然继续发射。


## Subject
Subject 是 Observable 的一个扩展，同时还实现了 Observer 接口。它同时充当了Observer和Observable的角色。因为它是一个Observer，它可以订阅一个或多个Observable；又因为它是一个Observable，它可以转发它收到(Observe)的数据，也可以发射新的数据。

Subject没法指定异步线程，更像是EventBus通过订阅来实现事件通知。

Subject 有各种不同的具体实现。RxJava中常见的Subject有4种，分别是 AsyncSubject、 BehaviorSubject、 PublishSubject、 ReplaySubject。

### AsyncSubject
AsyncSubject只在原始Observable完成后（onCompleted），发射来自原始Observable的最后一个值。（如果原始Observable没有发射任何值，AsyncObject也不发射任何值）它会把这最后一个值发射给任何后续的观察者。如果原始的Observable因为发生了错误而终止，AsyncSubject将不会发射任何数据，只是简单的向前传递这个错误通知。 
```java
AsyncSubject as = AsyncSubject.create();
as.onNext(1);
as.onNext(2);
as.onNext(3);
// 这里如果不complete，不会收到结果
as.onCompleted();
// 结束后，这里订阅也能收到3
as.subscribe(
        new Action1<Integer>() {
            @Override
            public void call(Integer o) {
                LogHelper.e("S:" + o);// 这里只会输出3
            }
        });
```

### BehaviorSubject
观察者订阅BehaviorSubject时，它开始发射原始Observable最近发射的数据（如果此时还没有收到任何数据，它会发射一个默认值），然后继续发射其它任何来自原始Observable的数据。 如果遇到错误会直接中断。
```java
BehaviorSubject<Integer> s = BehaviorSubject.create();
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e("TAG"," "+integer);
            }
        });
        s.onNext(3);
```

### PublishSubject
只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者。需要注意的是，PublishSubject可能会一创建完成就立刻开始发射数据（除非你可以阻止它发生），因此这里有一个风险：在Subject被创建后到有观察者订阅它之前这个时间段内，一个或多个数据可能会丢失。
```java
PublishSubject<Integer> subject = PublishSubject.create();
subject.onNext(1);
subject.subscribe(System.out::println);
subject.onNext(2);
subject.onNext(3);
subject.onNext(4);

```
### ReplaySubject

无论何时订阅，都会将所有历史订阅内容全部发出。
```java
ReplaySubject bs = ReplaySubject.create();
// 无论何时订阅都会收到1，2，3
bs.onNext(1);
bs.onNext(2);
bs.onNext(3);
bs.onCompleted();
bs.subscribe(
        new Action1<Integer>() {
            @Override
            public void call(Integer o) {
                LogHelper.e("S:" + o);
            }
        });
```

## 线程调度
由于 Rx 目标是用在异步系统上并且 Rx 支持多线程处理，所以很多 Rx 开发者认为默认情况下 Rx 就是多线程的。 其实实际情况不是这样的，Rx 默认是单线程的。
除非你明确的指定线程，否则所有 onNext/onError/onCompleted 以及各个操作函数的调用都是在同一个线程中完成的。

subscribeOn 和 observeOn 分别用来控制 subscription 的调用线程和 接受事件通知（Observer 的 onNext/onError/onCompleted 函数）的线程。

有些 Observable 会依赖一些资源，当该 Observable 完成后释放这些资源。如果释放资源比较耗时的话，可以通过 unsubscribeOn 来指定 释放资源代码执行的线程。





## RxJava1升级RxJava2

[https://toutiao.io/posts/6fpi77/preview]()
[http://www.jianshu.com/p/3ca96d96ffc0]()

## 资源
http://rxmarbles.com/