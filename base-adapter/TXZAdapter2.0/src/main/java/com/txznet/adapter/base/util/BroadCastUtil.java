package com.txznet.adapter.base.util;

import android.content.Intent;
import android.util.Log;

import com.txznet.adapter.AdpApplication;

import java.util.Arrays;

/**
 * 广播发送工具
 * Created by MarvinYang on 2018/3/15.
 */

@SuppressWarnings("WeakerAccess")
public class BroadCastUtil {
    private static final String TAG = "BroadCastUtil";
    private static final String BROADCAST_ACTION_SEND = "com.txznet.adapter.send";
    private static final String KEY_TYPE = "key_type";

    private static void sendBroadCastIntent(Intent intent) {
        AdpApplication.getInstance().sendBroadcast(intent);
    }

    public static void sendBroadCast(String action, String keyType, int type, Object... o) {
        if (o == null) {
            LogUtil.d(TAG, "sendBroadCast(int key, Object... o) : 0 = null");
            return;
        }
        if (o.length == 0) {
            LogUtil.d(TAG, "sendBroadCast(int key, Object... o) : o.length = 0");
            return;
        }
        if ((o.length % 2) != 0) {
            LogUtil.d(TAG, "sendBroadCast(int key, Object... o) : o.length % 2 != 0");
            return;
        }
        if (action == null) {
            LogUtil.d(TAG, "sendBroadCast(int key, Object... o) : action = null");
            return;
        }
        Intent intent = new Intent(action);
        intent.putExtra(keyType, type);
        for (int i = 0; i < o.length; i += 2) {
            // 确认是否
            if (!(o[i] instanceof String)) {
                LogUtil.d(TAG, "sendBroadCast(int key, Object... o) : the o[" + i + "] is not String key");
                return;
            }
            // Integer
            if (o[i + 1] instanceof Integer) {
                intent.putExtra((String) o[i], (Integer) o[i + 1]);
            }
            // Boolean
            else if (o[i + 1] instanceof Boolean) {
                intent.putExtra((String) o[i], (Boolean) o[i + 1]);
            }
            // Byte
            else if (o[i + 1] instanceof Byte) {
                intent.putExtra((String) o[i], (Byte) o[i + 1]);
            }
            // String
            else if (o[i + 1] instanceof String) {
                intent.putExtra((String) o[i], (String) o[i + 1]);
            }
            // short
            else if (o[i + 1] instanceof Short) {
                intent.putExtra((String) o[i], (Short) o[i + 1]);
            }
            // long
            else if (o[i + 1] instanceof Long) {
                intent.putExtra((String) o[i], (Long) o[i + 1]);
            }
            // float
            else if (o[i + 1] instanceof Float) {
                intent.putExtra((String) o[i], (Float) o[i + 1]);
            }
            // char
            else if (o[i + 1] instanceof Character) {
                intent.putExtra((String) o[i], (Character) o[i + 1]);
            }
            // Double
            else if (o[i + 1] instanceof Double) {
                intent.putExtra((String) o[i], (Double) o[i + 1]);
            }
            // String[]
            else if (o[i + 1] instanceof String[]) {
                intent.putExtra((String) o[i], (String[]) o[i + 1]);
            }
            // int[]
            else if (o[i + 1] instanceof int[]) {
                intent.putExtra((String) o[i], (int[]) o[i + 1]);
            }
            // Boolean[]
            else if (o[i + 1] instanceof boolean[]) {
                intent.putExtra((String) o[i], (boolean[]) o[i + 1]);
            }
            // byte[]
            else if (o[i + 1] instanceof byte[]) {
                intent.putExtra((String) o[i], (byte[]) o[i + 1]);
            }
            // byte[]
            else if (o[i + 1] instanceof byte[]) {
                intent.putExtra((String) o[i], (byte[]) o[i + 1]);
            }
        }
        Log.d(TAG, "sendBroadCast: action:: " + action + ", keyType:: " + type + ", params:: " + Arrays.toString(o));
        sendBroadCastIntent(intent);
    }

    /**
     * 专门为适配改的发广播工具
     *
     * @param keyType key值
     * @param o       其它的
     */
    public static void sendBroadCast(int keyType, Object... o) {
        sendBroadCast(BROADCAST_ACTION_SEND, KEY_TYPE, keyType, o);
    }
}
