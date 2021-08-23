package com.txznet.adapter.conn.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;

import com.txznet.adapter.aidl.ITXZAIDL;


/**
 * @author TXZ.MarvinYang
 * TXZAdapter 与外部通信的AIDL
 */
public class TXZAdpService extends Service {

    private final String TAG = "TXZASService";

    @Override
    public IBinder onBind(Intent intent) {
        // 接收来自jar包调用 的包名，然后主动去绑定它
        /*String name = intent.getStringExtra("package");
        Log.d(TAG, name + " :bind adapter");
        if (name == null) {
            Log.d(TAG, " the Client didn't send package name,but bind the ClientService");
        } else {
            Log.d(TAG, "the Client package name : " + name);
        }
        TXZAdpManager.getInstance().bindAdapterService(AdpApplication.getInstance(), name);*/
        // 避免有问题
        // 实践证明只一个Binder，new 不new都只有一个，系统缓存了，暂时不修改
        return new TXZAdpBinder();
    }

    /*
     * 实现接口
     * 接收来自客户端的请求：Jar包调用
     * 给客户端调用的接口句柄
     */
    private class TXZAdpBinder extends ITXZAIDL.Stub {

        @Override
        public byte[] sendInvoke(String packageName, int commandKey, String command, byte[] data) {
            synchronized (this) {
                return TXZAdpManager.getInstance().onCommandReceive(packageName, commandKey, command, data);
            }
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            try {
                return super.onTransact(code, data, reply, flags);
            } catch (Exception ignore) {
                return false;
            }
        }
    }

}
