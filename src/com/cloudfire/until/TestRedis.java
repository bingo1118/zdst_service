package com.cloudfire.until;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.Scanner;

public class TestRedis {
	//创建三个模拟主机，每个主机下有三个烟感,模拟主机心跳 0001 0000,0002 0000,0003 0000 
	//格式为 7e 0e 01 12 34 09 00 1111 1111 01 00 1111 1110 [03] 17 45 7f 
	public static void main(String[] args) {
		String localIp="192.168.8.14";
		int port=4567;
		DatagramSocket client;
		try {
//			2.准备数据
//			Scanner sc = new Scanner(System.in); 
//			System.out.println("input send msg:");
//			String msg = sc.nextLine();
//			byte[] data = msg.getBytes();
			while(true){
				client = new DatagramSocket(6666);
				byte[] data = getHeartimeData();
				//3.打包（发送的地点及端口）
		        DatagramPacket packet = new DatagramPacket(data, data.length,new InetSocketAddress(localIp,4567));
		        //4.发送
		        client.send(packet);
		        //5.释放
		        client.close();
		      
		        Thread.currentThread().sleep(5000); 
//		        break;
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		addSmoke();
	}
	
	private static byte[] getHeartimeData(){
		int[] macNums = {0,1,2,3}; //离线设备数
		String[] repeaterMacs={"00010000","00020000","00030000"};
		String[][] macs={{"00010001","00010002","00010003"},{"00020001","00020002","00020003"},{"00030001","00030002","00030003"}};
		Random rand = new Random(System.currentTimeMillis());
		int macNumIndex =rand.nextInt(4);
		int repeaterMacIndex = rand.nextInt(3);
		int ifRepeaterState=rand.nextInt(2);
		String heart = "7e0e010001";
		int macNum=macNums[macNumIndex];
		int length=4+macNum*4+ifRepeaterState;
		byte[] byteArray = IntegerTo16.toByteArray(length, 2);
		heart+=IntegerTo16.bytes2Hex(byteArray);
		heart+=repeaterMacs[repeaterMacIndex];
		if(macNum!=0){
			heart+=IntegerTo16.bytes2Hex(IntegerTo16.toByteArray(macNum, 2));
		}
		for(int i=0;i<macNum;i++){
			heart+=macs[repeaterMacIndex][i];
		}
		heart+=getHex(ifRepeaterState);
		heart+=String.format("%04x", CRC16.calcCrc16(IntegerTo16.hexString2Bytes(heart.substring(2))));
		heart+="7f";
		System.out.println(heart);
		return IntegerTo16.hexString2Bytes(heart);
	}

	//只能处理0-255
	private static String getHex(int ifRepeaterState) {
		if (ifRepeaterState < 16){
			return "0"+Integer.toHexString(ifRepeaterState);
		}
		return Integer.toHexString(ifRepeaterState);
	}
	
	//添加模拟设备
	private static void addSmoke(){
		String[] repeaterMacs={"00010000","00020000","00030000"};
		String[][] macs={{"00010001","00010002","00010003"},{"00020001","00020002","00020003"},{"00030001","00030002","00030003"}};
		for(int i=2;i<repeaterMacs.length;i++){
			for(int j=2;j<macs[i].length;j++){
				String getUrl ="http://localhost:8080/fireSystem/addSmoke?userId=13622215085&privilege=4&smokeName=redis测试烟感&smokeMac="+macs[i][j]+"&repeater="+repeaterMacs[i]+"&address=鱼珠&longitude=112&latitude=23&placeTypeId=2&principal1=12&principal1Phone=36&areaId=16&deviceType=5&placeAddress=";
				String result = OneNetHttpMethod.get(getUrl);
				System.out.println(result);
			}
		}
		
	}
	
}
