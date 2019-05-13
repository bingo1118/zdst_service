package com.cloudfire.until;

public class JavaByteArrToHex {
	public static String bytArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		for(byte b: a)
		sb.append(String.format("%02x", b&0xff));
		return sb.toString();
	}
	public static String getHexString2(byte[] b) throws Exception {
		String result = "";
		for (int i=0; i < b.length; i++) {
		result +=Integer.toString( ( b[i] & 0xff )+ 0x100, 16).substring( 1 );
		}
		return result;
		}
		public static String bytesToHexString(byte[] src) {
		StringBuilder builder = new StringBuilder();
		if (src == null || src.length <= 0) {
		return null;
		}
		String hv;
		for (int i = 0; i < src.length; i++) {
		// 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
		hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
		if (hv.length() < 2) {
		builder.append(0);
		}
		builder.append(hv);
		} 
		return builder.toString();
		}
		
		final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
		//将要读取文件头信息的文件的byte数组转换成string类型表示
		//要读取文件头信息的文件的byte数组
		//最快的方式
		public static String bytesToHex(byte[] bytes) {
			char[] hexChars = new char[bytes.length * 2];
			for ( int j = 0; j < bytes.length; j++ ) {
				int v = bytes[j] & 0xFF;
				hexChars[j * 2] = hexArray[v >>> 4];
				hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			}
			return new String(hexChars);
		}

		public static String getHexString (byte[] buf){ 
		StringBuffer sb = new StringBuffer();

		for (byte b:buf)
		{
		sb.append(String.format("%x", b));
		} 
		return sb.toString();}
}
