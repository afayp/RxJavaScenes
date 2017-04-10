package com.pfh.rxjavascenes.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.pfh.rxjavascenes.R;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText et_phone;
    private EditText et_code;
    private Button btn_get_code;
    private Button btn_register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        btn_get_code = (Button) findViewById(R.id.btn_get_code);
        btn_register = (Button) findViewById(R.id.btn_register);

        RxTextView.textChanges(et_phone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        btn_get_code.setEnabled(!TextUtils.isEmpty(charSequence.toString()));
                    }
                });

        RxView.clicks(btn_get_code)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!isNumberLawful(et_phone.getText().toString())) {
                            Toast.makeText(RegisterActivity.this,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getCode();

                    }
                });

    }

    private void getCode() {
        // 假装发个验证码
        Observable.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Toast.makeText(RegisterActivity.this,"你的验证码是2333",Toast.LENGTH_SHORT).show();
                    }
                });

        Observable.interval(1,TimeUnit.SECONDS)
                .take(30)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        btn_get_code.setText("点击获取验证码");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        btn_get_code.setText(30-aLong.intValue()-1+"s后重新获取");
                    }
                });

    }

    private boolean isNumberLawful(String number){
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(number);
        return m.matches();
    }
}
