package com.txznet.adapter.tool;

import com.txznet.adapter.AdpStatusManager;
import com.txznet.adapter.base.BaseTool;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.adapter.base.util.JsonUtil;
import com.txznet.adapter.conn.service.TXZAdpManager;
import com.txznet.sdk.TXZResourceManager;

/**
 * Created by MarvinYang on 2018/2/9.
 * 音量工具类
 */

@SuppressWarnings({"StatementWithEmptyBody", "ConstantConditions"})
public class VolumeTool extends BaseTool {


    /**
     * 音量加一档
     */
    public static void incVolume() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1002, "action", "volume_up");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1030, "action", "volume.up");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1030, "volume.up", null);
                break;
            default:
                break;
        }
    }

    /**
     * 音量减一档
     */
    public static void decVolume() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1002, "action", "volume_down");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1030, "action", "volume.down");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1030, "volume.down", null);
                break;
            default:
                break;
        }
    }

    /**
     * 音量加几
     *
     * @param value 数值
     */
    public static boolean incVolume(int value) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                // 1.X 协议不支持，最新版本仍然没有更新
                return false;
            case BROADCAST_VERSION_2_X:
                // 2.X 协议暂时不支持
                // 0709支持了
                BroadCastUtil.sendBroadCast(1030, "action", "volume.inc", "number", value);
                TXZResourceManager.getInstance().speakTextOnRecordWin("为您将音量增加"+ value, true, null);
                return true;
            case AIDL_VERSION:
                // 3.X 协议
                TXZAdpManager.getInstance().sendCommandToAll(1030, "volume.inc", JsonUtil.transParamToJson("number", value).getBytes());
                TXZResourceManager.getInstance().speakTextOnRecordWin("为您将音量增加"+ value, true, null);
                return true;
            default:
                return false;
        }
    }

    /**
     * 音量减几
     *
     * @param value 数值
     */
    public static boolean decVolume(int value) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X: // 1.X 协议不支持，最新版本仍然没有更新
                return false;
            case BROADCAST_VERSION_2_X:
                // 2.X 协议暂时不支持
                // 0709支持
                BroadCastUtil.sendBroadCast(1030, "action", "volume.dec", "number", value);
                TXZResourceManager.getInstance().speakTextOnRecordWin("为您将音量减小"+ value, true, null);
                return true;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1030, "volume.dec", JsonUtil.transParamToJson("number", value).getBytes());
                TXZResourceManager.getInstance().speakTextOnRecordWin("为您将音量减小"+ value, true, null);
                return true;
            default:
                return false;
        }
    }

    /**
     * 音量调到多少
     *
     * @param value 数值
     */
    public static boolean adjustVolumeTo(int value) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                // 1.X 协议不支持，最新版本仍然没有更新
                return false;
            case BROADCAST_VERSION_2_X:
                // 2.X 协议不支持，0709已支持
                BroadCastUtil.sendBroadCast(1030, "action", "volume.to", "number", value);
                TXZResourceManager.getInstance().speakTextOnRecordWin("为您将音量调整到"+ value, true, null);
                return true;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1030, "volume.to", JsonUtil.transParamToJson("number", value).getBytes());
                TXZResourceManager.getInstance().speakTextOnRecordWin("为您将音量调整到"+ value, true, null);
                return true;
            default:
                return false;
        }
    }


    /**
     * 音量调到最大
     */
    public static void adjustVolumeToMax() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1002, "action", "volume_max");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1030, "action", "volume.max");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1030, "volume.max", null);
                break;
            default:
                break;
        }
    }

    /**
     * 音量调到最小
     */
    public static void adjustVolumeToMin() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1002, "action", "volume_min");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1030, "action", "volume.min");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1030, "volume.min", null);
                break;
            default:
                break;
        }
    }

    /**
     * 静音
     *
     * @param isNeedToMute 是否要静音
     */
    public static void muteVolume(boolean isNeedToMute) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1002, "action", "volume_mute",
                        "mute", isNeedToMute ? "true" : "false");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1030, "action", "volume.mute", "mute", isNeedToMute);
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1030, "volume.mute", JsonUtil.transParamToJson("mute", isNeedToMute).getBytes());
                break;
            default:
                break;
        }
    }

    /**
     * 音量是不是最大的
     *
     * @return 是否
     */
    public static boolean isVolumeMax() {
        return AdpStatusManager.getInstance().getCurVolumeInt() == AdpStatusManager.VOLUME_MAX;
    }

    /**
     * 音量是不是最小的
     *
     * @return 是否
     */
    public static boolean isVolumeMin() {
        return AdpStatusManager.getInstance().getCurVolumeInt() == AdpStatusManager.VOLUME_MIN;
    }
}
