package com.txznet.adapter.module;

import com.txznet.adapter.base.BaseModule;
import com.txznet.adapter.base.util.LogUtil;
import com.txznet.adapter.tool.ApplicationTool;
import com.txznet.sdk.TXZSysManager;

/**
 * 应用管理
 * Created by MarvinYang on 2018/3/15.
 */

public class AppModule extends BaseModule {

    private static AppModule instance;

    private AppModule() {
    }

    public static AppModule getInstance() {
        if (instance == null) {
            synchronized (AppModule.class) {
                if (instance == null)
                    instance = new AppModule();
            }
        }
        return instance;
    }

    @Override
    public void init() {
        TXZSysManager.getInstance().setAppMgrTool(appMgrTool);
    }

    private TXZSysManager.AppMgrTool appMgrTool = new TXZSysManager.AppMgrTool() {
        @Override
        public void openApp(String packageName) {
            // 没有用工具处理则走默认
            if (ApplicationTool.openAppByPackName(packageName)) {
                LogUtil.d(TAG, "openApp:interface dev");
            } else {
                super.openApp(packageName);
            }
        }

        @Override
        public void closeApp(String packageName) {
            // 没有用工具处理则走默认
            if (ApplicationTool.closeAppByPackName(packageName)) {
                LogUtil.d(TAG, "closeApp:interface dev");
            } else {
                super.closeApp(packageName);
            }
        }
    };
}
