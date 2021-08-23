package com.txznet.adapter;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.txznet.adapter.module.BlueToothCallInit;
import com.txznet.adapter.module.CarControlInit;
import com.txznet.adapter.module.CommandInit;
import com.txznet.adapter.module.SysToolInit;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.sdk.TXZCallManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZConfigManager.ActiveListener;
import com.txznet.sdk.TXZConfigManager.InitListener;
import com.txznet.sdk.TXZConfigManager.InitParam;
import com.txznet.sdk.TXZTtsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MarvinYang
 *         Application 类包含初始化内容
 */
public class AdapterApplication extends Application implements InitListener,
        ActiveListener {

    private final String TAG = AdapterApplication.class.getSimpleName();

    private static AdapterApplication mInstance;

    protected static Handler uiHandler = new Handler(Looper.getMainLooper());

    // 是否已经初始化
    public boolean mIsInit;
    // 是否初始化成功
    public boolean mIsInitSuccess;

    public static AdapterApplication getApp() {
        return mInstance;
    }

    public static void runOnUiGround(Runnable r, long delay) {
        uiHandler.postDelayed(r, delay);
    }

    public static void removeUiGroundCallback(Runnable r) {
        uiHandler.removeCallbacks(r);
    }

    private InitParam mInitParam = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        {
            // TODO 获取接入分配的appId和appToken
            String appId = this.getResources().getString(
                    R.string.txz_sdk_init_app_id);
            String appToken = this.getResources().getString(
                    R.string.txz_sdk_init_app_token);
            // TODO 设置初始化参数
            mInitParam = new InitParam(appId, appToken);
            // TODO 可以设置自己的客户ID，同行者后台协助计数/鉴权
            // mInitParam.setAppCustomId("ABCDEFG");
            // TODO 可以设置自己的硬件唯一标识码
            // mInitParam.setUUID("0123456789");
            // TODO 可以设置是否显示悬浮图标
            // mInitParam.setFloatToolType(FloatToolType.FLOAT_NONE);
        }

        {
            // TODO 设置唤醒词
            String[] wakeupKeywords = this.getResources().getStringArray(
                    R.array.txz_sdk_init_wakeup_keywords);
            mInitParam.setWakeupKeywordsNew(wakeupKeywords);
        }

        {
            // TODO 可以按需要设置自己的对话模式
            // mInitParam.setAsrMode(AsrMode.ASR_MODE_CHAT);
            // TODO 设置识别模式，默认自动模式即可
            // mInitParam.setAsrServiceMode(AsrServiceMode.ASR_SVR_MODE_AUTO);
            // TODO 设置是否允许启用服务号
            // mInitParam.setEnableServiceContact(true);
            // TODO 设置开启回音消除模式
            // mInitParam.setFilterNoiseType(1);
            // TODO 其他设置
        }

        // 设置唤醒阀值-4.0
        // TXZConfigManager.getInstance().setWakeupThreshhold(-4.0f);
        // TXZConfigManager.getInstance().setAsrWakeupThreshhold(-4.0f);

        // TODO 初始化在这里
        if (AdapterConfig.USE_UUID) {
            BroadCastUtil.sendBroadCast(1020, "action", "get_uuid");
        } else {
            initSDK(null);
        }
    }

    public void initSDK(String uuid) {
        if (!mIsInit) {
            if (uuid != null && !uuid.isEmpty()) {
                mInitParam.setUUID(uuid);
            }
            TXZConfigManager.getInstance().initialize(this, mInitParam, this, this);
            mIsInit = true;
        }
    }

    @Override
    public void onFirstActived() {
        // TODO 首次联网激活，如果需要出厂激活提示，可以在这里完成
    }

    @Override
    public void onError(int errCode, String errDesc) {
        // TODO 初始化出错
    }

    @Override
    public void onSuccess() {
        mIsInitSuccess = true;
        // TODO 初始化成功，可以在这里根据需要执行一些初始化操作，参考其他Activity
        // TODO 设置一些参数(参考ConfigActivity)
        // TODO 注册指令(参考AsrActivity)
        // TODO 设置电话(参考CallActivity)、音乐(参考MusicActivity)、导航(参考NavActivity)工具
        // TODO 同步联系人(参考CallActivity)
        Log.d(TAG, "onsuccess");

        BlueToothCallInit.getInstance().init();// 蓝牙电话工具初始化

        // 车机才注册
        if (!AdapterConfig.IS_MIRROR)
            CarControlInit.getInstance().init();// 车身控制工具初始化

        SysToolInit.getInstance().init();// 系统工具初始化
        CommandInit.getInstance().init();// 自定义命令工具初始化

        TXZTtsManager.getInstance().speakText("同行者语音引擎初始化成功");
    }
}
