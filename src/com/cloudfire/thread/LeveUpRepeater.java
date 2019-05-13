package com.cloudfire.thread;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.cloudfire.until.ClientUtilPackage;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IntegerTo16;
import com.cloudfire.until.Utils;

public class LeveUpRepeater extends Thread {
	private List<byte[]> leveList;
	private String repeaterMac;
	private String levelNum;
	private File levenFile;

	public LeveUpRepeater(List<byte[]> leveList,String repeaterMac,String levelNum,File levenFile) {
		this.leveList = leveList;
		this.repeaterMac = repeaterMac;
		this.levelNum = levelNum;
		this.levenFile = levenFile;
	}
	
	@Override
	public void run() {
		if(leveList!=null){
			try {
				byte[] hostStartLevenUp = ClientUtilPackage.getHostStartLevenUp(repeaterMac,levelNum);//����������
				System.out.println("levelList is not null========lzo hostStartLevenUp"+hostStartLevenUp);
				int count = 1;
				while(count < 4){
					System.out.println();
					count++;
					Utils.hostLevenUp.put(repeaterMac, -1);
					//1��������2���ɹ���3��ʧ�ܣ�4��������
					Utils.hostLevenUpState.put(repeaterMac, 4);
					boolean result = Utils.hostLevenUp(repeaterMac, hostStartLevenUp);
					System.out.println("�������·��ɹ�״̬��"+result +"    =======================");
					if(result){//�·��ɹ�
						int maxIndex = 0;
						int hostValue = 0;
						System.out.println("Utils.hostLevenUp value is:"+Utils.hostLevenUp.get(repeaterMac)+" ============maxIndex="+maxIndex);
						while(maxIndex<40){	//�ж��Ƿ�������
							maxIndex++;
							Thread.sleep(1000);
							hostValue = Utils.hostLevenUp.get(repeaterMac);
							if(hostValue == 1){
								System.out.println("modify hostvalue success!=============");
								break;
							}
						}
						if(hostValue == 1){	//���������ͳɹ���׼���������ݰ�      �豸	����������ʼ�ظ� 0x7e 0x0e 0xd1 ���� 1byte 1byte �м���mac(4)����汾�ţ�4�����汾��Ҫ�����豸���ͣ�1byte	1byte 0x7f	
							maxIndex = 0;
							Utils.hostLevenUp.put(repeaterMac, 0);
							//1��������2���ɹ���3��ʧ�ܣ�4��������
							Utils.hostLevenUpState.put(repeaterMac, 4);
							System.out.println("join hostValue maxIndex:"+leveList.size()+" repeaterMac="+repeaterMac+" maxIndex="+maxIndex);
							for(byte[] byet:leveList){
								System.out.println("join leveList :"+leveList.size()+" repeaterMac="+repeaterMac+" maxIndex="+maxIndex+" byet:"+IntegerTo16.bytes2Hex(byet));
								byte[] byteData = ClientUtilPackage.sendLevenUpData(repeaterMac, byet, maxIndex);//������Ҫ���͵����ݰ�
								System.out.println("byteData num =maxIndex="+maxIndex+"  byteData======="+Arrays.toString(byteData));
								int errorNum = 0;
								while(errorNum < 3){
									System.out.println("join errorNum =="+errorNum);
									errorNum++;
									result = Utils.hostLevenUp(repeaterMac, byteData);
									if(result){
										for(int j = 0;j<40;j++){
											Thread.sleep(1000);
											hostValue = Utils.hostLevenUp.get(repeaterMac);
											if(hostValue == maxIndex+1){
												System.out.println("hostValue is levelUp success:"+hostValue);
												break;
											}
										}
										if(hostValue == maxIndex+1){
											System.out.println("hostValue == maxIndex+1 success:"+hostValue);
											break;	//�ɹ���ִ����һ��
										}else{
											System.out.println("errorNum == maxIndex+1 errorNum:"+errorNum);
											if(errorNum==3) errorNum++;
											continue;//ʧ�ܼ����ط�
										}
									}
								}
								if(errorNum==4){//�ϰ����ݷ���3��ʧ��
									//1��������2���ɹ���3��ʧ�ܣ�4��������
									Utils.hostLevenUpState.put(repeaterMac, 3);
									System.out.println("errorNum == maxIndex+1 errorNum:"+errorNum);
									break;
								}
								maxIndex++;
							}
							if(maxIndex == leveList.size()){	//���ݰ����ͳɹ�������ͽ�����
								System.out.println("join maxIndex == leveList.size()=="+maxIndex);
								int inPackage = maxIndex;	//�����ܰ���
								int indexMax = (int) levenFile.length();
								byte[] hostStopLevenUp = ClientUtilPackage.getHostStopLevenUp(repeaterMac, inPackage, indexMax);
								System.err.println("send to hostStopLevenUp :"+IntegerTo16.bytes2Hex(hostStopLevenUp));
								result = Utils.hostLevenUp(repeaterMac, hostStopLevenUp);	//���ͽ�����
								if(result){
									for(int i = 0;i<15;i++){
										Thread.sleep(1000);
										hostValue = Utils.hostLevenUp.get(repeaterMac);	//1����ɹ���0ʧ�ܣ��������ط�
										if(hostValue==1) {
											System.out.println("join host value is success:"+hostValue);
											count = 4;
											break;
										}
									}
									if(hostValue==1){	//�������ɹ�
										//1��������2���ɹ���3��ʧ�ܣ�4��������
										Utils.hostLevenUpState.put(repeaterMac,2);
										break;
									}else if(hostValue == 0){ 	//ʧ��
										//1��������2���ɹ���3��ʧ�ܣ�4��������
										Utils.hostLevenUpState.put(repeaterMac, 3);
										break;
									}else{
										continue;
									}
								}
							}
						}else{//����������ʧ��
							//1��������2���ɹ���3��ʧ�ܣ�4��������
							Utils.hostLevenUpState.put(repeaterMac, 3);
							continue;
						}
						break;
					}else{
						//1��������2���ɹ���3��ʧ�ܣ�4��������
						Utils.hostLevenUpState.put(repeaterMac, 3);
						continue;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
