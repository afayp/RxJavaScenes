package com.pfh.rxjavascenes.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pfh.rxjavascenes.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
