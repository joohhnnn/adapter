package com.txznet.adapter.module;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.txznet.adapter.AdapterApplication;
import com.txznet.adapter.AdapterConfig;
import com.txznet.adapter.BaseInitModule;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZAsrManager.CommandListener;
import com.txznet.sdk.TXZTtsManager;

public class CommandInit extends BaseInitModule {
	private final String CHOUTI_OPEN = "CHOUTI_OPEN";
	private final String RADIO_FM_OPEN = "RADIO_FM_OPEN";
	private final String RADIO_FM_CLOSE = "RADIO_FM_CLOSE";
	private final String RADIO_AM_OPEN = "RADIO_AM_OPEN";
	private final String FM = "FM#";
	private final String AM = "AM#";
	private final String RADIO_NEXT = "RADIO_NEXT";
	private final String RADIO_PRE = "RADIO_PRE";
	private final String RADIO_COLLECTION_ADD = "RADIO_COLLECTION_ADD";
	private final String RADIO_CHANGE = "RADIO_CHANGE";
	private final String RADIO_COLLECTION_OPEN = "RADIO_COLLECTION_OPEN";
	private final String RADIO_COLLECTION_CLOSE = "RADIO_COLLECTION_CLOSE";
	private final String RADIO_LIST_OPEN = "RADIO_LIST_OPEN";
	private final String RADIO_LIST_CLOSE = "RADIO_LIST_CLOSE";

	private final String VIDEO_OPEN_USB = "VIDEO_OPEN_USB";
	private final String VIDEO_CLOSE_USB = "VIDEO_CLOSE_USB";
	private final String VIDEO_OPEN_LOCAL = "VIDEO_OPEN_LOCAL";
	private final String VIDEO_CLOSE_LOCAL = "VIDEO_CLOSE_LOCAL";

	private final String PICTURE_USB = "PICTURE_USB";
	private final String PICTURE_LOCAL = "PICTURE_LOCAL";
	private final String PICTURE_NEXT = "PICTURE_NEXT";
	private final String PICTURE_PRE = "PICTURE_PRE";

	private final String TAG = CommandInit.class.getSimpleName();
	static CommandInit instance;
	Context context;

	public static CommandInit getInstance() {
		if (sInstance == null) {
			synchronized (CommandInit.class) {
				if (sInstance == null)
					sInstance = new CommandInit();
			}
		}
		return sInstance;
	}

	@Override
	public void init() {
		initWords();
	}

	public void initWords() {
		Log.d(TAG, "init");
		TXZAsrManager.getInstance().addCommandListener(cListener);
		Resources resources = AdapterApplication.getApp().getResources();
		if (!AdapterConfig.IS_MIRROR) {
			TXZAsrManager.getInstance().regCommand("打开抽屉", CHOUTI_OPEN);
			/********************** 收音机 ********************/
			TXZAsrManager.getInstance().regCommand("打开收音机", "打开调频", RADIO_FM_OPEN);
			TXZAsrManager.getInstance().regCommand("关闭收音机", RADIO_FM_CLOSE);
			TXZAsrManager.getInstance().regCommand("打开调幅", RADIO_AM_OPEN);
			TXZAsrManager.getInstance().regCommandForAM(531, 1602, "AM");
			TXZAsrManager.getInstance().regCommand(
					new String[] { "下一台", "向下搜索电台" }, RADIO_NEXT);
			TXZAsrManager.getInstance().regCommand(
					new String[] { "上一台", "向上搜索电台" }, RADIO_PRE);
			TXZAsrManager.getInstance()
					.regCommand("收藏电台", RADIO_COLLECTION_ADD);
			TXZAsrManager.getInstance().regCommand("换个电台", RADIO_CHANGE);
			TXZAsrManager.getInstance().regCommand("打开收藏列表",
					RADIO_COLLECTION_OPEN);
			TXZAsrManager.getInstance().regCommand("关闭收藏列表",
					RADIO_COLLECTION_CLOSE);
			TXZAsrManager.getInstance().regCommand("打开电台列表", RADIO_LIST_OPEN);
			TXZAsrManager.getInstance().regCommand("关闭电台列表", RADIO_LIST_CLOSE);
			/********************** 视频控制 ********************/
			TXZAsrManager.getInstance().regCommand(
					new String[] { "打开USB视频", "开启USB视频" }, VIDEO_OPEN_USB);
			TXZAsrManager.getInstance().regCommand(
					new String[] { "关闭USB视频", "关掉USB视频" }, VIDEO_CLOSE_USB);
			TXZAsrManager.getInstance().regCommand(
					new String[] { "打开本地视频", "开启本地视频" }, VIDEO_OPEN_LOCAL);
			TXZAsrManager.getInstance().regCommand(
					new String[] { "关闭本地视频", "关掉本地视频" }, VIDEO_CLOSE_LOCAL);
			/********************** 图片控制 ********************/
			TXZAsrManager.getInstance().regCommand(
					new String[] { "查看USB图片", "打开USB图片" }, PICTURE_USB);
			TXZAsrManager.getInstance().regCommand(
					new String[] { "查看本地图片", "打开本地图片" }, PICTURE_LOCAL);
			TXZAsrManager.getInstance().regCommand(
					new String[] { "下一张", "下一张图片", "切换下一张", "切换下一张图片" },
					PICTURE_NEXT);
			TXZAsrManager.getInstance().regCommand(
					new String[] { "上一张", "上一张图片", "切换上一张", "切换上一张图片" },
					PICTURE_PRE);
		}

		TXZAsrManager.getInstance().regCommandForFM(87.5f, 100f, "FM");
	}

	public void reInit() {
		TXZAsrManager.getInstance().removeCommandListener(cListener);
	}

	CommandListener cListener = new TXZAsr() {

		@Override
		public void onCommand(String cmd, String type) {
			// *****************收音机*********************
			if (type == null)
				return;
			Log.d(TAG, "自定义命令：" + cmd);
			// 收音机：打开调频
			if (type.equals(RADIO_FM_OPEN)) {
				BroadCastUtil.sendBroadCast(1004, "action", "open_fm");

				Log.d(TAG, "打开收音机");
			}
			else if (type.equals(CHOUTI_OPEN)) {
				TXZTtsManager.getInstance().speakText("已经打开抽屉了哈哈");
				Log.e("dddd", "已经打开抽屉了哈哈");
			}

			else if (type.equals(RADIO_FM_CLOSE)) {
				Log.d(TAG, "关闭收音机");
			}
			// 收音机：打开调幅
			else if (type.equals(RADIO_AM_OPEN)) {
				BroadCastUtil.sendBroadCast(1004, "action", "open_am");
				Log.d(TAG, "打开调幅");
			}
			// 收音机：调频
			else if (type.startsWith(FM)) {
				BroadCastUtil.sendBroadCast(1004, "action", "fm_to", "open",
						type.substring(FM.length()));
				Log.d(TAG, "调频：" + type.substring(FM.length()));
			}
			// 收音机：调幅
			else if (type.startsWith(AM)) {
				BroadCastUtil.sendBroadCast(1004, "action", "am_to", "open",
						type.substring(AM.length()));
				Log.d(TAG, "调幅：" + type.substring(AM.length()));
			}
			// 收音机：上个台
			else if (type.equals(RADIO_PRE)) {
				BroadCastUtil.sendBroadCast(1004, "action", "radio_up");
				Log.d(TAG, "上个台");
			}
			// 收音机：下个台
			else if (type.equals(RADIO_NEXT)) {
				BroadCastUtil.sendBroadCast(1004, "action", "radio_down");
				Log.d(TAG, "下个台");
			}
			// 收音机：收藏电台
			else if (type.equals(RADIO_COLLECTION_ADD)) {
				BroadCastUtil.sendBroadCast(1004, "action", "radio_favorite");
				Log.d(TAG, "收藏电台");
			}
			// 收音机：换个电台
			else if (type.equals(RADIO_CHANGE)) {
				BroadCastUtil.sendBroadCast(1004, "action", "radio_change");
				Log.d(TAG, "随便换个电台");
			}
			// 收音机：打开收藏列表
			else if (type.equals(RADIO_COLLECTION_OPEN)) {
				BroadCastUtil.sendBroadCast(1004, "action", "open_favorite");
				Log.d(TAG, "打开收藏列表");
			}
			// 收音机：关闭收藏列表
			else if (type.equals(RADIO_COLLECTION_CLOSE)) {
				BroadCastUtil.sendBroadCast(1004, "action", "close_favorite");
				Log.d(TAG, "关闭收藏列表");
			}
			// 收音机：打开电台列表
			else if (type.equals(RADIO_LIST_OPEN)) {
				BroadCastUtil.sendBroadCast(1004, "action", "open_radio_list");
			}
			// 收音机：关闭电台列表
			else if (type.equals(RADIO_LIST_CLOSE)) {
				BroadCastUtil.sendBroadCast(1004, "action", "close_radio_list");
			}
			// ****************视频**********************
			else if (type.equals(VIDEO_OPEN_USB)) {
				BroadCastUtil.sendBroadCast(1005, "action", "open_usb_video");
			} else if (type.equals(VIDEO_CLOSE_USB)) {
				BroadCastUtil.sendBroadCast(1005, "action", "close_usb_video");
			} else if (type.equals(VIDEO_OPEN_LOCAL)) {
				BroadCastUtil.sendBroadCast(1005, "action", "open_local_video");
			} else if (type.equals(VIDEO_CLOSE_LOCAL)) {
				BroadCastUtil
						.sendBroadCast(1005, "action", "close_local_video");
			}
			// ****************图片**********************
			else if (type.equals(PICTURE_USB)) {
				BroadCastUtil.sendBroadCast(1006, "action", "open_usb_picture");
			} else if (type.equals(PICTURE_LOCAL)) {
				BroadCastUtil.sendBroadCast(1006, "action",
						"open_native_picture");
			} else if (type.equals(PICTURE_NEXT)) {
				BroadCastUtil.sendBroadCast(1006, "action", "picture_up");
			} else if (type.equals(PICTURE_PRE)) {
				BroadCastUtil.sendBroadCast(1006, "action", "picture_down");
			}
		}
	};

	@Override
	public void handleMessage(Intent intent) {
		int keyType = intent.getIntExtra("key_type", 0);
		Bundle bundle = intent.getExtras();
		switch (keyType) {
		case 2001:
			int status = bundle.getInt("status");
			Log.d(TAG, "收音机收藏状态：" + status);
			if (status == -1) {
				TXZTtsManager.getInstance().speakText("收藏失败");
			} else if (status == 0) {
				TXZTtsManager.getInstance().speakText("收藏成功");
			} else if (status == 1) {
				TXZTtsManager.getInstance().speakText("收藏成功");
			}
			break;
		default:
			break;
		}
	}
}
