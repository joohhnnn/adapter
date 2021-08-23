package com.txznet.adapter.conn.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.txznet.adapter.AdpApplication;
import com.txznet.adapter.aidl.ITXZAIDL;
import com.txznet.adapter.base.util.JsonUtil;
import com.txznet.adapter.base.util.LogUtil;
import com.txznet.adapter.base.util.SPUtil;
import com.txznet.adapter.module.InitModule;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TXZ.MarvinYang
 * 2018.0519
 * 适配与方案商代码（客户端）AIDL调用类
 * -------------------------------------------------------------------------------------------------
 * 正式稳定再写版本号
 * @version 0.0.18.0927
 * 1）增加sendCommand只发key和command的方法，默认byte[]为空；
 * -------------------------------------------------------------------------------------------------
 */
@SuppressWarnings({"FieldCanBeLocal", "unused", "DanglingJavadoc"})
public class TXZAdpManager {

    private final String TAG = "TXZAdpManager";
    /*
     * 单例
     */
    private static TXZAdpManager instance;
    /*
     * handler
     */
    private Handler handler;
    /*
     * 绑定延迟
     */
    private final int DELAY_CHECK_UUID = 3000;
    /*
     * 绑定延迟
     */
    private final int DELAY_CHECK_BIND = 3000;
    /*
     * 绑定次数上限
     */
    private final int MAX_TIME_BIND = 20;
    /*
     * 是否需要从客户端拿UUID
     */
    private final boolean ASK_UUID_FROM_CLIENT = false;
    /*
     * 存下UUID，有一个人发了就不再发
     */
    private String uuid = null;
    /*
     * 是否已经在获取uuid的逻辑中
     */
    private boolean isInUUIDRunnable = false;
    /*
     * 客户端连接池
     */
    private final HashMap<String, TXZAdapterServiceConnection> serviceMaps;

    /*
     * 私有构造
     */
    private TXZAdpManager() {
        // 开启连接池
        serviceMaps = new HashMap<>();
        // 获取looper
        handler = new Handler(AdpApplication.getInstance().getMainLooper());
    }

    /*
     * 单例模式
     *
     * @return 实例
     */
    public static TXZAdpManager getInstance() {
        if (instance == null) {
            synchronized (TXZAdpManager.class) {
                if (instance == null)
                    instance = new TXZAdpManager();
            }
        }
        return instance;
    }

    /*
     * 初始化ServiceManager
     * 服务端去绑定客户端的Service
     * 即绑定SDK那一端
     *
     * @param context context
     */
    private void bindSDKService(Context context, String clientPackageName) {
        bindSDKService(context, clientPackageName, null);
    }

    /*
     * 初始化ServiceManager
     * 服务端去绑定客户端的Service
     * 即绑定SDK那一端
     *
     * @param context context
     */
    private void bindSDKService(Context context, String clientPackageName, Runnable runnable) {
        // 发起对端的绑定
        Intent service = new Intent(clientPackageName + ".StartService");
        service.setPackage(clientPackageName);
        // 把我的包名发给它
        service.putExtra("package", context.getPackageName());
        context.bindService(service, new TXZAdapterServiceConnection(clientPackageName), Context.BIND_AUTO_CREATE);
        LogUtil.d(TAG, "Pre to bind :" + clientPackageName);
        // 检测是否绑定成功
        // 首次绑定，new 一个
        if (runnable == null) {
            runOnUIGroundRunnable(new CheckServiceBindRunnable(clientPackageName), DELAY_CHECK_BIND);
        }
        // 仍然跑之前的runnable
        else {
            runOnUIGroundRunnable(runnable, DELAY_CHECK_BIND);
        }
    }

    /*
     * 与Client的连接
     */
    private class TXZAdapterServiceConnection implements ServiceConnection {

        /*
         * 当前连接的包名
         */
        private String serviceName;

        /*
         * 接口引用
         */
        private ITXZAIDL adapterAIDL;

        private TXZAdapterServiceConnection(String serviceName) {
            this.serviceName = serviceName;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "Bind success : " + serviceName);
            // 存下引用
            adapterAIDL = ITXZAIDL.Stub.asInterface(service);
            // 锁住
            synchronized (serviceMaps) {
                // 如果重连，删掉之前的，目前测试如果不移除可能会引起多次绑定的问题。主要是可能存在
                TXZAdapterServiceConnection connection = serviceMaps.remove(serviceName);
                if (connection != null) {
                    AdpApplication.getInstance().unbindService(connection);
                }
                // push进去
                serviceMaps.put(serviceName, TXZAdapterServiceConnection.this);
            }
            // 检查一下是否需要从客户端获取uuid
            if (ASK_UUID_FROM_CLIENT) {
                if (isInUUIDRunnable) {
                    LogUtil.d(TAG, "uuid Runnable is running");
                } else {
                    runOnUIGroundRunnable(checkUUIDRunnable);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "Disconnect service : " + serviceName);
            // 先锁住
            synchronized (serviceMaps) {
                // 从池中删除对象
                TXZAdapterServiceConnection connection = serviceMaps.remove(serviceName);
                if (connection != null) {
                    AdpApplication.getInstance().unbindService(connection);
                }
            }
            // 重新绑定
            runOnUIGroundRunnable(new CheckServiceBindRunnable(serviceName));
        }

        @Override
        public void onBindingDied(ComponentName name) {
            LogUtil.d(TAG, "Service : " + serviceName + " has died");
            // 先锁住
            synchronized (serviceMaps) {
                // 从池中删除对象
                TXZAdapterServiceConnection connection = serviceMaps.remove(serviceName);
                if (connection != null) {
                    AdpApplication.getInstance().unbindService(connection);
                }
            }
            // 重新绑定
            runOnUIGroundRunnable(new CheckServiceBindRunnable(serviceName));
        }
    }

    /*
     * 循环检查是否绑定SDK那边的Service
     */
    private class CheckServiceBindRunnable implements Runnable {

        /*
         * 留下包名
         */
        private String serviceName;
        /*
         * 连接计数
         */
        private int connectTime = 0;

        private CheckServiceBindRunnable(String serviceName) {
            this.serviceName = serviceName;
        }

        @Override
        public void run() {
            removeUIGroundRunnable(this);
            synchronized (serviceMaps) {
                if (serviceMaps.containsKey(serviceName)) {
                    LogUtil.d(TAG, "ServiceName :" + serviceName + " Has bind ,Remove code");
                } else {
                    // 防止超时，只连20次
                    if (connectTime > MAX_TIME_BIND) {
                        LogUtil.d(TAG, "ServiceName :" + serviceName + " is out of " + MAX_TIME_BIND + " times ,won't bind");
                    }
                    // 未连接上
                    else {
                        // 计数
                        connectTime++;
                        // 继续连接
                        bindSDKService(AdpApplication.getInstance(), serviceName, this);
                    }
                }
            }
        }
    }

    /*
     * runnable:循环检测UUID
     */
    private Runnable checkUUIDRunnable = new Runnable() {
        @Override
        public void run() {
            isInUUIDRunnable = true;
            removeUIGroundRunnable(this);
            if (uuid == null) {
                // 再给客户端发一个UUID请求
                askForUuidFromSDK();
                // 重新检查UUID，
                if (uuid == null) {
                    runOnUIGroundRunnable(checkUUIDRunnable, DELAY_CHECK_UUID);
                }
                // 获取成功
                else {
                    isInUUIDRunnable = false;
                    LogUtil.d(TAG, "UUID Get Success : " + uuid);
                }
            } else {
                isInUUIDRunnable = false;
                LogUtil.d(TAG, "UUID Has Obtain : " + uuid);
            }
        }
    };


    /*
     * 从客户端要UUID
     */
    private void askForUuidFromSDK() {
        // 如果当前uuid已经不为空，则不处理
        if (uuid != null) {
            LogUtil.d(TAG, "Uuid is't null  : " + uuid + " Remove code");
            return;
        }
        // 发送指令给client
        byte[] data = sendCommandToAll(1400, "system.uuid", new byte[0], true);
        this.uuid = JsonUtil.getStringFromJson("uuid", data, null);
        SPUtil.putString(AdpApplication.getInstance(), SPUtil.KEY_UUID, this.uuid);
        InitModule.getInstance().init();
    }

    /*
     * 主线程跑
     * @param runnable 内容
     */
    private void runOnUIGroundRunnable(Runnable runnable) {
        handler.post(runnable);
    }

    /*
     * 主线程跑 延迟
     * @param runnable 内容
     * @param delay    延迟
     */
    private void runOnUIGroundRunnable(Runnable runnable, int delay) {
        handler.postDelayed(runnable, delay);
    }

    /*
     * 移除主线程跑
     * @param runnable 内容
     */
    private void removeUIGroundRunnable(Runnable runnable) {
        handler.removeCallbacks(runnable);
    }

    /** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ***
     *|-----------------------------------------------------------------------------------------*|
     *|   看这里                                                                                *|
     *|   全部的接收                                                                            *|
     *|-----------------------------------------------------------------------------------------*|
     ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ***/

    /**
     * 发送消息给TXZ Adapter
     * 需要返回值
     * 只调用到最先返回值的
     *
     * @param keyType 消息
     * @param command 类型
     */
    public void sendCommandToAll(int keyType, String command) {
        sendCommandToAll(keyType, command, null, false);
    }

    /**
     * 发送消息给TXZ Adapter
     * 需要返回值
     * 只调用到最先返回值的
     *
     * @param keyType 消息
     * @param command 类型
     * @param data    数据
     */
    public void sendCommandToAll(int keyType, String command, byte[] data) {
        sendCommandToAll(keyType, command, data, false);
    }

    /**
     * 发送消息给TXZ Adapter
     * 需要返回值
     * 自行取要不要返回值
     *
     * @param keyType      消息
     * @param command      类型
     * @param data         数据
     * @param needBackData 是否需要返回值
     */
    public byte[] sendCommandToAll(int keyType, String command, byte[] data, boolean needBackData) {
        Log.d(TAG, "sendCommandToAll: 11111111111");
        // 遍历
        for (Map.Entry<String, TXZAdapterServiceConnection> entry : serviceMaps.entrySet()) {
            try {
                // 需要返回值的
                if (needBackData) {
                    byte[] backData = entry.getValue().adapterAIDL.sendInvoke(AdpApplication.getInstance().getPackageName(), keyType, command, data);
                    // 给了返回值 ，但是无效（说明他没有拦截）
                    if (backData == null || backData == new byte[0]) {
                        LogUtil.d(TAG, "The back data == null , continue");
                    }
                    // 有人给了返回值
                    else {
                        LogUtil.d(TAG, entry.getValue().serviceName + " reply the info");
                        return backData;
                    }
                }
                // 不需要返回值的
                else {
                    entry.getValue().adapterAIDL.sendInvoke(AdpApplication.getInstance().getPackageName(), keyType, command, data);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 耗时的操作需要使用此方法请求
     *
     * @param keyType  key
     * @param command  command
     * @param data     data
     * @param callBack callback
     */
    public void sendCommandToAll(int keyType, String command, byte[] data, CallBack callBack) {
        new Thread(() -> {
            // 遍历
            for (Map.Entry<String, TXZAdapterServiceConnection> entry : serviceMaps.entrySet()) {
                try {
                    // 需要返回值的
                    byte[] backData = entry.getValue().adapterAIDL.sendInvoke(AdpApplication.getInstance().getPackageName(), keyType, command, data);
                    // 给了返回值 ，但是无效（说明他没有拦截）
                    if (backData == null || backData == new byte[0]) {
                        LogUtil.d(TAG, "The back data == null , continue");
                    }
                    // 有人给了返回值
                    else {
                        LogUtil.d(TAG, entry.getValue().serviceName + " reply the info");
                        callBack.callBack(backData);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface CallBack {
        void callBack(byte[] data);
    }

    /**
     * 发送消息给指定包名TXZ Adapter
     *
     * @param keyType 消息
     * @param command 类型
     * @param data    数据
     */
    public byte[] sendCommandToService(String serviceName, int keyType, String command, byte[] data) {
        for (Map.Entry<String, TXZAdapterServiceConnection> entry : serviceMaps.entrySet()) {
            if (serviceName.equals(entry.getKey())) {
                try {
                    return entry.getValue().adapterAIDL.sendInvoke(AdpApplication.getInstance().getPackageName(), keyType, command, data);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        LogUtil.d(TAG, "didn't find the serviceName :" + serviceName);
        return null;
    }

    /**
     * 发送消息给指定包名
     *
     * @param keyType 消息
     * @param command 类型
     * @param data    数据
     */
    public void sendCommandToService(String serviceName, int keyType, String command, byte[] data, CallBack callBack) {
        new Thread(() -> {
            for (Map.Entry<String, TXZAdapterServiceConnection> entry : serviceMaps.entrySet()) {
                if (serviceName.equals(entry.getKey())) {
                    try {
                        byte[] dt = entry.getValue().adapterAIDL.sendInvoke(AdpApplication.getInstance().getPackageName(), keyType, command, data);
                        callBack.callBack(dt);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            LogUtil.d(TAG, "didn't find the serviceName :" + serviceName);
        }).start();

    }

    /**
     * 收取来自客户端的消息
     *
     * @param key     key
     * @param command 命令
     * @param data    内容
     * @return 值
     */
    public byte[] onCommandReceive(String packageName, int key, String command, byte[] data) {
        LogUtil.d(TAG, "packageName : " + packageName + "  keyType: " + key + "  command: " + command + " data " + new String(data == null ? new byte[0] : data));
        // 获取对端的包名，并绑定
        if ("txz.service.info".equals(command)) {
            String serviceName = JsonUtil.getStringFromJson("package", data, null);
            bindSDKService(AdpApplication.getInstance(), serviceName);
            return null;
        }
        return TXZClientMessageProcess.getInstance().processMessage(key, command, data);
    }

}
