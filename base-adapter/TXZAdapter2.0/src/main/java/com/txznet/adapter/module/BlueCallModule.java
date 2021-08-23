package com.txznet.adapter.module;

import com.txznet.adapter.AdpStatusManager;
import com.txznet.adapter.base.BaseModule;
import com.txznet.adapter.base.util.LogUtil;
import com.txznet.adapter.tool.BlueToothTool;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZCallManager;

import java.util.List;

/**
 * Created by MarvinYang on 2018/2/9.
 * 蓝牙电话管理类
 */

public class BlueCallModule extends BaseModule {

    private static BlueCallModule blueToothCallInitManager;
    
    /**
     * 当前状态
     */
    private static CallType currentCallType = CallType.CALL_IDLE;

    private TXZCallManager.CallToolStatusListener callToolStatusListener;

    private BlueCallModule() {
    }

    public static BlueCallModule getInstance() {
        if (blueToothCallInitManager == null) {
            synchronized (BlueCallModule.class) {
                if (blueToothCallInitManager == null)
                    blueToothCallInitManager = new BlueCallModule();
            }
        }
        return blueToothCallInitManager;
    }

    @Override
    public void init() {
        TXZCallManager.getInstance().setCallTool(callTool);
        BlueToothTool.getBlueStatus();
    }

    /*
     * 电话工具：通过语音的操作都会走到这里来
     */
    private TXZCallManager.CallTool callTool = new TXZCallManager.CallTool() {
        @Override
        public CallStatus getStatus() {
            return CallStatus.CALL_STATUS_IDLE;
        }

        @Override
        public boolean makeCall(TXZCallManager.Contact contact) {

            BlueToothTool.notifyToMakeCall(contact.getName(), contact.getNumber());
            return true;
        }

        @Override
        public boolean acceptIncoming() {
            BlueToothTool.notifyToAcceptCall();
            return true;
        }

        @Override
        public boolean rejectIncoming() {
            BlueToothTool.notifyToRejectCall();
            return true;
        }

        @Override
        public boolean hangupCall() {
            BlueToothTool.notifyToHangUpCall();
            return true;
        }

        @Override
        public void setStatusListener(TXZCallManager.CallToolStatusListener callToolStatusListener) {
            BlueCallModule.this.callToolStatusListener = callToolStatusListener;
            if (AdpStatusManager.getInstance().getBlueConnect() == null || !AdpStatusManager.getInstance().getBlueConnect()){
                        BlueCallModule.this.callToolStatusListener.onDisabled("抱歉，蓝牙未连接，请先连接蓝牙");
            }else{
                BlueCallModule.this.callToolStatusListener.onEnabled();
                switch (currentCallType){
                    case CALL_IN:
                        BlueCallModule.this.callToolStatusListener.onIncoming(new TXZCallManager.Contact(),false,false);
                        break;
                    case CALLING:
                        BlueCallModule.this.callToolStatusListener.onOffhook();
                        break;
                    case CALL_IDLE:
                        BlueCallModule.this.callToolStatusListener.onIdle();
                        break;
                }
            }
        }
    };

    /*
     * 蓝牙连接时调用：通知语音蓝牙状态
     */
    public void onBlueStateOnConnect() {
        AdpStatusManager.getInstance().setBlueConnect(true);
        if (callToolStatusListener == null) {
            LogUtil.d(TAG, "onBlueStateOnConnect : callToolListener is null");
            return;
        }
        callToolStatusListener.onEnabled();
    }

    /*
     * 蓝牙连接且空闲时调用
     */
    public void onBlueStateOnIdle() {
        currentCallType = CallType.CALL_IDLE;
        if (callToolStatusListener == null) {
            LogUtil.d(TAG, "onBlueStateOnConnect : callToolListener is null");
            return;
        }
        callToolStatusListener.onIdle();
    }

    /*
     * 蓝牙来电时调用
     */
    public void onBlueStateOnIncoming(String name, String num) {
        currentCallType = CallType.CALL_IN;
        if (callToolStatusListener == null) {
            LogUtil.d(TAG, "onBlueStateOnConnect : callToolListener is null");
            return;
        }
        // BUG 不然语音界面会卡住open
        TXZAsrManager.getInstance().cancel();
        TXZCallManager.Contact contact = new TXZCallManager.Contact();
        if (name != null) contact.setName(name);
        if (num != null) contact.setNumber(num);
        callToolStatusListener.onIncoming(contact, true, true);
    }

    /*
     * 蓝牙通话进入通话状态
     */
    public void onBlueStateOnOffHook() {
        currentCallType = CallType.CALLING;
        if (callToolStatusListener == null) {
            LogUtil.d(TAG, "onBlueStateOnConnect : callToolListener is null");
            return;
        }
        callToolStatusListener.onOffhook();
    }

    /*
     * 蓝牙准备打电话（此时肯定是用户手动拨号，非通过语音）
     */
    public void onBlueStateMakeCall(String name, String num) {
        currentCallType = CallType.CALLING;
        if (callToolStatusListener == null) {
            LogUtil.d(TAG, "onBlueStateOnConnect : callToolListener is null");
            return;
        }
        // BUG 不然语音界面会卡住
        TXZAsrManager.getInstance().cancel();
        TXZCallManager.Contact contact = new TXZCallManager.Contact();
        if (name != null) contact.setName(name);
        if (num != null) contact.setNumber(num);
        callToolStatusListener.onMakeCall(contact);
    }

    /*
     * 蓝牙断开时调用
     */
    public void onBlueStateDisconnect() {
        AdpStatusManager.getInstance().setBlueConnect(false);
        if (callToolStatusListener == null) {
            LogUtil.d(TAG, "onBlueStateOnConnect : callToolListener is null");
            return;
        }
        callToolStatusListener.onDisabled("抱歉，蓝牙未连接，请先连接蓝牙");

    }

    /*
     * 同步联系人
     */
    public void syncContact(List<TXZCallManager.Contact> contacts) {
        TXZCallManager.getInstance().syncContacts(contacts);
    }

    /**
     * 电话状态
     */
    private enum CallType {
        /**
         * 来电
         */
        CALL_IN,

        /**
         * 通话中
         */
        CALLING,

        /**
         * 电话空闲
         */
        CALL_IDLE
    }

}
