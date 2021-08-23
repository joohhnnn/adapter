package com.example.calltest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZCallManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TXZAsrManager.CommandListener {
    public static TXZCallManager.CallToolStatusListener mCallToolStatusListener;
    private Button call;
    private Button close;
    private Cursor cursor;
    private TXZCallManager.CallTool callTool;
   public static TXZCallManager.Contact CONTACT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getcontact();

    }
    public void init(){

        call=findViewById(R.id.call);
        close=findViewById(R.id.close);

        TXZAsrManager.getInstance().addCommandListener(this);
//        TXZConfigManager.getInstance().enableWakeup(true);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setcalltest();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closecalltest();
            }
        });
    }
    @Override
    public void onCommand(String s, String s1) {

    }

    //设置电话工具
    public  void setcalltest(){
               callTool=new TXZCallManager.CallTool() {
            @Override
            public CallStatus getStatus() {

                return CallStatus.CALL_STATUS_IDLE;
            }

            @Override
            public boolean makeCall(TXZCallManager.Contact contact) {
                callPhone(contact.getNumber());
               // mCallToolStatusListener.onMakeCall(contact);
                CONTACT=contact;

                return true;
            }

            @Override
            public boolean acceptIncoming() {
                return true;
            }

            @Override
            public boolean rejectIncoming() {
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public boolean hangupCall() {
                endCall(MainActivity.this);
                return true;
            }

            @Override
            public void setStatusListener(TXZCallManager.CallToolStatusListener listener) {
                mCallToolStatusListener = listener;
                if (listener != null) {
                    // TODO 通知最后的电话状态
                    listener.onEnabled();
                    listener.onIdle();
                }

            }
        };
        TXZCallManager.getInstance().setCallTool(callTool);
    }

    public  void closecalltest(){

        TXZCallManager.getInstance().setCallTool(null);
    }



    //同步联系人
    public  void getcontact() {
        List<TXZCallManager.Contact> lst = new ArrayList<TXZCallManager.Contact>();
        TXZCallManager.Contact con;
        con = new TXZCallManager.Contact();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        cursor = getContentResolver().query(uri, null, null, null, null);
        while (cursor.moveToNext()) {
            //获取联系人的ID
            int idFieldIndex = cursor.getColumnIndex("_id");
            int id = cursor.getInt(idFieldIndex);
            //获取联系人的名字
            int nameFieldIndex = cursor.getColumnIndex("display_name");
            String name = cursor.getString(nameFieldIndex);

            //获取联系人的电话号码个数
            int numCountFieldIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

            int numCount = cursor.getInt(numCountFieldIndex);
            String phoneNumber = "";
            if (numCount > 0) {
                Cursor phonecursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?"
                        , new String[]{Integer.toString(id)}, null);
                if (phonecursor.moveToFirst()) {
                    int numFileldIndex = phonecursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    phoneNumber = phonecursor.getString(numFileldIndex);
                }
            }
            con.setName(name);
            con.setNumber(phoneNumber);
            lst.add(con);
        }
        TXZCallManager.getInstance().syncContacts(lst);
    }

    //拨打电话
    public void callPhone(String phoneNum){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }
    //挂断电话
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void endCall(Context paramContext) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                TelecomManager telecom = (TelecomManager) paramContext.getSystemService(Context.TELECOM_SERVICE);
                if (ActivityCompat.checkSelfPermission(paramContext, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                ((TelecomManager) telecom).endCall();
            }
            TelephonyManager systemService = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (systemService == null)
                return;
            Method method = systemService.getClass().getDeclaredMethod("getITelephony", new Class[0]);
            method.setAccessible(true);
            Object invoke = method.invoke(paramContext, new Object[0]);
            if (invoke == null)
                return;
            Method localObject = paramContext.getClass().getMethod("endCall", new Class[0]);
            ((Method) localObject).setAccessible(true);
            ((Method) localObject).invoke(paramContext, new Object[0]);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}