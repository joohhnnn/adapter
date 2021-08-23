package com.example.week2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.week2.jsontool.JsonUtil;
import com.txznet.adapter.aidl.TXZAIDLManager;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private EditText ed;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    public void initView(){
        button=findViewById(R.id.button);
        ed=findViewById(R.id.ed);
        tv=findViewById(R.id.tv);
        //setResult("dadadada");


        button.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               String regCommand = ed.getText().toString();
               System.out.println(regCommand);
               if (!"".equals(regCommand)){
                   TXZAIDLManager.getInstance().sendCommand(2400, "ComReg", JsonUtil.transParamToJson("cr", regCommand).getBytes(), false);
               }

            }

       });

        TXZAIDLManager.getInstance().initService(this, new TXZAIDLManager.TXZCommandListener() {
            @Override
            public void onBindSuccess() {
                Log.e("1400TV1","bdbdbbd");

            }

            @Override
            public byte[] onCommandReceive(int keyType, String s, byte[] bytes) {
               // tv.setText("MessageProcess.getInstance().getInfo()");


                Log.e("1400TV1","1111");
               // Log.e("1400tv",MessageParser.getInstance().getInfo());
               // Log.e("1400TV1","111");
                //setResult("sad");
               return MessageParser.getInstance().processMessage(keyType, s, bytes);

            }
        });
    }
    public void setResult(String s){
        tv.setText("语音命中了《" + s + "》命令");
    }
}