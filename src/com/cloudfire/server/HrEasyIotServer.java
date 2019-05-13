package com.cloudfire.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.ToolDao;
import com.cloudfire.dao.impl.AlarmThresholdValueDaoImpl;
import com.cloudfire.dao.impl.ElectricTypeInfoDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.dao.impl.ToolDaoImpl;
import com.cloudfire.entity.EasyIOTEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.ServiceData;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

/**
 * ��Ժ�������ҵ���߼�����
 * @author lzo
 *
 */
public class HrEasyIotServer {
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private PublicUtils utilsDao;
	private ToolDao tooldao;
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private ElectricTypeInfoDao mElectricTypeInfoDao;
	private AlarmThresholdValueDao mAlarmThresholdValueDao;
	
	public void HrEasyIotServer(EasyIOTEntity easyIot){
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
				System.out.println("haiman�������ͣ�alarmType="+alarmType);
				if(StringUtils.isNotEmpty(alarmType)){
//					(�������ͷ�Ϊ���£�0Ϊ������ 1Ϊ������ 2Ϊ�������ָ� 3Ϊ�¶ȱ��� 4Ϊ�¶ȱ����ָ� 5Ϊ����͵������� 6Ϊ����͵��������ָ� 7ΪNB�͵������� 8ΪNB�͵��������ָ� 9Ϊ������ϱ��� 10 Ϊ������ϱ����ָ� 11Ϊ��ʪ�ȹ��ϱ��� 12Ϊ��ʪ�ȹ��ϱ����ָ�) 
					int alarmtype = Integer.parseInt(alarmType);
					if(alarmtype == 0){
						utilsDao.updateDeviceMac(mac);//@@�豸����
					}else{
						utilsDao.updateDeviceMac(mac);//@@�豸����
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
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						System.out.println("haiman���뱨�����ͣ�alarmType������0");
						new MyThread(push,userList,iosMap).start();  //����������Ϣ���߳�
						new WebThread(userList,mac).start();
					}
				}
				for(String key:serviceMap.keySet()){
					String IOT_key = key;
					String IOT_value = serviceMap.get(key);
					tooldao.addIotEntity(mac, createTime, iotEventTime, serviceId, IOT_key, IOT_value);
				}
			}
		}
	}
	
	/**
	 * ��Ϣ���룺3
		��ϢID��HR_electric_switchstate
		��ʾ���ƣ�����״̬
		��Ϣ��������բ״̬	
		ע��0 ��բ��ON��		1 ��բ��OFF��			�����ݿ⣺1��-��բ     2��-��բ��switchValue
	 */
	public void HR_electric_switchstate(EasyIOTEntity easyIotEntity){
		mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		utilsDao= new PublicUtilsImpl();
		tooldao = new ToolDaoImpl();
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		String devMac = easyIotEntity.getDevSerial();
		List<ServiceData> serviceData = easyIotEntity.getServiceData();
		if(serviceData !=null){
			for (ServiceData service:serviceData) {
				Map<String,String> serviceMap = service.getServiceData();
				String Switch = serviceMap.get("HR_electric_Switch");
				if(StringUtils.isNotEmpty(Switch)){
					int switchValue = Integer.parseInt(Switch);
					SmokeLineDao sld = new SmokeLineDaoImpl();
					if((Utils.controlDev.get(devMac)!=null)&&(Utils.controlDev.get(devMac)==switchValue)){
						Utils.controlDev.put(devMac, 99);
					}
					if(switchValue==0){
						sld.setElectrState(devMac, 1);
					}else if(switchValue==1){
						sld.setElectrState(devMac, 2);
					}
				}
			}
		}
	}
	
	/**
		Leakage_current:©����ֵ
		Current������ֵ /10
		Voltage����ѹ
	 */
	public void HR_electric_data(EasyIOTEntity easyIotEntity){
		mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		utilsDao= new PublicUtilsImpl();
		tooldao = new ToolDaoImpl();
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		String devMac = easyIotEntity.getDevSerial();
		List<ServiceData> serviceData = easyIotEntity.getServiceData();
		if(serviceData !=null){
			for (ServiceData service:serviceData) {
				Map<String,String> serviceMap = service.getServiceData();
				String Leakage_current = serviceMap.get("HR_electric_Leakage_current");
				String Current = serviceMap.get("HR_electric_Current");
				String Voltage = serviceMap.get("HR_electric_Voltage");
				if(StringUtils.isNotEmpty(Leakage_current)&&StringUtils.isNotEmpty(Current)&&StringUtils.isNotEmpty(Voltage)){
					float leakage_current = Float.parseFloat(Leakage_current);
					float current = Float.parseFloat(Current)/10;
					float voltage = Float.parseFloat(Voltage);
					mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
					List<String> list6 = new ArrayList<String>();
					List<String> list7 = new ArrayList<String>();
					String list8 = leakage_current+"";
					list6.add(voltage+"");
					list7.add(current+"");
					mElectricTypeInfoDao.addElectricInfo(list6, list7, list8, null, devMac, devMac, 1);
				}
			}
		}
	}
	
	/**
		HR_electric_breakdown:breakdown - 485���ϱ���
	 */
	public void HR_electric_breakdown(EasyIOTEntity easyIotEntity){
		mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		utilsDao= new PublicUtilsImpl();
		tooldao = new ToolDaoImpl();
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		String devMac = easyIotEntity.getDevSerial();
		List<ServiceData> serviceData = easyIotEntity.getServiceData();
		if(serviceData !=null){
			for (ServiceData service:serviceData) {
				Map<String,String> serviceMap = service.getServiceData();
				String Breakdown = serviceMap.get("HR_electric_Breakdown");
				if(StringUtils.isNotEmpty(Breakdown)){
					int breakdown = Integer.parseInt(Breakdown);
					if(breakdown==1){			//485���ϱ���
						mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, 36 , 10);
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,36);
						push.setAlarmFamily("36");
						push.setAlarmType("10");
						List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						new MyThread(push,userList,iosMap).start();  //����������Ϣ���߳�
						new WebThread(userList,devMac).start();
					}
				}
			}
		}
	}
	/**
		HR_electric_alarm:����
		HR_electric_voltage_alarm:��ѹ
		HR_electric_current_alarm������
		HR_electric_creepage_alarm��©����
	 */
	public void HR_electric_alarm(EasyIOTEntity easyIotEntity){
		mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		utilsDao= new PublicUtilsImpl();
		tooldao = new ToolDaoImpl();
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		String devMac = easyIotEntity.getDevSerial();
		utilsDao.updateDeviceMac(easyIotEntity.getDevSerial());
		mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
		
		List<ServiceData> serviceData = easyIotEntity.getServiceData();
		if(serviceData !=null){
			for (ServiceData service:serviceData) {
				Map<String,String> serviceMap = service.getServiceData();
				String AlarmData = serviceMap.get("HR_electric_Alarmtype");
				String alarmValue = "";
				List<String> eleList = new ArrayList<String>();
				if(StringUtils.isNotEmpty(AlarmData)){
					int alarmData = Integer.parseInt(AlarmData);
					if(alarmData==1){			//��������
						alarmValue = serviceMap.get("HR_electric_Current");
						alarmValue = Float.parseFloat(alarmValue)/10 +"";
						mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, 45 , alarmValue);
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,45);
						push.setAlarmFamily("45");
						List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						eleList.add(alarmValue);
						alarmData = 7;
						new MyThread(push,userList,iosMap).start();  
						new WebThread(userList,devMac).start();
					}else if(alarmData==2){			//��ѹ����
						alarmValue = serviceMap.get("HR_electric_Voltage");
						mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, 43 ,alarmValue);
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,43);
						push.setAlarmFamily("43");
						List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						eleList.add(alarmValue);
						alarmData = 6;
						new MyThread(push,userList,iosMap).start();  
						new WebThread(userList,devMac).start();
					}else if(alarmData==3){			//Ƿѹ����
						alarmValue = serviceMap.get("HR_electric_Voltage");
						mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, 44 , alarmValue);
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,36);
						push.setAlarmFamily("44");
						List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						eleList.add(alarmValue);
						alarmData = 6;
						new MyThread(push,userList,iosMap).start();  
						new WebThread(userList,devMac).start();
					}else if(alarmData==4){			//©����������
						alarmValue = serviceMap.get("HR_electric_Leakage_current");
						mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, 46 , alarmValue);
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,36);
						push.setAlarmFamily("46");
						List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						eleList.add(alarmValue);
						alarmData = 8;
						new MyThread(push,userList,iosMap).start();  
						new WebThread(userList,devMac).start();
					}else if(alarmData==5){			//���ϱ���
						mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, 36 , 2);
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,36);
						push.setAlarmFamily("36");
						push.setAlarmType("0");
						List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						new MyThread(push,userList,iosMap).start();  //����������Ϣ���߳�
						new WebThread(userList,devMac).start();
					}
					
					//�˴���ӱ������ݵ�electic_info��
					mElectricTypeInfoDao.addAlarmElectric(eleList, devMac, 
							devMac, 2, alarmData);
				}
			}
		}
	}
	
	/**
	HR_electric_Undervoltage_threshold:Ƿѹ��ֵ
	HR_electric_Overvoltage_threshold:��ѹ��ֵ
	HR_electric_Overcurrent_threshold��������ֵ
	HR_electric_Leakage_current_threshold��©������ֵ
	 */
	public void HR_electric_threshold(EasyIOTEntity easyIotEntity){
		System.out.println("����ʱ��operatorTime:"+GetTime.ConvertTimeByLong());
		mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		utilsDao= new PublicUtilsImpl();
		tooldao = new ToolDaoImpl();
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		String devMac = easyIotEntity.getDevSerial();
		List<ServiceData> serviceData = easyIotEntity.getServiceData();
		if(serviceData !=null){
			for (ServiceData service:serviceData) {
				Map<String,String> serviceMap = service.getServiceData();
				String up = serviceMap.get("HR_electric_Undervoltage_threshold");
				String op = serviceMap.get("HR_electric_Overvoltage_threshold");
				String oc = serviceMap.get("HR_electric_Overcurrent_threshold");
				if(StringUtils.isNotBlank(oc)){
					oc = Float.parseFloat(oc)/10+"";
				}
				String le = serviceMap.get("HR_electric_Leakage_current_threshold");
				System.out.println("huoquxintiao��ֵ��undervoltage"+up+" overvoltage="+op+" overcurrent="+oc+" leakage="+le);
				mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
				Map<String,Integer> threshold = new HashMap<String,Integer>();
				if(StringUtils.isNotEmpty(oc)){
					mAlarmThresholdValueDao.addThresholdValue(devMac, oc, "", "", "", devMac, 45);
					threshold.put("overcurrent", 999);
				}
				if(StringUtils.isNotEmpty(op)){
					mAlarmThresholdValueDao.addThresholdValue(devMac, op, "", "", "", devMac, 43);
					threshold.put("overvoltage", 999);
				}
				if(StringUtils.isNotEmpty(up)){
					mAlarmThresholdValueDao.addThresholdValue(devMac, up, "","", "", devMac, 44);
					threshold.put("undervoltage", 999);
				}
				if(StringUtils.isNotEmpty(le)){
					mAlarmThresholdValueDao.addThresholdValue(devMac, le, "","", "", devMac, 46);
					threshold.put("leakage", 999);
				}
				if(threshold!=null&&threshold.size()>0){
					Utils.eleThreshold.put(devMac, threshold);
				}
				/*System.out.println("overcurrentovercurrent="+Utils.eleThreshold.get(devMac).get("overcurrent"));
				System.out.println("overvoltageovervoltage="+Utils.eleThreshold.get(devMac).get("overvoltage"));
				System.out.println("undervoltageundervoltage="+Utils.eleThreshold.get(devMac).get("undervoltage"));
				System.out.println("leakageleakage="+Utils.eleThreshold.get(devMac).get("leakage"));*/
			}
		}
	}
	
	/**
	�ε±�������ģʽ
	stType-smoke-�̸�-3
	stType-gas-����-4
	stType-door-�Ŵ�-1
	stType-pir-����-2
	stOpt--	ȡֵ��alarm(����)-1
			ȡֵ��lowPw(�͵�ѹ)-2
			ȡֵ��code(����)-8
			ȡֵ��test(����)-13
			ȡֵ��normal(�ָ�)-17
			ȡֵ��online(��λ)-0
	 */
	public void stAlarm(EasyIOTEntity easyIotEntity){
		mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		utilsDao= new PublicUtilsImpl();
		tooldao = new ToolDaoImpl();
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		String devMac = easyIotEntity.getDevSerial();
		List<ServiceData> serviceData = easyIotEntity.getServiceData();
		if(serviceData !=null){
			for (ServiceData service:serviceData) {
				Map<String,String> serviceMap = service.getServiceData();
				String stType = serviceMap.get("stType");
				String stOpt = serviceMap.get("stOpt");
				/*if(StringUtils.isNotEmpty(stType)){
					
				}*/
				int alarmtype = 0;
				if(StringUtils.isNotEmpty(stOpt)){
					if(stOpt.equals("alarm")){	//��������
						alarmtype = 202;
					}else if(stOpt.equals("lowPw")){	//�͵�ѹ
						alarmtype = 193;
					}else if(stOpt.equals("test")){	//����
						alarmtype = 67;
					}else if(stOpt.equals("online")){	//����
						alarmtype = 0;
					}else if(stOpt.equals("code")){	//����
						alarmtype = 68;
					}else if(stOpt.equals("normal")){	//�ָ�
						alarmtype = 69;
					}
					
					if(alarmtype == 0){
						utilsDao.updateDeviceMac(devMac);//@@�豸����
					}else{
						utilsDao.updateDeviceMac(devMac);//@@�豸����
						int hmtype = 1;
						if(alarmtype==0){
							hmtype = 0;
						}else{
							hmtype = alarmtype;
							mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, hmtype , 1);
							PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,hmtype);
							List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
							Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
							List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(devMac); //��ȡuseridsmoke������û����ڶ���֪ͨ
							System.out.println("haiman���뱨�����ͣ�alarmType������0");
							new MyThread(push,userList,iosMap).start();  //����������Ϣ���߳�
							new WebThread(userList,devMac).start();
							new TxtThread(txtUserList,devMac).start();
						}	
					}
				}
			}
		}
	}
	 
	 	/**
	 	 *�����Լ���NB�̸�
	 	 *HR_smoke_alarmtype  = 0��ʾ������2��������1���͵�ѹ
		 */
		public void HR_smoke_data(EasyIOTEntity easyIotEntity){
			mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
			utilsDao= new PublicUtilsImpl();
			tooldao = new ToolDaoImpl();
			mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			String devMac = easyIotEntity.getDevSerial();
			List<ServiceData> serviceData = easyIotEntity.getServiceData();
			if(serviceData !=null){
				for (ServiceData service:serviceData) {
					Map<String,String> serviceMap = service.getServiceData();
					String HR_smoke_alarmtype = serviceMap.get("HR_smoke_alarmtype");
					int alarmtype = 0;
					if(StringUtils.isNotEmpty(HR_smoke_alarmtype)){
						int hr_smoke_alarmtype = Integer.parseInt(HR_smoke_alarmtype);
						if(hr_smoke_alarmtype==2){
							alarmtype = 202;
						}else if(hr_smoke_alarmtype==1){
							alarmtype = 193;
						}else if(hr_smoke_alarmtype==3){
							alarmtype = 14;
						}
						
						if(alarmtype == 0){
//							utilsDao.updateDeviceMac(devMac);//@@�豸����
						}else{
//							utilsDao.updateDeviceMac(devMac);//@@�豸����
							int hmtype = 1;
							if(alarmtype==0){
								hmtype = 0;
							}else{
								hmtype = alarmtype;
								mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, hmtype , 1);
								PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,hmtype);
								List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
								List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(devMac); //��ȡuseridsmoke������û����ڶ���֪ͨ
								Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
								System.out.println("haiman���뱨�����ͣ�alarmType������0");
								new MyThread(push,userList,iosMap).start();  //����������Ϣ���߳�
								new WebThread(userList,devMac).start();
								new TxtThread(txtUserList,devMac).start();
							}	
						}
					}
				}
			}
		}
		
		/**
		 * 	�ر�ע�⣺��ѹʵ��ֵ=�ϴ���ѹֵ/100
			����ʵ��ֵ=�ϴ�����ֵ/1000
			©����ֵ=�ϴ�©����ֵ/10
			�¶�ֵ=�ϴ��¶�ֵ/100
			��ֵ���ݡ��������ݲ�������
		 */
		
		/**
	 	 *���ص����豸��������HR_BQ_data
	 	 * ��ѹA:HR_BQ_voltageA  ��ѹB:HR_BQ_voltageB  ��ѹC:HR_BQ_voltageC  6
	 	 * ����A:HR_BQ_currentA  ����B:HR_BQ_currentB  ����C:HR_BQ_currentC  7
	 	 * �¶�1:HR_BQ_temperature1  �¶�2:HR_BQ_temperature2  �¶�3:HR_BQ_temperature3  �¶�4:HR_BQ_temperature4  9
	 	 * ����N:HR_BQ_currentN  ©����:HR_BQ_creepage		8
		 */
		public void HR_BQ_data(EasyIOTEntity easyIotEntity){
			mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
			utilsDao= new PublicUtilsImpl();
			tooldao = new ToolDaoImpl();
			mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			String devMac = easyIotEntity.getDevSerial();
			List<ServiceData> serviceData = easyIotEntity.getServiceData();
			if(serviceData !=null){
				for (ServiceData service:serviceData) {
					String serviceId = service.getServiceId();
					if(serviceId.equals("HR_BQ_data")){	//����������������
						List<String> list6 = new ArrayList<String>();	//��ѹ
						List<String> list7 = new ArrayList<String>();	//����
						List<String> list9 = new ArrayList<String>();	//�¶�
						String data8 = "";				//©����
						
						Map<String,String> serviceMap = service.getServiceData();
						
						String HR_BQ_currentA = serviceMap.get("HR_BQ_currentA");	//����A
						list7.add(Float.parseFloat(HR_BQ_currentA)/1000+"");
						
						String HR_BQ_currentB = serviceMap.get("HR_BQ_currentB");	//����B
						list7.add(Float.parseFloat(HR_BQ_currentB)/1000+"");
						String HR_BQ_currentC = serviceMap.get("HR_BQ_currentC");	//����C
						list7.add(Float.parseFloat(HR_BQ_currentC)/1000+"");
						String HR_BQ_currentN = serviceMap.get("HR_BQ_currentN");	//����N
						list7.add(Float.parseFloat(HR_BQ_currentN)/1000+"");
						
						String HR_BQ_creepage = serviceMap.get("HR_BQ_creepage");	//©����
						data8 = Float.parseFloat(HR_BQ_creepage)/10+"";
						
						String HR_BQ_temperature1 = serviceMap.get("HR_BQ_temperature1");	//�¶�1
						list9.add(Float.parseFloat(HR_BQ_temperature1)/100+"");
						String HR_BQ_temperature2 = serviceMap.get("HR_BQ_temperature2");	//�¶�2
						list9.add(Float.parseFloat(HR_BQ_temperature2)/100+"");
						String HR_BQ_temperature3 = serviceMap.get("HR_BQ_temperature3");	//�¶�3
						list9.add(Float.parseFloat(HR_BQ_temperature3)/100+"");
						String HR_BQ_temperature4 = serviceMap.get("HR_BQ_temperature4");	//�¶�4
						list9.add(Float.parseFloat(HR_BQ_temperature4)/100+"");
						
						String HR_BQ_voltageA = serviceMap.get("HR_BQ_voltageA");	//��ѹA
						list6.add(Float.parseFloat(HR_BQ_voltageA)/100+"");
						String HR_BQ_voltageB = serviceMap.get("HR_BQ_voltageB");	//��ѹB
						list6.add(Float.parseFloat(HR_BQ_voltageB)/100+"");
						String HR_BQ_voltageC = serviceMap.get("HR_BQ_voltageC");	//��ѹC
						list6.add(Float.parseFloat(HR_BQ_voltageC)/100+"");
						
						if(list6!=null&&list7!=null&&StringUtils.isNotBlank(data8)&&list9!=null&&list6.size()>0&&list7.size()>0&&list9.size()>0){
							mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
							mElectricTypeInfoDao.addElectricInfo(list6, list7, data8, list9, devMac, devMac,2);
						}
					}
				}
			}
		}
		
		/**
		 * ���ص�����ֵ�洢
		 * ����Ҫ����������
		 */
		public void HR_BQ_threshold(EasyIOTEntity easyIotEntity){
			System.out.println("����ʱ��operatorTime:"+GetTime.ConvertTimeByLong());
			mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
			utilsDao= new PublicUtilsImpl();
			tooldao = new ToolDaoImpl();
			mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			String devMac = easyIotEntity.getDevSerial();
			List<ServiceData> serviceData = easyIotEntity.getServiceData();
			if(serviceData !=null){
				for (ServiceData service:serviceData) {
					String serviceId = service.getServiceId();
					if(serviceId.equals("HR_BQ_threshold")){
						Map<String,String> serviceMap = service.getServiceData();
						String HR_BQ_overcurrent_threshold = serviceMap.get("HR_BQ_overcurrent_threshold");	//���� 45
						String HR_BQ_creepage_threshold = serviceMap.get("HR_BQ_creepage_threshold");		//©����ֵ 46
						String HR_BQ_overvoltage_threshold = serviceMap.get("HR_BQ_overvoltage_threshold");		//��ѹ��ֵ 43
						String HR_BQ_undervoltage_threshold = serviceMap.get("HR_BQ_undervoltage_threshold");		//Ƿѹ��ֵ 44
						String HR_BQ_temperature1_threshold = serviceMap.get("HR_BQ_temperature1_threshold");		//�¶�1��ֵ 47
						String HR_BQ_temperature2_threshold = serviceMap.get("HR_BQ_temperature2_threshold");		//�¶�2��ֵ 47
						String HR_BQ_temperature3_threshold = serviceMap.get("HR_BQ_temperature3_threshold");		//�¶�3��ֵ 47
						String HR_BQ_temperature4_threshold = serviceMap.get("HR_BQ_temperature4_threshold");		//�¶�4��ֵ 47
						
						mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
						mAlarmThresholdValueDao.addThresholdValue(devMac, HR_BQ_creepage_threshold, "", 
								"", "", devMac, 46);
						mAlarmThresholdValueDao.addThresholdValue(devMac, HR_BQ_overcurrent_threshold, "", 
								"", "",devMac, 45);
						mAlarmThresholdValueDao.addThresholdValue(devMac, HR_BQ_overvoltage_threshold, "", 
								"", "", devMac, 43);
						mAlarmThresholdValueDao.addThresholdValue(devMac, HR_BQ_undervoltage_threshold, "", 
								"", "", devMac, 44);
						mAlarmThresholdValueDao.addThresholdValue(devMac,HR_BQ_temperature1_threshold, HR_BQ_temperature2_threshold, 
								HR_BQ_temperature3_threshold, HR_BQ_temperature4_threshold, devMac, 47);
						
					}
				}
			}
		}
		
		/**
		 * ���ر���������
		 */
		public void HR_BQ_breakdown_data(EasyIOTEntity easyIotEntity){
			mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
			utilsDao= new PublicUtilsImpl();
			tooldao = new ToolDaoImpl();
			mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			String devMac = easyIotEntity.getDevSerial();
			List<ServiceData> serviceData = easyIotEntity.getServiceData();
			if(serviceData !=null){
				for (ServiceData service:serviceData) {
					String serviceId = service.getServiceId();
					if(serviceId.equals("HR_BQ_breakdown_data")){
						Map<String,String> serviceMap = service.getServiceData();
						String Breakdown = serviceMap.get("HR_BQ_breakdown");
						if(StringUtils.isNotEmpty(Breakdown)){
							int breakdown = Integer.parseInt(Breakdown);
							mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, 36 , breakdown);
							PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,36);
							push.setAlarmFamily("36");
							push.setAlarmType(breakdown+"");
							List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
							Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
							new MyThread(push,userList,iosMap).start();  //����������Ϣ���߳�
							new WebThread(userList,devMac).start();
						}
					}
				}
			}
		}
		
		/**
		 * ���ص�����������
		 */
		public void HR_BQ_alarm_data(EasyIOTEntity easyIotEntity){
			mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
			utilsDao= new PublicUtilsImpl();
			tooldao = new ToolDaoImpl();
			mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			String devMac = easyIotEntity.getDevSerial();
			utilsDao.updateDeviceMac(easyIotEntity.getDevSerial());
			mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
			List<ServiceData> serviceData = easyIotEntity.getServiceData();
			if(serviceData !=null){
				for (ServiceData service:serviceData) {
					String serviceId = service.getServiceId();
					if(serviceId.equals("HR_BQ_alarm_data")){
						Map<String,String> serviceMap = service.getServiceData();
						String AlarmData = serviceMap.get("HR_BQ_alarmtype");
						if(StringUtils.isNotEmpty(AlarmData)){
							int alarmValue = Integer.parseInt(AlarmData);
							int alarmData = Integer.parseInt(AlarmData);
							if(alarmValue==1){	//©�籨��
								alarmData = 46;
							}else if(alarmValue==2){	//�¶ȱ���
								alarmData = 47;
							}else if(alarmValue==3){	//Ƿѹ����
								alarmData = 44;
							}else if(alarmValue==4){	//��ѹ����
								alarmData = 43;
							}else if(alarmValue==5){	//��������
								alarmData = 45;
							}
							
							mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, alarmData , alarmValue);
							PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,alarmData);
							push.setAlarmFamily("alarmData");
							List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
							Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
							new MyThread(push,userList,iosMap).start();  
							new WebThread(userList,devMac).start();
							
							List<String> list6 = new ArrayList<String>();	//��ѹ
							List<String> list7 = new ArrayList<String>();	//����
							List<String> list9 = new ArrayList<String>();	//�¶�
							String data8 = "";				//©����
							
							String HR_BQ_currentA = serviceMap.get("HR_BQ_currentA");	//����A
							list7.add(Float.parseFloat(HR_BQ_currentA)/1000+"");
							
							String HR_BQ_currentB = serviceMap.get("HR_BQ_currentB");	//����B
							list7.add(Float.parseFloat(HR_BQ_currentB)/1000+"");
							String HR_BQ_currentC = serviceMap.get("HR_BQ_currentC");	//����C
							list7.add(Float.parseFloat(HR_BQ_currentC)/1000+"");
							
							String HR_BQ_creepage = serviceMap.get("HR_BQ_creepage");	//©����
							data8 = Float.parseFloat(HR_BQ_creepage)/10+"";
							
							String HR_BQ_temperature1 = serviceMap.get("HR_BQ_temperature1");	//�¶�1
							list9.add(Float.parseFloat(HR_BQ_temperature1)/100+"");
							String HR_BQ_temperature2 = serviceMap.get("HR_BQ_temperature2");	//�¶�2
							list9.add(Float.parseFloat(HR_BQ_temperature2)/100+"");
							String HR_BQ_temperature3 = serviceMap.get("HR_BQ_temperature3");	//�¶�3
							list9.add(Float.parseFloat(HR_BQ_temperature3)/100+"");
							String HR_BQ_temperature4 = serviceMap.get("HR_BQ_temperature4");	//�¶�4
							list9.add(Float.parseFloat(HR_BQ_temperature4)/100+"");
																    
							String HR_BQ_voltageA = serviceMap.get("HR_BQ_voltageA");	//��ѹA
							list6.add(Float.parseFloat(HR_BQ_voltageA)/100+"");
							String HR_BQ_voltageB = serviceMap.get("HR_BQ_voltageB");	//��ѹB
							list6.add(Float.parseFloat(HR_BQ_voltageB)/100+"");
							String HR_BQ_voltageC = serviceMap.get("HR_BQ_voltageC");	//��ѹC
							list6.add(Float.parseFloat(HR_BQ_voltageC)/100+"");
							
							if(list6!=null&&list7!=null&&StringUtils.isNotBlank(data8)&&list9!=null&&list6.size()>0&&list7.size()>0&&list9.size()>0){
								mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
								mElectricTypeInfoDao.addElectricInfo(list6, list7, data8, list9, devMac, devMac,2);
							}
							
						}
					}
				}
			}
		}
		
		/**
		 * NB_arc_data
		 * NB���ϵ绡̽��������
		 */
		
		public void NB_arc_data(EasyIOTEntity easyIotEntity){
			mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
			utilsDao= new PublicUtilsImpl();
			tooldao = new ToolDaoImpl();
			mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			String devMac = easyIotEntity.getDevSerial();
			utilsDao.updateDeviceMac(easyIotEntity.getDevSerial());
			mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
			List<ServiceData> serviceData = easyIotEntity.getServiceData();
			if(serviceData !=null){
				for (ServiceData service:serviceData) {
					String serviceId = service.getServiceId();
					if(serviceId.equals("NB_arc_data")){
						Map<String,String> serviceMap = service.getServiceData();
						String NB_arc_dataType = serviceMap.get("NB_arc_dataType");	
						if(StringUtils.isNotEmpty(NB_arc_dataType)){		//0������ 1���绡���� 2:485���� 3���绡̽��������
							int alarmValue = Integer.parseInt(NB_arc_dataType);
							int alarmData = Integer.parseInt(NB_arc_dataType);
							if(alarmValue==1){	//�绡����
								alarmData = 53;
							}else if(alarmValue==2){	//485����
								alarmData = 36;
							}else if(alarmValue==3){	//�绡̽��������
								alarmData = 54;
							}else{
								break;
							}
							
							mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, alarmData , alarmValue);
							PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,alarmData);
							push.setAlarmFamily(alarmData+"");
							List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
							Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
							new MyThread(push,userList,iosMap).start();  
							new WebThread(userList,devMac).start();
							
						}
					}else if(serviceId.equals("NB_arc_mute_state")){	//0	������  1������
						Map<String,String> serviceMap = service.getServiceData();
						String NB_arc_mute = serviceMap.get("NB_arc_mute");	
						if(StringUtils.isNotBlank(NB_arc_mute)){
							int devState = Integer.parseInt(NB_arc_mute);
							utilsDao.updateDeviceMac(devMac, devState);
						}
					}
				}
			}
		}
		
}
