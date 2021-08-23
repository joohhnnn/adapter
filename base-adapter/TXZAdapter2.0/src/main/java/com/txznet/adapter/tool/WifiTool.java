package com.txznet.adapter.tool;

import com.txznet.adapter.base.BaseTool;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.adapter.conn.service.TXZAdpManager;

public class WifiTool extends BaseTool {

    public static void openWifi() {
        switch (PROTO_VERSION) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1010, "action", "wifi.open");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1010, "wifi.open", null);
                break;
            default:
                break;

        }
    }

    public static void closeWifi() {
        switch (PROTO_VERSION) {
            case BROADCAST_VERSION_1_X:

                break;
            case BROADCAST_VERSION_2_X:
                BroadCastUtil.sendBroadCast(1010, "action", "wifi.close");
                break;
            case AIDL_VERSION:
                TXZAdpManager.getInstance().sendCommandToAll(1010, "wifi.close", null);
                break;
            default:
                break;

        }
    }


}
