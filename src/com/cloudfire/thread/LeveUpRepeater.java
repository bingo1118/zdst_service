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
				byte[] hostStartLevenUp = ClientUtilPackage.getHostStartLevenUp(repeaterMac,levelNum);//主机启动包
				System.out.println("levelList is not null========lzo hostStartLevenUp"+hostStartLevenUp);
				int count = 1;
				while(count < 4){
					System.out.println();
					count++;
					Utils.hostLevenUp.put(repeaterMac, -1);
					//1、正常，2、成功，3、失败，4、升级中
					Utils.hostLevenUpState.put(repeaterMac, 4);
					boolean result = Utils.hostLevenUp(repeaterMac, hostStartLevenUp);
					System.out.println("启动包下发成功状态："+result +"    =======================");
					if(result){//下发成功
						int maxIndex = 0;
						int hostValue = 0;
						System.out.println("Utils.hostLevenUp value is:"+Utils.hostLevenUp.get(repeaterMac)+" ============maxIndex="+maxIndex);
						while(maxIndex<40){	//判断是否开启启动
							maxIndex++;
							Thread.sleep(1000);
							hostValue = Utils.hostLevenUp.get(repeaterMac);
							if(hostValue == 1){
								System.out.println("modify hostvalue success!=============");
								break;
							}
						}
						if(hostValue == 1){	//启动包发送成功，准备发送数据包      设备	主机升级开始回复 0x7e 0x0e 0xd1 递增 1byte 1byte 中继器mac(4)软件版本号（4）（版本号要包括设备类型）1byte	1byte 0x7f	
							maxIndex = 0;
							Utils.hostLevenUp.put(repeaterMac, 0);
							//1、正常，2、成功，3、失败，4、升级中
							Utils.hostLevenUpState.put(repeaterMac, 4);
							System.out.println("join hostValue maxIndex:"+leveList.size()+" repeaterMac="+repeaterMac+" maxIndex="+maxIndex);
							for(byte[] byet:leveList){
								System.out.println("join leveList :"+leveList.size()+" repeaterMac="+repeaterMac+" maxIndex="+maxIndex+" byet:"+IntegerTo16.bytes2Hex(byet));
								byte[] byteData = ClientUtilPackage.sendLevenUpData(repeaterMac, byet, maxIndex);//处理需要发送的数据包
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
											break;	//成功，执行下一条
										}else{
											System.out.println("errorNum == maxIndex+1 errorNum:"+errorNum);
											if(errorNum==3) errorNum++;
											continue;//失败继续重发
										}
									}
								}
								if(errorNum==4){//上包数据发送3次失败
									//1、正常，2、成功，3、失败，4、升级中
									Utils.hostLevenUpState.put(repeaterMac, 3);
									System.out.println("errorNum == maxIndex+1 errorNum:"+errorNum);
									break;
								}
								maxIndex++;
							}
							if(maxIndex == leveList.size()){	//数据包发送成功后，最后发送结束包
								System.out.println("join maxIndex == leveList.size()=="+maxIndex);
								int inPackage = maxIndex;	//发的总包数
								int indexMax = (int) levenFile.length();
								byte[] hostStopLevenUp = ClientUtilPackage.getHostStopLevenUp(repeaterMac, inPackage, indexMax);
								System.err.println("send to hostStopLevenUp :"+IntegerTo16.bytes2Hex(hostStopLevenUp));
								result = Utils.hostLevenUp(repeaterMac, hostStopLevenUp);	//发送结束包
								if(result){
									for(int i = 0;i<15;i++){
										Thread.sleep(1000);
										hostValue = Utils.hostLevenUp.get(repeaterMac);	//1代表成功，0失败，其他的重发
										if(hostValue==1) {
											System.out.println("join host value is success:"+hostValue);
											count = 4;
											break;
										}
									}
									if(hostValue==1){	//结束包成功
										//1、正常，2、成功，3、失败，4、升级中
										Utils.hostLevenUpState.put(repeaterMac,2);
										break;
									}else if(hostValue == 0){ 	//失败
										//1、正常，2、成功，3、失败，4、升级中
										Utils.hostLevenUpState.put(repeaterMac, 3);
										break;
									}else{
										continue;
									}
								}
							}
						}else{//启动包发送失败
							//1、正常，2、成功，3、失败，4、升级中
							Utils.hostLevenUpState.put(repeaterMac, 3);
							continue;
						}
						break;
					}else{
						//1、正常，2、成功，3、失败，4、升级中
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
