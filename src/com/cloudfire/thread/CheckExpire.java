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
	private static int state = 0;  //״̬��ʶ��0�����һ�������������ʾ��������
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
	
	//�ӷ������̣߳�����������������֤���ڣ������ݷ��ؽ�������Ƿ�����쳣�������̡�
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
//	        if ("".equals(webContent)) { //�����Ϸ�������Ĭ��webContentΪ1
//	        	webContent = "1";
//	        }
//	        
//	        
//	        //���ݷ��ؽ��webContent������Ȩ�쳣��������
//	        if(state == 0) { //�״�����
//	        	if("1".equals(webContent) || "4".equals(webContent) ){ //1 �����Ϸ�����;�����ڸ�ע����;���� 4 �ж���û���������Ip��һ��
//	        		stopProcess();
//	        	} else { //�ɹ�����
//	        		state = 1;
//	        	}
//	        } else { //������
//	        	switch(webContent){
//	        	case  "1":
//	        	case "4":
//	        		//�����쳣��������
//	        		if(wrongTime == 0) { //û�н����쳣�������̵�ʱ��
//	        			wrongTime = System.currentTimeMillis(); //�����ڿ�ʼ�����쳣�������� 
//	        			Mail.sendmail();
//	        		} else if (System.currentTimeMillis() > wrongTime + 1000 * 60 * 60 * 24 * 14) { //������
//	        			stopProcess();
//	        		} else if (System.currentTimeMillis() > wrongTime + 1000 * 60 * 60 * 24 * 7) { //�ڶ���
//	        			//ƽ̨���ֻ�����
//	        		} else if (System.currentTimeMillis() > wrongTime ){ //��һ��
//	        			Mail.sendmail();
//	        		}
//	        		break;
//	        	case "2":
//	        		wrongTime = 0;
//	        		Mail.sendmail();
//	        		break;
//	        	case "3":
//	        	case "5":
//	        		wrongTime = 0; //�˳��쳣�������̡�
//	        		break;
//	        	}
//	        } 
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//	}
	
	
	public void stopProcess() { //�رյ�ǰ����
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
	
	
	
	//������̣߳����¸��ӷ������Ĺ���״̬
	public void run(){
		ExpireDao ed = new ExpireDaoImpl();
		List<Expire> lstExp = ed.getAllExpire();
		if (lstExp != null){
			for (Expire expire : lstExp) {
				int state = expire.getState(); //0 ���� 1����ǰһ������ 2���ں�
				String registerCode = expire.getRegisterCode();
				long now = System.currentTimeMillis();
				long expireTime = GetTime.getTimeByString(expire.getExpireTime());
				if (now > expireTime) { //���ں�
					if (state != 2) {
						ed.updateExpire(registerCode, 2);
					}
				} else if (now + 1000 * 60 * 60 * 24 * 30  >expireTime) { //1����ǰһ������
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
