package com.txznet.adapter.conn.receiver.resolve;

import android.content.Intent;

import com.txznet.adapter.AdpApplication;
import com.txznet.adapter.AdpStatusManager;
import com.txznet.adapter.base.BaseBroadcastResolve;
import com.txznet.adapter.base.util.LogUtil;
import com.txznet.adapter.base.util.SPUtil;
import com.txznet.adapter.module.BlueCallModule;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZCallManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZPowerManager;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZTtsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 2.X 协议版本广播解析
 * Created by MarvinYang on 2018/3/15.
 */
public class BroadcastResolveForVersion2 extends BaseBroadcastResolve {

    private static BroadcastResolveForVersion2 instance;

    private BroadcastResolveForVersion2() {
    }

    public static BroadcastResolveForVersion2 getInstance() {
        if (instance == null) {
            synchronized (BroadcastResolveForVersion1.class) {
                if (instance == null)
                    instance = new BroadcastResolveForVersion2();
            }
        }
        return instance;
    }

    @Override
    public void resolveIntentMessage(Intent intent) {
        int keyType = intent.getIntExtra("key_type", -1);
        String action = intent.getStringExtra("action");
        switch (keyType) {
            //--------------------------------------------------------------------------------------
            //                                       2000 APP状态
            //--------------------------------------------------------------------------------------
            case 2000:

                break;
            //--------------------------------------------------------------------------------------
            //                                       2010 系统
            //--------------------------------------------------------------------------------------
            case 2010:
                if ("wifi.open".equals(action)) {
                    AdpStatusManager.getInstance().setWifiOpen(true);
                    return;
                }
                if ("wifi.close".equals(action)) {
                    AdpStatusManager.getInstance().setWifiOpen(false);
                    return;
                }
                if ("wifi.connect".equals(action)) {
                    boolean connect = intent.getBooleanExtra("type", false);
                    String name = intent.getStringExtra("name");
                    AdpStatusManager.getInstance().setWifiConnect(connect);
                    AdpStatusManager.getInstance().setCurWifiName(name);
                    return;
                }
                if ("system.status".equals(action)) {
                    String status = intent.getStringExtra("status");
                    if (status == null) return;
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
                    return;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2020 亮度
            //--------------------------------------------------------------------------------------
            case 2020:
                boolean isAuto = intent.getBooleanExtra("auto", false);
                int light = intent.getIntExtra("number", -1);
                AdpStatusManager.getInstance().setLightAuto(isAuto);
                AdpStatusManager.getInstance().setCurLightInt(light);
                break;
            //--------------------------------------------------------------------------------------
            //                                       2030 音量
            //--------------------------------------------------------------------------------------
            case 2030:
                int volume = intent.getIntExtra("number", -1);
                AdpStatusManager.getInstance().setCurVolumeInt(volume);
                break;
            //--------------------------------------------------------------------------------------
            //                                       2040 蓝牙
            //--------------------------------------------------------------------------------------
            case 2040:
                // 蓝牙连接
                if ("bluetooth.connect".equals(action)) {
                    BlueCallModule.getInstance().onBlueStateOnConnect();
                    return;
                }
                // 蓝牙断开
                if ("bluetooth.disconnect".equals(action)) {
                    BlueCallModule.getInstance().onBlueStateDisconnect();
                    return;
                }
                // 空闲
                if ("bluetooth.idle".equals(action)) {
                    BlueCallModule.getInstance().onBlueStateOnIdle();
                    return;
                }
                // 来电
                if ("bluetooth.incoming".equals(action)) {
                    String name = intent.getStringExtra("name");
                    String number = intent.getStringExtra("number");
                    BlueCallModule.getInstance().onBlueStateOnIncoming(name, number);
                    return;
                }
                // 去电
                if ("bluetooth.call".equals(action)) {
                    String name = intent.getStringExtra("name");
                    String number = intent.getStringExtra("number");
                    BlueCallModule.getInstance().onBlueStateMakeCall(name, number);
                    return;
                }
                // 挂断
                if ("bluetooth.hangup".equals(action)) {
                    BlueCallModule.getInstance().onBlueStateOnIdle();
                    return;
                }
                // 挂断
                if ("bluetooth.reject".equals(action)) {
                    BlueCallModule.getInstance().onBlueStateOnIdle();
                    return;
                }
                // 接通
                if ("bluetooth.offhook".equals(action)) {
                    BlueCallModule.getInstance().onBlueStateOnOffHook();
                    return;
                }
                // 蓝牙联系人同步完成
                if ("bluetooth.contact".equals(action)) {
                    String json = intent.getStringExtra("contact");
                    LogUtil.d(TAG, "Receive:" + json);
                    List<TXZCallManager.Contact> contactList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(json);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TXZCallManager.Contact contact = new TXZCallManager.Contact();
                            contact.setName(jsonObject.getString("name"));
                            contact.setNumber(jsonObject.getString("number"));
                            contactList.add(contact);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    BlueCallModule.getInstance().syncContact(contactList);
                    return;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2050 媒体
            //--------------------------------------------------------------------------------------
            case 2050:
                if ("radio.open".equals(action)) {
                    AdpStatusManager.getInstance().setRadioOpen(true);
                    return;
                }
                if ("radio.close".equals(action)) {
                    AdpStatusManager.getInstance().setRadioOpen(false);
                    return;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2060 媒体
            //--------------------------------------------------------------------------------------
            case 2060:
                if ("music.open".equals(action)) {

                    return;
                }
                if ("music.close".equals(action)) {
                    AdpStatusManager.getInstance().setMediaType(null);
                    return;
                }
                if ("music.show".equals(action)) {
                    return;
                }
                if ("music.dismiss".equals(action)) {
                    return;
                }
                if ("music.statue".equals(action)) {
                    return;
                }
                if ("media.type".equals(action)) {
                    String type = intent.getStringExtra("type");
                    AdpStatusManager.getInstance().setMediaType(type);
                    return;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2070 抓拍
            //--------------------------------------------------------------------------------------
            case 2070:
                if ("capture.photo".equals(action)) {

                    return;
                }
                if ("capture.video".equals(action)) {

                    return;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2080 空调
            //--------------------------------------------------------------------------------------
            case 2080:
                if ("ac.wind.change".equals(action)) {
                    int windSpeed=intent.getIntExtra("number",-1);
                    AdpStatusManager.getInstance().setCurAirWindInt(windSpeed);
                    return;
                }
                if ("ac.temp.change".equals(action)) {
                    int temp=intent.getIntExtra("number", -1);
                    AdpStatusManager.getInstance().setCurAirTempInt(temp);
                    return;
                }
                if ("ac.status".equals(action)) {
                    boolean acStatus = intent.getBooleanExtra("type", false);
                    AdpStatusManager.getInstance().setAirACOpen(acStatus);
                    return;
                }
                if ("ac.mode".equals(action)) {
                    String mode = intent.getStringExtra("mode");
                    // todo  暂未处理
                    return;
                }
                if ("ac.front.defrost".equals(action)) {
                    boolean type = intent.getBooleanExtra("type", false);
                    AdpStatusManager.getInstance().setFrontDefrostOpen(type);
                    return;
                }
                if ("ac.behind.defrost".equals(action)) {
                    boolean type = intent.getBooleanExtra("type", false);
                    AdpStatusManager.getInstance().setBehindDefrostOpen(type);
                    return;
                }
                if ("ac.loop".equals(action)) {
                    boolean type = intent.getBooleanExtra("type", false);
                    AdpStatusManager.getInstance().setInnerLoop(type);
                    return;
                }
                break;
            //--------------------------------------------------------------------------------------
            //                                       2400 语音
            //--------------------------------------------------------------------------------------
            case 2400:
                // 界面自动
                if ("txz.window.auto".equals(action)) {
                    TXZAsrManager.getInstance().triggerRecordButton();
                    return;
                }
                // 打开界面
                if ("txz.window.open".equals(action)) {
                    TXZAsrManager.getInstance().restart(null);
                    return;
                }
                // 关闭界面
                if ("txz.window.close".equals(action)) {
                    TXZAsrManager.getInstance().cancel();
                    return;
                }
                if ("txz.tts.speak".equals(action)) {
                    String tts = intent.getStringExtra("tts");
                    if (tts == null) return;
                    TXZTtsManager.getInstance().speakText(tts);
                    return;
                }
                if ("txz.tts.show".equals(action)) {
                    String tts = intent.getStringExtra("tts");
                    if (tts == null) return;
                    TXZResourceManager.getInstance().speakTextOnRecordWin(tts, false, null);
                    return;
                }
                if ("txz.float.mode".equals(action)) {
                    String mode = intent.getStringExtra("mode");
                    if (mode == null) return;
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
                }
        }
    }
}
