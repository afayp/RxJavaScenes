package com.pfh.rxjavascenes.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.pfh.rxjavascenes.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 多级缓存
 */

public class CacheActivity extends AppCompatActivity {

    private CheckBox cb_memory;
    private CheckBox cb_disk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        Button btn_start = (Button) findViewById(R.id.btn_start);
        cb_memory = (CheckBox) findViewById(R.id.cb_memory);
        cb_disk = (CheckBox) findViewById(R.id.cb_disk);
        final TextView tv_result = (TextView) findViewById(R.id.tv_result);

        RxView.clicks(btn_start)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        tv_result.setText("获取数据中...");
                        Log.e("TAG","doOnNext");
                    }
                })
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Observable.concat(getDataFromMemory(),getDataFromDisk(),getDataFromNetWork())
                                .first()
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String s) {
                                        tv_result.setText(s);
                                    }
                                });
                    }
                });
    }

    private Observable<String> getDataFromMemory(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (cb_memory.isChecked()) {
                    subscriber.onNext("获取数据成功！来自内存缓存");
                } else {
                    subscriber.onCompleted();
                }
            }
        }).delay(1,TimeUnit.SECONDS);
    }

    private Observable<String> getDataFromDisk(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (cb_disk.isChecked()) {
                    subscriber.onNext("获取数据成功！来自硬盘缓存");
                }else {
                    subscriber.onCompleted();
                }
            }
        }).delay(1,TimeUnit.SECONDS);
    }

    private Observable<String> getDataFromNetWork(){
        return Observable.just("获取数据成功！来自网络").delay(3,TimeUnit.SECONDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
