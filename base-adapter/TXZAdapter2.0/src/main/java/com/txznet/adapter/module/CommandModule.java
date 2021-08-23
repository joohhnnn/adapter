package com.txznet.adapter.module;

import android.util.Log;

import com.txznet.adapter.AdpStatusManager;
import com.txznet.adapter.base.BaseModule;
import com.txznet.adapter.base.util.JsonUtil;
import com.txznet.adapter.conn.service.TXZAdpManager;
import com.txznet.adapter.conn.service.TXZClientMessageProcess;
import com.txznet.adapter.tool.BlueToothTool;
import com.txznet.adapter.tool.MusicTool;
import com.txznet.adapter.tool.RadioTool;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZTtsManager;

/**
 * Created by MarvinYang on 2018/2/9.
 * 命令字初始化管理类
 */

@SuppressWarnings("FieldCanBeLocal")
public class CommandModule extends BaseModule {
    private final String CHOUTI_OPEN = "CHOUTI_OPEN";
    private static CommandModule commandInitManager;

    private CommandModule() {
    }

    /**
     * 单例
     *
     * @return 实例
     */
    public static CommandModule getInstance() {
        if (commandInitManager == null) {
            synchronized (CommandModule.class) {
                if (commandInitManager == null)
                    commandInitManager = new CommandModule();
            }
        }
        return commandInitManager;
    }

    /*
     * 注册命令字
     */
    @Override
    public void init() {
        //TODO 在这里实现本地命令字的注册和实现
        TXZAsrManager.getInstance().regCommand("打开抽屉", CHOUTI_OPEN);
        TXZAsrManager.getInstance().addCommandListener(commandListener);
    }

    /*
     * 注册语音命令字的回调
     */
    private TXZAsrManager.CommandListener commandListener = new TXZAsrManager.CommandListener() {

        @Override
        public void onCommand(String command, String data) {
            if(data.equals("CHOUTI_OPEN")){
                TXZTtsManager.getInstance().speakText("已经打开抽屉了哈哈");
                Log.e("dddd", "已经打开抽屉了哈哈");
                return;

            }else if(data.equals(TXZClientMessageProcess.getInstance().getCmd())){
                TXZTtsManager.getInstance().speakText("已经注册了"+TXZClientMessageProcess.getInstance().getCmd()+"的命令");
                Log.e("dddd", "注册命令成功");
                onCommandReturn(TXZClientMessageProcess.getInstance().getCmd());
                return;

            }



            Log.d(TAG, "onCommand: command:: " + command + ", data:: " + data);
        }
    };

    private void onCommandReturn(String cr){
        TXZAdpManager.getInstance().sendCommandToAll
                (1400,"ComReg",
                        JsonUtil.transParamToJson("cr",cr).getBytes());}
}
