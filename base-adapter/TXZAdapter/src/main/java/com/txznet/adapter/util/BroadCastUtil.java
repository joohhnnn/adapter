package com.txznet.adapter.base.util;

import com.txznet.adapter.AdapterApplication;
import com.txznet.adapter.AdapterConfig;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author MarvinYang
 * @version 2017.0519 MarvinYang 修改BUG 以前是：b.putString(string[i],
 *          string[i++]);造成key和value对不上
 */
public class BroadCastUtil {
	
	private static final String TAG = BroadCastUtil.class.getSimpleName();

	/**
	 * 专用广播工具 默认有action 需要传key值
	 * 
	 * @param key
	 *            KEY值
	 * @param str
	 *            bundle其他参数及值
	 */
	public static void sendBroadCast(int key, String... str) {
		Log.d(TAG, key + "");
		if (str.length % 2 != 0){
			Log.e(TAG, "广播参数错误");
			Exception e = new Exception();
			e.printStackTrace();
			return;
		}
		Intent intent = new Intent(AdapterConfig.BROADCAST_TXZ_SEND);
		intent.putExtra("key_type", key);
		Bundle b = new Bundle();
		for (int i = 0; i < str.length; i++) {
			b.putString(str[i], str[++i]);
		}
		intent.putExtras(b);
		AdapterApplication.getApp().sendBroadcast(intent);
	}

	/**
	 * @param key
	 *            KEY值
	 * @param intKeyString
	 *            bundle内key值
	 * @param intValue
	 *            bundle内value值
	 * @param str
	 *            bundle其他参数及值
	 */
	public static void sendBroadCast(int key, String intKeyString,
			int intValue, String... str) {
		Log.d(TAG, key + "");
		if (str.length % 2 != 0){
			Log.e(TAG, "广播参数错误");
			Exception e = new Exception();
			e.printStackTrace();
			return;
		}
		Intent intent = new Intent(AdapterConfig.BROADCAST_TXZ_SEND);
		intent.putExtra("key_type", key);
		Bundle b = new Bundle();
		b.putInt(intKeyString, intValue);
		for (int i = 0; i < str.length; i++) {
			b.putString(str[i], str[++i]);
		}
		intent.putExtras(b);
		AdapterApplication.getApp().sendBroadcast(intent);
	}

}
