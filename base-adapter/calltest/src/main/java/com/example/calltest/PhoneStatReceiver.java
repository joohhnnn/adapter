package com.example.calltest;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.txznet.sdk.TXZCallManager;


public class PhoneStatReceiver extends BroadcastReceiver {
    private TXZCallManager.CallToolStatusListener mCallToolStatusListener;

    private static final String TAG = "PhoneStatReceiver";

//        private static MyPhoneStateListener phoneListener = new MyPhoneStateListener();

    private static boolean incomingFlag = false;

    private static String incoming_number = null;



    @Override
    public void onReceive(Context context, Intent intent) {
        //如果是拨打电话
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            incomingFlag = false;
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            MainActivity.mCallToolStatusListener.onMakeCall(MainActivity.CONTACT);
            Log.i(TAG, "call OUT:"+phoneNumber);

        }else{
            //如果是来电
            TelephonyManager tm =
                    (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);

            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    incomingFlag = true;//标识当前是来电
                    incoming_number = intent.getStringExtra("incoming_number");
                    Log.i(TAG, "RINGING :"+ incoming_number);
                    MainActivity.mCallToolStatusListener.onIncoming(MainActivity.CONTACT,);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if(incomingFlag){
                        Log.i(TAG, "incoming ACCEPT :"+ incoming_number);
                    }
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    if(incomingFlag){
                        Log.i(TAG, "incoming IDLE");
                    }
                    break;
            }
        }
    }
}
