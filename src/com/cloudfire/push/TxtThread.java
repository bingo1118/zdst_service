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
		//����֪ͨ
		UserLongerDaoImpl userimpl=new UserLongerDaoImpl();
		SmokeDao smokeDao = new SmokeDaoImpl();
		SmokeBean smoke = smokeDao.getSmokeByMac(mac);
		
		AreaDao areaDao = new AreaDaoImpl();
		String areaid = areaDao.getAreaIdByMac(mac);
		AreaidGoEasy areaidGoEasy = null;
		if(areaid != null){
			//ÿ���������������Ķ���֪ͨappId��appkey
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
			String device_name=smoke.getName();//�����豸����
			String address=smoke.getAddress();//�����豸��װ��ַ
			if (userList == null) {
				return;
			}
			
			synchronized (txtMap) {
				if (!txtMap.containsKey(mac) || Calendar.getInstance().getTimeInMillis() - txtMap.get(mac).getTimeInMillis() > (1000*10) ) {  //txtMap �����ڸ�mac,����txtʱ���Ѿ�����3min���Ż����֪ͨ��
					for(String userid:userList) {  //����userList��
						System.out.println("txtThreadStart"+userid);
						if(userimpl.getUserInfoByUserId(userid).getIstxt()==1) { //Ϊ1ʱ�û���ͨ���Ź���
							try { 
								System.out.println("txtThread"+device_name+address+userid);
								/*System.out.println(JavaSmsApi.tplSendSms(device_name,address, userid));*/
								Utils.sentMessage(userid, device_name, address,areaidGoEasy);//����֪ͨ
								//String result = Utils.telphone(userid, device_name, address);
								//System.out.println("result:=======>>>>"+result);
								txtMap.put(mac,Calendar.getInstance());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else if(userimpl.getUserInfoByUserId(userid).getIstxt()==2){//Ϊ2ʱ�û���ͨ�绰֪ͨ����
							try { 
								System.out.println("txtThread"+device_name+address+userid);
								/*System.out.println(JavaSmsApi.tplSendSms(device_name,address, userid));*/
								//Utils.sentMessage(userid, device_name, address);//����֪ͨ
								String result = Utils.telphone(userid, device_name, address,areaidGoEasy);
								System.out.println("result:=======>>>>"+result);
								txtMap.put(mac,Calendar.getInstance());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else if(userimpl.getUserInfoByUserId(userid).getIstxt()==3){//Ϊ3ʱ�û�ͬʱ��ͨ���ź͵绰֪ͨ���ܹ���
							try { 
								System.out.println("txtThread"+device_name+address+userid);
								/*System.out.println(JavaSmsApi.tplSendSms(device_name,address, userid));*/
								Utils.sentMessage(userid, device_name, address,areaidGoEasy);//����֪ͨ
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
	/*	Utils.telphone("15779500118", "�����̸�", "�����ǹ�");
	String	result =Utils.sentMessage("15779500118", "�����̸�", "�����ǹ�");
	System.out.println(result);*/
		/*try {
			System.out.println(JavaSmsApi.tplSendSms("�����̸�","������", "15779500118"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
