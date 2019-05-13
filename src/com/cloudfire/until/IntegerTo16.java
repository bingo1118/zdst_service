package com.cloudfire.until;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class IntegerTo16 {
	private final static Log log = LogFactory.getLog(IntegerTo16.class);
	
	

	 public static byte algorismToHEXString(int algorism) {
        String result = "";
        result = Integer.toHexString(algorism);

        if (result.length() % 2 == 1) {
            result = "0" + result;

        }
        result = result.toUpperCase();
        
        return (byte) Integer.parseInt(result, 16);
    }
	 
	 public byte str16ToByte(String result){
		 
		 if (result.length() % 2 == 1) {
	            result = "0" + result;
	        }
        result = result.toUpperCase();
        return (byte) Integer.parseInt(result, 16);
	 }
	 
	 public static String bytes2Hex(byte[] src) {
        if (src == null || src.length <= 0) {   
            return null;   
        } 

        char[] res = new char[src.length * 2]; // 每个byte对应两个字符
        final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >> 4 & 0x0f]; // 先存byte的高4位
            res[j++] = hexDigits[src[i] & 0x0f]; // 再存byte的低4位
        }

        return new String(res);
    }
	 public static int byteToInt(byte src){
		 byte[] data=new byte[1];
		 data[0]=src;
		 int ret=0;
		 ret=Integer.parseInt(bytes2Hex(data));
		 return ret;
	 }

	 public static int byteToInt2(byte src){ //将byte的16进制表示为int eg. (byte)0xff->255
		 return src&0xff;
	 }
	 
	 public static int byteToInt3(byte src){ //获取byte的值 eg. (byte)0xff ->-1
		 return src;
	 }
	 
	public static int bytesToInt(byte[] dataLen) {
		int lenth = dataLen.length;
		int number = dataLen[0] & 0xFF;  
	    // "|="按位或赋值。  
		if(lenth==2){
			number |= ((dataLen[1] << 8) & 0xFF00);  
		}
	    return number;   
	}
	
	public static short bytesToShort(byte[] dataLen) {
		short number = (short) (dataLen[1] & 0xFF);  
	    // "|="按位或赋值。  
	    number |= ((dataLen[0] << 8) & 0xFF00);  
	    return number;   
	}
	
	public static byte[] hexString2Bytes(String src){  
		log.debug("src======="+src);
	    byte[] ret = new byte[src.length()/2];  
	    byte[] tmp = src.getBytes();  
	    for(int i=0; i< tmp.length/2; i++){  
	      ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);  
	    }  
	    return ret;  
	  } 
	
	private static byte uniteBytes(byte src0, byte src1) {  
		byte ret=0;
		try{
			 byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();  
		        _b0 = (byte)(_b0 << 4);
		        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();  
		         ret= (byte)(_b0 ^ _b1);  
		}catch (NumberFormatException e) {
			e.printStackTrace();
			log.debug("mac 地址格式错误 只能是16进制字母和数字，Mac会改成0x00...");
		}
       
        return ret;  
	}
	
	public static byte[] hexString2Bytes2(String src){  
	    byte[] ret = new byte[src.length()/2];  
	    for(int i=0; i< src.length()/2; i++){  
	      ret[i] = uniteBytes2(src.substring(i*2,i*2+1), src.substring(i*2+1,i*2+2));  
	    }  
	    return ret;  
	  } 
	
	private static byte uniteBytes2(String s1, String s2) {  
		byte ret=0;
		try{
			byte _b0 = Byte.decode("0x0" + s1).byteValue();  
	        _b0 = (byte)(_b0 << 4);
	        byte _b1 = Byte.decode("0x0" + s2).byteValue();  
	         ret= (byte)(_b0 ^ _b1);  
		} catch (NumberFormatException e) {
			e.printStackTrace();
			log.debug("字符串转换为byte出现错误，字符串为s1:"+s1+",s2:"+s2);
		}
       
        return ret;  
	}
	
	public static byte[] toByteArray(int iSource, int iArrayLen) {  
	   byte[] bLocalArr = new byte[iArrayLen];  
	   for ( int i = 0; (i < 4) && (i < iArrayLen); i++) {  
	         bLocalArr[i] = (byte)( iSource>>8*i & 0xFF );  
	   }  
	   return hexString2Bytes(bytes2Hex(bLocalArr));  
	}
	
	//Ascii码30转十进制1
	public static String asciiStringToString(String content) {
        String result = "";
        int length = content.length() / 2;
        for (int i = 0; i < length; i++) {
            String c = content.substring(i * 2, i * 2 + 2);
            int a = hexStringToAlgorism(c);
            char b = (char) a;
            String d = String.valueOf(b);
            result += d;
        }
        return result;
    }
	 
	 public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }

	//十六进制转为汉字
	public static String hexToStringGBK(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
		try {
		baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
		} catch (Exception e) {
		e.printStackTrace();
		return "";
		}
		}
		try {
		s = new String(baKeyword, "GBK");// UTF-16le:Not
		} catch (Exception e1) {
		e1.printStackTrace();
		return "";
		}
		return s;
	}
	
	public static String unicode2Hex(byte[] src) {
		
		String str = Utils.string2Unicode(src);
//		System.out.println(str);
		String unicode = Utils.unicode2String(str) ;
//		System.out.println(unicode);
		return unicode.trim();
    }
	
	public static int parseInt(String str) {
		int result = 0;
		try {
			if(str.length()<9){
				result = (int)Long.parseLong(str, 16);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return result;
    }
	
	public static void main(String[] args) {
		byte[] b1 = new byte[4];
		b1[0] = (byte)0x30;
		b1[1] = (byte)0x30;
		b1[2] = (byte)0x36;
		b1[3] = (byte)0x30;
		
		String unicode2Hex = unicode2Hex(b1);
		
		
		System.out.println(unicode2Hex);
		
		System.out.println(Long.parseLong(unicode2Hex, 16));
		
//		System.out.println(Integer.parseInt("1001", 16));
		
		
	}
}
