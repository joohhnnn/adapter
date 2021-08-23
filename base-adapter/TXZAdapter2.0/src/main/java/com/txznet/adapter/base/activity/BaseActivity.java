package com.txznet.adapter.base.activity;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;

import com.txznet.adapter.base.util.LogUtil;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    protected final String TAG = getClass().getSimpleName();

    protected void log(Object o) {
        log(TAG, o);
    }

    protected void log(String TAG, Object o) {
        LogUtil.d(TAG, o);
    }

}
