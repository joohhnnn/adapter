package com.txznet.adapter.conn.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BootService extends Service {
	
	private final String TAG = BootService.class.getSimpleName();
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "BootService onCreate");
	}
	    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
