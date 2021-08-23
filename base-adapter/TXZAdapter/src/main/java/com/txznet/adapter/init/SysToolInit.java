package com.txznet.adapter.module;

import android.content.Intent;
import android.util.Log;

import com.txznet.adapter.AdapterConfig;
import com.txznet.adapter.BaseInitModule;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZSysManager;
import com.txznet.sdk.TXZSysManager.ScreenLightTool;
import com.txznet.sdk.TXZSysManager.VolumeMgrTool;

public class SysToolInit extends BaseInitModule {

	private static SysToolInit instance;
	private final String TAG = SysToolInit.class.getSimpleName();

	public static SysToolInit getInstance() {
		if (instance == null) {
			synchronized (SysToolInit.class) {
				if (instance == null)
					instance = new SysToolInit();
			}
		}
		return instance;
	}

	@Override
	public void init() {
		if (!AdapterConfig.IS_MIRROR) {
			// 车机才注册，后视镜用默认
			TXZSysManager.getInstance().setVolumeMgrTool(volumeMgrTool);
			TXZSysManager.getInstance().setScreenLightTool(screenLightTool);
		}

		// TXZSysManager.getInstance().setAppMgrTool(appMgrTool); 有需要再添加
	}

	@Override
	public void handleMessage(Intent intent) {
	}

	/**
	 * 音量控制工具
	 */
	private VolumeMgrTool volumeMgrTool = new VolumeMgrTool() {

		@Override
		public void mute(boolean isMute) {
			Log.d(TAG, "isMute=" + isMute);
			if (isMute)
				BroadCastUtil.sendBroadCast(1002, "action", "volume_mute",
						"mute", "true");
			else {
				BroadCastUtil.sendBroadCast(1002, "action", "volume_mute",
						"mute", "false");
			}
		}

		@Override
		public void minVolume() {
			Log.d(TAG, "minVolume");
			BroadCastUtil.sendBroadCast(1002, "action", "volume_min");
		}

		@Override
		public void maxVolume() {
			Log.d(TAG, "maxVolume");
			BroadCastUtil.sendBroadCast(1002, "action", "volume_max");
		}

		@Override
		public void incVolume() {
			Log.d(TAG, "incVolume");
			BroadCastUtil.sendBroadCast(1002, "action", "volume_up");
		}

		@Override
		public void decVolume() {
			Log.d(TAG, "decVolume");
			BroadCastUtil.sendBroadCast(1002, "action", "volume_down");
		}

	};

	/**
	 * 亮度控制工具
	 */
	private ScreenLightTool screenLightTool = new ScreenLightTool() {

		@Override
		public void incLight() {
			Log.d(TAG, "incLight");
			BroadCastUtil.sendBroadCast(1001, "action", "brightness_up");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您增加亮度",
					true, null);
		}

		@Override
		public void decLight() {
			Log.d(TAG, "decLight");
			BroadCastUtil.sendBroadCast(1001, "action", "brightness_down");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您降低亮度",
					true, null);
		}

		@Override
		public void maxLight() {
			Log.d(TAG, "maxLight");
			BroadCastUtil.sendBroadCast(1001, "action", "brightness_max");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您调至最高亮度",
					true, null);
		}

		@Override
		public void minLight() {
			Log.d(TAG, "minLight");
			BroadCastUtil.sendBroadCast(1001, "action", "brightness_min");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您调至最小亮度",
					true, null);
		}

	};

}
