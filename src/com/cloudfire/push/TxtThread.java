package com.cloudfire.push;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.dao.impl.UserLongerDaoImpl;
import com.cloudfire.entity.AreaidGoEasy;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.Constant;
import com.cloudfire.until.Utils;

public class TxtThread extends Thread{
	private List<String> userList;
	private int type = 0;
	private String mac="";
	static Map<String,Calendar>  txtMap = new HashMap<String,Calendar>();
	
	public TxtThread(List<String> userList) {
		this.userList = userList;
	}
	public TxtThread(List<String> userList ,String mac) {
		this.userList = userList;
		this.mac=mac;
	}
	
	public TxtThread(List<String> userList,String mac,int type) {
		this.type = type;
		this.userList = userList;
		this.mac=mac;
	}
	
	public TxtThread(List<String> userList,int type) {
		this.type = type;
		this.userList = userList;
	}
	
	
	public void run(){
		//短信通知
		UserLongerDaoImpl userimpl=new UserLongerDaoImpl();
		SmokeDao smokeDao = new SmokeDaoImpl();
		SmokeBean smoke = smokeDao.getSmokeByMac(mac);
		
		AreaDao areaDao = new AreaDaoImpl();
		String areaid = areaDao.getAreaIdByMac(mac);
		AreaidGoEasy areaidGoEasy = null;
		if(areaid != null){
			//每个区域都有属于他的短信通知appId和appkey
			areaidGoEasy = areaDao.queryAppidAppkey(areaid);
			if(areaidGoEasy == null ){
				areaidGoEasy = new AreaidGoEasy();
				areaidGoEasy.setAppid(Constant.NOTICE_APPID);
				areaidGoEasy.setAppkey(Constant.NOTICE_APPKEY);
			}
		}else{
			areaidGoEasy = new AreaidGoEasy();
			areaidGoEasy.setAppid(Constant.NOTICE_APPID);
			areaidGoEasy.setAppkey(Constant.NOTICE_APPKEY);
		}
		if(smoke!=null){
			String device_name=smoke.getName();//报警设备名称
			String address=smoke.getAddress();//报警设备安装地址
			if (userList == null) {
				return;
			}
			
			synchronized (txtMap) {
				if (!txtMap.containsKey(mac) || Calendar.getInstance().getTimeInMillis() - txtMap.get(mac).getTimeInMillis() > (1000*10) ) {  //txtMap 不存在该mac,或者txt时间已经过了3min，才会短信通知。
					for(String userid:userList) {  //遍历userList表
						System.out.println("txtThreadStart"+userid);
						if(userimpl.getUserInfoByUserId(userid).getIstxt()==1) { //为1时用户开通短信功能
							try { 
								System.out.println("txtThread"+device_name+address+userid);
								/*System.out.println(JavaSmsApi.tplSendSms(device_name,address, userid));*/
								Utils.sentMessage(userid, device_name, address,areaidGoEasy);//短信通知
								//String result = Utils.telphone(userid, device_name, address);
								//System.out.println("result:=======>>>>"+result);
								txtMap.put(mac,Calendar.getInstance());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else if(userimpl.getUserInfoByUserId(userid).getIstxt()==2){//为2时用户开通电话通知功能
							try { 
								System.out.println("txtThread"+device_name+address+userid);
								/*System.out.println(JavaSmsApi.tplSendSms(device_name,address, userid));*/
								//Utils.sentMessage(userid, device_name, address);//短信通知
								String result = Utils.telphone(userid, device_name, address,areaidGoEasy);
								System.out.println("result:=======>>>>"+result);
								txtMap.put(mac,Calendar.getInstance());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else if(userimpl.getUserInfoByUserId(userid).getIstxt()==3){//为3时用户同时开通短信和电话通知功能功能
							try { 
								System.out.println("txtThread"+device_name+address+userid);
								/*System.out.println(JavaSmsApi.tplSendSms(device_name,address, userid));*/
								Utils.sentMessage(userid, device_name, address,areaidGoEasy);//短信通知
								String result = Utils.telphone(userid, device_name, address,areaidGoEasy);
								System.out.println("result:=======>>>>"+result);
								txtMap.put(mac,Calendar.getInstance());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
				}
			}
		}	
	}
	
	public static void main(String[] args) {
	/*	Utils.telphone("15779500118", "测试烟感", "鱼珠智谷");
	String	result =Utils.sentMessage("15779500118", "测试烟感", "鱼珠智谷");
	System.out.println(result);*/
		/*try {
			System.out.println(JavaSmsApi.tplSendSms("测试烟感","黄埔区", "15779500118"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
