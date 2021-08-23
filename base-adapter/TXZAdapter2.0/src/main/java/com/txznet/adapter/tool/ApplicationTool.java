package com.txznet.adapter.tool;

import com.txznet.adapter.base.BaseTool;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.adapter.base.util.JsonUtil;
import com.txznet.adapter.conn.service.TXZAdpManager;

/**
 * 应用工具
 * 包含：
 * - 获取UUID
 * - 初始化配置Manager
 * - 打开App
 * - 关闭App
 * Created by MarvinYang on 2018/3/15.
 */

@SuppressWarnings({"FieldCanBeLocal", "ConstantConditions"})
public class ApplicationTool extends BaseTool {

    /**
     * 通过包名打开App
     *
     * @param packName 包名
     */
    public static boolean openAppByPackName(String packName) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1003, "action", "open", "package", packName);
                return true;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1000, "action", "app.open", "package", packName);
                return true;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1000, "app.open", JsonUtil.transParamToJson("package", packName).getBytes());
                return true;
            default:
                return false;
        }
    }

    /**
     * 通过包名关闭App
     *
     * @param packName 包名
     */
    public static boolean closeAppByPackName(String packName) {
        switch (CUR_PROTO_INFO) {
            case BROADCAST_VERSION_1_X:
                BroadCastUtil.sendBroadCast(1003, "action", "close", "package", packName);
                return true;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1000, "action", "app.close", "package", packName);
                return true;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1000, "app.close", JsonUtil.transParamToJson("package", packName).getBytes());
                return true;
            default:
                return false;
        }
    }

}
