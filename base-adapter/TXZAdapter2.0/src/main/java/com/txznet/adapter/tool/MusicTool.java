package com.txznet.adapter.tool;

import com.txznet.adapter.base.BaseTool;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.adapter.base.util.JsonUtil;
import com.txznet.adapter.conn.service.TXZAdpManager;

@SuppressWarnings("ConstantConditions")
public class MusicTool extends BaseTool {

    /**
     * 上一首
     */
    public static void prevMusic(){
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1060, "action", "music.prev");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1060,"music.prev",null);
                break;
            default:
                break;
        }
    }

    /**
     * 下一首
     */
    public static void nextMusic(){
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1060, "action", "music.next");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1060,"music.next",null);
                break;
            default:
                break;
        }
    }

    /**
     * 音乐暂停
     */
    public static void pauseMusic(){
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1060, "action", "music.pause");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1060,"music.pause",null);
                break;
            default:
                break;
        }
    }

    /**
     * 音乐继续播放
     */
    public static void continueMusic(){
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1060, "action", "music.continue");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1060,"music.continue",null);
                break;
            default:
                break;
        }
    }

    /**
     * 播放音乐(分为本地local，蓝牙blue，Usb三种，用type参数设置)
     */
    public static void playMusic(String type,String title,String artist,String album,String keyword){

        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1060, "action", "music.play","type",type,
                        "title", title,
                        "artist", artist,
                        "album", album,
                        "keyword",keyword
                );
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1060, "music.play",
                        JsonUtil.transParamToJson(
                                "type",type,
                                "title", title,
                                "artist", artist,
                                "album", album,
                                "keyword",keyword
                        ).getBytes(), false);
                break;
            default:
                break;
        }
    }

    /**
     * 打开音乐(分为本地local，蓝牙blue，Usb三种，用type参数设置)
     */
    public static void openMusic(String type){

        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1060, "action", "music.open","type",type );
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1060, "music.open",
                        JsonUtil.transParamToJson("type",type).getBytes(), false);
                break;
            default:
                break;
        }
    }

    /**
     * 随机切歌
     */
    public static void randomMusic(){
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1060, "action", "music.random");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1060,"music.random",null);
                break;
            default:
                break;
        }
    }

    /**
     * 单曲循环
     */
    public static void singleLoop(){
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1060, "action", "music.loop.single");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1060,"music.loop.single",null);
                break;
            default:
                break;
        }
    }

    /**
     * 列表循环
     */
    public static void listLoop(){
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1060, "action", "music.loop.all");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1060,"music.loop.all",null);
                break;
            default:
                break;
        }
    }

    /**
     * 随机播放循环
     */
    public static void randomLoop(){
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1060, "action", "music.loop.random");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1060,"music.loop.random",null);
                break;
            default:
                break;
        }
    }

    /**
     * 退出音乐
     */
    public static void exitMusic(){
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1060, "action", "music.close");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1060,"music.close",null);
                break;
            default:
                break;
        }
    }
}
