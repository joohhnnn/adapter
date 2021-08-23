package com.txznet.adapter.conn.receiver.resolve;

import android.content.Intent;

import com.txznet.adapter.base.BaseBroadcastResolve;

public class BroadcastResolveForAIDL extends BaseBroadcastResolve {

    private static BroadcastResolveForAIDL instance;

    private BroadcastResolveForAIDL() {
    }

    public static BroadcastResolveForAIDL getInstance() {
        if (instance == null) {
            synchronized (BroadcastResolveForVersion1.class) {
                if (instance == null)
                    instance = new BroadcastResolveForAIDL();
            }
        }
        return instance;
    }

    @Override
    public void resolveIntentMessage(Intent intent) {

    }
}
