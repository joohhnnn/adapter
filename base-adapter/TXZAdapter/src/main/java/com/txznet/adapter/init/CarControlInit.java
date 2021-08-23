package com.txznet.adapter.module;

import android.content.Intent;
import android.util.Log;

import com.txznet.adapter.BaseInitModule;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.sdk.TXZCarControlManager;
import com.txznet.sdk.TXZCarControlManager.ACMgrTool;
import com.txznet.sdk.TXZResourceManager;

public class CarControlInit extends BaseInitModule {

	private final String TAG = CarControlInit.class.getSimpleName();
	private static CarControlInit instance;
	private ACMgrTool mgrTool = new ACMgrTool() {

		@Override
		public boolean selectMode(ACMode mode) {
			// 吹面模式
			if (mode == ACMode.MODE_BLOW_FACE) {
				BroadCastUtil.sendBroadCast(1009, "action", "open_surface");
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您切换到吹面模式", true, null);
			}
			// 吹脚模式
			else if (mode == ACMode.MODE_BLOW_FOOT) {
				BroadCastUtil.sendBroadCast(1009, "action", "open_blow");
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您切换到吹脚模式", true, null);
			}
			// 除霜模式
			else if (mode == ACMode.MODE_DEFROST) {
				BroadCastUtil.sendBroadCast(1009, "action", "open_defrost");
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您切换到除霜模式", true, null);
			}
			// 自动模式
			else if (mode == ACMode.MODE_AUTO) {
				BroadCastUtil.sendBroadCast(1009, "action", "open_auto");
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您切换到自动模式", true, null);
			}
			// 吹面吹脚
			else if (mode == ACMode.MODE_BLOW_FACE_FOOT) {
				BroadCastUtil
						.sendBroadCast(1009, "action", "open_blow_surface");
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您切换到吹面吹脚模式", true, null);
			}
			// 吹脚除霜
			else if (mode == ACMode.MODE_BLOW_FOOT_DEFROST) {
				BroadCastUtil
						.sendBroadCast(1009, "action", "open_defrost_blow");
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您切换到吹脚除霜模式", true, null);
			}
			Log.d(TAG, "空调状态切换：" + mode.name());
			return true;
		}

		/**
		 * 打开前除霜
		 */
		@Override
		public boolean openFDef() {
			BroadCastUtil.sendBroadCast(1009, "action", "open_front_defrost");
			TXZResourceManager.getInstance().speakTextOnRecordWin("将为您打开前除霜",
					true, null);
			Log.d(TAG, "打开前除霜");
			return true;
		}

		/**
		 * 关闭前除霜
		 */
		@Override
		public boolean closeFDef() {
			BroadCastUtil.sendBroadCast(1009, "action", "close_front_defrost");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您关闭前除霜",
					true, null);
			Log.d(TAG, "关闭前除霜");
			return true;
		}

		/**
		 * 打开后除霜
		 */
		@Override
		public boolean openADef() {
			BroadCastUtil.sendBroadCast(1009, "action", "open_back_defrost");
			TXZResourceManager.getInstance().speakTextOnRecordWin("将为您打开后除霜",
					true, null);
			Log.d(TAG, "打开后除霜");
			return true;
		}

		/**
		 * 关闭后除霜
		 */
		@Override
		public boolean closeADef() {
			BroadCastUtil.sendBroadCast(1009, "action", "close_back_defrost");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您关闭后除霜",
					true, null);
			Log.d(TAG, "关闭后除霜");
			return true;
		}

		/**
		 * 打开空调
		 */
		@Override
		public boolean openAirConditioner() {
			BroadCastUtil.sendBroadCast(1009, "action", "open_air_conditioner");
			TXZResourceManager.getInstance().speakTextOnRecordWin("将为您打开空调",
					true, null);
			Log.d(TAG, "打开空调");
			return true;
		}

		/**
		 * 关闭空调
		 */
		@Override
		public boolean closeAirConditioner() {
			BroadCastUtil
					.sendBroadCast(1009, "action", "close_air_conditioner");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您关闭空调",
					true, null);
			Log.d(TAG, "关闭空调");
			return true;
		}

		/**
		 * 打开压缩机
		 */
		@Override
		public boolean openCompressor() {
			BroadCastUtil.sendBroadCast(1009, "action", "open_ac");
			TXZResourceManager.getInstance().speakTextOnRecordWin("将为您打开AC开关",
					true, null);
			Log.d(TAG, "打开压缩机");
			return true;
		}

		/**
		 * 关闭压缩机
		 */
		@Override
		public boolean closeCompressor() {
			BroadCastUtil.sendBroadCast(1009, "action", "close_ac");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您关闭AC开关",
					true, null);
			Log.d(TAG, "关闭压缩机");
			return true;
		}

		/**
		 * 打开内循环
		 */
		@Override
		public boolean innerLoop() {
			BroadCastUtil.sendBroadCast(1009, "action", "open_inner_loop");
			TXZResourceManager.getInstance().speakTextOnRecordWin("将为您打开内循环",
					true, null);
			Log.d(TAG, "打开内循环");
			return true;
		}

		/**
		 * 打开外循环
		 */
		@Override
		public boolean outLoop() {
			BroadCastUtil.sendBroadCast(1009, "action", "open_outer_loop");
			TXZResourceManager.getInstance().speakTextOnRecordWin("将为您打开外循环",
					true, null);
			Log.d(TAG, "打开外循环");
			return true;
		}

		/**
		 * 风量调整到speed档
		 */
		@Override
		public boolean ctrlToWSpeed(int speed) {
			BroadCastUtil.sendBroadCast(1009, "to", speed, "action", "wind_to");
			TXZResourceManager.getInstance().speakTextOnRecordWin(
					"已为您调整风量至" + speed, true, null);
			Log.d(TAG, "风量调整到speed档：" + speed);
			return true;
		}

		/**
		 * 加风速一档
		 */
		@Override
		public boolean incWSpeed() {
			BroadCastUtil.sendBroadCast(1009, "action", "wind_up");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您增加风速",
					true, null);
			Log.d(TAG, "加风速一档");
			return true;
		}

		/**
		 * 减风速一档
		 */
		@Override
		public boolean decWSpeed() {
			BroadCastUtil.sendBroadCast(1009, "action", "wind_down");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您减小风速",
					true, null);
			Log.d(TAG, "减风速一档");
			return true;
		}

		/**
		 * 调整温度至
		 */
		@Override
		public boolean ctrlToTemp(int temp) {
			BroadCastUtil.sendBroadCast(1009, "to", temp, "action", "air_to");
			TXZResourceManager.getInstance().speakTextOnRecordWin(
					"已为您将温度调整至" + temp + "度", true, null);
			Log.d(TAG, "调整温度至：" + temp);
			return true;
		}

		/**
		 * 温度调高X
		 */
		@Override
		public boolean incTemp(int temp) {
			BroadCastUtil
					.sendBroadCast(1009, "to", temp, "action", "air_up_to");
			TXZResourceManager.getInstance().speakTextOnRecordWin(
					"已为您将温度增加" + temp + "度", true, null);
			Log.d(TAG, "温度调高X:" + temp);
			return true;
		}

		/**
		 * 温度调低X
		 */
		@Override
		public boolean decTemp(int temp) {
			BroadCastUtil.sendBroadCast(1009, "to", temp, "action",
					"air_down_to");
			TXZResourceManager.getInstance().speakTextOnRecordWin(
					"已为您将温度降低" + temp + "度", true, null);
			Log.d(TAG, "温度调低X：" + temp);
			return true;
		}

		/**
		 * 升高温度
		 */
		@Override
		public boolean incTemp() {
			BroadCastUtil.sendBroadCast(1009, "action", "air_up");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您增加温度",
					true, null);
			Log.d(TAG, "温度调高1");
			return true;
		}

		/**
		 * 降低温度
		 */
		@Override
		public boolean decTemp() {
			BroadCastUtil.sendBroadCast(1009, "action", "air_down");
			Log.d(TAG, "温度调低1");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您降低温度",
					true, null);
			return true;
		}

		/**
		 * 最大温度
		 */
		@Override
		public boolean maxTemp() {
			BroadCastUtil.sendBroadCast(1009, "action", "air_up_highest");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您调整至最高温度",
					true, null);
			Log.d(TAG, "温度最高");
			return true;
		}

		/**
		 * 最小温度
		 */
		@Override
		public boolean minTemp() {
			BroadCastUtil.sendBroadCast(1009, "action", "air_down_lowest");
			TXZResourceManager.getInstance().speakTextOnRecordWin("已为您调整至最低温度",
					true, null);
			Log.d(TAG, "温度最低");
			return true;
		}

	};

	/**
	 * 获取实例
	 * 
	 * @return 实例
	 */
	public static CarControlInit getInstance() {
		if (instance == null) {
			synchronized (CarControlInit.class) {
				if (instance == null)
					instance = new CarControlInit();
			}
		}
		return instance;
	}

	@Override
	public void init() {
		TXZCarControlManager.getInstance().setACMgrTool(mgrTool);
	}

	@Override
	public void handleMessage(Intent intent) {

	}

}
