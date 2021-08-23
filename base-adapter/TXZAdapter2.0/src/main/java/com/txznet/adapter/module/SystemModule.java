package com.txznet.adapter.module;

import com.txznet.adapter.AdpStatusManager;
import com.txznet.adapter.base.BaseModule;
import com.txznet.adapter.tool.ScreenLightTool;
import com.txznet.adapter.tool.VolumeTool;
import com.txznet.sdk.TXZSysManager;

/**
 * Created by MarvinYang on 2018/2/9.
 * 系统工具管理
 */

public class SystemModule extends BaseModule {
    private static SystemModule systemInitManager;

    private SystemModule() {
    }

    /**
     * 单例
     *
     * @return 实例
     */
    public static SystemModule getInstance() {
        if (systemInitManager == null) {
            synchronized (SystemModule.class) {
                if (systemInitManager == null)
                    systemInitManager = new SystemModule();
            }
        }
        return systemInitManager;
    }

    @Override
    public void init() {
        //TODO 自行实现
        // 音量工具
//        TXZSysManager.getInstance().setVolumeMgrTool(volumeMgrTool);
        // 亮度工具
//        TXZSysManager.getInstance().setScreenLightTool(screenLightTool);
        // 音量范围
//        TXZSysManager.getInstance().setVolumeDistance(AdpStatusManager.VOLUME_MIN, AdpStatusManager.VOLUME_MAX);
    }

    /*
     * 音量处理工具，不需要自己播报，但是调整数值的需要
     */
    private TXZSysManager.VolumeMgrTool volumeMgrTool = new TXZSysManager.VolumeMgrTool() {
        @Override
        public void incVolume() {
            VolumeTool.incVolume();
        }

        @Override
        public void decVolume() {
            VolumeTool.decVolume();
        }

        @Override
        public void maxVolume() {
            VolumeTool.adjustVolumeToMax();
        }

        @Override
        public void minVolume() {
            VolumeTool.adjustVolumeToMin();
        }

        @Override
        public void mute(boolean b) {
            VolumeTool.muteVolume(b);
        }

        @Override
        public boolean isMaxVolume() {
            return VolumeTool.isVolumeMax();
        }

        @Override
        public boolean isMinVolume() {
            return VolumeTool.isVolumeMin();
        }

        @Override
        public boolean decVolume(int i) {
            return VolumeTool.decVolume(i);
        }

        @Override
        public boolean incVolume(int i) {
            return VolumeTool.incVolume(i);
        }

        @Override
        public boolean setVolume(int i) {
            return VolumeTool.adjustVolumeTo(i);
        }


    };

    /*
     * 亮度处理工具，不需要自己播报
     */
    private TXZSysManager.ScreenLightTool screenLightTool = new TXZSysManager.ScreenLightTool() {
        @Override
        public void incLight() {
            ScreenLightTool.incScreenLight();
        }

        @Override
        public void decLight() {
            ScreenLightTool.decScreenLight();
        }

        @Override
        public void maxLight() {
            ScreenLightTool.adjustScreenLightToMax();
        }

        @Override
        public void minLight() {
            ScreenLightTool.adjustScreenLightToMin();
        }

        @Override
        public boolean isMaxLight() {
            return ScreenLightTool.isScreenLightMax();
        }

        @Override
        public boolean isMinLight() {
            return ScreenLightTool.isScreenLightMin();
        }
    };
}
