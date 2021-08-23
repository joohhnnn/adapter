package com.txznet.adapter.base.util;

import android.util.Log;

/**
 * Created by MarvinYang on 2018/2/9.
 * LOG管理类
 */

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal", "unused"})
public class LogUtil {

    private static final String TAG = "marvinTest";

    public static void d(Object object) {
        d(TAG, object);
    }

    public static void d(String tag, Object object) {
        Log.d(tag, object.toString());
    }

    public static void e(Object object) {
        e(TAG, object);
    }

    public static void e(String tag, Object object) {
        Log.e(tag, object.toString());
    }

}