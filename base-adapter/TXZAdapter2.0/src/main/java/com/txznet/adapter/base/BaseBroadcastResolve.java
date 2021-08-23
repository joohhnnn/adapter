package com.txznet.adapter.base;

import android.content.Intent;

import com.txznet.adapter.base.util.LogUtil;

/**
 * 协议解析基类
 * Created by MarvinYang on 2018/3/15.
 */

public abstract class BaseBroadcastResolve {

    protected final String TAG = getClass().getSimpleName();

    protected void log(Object o) {
        log(TAG, o);
    }

    protected void log(String TAG, Object o) {
        LogUtil.d(TAG, o);
    }
    public abstract void resolveIntentMessage(Intent intent);

}
