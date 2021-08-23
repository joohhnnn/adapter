package com.txznet.adapter.tool;

import com.txznet.adapter.base.BaseTool;

/**
 * Created by MarvinYang on 2018/2/9.
 * 视频管理工具
 * 0709删除，自己写吧，默认协议不提供
 */

@SuppressWarnings("ConstantConditions")
public class VideoTool extends BaseTool {

    /**
     * 打开本地视频
     */
    public static void openLocalVideo() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                break;
            case BROADCAST_VERSION_2_X:
                break;
            case AIDL_VERSION:
                break;
            default:
                break;
        }

    }

    /**
     * 关闭本地视频
     */
    public static void closeLocalVideo() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                break;
            case BROADCAST_VERSION_2_X:
                break;
            case AIDL_VERSION:
                break;
            default:
                break;
        }
    }

    /**
     * 打开USB视频
     */
    public static void openUSBVideo() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                break;
            case BROADCAST_VERSION_2_X:
                break;
            case AIDL_VERSION:
                break;
            default:
                break;
        }
    }

    /**
     * 关闭UBS视频
     */
    public static void closeUSBVideo() {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                break;
            case BROADCAST_VERSION_2_X:
                break;
            case AIDL_VERSION:
                break;
            default:
                break;
        }
    }

}
