package com.txznet.adapter.base;

import com.txznet.adapter.base.util.LogUtil;

/**
 * Created by MarvinYang on 2018/2/9.
 * 管理类基类
 */

@SuppressWarnings("unused")
public abstract class BaseModule {

    protected final String TAG = getClass().getSimpleName();

    protected void log(Object o) {
        log(TAG, o);
    }

    protected void log(String TAG, Object o) {
        LogUtil.d(TAG, o);
    }

    public abstract void init();

}
