#ifndef _GETCXFLASH_H
#define _GETCXFLASH_H




//���� uidone Ϊuid��һ�����֣� ���� uidtwo Ϊuid �ڶ�����
//����ֵ������ ��0�� �ɹ�
int getuid(int *uidone, int *uidtwo); 

//���� ��cryptdata�� ��ȡ¼��DSP�����ݵ�buff�� ����length Ϊbuff�ĳ���(4 * 1024)
//����ֵ������ ��0��  �ɹ�
int getencryptdata(int cryptdata[], int length); 

 
#endif