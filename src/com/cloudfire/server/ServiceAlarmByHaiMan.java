package com.cloudfire.server;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.ToolDao;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.ToolDaoImpl;
import com.cloudfire.entity.EasyIOTEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.ServiceData;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;

/**
 * ��Ժ�������ҵ���߼�����
 * @author lzo
 *
 */
public class ServiceAlarmByHaiMan {
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private PublicUtils utilsDao;
	private ToolDao tooldao;
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	
	public void haiManData(EasyIOTEntity easyIot){
		mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		utilsDao= new PublicUtilsImpl();
		tooldao = new ToolDaoImpl();
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		String mac = easyIot.getDevSerial();
		String createTime = easyIot.getCreateTime();
		String iotEventTime = easyIot.getIotEventTime();
		List<ServiceData> serviceData = easyIot.getServiceData();
		if(serviceData !=null){
			for (ServiceData service:serviceData) {
				String serviceId = service.getServiceId();
				Map<String,String> serviceMap = service.getServiceData();
				String alarmType = serviceMap.get("alarmType");
				String hump = serviceMap.get("hump");
				String gasDev = serviceMap.get("gas"); 
				System.out.println("haiman�������ͣ�alarmType="+alarmType);
				if(StringUtils.isNotEmpty(alarmType)&&StringUtils.isNotEmpty(hump)){	//�̸����ݴ���ʱ
//					(�������ͷ�Ϊ���£�0Ϊ������ 1Ϊ������ 2Ϊ�������ָ� 3Ϊ�¶ȱ��� 4Ϊ�¶ȱ����ָ� 5Ϊ����͵������� 6Ϊ����͵��������ָ� 7ΪNB�͵������� 8ΪNB�͵��������ָ� 9Ϊ������ϱ��� 10 Ϊ������ϱ����ָ� 11Ϊ��ʪ�ȹ��ϱ��� 12Ϊ��ʪ�ȹ��ϱ����ָ�) 
					int alarmtype = Integer.parseInt(alarmType);
					if(alarmtype == 0){
//						utilsDao.updateDeviceMac(mac);//@@�豸����
					}else{
//						utilsDao.updateDeviceMac(mac);//@@�豸����
						int hmtype = 1;
						if(alarmtype==1){
							hmtype = 202;
						}else if(alarmtype==2){
							hmtype = 102;
						}else if(alarmtype == 3){
							hmtype = 103;
						}else if(alarmtype == 4){
							hmtype = 104;
						}else if(alarmtype == 5){
							hmtype = 105;
						}else if(alarmtype == 6){
							hmtype = 106;
						}else if(alarmtype == 7){
							hmtype = 107;
						}else if(alarmtype == 8){
							hmtype = 108;
						}else if(alarmtype == 9){
							hmtype = 109;
						}else if(alarmtype == 10){
							hmtype = 110;
						}else if(alarmtype == 11){
							hmtype = 111;
						}else if(alarmtype == 12){
							hmtype = 112;
						}else if(alarmtype>12&&alarmtype<180){
							hmtype = 113;
						}
						mFromRepeaterAlarmDao.addAlarmMsg(mac, mac, hmtype , 1);
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,hmtype);
						List<String> userList = mGetPushUserIdDao.getAllUser(mac);
//						List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //��ȡuseridsmoke������û����ڶ���֪ͨ
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						System.out.println("haiman���뱨�����ͣ�alarmType������0");
						new MyThread(push,userList,iosMap).start();  //����������Ϣ���߳�
						new WebThread(userList,mac).start();
//						new TxtThread(txtUserList,mac).start();
					}
					if(serviceId.equals("alldata")){
						for(String key:serviceMap.keySet()){
							String IOT_key = key;
							String IOT_value = serviceMap.get(key);
							tooldao.addIotEntity(mac, createTime, iotEventTime, serviceId, IOT_key, IOT_value);
						}
					}
				}else if(StringUtils.isNotBlank(gasDev)){	//�������ݴ���ʱ
					int gasvalue = Integer.parseInt(gasDev);	//����ֵ
					int gasState = Integer.parseInt(serviceMap.get("gasalarm"));	//���б���״̬	0 ����  1�����Լ�  2����Ԥ��  3�Լ�ɹ� 4�豸����
					int gasalarmType = Integer.parseInt(serviceMap.get("alarmType"));	//���б������� 0Ϊ������ 1 ΪGAS�����ָ� 2ΪGAS���й© 3ΪGAS�ض�й© 4Ϊ��·���� 5Ϊ��·���� 6Ϊ��е�ֹ��� 7Ϊ��������
					int language = Integer.parseInt(serviceMap.get("language"));    	//��������ѡ��Ĭ��0
					int beepsoundlevel = Integer.parseInt(serviceMap.get("beepsoundlevel"));	//��������
					int handonoff = Integer.parseInt(serviceMap.get("handonoff"));		//��е�ֿ���
					tooldao.addgasbyhm(mac, gasState, language, gasalarmType, beepsoundlevel, handonoff);
					//0Ϊ������ 1 ΪGAS�����ָ� 2ΪGAS���й© 3ΪGAS�ض�й© 4Ϊ��·���� 5Ϊ��·���� 6Ϊ��е�ֹ��� 7Ϊ��������
					if(gasalarmType!=0){
						int GasType = gasalarmType;
						if(gasalarmType==1){
							GasType = 70;
						}else if(gasalarmType==2){
							GasType = 71;
						}else if(gasalarmType==3){
							GasType = 72;
						}else if(gasalarmType==4){
							GasType = 73;
						}else if(gasalarmType==5){
							GasType = 74;
						}else if(gasalarmType==6){
							GasType = 75;
						}
						mFromRepeaterAlarmDao.addAlarmMsg(mac, mac, GasType , gasvalue);
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,GasType);
						List<String> userList = mGetPushUserIdDao.getAllUser(mac);
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						System.out.println("haiman���뱨�����ͣ�alarmType������0");
						new MyThread(push,userList,iosMap).start();  //����������Ϣ���߳�
						new WebThread(userList,mac).start();
					}
				}
			}
		}
	}
}
