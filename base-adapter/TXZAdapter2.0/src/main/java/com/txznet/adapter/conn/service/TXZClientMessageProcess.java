package com.txznet.adapter.conn.service;

import android.util.Log;

import com.txznet.adapter.AdpApplication;
import com.txznet.adapter.AdpStatusManager;
import com.txznet.adapter.base.util.JsonUtil;
import com.txznet.adapter.base.util.SPUtil;
import com.txznet.adapter.module.BlueCallModule;
import com.txznet.adapter.tool.BlueToothTool;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZPowerManager;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdk.bean.TxzPoi;

public class TXZClientMessageProcess {
    private static final String TAG="TXZClientMessageProcess";
    private static TXZClientMessageProcess instance;

    String cmd;

    public String getCmd() {
        return cmd;
    }

    private TXZClientMessageProcess() {
    }

    public static TXZClientMessageProcess getInstance() {
        if (null == instance) {
            synchronized (TXZClientMessageProcess.class) {
                if (null == instance) {
                    instance = new TXZClientMessageProcess();
                }
            }
        }
        return instance;
    }

    public byte[] processMessage(int key, String command, byte[] data) {
        if (command.startsWith("async")) {
            Log.d(TAG, "this data is client async sent!");
            //暂时没有此需求，有异步请求客户端时在此处接收数据
            command = command.replace("async.", "");
        }

        switch (key) {
            //--------------------------------------------------------------------------------------
            //                                       2000 APP状态
            //--------------------------------------------------------------------------------------
            case 2000:

                break;
            //--------------------------------------------------------------------------------------
            //                                       2010 系统
            //--------------------------------------------------------------------------------------
            case 2010:
                // WIFI打开
                if ("wifi.open".equals(command)) {
                    AdpStatusManager.getInstance().setWifiOpen(true);
                    return null;
                }
                // WIFI断开
                if ("wifi.close".equals(command)) {
                    AdpStatusManager.getInstance().setWifiOpen(false);
                    return null;
                }
                // WIFI连接
                if ("wifi.connect".equals(command)) {
                    boolean connect = JsonUtil.getBooleanFromJson("type", data, false);
                    String name = JsonUtil.getStringFromJson("name", data, null);
                    AdpStatusManager.getInstance().setWifiConnect(connect);
                    AdpStatusManager.getInstance().setCurWifiName(name);
                    return null;
                }
                // 系统状态
                if ("system.status".equals(command)) {
                    String status = JsonUtil.getStringFromJson("status", data, "");
                    switch (status) {
                        case "car.on":
                            TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_POWER_ON);
                            break;
                        case "car.off":
                            TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_POWER_OFF);
                            break;
                        case "before.sleep":
                            TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
                            TXZPowerManager.getInstance().releaseTXZ();
                            break;
                        case "sleep":
                            TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_SLEEP);
                            break;
                        case "wakeup":
                            TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
                            TXZPowerManager.getInstance().reinitTXZ();
                            break;
                        case "shock":
                            TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_SHOCK_WAKEUP);
                            break;
                        case "reverse.enter":
                            TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_ENTER_REVERSE);
                            break;
                        case "reverse.quit":
                            TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_QUIT_REVERSE);
                            break;
                        case "power.off":
                            TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_POWER_OFF);
                            break;
                    }
                    return null;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2020 亮度
            //--------------------------------------------------------------------------------------
            case 2020:
                boolean isAuto = JsonUtil.getBooleanFromJson("auto", data, false);
                int light = JsonUtil.getIntFromJson("number", data, -1);
                AdpStatusManager.getInstance().setLightAuto(isAuto);
                AdpStatusManager.getInstance().setCurLightInt(light);
                break;
            //--------------------------------------------------------------------------------------
            //                                       2030 音量
            //--------------------------------------------------------------------------------------
            case 2030:
                int volume = JsonUtil.getIntFromJson("number", data, -1);
                AdpStatusManager.getInstance().setCurVolumeInt(volume);
                break;
            //--------------------------------------------------------------------------------------
            //                                       2040 蓝牙
            //--------------------------------------------------------------------------------------
            case 2040:
                // 蓝牙连接
                if ("bluetooth.connect".equals(command)) {
                    BlueCallModule.getInstance().onBlueStateOnConnect();
                    return null;
                }
                // 蓝牙断开
                if ("bluetooth.disconnect".equals(command)) {
                    BlueCallModule.getInstance().onBlueStateDisconnect();
                    return null;
                }
                // 空闲
                if ("bluetooth.idle".equals(command)) {
                    BlueCallModule.getInstance().onBlueStateOnIdle();
                    return null;
                }
                // 来电
                if ("bluetooth.incoming".equals(command)) {
                    String name = JsonUtil.getStringFromJson("name", data, null);
                    String phone = JsonUtil.getStringFromJson("number", data, null);
                    BlueCallModule.getInstance().onBlueStateOnIncoming(name, phone);
                    return null;
                }
                // 去电
                if ("bluetooth.call".equals(command)) {
                    String name = JsonUtil.getStringFromJson("name", data, null);
                    String phone = JsonUtil.getStringFromJson("number", data, null);
                    BlueCallModule.getInstance().onBlueStateMakeCall(name, phone);
                    return null;
                }
                // 挂断
                if ("bluetooth.hangup".equals(command)) {
                    BlueCallModule.getInstance().onBlueStateOnIdle();
                    return null;
                }
                // 拒接
                if ("bluetooth.reject".equals(command)) {
                    BlueCallModule.getInstance().onBlueStateOnIdle();
                    return null;
                }
                // 接通
                if ("bluetooth.offhook".equals(command)) {
                    BlueCallModule.getInstance().onBlueStateOnOffHook();
                    return null;
                }
                // 蓝牙联系人同步完成
                if ("bluetooth.contact".equals(command)) {
                    BlueCallModule.getInstance().syncContact(BlueToothTool.getContact());
                    return null;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2050 媒体
            //--------------------------------------------------------------------------------------
            case 2050:
                if ("radio.open".equals(command)) {
                    AdpStatusManager.getInstance().setRadioOpen(true);
                    return null;
                }
                if ("radio.close".equals(command)) {
                    AdpStatusManager.getInstance().setRadioOpen(false);
                    return null;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2060 媒体
            //--------------------------------------------------------------------------------------
            case 2060:
                if ("music.open".equals(command)) {

                    return null;
                }
                if ("music.close".equals(command)) {
                    AdpStatusManager.getInstance().setMediaType(null);
                    return null;
                }
                if ("music.show".equals(command)) {

                    return null;
                }
                if ("music.dismiss".equals(command)) {

                    return null;
                }
                if ("music.statue".equals(command)) {

                    return null;
                }
                if ("media.type".equals(command)) {
                    String type = JsonUtil.getStringFromJson("type", data, null);
                    AdpStatusManager.getInstance().setMediaType(type);
                    return null;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2070 抓拍
            //--------------------------------------------------------------------------------------
            case 2070:
                if ("capture.photo".equals(command)) {

                    return null;
                }
                if ("capture.video".equals(command)) {

                    return null;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2080 空调
            //--------------------------------------------------------------------------------------
            case 2080:
                if ("ac.wind.change".equals(command)) {
                    int windSpeed = JsonUtil.getIntFromJson("number", data, -1);
                    AdpStatusManager.getInstance().setCurAirWindInt(windSpeed);
                    return null;
                }
                if ("ac.temp.change".equals(command)) {
                    int temp = JsonUtil.getIntFromJson("number", data, -1);
                    AdpStatusManager.getInstance().setCurAirTempInt(temp);
                    return null;
                }
                if ("ac.status".equals(command)) {
                    boolean acStatus = JsonUtil.getBooleanFromJson("type", data, false);
                    AdpStatusManager.getInstance().setAirACOpen(acStatus);
                    return null;
                }
                if ("ac.mode".equals(command)) {
                    JsonUtil.getStringFromJson("mode", data, null);
                    // todo  暂未处理
                    return null;
                }
                if ("ac.front.defrost".equals(command)) {
                    boolean type = JsonUtil.getBooleanFromJson("type", data, false);
                    AdpStatusManager.getInstance().setFrontDefrostOpen(type);
                    return null;
                }
                if ("ac.behind.defrost".equals(command)) {
                    boolean type = JsonUtil.getBooleanFromJson("type", data, false);
                    AdpStatusManager.getInstance().setBehindDefrostOpen(type);
                    return null;
                }
                if ("ac.loop".equals(command)) {
                    boolean type = JsonUtil.getBooleanFromJson("type", data, false);
                    AdpStatusManager.getInstance().setInnerLoop(type);
                    return null;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2400 语音
            //--------------------------------------------------------------------------------------
            case 2400:
                // 界面自动
                if ("txz.window.auto".equals(command)) {
                    TXZAsrManager.getInstance().triggerRecordButton();
                    return "okokokok".getBytes();
                }
                // 打开界面
                if ("txz.window.open".equals(command)) {
                    TXZAsrManager.getInstance().restart(null);
                    return null;
                }
                // 关闭界面
                if ("txz.window.close".equals(command)) {
                    TXZAsrManager.getInstance().cancel();
                    return null;
                }
                // 后台播报
                if ("txz.tts.speak".equals(command)) {
                    TXZTtsManager.getInstance().speakText(JsonUtil.getStringFromJson("tts", data, null));
                    return null;
                }
                // 前台播报
                if ("txz.tts.show".equals(command)) {
                    TXZResourceManager.getInstance().speakTextOnRecordWin(JsonUtil.getStringFromJson("tts", data, null), false, null);
                    return null;
                }
                // 修改图标
                if ("txz.float.mode".equals(command)) {
                    String mode = JsonUtil.getStringFromJson("mode", data, null);
                    if (mode == null) return null;
                    switch (mode) {
                        case "top":
                            TXZConfigManager.getInstance().showFloatTool(TXZConfigManager.FloatToolType.FLOAT_TOP);
                            SPUtil.putString(AdpApplication.getInstance(), SPUtil.KEY_FLOAT_TOOL_TYPE, "top");
                            break;
                        case "normal":
                            TXZConfigManager.getInstance().showFloatTool(TXZConfigManager.FloatToolType.FLOAT_NORMAL);
                            SPUtil.putString(AdpApplication.getInstance(), SPUtil.KEY_FLOAT_TOOL_TYPE, "normal");
                            break;
                        case "none":
                            TXZConfigManager.getInstance().showFloatTool(TXZConfigManager.FloatToolType.FLOAT_NONE);
                            SPUtil.putString(AdpApplication.getInstance(), SPUtil.KEY_FLOAT_TOOL_TYPE, "none");
                            break;
                    }
                    return null;
                }
                if ("ComReg".equals(command)){
                    cmd = JsonUtil.getStringFromJson("cr", data, "");
                    TXZAsrManager.getInstance().regCommand(cmd, cmd);
                }
        }
        return new byte[0];
    }
}
