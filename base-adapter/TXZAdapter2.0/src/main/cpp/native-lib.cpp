/**
 * JNI类
 * create By Marvin.yang
 * JNI格式：Java_详细类名（包名）_方法名
 * JNI主要类JNIEnv，详细参见jni.h
 */
#include <jni.h>
#include <string>
#include <malloc.h>
#include "main/DeviceManager.h"
#include "fvm/CxFlash.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_marvin_linux_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint JNICALL
Java_com_marvin_linux_MainActivity_addNum(JNIEnv *env, jobject /* this */,
                                          jint a, jint b) {
    DeviceManager deviceManager;
    return deviceManager.add(&a, &b);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_marvin_linux_MainActivity_i2cRead(JNIEnv *env, jobject /* this */,
                                           jstring device_i2c_address,
                                           jint device_i2c_slave,
                                           jint device_sub_address,
                                           jbyteArray buffer,
                                           jint data_size) {
    DeviceManager deviceManager;
    // 强转到指针
    const char *dev_i2c = env->GetStringUTFChars(device_i2c_address, nullptr);
    jbyte *bBuffer = env->GetByteArrayElements(buffer, nullptr);
    int ret = deviceManager._i2c_read(dev_i2c,
                                      reinterpret_cast<__u8 *>(&device_i2c_slave),
                                      reinterpret_cast<__u16 *>(&device_sub_address),
                                      (unsigned char *) bBuffer,
                                      data_size);
    return ret;
}

extern "C" char *constCharToChar(const char *constc) {
    char *c = nullptr; //初始化char*类型
    c = const_cast<char *>(constc); //const char*类型抓char*类型
    return c;
}


extern "C" JNIEXPORT jint JNICALL
Java_com_txznet_adapter_fvm_JNI_setDirectWidth(JNIEnv *env, jobject instance, jint width) {
    LOGD("native setDirectWidth::%d",width);
    return cxdish_setDirectWidth(width);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_txznet_adapter_fvm_JNI_update(JNIEnv *env, jobject instance, jstring fw_file_name_,
                                       jstring fw_file_name1_,jstring address_,jint mode) {
    LOGD("native update start!");
    const char *fw_file_name = env->GetStringUTFChars(fw_file_name_, 0);
    const char *fw_file_name1 = env->GetStringUTFChars(fw_file_name1_, 0);
    const char *address = env->GetStringUTFChars(address_,0);
    int retCode = cxdish_update_fw(constCharToChar(fw_file_name), constCharToChar(fw_file_name1),address,mode);
    LOGD("native update sfs:: %s bin:: %s address::%s mode::%d retCode::%d ",
         fw_file_name, fw_file_name1, address, mode, retCode);
    env->ReleaseStringUTFChars(fw_file_name_, fw_file_name);
    env->ReleaseStringUTFChars(fw_file_name1_, fw_file_name1);
    env->ReleaseStringUTFChars(address_,address);
    return retCode;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_txznet_adapter_fvm_JNI_setDirectAngle(JNIEnv *env, jobject instance, jint angle) {
    LOGD("native setDirectAngle::%d",angle);
    return cxdish_setDirectAngle(angle);

}

extern "C" JNIEXPORT jstring JNICALL
Java_com_txznet_adapter_fvm_JNI_readVersion
        (JNIEnv *env, jobject instance){
//    char *buf = "00.00.00.000";
    char *buf = (char *) malloc(10);
    LOGD("native readVersion start....");
    int read = cxdish_readVersion(buf);
    LOGD("native readVersion version::%s retCode::%d",buf,read);
//    return env->NewStringUTF(buf);
    jstring res = env->NewStringUTF(buf);
    free(buf);
    LOGD("native readVersion end!!!");
    return res;
}


extern "C" JNIEXPORT jint JNICALL
Java_com_txznet_adapter_fvm_JNI_init(JNIEnv *env, jobject instance, jstring i2c_dev_,
                                     jchar i2c_slave_id, jint reset_gpio, jint max_i2c_write,
                                     jint max_i2c_read) {
    LOGD("native init start...");
    const char *i2c_dev = env->GetStringUTFChars(i2c_dev_, 0);
    int retCode = cxdish_init(constCharToChar(i2c_dev), i2c_slave_id, reset_gpio, max_i2c_write,
                              max_i2c_read);
    LOGD("native init i2c_dev: %s i2c_slave_id: %d reset_gpio: %d max_i2c_write: %d max_i2c_read: %d",
         i2c_dev,i2c_slave_id,reset_gpio,max_i2c_write,max_i2c_read);
    env->ReleaseStringUTFChars(i2c_dev_, i2c_dev);
    LOGD("native init end!!!");
    return retCode;
}

extern "C" JNIEXPORT jfloat JNICALL
Java_com_txznet_adapter_fvm_JNI_getCurrentProgress(JNIEnv *env, jobject instance) {

    // TODO
    return 100;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_txznet_adapter_fvm_JNI_getAngleAndProb(JNIEnv *env, jobject instance, jdoubleArray angle_,
                                                jdoubleArray prob_) {
    jdouble *angle = env->GetDoubleArrayElements(angle_, NULL);
    jdouble *prob = env->GetDoubleArrayElements(prob_, NULL);
    // TODO
    int retcode = cxdish_YzsReadAngleProb(angle, prob);
    LOGD("native getAngleProb retCode::%d",retcode);
    env->ReleaseDoubleArrayElements(angle_, angle, 0);
    env->ReleaseDoubleArrayElements(prob_, prob, 0);
    return retcode;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_txznet_adapter_fvm_JNI_setMode(JNIEnv *env, jobject instance,jint argc,jstring mode) {
    char *argv[4];
    const char *mode_ = env->GetStringUTFChars(mode,0);
    argv[0] = "./cxdish";
    argv[1] = "set-mode";
    argv[2] = const_cast<char *>(mode_);
    int ret = cxdish_setModel(argc,argv);
//    free(argv);
    return ret;
}