package com.pfh.rxjavascenes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.pfh.rxjavascenes.R;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private String TAG = "RxJava";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Integer[] items = {1,2,3,4,5};
        List<String> itemList = Arrays.asList("faj","adf","dfa","gag");


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

        Observable<String> fastLocalData = Observable.just("来自本地缓存").delay(1, TimeUnit.SECONDS);
        Observable<String> slowNetData = Observable.just("来自网络获取").delay(3, TimeUnit.SECONDS);
        Observable.concat(fastLocalData,slowNetData);

    }


    public void textSearch(View v) {
        Intent intent = new Intent(MainActivity.this, TextSearchActivity.class);
        startActivity(intent);
    }

    public void repeatClick(View v) {
        Intent intent = new Intent(MainActivity.this, RepeatClickActivity.class);
        startActivity(intent);
    }

    public void login(View v) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void cache(View v) {
        Intent intent = new Intent(MainActivity.this, CacheActivity.class);
        startActivity(intent);
    }

    public void register(View v) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
