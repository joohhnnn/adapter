#ifndef _GETCXFLASH_H
#define _GETCXFLASH_H




//参数 uidone 为uid第一个部分， 参数 uidtwo 为uid 第二部分
//返回值：返回 “0” 成功
int getuid(int *uidone, int *uidtwo); 

//参数 “cryptdata” 获取录入DSP的数据的buff， 参数length 为buff的长度(4 * 1024)
//返回值：返回 “0”  成功
int getencryptdata(int cryptdata[], int length); 

 
#endif