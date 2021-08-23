package com.txznet.adapter.tool;

import com.txznet.adapter.base.BaseTool;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.adapter.base.util.JsonUtil;
import com.txznet.adapter.conn.service.TXZAdpManager;


@SuppressWarnings("ConstantConditions")
public class AirControlTool extends BaseTool {

    /*
     * 打开空调
     */
    public static void openAirControl() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.air.open");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.air.open", null);
                break;
            default:
                break;
        }
    }

    /*
     * 关闭空调
     */
    public static void closeAirControl() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.air.close");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.air.close", null);
                break;
            default:
                break;
        }
    }

    /*
     * 打开制冷
     */
    public static void openAC() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.open");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.open", null);
                break;
            default:
                break;
        }
    }

    /*
     * 关闭制冷
     */
    public static void closeAC() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.close");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.close", null);
                break;
            default:
                break;
        }
    }

    /*
     * 切换到吹脸模式
     */
    public static void switchModeFace() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.mode","mode", "face");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.mode", JsonUtil.transParamToJson("mode", "face").getBytes());
                break;
            default:
                break;
        }
    }

    /*
     * 切换到吹脸吹脚
     */
    public static void switchModeFaceFoot() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.mode","mode", "face.foot");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.mode", JsonUtil.transParamToJson("mode", "face.foot").getBytes());
                break;
            default:
                break;
        }
    }

    /*
     * 切换到除霜模式
     */
    public static void switchModeDefrost() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.mode","mode", "defrost");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.mode", JsonUtil.transParamToJson("mode", "defrost").getBytes());
                break;
            default:
                break;
        }
    }

    /*
     * 切换到吹脚
     */
    public static void switchModeFoot() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.mode","mode", "foot");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.mode", JsonUtil.transParamToJson("mode", "foot").getBytes());
                break;
            default:
                break;
        }
    }

    /*
     * 切换到吹脚除霜
     */
    public static void switchModeFootDefrost() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.mode","mode", "foot.defrost");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.mode", JsonUtil.transParamToJson("mode", "foot.defrost").getBytes());
                break;
            default:
                break;
        }
    }

    /*
     * 切换到自动模式
     */
    public static void switchModeAuto() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.mode","mode", "auto");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.mode", JsonUtil.transParamToJson("mode", "auto").getBytes());
                break;
            default:
                break;
        }
    }

    /*
     * 切换到内循环
     */
    public static void switchLoopInSide() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.loop.inside");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.loop.inside", null);
                break;
            default:
                break;
        }
    }

    /*
     * 切换到外循环
     */
    public static void switchLoopOutSide() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.loop.outside");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.loop.outside", null);
                break;
            default:
                break;
        }
    }

    /*
     * 打开前除霜
     */
    public static void openFrontDef() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.defrost.front.open");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.defrost.front.open", null);
                break;
            default:
                break;
        }
    }

    /*
     * 关闭前除霜
     */
    public static void closeFrontDef() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.defrost.front.close");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.defrost.front.close", null);
                break;
            default:
                break;
        }
    }

    /*
     * 打开后除霜
     */
    public static void openBehindDef() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.defrost.behind.open");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.defrost.behind.open", null);
                break;
            default:
                break;
        }
    }

    /*
     * 关闭后除霜
     */
    public static void closeBehindDef() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.defrost.behind.close");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.defrost.behind.close", null);
                break;
            default:
                break;
        }
    }

    /*
     * 温度调到X
     */
    public static void ctrlTempTo(int temp) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.temp.to","number",temp);
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.temp.to", JsonUtil.transParamToJson("number", temp).getBytes());
                break;
            default:
                break;
        }
    }

    /*
     * 温度增加X
     */
    public static void incTemp(int temp) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.temp.inc","number",temp);
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.temp.inc", JsonUtil.transParamToJson("number", temp).getBytes());
                break;
            default:
                break;
        }
    }

    /*
     * 温度降低X
     */
    public static void decTemp(int temp) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.temp.dec","number",temp);
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.temp.dec", JsonUtil.transParamToJson("number", temp).getBytes());
                break;
            default:
                break;
        }
    }

    /*
     * 风量提高
     */
    public static void incWind() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.wind.up");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.wind.up", null);
                break;
            default:
                break;
        }
    }

    /*
     * 风量降低
     */
    public static void decWind() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.wind.down");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.wind.down", null);
                break;
            default:
                break;
        }
    }

    /*
     * 风量调到X
     */
    public static void ctrlWindTo(int speed) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1080, "action", "ac.wind.to","number", speed);
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1080, "ac.wind.to", JsonUtil.transParamToJson("number", speed).getBytes());
                break;
            default:
                break;
        }
    }


}
