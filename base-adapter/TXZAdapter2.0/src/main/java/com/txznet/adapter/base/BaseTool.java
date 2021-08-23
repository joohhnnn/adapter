package com.txznet.adapter.base;

import com.txznet.adapter.base.util.LogUtil;

/**
 * 基类工具
 * Created by MarvinYang on 2018/3/15.
 */

public class BaseTool implements VersionInfo {

    protected final String TAG = getClass().getSimpleName();

    protected void log(Object o) {
        log(TAG, o);
    }

    protected void log(String TAG, Object o) {
        LogUtil.d(TAG, o);
    }

    protected static final int CUR_PROTO_INFO = PROTO_VERSION;

}
