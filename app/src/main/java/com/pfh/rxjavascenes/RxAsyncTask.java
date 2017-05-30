package com.pfh.rxjavascenes;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * 异步任务rx实现，替代asyncTask
 * 参考 http://www.developersite.org/905-199836-RxJava%E5%BC%82%E6%AD%A5%E4%BB%BB%E5%8A%A1
 * http://blog.csdn.net/relicemxd/article/details/52623003
 * https://github.com/lzyzsd/Awesome-RxJava/issues/9
 * https://github.com/hehonghui/android-tech-frontier/blob/master/issue-34/%E5%9C%A8Android%E5%BC%80%E5%8F%91%E4%B8%AD%E4%BD%BF%E7%94%A8RxJava.md
 *
 * @param <Param>  参数
 * @param <Result> 结果
 */
public abstract class RxAsyncTask<Param, Result> {

    public Subscription execute(final Param... params) {
        return Observable.create(new Observable.OnSubscribe<Result>() {
            @Override
            public void call(Subscriber<? super Result> subscriber) {
                subscriber.onNext(doInBackground(params));
                subscriber.onCompleted(); // 注意要调用onCompleted(或者onError),否则不会反注册
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        RxAsyncTask.this.onPreExecute();
                    }
                })
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onCompleted() {
                        RxAsyncTask.this.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        RxAsyncTask.this.onError(e);
                    }

                    @Override
                    public void onNext(Result result) {
                        RxAsyncTask.this.onResult(result);
                    }
                });

    }

    protected abstract Result doInBackground(Param... params);

    /**
     * 任务开始之前调用(在当前调用者所在线程执行)
     */
    protected void onPreExecute() {
    }

    /**
     * 执行结果返回
     */
    protected void onResult(Result result) {
    }

    /**
     * RxJava中的onComplete回调
     */
    protected void onCompleted() {
    }

    /**
     * RxJava中的onError回调
     */
    protected void onError(Throwable e) {
    }

}
