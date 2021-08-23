package com.txznet.adapter.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 类名称：同行者AIDL适配管理器
 * 类描述：用于同行者适配器与客户端的通讯管理
 *
 * @author TXZ.MarvinYang
 * 适配管理器
 */
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess", "DanglingJavadoc"})
public class TXZAIDLManager {

    private final String TAG = "TXZAIDLManager";
    /*
     * 缓存自己的包名
     */
    private String packageName = null;
    /*
     * 单例
     */
    private static TXZAIDLManager instance;
    /*
     * 监听
     */
    private TXZCommandListener txzCommandListener;
    /**
     * 默认的command
     */
    private String _command = "txz.service.info";
    /**
     * 异步的command前缀
     */
    private String _async = "async.";

    /*
     * context持有
     */
    private MyContext myContext;
    /*
     * handler
     */
    private Handler handler = new Handler(Looper.getMainLooper());
    /*
     * 绑定延迟
     */
    private final int serviceBindDelay = 3000;

    /*
     * 循环绑定程序
     */
    private Runnable checkServiceBindRunnable;

    /*
     * 是否处理绑定逻辑中
     */
    private boolean isInBindRunnable = false;
    /*
     * 客户端请求队列
     */
    private HandlerThread mHandlerThread;
    /*
     * 客户端请求处理
     */
    private Handler mHandler;

    /**
     * 客户端回调接口集合
     */
//    private RemoteCallbackList<ITXZCallBack> mCallBacks;

    /**
     * 客户端回调接口的集合
     */
    private Map<ITXZCallBack, String> callBackMap;

    /*
     * 私有构造
     */
    private TXZAIDLManager() {
    }

    /**
     * 方法名称：获取实例
     * 方法描述：获取实例
     *
     * @return 实例
     */
    public static TXZAIDLManager getInstance() {
        if (instance == null) {
            synchronized (TXZAIDLManager.class) {
                if (instance == null)
                    instance = new TXZAIDLManager();
            }
        }
        return instance;
    }

    /*
     * 初始化ServiceManager
     *
     * @param context context
     */
    private void bindAdapterService(Context context) {
        if (packageName == null) {
            // 想多了
            // String name = context.getPackageManager().getNameForUid(Binder.getCallingUid());
            // Log.d(TAG, "Calling name : " + name + " Context name : " + context.getPackageName());
            packageName = context.getPackageName();
        }

        Intent service = new Intent("com.txznet.adapter.StartService");
        service.setPackage("com.txznet.adapter");
        service.putExtra("package", packageName);
        service.putExtra("code", BuildConfig.VERSION_CODE);
        service.putExtra("name", BuildConfig.VERSION_NAME);
        context.bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, packageName + " : Pre bind Adapter");
    }

    /*
     * 接口引用
     */
    private ITXZAIDL iTxzAIDL;

    /*
     * Service 连接实体
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, componentName.getPackageName() + " : Connect : " + packageName);
            // 获取从Adapter返回来的Binder
            iTxzAIDL = ITXZAIDL.Stub.asInterface(iBinder);
            // 提前退出绑定逻辑，正常会等到下一次绑定检查才退出
            isInBindRunnable = false;
            // 连接上的时候把包名发回给Adapter
            sendInfoToAdapter();
            // 通知三方已经绑定成功
            if (txzCommandListener != null) txzCommandListener.onBindSuccess();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, componentName.getPackageName() + " : Disconnect : " + packageName);
            // 重置引用
            iTxzAIDL = null;
            // 开始重连逻辑
            reCheckServiceBind();
        }
    };

    /*
     * 把信息发回给Adapter
     */
    private void sendInfoToAdapter() {
        JSONObject jsonObject = new JSONObject();
        try {
            // 包名
            jsonObject.put("package", packageName);
            // SDK名
            jsonObject.put("name", BuildConfig.VERSION_NAME);
            // SDK版本
            jsonObject.put("code", BuildConfig.VERSION_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // key是瞎写的
        sendCommand(7086, _command, jsonObject.toString().getBytes(), false);
    }

    /*
     * TXZAdapter传过来的数据
     *
     * @param keyType keyType
     * @param command 命令
     * @param data    数据
     * @return
     */
    byte[] onCommandReceive(String packageName, int keyType, String command, byte[] data) {
        if (this.txzCommandListener == null) {
            Log.e(TAG, packageName + " : You forgot set Listener ?");
            return null;
        }
        return this.txzCommandListener.onCommandReceive(keyType, command, data);
    }

    /**
     * 接口名称：同行者指令回调监听
     * 接口描述：客户端收到同行者事件回调
     */
    public interface TXZCommandListener {

        /**
         * 方法名称：绑定成功
         * 方法描述：AIDL绑定成功回调
         */
        void onBindSuccess();

        /**
         * 方法名称：接收同行者指令
         * 方法描述：接收同行者外发数据
         */
        byte[] onCommandReceive(int keyType, String command, byte[] data);
    }

    /*
     * Context 持有类，防止报警
     * ...
     */
    private class MyContext {
        // 自己持有
        private Context context;

        private MyContext(Context context) {
            this.context = context;
        }

    }

    /**
     * 检查绑定状态
     * 没有实例，在绑定中》重新绑定
     *
     * @return 状态
     */
    private boolean checkBindState() {
        if (iTxzAIDL == null || isInBindRunnable) {
            Log.e(TAG, "AIDL isn't bind");
            reCheckServiceBind();
            return false;
        }
        return true;
    }

    /**
     * 重新绑定服务
     */
    private void reCheckServiceBind() {
        runOnUIGroundRunnable(getCheckServiceBindRunnable(), serviceBindDelay);
    }

    /*
     * 获取checkRunnable
     */
    private Runnable getCheckServiceBindRunnable() {
        if (checkServiceBindRunnable == null) {
            checkServiceBindRunnable = new Runnable() {
                @Override
                public void run() {
                    // 循环
                    removeUIGroundRunnable(this);
                    if (iTxzAIDL == null) {
                        // 处理绑定逻辑中
                        isInBindRunnable = true;
                        // 继续绑定
                        bindAdapterService(myContext.context);
                        // 设置检查逻辑
                        reCheckServiceBind();
                    } else {
                        // 退出绑定逻辑
                        isInBindRunnable = false;
                        Log.d(TAG, "Bind Adapter Service success ,Remove code");
                    }
                }
            };
        }
        return checkServiceBindRunnable;
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
     *|   外部只引用这里的方法                                                                  *|
     *|-----------------------------------------------------------------------------------------*|
     ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ***/


    /**
     * 方法名称：初始化同行者服务
     * 方法描述：初始化，Bind同行者服务
     *
     * @param context         context
     * @param commandListener 监听
     */
    public void initService(Context context, TXZCommandListener commandListener) {
        if (context == null) {
            Log.e(TAG, "Context is Null !");
            return;
        }
        // 设置监听
        this.txzCommandListener = commandListener;
        // 缓存context
        myContext = new MyContext(context);
        // 拿到looper
        handler = new Handler(context.getMainLooper());
        // 绑定TXZAdapter
        bindAdapterService(context);
        // 检查一下绑定
        reCheckServiceBind();
        //开启异步队列
        mHandlerThread = new HandlerThread("txz_queue");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

//        mCallBacks = new RemoteCallbackList<ITXZCallBack>();
        callBackMap = new HashMap<ITXZCallBack, String>();
    }

    /**
     * 方法名称：发送指令
     * 方法描述：发送指令给同行者适配程序
     *
     * @param keyType 消息
     * @param command 类型
     * @param data    数据
     * @param isAsync 是否异步
     */
    @SuppressWarnings("UnusedReturnValue")
    public byte[] sendCommand(int keyType, String command, byte[] data, boolean isAsync) {
        if (!checkBindState()) return null;
        try {
            if (isAsync) {
                return sendInvokeAsync(keyType, command, data);
            } else {
                return iTxzAIDL.sendInvoke(packageName, keyType, command, data);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 异步请求
     * 客户端消息发给Adapter 直接返回
     *
     * @param keyType 消息
     * @param command 类型
     * @param data    数据
     * @return 数据
     */
    private byte[] sendInvokeAsync(final int keyType, final String command, final byte[] data) {
        if (!checkBindState()) return null;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] rec = iTxzAIDL.sendInvoke(packageName, keyType, _async + command, data);
                    for (Map.Entry<ITXZCallBack, String> entry : callBackMap.entrySet()) {
                        if (packageName.equalsIgnoreCase(entry.getValue())) {
                            entry.getKey().callBack(rec);
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        return new byte[0];
    }

    /**
     * 注册数据回调
     * 客户端注册异步请求的统一回调接口
     *
     * @param callBack 数据回调
     */
    public void regTXZCallback(ITXZCallBack callBack) {
        if (!checkBindState()) return;
        if (callBackMap.containsKey(callBack)) {
            return;
        }
        if (null != callBack) {
            callBackMap.put(callBack, packageName);
        }
    }

    /**
     * 取消数据回调
     * 客户端反注册数据回调
     *
     * @param callBack 回调方法
     */
    public void unregTXZCallback(ITXZCallBack callBack) {
        if (!checkBindState()) return;
        if (null != callBack) {
            callBackMap.remove(callBack);
        }
    }

    /**
     * 同行者数据回调接口
     */
    public interface ITXZCallBack {
        void callBack(byte[] data);
    }
}