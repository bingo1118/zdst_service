package com.cloudfire.until;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetTime {
	private static  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String ConvertTimeByLong() {
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }
	
	public static String getTimeByLong(long timeLong) {
        Date date = new Date(timeLong);
        return sdf.format(date);
    }
	
	public static boolean ifOffLine(long beforeTime){
		long nowTime =System.currentTimeMillis();
		if(beforeTime==0){
			return true;
		}
		long reduceTime = nowTime-beforeTime;
		if(reduceTime>12*60*1000){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean ifSmokeOffLine(long beforeTime,long nowTime){
		if(beforeTime==0){
			return false;
		}
		long reduceTime = nowTime-beforeTime;
	
//		long tim = 5*60*60*1000;
		long tim = 0;
		if(reduceTime>tim){
			return true;
		}else{
			return false;
		}
	}
	
	public static byte[] timeToByte(){
		byte[] timeByte = new byte[7];
		String timeStr = ConvertTimeByLong();
		String[] tt = timeStr.split(" ");
		String[] ttt = tt[0].split("-");
		String[] tttt = tt[1].split(":");
		byte[] yearByte = IntegerTo16.hexString2Bytes(ttt[0]);
		timeByte[0]= yearByte[0];
		timeByte[1]= yearByte[1];
		timeByte[2]= new IntegerTo16().str16ToByte(ttt[1]);	
		timeByte[3]= new IntegerTo16().str16ToByte(ttt[2]);
		
		timeByte[4] = new IntegerTo16().str16ToByte(tttt[0]);
		timeByte[5] = new IntegerTo16().str16ToByte(tttt[1]);
		timeByte[6] = new IntegerTo16().str16ToByte(tttt[2]);
		return timeByte;
	}
	
	public static Long getTimeByString(String time){
		long time2 = 0;
		try {
			time2 = sdf.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time2;
	}
}
