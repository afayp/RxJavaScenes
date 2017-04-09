package com.pfh.rxjavascenes.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.pfh.rxjavascenes.R;

import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * 使用debounce减少频繁的网络请求。避免每输入（删除）一个字就做一次联想
 */

public class TextSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_search);
        EditText et_search = (EditText) findViewById(R.id.et_search);
        final TextView tv_result = (TextView) findViewById(R.id.tv_result);
        RxTextView.textChangeEvents(et_search)
                .debounce(1, TimeUnit.SECONDS)
                .filter(new Func1<TextViewTextChangeEvent, Boolean>() {
                    @Override
                    public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                        return !TextUtils.isEmpty(textViewTextChangeEvent.text().toString()); // 空字符不发射
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TextViewTextChangeEvent>() {
                    @Override
                    public void onCompleted() {
                        tv_result.setText(tv_result.getText().toString() + "\n" +"搜索完毕");
                    }

                    @Override
                    public void onError(Throwable e) {
                        tv_result.setText(tv_result.getText().toString() + "\n" +"搜索出错");
                    }

                    @Override
                    public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                        tv_result.setText(tv_result.getText().toString() + "\n" +"搜索"+textViewTextChangeEvent.text());
                    }
                });

    }
}
