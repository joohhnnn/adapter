package com.txznet.adapter.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.txznet.adapter.AdapterApplication;
import com.txznet.adapter.AdapterConfig;
import com.txznet.adapter.module.BlueToothCallInit;
import com.txznet.adapter.module.CameraInit;
import com.txznet.adapter.module.CommandInit;
import com.txznet.adapter.module.WeatherUtil;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZMusicManager;
import com.txznet.sdk.TXZPowerManager;
import com.txznet.sdk.TXZTtsManager;

public class SysReceiver extends BroadcastReceiver {

    private final String TAG = SysReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d(TAG, "同行者收到广播，但对象为空！！！");
            return;
        }

        String action = intent.getAction();
        // 同行者语音界面开
        if (AdapterConfig.BROADCAST_RECORDWIN_SHOW.equals(action)) {
            Log.d(TAG, "同行者语音界面起");
            txzRecordShow(intent);

        }

        // 同行者语音界面关
        else if (AdapterConfig.BROADCAST_RECORDWIN_DISMISS.equals(action)) {
            Log.d(TAG, "同行者语音界面关");
            txzRecordDismiss(intent);
        }

        // 同行者接收的广播
        else if (AdapterConfig.BROADCAST_TXZ_RECEIVED.equals(action)) {
            Log.d(TAG, "同行者收到消息处理广播");
            txzDecideToHandler(intent);
        }
    }

    /**
     * 同行者：语音界面起
     *
     * @param intent 消息内容
     */
    private void txzRecordShow(Intent intent) {

    }

    /**
     * 同行者：语音界面关
     *
     * @param intent 消息内容
     */
    private void txzRecordDismiss(Intent intent) {

    }

    /**
     * 同行者：同行者处理
     *
     * @param intent 消息内容
     */
    private void txzDecideToHandler(Intent intent) {
        int key = intent.getIntExtra("key_type", 0);
        Log.d(TAG, "key=" + key);
        Bundle b = intent.getExtras();
        switch (key) {
            case 2020:
                if (b == null) {
                    Log.e(TAG, "系统工具消息处理：你是不是忘了带uuid信息进来？");
                    return;
                }
                String uuid = b.getString("uuid");
                if (uuid.isEmpty()) {
                    Log.e(TAG, "uuid = null");
                    return;
                }
                // 初始化语音
                AdapterApplication.getApp().initSDK(uuid);
                break;
            // 接收：蓝牙状态通知
            case 2000:
                BlueToothCallInit.getInstance().handleMessage(intent);
                break;
            // 接收：收音机状态通知
            case 2001:
                CommandInit.getInstance().handleMessage(intent);
                break;
            // 接收：拍照路径通知
            case 2002:
                CameraInit.getInstance().handleMessage(intent);
                break;
            case 2030:
                if (!AdapterApplication.getApp().mIsInitSuccess) {
                    Log.e(TAG,
                            "RECEIVE_KEYTYPE_CONTROL_TXZRECORDWINDOWS_SWICTH mIsInitSuccess=false!");
                    return;
                }
                // 三方想要开启或关闭语音界面
                if (b.getBoolean("open")) {
                    TXZAsrManager.getInstance().restart("有什么可以帮您？");
                } else {
                    TXZAsrManager.getInstance().cancel();
                }
                break;
            // 系统通知休眠和苏醒
            case 2040:
                if (b == null) {
                    Log.e(TAG, "系统通知休眠和苏醒：你是不是忘了带信息进来？");
                    return;
                }
                String action = b.getString("action");

                if ("car_strike".equals(action)) {
                    TXZPowerManager.getInstance().notifyPowerAction(
                            TXZPowerManager.PowerAction.POWER_ACTION_POWER_ON);
                } else if ("before_sleep".equals(action)) {
                    TXZPowerManager.getInstance().notifyPowerAction(
                            TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
                    TXZPowerManager.getInstance().releaseTXZ();
                } else if ("sleep".equals(action)) {
                    TXZPowerManager.getInstance().notifyPowerAction(
                            TXZPowerManager.PowerAction.POWER_ACTION_SLEEP);
                } else if ("wakeup".equals(action)) {
                    TXZPowerManager.getInstance().reinitTXZ(new Runnable() {
                        @Override
                        public void run() {
                            TXZPowerManager
                                    .getInstance()
                                    .notifyPowerAction(
                                            TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
                        }
                    });
                } else if ("shock_wakeup".equals(action)) {
                    TXZPowerManager.getInstance().notifyPowerAction(
                            TXZPowerManager.PowerAction.POWER_ACTION_SHOCK_WAKEUP);
                } else if ("enter_reverse".equals(action)) {
                    TXZPowerManager.getInstance().notifyPowerAction(
                            TXZPowerManager.PowerAction.POWER_ACTION_ENTER_REVERSE);
                } else if ("quit_reverse".equals(action)) {
                    TXZPowerManager.getInstance().notifyPowerAction(
                            TXZPowerManager.PowerAction.POWER_ACTION_QUIT_REVERSE);
                } else if ("before_power_off".equals(action)) {
                    TXZPowerManager
                            .getInstance()
                            .notifyPowerAction(
                                    TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_POWER_OFF);
                } else if ("car_flameout".equals(action)) {
                    TXZPowerManager.getInstance().notifyPowerAction(
                            TXZPowerManager.PowerAction.POWER_ACTION_POWER_OFF);
                }
                break;
            case 2050:
                if (b == null) {
                    Log.e(TAG, "同行者播报：你没有传bundle");
                    return;
                }
                if (TXZConfigManager.getInstance().isInitedSuccess()) {
                    if (b.getString("speak") == null) {
                        Log.e(TAG, "同行者播报：你的bundle里面没有String,字段为speak");
                        return;
                    }
                    TXZTtsManager.getInstance().speakText(b.getString("speak"));
                } else {
                    Log.e(TAG, "txz is not init success !!!");
                }
                break;
            case 2060:
                WeatherUtil.sendWeatherData();
                break;
            case 2061:
                TXZMusicManager.MusicModel model = TXZMusicManager.getInstance().getCurrentMusicModel();
                if (model!=null) {
                    BroadCastUtil.sendBroadCast(1061, "ttMusicModel", model.toString());
                }else {
                    BroadCastUtil.sendBroadCast(1061, "ttMusicModel", "");
                }
                break;
            case 2003:
                if (b == null) {
                    Log.e(TAG, "同行者修改按钮状：你没有传bundle");
                    return;
                }
                if (TXZConfigManager.getInstance().isInitedSuccess()) {
                    String iconType = b.getString("type");
                    if (iconType == null) {
                        Log.e(TAG, "同行者修改按钮状：你的bundle里面没有String,type");
                        return;
                    }
                    switch (iconType) {
                        case "none":
                            TXZConfigManager.getInstance().showFloatTool(
                                    TXZConfigManager.FloatToolType.FLOAT_NONE);
                            Log.d(TAG, "已隐藏图标");
                            break;
                        case "normal":
                            TXZConfigManager.getInstance().showFloatTool(
                                    TXZConfigManager.FloatToolType.FLOAT_NORMAL);
                            Log.d(TAG, "已显示图标");
                            break;
                        case "top":
                            TXZConfigManager.getInstance().showFloatTool(
                                    TXZConfigManager.FloatToolType.FLOAT_TOP);
                            Log.d(TAG, "已置顶图标");
                            break;
                    }
                } else {
                    Log.e(TAG, "txz is not init success !!!");
                }
                break;
            default:
                break;
        }
    }
}
