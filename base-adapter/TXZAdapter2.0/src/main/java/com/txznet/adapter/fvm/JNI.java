package com.txznet.adapter.fvm;

@SuppressWarnings({"JniMissingFunction", "SpellCheckingInspection"})
public class JNI {

    /**
     * @param paramArrayOfDouble1 未知
     * @param paramArrayOfDouble2 未知
     * @return 未知
     */
    public native int getAngleAndProb(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2);


    /**
     * 无效代码
     */
    @Deprecated
    public native float getCurrentProgress();

    /**
     * @param i2cDeviceAddress I2CAddress(Exap:/dev/i2c-1)
     * @param paramA           A 不用修改
     * @param resetInt         GPIOReset
     * @param i2cWriteBit      G_MAX_I2C_WRITE 不用修改=64
     * @param i2cReadBit       G_MAX_I2C_READ 不用修改=16
     * @return -1 失败 0 成功
     */
    public native int init(String i2cDeviceAddress, char paramA, int resetInt, int i2cWriteBit, int i2cReadBit);

    /**
     * 读取版本号，需要先init
     *
     * @return 版本号
     */
    public native String readVersion();

    /**
     * 设置拾音角
     *
     * @param focusAngle 拾音偏向角度
     * @return 是否成功
     */
    public native int setDirectAngle(int focusAngle);

    /**
     * 设置范围角
     *
     * @param halfRangeAngle 拾音范围角度
     * @return 是否成功
     */
    public native int setDirectWidth(int halfRangeAngle);

    /**
     * 更新固件
     *
     * @param sfsFilePath sfs文件路径
     * @param binFilePath bin文件路径
     * @param address     i2c封装驱动目录
     * @param mode        reset方式 0 低电平 1 高电平
     * @return 是否成功 0 成功
     */
    public native int update(String sfsFilePath, String binFilePath, String address, int mode);

    /**
     * 设置USB及I2S输出模式
     *
     * @param argc    没有用。。。大于2即可
     * @param fvmMode 设置模式    - 正常输出     ZSW2
     *                -------------- USB不输出    ZVS2
     *                -------------- 输出MIC录音  ZMP6
     *                -------------- 输出参考信号 ZSH2
     */
    public native int setMode(int argc, String fvmMode);


}
