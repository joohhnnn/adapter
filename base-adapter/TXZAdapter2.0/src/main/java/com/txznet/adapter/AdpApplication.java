package com.txznet.adapter;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Environment;

import com.txznet.adapter.base.BaseApplication;
import com.txznet.adapter.base.crash.CrashCommonHandler;
import com.txznet.adapter.module.AirControlModule;
import com.txznet.adapter.module.AppModule;
import com.txznet.adapter.module.BlueCallModule;
import com.txznet.adapter.module.CommandModule;
import com.txznet.adapter.module.InitModule;
import com.txznet.adapter.module.SceneModule;
import com.txznet.adapter.module.SystemModule;
import com.txznet.adapter.ui.view.FloatView;
import com.txznet.adapter.ui.FVMUpdateActivity;
import com.txznet.debugreceiver.DebugMessageReceiver;
import com.txznet.sdk.TXZCallManager;

/**
 * @author MarvinYang
 * Application 类包含初始化内容
 * ----------   *.module类 SDK逻辑类，一般不修改
 * ----------   *.tool类   实现类，针对不同方案需要修改
 * ----------   *.uitl类   通用工具类
 * ----------   广播在CenterBroadcast中，分发给其它的协议
 * ----------   AIDL协议中可以配置是否需要从AIDL中获取UUID，默认是false
 */
public class AdpApplication extends BaseApplication {
    private TXZCallManager.CallToolStatusListener mCallToolStatusListener;
    private static AdpApplication adpApplication;

    /**
     * 单例
     *
     * @return Application
     */
    public static AdpApplication getInstance() {
        return adpApplication;
    }

    @Override
    public void onCreate() {




        super.onCreate();
        // 赋值实例
        adpApplication = this;
        // 初始化捕获异常模块
        initCrash();
        // 初始化语音程序
        initTXZSDK();
        FloatView.init(this);
        //TXZCallManager.getInstance().setCallTool(callTool);
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String buildType = applicationInfo.metaData.getString("buildType");
            if ("debug".equals(buildType)){
                FloatView.showFloatView();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TXZCallManager.getInstance().setCallTool(mCallTool);
        mCallToolStatusListener.onEnabled();
    }

    private TXZCallManager.CallTool mCallTool = new TXZCallManager.CallTool() {
        @Override
        public void setStatusListener(TXZCallManager.CallToolStatusListener listener) {
            // 记录下listener，适当的时机通知sdk状态变
            mCallToolStatusListener = listener;
            if (listener != null) {
                // TODO 通知最后的电话状态
                listener.onEnabled();
                listener.onIdle();
            }
        }

        @Override
        public boolean rejectIncoming() {
            //DebugUtil.showTips("模拟拒接来电");
            return true;
        }

        @Override
        public boolean makeCall(TXZCallManager.Contact contact) {
            //DebugUtil.showTips("模拟打电话给" + contact.getName()
                //    + contact.getNumber());
            return true;
        }

        @Override
        public boolean hangupCall() {
          //  DebugUtil.showTips("模拟挂断电话");
            return true;
        }

        @Override
        public CallStatus getStatus() {
            return CallStatus.CALL_STATUS_IDLE;
        }

        @Override
        public boolean acceptIncoming() {
            //DebugUtil.showTips("模拟接受来电");
            return true;
        }
    };

    private void initCrash() {
        // 初始化异常捕获
        try {
            String dir = Environment.getExternalStorageDirectory().getPath();
            // 此目录同行者会一并上传至服务器，方便统计BUG
            CrashCommonHandler.init(this, new CrashCommonHandler.CrashLisener(dir + "/txz/report/"));
        } catch (Exception ignored) {
        }
    }

    public void startFVMUpdate(String fvmVersion, String expectedVersion){
        Intent intent = new Intent(this, FVMUpdateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(FVMUpdateActivity.INTENT_EXTRA_FVM_VERSION, fvmVersion);
        intent.putExtra(FVMUpdateActivity.INTENT_EXTRA_EXPECTED_VERSION, expectedVersion);
        startActivity(intent);
    }

    /**
     * 初始化语音
     */
    private void initTXZSDK() {
        InitModule.getInstance().init();
    }

    /**
     * 配置语音相关
     */
    public void setTXZConfigWhenInitSuccess() {
        // 调试用广播接收器插件
        DebugMessageReceiver.init(this);
        AppModule.getInstance().init();
        BlueCallModule.getInstance().init();
        CommandModule.getInstance().init();
        SceneModule.getInstance().init();
        SystemModule.getInstance().init();
        AirControlModule.getInstance().init();
    }


}
