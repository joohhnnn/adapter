package com.txznet.adapter.conn.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.txznet.adapter.base.VersionInfo;
import com.txznet.adapter.base.util.LogUtil;
import com.txznet.adapter.conn.receiver.resolve.BroadcastResolveForAIDL;
import com.txznet.adapter.conn.receiver.resolve.BroadcastResolveForVersion1;
import com.txznet.adapter.conn.receiver.resolve.BroadcastResolveForVersion2;

/**
 * Created by MarvinYang on 2018/2/9.
 * 广播接收类
 */

public class CenterBroadcastReceiver extends BroadcastReceiver implements VersionInfo {

    private static final String TAG = "CenterBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, " -> " + intent.getAction());
        switch (PROTO_VERSION) {
            case BROADCAST_VERSION_1_X:
                BroadcastResolveForVersion1.getInstance().resolveIntentMessage(intent);
                break;
            case BROADCAST_VERSION_2_X:
                BroadcastResolveForVersion2.getInstance().resolveIntentMessage(intent);
                break;
            case AIDL_VERSION:
                BroadcastResolveForAIDL.getInstance().resolveIntentMessage(intent);
                break;
            default:
                break;
        }
    }

}
