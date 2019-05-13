package com.cloudfire.until;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IpUtils {
	/**
	 * 根据网关地址，获取网关下所有活动的IP地址，存储到map里面。
	 * @author lzo
	 */
	public static Map<String, String> getAllIpMap(String ip){
		Map<String, String> listMap = new HashMap<String,String>();
		try {
			int k = 0;
			k = ip.lastIndexOf(".");
			String ss = ip.substring(0, k+1);
			for(int i = 2;i<=255;i++){
				String iip = ss+i;
				Process p = Runtime.getRuntime().exec("ping "+iip+" -w 300 -n 1");
				InputStreamReader ir = new InputStreamReader(p.getInputStream());
				LineNumberReader input = new LineNumberReader(ir);
				for(int j = 1;j<7;j++){
					String line = input.readLine();
					if(j==3){
						if(line == null||line.equals("请求超时。")|| line.equals("timed out")){
//							System.out.println(j+line+"==false=");
						}else{
//							System.out.println(j+line+"==true==");
							listMap.put(iip, "true");
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listMap;
	}
	
	/**
	 * @param ip
	 * @return 返回一个list集合
	 */
	public static List<String> getAllIpList(String ip){
		List<String> iplist = new ArrayList<String>();
		try {
			int k = 0;
			k = ip.lastIndexOf(".");
			String ss = ip.substring(0, k+1);
			for(int i = 2;i<=255;i++){
				String iip = ss+i;
				Process p = Runtime.getRuntime().exec("ping "+iip+" -w 300 -n 1");
				InputStreamReader ir = new InputStreamReader(p.getInputStream());
				LineNumberReader input = new LineNumberReader(ir);
				for(int j = 1;j<7;j++){
					String line = input.readLine();
					if(j==3){
						if(line == null||line.equals("请求超时。")|| line.equals("timed out")){
//							System.out.println(j+line+"==false=");
						}else{
//							System.out.println(j+line+"==true==");
							iplist.add(iip);
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iplist;
	}
}
