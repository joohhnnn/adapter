package com.txznet.adapter.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.txznet.adapter.base.util.LogUtil;

/**
 * Created by MarvinYang on 2018/2/9.
 * Application 基类
 */
@SuppressLint("Registered")
@SuppressWarnings({"unused", "SameParameterValue"})
public class BaseApplication extends Application {

    protected final String TAG = getClass().getSimpleName();

    protected void log(Object o) {
        log(TAG, o);
    }

    protected void log(String TAG, Object o) {
        LogUtil.d(TAG, o);
    }

    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    /**
     * 主线程运行Runnable
     *
     * @param task Runnable
     */

    public void runOnUiThread(Runnable task) {
        runOnUiThread(task, 0);
    }

    /**
     * 主线程运行Runnable
     *
     * @param task  Runnable
     * @param delay 延迟
     */
    public void runOnUiThread(Runnable task, long delay) {
        mUiHandler.postDelayed(task, delay);
    }

    /**
     * 主线程除掉Runnable
     *
     * @param task Runnable
     */
    public void removeOnUiThread(Runnable task) {
        mUiHandler.removeCallbacks(task);
    }

}
