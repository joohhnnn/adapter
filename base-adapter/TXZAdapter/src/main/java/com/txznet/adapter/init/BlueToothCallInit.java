package com.txznet.adapter.module;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.txznet.adapter.AdapterApplication;
import com.txznet.adapter.BaseInitModule;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.sdk.TXZCallManager;
import com.txznet.sdk.TXZCallManager.CallTool;
import com.txznet.sdk.TXZCallManager.CallToolStatusListener;
import com.txznet.sdk.TXZCallManager.Contact;

/**
 * @author MarvinYang 蓝牙工具
 * @version 2017/05/19
 */
public class BlueToothCallInit extends BaseInitModule {

    private static BlueToothCallInit instance;
    private static CallToolStatusListener mListener;

    private final int STATE_DISCONNECTED = 0;
    private final int STATE_IDLE = 1;
    private final int STATE_INCOMING = 2;
    private final int STATE_MAKING_CALL = 3;
    private final int STATE_OFFHOOK = 4;
    private final int STATE_CONNECTED = 5;

    private int mCallStatus = STATE_DISCONNECTED;
    private String mNum;

    private final String TAG = BlueToothCallInit.class.getSimpleName();

    @Override
    public void init() {
        BroadCastUtil.sendBroadCast(1003, "action", "getstatus");
        Log.d(TAG, "获取蓝牙状态");
        // 获取蓝牙联系人
        TXZCallManager.getInstance().setCallTool(mCallTool);
        getContact();
    }

    /**
     * 获取实例
     *
     * @return 实例
     */
    public static BlueToothCallInit getInstance() {
        if (instance == null) {
            synchronized (BlueToothCallInit.class) {
                if (instance == null)
                    instance = new BlueToothCallInit();
            }
        }
        return instance;
    }

    /**
     * 电话工具
     */
    private CallTool mCallTool = new CallTool() {
        @Override
        public CallStatus getStatus() {
            if (STATE_OFFHOOK == mCallStatus)
                return CallStatus.CALL_STATUS_OFFHOOK;
            else if (STATE_INCOMING == mCallStatus)
                return CallStatus.CALL_STATUS_RINGING;
            else
                return CallStatus.CALL_STATUS_IDLE;
        }

        @Override
        public boolean acceptIncoming() {
            Log.d(TAG, "acceptIncoming");
            BroadCastUtil.sendBroadCast(1003, "action", "accept");
            return true;
        }

        @Override
        public boolean hangupCall() {
            Log.d(TAG, "hangupCall");
            BroadCastUtil.sendBroadCast(1003, "action", "hangup");
            return true;
        }

        @Override
        public boolean makeCall(Contact contact) {
            Log.d(TAG, "makeCall num=" + contact.getNumber());
            BroadCastUtil.sendBroadCast(1003, "action", "call", "num",
                    contact.getNumber());
            return true;
        }

        @Override
        public boolean rejectIncoming() {
            Log.d(TAG, "rejectIncoming");
            BroadCastUtil.sendBroadCast(1003, "action", "reject");
            return true;
        }

        @Override
        public void setStatusListener(CallToolStatusListener statusListener) {
            Log.d(TAG, "setStatusListener");
            mListener = statusListener;
            handleState();
        }

    };

    /**
     * 仅仅对状态进行处理
     *
     * @param intent intent
     */
    @Override
    public void handleMessage(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null || bundle.getString("status") == null) {
            Log.e(TAG, "蓝牙消息处理：你是不是忘了带蓝牙状态进来？");
            return;
        }
        String status = bundle.getString("status");
        if ("onidle".equals(status)) {
            mCallStatus = STATE_IDLE;
            handleState();
        } else if ("onincoming".equals(status)) {
            String num = bundle.getString("num");
            if (num != null && num.isEmpty()) {
                Log.e(TAG, "onincoming num.isEmpty()=true");
                return;
            }
            mCallStatus = STATE_INCOMING;
            mNum = num;
            handleState();
        } else if ("onoffhook".equals(status)) {
            mCallStatus = STATE_OFFHOOK;
            handleState();
        } else if ("onmakecall".equals(status)) {
            String num = bundle.getString("num");
            if (num != null && num.isEmpty()) {
                Log.e(TAG, "onmakecall num.isEmpty()=true");
                return;
            }
            mCallStatus = STATE_MAKING_CALL;
            mNum = num;
            handleState();
        } else if ("onconnected".equals(status)) {
            mCallStatus = STATE_CONNECTED;
            handleState();
        } else if ("ondisconnected".equals(status)) {
            mCallStatus = STATE_DISCONNECTED;
            handleState();
        } else if ("getcontact".equals(status)) {
            List<Contact> contacts = new ArrayList<TXZCallManager.Contact>();
            String[] names = bundle.getStringArray("name");
            String[] nums = bundle.getStringArray("num");
            long[] times = bundle.getLongArray("lastcalltime");
            if (nums == null || names == null) {
                Log.e(TAG, "getcontact param==null");
                return;
            }
            Log.d(TAG, "getcontact");
            for (int i = 0; i < nums.length; i++) {
                Contact contact = new Contact();
                contact.setName(names[i]);
                contact.setNumber(nums[i]);
                if (times != null && times.length > i)
                    contact.setLastTimeContacted(times[i]);
                contacts.add(contact);
            }
            TXZCallManager.getInstance().syncContacts(contacts);
        }
    }

    /**
     * 针对状态进行相应操作
     */
    private void handleState() {
        switch (mCallStatus) {
            case STATE_DISCONNECTED:
                Log.d(TAG, "蓝牙已断开");
                mListener.onDisabled("蓝牙已断开连接");
                break;
            case STATE_IDLE:
                Log.d(TAG, "onIdle");
                mListener.onEnabled();
                mListener.onIdle();
                getContact();
                break;
            case STATE_INCOMING:
                Log.d(TAG, "收到新来电：" + mNum);
                mListener.onEnabled();
                Contact contactIncoming = new Contact();
                contactIncoming.setNumber(mNum);
                mListener.onIncoming(contactIncoming, true, true);
                break;
            case STATE_OFFHOOK:
                Log.d(TAG, "通话中");
                mListener.onEnabled();
                mListener.onOffhook();
                break;
            case STATE_MAKING_CALL:
                Log.d(TAG, "开始打电话：" + mNum);
                mListener.onEnabled();
                Contact contactMakeCall = new Contact();
                contactMakeCall.setNumber(mNum);
                mListener.onMakeCall(contactMakeCall);
                break;
            case STATE_CONNECTED:
                Log.d(TAG, "蓝牙已连接");
                mListener.onEnabled();
                getContact();
                break;
            default:
                break;
        }
    }

    public void getContact() {
        BroadCastUtil.sendBroadCast(1003, "action", "getcontact");
    }
}
