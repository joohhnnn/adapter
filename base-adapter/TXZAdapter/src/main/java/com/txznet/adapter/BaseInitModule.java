package com.txznet.adapter;

import android.content.Intent;

/**
 * @author MarvinYang 2017/05/04
 * 初始化基类
 */
public abstract class BaseInitModule {

	/**
	 * 初始化用
	 */
	public abstract void init();

	/**
	 * 外部发送Intent 进类内部，如广播消息处理
	 * 
	 * @param intent
	 *            用于传递具体参数的
	 */
	public abstract void handleMessage(Intent intent);

}
