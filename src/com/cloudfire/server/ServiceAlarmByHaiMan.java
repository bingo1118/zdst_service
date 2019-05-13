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
 * 针对海曼报警业务逻辑处理
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
				System.out.println("haiman报警类型：alarmType="+alarmType);
				if(StringUtils.isNotEmpty(alarmType)&&StringUtils.isNotEmpty(hump)){	//烟感数据处理时
//					(报警类型分为以下：0为不报警 1为烟雾报警 2为烟雾报警恢复 3为温度报警 4为温度报警恢复 5为烟雾低电量报警 6为烟雾低电量报警恢复 7为NB低电量报警 8为NB低电量报警恢复 9为烟雾故障报警 10 为烟雾故障报警恢复 11为温湿度故障报警 12为温湿度故障报警恢复) 
					int alarmtype = Integer.parseInt(alarmType);
					if(alarmtype == 0){
//						utilsDao.updateDeviceMac(mac);//@@设备上线
					}else{
//						utilsDao.updateDeviceMac(mac);//@@设备上线
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
//						List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //获取useridsmoke表里的用户用于短信通知
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						System.out.println("haiman进入报警推送：alarmType不等于0");
						new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
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
				}else if(StringUtils.isNotBlank(gasDev)){	//气感数据处理时
					int gasvalue = Integer.parseInt(gasDev);	//气感值
					int gasState = Integer.parseInt(serviceMap.get("gasalarm"));	//气感报警状态	0 正常  1正在自检  2正在预热  3自检成功 4设备故障
					int gasalarmType = Integer.parseInt(serviceMap.get("alarmType"));	//气感报警类型 0为不报警 1 为GAS报警恢复 2为GAS轻度泄漏 3为GAS重度泄漏 4为短路故障 5为开路故障 6为机械手故障 7为其他故障
					int language = Integer.parseInt(serviceMap.get("language"));    	//气感语言选择，默认0
					int beepsoundlevel = Integer.parseInt(serviceMap.get("beepsoundlevel"));	//音量调节
					int handonoff = Integer.parseInt(serviceMap.get("handonoff"));		//机械手开关
					tooldao.addgasbyhm(mac, gasState, language, gasalarmType, beepsoundlevel, handonoff);
					//0为不报警 1 为GAS报警恢复 2为GAS轻度泄漏 3为GAS重度泄漏 4为短路故障 5为开路故障 6为机械手故障 7为其他故障
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
						System.out.println("haiman进入报警推送：alarmType不等于0");
						new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
						new WebThread(userList,mac).start();
					}
				}
			}
		}
	}
}
