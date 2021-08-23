package com.txznet.adapter.ui;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.txznet.adapter.R;
import com.txznet.adapter.module.FVMModule;

public class TestActivity extends Activity {
    public String TAG="TestActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String t1=((EditText)findViewById (R.id.et1)).getText().toString().trim();
                Log.d(TAG, "onClick: setFVMFocusWidthAngle :"+t1);
                int c1=0;
                try {
                    c1= Integer.parseInt( t1);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                boolean rs= FVMModule.getInstance().setFVMFocusWidthAngle(c1);
                Toast.makeText(getApplicationContext(),  "偏向角设置"+ (rs ? "成功":"失败"), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String t1=((EditText)findViewById (R.id.et2)).getText().toString().trim();
                Log.d(TAG, "onClick: setFVMFocusAngle:"+t1);
                int c1=0;
                try {
                    c1= Integer.parseInt( t1);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                boolean rs= FVMModule.getInstance().setFVMFocusAngle(c1);
                Toast.makeText(getApplicationContext(), "拾音角设置"+ (rs ? "成功":"失败"), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: getAngleAndProb");
                String t1=((EditText)findViewById (R.id.et1)).getText().toString().trim();
                String t2=( (EditText)findViewById (R.id.et2)).getText().toString().trim();
                double c1=0;
                double c2=0;
                try {
                    c1= Double.parseDouble( t1);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                try {
                    c2= Double.parseDouble(t2);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "c1: "+t1);
                Log.d(TAG, "c2: "+t2);
                double[] temp1=new double[]{c1};
                double[] temp2=new double[]{c2};

                boolean count = FVMModule.getInstance().getAngleAndProbTest(temp1,temp2);
                StringBuffer word=new StringBuffer("temp1:");
                for (double d1: temp1) {
                    Log.d(TAG, "temp1中的值："+d1);
                    word.append("\t"+d1);
                }
                word.append("\ntemp2:");
                for (double d2: temp2) {
                    Log.d(TAG, "temp2中的值："+d2);
                    word.append("\t"+d2);
                }
                word.append("\t返回值为："+count);
                Log.d(TAG, "getAngleAndProbTest返回值为："+count);
                Toast.makeText(getApplicationContext(), word, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.bt4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "获取适配版本号");
                getAPKVersionCode();
            }
        });

        findViewById(R.id.bt5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "获取适配信息");
                getAPKVersionInfo();
            }
        });

        findViewById(R.id.bt6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "获取Core版本号");
                getCoreVersionCode();
            }
        });

        findViewById(R.id.bt7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "获取core信息");
                getCoreVersionInfo();
            }
        });

    }

    /**
     * 获取适配版本号
     */
    public void getAPKVersionCode(){
        int versionCode=0;
        try {
            versionCode=getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "versionCode版本号为："+versionCode);
        Toast.makeText(getApplicationContext(), "versionCode版本号为："+versionCode, Toast.LENGTH_SHORT).show();
    }


    /**
     * 获取适配信息
     */
    public void getAPKVersionInfo(){
        StringBuffer versionInfo=new StringBuffer("版本名：");
        try {
            versionInfo.append(getPackageManager().getPackageInfo(getPackageName(), 0).versionName+"   ###   路径：");
            versionInfo.append(getPackageManager().getApplicationInfo(getPackageName(), 0).sourceDir);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "VersionInfo  APK的信息为："+versionInfo);
        Toast.makeText(getApplicationContext(), "VersionInfo  APK的信息为："+versionInfo, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取core版本号
     */
    public void getCoreVersionCode(){
        int versionCode=0;
        try {
            versionCode=getPackageManager().getPackageInfo("com.txznet.txz", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "CoreVersion版本号为："+versionCode);
        Toast.makeText(getApplicationContext(), "CoreVersion版本号为："+versionCode, Toast.LENGTH_SHORT).show();
    }




    /**
     * 获取core信息
     */
    public void getCoreVersionInfo(){
        StringBuffer versionInfo=new StringBuffer("版本名：");
        try {
            versionInfo.append(getPackageManager().getPackageInfo("com.txznet.txz", 0).versionName+"   ###   路径：");
            versionInfo.append(getPackageManager().getApplicationInfo("com.txznet.txz", 0).sourceDir);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "VersionInfo  Core的信息为："+versionInfo);
        Toast.makeText(getApplicationContext(), "VersionInfo  Core的信息为："+versionInfo, Toast.LENGTH_SHORT).show();
    }


}
