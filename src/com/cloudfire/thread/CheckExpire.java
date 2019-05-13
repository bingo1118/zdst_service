package com.cloudfire.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.TimerTask;

import com.cloudfire.dao.ExpireDao;
import com.cloudfire.dao.impl.ExpireDaoImpl;
import com.cloudfire.entity.Expire;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IpUtil;
import com.cloudfire.until.Mail;


public class CheckExpire extends TimerTask{
//	public static boolean expire = false;
	private static int state = 0;  //状态标识，0代表第一次启动；否则表示在运行中
	private static final String registerCode = "registerCodeTest";
	private static  String expireUrl = "http://139.159.220.138:8080/fireSystem/validExpireTime.do";
//	private static final String checkExpire2 = "http://139.159.220.138:51091/fireSystem/validExpireTime.do?innerIp=192.168.0.109&outerIp=59.41.94.214&registerCode=registerCodeTest";
	private static long wrongTime = 0;
	
	public static void main(String[] args) {
		String urlStr = "http://localhost:8080/fireSystem/validExpireTime.do?innerIp=123456666&outerIp=456789&registerCode="+registerCode;
		try {
			URL url = new URL(urlStr);
			InputStream openStream = url.openStream();
			InputStreamReader inputStreamReader = new InputStreamReader(openStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String s = "";
			StringBuffer sb = new  StringBuffer();
			while((s = bufferedReader.readLine()) != null){
				sb.append(s);
			}
			bufferedReader.close();
			String response = sb.toString();
			System.out.println(response);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//从服务器线程：向主服务器请求验证过期，并根据返回结果决定是否进入异常处理流程。
//	public void run() {
//		URL url;
//		try {
//			String checkExpire = expireUrl +"?innerIp="+IpUtil.getIP(2)+"&outerIp="+IpUtil.getIP(1)+"&registerCode="+registerCode;
//			checkExpire = checkExpire.replace("\r\n", "");
//			url = new URL(checkExpire);
//			InputStream openStream = url.openStream();
//			InputStreamReader inputStreamReader = new InputStreamReader(openStream);
//			BufferedReader br = new BufferedReader(inputStreamReader);
//	        String s = "";
//	        StringBuffer sb = new StringBuffer("");
//	        String webContent = "";
//	        while ((s = br.readLine()) != null) {
//	            //System.err.println("---"+s);
//	            sb.append(s);
//	        }
//	        br.close();
//	        webContent = sb.toString();
//	        if ("".equals(webContent)) { //连不上服务器，默认webContent为1
//	        	webContent = "1";
//	        }
//	        
//	        
//	        //根据返回结果webContent进入授权异常处理流程
//	        if(state == 0) { //首次启动
//	        	if("1".equals(webContent) || "4".equals(webContent) ){ //1 连不上服务器;不存在该注册码;过期 4 有多个用户且内外网Ip不一致
//	        		stopProcess();
//	        	} else { //成功启动
//	        		state = 1;
//	        	}
//	        } else { //运行中
//	        	switch(webContent){
//	        	case  "1":
//	        	case "4":
//	        		//进入异常处理流程
//	        		if(wrongTime == 0) { //没有进入异常处理流程的时间
//	        			wrongTime = System.currentTimeMillis(); //从现在开始进入异常处理流程 
//	        			Mail.sendmail();
//	        		} else if (System.currentTimeMillis() > wrongTime + 1000 * 60 * 60 * 24 * 14) { //第三周
//	        			stopProcess();
//	        		} else if (System.currentTimeMillis() > wrongTime + 1000 * 60 * 60 * 24 * 7) { //第二周
//	        			//平台、手机推送
//	        		} else if (System.currentTimeMillis() > wrongTime ){ //第一周
//	        			Mail.sendmail();
//	        		}
//	        		break;
//	        	case "2":
//	        		wrongTime = 0;
//	        		Mail.sendmail();
//	        		break;
//	        	case "3":
//	        	case "5":
//	        		wrongTime = 0; //退出异常处理流程。
//	        		break;
//	        	}
//	        } 
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//	}
	
	
	public void stopProcess() { //关闭当前进程
		String name = ManagementFactory.getRuntimeMXBean().getName();  
		System.out.println(name);  
		// get pid  
		String pid = name.split("@")[0];  
		System.out.println("Pid is:" + pid);  
		
		try {
			Runtime.getRuntime().exec("Taskkill /F /PID "+pid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	//服务端线程：更新各从服务器的过期状态
	public void run(){
		ExpireDao ed = new ExpireDaoImpl();
		List<Expire> lstExp = ed.getAllExpire();
		if (lstExp != null){
			for (Expire expire : lstExp) {
				int state = expire.getState(); //0 正常 1过期前一个月内 2过期后
				String registerCode = expire.getRegisterCode();
				long now = System.currentTimeMillis();
				long expireTime = GetTime.getTimeByString(expire.getExpireTime());
				if (now > expireTime) { //过期后
					if (state != 2) {
						ed.updateExpire(registerCode, 2);
					}
				} else if (now + 1000 * 60 * 60 * 24 * 30  >expireTime) { //1过期前一个月内
					if (state != 1) {
						ed.updateExpire(registerCode, 1);
					}
					try {
						Mail.sendmail();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					if (state != 0){
						ed.updateExpire(registerCode, 0);
					}
				}
				
			}
		}
	}
}
