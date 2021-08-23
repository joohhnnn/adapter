package com.example.appc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZTtsManager;


public class MainActivity extends AppCompatActivity {
    private final String SHUIBEI_OPEN = "SHUIBEI_OPEN";
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    public void initView(){
        button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TXZAsrManager.getInstance().regCommand("打开抽屉", SHUIBEI_OPEN);
                //TXZAsrManager.getInstance().addCommandListener(commandListener);
            }
        });

    }
    /*private TXZAsrManager.CommandListener commandListener = new TXZAsrManager.CommandListener() {
        @Override
        public void onCommand(String command, String data) {
            if(data.equals("SHUIBEI_OPEN")){
                TXZTtsManager.getInstance().speakText("已经打开水杯水杯水杯了哈哈");
                Log.e("dddd", "已经打开抽屉了哈哈");
                return;
            }

        }
    }; */

}
