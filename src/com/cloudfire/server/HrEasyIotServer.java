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
 * 针对海曼报警业务逻辑处理
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
				System.out.println("haiman报警类型：alarmType="+alarmType);
				if(StringUtils.isNotEmpty(alarmType)){
//					(报警类型分为以下：0为不报警 1为烟雾报警 2为烟雾报警恢复 3为温度报警 4为温度报警恢复 5为烟雾低电量报警 6为烟雾低电量报警恢复 7为NB低电量报警 8为NB低电量报警恢复 9为烟雾故障报警 10 为烟雾故障报警恢复 11为温湿度故障报警 12为温湿度故障报警恢复) 
					int alarmtype = Integer.parseInt(alarmType);
					if(alarmtype == 0){
						utilsDao.updateDeviceMac(mac);//@@设备上线
					}else{
						utilsDao.updateDeviceMac(mac);//@@设备上线
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
						System.out.println("haiman进入报警推送：alarmType不等于0");
						new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
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
	 * 消息代码：3
		消息ID：HR_electric_switchstate
		显示名称：开关状态
		消息参数：合闸状态	
		注：0 合闸（ON）		1 分闸（OFF）			（数据库：1开-合闸     2关-分闸）switchValue
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
		Leakage_current:漏电流值
		Current：电流值 /10
		Voltage：电压
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
		HR_electric_breakdown:breakdown - 485故障报警
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
					if(breakdown==1){			//485故障报警
						mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, 36 , 10);
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,36);
						push.setAlarmFamily("36");
						push.setAlarmType("10");
						List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
						new WebThread(userList,devMac).start();
					}
				}
			}
		}
	}
	/**
		HR_electric_alarm:报警
		HR_electric_voltage_alarm:电压
		HR_electric_current_alarm：电流
		HR_electric_creepage_alarm：漏电流
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
					if(alarmData==1){			//过流报警
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
					}else if(alarmData==2){			//过压报警
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
					}else if(alarmData==3){			//欠压报警
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
					}else if(alarmData==4){			//漏电流流报警
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
					}else if(alarmData==5){			//故障报警
						mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, 36 , 2);
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,36);
						push.setAlarmFamily("36");
						push.setAlarmType("0");
						List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
						new WebThread(userList,devMac).start();
					}
					
					//此处添加报警数据到electic_info表。
					mElectricTypeInfoDao.addAlarmElectric(eleList, devMac, 
							devMac, 2, alarmData);
				}
			}
		}
	}
	
	/**
	HR_electric_Undervoltage_threshold:欠压阈值
	HR_electric_Overvoltage_threshold:过压阈值
	HR_electric_Overcurrent_threshold：过流阈值
	HR_electric_Leakage_current_threshold：漏电流阈值
	 */
	public void HR_electric_threshold(EasyIOTEntity easyIotEntity){
		System.out.println("操作时间operatorTime:"+GetTime.ConvertTimeByLong());
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
				System.out.println("huoquxintiao阈值：undervoltage"+up+" overvoltage="+op+" overcurrent="+oc+" leakage="+le);
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
	嘉德报警处理模式
	stType-smoke-烟感-3
	stType-gas-气感-4
	stType-door-门磁-1
	stType-pir-红外-2
	stOpt--	取值：alarm(报警)-1
			取值：lowPw(低电压)-2
			取值：code(对码)-8
			取值：test(测试)-13
			取值：normal(恢复)-17
			取值：online(在位)-0
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
					if(stOpt.equals("alarm")){	//报警推送
						alarmtype = 202;
					}else if(stOpt.equals("lowPw")){	//低电压
						alarmtype = 193;
					}else if(stOpt.equals("test")){	//测试
						alarmtype = 67;
					}else if(stOpt.equals("online")){	//在线
						alarmtype = 0;
					}else if(stOpt.equals("code")){	//对码
						alarmtype = 68;
					}else if(stOpt.equals("normal")){	//恢复
						alarmtype = 69;
					}
					
					if(alarmtype == 0){
						utilsDao.updateDeviceMac(devMac);//@@设备上线
					}else{
						utilsDao.updateDeviceMac(devMac);//@@设备上线
						int hmtype = 1;
						if(alarmtype==0){
							hmtype = 0;
						}else{
							hmtype = alarmtype;
							mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, hmtype , 1);
							PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,hmtype);
							List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
							Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
							List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(devMac); //获取useridsmoke表里的用户用于短信通知
							System.out.println("haiman进入报警推送：alarmType不等于0");
							new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
							new WebThread(userList,devMac).start();
							new TxtThread(txtUserList,devMac).start();
						}	
					}
				}
			}
		}
	}
	 
	 	/**
	 	 *我们自己的NB烟感
	 	 *HR_smoke_alarmtype  = 0表示心跳，2，报警，1，低电压
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
//							utilsDao.updateDeviceMac(devMac);//@@设备上线
						}else{
//							utilsDao.updateDeviceMac(devMac);//@@设备上线
							int hmtype = 1;
							if(alarmtype==0){
								hmtype = 0;
							}else{
								hmtype = alarmtype;
								mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, hmtype , 1);
								PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,hmtype);
								List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
								List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(devMac); //获取useridsmoke表里的用户用于短信通知
								Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
								System.out.println("haiman进入报警推送：alarmType不等于0");
								new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
								new WebThread(userList,devMac).start();
								new TxtThread(txtUserList,devMac).start();
							}	
						}
					}
				}
			}
		}
		
		/**
		 * 	特别注意：电压实际值=上传电压值/100
			电流实际值=上传电流值/1000
			漏电流值=上传漏电流值/10
			温度值=上传温度值/100
			阈值数据、报警数据不做处理
		 */
		
		/**
	 	 *北秦电气设备心跳数据HR_BQ_data
	 	 * 电压A:HR_BQ_voltageA  电压B:HR_BQ_voltageB  电压C:HR_BQ_voltageC  6
	 	 * 电流A:HR_BQ_currentA  电流B:HR_BQ_currentB  电流C:HR_BQ_currentC  7
	 	 * 温度1:HR_BQ_temperature1  温度2:HR_BQ_temperature2  温度3:HR_BQ_temperature3  温度4:HR_BQ_temperature4  9
	 	 * 电流N:HR_BQ_currentN  漏电流:HR_BQ_creepage		8
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
					if(serviceId.equals("HR_BQ_data")){	//北秦数据心跳处理
						List<String> list6 = new ArrayList<String>();	//电压
						List<String> list7 = new ArrayList<String>();	//电流
						List<String> list9 = new ArrayList<String>();	//温度
						String data8 = "";				//漏电流
						
						Map<String,String> serviceMap = service.getServiceData();
						
						String HR_BQ_currentA = serviceMap.get("HR_BQ_currentA");	//电流A
						list7.add(Float.parseFloat(HR_BQ_currentA)/1000+"");
						
						String HR_BQ_currentB = serviceMap.get("HR_BQ_currentB");	//电流B
						list7.add(Float.parseFloat(HR_BQ_currentB)/1000+"");
						String HR_BQ_currentC = serviceMap.get("HR_BQ_currentC");	//电流C
						list7.add(Float.parseFloat(HR_BQ_currentC)/1000+"");
						String HR_BQ_currentN = serviceMap.get("HR_BQ_currentN");	//电流N
						list7.add(Float.parseFloat(HR_BQ_currentN)/1000+"");
						
						String HR_BQ_creepage = serviceMap.get("HR_BQ_creepage");	//漏电流
						data8 = Float.parseFloat(HR_BQ_creepage)/10+"";
						
						String HR_BQ_temperature1 = serviceMap.get("HR_BQ_temperature1");	//温度1
						list9.add(Float.parseFloat(HR_BQ_temperature1)/100+"");
						String HR_BQ_temperature2 = serviceMap.get("HR_BQ_temperature2");	//温度2
						list9.add(Float.parseFloat(HR_BQ_temperature2)/100+"");
						String HR_BQ_temperature3 = serviceMap.get("HR_BQ_temperature3");	//温度3
						list9.add(Float.parseFloat(HR_BQ_temperature3)/100+"");
						String HR_BQ_temperature4 = serviceMap.get("HR_BQ_temperature4");	//温度4
						list9.add(Float.parseFloat(HR_BQ_temperature4)/100+"");
						
						String HR_BQ_voltageA = serviceMap.get("HR_BQ_voltageA");	//电压A
						list6.add(Float.parseFloat(HR_BQ_voltageA)/100+"");
						String HR_BQ_voltageB = serviceMap.get("HR_BQ_voltageB");	//电压B
						list6.add(Float.parseFloat(HR_BQ_voltageB)/100+"");
						String HR_BQ_voltageC = serviceMap.get("HR_BQ_voltageC");	//电压C
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
		 * 北秦电气阈值存储
		 * 不需要做除法运算
		 */
		public void HR_BQ_threshold(EasyIOTEntity easyIotEntity){
			System.out.println("操作时间operatorTime:"+GetTime.ConvertTimeByLong());
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
						String HR_BQ_overcurrent_threshold = serviceMap.get("HR_BQ_overcurrent_threshold");	//过流 45
						String HR_BQ_creepage_threshold = serviceMap.get("HR_BQ_creepage_threshold");		//漏电阈值 46
						String HR_BQ_overvoltage_threshold = serviceMap.get("HR_BQ_overvoltage_threshold");		//过压阈值 43
						String HR_BQ_undervoltage_threshold = serviceMap.get("HR_BQ_undervoltage_threshold");		//欠压阈值 44
						String HR_BQ_temperature1_threshold = serviceMap.get("HR_BQ_temperature1_threshold");		//温度1阈值 47
						String HR_BQ_temperature2_threshold = serviceMap.get("HR_BQ_temperature2_threshold");		//温度2阈值 47
						String HR_BQ_temperature3_threshold = serviceMap.get("HR_BQ_temperature3_threshold");		//温度3阈值 47
						String HR_BQ_temperature4_threshold = serviceMap.get("HR_BQ_temperature4_threshold");		//温度4阈值 47
						
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
		 * 北秦报电气故障
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
							new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
							new WebThread(userList,devMac).start();
						}
					}
				}
			}
		}
		
		/**
		 * 北秦电气报警处理
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
							if(alarmValue==1){	//漏电报警
								alarmData = 46;
							}else if(alarmValue==2){	//温度报警
								alarmData = 47;
							}else if(alarmValue==3){	//欠压报警
								alarmData = 44;
							}else if(alarmValue==4){	//过压报警
								alarmData = 43;
							}else if(alarmValue==5){	//过流报警
								alarmData = 45;
							}
							
							mFromRepeaterAlarmDao.addAlarmMsg(devMac, devMac, alarmData , alarmValue);
							PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(devMac,alarmData);
							push.setAlarmFamily("alarmData");
							List<String> userList = mGetPushUserIdDao.getAllUser(devMac);
							Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
							new MyThread(push,userList,iosMap).start();  
							new WebThread(userList,devMac).start();
							
							List<String> list6 = new ArrayList<String>();	//电压
							List<String> list7 = new ArrayList<String>();	//电流
							List<String> list9 = new ArrayList<String>();	//温度
							String data8 = "";				//漏电流
							
							String HR_BQ_currentA = serviceMap.get("HR_BQ_currentA");	//电流A
							list7.add(Float.parseFloat(HR_BQ_currentA)/1000+"");
							
							String HR_BQ_currentB = serviceMap.get("HR_BQ_currentB");	//电流B
							list7.add(Float.parseFloat(HR_BQ_currentB)/1000+"");
							String HR_BQ_currentC = serviceMap.get("HR_BQ_currentC");	//电流C
							list7.add(Float.parseFloat(HR_BQ_currentC)/1000+"");
							
							String HR_BQ_creepage = serviceMap.get("HR_BQ_creepage");	//漏电流
							data8 = Float.parseFloat(HR_BQ_creepage)/10+"";
							
							String HR_BQ_temperature1 = serviceMap.get("HR_BQ_temperature1");	//温度1
							list9.add(Float.parseFloat(HR_BQ_temperature1)/100+"");
							String HR_BQ_temperature2 = serviceMap.get("HR_BQ_temperature2");	//温度2
							list9.add(Float.parseFloat(HR_BQ_temperature2)/100+"");
							String HR_BQ_temperature3 = serviceMap.get("HR_BQ_temperature3");	//温度3
							list9.add(Float.parseFloat(HR_BQ_temperature3)/100+"");
							String HR_BQ_temperature4 = serviceMap.get("HR_BQ_temperature4");	//温度4
							list9.add(Float.parseFloat(HR_BQ_temperature4)/100+"");
																    
							String HR_BQ_voltageA = serviceMap.get("HR_BQ_voltageA");	//电压A
							list6.add(Float.parseFloat(HR_BQ_voltageA)/100+"");
							String HR_BQ_voltageB = serviceMap.get("HR_BQ_voltageB");	//电压B
							list6.add(Float.parseFloat(HR_BQ_voltageB)/100+"");
							String HR_BQ_voltageC = serviceMap.get("HR_BQ_voltageC");	//电压C
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
		 * NB故障电弧探测器报警
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
						if(StringUtils.isNotEmpty(NB_arc_dataType)){		//0：心跳 1：电弧报警 2:485故障 3：电弧探测器故障
							int alarmValue = Integer.parseInt(NB_arc_dataType);
							int alarmData = Integer.parseInt(NB_arc_dataType);
							if(alarmValue==1){	//电弧报警
								alarmData = 53;
							}else if(alarmValue==2){	//485故障
								alarmData = 36;
							}else if(alarmValue==3){	//电弧探测器故障
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
					}else if(serviceId.equals("NB_arc_mute_state")){	//0	消音开  1消音关
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
