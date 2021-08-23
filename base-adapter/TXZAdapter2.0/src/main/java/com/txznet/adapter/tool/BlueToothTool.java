package com.txznet.adapter.tool;

import android.util.Log;

import com.txznet.adapter.AdpStatusManager;
import com.txznet.adapter.base.BaseTool;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.adapter.base.util.JsonUtil;
import com.txznet.adapter.conn.service.TXZAdpManager;
import com.txznet.sdk.TXZCallManager;
import com.txznet.sdk.TXZResourceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙相关工具
 * Created by MarvinYang on 2018/3/15.
 */

@SuppressWarnings("ConstantConditions")
public class BlueToothTool extends BaseTool {

    private static final String TAG = "BlueToothTool";

    /**
     * 通知车机打电话
     *
     * @param name   姓名
     * @param number 电话
     */
    public static void notifyToMakeCall(String name, String number) {
        Log.d(TAG, "notifyToMakeCall " + name + " -> " + number);
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1003, "action", "call", "num", number);
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1040, "action", "bluetooth.call", "name", name, "number", number);
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1040, "bluetooth.call", JsonUtil.transParamToJson("name", name, "number", number).getBytes());
                break;
            default:
                break;
        }
    }

    /**
     * 通知车机接听电话
     */
    public static void notifyToAcceptCall() {
        Log.d(TAG, "notifyToAcceptCall");
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1003, "action", "accept");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1040, "action", "bluetooth.accept");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1040, "bluetooth.accept", null);
                break;
            default:
                break;
        }
    }

    /**
     * 通知车机拒接电话
     */
    public static void notifyToRejectCall() {
        Log.d(TAG, "notifyToRejectCall");
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1003, "action", "reject");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1040, "action", "bluetooth.reject");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1040, "bluetooth.reject", null);
                break;
            default:
                break;
        }
    }

    /**
     * 通知车机挂电话
     */
    public static void notifyToHangUpCall() {
        Log.d(TAG, "notifyToHangUpCall");
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1003, "action", "hangup");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1040, "action", "bluetooth.hangup");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1040, "bluetooth.hangup", null);
                break;
            default:
                break;
        }
    }

    /*
     * 打开蓝牙
     */
    public static void openBlueTooth() {
        // 蓝牙无任何状态
        if (AdpStatusManager.getInstance().getBluetoothOpen() == null || AdpStatusManager.getInstance().getBlueConnect() == null) {
            open();
        }
        // 蓝牙已连接
        if (AdpStatusManager.getInstance().getBlueConnect()) {
            TXZResourceManager.getInstance().speakTextOnRecordWin("当前蓝牙已连接", false, null);
        }
        // 蓝牙已打开
        else if (AdpStatusManager.getInstance().getBluetoothOpen()) {
            TXZResourceManager.getInstance().speakTextOnRecordWin("当前蓝牙已打开", false, null);
        }
        // 其它情况
        else {
            open();
        }
    }

    private static void open() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1040, "action", "bluetooth.open");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1040, "bluetooth.open", null);
                break;
        }
    }

    /*
     * 关闭蓝牙
     */
    public static void closeBlueTooth() {
        // 没有发过蓝牙状态以及蓝牙是开的
        if (AdpStatusManager.getInstance().getBluetoothOpen() == null || AdpStatusManager.getInstance().getBluetoothOpen()) {
            switch (CUR_PROTO_INFO) {
                case BROADCAST_VERSION_1_X:

                    break;
                case BROADCAST_VERSION_2_X:
                    BroadCastUtil.sendBroadCast(1040, "action", "bluetooth.close");
                    break;
                case AIDL_VERSION:
                    TXZAdpManager.getInstance().sendCommandToAll(1040, "bluetooth.close", null);
                    break;
            }
        }
        // 其它
        else {
            TXZResourceManager.getInstance().speakTextOnRecordWin("当前蓝牙已关闭", false, null);
        }
    }

    /*
     * 主动查询蓝牙状态
     */
    public static void getBlueStatus() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1040, "action", "bluetooth.status");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1040, "bluetooth.status", null);
                break;
        }
    }

    public static List<TXZCallManager.Contact> getContact() {
        byte[] data = TXZAdpManager.getInstance().sendCommandToAll(1040, "bluetooth.contact", null, true);
        List<TXZCallManager.Contact> contactList = new ArrayList<>();
        if (data == null) {
            Log.d(TAG, "GetContact is null");
            return contactList;
        }
        try {
            JSONArray jsonArray = new JSONArray(new String(data));
            for (int i = 0; i < jsonArray.length(); i++) {
                TXZCallManager.Contact contact = new TXZCallManager.Contact();
                contact.setNumber(jsonArray.getJSONObject(i).getString("number"));
                contact.setName(jsonArray.getJSONObject(i).getString("name"));
                contactList.add(contact);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "GetContact : " + contactList.size());
        return contactList;
    }

    /*
     * 初始时请求蓝牙相关数据
     */
    public static void requestBlueContact(){
        switch (PROTO_VERSION){
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1040, "action", "bluetooth.contact");
                break;
            case AIDL_VERSION:

                break;
        }
    }

}
