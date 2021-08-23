package com.txznet.adapter.tool;

import com.txznet.adapter.base.BaseTool;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.adapter.base.util.JsonUtil;
import com.txznet.adapter.conn.service.TXZAdpManager;

/**
 * Created by MarvinYang on 2018/2/9.
 * 电台操作工具
 */

@SuppressWarnings({"ConstantConditions", "unused"})
public class RadioTool extends BaseTool {

    /**
     * 打开收音机
     */
    public static void openRadio() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "open_fm");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.open");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.open", null);
                break;
            default:
                break;
        }
    }

    /**
     * 关闭收音机
     */
    public static void closeRadio() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "close_radio");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.close");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.close", null);
                break;
            default:
                break;
        }

    }


    /**
     * 打开调频
     */
    public static void openRadioFM() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "open_fm");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.fm");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.fm", null);
                break;
            default:
                break;
        }

    }

    /**
     * 打开调幅
     */
    public static void openRadioAM() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "open_am");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.am");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.am", null);
                break;
            default:
                break;
        }
    }

    /**
     * 下个台
     */
    public static void next() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "radio_down");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.next");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.next", null);
                break;
            default:
                break;
        }
    }

    /**
     * 上个台
     */
    public static void prev() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "radio_up");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.prev");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.prev", null);
                break;
            default:
                break;
        }
    }

    /**
     * 播放调频
     *
     * @param fm 数值
     */
    public static void playFM(String fm) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "fm_to", "to", fm);
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.fm.to", "fm", fm);
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.fm.to", JsonUtil.transParamToJson("fm", fm).getBytes());
                break;
            default:
                break;
        }
    }

    /**
     * 播放调幅
     *
     * @param am 数值
     */
    public static void playAM(String am) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1050, "action", "ctrl_am", "rate", am);
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.am.to", "am", am);
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.am.to", JsonUtil.transParamToJson("am", am).getBytes());
                break;
            default:
                break;
        }
    }

    /**
     * 搜索电台
     */
    public static void searchRadio() {
        // 默认向下搜个台？？？
        searchRadioToDown();
    }

    /**
     * 向上搜索电台
     */
    public static void searchRadioToUp() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                //Sorry 1.X不支持
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.up");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.up", null);
                break;
            default:
                break;
        }
    }

    /**
     * 向下搜索电台
     */
    public static void searchRadioToDown() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                //Sorry 1.X不支持
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.down");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.down", null);
                break;
            default:
                break;
        }
    }

    /**
     * 随便换个台
     */
    public static void changeRadioRandom() {
        /* switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                //Sorry 1.X不支持
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.random");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.random", null);
                break;
            default:
                break;
        }*/
        // 直接切下个台，省事，协议是没有随机换台的
        next();
    }

    /**
     * 打开电台列表
     */
    public static void openRadioList() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "open_radio_list");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.list.open");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.list.open", null);
                break;
            default:
                break;
        }
    }

    /**
     * 关闭电台列表
     */
    public static void closeRadioList() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "close_radio_list");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.list.close");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.list.close", null);
                break;
            default:
                break;
        }

    }

    /**
     * 打开收藏列表
     */
    public static void openCollectionList() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "open_favorite");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.favour.open");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.favour.open", null);
                break;
            default:
                break;
        }
    }

    /**
     * 关闭收藏列表
     */
    public static void closeCollectionList() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "close_favorite");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.favour.close");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.favour.close", null);
                break;
            default:
                break;
        }
    }

    /**
     * 收藏当前电台
     */
    public static void collectCurrentRadio() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1004, "action", "radio_favorite");
                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.favour");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.favour", null);
                break;
            default:
                break;
        }
    }

    /**
     * 取消收藏当前电台
     */
    public static void unCollectCurrentRadio() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1050, "action", "radio.unfavour");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1050, "radio.unfavour", null);
                break;
            default:
                break;
        }
    }
}

