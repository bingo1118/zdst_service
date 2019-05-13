package com.cloudfire.until.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cloudfire.until.IntegerTo16;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64Utils {
	public	String base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static byte[] base64DecodeChars = 
				new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		                   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		                   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
		                   52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
		                   -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
		                   15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
		                   -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
		                   41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

	
	/**
	 * 编码64
	 */
	public static String encode(byte[] bstr){
		return new BASE64Encoder().encode(bstr);
	}
	
	/**
	 * 64解码
	 */
	public static String decode(String str){
		byte[] bt = null;
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			bt = decoder.decodeBuffer(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(bt);
	}
	
	/**
	 *	json解析 
	 */
	public static List<Map<String,String>> jsonStringToList(String jsonString){
		JSONArray array = JSONArray.fromObject(jsonString);
		System.out.println("json字符串如下");
		System.out.println(array);
		List<Map<String,String>> rslist = new ArrayList<Map<String,String>>();
		for (int i = 0; i < array.size(); i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			Map<String,String> map = new HashMap<String,String>();
			for(Iterator<?> iter = jsonObject.keys();iter.hasNext();){
				String key = (String)iter.next();
				String value = jsonObject.get(key).toString();
				map.put(key, value);
//				System.out.println(key+"="+value);
			}
			rslist.add(map);
		}
		return rslist;
	}
	
	
	public static String base64decode(String str){
		int len = str.length();
		int i = 0;
		int c1,c2,c3,c4;
		String out = "";
		while(i<len){
			do{
				int s1 = (int)str.charAt(i++);
				c1 = base64DecodeChars[s1&0xff];
			}while(i<len && c1 == -1);
			if(c1 == -1) break;
			do{
				int s2 = (int)str.charAt(i++);
				c2 = base64DecodeChars[s2&0xff];
			}while(i<len && c2 == -1);
			if(c2==-1) break;
			out += (char)((c1<<2)|((c2&0x30)>>4));
			do{
				int s3 = (int)str.charAt(i++);
				c3 = s3&0xff;
				if(c3 == 61) return out;
					c3 = base64DecodeChars[c3];
			}while(i<len && c3 == -1);
			if(c3 == -1) break;
			out += (char)(((c2&0xF)<<4)|((c3&0x3c)>>2));
			do{
				int s4 = (int)str.charAt(i++);
				c4 = s4&0xff;
				if(c4 == 61) return out;
				c4 = base64DecodeChars[c4];
			}while(i<len&&c4== -1);
			if(c4 == -1) break;
			out += (char)(((c3&0x03)<<6)|c4);
		}
		return out;
	}
	
	//8转16
	public String utf8to16(String src){
		String out = "";
		int len,i,c,char2,char3;
		len = src.length();
		i = 0;
		while(i<len){
			c = (int)src.charAt(i++);
			switch(c>>4){
			case 0:case 1:case 2:case 3:case 4:case 5:case 6:
			case 7:
				out += src.charAt(i-1);
				break;
			case 12:
			case 13:
				char2 = (int)src.charAt(i++);
				out += (char)(((c&0x1F)<<6)|(char2&0x3f));
				break;
			case 14:
				char2 = (int)src.charAt(i++);
				char3 = (int)src.charAt(i++);
				out += (char)(((c&0x0f)<<12)|((char2&0x3f)<<6)|((char3 & 0x3f)<<0));
				break;
			}
		}
		
		return out;
	}
	
	//将char字符转换成String 16进制
	public static String CharToHex(String src){
		String out = "";
		int i = 0;
		String h;
		while(i<src.length()){
			h = Integer.toHexString((int)src.charAt(i++));
			out += "\\0x"+h;
			out += (i>0&&i%8==0)?"\r\n":", ";
		}
		return out;
	}
	
	//获取Unicode码
	public static String getUnicode(char c){
		String returnUniCode=null;
		returnUniCode=String.valueOf((int)c);

		return returnUniCode;
		}
	
	//封装直接调用该方法将字符串解密成16进制数。
	public static String base64ToHex(String src){
		String result="";
		if(src!=null&&src.length()>0){
			result = CharToHex(base64decode(src));
		}
		return result;
	}
/*	public static void main(String[] args) {
		Map<String,String> map = mqttBase("qw==");
		String loraMac = "";
		String cmd = "";
		for(String key:map.keySet()){
			System.out.println(key+map.get(key));
			loraMac = key;
			cmd = map.get(key);
		}
		System.out.println(loraMac+"="+cmd);
		String out = base64ToHex(cmd).trim();
		String src = "";
		String[] outs = out.split(",");
		for (int i = 0; i < outs.length; i++) {
			System.out.println(outs[i]);
			src = src+outs[i].substring(3);
			System.out.println(src);
			System.out.println(IntegerTo16.hexStringToAlgorism(src));
		}
		
		
		System.out.println(IntegerTo16.hexStringToAlgorism("ab"));
		
//		int a = out&0xff;
	}*/
	
	public static Map<String,String> mqttBase(String src) {
		String str1 = "[{'applicationID':'1','applicationName':'Smog_siren','nodeName':'RXHF','devEUI':'4776e6ed00430045','rxInfo':[{'mac':'b827ebfffe7b232a','rssi':-114,'loRaSNR':8,'name':'b827ebfffe7b232a','latitude':0,'longitude':0,'altitude':0}],'txInfo':{'frequency':433575000,'dataRate':{'modulation':'LORA','bandwidth':125,'spreadFactor':12},'adr':true,'codeRate':'4/5'},'fCnt':3,'fPort':8,'data':'qw=='}]";
		src = "["+src+"]";
		List<Map<String,String>> list = jsonStringToList(src);
		Map<String,String> endMap = new HashMap<String,String>();
		for(Map<String,String> map:list){
			String devEUI = map.get("devEUI");
			String data = map.get("data");
			endMap.put(devEUI, data);
		}
		return endMap;
	}
	
	/* Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串�?  
	 * @param src byte[] data  
	 * @return hex string  
	 */
	public static String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}  
	/** 
	 * Convert hex string to byte[] 
	 * @param hexString the hex string 
	 * @return byte[] 
	 */  
	public static byte[] hexStringToBytes(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	}  
	/** 
	 * Convert char to byte 
	 * @param c char 
	 * @return byte 
	 */  
	 private static byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
	} 
	 
	//将指定byte数组以16进制的形式打印到控制台  
	 public static void printHexString( byte[] b) {    
	    for (int i = 0; i < b.length; i++) {   
	      String hex = Integer.toHexString(b[i] & 0xFF);   
	      if (hex.length() == 1) {
	        hex = '0' + hex;   
	      }   
	      System.out.print(hex.toUpperCase() );   
	    }   
	   
	 }
	 
	 public static String bytes2HexString(byte[] b) {  
		  String ret = "";  
		  for (int i = 0; i < b.length; i++) {  
		   String hex = Integer.toHexString(b[ i ] & 0xFF);  
		   if (hex.length() == 1) {  
		    hex = '0' + hex;  
		   }  
		   ret += hex.toUpperCase();  
		  }  
		  return ret;  
		}  
}
