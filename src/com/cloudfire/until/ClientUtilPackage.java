package com.cloudfire.until;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClientUtilPackage {
	private final static Log log = LogFactory.getLog(ClientUtilPackage.class);
	
	//服务器	主机升级开始	0x7e	0x0e	0xdd	递增		1byte	1byte	中继器mac(4)软件版本号（4）（版本号要包括设备类型）		1byte	1byte	0x7f
	public static byte[] getHostStartLevenUp(String repeater,String levelNum){
		byte[] ackbyte = new byte[18];
		byte[] crcByte = new byte[14];
		ackbyte[0] = 0x7e;
		ackbyte[1] = 0x0e;
		ackbyte[2] = (byte)0xdd;
		ackbyte[3] = (byte)0x01;
		ackbyte[4] = (byte)0x00;
		ackbyte[5] = (byte)0x08;
		ackbyte[6] = (byte)0x00;
		byte[] repeaterMac = IntegerTo16.hexString2Bytes(repeater);
		for(int i=0;i<4;i++){
			ackbyte[7+i] = repeaterMac[i];
		}
		byte[] levelNums = IntegerTo16.hexString2Bytes(levelNum);
		
		for(int i=0;i<4;i++){		//版本号
			ackbyte[11+i] = levelNums[levelNums.length-1-i];
		}
		int levNub = Integer.parseInt(levelNum.substring(levelNum.length()-1));
		Utils.hostVersion.put(repeater, levNub);
		System.out.println("jion repeater levNub :" + levNub);
		for(int i = 0;i<14;i++){
			crcByte[i] = ackbyte[i+1];
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackbyte[15] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackbyte[16] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackbyte[17]=0x7f;
		return ackbyte;
	}
	
//	服务器 主机升级数据 0x7e 0x0e	0xd0 递增   0  0	1byte 1byte 中继器mac(4)数据包num（4）数据偏移（2前面数据总长度）数据长度（2）数据（n） 1byte 1byte	0x7f	
		public static byte[] sendLevenUpData(String repeater,byte[] byteData,int num){
			System.out.println("join sendLevenUpData:"+IntegerTo16.bytes2Hex(byteData));
			int dataLength = byteData.length;
			byte[] ackbyte = new byte[22+dataLength];
			byte[] crcByte = new byte[18+dataLength];
			ackbyte[0] = 0x7e;
			ackbyte[1] = 0x0e;
			ackbyte[2] = (byte)0xd0;
			
			byte[] dataLenByte = IntegerTo16.toByteArray(num+1,2);	//递增
			ackbyte[3] = dataLenByte[0];
			ackbyte[4] = dataLenByte[1];
			
			int lenData = 12+dataLength;
			byte[] lenDataByte =  IntegerTo16.toByteArray(lenData,2); //data数据长度
			ackbyte[5] = lenDataByte[0];
			ackbyte[6] = lenDataByte[1];
			
			byte[] repeaterMac = IntegerTo16.hexString2Bytes(repeater);		//mac
			for(int i=0;i<4;i++){
				ackbyte[7+i] = repeaterMac[i];
			}
			
			byte[] dataPackage = IntegerTo16.toByteArray(num+1,2);
			ackbyte[11] = dataPackage[1];
			ackbyte[12] = dataPackage[0];
			
			int offset = num*512;
			byte[] dataoffset = IntegerTo16.toByteArray(offset,4);	//	偏移量
			ackbyte[13] = dataoffset[3];
			ackbyte[14] = dataoffset[2];
			ackbyte[15] = dataoffset[1];
			ackbyte[16] = dataoffset[0];
			
			byte[] dataLengthByte = IntegerTo16.toByteArray(dataLength,2);//包数据长度
			ackbyte[17] = dataLengthByte[1];
			ackbyte[18] = dataLengthByte[0];
			
			for(int i = 0;i<18;i++){
				crcByte[i] = ackbyte[i+1];
			}
			System.out.println("================= ================= ===========");
			for (int i = 0; i < byteData.length; i++) {
				ackbyte[19+i] = byteData[i];
				crcByte[18+i] = ackbyte[19+i];
			}
			
			String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
			ackbyte[19+dataLength] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
			ackbyte[20+dataLength] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
			ackbyte[21+dataLength]=0x7f;
			System.out.println("ackbyteackbyte:"+IntegerTo16.bytes2Hex(ackbyte));
			return ackbyte;
		}
		
		
//		服务器 主机升级结束 0x7e 0x0e	0xde 递增 1byte 1byte	中继器mac(4)软件版本号（4）（版本号要包括设备类型）数据总包数（2）数据总大小（4） 1byte 1byte 0x7f	
		public static byte[] getHostStopLevenUp(String repeater,int inPackage,int indexMax){
			System.out.println("join getHostStopLevenUp value is repeater="+repeater+" inPackage="+inPackage+" indexMax="+indexMax);
			byte[] ackbyte = new byte[24];
			byte[] crcByte = new byte[20];
			ackbyte[0] = 0x7e;
			ackbyte[1] = 0x0e;
			ackbyte[2] = (byte)0xde;
			ackbyte[3] = (byte)0x01;
			ackbyte[4] = (byte)0x00;
			ackbyte[5] = (byte)0x0E;
			ackbyte[6] = (byte)0x00;
			byte[] repeaterMac = IntegerTo16.hexString2Bytes(repeater);
			for(int i=0;i<4;i++){
				ackbyte[7+i] = repeaterMac[i];
			}
			for(int i=0;i<4;i++){
				ackbyte[11+i] = 0x00;
			}
			
			byte[] dataPackageByte = IntegerTo16.toByteArray(inPackage,2);//数据总包数
			ackbyte[15] = dataPackageByte[1];
			ackbyte[16] = dataPackageByte[0];
			byte[] dataIndexMaxByte = IntegerTo16.toByteArray(indexMax,4);//数据总大小
			ackbyte[17] = dataIndexMaxByte[3];
			ackbyte[18] = dataIndexMaxByte[2];
			ackbyte[19] = dataIndexMaxByte[1];
			ackbyte[20] = dataIndexMaxByte[0];
			for(int i = 0;i<20;i++){
				crcByte[i] = ackbyte[i+1];
			}
			String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
			ackbyte[21] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
			ackbyte[22] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
			ackbyte[23]=0x7f;
			System.err.println("send to getHostStopLevenUp :"+IntegerTo16.bytes2Hex(ackbyte));
			return ackbyte;
		}
		
		
}
