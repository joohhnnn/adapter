/*
* Conexant CxDish
*
* Copyright:   (C) 2013 Conexant Systems
*
*      
*************************************************************************
*  Modified Date:  6/25/2015
*  File Version:   1.0.0.6 
*************************************************************************
*/
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <unistd.h>
#include <stdint.h>
#include <string.h>
#include <getopt.h>
#include <stdarg.h>
#include <ctype.h>
#include <errno.h>
#include <assert.h>
#include <sys/stat.h>
#include <sys/sysmacros.h>
#include "capehostx.h"
#include "CxFlash.h"
#include <unistd.h>
#include <math.h>
#include <fcntl.h>	
//#include "uni_encrypt.h"
#include <sys/ioctl.h>		
#include <linux/i2c-dev.h>
#include <linux/i2c.h>
#include "getcxFlash.h"
#include "../util/Log.h"
#include <sys/time.h>

#if defined(_MSC_VER)
inline void* malloc(size_t size)
{
	return (void*) new char[size];
}
#endif
#define CXDISH_VERSION_STR "1.0.0.6"
#define HAS_GPIO_RESET_PIN 1

#define DEF_MAX_I2C_WRITE_LEN 64 /*max = 0x1000*/
#define DEF_MAX_I2C_READ_LEN 16

#define MAX_DEV_NAME 256
char g_devname[MAX_DEV_NAME]="/dev/i2c-3";
#define I2C_CHIP_ADDRESS    0x41
#define GPIO_RESET_PIN     81

typedef void *HANDLE;

HANDLE YzsDev = NULL;

HANDLE OpenI2cDevice();
void CloseI2cDevice(HANDLE hI2cDevice);
HANDLE OpenGpioDevice(int gpio_reset_pin);
void CloseGpioDevice(HANDLE device);
void SetResetPin(HANDLE hI2cDevice, unsigned long bHigh);
//void *__gxx_personality_v0;
static int quiet = 0;
extern "C" { 
int debugflag = 0;
}
static int ignore_error = 0;
static int golem_intf   = 0;
static int g_gpio_reset_pin = GPIO_RESET_PIN;
static int  g_max_i2c_write = DEF_MAX_I2C_WRITE_LEN;
static int  g_max_i2c_read = DEF_MAX_I2C_READ_LEN;
static unsigned char  g_ChipAddress = I2C_CHIP_ADDRESS;
extern "C" {
	extern unsigned int        g_cbMaxI2cWrite;
	extern unsigned int        g_cbMaxI2cRead;
	extern unsigned char       g_bChipAddress;                   /*Specify the i2c chip address*/
}

static int g_flash_legacy = 0;
static int g_hot_flash = 0;

Command cmd;


#define PUT_CHAR(_x_) putchar(_x_)

#define SENDCMD_MSG_BEGIN      0x98
#define SENDCMD_MSG_END        0x9c
#define SENDCMD_MSG_TYPE_OK    0Xa9
#define SENDCMD_MSG_TYPE_ERROR 0xa2
#define SENDCMD_MSG_DATA_MASK  0xd0

#define HINIBBLE(_x_) (((unsigned char) (_x_)) >> 4) 
#define LONIBBLE(_x_) (((unsigned char) (_x_)) & 0x0f) 

#define PUT_DATA(_data_,_check_sum_) \
	PUT_CHAR( HINIBBLE(_data_) | SENDCMD_MSG_DATA_MASK ); \
	PUT_CHAR( LONIBBLE(_data_) | SENDCMD_MSG_DATA_MASK ); \
	(_check_sum_) += (_data_); 

int i2c_write_imp(HANDLE i2c_dev, unsigned char slave_addr, unsigned long sub_addr,
	unsigned long write_len,unsigned char* write_buf);

int i2c_read_imp(HANDLE i2c_dev, unsigned char slave_addr, 
	unsigned long sub_addr, unsigned long rd_len, unsigned char*rd_buf);


void send_to_golem(bool bIsErr, unsigned char size, unsigned long * pData)
{
	unsigned char type = bIsErr? SENDCMD_MSG_TYPE_ERROR : SENDCMD_MSG_TYPE_OK;
	unsigned char checksum =0;
	PUT_CHAR(SENDCMD_MSG_BEGIN);
	PUT_CHAR( type ) ;
	PUT_DATA( size , checksum) ;
	for(int i=0;i<size;i++)
	{
		unsigned long lData = pData[i] ;
		PUT_DATA(  lData >> 24          , checksum) ;
		PUT_DATA( (lData >> 16 ) & 0xff , checksum) ;
		PUT_DATA( (lData >> 8 )  & 0xff , checksum) ;
		PUT_DATA( (lData)        & 0xff , checksum) ;
	}
	PUT_DATA( checksum , checksum) ;
	PUT_CHAR(SENDCMD_MSG_END);
}

int dspinit()
{
	
	g_bChipAddress = g_ChipAddress;
	g_cbMaxI2cWrite = g_max_i2c_write;
	g_cbMaxI2cRead = g_max_i2c_read;
	return 0;
}

int cxdish_sendcmd(unsigned int argc, char *argv[],double *degreePtr, double *probPtr)
{
	int err = 0;

	if (argc < 2) {
		printf( "Specify a register address\n");
		return -EINVAL;
	}

	int          num_32b_words = argc -2;
	unsigned int app_module_id;
	unsigned int command_id;

	if (sscanf(argv[0], "%x", &app_module_id) != 1)
		return -EINVAL;


	if (sscanf(argv[1], "%x", &command_id) != 1)
		return -EINVAL;

	cmd.num_32b_words =  num_32b_words;
	cmd.command_id    = command_id;
	cmd.reply         = 0;
	cmd.app_module_id = app_module_id;

	for (int n = 0 ; n < num_32b_words ; n++)
	{
		if (sscanf(argv[n+2], "%x", &cmd.data[n]) != 1)
		{
			return -EINVAL;
		}
	}


	int result = SendCmdV(&cmd) ; 
printf("result=%d\n", result);
	if( result >= 0 )
	{
		double degree, prob;
		if(golem_intf)
		{   // send to golem tool.
			send_to_golem(false,result,(unsigned long*)cmd.data);
		}
		else
		{
			// print out the result to screen
			printf("=> ");
			for(int i=0;i<cmd.num_32b_words;i++)
				printf("0x%08x ",cmd.data[i]);
			printf("\n");

			
			
//			int data2 = cmd.data[2];
//			int data4 = cmd.data[4];
			degree = cmd.data[2] * (1.0/double(1 << 23));
			prob   = cmd.data[4] * (0.5/double(1 << 30));
			printf("degree = %f, prob = %f\n", degree, prob);
			if(/* 0 && */degree > 90)
			{
				degree -= 520;
//				degree = 0.0;
				prob = 0.0;

			printf("degree = %f, prob = %f\n", degree, prob);
			}
		}		
		if(degreePtr != NULL && probPtr != NULL){
			*degreePtr = degree;
			*probPtr = prob;
		}
	}
	else
	{
		// error occured
		if(golem_intf)
		{   // send to golem tool.
			send_to_golem(true,1,(unsigned long*)&result);
		}
		else
		{
			// print out the result to screen
			printf("ERROR: failed to call sendcmd : err no=%d \n",result);
		}
	}
	return err;
}

int YzsOpenI2cDevice(){
	YzsDev = OpenI2cDevice();
	//printf("===func10=%s, line10=%d===\n",__func__, __LINE__);
	if(YzsDev)
	{
		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) YzsDev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) YzsDev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

		return 1;
	}
	//printf("===func11=%s, line12=%d===\n",__func__, __LINE__);	
	return 0;

}

int YzsReadAngleAndProb(int argc,char *argv[],double *angle,double *prob){

return cxdish_sendcmd(argc, argv,angle,prob);
}

void YzsClose(){
	CloseI2cDevice(YzsDev);

}

int YzsReadAngleProb(double *angle,double *prob){
	int ret = 0;
	char *argv[3];
/***
	int i;
	for(i = 0; i < 4; i++)
	{
		argv[i] = (char *)malloc(sizeof(char)*4);
	}
***/
	char *address = "0xD308631E";
	char *value = "0x00000142";
	char *extValue = "0x0";
	
	g_bChipAddress = g_ChipAddress;
	g_cbMaxI2cWrite = g_max_i2c_write;
	g_cbMaxI2cRead = g_max_i2c_read;
	
	printf("YzsReadAngleProb--------->1\n");
	ret = YzsOpenI2cDevice();
	printf("ret = %d\n", ret);
	printf("YzsReadAngleProb--------->2\n");
	if(ret != 0){
		argv[0] = (char *)address;
		argv[1] = (char *)value;
		argv[2] = (char *)extValue;
		
		//sprintf(argv[0],"%s",address);
		//sprintf(argv[1],"%s",value);
		//sprintf(argv[2],"%s",extValue);
		printf("argv[0] = %s ; argv[1] = %s ; argv[2] = %s\n",argv[0],argv[1],argv[2]);
		//printf("YzsReadAngleProb--------->3\n");
		YzsReadAngleAndProb(3,argv,angle,prob);
		YzsClose();
		//printf("YzsReadAngleProb--------->4\n");
		return 1;
	}
	//printf("YzsReadAngleProb--------->5\n");
	return 0;
}

////////////////

int setDirectAngle(int angle){
	g_bChipAddress = g_ChipAddress;
	g_cbMaxI2cWrite = g_max_i2c_write;
	g_cbMaxI2cRead = g_max_i2c_read;
	int ret_val = 0;
	HANDLE dev = OpenI2cDevice(); 
	printf("dev=%d\n", dev);
	if(dev)
	{
		//
		// setup the i2c callback functions.
		//
		int temp=0;
		SetupI2cWriteMemCallback( (void *) YzsDev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) YzsDev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

		cmd.num_32b_words = 1;
		cmd.command_id    =  0x00000058;
		cmd.reply         = 0;
		cmd.app_module_id = 0xD308631E;
		cmd.data[0] = (unsigned int )angle;
		temp = SendCmdV(&cmd);
		printf("SendCmdV(&cmd)=%d\n", temp);
		if(temp >= 0)
		{
			struct timeval time; 
			gettimeofday(&time, NULL);  
			printf("[level:INFO] {Setting DSP ANGLE}--->>%ld.%06ld<<----|||ANGLE[%d]\n", (long)time.tv_sec, (long)time.tv_usec, angle);
		}
		else
		{
			printf("setDirectAngle ERROR: Failed to get firmware version!\n");
			ret_val =2;
		}
		CloseI2cDevice(dev);
	}
	else
	{
		printf("setDirectAngle ERROR: Failed to open I2C device!\n");
		ret_val = 1;
	}
	return ret_val;


}

int setDirectWidth(int width){
	g_bChipAddress = g_ChipAddress;
	g_cbMaxI2cWrite = g_max_i2c_write;
	g_cbMaxI2cRead = g_max_i2c_read;
	int ret_val = 0;
	HANDLE dev = OpenI2cDevice(); 
	if(dev)
	{
		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) YzsDev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) YzsDev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

		cmd.num_32b_words = 1;
		cmd.command_id    =  0x00000059;
		cmd.reply         = 0;
		cmd.app_module_id = 0xD308631E;
		cmd.data[0] = (unsigned int )width;
		//printf("SendCmdV(&cmd)=%d\n", SendCmdV(&cmd));
		if( SendCmdV(&cmd) >= 0 )
		{
			struct timeval time; 
			gettimeofday(&time, NULL);  
			printf("[level:INFO] {Setting DSP SECTOR}--->>%ld.%06ld<<----|||SECTOR[%d]\n", (long)time.tv_sec, (long)time.tv_usec, width);
		}
		else
		{
			printf("setDirectWidth ERROR: Failed to get firmware version!\n");
			ret_val =2;
		}
		CloseI2cDevice(dev);
	}
	else
	{
		printf("setDirectWidth ERROR: Failed to open I2C device!\n");
		ret_val = 1;
	}
	return ret_val;
	
}


int setDefault(){
	g_bChipAddress = g_ChipAddress;
	g_cbMaxI2cWrite = g_max_i2c_write;
	g_cbMaxI2cRead = g_max_i2c_read;
	int ret_val = 0;
	unsigned int index = 0;
	HANDLE dev = OpenI2cDevice(); 
	if(dev)
	{
		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) YzsDev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) YzsDev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

		cmd.command_id =  4;/*CMD_SET(CONTROL_APP_EXEC_FILE)*/
		cmd.reply = 0;
		cmd.app_module_id = APP_ID ( 'C', 'T', 'R', 'L');
		cmd.num_32b_words = 1;
		cmd.data[0] = APP_ID( 'Z', 'S', 'W', '2') + (index&0xff);
		if( SendCmdV(&cmd) >= 0 )
		{
			struct timeval time; 
			gettimeofday(&time, NULL);  
			printf("[level:INFO] {Setting DSP MOD}--->>%ld.%06ld<<----|||0[0-90]MOD\n", (long)time.tv_sec, (long)time.tv_usec);
		}
		else
		{
			printf("ERROR: Failed to get firmware version!\n");
			ret_val =2;
		}
		CloseI2cDevice(dev);
	}
	else
	{
		printf("ERROR: Failed to open I2C device!\n");
		ret_val = 1;
	}
	return ret_val;
	
	
}


////////////////
int file_length(FILE *f);
int cxdish_flash(unsigned int argc, char *argv[], const char *reset_address,int mode)
{


	HANDLE            hDevice      = NULL;
	HANDLE	          hGpioDevice  = NULL;
	unsigned char    *pBootloader  =  NULL;
	unsigned char    *pImage       =  NULL;
	unsigned long     cbBootloader = 0;
	unsigned long     cbImage      = 0;

	int               ErrNo        = 0;
	FILE*             hBootLoader  = NULL;
	FILE*             hImage       = NULL;
	void             *buf          = NULL;
	printf("----------begin----\n");
    LOGD("cxdish cxdish_flash ----------begin----");
	printf("--------------%s\n",argv[0]);
    LOGD("cxdish cxdish_flash --------------%s",argv[0]);
	printf("----------1111----%s\n",argv[1]);
    LOGD("cxdish cxdish_flash ----------1111----%s",argv[1]);
	printf("----------finish----\n");
    LOGD("cxdish cxdish_flash ----------finish----");
	do{
		if (argc < 1) {
			printf( "did not specify sfs file path.\n");
            LOGE("cxdish cxdish_flash did not specify sfs file path.");
			return -EINVAL;
		}

		// Attempt to open i2c device.  
		hDevice = OpenI2cDevice();
		if(hDevice == NULL )
		{
			// I2C device is not ready, return 1.
			printf( "I2c device is not present.\n");
            LOGE("cxdish cxdish_flash I2c device is not present.");
			return -ENODEV;
		}
		
		printf("GPIO_RESET_PIN05: %d\n",g_gpio_reset_pin);
        LOGD("cxdish cxdish_flash GPIO_RESET_PING05: %d",g_gpio_reset_pin);
		//hGpioDevice = OpenGpioDevice(g_gpio_reset_pin);
		printf("GPIO_RESET_PIN06: %d\n",g_gpio_reset_pin);
        LOGD("cxdish cxdish_flash GPIO_RESET_PING06: %d",g_gpio_reset_pin);
		/***
		if(hGpioDevice == NULL )
		{
			// GPIO is not available.
			printf( "GPIO %d is not present.\n",g_gpio_reset_pin);
			return -ENODEV;
		}
		***/
		/***
		system("echo 0 > /sys/devices/platform/zwattr/denoise_rst");
		sys_mdelay(RESET_INTERVAL_MS);
		system("echo 1 > /sys/devices/platform/zwattr/denoise_rst");
		***/

		//
		// Load BootLoader from file to memory.
		//
		
		if( argc ==2 )
		{
			printf("=======cxdish_flash01======\n");
            LOGD("cxdish cxdish_flash =======cxdish_flash01======");
			hBootLoader= fopen(argv[1],"rb");
			printf("=======cxdish_flash02======\n");
            LOGD("cxdish cxdish_flash =======cxdish_flash02======");
			if(hBootLoader  )
			{
				// store the file size for later use.
				printf("=======cxdish_flash03======\n");
                LOGD("cxdish cxdish_flash =======cxdish_flash03======");
				cbBootloader = file_length(hBootLoader);  

				// allocate the memory to store the bootlaod. 
				pBootloader = (unsigned char*) malloc(cbBootloader);
				printf("=======cxdish_flash04======\n");
                LOGD("cxdish cxdish_flash =======cxdish_flash04====== %d",__LINE__);
				if (fread(pBootloader, sizeof(char), cbBootloader, hBootLoader) != cbBootloader)
				{
					printf( "read bootload file failed.\n");
                    LOGE("cxdish cxdish_flash read bootload file failed. %d",__LINE__);
					break;
				}
				printf("=======cxdish_flash05======\n");
                LOGD("cxdish cxdish_flash =======cxdish_flash05====== %d",__LINE__);
				fclose(hBootLoader);
				hBootLoader = NULL;
			}
		}


		//
		// Load cape image from file to memory.
		//
		hImage= fopen(argv[0],"rb");
		if(hImage == NULL )
		{
			printf( "Open eeprom file failed.\n");
            LOGE("cxdish cxdish_flash Open eeprom file failed.%d",__LINE__);
			return 1;
		}
		// save the file size for later use.
		cbImage = file_length(hImage);
		// allocate the memory to store the bootlaod.
		pImage = (unsigned char*) malloc(cbImage);
		if (fread(pImage, sizeof(char), cbImage, hImage) != cbImage)
		{
			printf( "read bootload file failed.\n");
            LOGE("cxdish cxdish_flash Open eeprom file failed.%d",__LINE__);
			break;
		}
		fclose(hImage);
		hImage = NULL;

		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) hDevice, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) hDevice, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

#ifdef  HAS_GPIO_RESET_PIN
		// setup the reset pin callback function.
		SetupSetResetPin((void *) hGpioDevice,(fn_SetResetPin) SetResetPin );
#endif // HAS_GPIO_RESET_PIN


		buf = malloc(GetSizeOfBuffer());


		//
		//  Download FW.
		//
		//If the operation completes successfull,the return value is zero. Otherwise,
		//return EERON_* error code. For more information about return code, please refer
		//to cxpump.h file.
		ErrNo = DownloadFW(buf, pBootloader, cbBootloader,pImage, cbImage, g_ChipAddress , SFS_UPDATE_AUTO,g_hot_flash?0:1,g_flash_legacy,reset_address,mode);


		if(ErrNo)
		{
			printf("\nFailed! ERROR CODE = %d\n\n",ErrNo);
            LOGE("cxdish cxdish_flash Failed! ERROR CODE = %d",ErrNo);
			system("aplay -c 2 -r 16000 -f S16_LE update_error.pcm");
            LOGE("cxdish cxdish_flash aplay -c 2 -r 16000 -f S16_LE update_error.pcm");
		}
		else
		{
			printf("Firmware Downloaded Successfully\n");
            LOGD("cxdish cxdish_flash Firmware Downloaded Successfully");
			system("./gpiodrvtest /dev/gpiod21 0");
            LOGD("cxdish cxdish_flash ./gpiodrvtest /dev/gpiod21 0");
			system("./gpiodrvtest /dev/gpiod22 0");
            LOGD("cxdish cxdish_flash ./gpiodrvtest /dev/gpiod22 0");
#if 1
#else
			int fd21,fd22,val;
			int err = 0;

			fd21 = open("/dev/gpiod21", O_WRONLY);
			if (fd21 < 0) 
			{
		    	fprintf(stderr, "failed to open /dev/gpiod21\n");
		    	err = -ENOENT;
				return err;
		    }
			val = 1;
			write(fd21,&val,4);
			
			fd22 = open("/dev/gpiod22", O_WRONLY);
			if (fd22 < 0) 
			{
		    	fprintf(stderr, "failed to open /dev/gpiod22\n");
		    	err = -ENOENT;
				return err;
		    }
			val = 1;
			write(fd22,&val,4);
#endif
			system("aplay -c 2 -r 16000 -f S16_LE update.pcm");
            LOGD("cxdish cxdish_flash aplay -c 2 -r 16000 -f S16_LE update.pcm");
		}


	}while(0);

	// Clean up.
	if(pImage) free(pImage);
	if(pBootloader) free(pBootloader);
	CloseI2cDevice(hDevice);
	if(hGpioDevice)  CloseGpioDevice(hGpioDevice);
	if(  hBootLoader ) fclose(hBootLoader);
	if( hImage ) fclose( hImage );
	if( buf ) free( buf);
	return ErrNo;
}

int getfwver(void)
{
	int ret_val = 0;
	HANDLE dev = OpenI2cDevice(); 
	if(dev)
	{
		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

		cmd.command_id    =  0x0103;
		cmd.reply         = 0;
		cmd.app_module_id = 0xb32d2300;

		if( SendCmdV(&cmd) >0 )
		{
			printf("Firmware Version: %d.%d.%d.%d\n",cmd.data[0],cmd.data[1],cmd.data[2],cmd.data[3] );
		}
		else
		{
			printf("Firmware Version: %d.%d.%d.%d\n",cmd.data[0],cmd.data[1],cmd.data[2],cmd.data[3] );
			printf("getfwver ERROR: Failed to get firmware version!\n");
			ret_val =2;
		}
		CloseI2cDevice(dev);
	}
	else
	{
		printf("ERROR: Failed to open I2C device!\n");
		ret_val = 1;
	}
	return ret_val;
}

static int get_mode()
{

	int ret ;
	char data[5];
	unsigned char index;
	HANDLE dev = OpenI2cDevice();
	if(dev == NULL)
	{
		printf("ERROR: Failed to open I2C device!\n");
		return -1;
	}
	SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
	SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

	cmd.command_id = 0x12f; /*CMD_GET(SOS_RESOURCE);*/
	cmd.reply = 0;
	cmd.app_module_id = APP_ID ( 'S', 'O', 'S', ' ');
	cmd.num_32b_words = 1;
	cmd.data[0] = APP_ID ( 'C', 'T', 'R', 'L');
	ret = SendCmdV(&cmd);
	if( ret <= 0 || ret > MAX_COMMAND_SIZE ) 
		printf("Failed to get tv mode, sendcmd error = %d\n",ret);
	else {
		data[0]= CHAR_FROM_CAPE_ID_A(cmd.data[0]);
		data[1] = CHAR_FROM_CAPE_ID_B(cmd.data[0]);
		data[2] = CHAR_FROM_CAPE_ID_C(cmd.data[0]);
		data[3] = CHAR_FROM_CAPE_ID_D(cmd.data[0]);
		index   = (unsigned char)(cmd.data[0]&0xff);
		if( index == 0)
		{
			printf("Current tv mode = \"%c%c%c%c\" \n",
				data[0],
				data[1],
				data[2],
				data[3]);
		}
		else
			printf("Current tv mode = \"%c%c%c%c|%d\" \n",
			data[0],
			data[1],
			data[2],
			data[3],
			index);
		ret = 0;
	}

	CloseI2cDevice(dev);
	return ret;
}

static int set_mode(unsigned int argc, char *argv[])
{

	int ret;
	int len;
	unsigned int index = 0;
	HANDLE dev ;
	if (argc < 1) {
		printf( "ERROR: Did not specify MODE id.\n");
		return -EINVAL;
	}

	len = strlen(argv[0]);

	if( len < 4 || len == 5) {
		printf( "ERROR: set-mode failed, mode id is not correct\n");
		return -EINVAL;
	}

	if( len >5 && argv[0][4]!='|') 
	{
		printf( "ERROR: set-mode failed, mode id is not correct\n");
		return -EINVAL;
	}

	if ( len >5  ) 
	{
		sscanf(&(argv[0][5]),"%d",&index );
	}


	dev = OpenI2cDevice();
	if(dev == NULL)
	{
		printf("ERROR: Failed to open I2C device!\n");
		return -1;
	}

	SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
	SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

	cmd.command_id =  4;/*CMD_SET(CONTROL_APP_EXEC_FILE)*/
	cmd.reply = 0;
	cmd.app_module_id = APP_ID ( 'C', 'T', 'R', 'L');
	cmd.num_32b_words = 1;
	cmd.data[0] = APP_ID(argv[0][0],
		argv[0][1],
		argv[0][2],
		argv[0][3]) + (index&0xff);

	ret = SendCmdV(&cmd);
	if( ret < 0 || ret > MAX_COMMAND_SIZE ) {
		printf( "ERROR: failed to set mode %s, sendcmd error = %d \n",argv[0],ret );
		LOGE( "ERROR: failed to set mode %s, sendcmd error = %d \n",argv[0],ret );

		ret = -1;
	} else {
		printf( "set mode to \"%s\"\n",argv[0] );
        LOGD( "set mode to \"%s\"\n",argv[0] );
		ret = 0 ;
	}
	CloseI2cDevice(dev);
	return ret;
}



/*
* split a line into tokens
* the content in the line buffer is modified
*/
int split_line(char *buf, char **token, int max_token)
{
	char *dst;
	int n, esc, quote;

	for (n = 0; n < max_token; n++) {
		while (isspace(*buf))
			buf++;
		if (! *buf || *buf == '\n')
			return n;
		/* skip comments */
		if (*buf == '#' || *buf == '!')
			return n;
		esc = 0;
		quote = 0;
		token[n] = buf;
		for (dst = buf; *buf && *buf != '\n'; buf++) {
			if (esc)
				esc = 0;
			else if (isspace(*buf) && !quote) {
				buf++;
				break;
			} else if (*buf == '\\') {
				esc = 1;
				continue;
			} else if (*buf == '\'' || *buf == '"') {
				if (! quote) {
					quote = *buf;
					continue;
				} else if (*buf == quote) {
					quote = 0;
					continue;
				}
			}
			*dst++ = *buf;
		}
		*dst = 0;
	}
	return n;
}
#define MAX_ARGS  32

static int exec_stdin(void)
{
	int narg;
	double angle,prob;
	char buf[256], *args[MAX_ARGS];
	int err = 0;
	HANDLE dev = OpenI2cDevice();
	if(dev == 0)
	{
		printf("ERROR: Failed to open device\n");
		return -1;
	}

	//
	// setup the i2c callback functions.
	//
	SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
	SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

	/* quiet = 1; */
	ignore_error = 1;

	while (fgets(buf, sizeof(buf), stdin)) {
		narg = split_line(buf, args, MAX_ARGS);
		if (narg > 0) {
			if (!strcmp(args[0], "sendcmd"))
//				err = cxdish_sendcmd(narg - 1, args + 1, 0, 0);
			//YzsReadAngleProb(&angle, &prob);
			YzsReadAngleAndProb(narg - 1, args + 1, &angle, &prob);
			if (!strcmp(args[0], "quit"))
			{
				break;
			}
		}
	}
	CloseI2cDevice(dev);
	return err;
}

int getuid(int *uidone, int *uidtwo)  //å‚æ•° uidone ä¸ºuidç¬¬ä¸€ä¸ªéƒ¨åˆ†ï¼Œ å‚æ•° uidtwo ä¸ºuid ç¬¬äºŒéƒ¨åˆ†
{
	dspinit();
	int ret_val = 0;
	HANDLE dev = OpenI2cDevice(); 
	if(dev)
	{
		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

		cmd.num_32b_words = 0;
		cmd.command_id    =  0x00000067;
		cmd.reply         = 0;
		cmd.app_module_id = 0xb32d2300;

		if( SendCmdV(&cmd) >= 0 )
		{
			*uidone = cmd.data[0];
			*uidtwo = cmd.data[1];
			printf("UIDONE: 0x%8x  UIDTWO: 0x%8x\n",cmd.data[0],cmd.data[1]);
		}
		else
		{
			printf("ERROR: Failed to get firmware version!\n");
			ret_val =2;
		}
		CloseI2cDevice(dev);
	}
	else
	{
		printf("ERROR: Failed to open I2C device!\n");
		ret_val = 1;
	}
	return ret_val;
	
}

int openfile();
int closefile();
int partitionspace()
{
	int ret_val = 0;
	HANDLE dev = OpenI2cDevice(); 
	if(dev)
	{
		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);
		cmd.num_32b_words = 4;
		cmd.command_id    =  0x0000007C;
		cmd.reply         = 0;
		cmd.app_module_id = 0xb32d2300;
		cmd.data[0] = 0xca3ba500;
		cmd.data[1] = 0x00000005;
		cmd.data[2] = 0x00000000;
		cmd.data[3] = 0x00000300;
		ret_val = SendCmdV(&cmd);
		if( ret_val >= 0 )
		{
			printf("Partition is success \n");
		}
		else
		{
			printf("[partitionspace]ERROR: Failed to get firmware version!\n");
			ret_val =2;
		}
		CloseI2cDevice(dev);
	}
	else
	{
		printf("ERROR: Failed to open I2C device!\n");
		ret_val = 1;
	}
	return ret_val;
	
}

int closefile()
{
	int ret_val = 0;
	HANDLE dev = OpenI2cDevice(); 
	if(dev)
	{
		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);
		cmd.num_32b_words = 1;
		cmd.command_id    =  0x00000078;
		cmd.reply         = 0;
		cmd.app_module_id = 0xb32d2300;
		cmd.data[0] = 0xca3ba500;

		if( SendCmdV(&cmd) >= 0 )
		{
			printf("Close file is success \n");
		}
		else
		{
			printf("[closefile]ERROR: Failed to get firmware version!\n");

			ret_val =2;
		}
		CloseI2cDevice(dev);
	}
	else
	{
		printf("ERROR: Failed to open I2C device!\n");
		ret_val = 1;
	}
	return ret_val;


}


int openfile()
{
	int ret_val = 0;
	HANDLE dev = OpenI2cDevice(); 
	if(dev)
	{
		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);
		cmd.num_32b_words = 2;
		cmd.command_id    =  0x00000077;
		cmd.reply         = 0;
		cmd.app_module_id = 0xb32d2300;
		cmd.data[0] = 0xca3ba500;
		cmd.data[1] = 0x00000300;
		if( SendCmdV(&cmd) >= 0 )
		{
			printf("Open is success \n");
		}
		else
		{
			printf("[openfile]  ERROR: Failed to get firmware version!\n");
			ret_val =2;
		}
		CloseI2cDevice(dev);
	}
	else
	{
		printf("ERROR: Failed to open I2C device!\n");
		ret_val = 1;
	}
	return ret_val;


}

int readfile(int cryptdata[], int length)
{
	int ret_val = 0;
	HANDLE dev = OpenI2cDevice();
//	COMMAND_OF_SIZE(1024) cmd;
	if(dev)
	{
		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);
		int postion = 0;
		int tmp = 0;
		while(1)
		{
			cmd.num_32b_words = 2;
			cmd.command_id    =  0x0000017A;
			cmd.reply         = 0;
			cmd.app_module_id = 0xb32d2300;
			cmd.data[0] = 0xca3ba500;
			cmd.data[1] = 0x0000000b;
			
			if(length <= 11)
				cmd.data[1] = length;
			
				
			if( SendCmdV(&cmd) >= 0 )
			{
				memcpy(cryptdata + postion, cmd.data, cmd.num_32b_words * 4);
/*				printf("Read is success ;position [%d] length [%d] cmd.num_32b_words [%d]\n", postion, length, cmd.num_32b_words);
				printf("=> ");
				for(int i=0;i<cmd.num_32b_words;i++)
					printf("0x%08x ",*(cryptdata + postion + i));
				printf("\n");
*/				
				if(length <= 11)
					break;
			}
			else
			{
				printf("[readfile ]  ERROR: Failed to get firmware version!\n");
				ret_val =2;
				break;
			}
			
			
			postion += cmd.num_32b_words;
			length -= cmd.num_32b_words;
			

			
		}

		CloseI2cDevice(dev);
	}
	else
	{
		printf("ERROR: Failed to open I2C device!\n");
		ret_val = 1;
	}
	return ret_val;


}


int writefile(int buffer[], int length)
{
	int ret_val = 0;
	HANDLE dev = OpenI2cDevice();
//	Command2048 cmd;
	int i;
	int count;
	int tmp;
	if(dev)
	{
		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

		count = 0;
		while( count * 12 - length < 0 )
//		while( count < 3 )
		{
			cmd.command_id    =  0x0000007A;
			cmd.reply         = 0;
			cmd.app_module_id = 0xb32d2300;
			cmd.data[0] = 0xca3ba500;

			if(length - count * 12 <= 12)
				tmp = length - count * 12;
			else
				tmp = 12;
			
//			printf("tmp = [%d]\n", tmp);
			for(i = 0; i < tmp; i++)
			{
				cmd.data[i + 1] = buffer[i + count * 12];
			}

			if(0 && tmp <= 12)
			{
				for(i = 0; i < tmp; i++)
				{
					printf("cmd.data[i + 1] = [0x%8x]\n", cmd.data[i + 1]);
				}
			}

			cmd.num_32b_words = tmp + 1;
			count ++;
			
			if( SendCmdV(&cmd) >= 0 )
			{
				printf("writefile is success \n");
			}
			else
			{
				printf("[writefile]   ERROR: Failed to get firmware version!\n");
				break;
				ret_val =2;
			}
			
			usleep(500);
		}

		CloseI2cDevice(dev);
	}
	else
	{
		printf("ERROR: Failed to open I2C device!\n");
		ret_val = 1;
	}
	return ret_val;


}


int encryption()
{
	int uidone;
	int uidtwo;
	int ret;
	char serial[8];
//	closefile();
#if 1
	getfwver();
	
//	printf("\n\n\n\n");
	getuid(&uidone, &uidtwo);
	
	memcpy(serial, (char *)&uidtwo, 4);
	memcpy(serial+4, (char *)&uidone, 4);
	
//	printf("\n\n\n\n");
	ret = partitionspace();
	if(ret != 0)
		return -1;
//	printf("\n\n\n\n");
#endif

	closefile();

//	printf("\n\n\n\n");
	ret = openfile();
	if(ret != 0)
		return -1;
//	printf("\n\n\n\n");
//	printf("0000\n\n\n\n");
	int ibuf[4096];// = (int *) malloc(8096);
//	printf("1111\n\n\n\n");
	uint8_t buf[16384];// = (uint8_t*)malloc(16384);
// printf("2222\n\n\n\n");
//	uni_encrypt((uint8_t*)serial, 8 , buf, 16*1024);
	int i;
	for(i = 0; i < 4 * 1024; i ++)
	{
		ibuf[i] = (buf[i * 4] ) + (buf[i * 4 + 1] << 8) + (buf[i * 4 + 2] << 16) + (buf[i * 4 + 3] << 24);
//		ibuf[i] = i;
	}
/*
	int buffer[1056];
	int i;
	for(i = 0; i < 1056; i ++)
	{
		buffer[i] = i;	
	}
	printf("\n\n\n\n");
*/
	writefile(ibuf, 4 * 1024);
	
//	free(buf);
//	printf("\n\n\n\n");
	closefile();
//	printf("\n\n\n\n");
#if 0
	int cryptdata[4096];
	openfile();
//	printf("\n\n\n\n");
	readfile(cryptdata, 4096);
//	printf("\n\n\n\n");
	//int i = 0;
	for(i = 0; i < 4096; i++)
	{
		if(cryptdata[i] != ibuf[i])
		{
			printf("position = [%d] cryptdata[] = [0x%8x] ibuf[] = [0x%8x]\n", i, cryptdata[i], ibuf[i]);
			
		}
	}
	
//	printf("\n\n\n\n");
	closefile();
#endif
	return 1;
}


int getencryptdata(int cryptdata[], int length) //å‚æ•° â€œcryptdataâ€ èŽ·å–å½•å…¥DSPçš„æ•°æ®çš„buffï¼Œ å‚æ•°length ä¸ºbuffçš„é•¿åº¦
{
	dspinit();
	int ret = openfile();
	if(ret == 0)
		readfile(cryptdata, length);
	else
		return -1;
	closefile();
	
	return 0;
} 



static int help(void)
{
	printf("Conexant CxDish version: %s\n",CXDISH_VERSION_STR); 
	printf("Usage: cxdish <options> [command]\n");
	printf("\nAvailable options:\n");
	printf("  -h, --help                this help\n");
	printf("  -D, --device STR          specifies the i2c device path, default is '%s'\n", "/dev/i2c-0");	
	printf("  -r, --reset GPIO_NUM      specifies the RESET GPIO pin number, default is '%d'\n", GPIO_RESET_PIN );	
	printf("  -a, --address I2C_ADDRESS specifies i2c address, default is '0x%02x'\n",I2C_CHIP_ADDRESS) ;
	printf("  -d, --debug LEVEL         specifies device level from 0 to 2\n");
	printf("  -W, --max_write           specifies max writing lenght,default is %d, max= 4000\n",DEF_MAX_I2C_WRITE_LEN);
	printf("  -R, --max_read            specifies max writing lenght,default is %d, max= 4000\n",DEF_MAX_I2C_READ_LEN);
	printf("  -L, --legacy              enables legacy mode flash for legacy device\n");
	printf("  -H, --hot_flash           download image during firmware is running\n");
	printf("  -d, --debug               enables i2c dump\n");
	printf("  -p  --angle_prob          specifies the angle and prob\n");
	printf("  -v, --version             prints cxdish version\n");
	printf("  -q, --quiet               enables quiet mode\n");
	printf("\nAvailable commands:\n");
	printf("  sendcmd STREAM_ID COMMAND [arg]\n");
	printf("  flash SFS_FILE [BOOTLOADER] \n");
	printf("  set-mode MODE\n");
	printf("  get-mode\n");
	printf("  fw-version\n");
	printf("  encryption");
	return 0;
}

HANDLE device =NULL;
/**
 *  参数赋值
 * @param i2c_dev
 * @param i2c_slave_id
 * @param reset_gpio
 * @param max_i2c_write
 * @param max_i2c_read
 * @return
 */
int cxdish_init(char *i2c_dev, unsigned char i2c_slave_id, int reset_gpio,int max_i2c_write, int max_i2c_read)
{
	//uint32_t  addr;
	if(i2c_dev != NULL)
	{
		strncpy(g_devname,i2c_dev,MAX_DEV_NAME-1);
		printf("g_devname=%s\n",g_devname);
        LOGD("cxdish cxdish_init g_devname=%s",g_devname);
	}else{
        LOGE("cxdish cxdish_init g_devname %s error!!",i2c_dev);
		return -1;
	}
	
	if(reset_gpio)
	{
		g_gpio_reset_pin = reset_gpio;
		printf("GPIO_RESET_PIN00: %d\n",g_gpio_reset_pin);
        LOGD("cxdish cxdish_init GPIO_RESET_PIN00: %d",g_gpio_reset_pin);
	}else{
        LOGE("cxdish cxdish_init GPIO_RESET_PIN %d error!!!",reset_gpio);
		return -1;
	}
	
	if(i2c_slave_id)
	{
		//sscanf(i2c_slave_id,"%x",&addr);
		g_ChipAddress =(uint8_t)i2c_slave_id;
		printf("I2C_CHIP_ADDRESS:0x%02x\n",g_ChipAddress);
        LOGD("cxdish cxdish_init I2C_CHIP_ADDRESS:0x%02x",g_ChipAddress);
	}else{
        LOGE("cxdish cxdish_init I2C_CHIP_ADDRESS %s error!!!",i2c_slave_id);
		return -1;
	}
	
	if(max_i2c_write)
	{
		g_max_i2c_write = max_i2c_write;
		printf("G_MAX_I2C_WRITE: %d\n",g_max_i2c_write);
        LOGD("cxdish cxdish_init G_MAX_I2C_WRITE: %d",g_max_i2c_write);
	}else{
        LOGE("cxdish cxdish_init G_MAX_I2C_WRITE %d error!!!",max_i2c_write);
		return -1;
	}
	
	if(max_i2c_read)
	{
		g_max_i2c_read = max_i2c_read;
		printf("G_MAX_I2C_READ: %d\n",g_max_i2c_read);
        LOGD("cxdish cxdish_init G_MAX_I2C_READ: %d",g_max_i2c_read);
	}else{
        LOGE("cxdish cxdish_init G_MAX_I2C_READ %d error!!!",max_i2c_read);
		return -1;
	}
	
	return 0;
}

int cxdish_readVersion(char *buf)
{
	int ret_val;
	//char cmd_id[4];
	//ret_val = (char *)malloc(10);

	//update I2C settings
	g_bChipAddress = g_ChipAddress;
	g_cbMaxI2cWrite = g_max_i2c_write;
	g_cbMaxI2cRead = g_max_i2c_read;	
    LOGD("%s:%d cxdish_readVersion",__FILE__,__LINE__);
	if(buf == NULL)
	{
		ret_val = 1;
	}
    LOGD("%s:%d cxdish_readVersion",__FILE__,__LINE__);
	HANDLE dev = OpenI2cDevice();
    LOGD("%s:%d cxdish_readVersion",__FILE__,__LINE__);
	if(dev)
	{
		//
		// setup the i2c callback functions.
		//
		SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
		SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);
        LOGD("%s:%d cxdish_readVersion",__FILE__,__LINE__);
		cmd.command_id    =  0x0103;
		cmd.reply         = 0;
		cmd.app_module_id = 0xb32d2300;
        LOGD("%s:%d cxdish_readVersion",__FILE__,__LINE__);
		if( SendCmdV(&cmd) >0 )
		{
			//printf("*********getfwver******\n");
			printf("---*---Firmware Version: %d.%d.%d.%d---*---\n",cmd.data[0],cmd.data[1],cmd.data[2],cmd.data[3] );
            LOGD("cxdish cxdish_readVersioin ---*---Firmware Version: %d.%d.%d.%d---*---",cmd.data[0],cmd.data[1],cmd.data[2],cmd.data[3]);
			sprintf(buf, "%d.%d.%d.%d\n", cmd.data[0],cmd.data[1],cmd.data[2],cmd.data[3]);
			ret_val = 0;
		}
		else
		{
			printf("---*---Firmware Version: %d.%d.%d.%d---*---\n",cmd.data[0],cmd.data[1],cmd.data[2],cmd.data[3] );
            LOGE("cxdish cxdish_readVersioin ---*---Firmware Version: %d.%d.%d.%d---*---",cmd.data[0],cmd.data[1],cmd.data[2],cmd.data[3]);
			printf("getfwver ERROR: Failed to get firmware version!\n");
            LOGE("cxdish cxdish_readVersion getfwver ERROR: Failed to get firmware version!");
            sprintf(buf, "ERROR");
			ret_val =2;
		}
        LOGD("%s:%d cxdish_readVersion",__FILE__,__LINE__);
		CloseI2cDevice(dev);
        LOGD("%s:%d cxdish_readVersion",__FILE__,__LINE__);
	}
	else
	{
		printf("ERROR: Failed to open I2C device!\n");
        LOGE("cxdish cxdish_readVersion ERROR: Failed to open I2C device!");
		ret_val = 1;
	}
	return ret_val;
}

int cxdish_update_fw(char *fw_file_name,char *fw_file_name1,const char *reset_address,int mode)
{
	char *str[2];
	printf("======cxdish_update_fw01======\n");
	LOGD("cxdish cxdish_update_fw ======cxdish_update_fw01======");
	printf("======cxdish_update_fw02======\n");
    LOGD("cxdish cxdish_update_fw ======cxdish_update_fw02======");
	if( (fw_file_name != NULL) && (fw_file_name1 != NULL))
	{
		printf("cxdish_update_fw success!\n");
        LOGD("cxdish cxdish_update_fw success!");
		//sprintf(str[0],"%s",fw_file_name); //ÐèÒª¸østr·ÖÅäÄÚ´æ
		str[0] = fw_file_name;
		str[1] = fw_file_name1;
		printf("cxdish_update_fw  : fw_file_name=%s\n", str[0]);
        LOGD("cxdish cxdish_update_fw : fw_file_name=%s",str[0]);
		printf("cxdish_update_fw  : fw_file_name1=%s\n", str[1]);
        LOGD("cxdish cxdish_update_fw  : fw_file_name1=%s",str[1]);
	}else{
		printf("cxdish_update_fw ERROR : fw_file_name=%s\n", str[0]);
        LOGE("cxdish cxdish_update_fw ERROR : fw_file_name=%s",str[0]);
		printf("cxdish_update_fw ERROR : fw_file_name1=%s\n", str[1]);
        LOGE("cxdish cxdish_update_fw ERROR : fw_file_name1=%s",str[0]);
	}
	return cxdish_flash(2, str,reset_address,mode) ? 1 : 0;
}
//cxdish_update_fw("F6.26.3.17_80_S48M16.sfs","iflash.bin");

int cxdish_YzsReadAngleProb(double *angle,double *prob)
{
	return YzsReadAngleProb(angle, prob);
}

int cxdish_setDirectAngle(int angle)
{
	return setDirectAngle(angle);
}

int cxdish_setDirectWidth(int width)
{
	return setDirectWidth(width);
}

int function_play(int argc, char *argv[],const char *reset_address,int mode)
{
	int morehelp= 0;
    int read_stdin = 0;
    uint32_t  addr;
    int pin;
    double angle,prob;
	

//	char *fw_file_name = "F6.26.3.17_80_S48M16.sfs";
//	char *fw_file_name1 = "iflash.bin";
//	cxdish_update_fw(fw_file_name, fw_file_name1);



/***
	do{
			cxdish_YzsReadAngleProb(&angle, &prob);
			cxdish_setDirectAngle(0);
			cxdish_setDirectWidth(90);
			printf("The angle is [%lf], the prob is [%lf]\n", angle, prob);
			sleep(2);
	}while(1);
***/

/***
	char *i2c_dev = "/dev/i2c-4";
	unsigned char i2c_slave_id = 0x41;
	int reset_gpio = 184;
	cxdish_init(i2c_dev, i2c_slave_id, reset_gpio);
***/
	
	static const struct option long_option[] =
	{
		{"help",no_argument, NULL, 'h'},
		{"quiet", no_argument, NULL, 'q'},
		{"debug", required_argument, NULL, 'd'},
		{"version", no_argument, NULL, 'v'},
		{"angle_prob", no_argument, NULL, 'p'},
		{"stdin", no_argument, NULL, 's'},
		{"golem",required_argument,NULL,'g'},
		{"device",required_argument,NULL,'D'},
		{"address",required_argument,NULL,'a'},
		{"reset",required_argument,NULL,'r'},
		{"max_write",required_argument,NULL,'W'},
		{"max_read",required_argument,NULL,'R'},
		{"legacy",no_argument,NULL,'L'},
		{"hot_flash",no_argument,NULL,'H'},
		{NULL, 0, NULL, 0},
	};


	morehelp = 0;
	while (1) {
		int c;

		if ((c = getopt_long(argc, argv, "qd:pghvsD:a:r:W:R:LH", long_option, NULL)) < 0)
			break;
		switch (c) {
		case 'h':
			help();
			return 0;
		case 'q':
			quiet = 1;
			break;
		case 'd':
			sscanf(optarg,"%d",&debugflag );
			printf("debugflag=%d\n",debugflag);
			break;
		case 'v':
			printf("cxdish version " CXDISH_VERSION_STR "\n");
			return 1;
		case 'p':
			do{
			YzsReadAngleProb(&angle, &prob);
			setDirectAngle(0);
			setDirectWidth(90);
			printf("The angle is [%lf], the prob is [%lf]\n", angle, prob);
			sleep(2);
			}while(1);
		case 's':
			read_stdin = 1;
			break;
		case 'g':
			golem_intf = 1;
			break;
		case 'r':
			sscanf(optarg,"%d",&pin);
			//printf("GPIO_RESET_PIN01: %d\n",g_gpio_reset_pin);
			g_gpio_reset_pin = pin;
				LOGD("GPIO_RESET_PIN02: %d\n",g_gpio_reset_pin);
			printf("GPIO_RESET_PIN02: %d\n",g_gpio_reset_pin);
			// return 1;
			break;
		case 'a':
			sscanf(optarg,"%x",&addr);
			g_ChipAddress =(uint8_t)addr;
			printf("I2C_CHIP_ADDRESS:0x%02x\n",g_ChipAddress);
				LOGD("I2C_CHIP_ADDRESS:0x%02x\n",g_ChipAddress);
			// return 1;
			break;
		case 'D':
			strncpy(g_devname,optarg,MAX_DEV_NAME-1);
			printf("g_devname=%s\n",g_devname);
				LOGD("g_devname=%s\n",g_devname);
			// return 1;
			break;
		case 'W':
			sscanf(optarg,"%d",&g_max_i2c_write );
			if ( g_max_i2c_write < 6)
			{
				printf( "ERROR: writing length is too small, length must >=6\n\n");
				LOGD( "ERROR: writing length is too small, length must >=6\n\n");
				return -1;
			}
			break;
		case 'R':
			sscanf(optarg,"%d",&g_max_i2c_read );
			if ( g_max_i2c_read < 4)
			{
				printf( "ERROR: reading length is too small, length must >=4\n\n");
				LOGD( "ERROR: reading length is too small, length must >=4\n\n");
				return -1;
			}
			break;
		case 'L':
			g_flash_legacy = 1;
			break;
		case 'H':
			g_hot_flash = 1;
			break;
		default:
			printf( "ERROR: Invalid switch or option needs an argument.\n\n");
				LOGD( "ERROR: Invalid switch or option needs an argument.\n\n");
			morehelp++;
		}
	}

	if (morehelp) {
		help();
		return 1;
	}

	if (read_stdin)
		return exec_stdin();


	if (argc - optind <= 0) {
		help();
		return 1;
	}

	//update I2C settings
	g_bChipAddress = g_ChipAddress;
	g_cbMaxI2cWrite = g_max_i2c_write;
	g_cbMaxI2cRead = g_max_i2c_read;

	if (!strcmp(argv[optind], "help")) {
		return help() ? 1 : 0;
	} else if (!strcmp(argv[optind], "sendcmd")) {
		HANDLE dev = OpenI2cDevice();
		if(dev)
		{
			//
			// setup the i2c callback functions.
			//
			SetupI2cWriteMemCallback( (void *) dev, (fn_I2cWriteMem) i2c_write_imp,g_max_i2c_write);
			SetupI2cReadMemCallback( (void *) dev, (fn_I2cReadMem) i2c_read_imp, g_max_i2c_read);

//			cxdish_sendcmd(argc - optind - 1, (argc - optind > 1? argv + optind + 1 : NULL),0 ,0);
			YzsReadAngleAndProb(argc - optind - 1, (argc - optind > 1? argv + optind + 1 : NULL), &angle, &prob);

			CloseI2cDevice(dev);
		}
		else
		{
			printf( "ERROR: Failed to open I2C device!\n");
			LOGD( "ERROR: Failed to open I2C device!\n");
		}
	} else if (!strcmp(argv[optind], "flash")) {
		printf("GPIO_RESET_PIN03: %d\n",g_gpio_reset_pin);
		LOGD("GPIO_RESET_PIN03: %d\n",g_gpio_reset_pin);
		return cxdish_flash(argc - optind - 1, argc - optind > 1 ? argv + optind + 1 : NULL,reset_address,mode) ? 1 : 0;
	} else if (!strcmp(argv[optind], "fw-version")) {
		printf("------llllllllllllllll------------\n");
		LOGD("------llllllllllllllll------------\n");
		return getfwver() ? 1 : 0;
	} else if (!strcmp(argv[optind], "set-mode")) {
		return set_mode(argc - optind - 1, argc - optind > 1 ? argv + optind + 1 : NULL) ? 1 : 0;
	} else if (!strcmp(argv[optind], "get-mode")) {
		return get_mode() ? 1 : 0;
	}else if (!strcmp(argv[optind], "encryption")) {
		return encryption() ? 1 : 0;
	} else {
		printf( "ERROR: Unknown command '%s'...\n", argv[optind]);
		LOGD( "ERROR: Unknown command '%s'...\n", argv[optind]);
	}
	return 0;
}

int cxdish_setModel(int argc,char *argv[]){
    int ret = function_play(argc,argv,"",0);
    LOGD("set_mode ret=%d",ret);
	return ret;
}


/*
void read_version()
{
	char *str[2];
	str[1] = "fw-version";
	function_play(2, str);
}
*/

#if 0
int main()
{	
	read_version();
}
#endif
