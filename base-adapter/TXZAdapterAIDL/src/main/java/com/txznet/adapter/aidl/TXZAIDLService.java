


package com.txznet.adapter.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author TXZ.marinv.yang 2018/0505
 * 同行者适配SDK对外Service
 * 服务端
 */
public class TXZAIDLService extends Service {

    /*
     * 实现接口
     * 接收来自客户端的请求：Adapter调用
     */
    private class TXZClientAIDLBinder extends ITXZAIDL.Stub {

        @Override
        public byte[] sendInvoke(String packageName, int commandKey, String command, byte[] data) {
            synchronized (this) {
                return TXZAIDLManager.getInstance().onCommandReceive(packageName, commandKey, command, data);
            }
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            try {
                return super.onTransact(code, data, reply, flags);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

    }


    public IBinder onBind(Intent intent) {
        return new TXZClientAIDLBinder();
    }


}
