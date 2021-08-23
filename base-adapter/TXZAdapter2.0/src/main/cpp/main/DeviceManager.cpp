//
// Created by MarvinYang on 2019/3/7.
//

#include <zconf.h>
#include <cstdlib>
#include <cstring>
#include <cerrno>
#include <unistd.h>
#include "DeviceManager.h"
#include "../fm_1388/fm_fm1388.h"

DeviceManager::DeviceManager() {

}

int DeviceManager::add(int *a, int *b) {
    char *s = nullptr;
    LOGD("数据：%08X", main(0x5FFDFD98, &s));
    if (nullptr == a || nullptr == b) {
        LOGE("what are you doing");
        return 1024;
    }
    LOGD("DeviceManager:ADD");
    return *a + *b;
}

DeviceManager::~DeviceManager() {

}

/**
 * 云知声FVM：0x00080800
 * 标准I2C读取
 */
int DeviceManager::_i2c_read(const char *dev_i2c_address,
                             __u8 *dev_i2c_slave,
                             __u16 *dev_sub_address,
                             unsigned char *buffer,
                             int readLength) {
    int i2c_fd, ret;
    i2c_rdwr_ioctl_data i2c_data{};

    // 尝试打开I2C设备
    if ((i2c_fd = open(dev_i2c_address, 0, O_RDWR)) == -1) {
        LOGD("I2c打不开...");
        return -1;
    }
    // 设置超时时限的jiffies
    ret = ioctl(i2c_fd, I2C_TIMEOUT, 1);
    LOGD(ret < 0 ? "I2c set timeOut fail" : "I2c set timeOut Success");
    // 设置收不到ACK时的重试次数
    ret = ioctl(i2c_fd, I2C_RETRIES, 2);
    LOGD(ret < 0 ? "I2c set resend fail" : "I2c set resend Success");
    LOGD("I2c Open Success");
    // 打开I2C成功，准备数据
    // 对I2C read操作需要两个步骤，write和read
    // write:写一个为0的空数据
    // read:...
    i2c_data.nmsgs = 2;
    i2c_data.msgs = (struct i2c_msg *) malloc(i2c_data.nmsgs * sizeof(struct i2c_msg));
    if (!i2c_data.msgs) {
        LOGD("i2c_data malloc error");
        close(i2c_fd);
        return -1;
    }
    // 写寄存器地址
    i2c_data.nmsgs = 2;
    i2c_data.msgs[0].addr = (*dev_i2c_slave);
    i2c_data.msgs[0].len = 1;
    i2c_data.msgs[0].flags = 0;  //read ，如果值为1 则是write操作
    i2c_data.msgs[0].buf = (__u8 *)(dev_sub_address);
    // 读数据
    i2c_data.msgs[1].addr = (*dev_i2c_slave);
    i2c_data.msgs[1].len = (__u16) (readLength);
    i2c_data.msgs[1].flags = I2C_M_RD; //read
    i2c_data.msgs[1].buf = buffer;
    // 发起I2C操作
    ret = ioctl(i2c_fd, I2C_RDWR, &i2c_data);
    if (ret < 0) {
        LOGD("ioctl read error. ret :%d", ret);
        close(i2c_fd);
        free(i2c_data.msgs);
        return -1;
    }
    LOGD("I2C Operate Success");
    close(i2c_fd);
    free(i2c_data.msgs);
    return 0;
//    int i2c_fd, ret;
//    const char *i2c_dev = "/dev/i2c-1";
//    unsigned char slave_address = 0x41;
////    uint32_t sub_address = 0x08000000;
//    uint32_t sub_address = 0x00080800;
//    i2c_rdwr_ioctl_data i2c_data{};
//    if ((i2c_fd = open(i2c_dev, 0, O_RDWR)) == -1) {
//        LOGD("I2c打不开...");
//        return -1;
//    } else {
//        i2c_data.nmsgs = 2;
//        i2c_data.msgs = (struct i2c_msg *) malloc(i2c_data.nmsgs * sizeof(struct i2c_msg));
//        if (!i2c_data.msgs) {
//            LOGD("i2c_date malloc error \n");
//            close(i2c_fd);
//            return -1;
//        }
//        ret = ioctl(i2c_fd, I2C_TIMEOUT, 1);
//        LOGD(ret < 0 ? "I2c set timeOut fail" : "I2c set timeOut Success");
//        ret = ioctl(i2c_fd, I2C_RETRIES, 2);
//        LOGD(ret < 0 ? "I2c set resend fail" : "I2c set resend Success");
//    }
//    LOGD("I2c Open Success");
//
//    i2c_data.nmsgs = 2;
//    i2c_data.msgs[0].addr = slave_address;   //  slave address
//    i2c_data.msgs[0].len = 2;
//    i2c_data.msgs[0].flags = 0;  //read ，如果值为1 则是write操作
//    i2c_data.msgs[0].buf = reinterpret_cast<__u8 *>(&sub_address);
//    /*for(int i=0; i<2;i++){
//        i2c_data.msgs[0].buf[i] = (unsigned char)(0x08>>((2-i-1)*8));
//        LOGD("marvin:sub address0:0x%02X",i2c_data.msgs[0].buf[i]);
//    }*/
////    char *readBuf = static_cast<char *>(malloc(4));
////    i2c_data.msgs[1].addr = slave_address;
////    i2c_data.msgs[1].len = 24;
////    i2c_data.msgs[1].flags = I2C_M_RD; //read
////    i2c_data.msgs[1].buf = (__u8 *) (readBuf);
//    __u32 readBuf = 0;
//    i2c_data.msgs[1].addr = slave_address;
//    i2c_data.msgs[1].len = 16;
//    i2c_data.msgs[1].flags = I2C_M_RD; //read
//    i2c_data.msgs[1].buf = (__u8 *) (&readBuf);
//
//    int i = ioctl(i2c_fd, I2C_RDWR, &i2c_data);
//    if (i < 0) {
//        LOGD("ioctl read error. ret :%d", i);
//        close(i2c_fd);
//        free(i2c_data.msgs);
//        return -1;
//    }
//    for(int count =0;count<16;count++)
//        LOGD("读取数据：[%d]",((char *) (&readBuf))[count]);
//    LOGD("读取数据%d.%d.%d.%d", ((char *) (&readBuf))[0], ((char *) (&readBuf))[4],
//         ((char *) (&readBuf))[8], ((char *) (&readBuf))[12]);
////    LOGD("最终数据:%x.%x.%x.%x.%x.%x",*readBuf,*(readBuf+4),*(readBuf+8),*(readBuf+12),*(readBuf+16),*(readBuf+20));
//    LOGD("最终数据:%x.%x.%x.%x.%x.%x",*(&readBuf),*(&readBuf+4),*(&readBuf+8),*(&readBuf+12),*(&readBuf+16),*(&readBuf+20));
//    return 1;
}

/*****************************************************************************
  i2c写函数，参数1：从设备地址，参数2：寄存器地址，参数3：要写入的数据缓冲区，参数4：写入数据大小
******************************************************************************/
unsigned char
DeviceManager::_i2c_write(unsigned char device_addr, unsigned char sub_addr, unsigned char *buff,
                          int ByteNo) {
    int fd, ret;
    unsigned char buftmp[32];
    struct i2c_rdwr_ioctl_data i2c_data;
    const char *i2c_dev = "/dev/i2c-1";
    //----------------------------------

    device_addr >>= 1;
    //init
    fd = open(i2c_dev, O_RDWR);
    if (fd < 0) {
        LOGD("not have /dev/i2c-0\r\n");
        return -1;
    }

    i2c_data.nmsgs = 1;
    i2c_data.msgs = (struct i2c_msg *) malloc(i2c_data.nmsgs * sizeof(struct i2c_msg));
    if (i2c_data.msgs == NULL) {
        LOGD("malloc error");
        close(fd);
        return -1;
    }

    ioctl(fd, I2C_TIMEOUT, 1);
    ioctl(fd, I2C_RETRIES, 2);

    memset(buftmp, 0, 32);
    buftmp[0] = sub_addr;
    memcpy(buftmp + 1, buff, ByteNo);
    i2c_data.msgs[0].len = ByteNo + 1;;
    i2c_data.msgs[0].addr = device_addr;
    i2c_data.msgs[0].flags = 0;     // 0: write 1:read
    i2c_data.msgs[0].buf = buftmp;
    ret = ioctl(fd, I2C_RDWR, (unsigned long) &i2c_data);
    if (ret < 0) {
        LOGD("write reg %x %x error\r\n", device_addr, sub_addr);
        close(fd);
        free(i2c_data.msgs);
        return 1;
    }
    free(i2c_data.msgs);
    close(fd);

#if 1
    int i;
    LOGD("i2c_write 0x%02x:", buftmp[0]);
    for (i = 0; i < ByteNo; i++) {
        LOGD(" 0x%02x", buftmp[1 + i]);
    }
    LOGD("\n");
#endif
    usleep(2000);
    return 0;
}

//bool DeviceManager::write(int address, int value) {
//    int devStatus, result;
//    char buffer[128];
//    float temp[64];
//
//    // 尝试打开I2C设备
//    devStatus = open(DEV_ADDRESS, O_RDWR | O_CLOEXEC);
//    if (devStatus < 0) {
//        LOGD("DeviceManager:write 打开",DEV_ADDRESS,"失败");
//        return false;
//    }
//    result = ioctl(devStatus, I2C_TENBIT, 0);
//    result = ioctl(devStatus, I2C_SLAVE, "/dev/i2c-0");
//    while (1) {
//        result = iic_read(devStatus, buffer, address, 128);
//        for (int i = 0; i < 64; i++) {
//            int pos = i << 1;
//            int recast = ((uint16_t) buf[pos] << 8) | ((uint16_t) buf[pos + 1]);  //将2个字节合并为一个数据
//            converted = signedMagToFloat(recast) * 0.25;
//            temp[i] = converted;
//        }
//        for (int j = 0; j < 64; j++)
//            LOGD("%.2f\t", temp[j]);
//        sleep(1);
//    }
//}
//
//int DeviceManager::read(int address, int defaultValue) {
//
//    return 0;
//}