package com.txznet.adapter.module;

import com.txznet.adapter.AdpApplication;
import com.txznet.adapter.R;
import com.txznet.adapter.base.BaseModule;
import com.txznet.adapter.base.util.SPUtil;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZConfigManager.InitParam;

import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("Convert2Lambda")
public class InitModule extends BaseModule {

	private static InitModule instance;

	public static InitModule getInstance() {
		if (instance == null) {
			synchronized (InitModule.class) {
				if (instance == null)
					instance = new InitModule();
			}
		}
		return instance;
	}

	private InitModule() {
	}

	private String uuid = null;
	private boolean initState = false;
	private final Queue<Runnable> runAfterInit = new LinkedList<>();

	/**
	 * 如果已经初始化则直接运行，否则延迟到初始化之后运行
	 * <p>主要用于Core或FVM模块初始化后才能进行的操作可能提前出现的情况，比如系统消息触发的倒车、FVM模块模式变更等</p>
	 *
	 * @param r 需要运行的Runnable，注意可能会在主线程上运行，出现的Exception会被catch掉
	 */
	public void runOrPostAfterInit(Runnable r) {
		// Synchronized because tasks could be posted at the end of init, after executing all tasks
		synchronized (runAfterInit) {
			if (initState) {
				try {
					r.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				runAfterInit.add(r);
			}
		}
	}

	@Override
	public void init() {
		String appId = AdpApplication.getInstance().getResources().getString(R.string.txz_sdk_init_app_id);
		String appToken = AdpApplication.getInstance().getResources().getString(R.string.txz_sdk_init_app_token);
		String[] wakeUpNews = AdpApplication.getInstance().getResources().getStringArray(R.array.txz_sdk_init_wakeup_keywords);
		// 初始化参数
		InitParam initParam = new InitParam(appId, appToken);
		// 设置出厂唤醒词
		initParam.setWakeupKeywordsNew(wakeUpNews);
		// 检查UUID
//		uuid = SPUtil.getString(AdpApplication.getInstance(), SPUtil.KEY_UUID, null);
		uuid = "mnbv32471";
		if (uuid == null) {
//			uuid = System.currentTimeMillis() + "" + System.getenv().get("USERNAME");
			log("check uuid1:" + uuid);
		} else {
			log("check uuid2:" + uuid);
		}
		initParam.setUUID(uuid);
		// 根据SP设置默认图标
		switch (SPUtil.getString(AdpApplication.getInstance(), SPUtil.KEY_FLOAT_TOOL_TYPE, "top")) {
			case "top":
				initParam.setFloatToolType(TXZConfigManager.FloatToolType.FLOAT_TOP);
				break;
			case "normal":
				initParam.setFloatToolType(TXZConfigManager.FloatToolType.FLOAT_NORMAL);
				break;
			case "none":
				initParam.setFloatToolType(TXZConfigManager.FloatToolType.FLOAT_NONE);
				break;
		}
		// 初始化语音
		TXZConfigManager.getInstance().initialize(
				AdpApplication.getInstance(),
				initParam,
				new TXZConfigManager.InitListener() {
					@Override
					public void onSuccess() {
						if (uuid != null) {
							SPUtil.putString(AdpApplication.getInstance(), SPUtil.KEY_UUID, uuid);
							log("put uuid:" + uuid);
						}
						AdpApplication.getInstance().setTXZConfigWhenInitSuccess();
						// Run postponed tasks
						synchronized (runAfterInit) {
							Runnable r = runAfterInit.poll();
							while (r != null) {
								try {
									r.run();
								} catch (Exception e) {
									e.printStackTrace();
								}
								r = runAfterInit.poll();
							}
							initState = true;
						}
					}

					@Override
					public void onError(int i, String s) {

					}
				}, new TXZConfigManager.ActiveListener() {
					@Override
					public void onFirstActived() {

					}
				});
	}


}
