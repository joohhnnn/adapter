package com.txznet.adapter.aidl;


interface ITXZAIDL {

    byte[] sendInvoke(in String packageName,in int commandKey, in String command, in byte[] data);

}
