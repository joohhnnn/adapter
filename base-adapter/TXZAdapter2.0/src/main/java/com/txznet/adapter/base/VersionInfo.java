package com.txznet.adapter.base;

public interface VersionInfo {

    /**
     * TODO 根据项目版本修改
     * 当前车机的协议版本 ，请根据实际修改
     * 0 标准安卓协议
     * 1 开头为 1.X 广播协议
     * 2 开关为 2.X 广播协议
     * 3 开头为 3.X AIDL协议
     */
    int PROTO_VERSION = 0;

    /**
     * 1.X 版本协议
     */
    int BROADCAST_VERSION_1_X = 1;

    /**
     * 2.X 版本协议
     */
    int BROADCAST_VERSION_2_X = 2;

    /**
     * AIDL 版本，和2.0协议一起走，用负数表示
     */
    int AIDL_VERSION = 3;

}
