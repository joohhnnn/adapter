package com.txznet.adapter.tool;

import com.txznet.adapter.AdpStatusManager;
import com.txznet.adapter.base.BaseTool;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.adapter.conn.service.TXZAdpManager;

/**
 * Created by MarvinYang on 2018/2/9.
 * 亮度工具类
 */

@SuppressWarnings("ConstantConditions")
public class ScreenLightTool extends BaseTool {

    /**
     * 亮度加一档
     */
    public static void incScreenLight() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1001, "action", "brightness_up");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1020, "action", "light.up");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1020, "light.up", null);
                break;
            default:
                break;
        }
    }

    /**
     * 亮度减一档
     */
    public static void decScreenLight() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1001, "action", "brightness_down");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1020, "action", "light.down");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1020, "light.down", null);
                break;
            default:
                break;
        }
    }

    /**
     * 亮度调到最大
     */
    public static void adjustScreenLightToMax() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1001, "action", "brightness_max");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1020, "action", "light.max");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1020, "light.max", null);
                break;
            default:
                break;
        }
    }

    /**
     * 亮度调到最小
     */
    public static void adjustScreenLightToMin() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1001, "action", "brightness_min");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1020, "action", "light.min");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1020, "light.min", null);
                break;
            default:
                break;
        }
    }

    /**
     * 亮度是否最大
     *
     * @return 是否
     */
    public static boolean isScreenLightMax() {
        return AdpStatusManager.getInstance().getCurLightInt() == AdpStatusManager.LIGHT_MAX;
    }

    /**
     * 亮度是否最低
     *
     * @return 是否
     */
    public static boolean isScreenLightMin() {
        return AdpStatusManager.getInstance().getCurLightInt() == AdpStatusManager.LIGHT_MIN;
    }

}
