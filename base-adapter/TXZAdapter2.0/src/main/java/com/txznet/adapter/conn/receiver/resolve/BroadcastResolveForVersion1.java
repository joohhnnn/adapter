package com.txznet.adapter.conn.receiver.resolve;

import android.content.Intent;

import com.txznet.adapter.base.BaseBroadcastResolve;
import com.txznet.adapter.base.util.LogUtil;
import com.txznet.adapter.module.BlueCallModule;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZCallManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZPowerManager;
import com.txznet.sdk.TXZTtsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 1.X 协议版本广播解析
 * Created by MarvinYang on 2018/3/15.
 */
public class BroadcastResolveForVersion1 extends BaseBroadcastResolve {

    private static BroadcastResolveForVersion1 instance;

    private BroadcastResolveForVersion1() {
    }

    public static BroadcastResolveForVersion1 getInstance() {
        if (instance == null) {
            synchronized (BroadcastResolveForVersion1.class) {
                if (instance == null)
                    instance = new BroadcastResolveForVersion1();
            }
        }
        return instance;
    }

    @Override
    public void resolveIntentMessage(Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            LogUtil.d(TAG, "resolveIntentMessage : action is null");
            return;
        }
        switch (action) {
            // 开机广播
            case "android.intent.action.BOOT_COMPLETED":
                LogUtil.d(TAG, ": BOOT_COMPLETED");
                break;
            // 语音界面启动
            case "com.txznet.txz.record.show":
                LogUtil.d(TAG, "adapter : record show");
                break;
            // 语音界面消失
            case "com.txznet.txz.record.dismiss":
                LogUtil.d(TAG, "adapter : record dismiss");
                break;
            // 适配统一收广播
            case "com.txznet.adapter.recv":
                resolveKeyType(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 处理keyType 值
     *
     * @param intent 广播intent
     */
    private void resolveKeyType(Intent intent) {
        int keyType = intent.getIntExtra("keyType", 0);
        switch (keyType) {
            case 2000:// 蓝牙状态通知
                String status = intent.getStringExtra("status");
                if (status == null) {
                    LogUtil.d(TAG, "keyType = 2000 : status == null");
                    return;
                }
                switch (status) {
                    case "onconnect":
                        BlueCallModule.getInstance().onBlueStateOnConnect();
                        break;
                    case "onidle":// 蓝牙空闲
                        BlueCallModule.getInstance().onBlueStateOnIdle();
                        break;
                    case "onincoming":// 蓝牙来电
                        String name = intent.getStringExtra("name");
                        String num = intent.getStringExtra("num");
                        BlueCallModule.getInstance().onBlueStateOnIncoming(name, num);
                        break;
                    case "onoffhook":// 蓝牙通话中
                        BlueCallModule.getInstance().onBlueStateOnOffHook();
                        break;
                    case "onmakecall":// 蓝牙打电话
                        String nameCall = intent.getStringExtra("name");
                        String numCall = intent.getStringExtra("num");
                        BlueCallModule.getInstance().onBlueStateMakeCall(nameCall, numCall);
                        break;
                    case "ondisconnected":// 蓝牙断开
                        BlueCallModule.getInstance().onBlueStateDisconnect();
                        break;
                    case "getcontact":// 发过来蓝牙联系人
                        List<TXZCallManager.Contact> contacts = new ArrayList<TXZCallManager.Contact>();
                        String[] names = intent.getStringArrayExtra("name");
                        String[] nums = intent.getStringArrayExtra("num");
                        long[] times = intent.getLongArrayExtra("lastcalltime");
                        if (nums == null || names == null) {
                            LogUtil.e(TAG, "getcontact param==null");
                            return;
                        }
                        for (int i = 0; i < nums.length; i++) {
                            TXZCallManager.Contact contact = new TXZCallManager.Contact();
                            contact.setName(names[i]);
                            contact.setNumber(nums[i]);
                            if (times != null && times.length > i)
                                contact.setLastTimeContacted(times[i]);
                            contacts.add(contact);
                        }
                        TXZCallManager.getInstance().syncContacts(contacts);
                        break;
                }
                break;
            case 2001:// 接收：收音机状态通知

                break;
            case 2002:// 接收：拍照路径通知

                break;
            case 2003://
                if (TXZConfigManager.getInstance().isInitedSuccess()) {
                    String iconType = intent.getStringExtra("type");
                    if (iconType == null) {
                        LogUtil.e(TAG, "同行者修改按钮状：您的bundle里面没有String,type");
                        return;
                    }
                    switch (iconType) {
                        case "none":
                            TXZConfigManager.getInstance().showFloatTool(TXZConfigManager.FloatToolType.FLOAT_NONE);
                            LogUtil.d(TAG, "已隐藏图标");
                            break;
                        case "normal":
                            TXZConfigManager.getInstance().showFloatTool(TXZConfigManager.FloatToolType.FLOAT_NORMAL);
                            LogUtil.d(TAG, "已显示图标");
                            break;
                        case "top":
                            TXZConfigManager.getInstance().showFloatTool(TXZConfigManager.FloatToolType.FLOAT_TOP);
                            LogUtil.d(TAG, "已置顶图标");
                            break;
                    }
                } else {
                    LogUtil.e(TAG, "txz is not init success !!!");
                }
                break;
            /*case 2020:// getUUID
            // 已删除
                break;*/
            case 2030:// 打开语音
                if (!TXZConfigManager.getInstance().isInitedSuccess()) {
                    LogUtil.d(TAG, "TXZ is not init success");
                    return;
                }
                String auto = intent.getStringExtra("action");
                if ("auto".equals(auto)) {
                    TXZAsrManager.getInstance().triggerRecordButton();
                    return;
                }
                // 三方想要开启或关闭语音界面
                if (intent.getBooleanExtra("action", false)) {
                    TXZAsrManager.getInstance().restart(null);
                } else {
                    TXZAsrManager.getInstance().cancel();
                }
                break;
            case 2040:// 系统通知休眠和苏醒
                String sysAction = intent.getStringExtra("action");
                LogUtil.d(TAG, "2040 : action = " + sysAction);
                if (sysAction == null) return;
                switch (sysAction) {
                    case "car_strike":
                        TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_POWER_ON);
                        break;
                    case "before_sleep":
                        TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
                        TXZPowerManager.getInstance().releaseTXZ();
                        break;
                    case "sleep":
                        TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_SLEEP);
                        break;
                    case "wakeup":
                        TXZPowerManager.getInstance().reinitTXZ(new Runnable() {
                            @Override
                            public void run() {
                                TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
                            }
                        });
                        break;
                    case "shock_wakeup":
                        TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_SHOCK_WAKEUP);
                        break;
                    case "enter_reverse":
                        TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_ENTER_REVERSE);
                        break;
                    case "quit_reverse":
                        TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_QUIT_REVERSE);
                        break;
                    case "before_power_off":
                        TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_POWER_OFF);
                        break;
                    case "car_flameout":
                        TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_POWER_OFF);
                        break;
                }
                break;
            case 2050:
                if (TXZConfigManager.getInstance().isInitedSuccess()) {
                    String tts = intent.getStringExtra("speak");
                    TXZTtsManager.getInstance().speakText(tts == null ? "" : tts);
                } else {
                    LogUtil.e(TAG, "txz is not init success !!!");
                }
                break;
        }
    }
}
