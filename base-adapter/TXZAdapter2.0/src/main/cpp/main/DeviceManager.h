//
// Created by MarvinYang on 2019/3/7.
//

#ifndef PROJECTANDROIDSTUDIO_DEVICEMANAGER_H
#define PROJECTANDROIDSTUDIO_DEVICEMANAGER_H

// DSP寄存器地址：0-180,越大稳态角度容易变
#define DEV_PRECISION 0x5FFDFA10
// DSP寄存器地址：0-180，角度下限
#define DEV_DONW 0x5FFDFA18
// DSP寄存器地址：0-180，角度上限
#define DEV_UP 0x5FFDFA1A
// DSP寄存器地址：0-180，默认DSP方向
#define DEV_DOA 0x5FFDFA1C

#define APP_ID(a, b, c, d) ((((a)-0x20)<<8)|(((b)-0x20)<<14)|(((c)-0x20)<<20)|(((d)-0x20)<<26))
#define CHAR_FROM_CAPE_ID_A(id)  (((((unsigned int)(id))>>8) & 0x3f) + 0x20)
#define CHAR_FROM_CAPE_ID_B(id)  (((((unsigned int)(id))>>14) & 0x3f) + 0x20)
#define CHAR_FROM_CAPE_ID_C(id)  (((((unsigned int)(id))>>20) & 0x3f) + 0x20)
#define CHAR_FROM_CAPE_ID_D(id)  (((((unsigned int)(id))>>26) & 0x3f) + 0x20)

#include <fcntl.h>
#include <linux/i2c.h>
#include <linux/i2c-dev.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include "../util/Log.h"

class DeviceManager {

public:
    DeviceManager();

    ~DeviceManager();

    int add(int *a, int *b);

    /**
     *
     * @param dev_i2c_address I2c设备地址
     * @param dev_i2c_slave I2c设备从地址
     * @param dev_sub_address I2c通信寄存器地址
     * @param buffer 数据串
     * @param readLength 需要读取的数据长
     * @return 0 成功 other 失败
     */
    int _i2c_read(const char *dev_i2c_address,
                  __u8 *dev_i2c_slave,
                  __u16 *dev_sub_address,
                  unsigned char *buffer,
                  int readLength);

    unsigned char _i2c_write(unsigned char device_addr,
                             unsigned char sub_addr,
                             unsigned char *buff, int ByteNo);

//    bool write(unsigned char i2c_device_address, int value);
};


#endif //PROJECTANDROIDSTUDIO_DEVICEMANAGER_H
