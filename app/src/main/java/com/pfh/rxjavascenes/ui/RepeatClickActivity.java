package com.pfh.rxjavascenes.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.pfh.rxjavascenes.R;

import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 使用throttleFirst防止按钮重复点击
 */

public class RepeatClickActivity extends AppCompatActivity {
    int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_click);
        Button btn_click = (Button) findViewById(R.id.btn_click);
        RxView.clicks(btn_click)
                .throttleFirst(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {
                        count++;
                        Toast.makeText(RepeatClickActivity.this,"成功点击次数"+count,Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
