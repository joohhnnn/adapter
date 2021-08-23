package com.txznet.adapter;

/**
 * 车机状态管理类
 * 包含车机当前的全部状态
 */
public class AdpStatusManager {

    private static AdpStatusManager instance;

    private AdpStatusManager() {
    }

    public static AdpStatusManager getInstance() {
        if (instance == null) {
            synchronized (AdpStatusManager.class) {
                if (instance == null)
                    instance = new AdpStatusManager();
            }
        }
        return instance;
    }

    //==============================================================================================
    //====================================       空调         ======================================
    //==============================================================================================
    public static final int AIR_WIND_MAX = 7;// 空调：风量最大值 TODO 根据项目修改
    public static final int AIR_WIND_MIN = 1;// 空调：风量最小值 TODO 根据项目修改
    public static final int AIR_TEMP_MIN = 16;// 空调：温度最小值 TODO 根据项目修改
    public static final int AIR_TEMP_MAX = 32;// 空调：温度最大值 TODO 根据项目修改
    private int curAirWindInt = -1;// 当前风量
    private int curAirTempInt = -1;// 当前温度
    private Boolean isAirACOpen = null;// 当前压缩机是否打开
    private Boolean isFrontDefrostOpen = null;// 前除霜是否打开
    private Boolean isBehindDefrostOpen = null;// 后除霜是否打开
    private Boolean isInnerLoop = null;// 当前是否内循环模式
    //==============================================================================================
    //====================================       收音机        ======================================
    //==============================================================================================
    public static final float RADIO_FM_START = 87.5f;// 收音机：调频起点 TODO 根据项目修改
    public static final float RADIO_FM_END = 108f;// 收音机：调频止 TODO 根据项目修改
    public static final int RADIO_AM_START = 531;// 收音机：调幅起点 TODO 根据项目修改
    public static final int RADIO_AM_END = 1062;// 收音机：调幅止 TODO 根据项目修改
    private Boolean isRadioOpen = null; // 当前收音机是否打开
    private Boolean isFmOpen = null;// 当前是否是调频
    private Boolean isAmOpen = null;// 当前是否是调幅
    //==============================================================================================
    //====================================       蓝牙         ======================================
    //==============================================================================================
    private Boolean isBluetoothOpen = null;// 当前蓝牙开关状态
    private Boolean isBlueConnect = null;// 当前蓝牙连接状态
    //==============================================================================================
    //====================================       媒体         ======================================
    //==============================================================================================
    private String mediaType = null;// 当前媒体播放类型
    //==============================================================================================
    //===================================       音量      ==========================================
    //==============================================================================================
    public static final int VOLUME_MAX = 30;// 音量最大值 TODO 根据项目修改
    public static final int VOLUME_MIN = 1;// 音量最小值 TODO 根据项目修改
    private int curVolumeInt = -1;// 当前音量值
    //==============================================================================================
    //=====================================      亮度     ==========================================
    //==============================================================================================
    public static final int LIGHT_MAX = 7;// 亮度最大值 TODO 根据项目修改
    public static final int LIGHT_MIN = 1;// 亮度最小值  TODO 根据项目修改
    private int curLightInt = -1;// 当前亮度值
    private Boolean isLightAuto = null;// 当前亮度是否为自动模式，自动模式下无法控制亮度

    //==============================================================================================
    //=====================================      WIFI   ============================================
    //==============================================================================================
    private Boolean isWifiOpen = null;// 当前WIFI开关状态
    private Boolean isWifiConnect = null;// 当前WIFI是否连接
    private String curWifiName = null;// 当前连接的WIFI名字
    //==============================================================================================
    //===================================      语音      ===========================================
    //==============================================================================================
    private String versionTXZ = null;
    private String versionWebChat = null;
    private String versionMusic = null;
    private String versionAdapter = null;

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++                             以下Get Set不看                                  ++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public Boolean getRadioOpen() {
        return isRadioOpen;
    }

    public void setRadioOpen(Boolean radioOpen) {
        isRadioOpen = radioOpen;
    }

    public Boolean getFmOpen() {
        return isFmOpen;
    }

    public void setFmOpen(Boolean fmOpen) {
        isFmOpen = fmOpen;
    }

    public Boolean getAmOpen() {
        return isAmOpen;
    }

    public void setAmOpen(Boolean amOpen) {
        isAmOpen = amOpen;
    }

    public Boolean getBluetoothOpen() {
        return isBluetoothOpen;
    }

    public void setBluetoothOpen(Boolean bluetoothOpen) {
        isBluetoothOpen = bluetoothOpen;
    }

    public Boolean getBlueConnect() {
        return isBlueConnect;
    }

    public void setBlueConnect(Boolean blueConnect) {
        isBlueConnect = blueConnect;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public int getCurVolumeInt() {
        return curVolumeInt;
    }

    public void setCurVolumeInt(int curVolumeInt) {
        this.curVolumeInt = curVolumeInt;
    }

    public int getCurLightInt() {
        return curLightInt;
    }

    public void setCurLightInt(int curLightInt) {
        this.curLightInt = curLightInt;
    }

    public Boolean getLightAuto() {
        return isLightAuto;
    }

    public void setLightAuto(Boolean lightAuto) {
        isLightAuto = lightAuto;
    }

    public Boolean getWifiOpen() {
        return isWifiOpen;
    }

    public void setWifiOpen(Boolean wifiOpen) {
        isWifiOpen = wifiOpen;
    }

    public Boolean getWifiConnect() {
        return isWifiConnect;
    }

    public void setWifiConnect(Boolean wifiConnect) {
        isWifiConnect = wifiConnect;
    }

    public String getCurWifiName() {
        return curWifiName;
    }

    public void setCurWifiName(String curWifiName) {
        this.curWifiName = curWifiName;
    }

    public void setVersionTXZ(String versionTXZ) {
        this.versionTXZ = versionTXZ;
    }

    public void setVersionWebChat(String versionWebChat) {
        this.versionWebChat = versionWebChat;
    }

    public void setVersionMusic(String versionMusic) {
        this.versionMusic = versionMusic;
    }

    public void setVersionAdapter(String versionAdapter) {
        this.versionAdapter = versionAdapter;
    }

    public String getVersionTXZ() {
        return versionTXZ;
    }

    public String getVersionWebChat() {
        return versionWebChat;
    }

    public String getVersionMusic() {
        return versionMusic;
    }

    public String getVersionAdapter() {
        return versionAdapter;
    }


    public int getCurAirWindInt() {
        return curAirWindInt;
    }

    public void setCurAirWindInt(int curAirWindInt) {
        this.curAirWindInt = curAirWindInt;
    }

    public int getCurAirTempInt() {
        return curAirTempInt;
    }

    public void setCurAirTempInt(int curAirTempInt) {
        this.curAirTempInt = curAirTempInt;
    }

    public Boolean getAirACOpen() {
        return isAirACOpen;
    }

    public void setAirACOpen(Boolean airACOpen) {
        isAirACOpen = airACOpen;
    }

    public Boolean getFrontDefrostOpen() {
        return isFrontDefrostOpen;
    }

    public void setFrontDefrostOpen(Boolean frontDefrostOpen) {
        isFrontDefrostOpen = frontDefrostOpen;
    }

    public Boolean getBehindDefrostOpen() {
        return isBehindDefrostOpen;
    }

    public void setBehindDefrostOpen(Boolean behindDefrostOpen) {
        isBehindDefrostOpen = behindDefrostOpen;
    }

    public Boolean getInnerLoop() {
        return isInnerLoop;
    }

    public void setInnerLoop(Boolean innerLoop) {
        isInnerLoop = innerLoop;
    }

}
