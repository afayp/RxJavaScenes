package com.pfh.rxjavascenes.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pfh.rxjavascenes.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class NoUIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_ui_activity);

        Observable.timer(3, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        // 3秒延时后执行的操作
                    }
                });
    }

    /**
     * 数据来自多个数据源（请求）,用merge合并
     */
    public void mergeDataFromDifferentSource(){
        ArrayList<String> data1 = new ArrayList<>();
        ArrayList<Integer> data2 = new ArrayList<>();

        Observable<ArrayList<String>> observable1 = Observable.just(data1);
        Observable<ArrayList<Integer>> observable2 = Observable.just(data2);

        Observable.merge(observable1,observable2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<? extends Serializable>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<? extends Serializable> serializables) {
                        // do something
                    }
                });
    }

    public void TimingTask(){
        Observable.timer(3,TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {

                    }
                });
    }

    public void CycleTask(){
        Observable.interval(3,TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {

                    }
                });
    }

    public void dataFilter(){
        Observable.just(1,2,3)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer == 1;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        
                    }
                });
    }
}
