package com.pfh.rxjavascenes.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.pfh.rxjavascenes.R;

import rx.Observable;
import rx.Observer;
import rx.functions.Func2;

/**
 * 使用combineLatest合并最近N个Observable,只有每个Observable都满足条件时才执行操作
 */

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText et_username = (EditText) findViewById(R.id.et_username);
        final EditText et_password = (EditText) findViewById(R.id.et_password);
        final Button btn_login = (Button) findViewById(R.id.btn_login);

        Observable<CharSequence> usernameObservable = RxTextView.textChanges(et_username);
        Observable<CharSequence> passwordObservable = RxTextView.textChanges(et_password);
        Observable.combineLatest(usernameObservable, passwordObservable, new Func2<CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean call(CharSequence username, CharSequence password) {
                return !TextUtils.isEmpty(username.toString()) && !TextUtils.isEmpty(password.toString());
            }
        }).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                btn_login.setVisibility(aBoolean ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }
}
