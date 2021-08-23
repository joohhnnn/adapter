package com.txznet.adapter.module;

import android.util.Log;

import com.txznet.adapter.AdpStatusManager;
import com.txznet.adapter.base.BaseModule;
import com.txznet.adapter.base.util.JsonUtil;
import com.txznet.adapter.base.util.LogUtil;
import com.txznet.adapter.tool.MusicTool;
import com.txznet.adapter.tool.WifiTool;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZSceneManager;

import org.json.JSONObject;

public class SceneModule extends BaseModule {
    private static SceneModule instance;

    private SceneModule() {
    }

    public static SceneModule getInstance() {
        if (instance == null) {
            synchronized (SceneModule.class) {
                if (instance == null)
                    instance = new SceneModule();
            }
        }
        return instance;
    }

    @Override
    public void init() {
        initSceneAll();
        initSceneMusic();
        initCommand();
    }

    private void initCommand(){
        TXZSceneManager.getInstance().setSceneTool(TXZSceneManager.SceneType.SCENE_TYPE_COMMAND, new TXZSceneManager.SceneTool() {
            @Override
            public boolean process(TXZSceneManager.SceneType sceneType, String json) {
                Log.d(TAG, "SCENE_TYPE_COMMAND: "+json);
                return false;
            }
        });
    }

    private void initSceneAll() {
        TXZSceneManager.getInstance().setSceneTool(TXZSceneManager.SceneType.SCENE_TYPE_ALL, new TXZSceneManager.SceneTool() {
            @Override
            public boolean process(TXZSceneManager.SceneType sceneType, String json) {
                LogUtil.d(TAG, "SCENE_TYPE_ALL: " + json);
                String command = JsonUtil.getStringFromJson("cmd", json, "default");
                //TODO 按需实现
                /*
                switch (command) {
                    // 打开wifi
                    case "GLOBAL_CMD_OPEN_WIFI":
                        // 如果WIFI都是默认值
                        if (AdpStatusManager.getInstance().getWifiOpen() == null || AdpStatusManager.getInstance().getWifiConnect() == null) {
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您打开无线网络", true, null);
                            WifiTool.openWifi();
                        }
                        // WIFI是连接的
                        else if (AdpStatusManager.getInstance().getWifiConnect()) {
                            TXZResourceManager.getInstance().speakTextOnRecordWin("当前WIFI已连接", false, null);
                        }
                        // WIFI是开的
                        else if (AdpStatusManager.getInstance().getWifiOpen()) {
                            TXZResourceManager.getInstance().speakTextOnRecordWin("当前WIFI已打开", false, null);
                        }
                        // 其它
                        else {
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您打开WIFI", true, null);
                            WifiTool.openWifi();
                        }
                        return true;
                    // 关闭wifi
                    case "GLOBAL_CMD_CLOSE_WIFI":
                        // 如果WIFI都是默认值
                        if (AdpStatusManager.getInstance().getWifiOpen() == null || AdpStatusManager.getInstance().getWifiConnect() == null) {
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您关闭WIFI", true, null);
                            WifiTool.closeWifi();
                        }
                        // WIFI是开的
                        else if (!AdpStatusManager.getInstance().getWifiOpen()) {
                            TXZResourceManager.getInstance().speakTextOnRecordWin("当前WIFI已关闭", false, null);
                        }
                        // 其它
                        else {
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您关闭WIFI", true, null);
                            WifiTool.closeWifi();
                        }
                        return true;
                }
                */
                return false;
            }
        });
    }

    private void initSceneMusic() {
        TXZSceneManager.getInstance().setSceneTool(TXZSceneManager.SceneType.SCENE_TYPE_MUSIC, new TXZSceneManager.SceneTool() {
            @Override
            public boolean process(TXZSceneManager.SceneType sceneType, String json) {
                LogUtil.d(TAG, "SCENE_TYPE_MUSIC: " + json);
                //TODO 按需实现
/*
                JSONObject jsonObject;
                //酷我音乐拦截，音乐播放器默认使用酷我，交给core自己处理不拦截
                if(AdpStatusManager.getInstance().getMediaType() == null || AdpStatusManager.getInstance().getMediaType().equalsIgnoreCase("kuwo")){
                    return false;
                }
                try {
                    jsonObject = new JSONObject(json);
                    String action = (String) jsonObject.get("action");
                    if (action == null) return false;
                    switch (action) {
                        // 上一首
                        case "prev":
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您播放上一首", true, new Runnable() {
                                @Override
                                public void run() {
                                    MusicTool.prevMusic();
                                }
                            });
                            return true;
                        // 下一首
                        case "next":
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您播放下一首", true, new Runnable() {
                                @Override
                                public void run() {
                                    MusicTool.nextMusic();
                                }
                            });
                            return true;
                        // 播放音乐等相关
                        case "play":
                            // 如果是播放指定类型音乐
                            if (jsonObject.has("model")) {
                                JSONObject model = jsonObject.getJSONObject("model");
                                String title = model.has("title") ? model.getString("title") : "";
                                String artist = (model.has("artist") && !model.getJSONArray("artist").isNull(0)) ? model.getJSONArray("artist").get(0).toString() : "";
                                String album = model.has("album") ? model.getString("album") : "";
                                String keyword = (model.has("keywords") && !model.getJSONArray("keywords").isNull(0)) ? model.getJSONArray("keywords").get(0).toString() : "";
                                TXZResourceManager.getInstance().speakTextOnRecordWin("将为您播放", true, new Runnable() {
                                    @Override
                                    public void run() {
                                        MusicTool.playMusic(AdpStatusManager.getInstance().getMediaType(),title, artist, album, keyword);
                                    }
                                });
                            }
                            // 如果是播放音乐或者打开音乐
                            else {
                                // 这里不好区分是打开音乐还是播放音乐，推荐把打开音乐单独注册命令完成
                                TXZResourceManager.getInstance().speakTextOnRecordWin("将为您开始播放", true, new Runnable() {
                                    @Override
                                    public void run() {
                                        MusicTool.playMusic(AdpStatusManager.getInstance().getMediaType(),null, null, null, null);
                                    }
                                });
                            }
                            return true;
                        // 暂停播放
                        case "pause":
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您暂停播放", true, new Runnable() {
                                @Override
                                public void run() {
                                    MusicTool.pauseMusic();
                                }
                            });
                            return true;
                        // 继续播放
                        case "continue":
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您继续播放", true, new Runnable() {
                                @Override
                                public void run() {
                                    MusicTool.continueMusic();
                                }
                            });
                            return true;

                        // 切换歌曲
                        case "switchSong":
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您随机切歌", true, new Runnable() {
                                @Override
                                public void run() {
                                    MusicTool.randomMusic();
                                }
                            });
                            return true;
                        // 单曲循环
                        case "switchModeLoopOne":
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您切换到单曲循环", true, new Runnable() {
                                @Override
                                public void run() {
                                    MusicTool.singleLoop();
                                }
                            });
                            return true;
                        // 顺序播放
                        case "switchModeLoopOnce":
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您切换到顺序播放模式", true, new Runnable() {
                                @Override
                                public void run() {
                                    MusicTool.listLoop();
                                }
                            });
                            return true;
                        // 列表循环
                        case "switchModeLoopAll":
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您切换到列表循环模式", true, new Runnable() {
                                @Override
                                public void run() {
                                    MusicTool.listLoop();
                                }
                            });
                            return true;
                        // 随机播放
                        case "switchModeRandom":
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您切换到随机播放模式", true, new Runnable() {
                                @Override
                                public void run() {
                                    MusicTool.randomLoop();
                                }
                            });
                            return true;
                        // 退出音乐
                        case "exit":
                            TXZResourceManager.getInstance().speakTextOnRecordWin("将为您退出音乐", true, new Runnable() {
                                @Override
                                public void run() {
                                    MusicTool.exitMusic();
                                }
                            });
                            return true;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

 */
                return false;
            }
        });
    }


    private void initSceneAudio() {

    }
}
