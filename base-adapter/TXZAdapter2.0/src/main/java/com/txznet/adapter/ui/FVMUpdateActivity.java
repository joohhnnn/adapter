package com.txznet.adapter.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.txznet.adapter.AdpApplication;
import com.txznet.adapter.module.FVMModule;

import java.util.stream.Stream;

public class FVMUpdateActivity extends AppCompatActivity {

	public static final String INTENT_EXTRA_FVM_VERSION = "FVM_VERSION";
	public static final String INTENT_EXTRA_EXPECTED_VERSION = "FVM_EXPECTED_VERSION";

	private static final String TAG = "FVMUpdateActivity";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String fvmVersion = intent.getStringExtra(INTENT_EXTRA_FVM_VERSION);
		String expectedVersion = intent.getStringExtra(INTENT_EXTRA_EXPECTED_VERSION);
		Log.d(TAG, "onCreate: VERSION "+fvmVersion+" EXPECTED "+expectedVersion);
		AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
		aBuilder.setPositiveButton("确定", (dialog, which) -> {
			Log.d(TAG, "onClick: 确定");
			ProgressDialog progressDialog = new ProgressDialog(FVMUpdateActivity.this);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setTitle("提示");
			progressDialog.setMessage("正在刷入固件，请不要关闭电源或打火熄火");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setIndeterminate(true);
			Log.d(TAG, "onClick: 正在显示刷机进度框");
			progressDialog.show();
			Log.d(TAG, "onClick: 正在刷入...");
			long startTime = System.currentTimeMillis();
			FVMModule.getInstance().updateFVM(new FVMModule.FVMUpdateListener() {
				@Override
				public void onSuccess() {
					long endTime = System.currentTimeMillis();
					FVMUpdateActivity.this.runOnUiThread(()->{
						Log.d(TAG, "onSuccess: 刷入成功，dismiss进度框");
						progressDialog.dismiss();
						AlertDialog.Builder bb = new AlertDialog.Builder(FVMUpdateActivity.this);
						Log.d(TAG, "onSuccess: 刷入用时"+(endTime-startTime)+"ms");
						bb.setTitle("提示").setMessage("刷入成功，用时"+(endTime-startTime)+"ms").setCancelable(false);
						bb.setPositiveButton("确定", (dialog1, which1)->{
							Log.d(TAG, "用户确认，刷入流程结束");
							dialog1.dismiss();
							FVMUpdateActivity.this.finish();
						});
						Log.d(TAG, "onSuccess: 正在显示刷入成功提示...");
						bb.show();
					});
				}

				@Override
				public void onFailed() {
					FVMUpdateActivity.this.runOnUiThread(()-> {
						Log.d(TAG, "onSuccess: 失败，dismiss进度框");
						progressDialog.dismiss();
						AlertDialog.Builder bb = new AlertDialog.Builder(FVMUpdateActivity.this);
						bb.setTitle("提示").setMessage("刷入失败").setCancelable(false);
						bb.setPositiveButton("确定", (dialog1, which1) -> {
							Log.d(TAG, "用户确认，刷入失败流程结束");
							dialog1.dismiss();
							FVMUpdateActivity.this.finish();
						});
						Log.d(TAG, "onSuccess: 正在显示刷入失败提示...");
						bb.show();
					});
				}
			});
		});
		aBuilder.setNegativeButton("取消", (dialog, which) -> {
			dialog.dismiss();
			FVMUpdateActivity.this.finish();
		});
		aBuilder.setMessage(String.format("麦克风降噪模块版本错误，当前版本为%s，预期版本为%s，是否进行刷入？（刷入约1分钟）", fvmVersion, expectedVersion));
		aBuilder.setTitle("同行者警告");
		Log.d(TAG, "onCreate: Showing Confirm Dialog");
		AlertDialog alertDialog = aBuilder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
	}
}
