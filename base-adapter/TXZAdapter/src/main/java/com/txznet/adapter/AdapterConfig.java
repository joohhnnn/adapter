package com.txznet.adapter;

/**
 * @author MarvinYang
 * 全部参数、Integer String
 */
public class AdapterConfig {

	public static final boolean USE_UUID = false;
	public static final boolean IS_MIRROR = false; //true为后视镜，false为车机
	
	
	public static final String BROADCAST_RECORDWIN_SHOW = "com.txznet.txz.record.show"; // 语音开启
	public static final String BROADCAST_RECORDWIN_DISMISS = "com.txznet.txz.record.dismiss"; // 语音关闭
	
	public static final String BROADCAST_TXZ_RECEIVED = "com.txznet.adapter.recv"; // 同行者收到的广播
	public static final String BROADCAST_TXZ_SEND = "com.txznet.adapter.send"; // 同行者发送的广播
}
