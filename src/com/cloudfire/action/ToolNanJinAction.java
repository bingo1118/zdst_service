package com.cloudfire.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.MeterInfoDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.ToolNanJinDao;
import com.cloudfire.dao.impl.AlarmThresholdValueDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.MeterInfoDaoImp;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.dao.impl.ToolNanJinDaoImpl;
import com.cloudfire.entity.DX_NB_NJ_DataMqttEntity;
import com.cloudfire.entity.EasyIOTCmdEntity;
import com.cloudfire.entity.ElectricEnergyEntity;
import com.cloudfire.entity.ElectricThresholdBean;
import com.cloudfire.entity.EnergrEntity;
import com.cloudfire.entity.MqttApiEntity;
import com.cloudfire.entity.MqttElectricEntity;
import com.cloudfire.entity.MqttGasEntity;
import com.cloudfire.entity.MqttSmokeEntity;
import com.cloudfire.entity.MqttTempHumiEntity;
import com.cloudfire.entity.MqttWaterEntity;
import com.cloudfire.entity.NanJing_NB_IOT_Entity;
import com.cloudfire.entity.ProofGasEntity;
import com.cloudfire.entity.ServiceData;
import com.cloudfire.entity.THDevice;
import com.cloudfire.entity.ThreePhaseElectricEntity;
import com.cloudfire.entity.WaterAckEntity;
import com.cloudfire.server.HrNanJingIotServer;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.OneNetHttpMethod;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


public class ToolNanJinAction extends ActionSupport implements 
ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -4581514309685063419L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private static HrNanJingIotServer hrServer;
	private static ToolNanJinDao nanJingDao;
	private static MeterInfoDao meterInfoDao; 
	private final static Log log = LogFactory.getLog(ToolNanJinAction.class);
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private String getRequestPostData(HttpServletRequest request) {
		String result = "";
		int contentLength = request.getContentLength()+1;
		System.out.println("数据调用getRequestPostData方法"+contentLength);
		if(contentLength<0){
			return null;
		}
		byte buffer[] = new byte[contentLength];
		try {
			for (int i = 0; i <contentLength; i++) {
				int len = request.getInputStream().read(buffer,0,contentLength-1);
				if(len == -1){
					break;
				}
				i += len;
			}
			result = new String(buffer,"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String execute() throws Exception {
		ActionContext context = ActionContext.getContext();
		Map<String,Object> map = context.getParameters();
		Set<String> keys = map.keySet();
		for (String key:keys) {
			Object[] obj = (Object[])map.get(key);
			System.out.println(Arrays.toString(obj));
		}
		return NONE;
	}
	
	public void nanjinUploadData(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("nanjinUploadDatas");
		String result = getRequestPostData(this.request);
		System.out.println("nanjinUploadDatas数据回调结果："+result);
	}
	
	/**
	 * 南京平台数据上传回调
	 */
	public void nanjinUploadDatas(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("南京:数据上传回调====启用");
		String result = getRequestPostData(this.request);
		System.out.println("数据回调结果："+result);
		
		String hearTime = GetTime.ConvertTimeByLong();
		
		NanJing_NB_IOT_Entity IotEntity = new NanJing_NB_IOT_Entity();
		hrServer = new HrNanJingIotServer();
		IotEntity = getNanJing_IOT_Entity(result);
		IotEntity.setHearTime(hearTime);
		
		PublicUtils utilsDao = new PublicUtilsImpl();
		utilsDao.updateDeviceMac(IotEntity.getImeiVal());
		
		//若设备处于智敏南京嘉德烟感区域，则转发给智敏平台处理
		AreaDao ad = new AreaDaoImpl();
		
		int alarmType = IotEntity.getAlarmType();
			switch(alarmType){
				case 1990://中电电气功能电率
				case 1991://中电电气数据
					hrServer.Hr_NanJing_EnergyEntity_zhongdian(IotEntity);
					break;
				case 1992:	//U特电气功能电率数据
					hrServer.Hr_NanJing_EnergyEntity(IotEntity,0);//处理心跳数据
					break;
				case 1993:	//NB普通温湿度心跳
					hrServer.Hr_NanJing_TempHumi(IotEntity,0);//处理心跳数据
					break;
				case 1994:	//NB普通温湿度报警
					hrServer.Hr_NanJing_TempHumi(IotEntity,1);//处理报警数据
					break;
				case 1995:	//NB普通水压心跳
					hrServer.Hr_NanJing_WaterData(IotEntity,0);//处理心跳数据
					break;
				case 1996:	//NB普通水压报警
					hrServer.Hr_NanJing_WaterData(IotEntity,1);//处理报警数据
					break;
				case 1997://三相电气阈值
					hrServer.Hr_NanJing_Three_ElectricData(IotEntity,1);//处理报警数据
					break; 
				case 1998://三相电气故障
				case 1999://三相电气心跳
					hrServer.Hr_NanJing_Three_ElectricData(IotEntity,0);//处理报警数据
					break;
				case 2000://电气心跳
					hrServer.Hr_NanJing_ElectricData(IotEntity);//处理报警数据
					break;
				case 43:
				case 44:
				case 45:
				case 46:
				case 76://传感器故障
				case 77://串口通讯故障
				case 136://电气报警
					hrServer.Hr_NanJing_ElectricAlarm(IotEntity);//处理报警数据
					break;
				case 2:
				case 193:
				case 194: //低电恢复
				case 202:
				case 67:
				case 68:
				case 69:
				case 14: 
				case 20:
				case 6:
					hrServer.Hr_NanJing_IotServer(IotEntity);//处理报警数据
					break;
				case 1000:	//设备信号强度
					hrServer.Hr_NanJing_DevSignal(IotEntity);//处理信号数据
					break;
				case 1001:	//设备电池量度
					hrServer.Hr_NanJing_DevBattery(IotEntity);//处理电池量数据
					break;
				case 2001:	//电气开
				case 2002:	//电气关
					hrServer.Hr_Electric_Switch(IotEntity);//处理电池量数据
					break;
			}
	}
	
	
	/**
	 * 命令回调实体类封装
	 * @param args 回调返回的数据
	 * @return
	 */
	public static EasyIOTCmdEntity getEasyIOTCmdEntity(String args) {
		String str = args;
		EasyIOTCmdEntity iotEntity = new EasyIOTCmdEntity();
		Map<String,String> resultParams = new HashMap<String,String>();
		try {
			JSONObject json = new JSONObject(str);
			String commandId = json.getString("commandId");
			String devSerial = json.getString("devSerial");
			JSONObject json2 = new JSONObject(json.getString("resultParams"));
			Iterator<?> it = json2.keys();
			while(it.hasNext()){
				String key = it.next().toString();
				String value = json2.getString(key);
				resultParams.put(key, value);
				if(StringUtils.isNotBlank(value)){
					int val = Integer.parseInt(value);
					if(Utils.controlDev.get(devSerial)!=null){
						System.out.println("jinru:controlDev="+Utils.controlDev.get(devSerial));
						if((val==0)&&(Utils.controlDev.get(devSerial)==199)){
							Utils.controlDev.put(devSerial, 99);
						}else if(val!=0){
							Utils.controlDev.put(devSerial, 200);
						}
					}
					if(Utils.eleThreshold.get(devSerial)!=null){
						System.out.println("jinru:eleThreshold="+Utils.controlDev.get(devSerial));
						Map<String,Integer> threshold = new HashMap<String,Integer>();
						if(val==0){
							threshold.put("overcurrent", 999);
							threshold.put("overvoltage", 999);
							threshold.put("undervoltage", 999);
							threshold.put("leakage", 999);
						}else if(val!=0){
							threshold.put("overcurrent", 998);
							threshold.put("overvoltage", 998);
							threshold.put("undervoltage", 998);
							threshold.put("leakage", 998);
						}
						Utils.eleThreshold.put(devSerial, threshold);
					}
				}
			}
			iotEntity.setDevSerial(devSerial);
			iotEntity.setCommandId(commandId);
			iotEntity.setResultParams(resultParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iotEntity;
	}
	
	/**
	 * 数据上传封装实体类参数
	 * @param iotStr 回调返回的数据
	 * @return
	 */
	public static NanJing_NB_IOT_Entity getNanJing_IOT_Entity(String iotStr) {
		String str = iotStr;
		NanJing_NB_IOT_Entity iotEntity = new NanJing_NB_IOT_Entity();
		Object obj = null;
		MqttApiEntity mae = null;
		ThreePhaseElectricEntity threeElec = null;
		MqttSmokeEntity mse = null;
		MqttElectricEntity mee = null;
		MqttGasEntity mge = null;
		ElectricEnergyEntity eee=null;
		MqttTempHumiEntity mthe = null;
		MqttWaterEntity mwe = null;
		String dataTime = GetTime.ConvertTimeByLong();
		DX_NB_NJ_DataMqttEntity mqtt = new DX_NB_NJ_DataMqttEntity();
		List<ServiceData> serviceList = new ArrayList<ServiceData>();
		try {
			JSONObject json = new JSONObject(str);
			String deviceId = json.getString("deviceId");
			String gatewayId = json.getString("gatewayId");
			String service = "";
			JSONObject jarray = null;
			nanJingDao = new ToolNanJinDaoImpl();
			String imeiVal = "";
			if(StringUtils.isNotBlank(deviceId)){
				imeiVal = nanJingDao.getImeiBydeviceId(deviceId);
				System.out.println("imeiVal:"+imeiVal);
			}
			
			if(StringUtils.isBlank(imeiVal)){
				return null;
			}else{
				iotEntity.setImeiVal(imeiVal);
			}
			
			if(json.has("service")){
				jarray = new JSONObject(json.getString("service"));	//我们自己
			}else if(json.has("services")){
				JSONArray jary =json.getJSONArray("services");		//日海燃气
				jarray = jary.getJSONObject(0);
			}
			/**===========start===========**/
			String serviceId = jarray.getString("serviceId");
			String serviceType = jarray.getString("serviceType");
			JSONObject json2 = new JSONObject(jarray.getString("data"));
			String status = "";
			
			String rssi = "";	//rssi信号强度值
			int devType = 0;	//设备类型 1：燃气 2：电气
			String imsi = "0";	//imsi值
			String batteryPower = "";	//电池电量
			int cmd = 0;	//命令提示符 0x00(心跳)0x01(报警)0x03(设备通讯故障)0x10(开关闭合)0x11(开关断开)0x20(阈值)
			
			if(json2.has("RSSI")){
				rssi = json2.getString("RSSI");	//rssi信号强度值
				iotEntity.setRsiiVal(rssi);
			}
			if(json2.has("IMSI")){
				imsi = json2.getString("IMSI");	//imsi值
				iotEntity.setImsiVal(imsi);
			}
			if(json2.has("BatteryPower")){
				batteryPower = json2.getString("BatteryPower");	//电池电量
				iotEntity.setBatteryPower(batteryPower);
			}
			if(json2.has("DevType")){
				devType = json2.getInt("DevType");	//设备类型 1：燃气 2：电气、3三相电气、4：U特电气
			}
			if(json2.has("cmd")){
				cmd = json2.getInt("cmd");	//命令提示符 0x00(心跳) 0x01(过压报警) 0x02(欠压报警) 0x03(485通讯故障) 0x04(过流报警) 0x05(漏电报警) 0x10(开关闭合) 0x11(开关断开)
			}
			if(json2.has("rssi")){
				rssi = json2.getString("rssi");	//rssi信号强度值
				iotEntity.setRsiiVal(rssi);
			}
			if(json2.has("imsi")){
				imsi = json2.getString("imsi");	//imsi值
				iotEntity.setImsiVal(imsi);
			}
			if(StringUtils.isNotBlank(deviceId)&&StringUtils.isNotBlank(imeiVal)){
				nanJingDao.addImeiDeviceId(imeiVal, deviceId, imsi);
				nanJingDao.updateSmokeInfo(iotEntity);
			}
			switch(serviceId){
//			数据类别 serviceId
				case "FrequentlyUsedData"://常用数据 FrequentlyUsedData
					/*DI 状态 DIStatus -
					DO 状态 DOStatus -
					DI 联动状态 DILinkStatus -
					剩余电流报警状态 IRalarmStatus
					PMC350F 接入云平台北向规约
					 版本 V1.1
					2
					温度报警状态 TCalarmStatus
					电流报警/预警状态 IalarmStatus
					剩余电流自检状态 IRCheckStatus
					温度自检状态 TCCheckStatus
					总故障报警状态 AllalarmStatus -
					越限状态 setpointStatus
					自检状态 selfCheckStatus
					无线信号强度 wirelessSignalIntensity
					总正向有功电能 kWhImp kWh
					A 相正向有功电能 kWhImp1 kWh
					B 相正向有功电能 kWhImp2 kWh
					C 相正向有功电能 kWhImp3 kWh
					A 相电流 I1 mA
					B 相电流 I2 mA
					C 相电流 I3 mA
					A 相电压 U1 mV
					B 相电压 U2 mV
					C 相电压 U3 mV
					频率 freq Hz
					总功率因数 PFavg
					A 相有功功率 P1 W
					B 相有功功率 P2 W
					C 相有功功率 P3 W
					A 相无功功率 Q1 var
					B 相无功功率 Q2 var
					C 相无功功率 Q3 var
					A 相视在功率 S1 VA
					B 相视在功率 S2 VA
					C 相视在功率 S3 VA
					三相总有功功率 P W
					三相总无功功率 Q var
					三相总视在功率 S VA
					A/AB 电压相位 U1Ang °
					B/BC 电压相位 U2Ang °
					C/CA 电压相位 U3Ang °
					A 相电流相位 IaAng °
					B 相电流相位 IbAng °
					C 相电流相位 IcAng °
					电压不平衡度 UUnb %
					电流不平衡度 IUnb %
					A 相电压总畸变率 U1THD %
					B 相电压总畸变率 U2THD %
					C 相电压总畸变率 U3THD %
					PMC350F 接入云平台北向规约
					 版本 V1.1
					3
					A 相电流总畸变率 I1THD %
					B 相电流总畸变率 I2THD %
					C 相电流总畸变率 I3THD %
					温度 1 TC1 ℃
					温度 2 TC2 ℃
					温度 3 TC3 ℃
					温度 4 TC4 ℃
					剩余电流 IR IR mA*/
					
					break;
				case "RealTimeData"://实时测量数据 RealTimeData
					/*
					A/AB 电压相角 U1Ang °
					PMC350F 接入云平台北向规约
					 版本 V1.1
					4
					B/BC 电压相角 U2Ang °
					C/CA 电压相角 U3Ang °
					A 相电流相角 IaAng °
					B 相电流相角 IbAng °
					C 相电流相角 IcAng °
					计算中性线电流 Inc In A
					
					A 相基波功率因数 dPF1
					B 相基波功率因数 dPF2
					C 相基波功率因数 dPF3
					总基波功率因数 dPF
					
					
					DI 状态 DIStatus--DI 状态，bit0~ bit1 分别表示 DI1~2 的状态，0：打开，1：闭合
					DI 联动状态 DILinkStatus --- DI 联动状态，bit0~ bit1 分别表示 D11~D12 的联动状态，0：没有联动，1：联动动作。
					DO 状态 DOStatus ---   DO 状态，bit0~ bit1 分别表示 DO1~DO2 的状态，0：返回，1：动作。
					
					越限报警状态 setpointStatus
					接线自检状态 selfCheckStatus
					SOE 指针总数 SOEpointer
					装置运行时间 OperatingTime hour
					剩余电流自检状态 IRCheckStatus
					温度自检状态 TCCheckStatus
					*/
					
					eee = new ElectricEnergyEntity();  //电率
					threeElec = new ThreePhaseElectricEntity();
					threeElec.setImeiStr(imeiVal);
					eee.setImei(imeiVal);
					iotEntity.setAlarmType(1990);
					String U1 = json2.getString("U1");//A 相电压 U1 V
					threeElec.setVoltageA(U1);
					String U2 = json2.getString("U2");//B 相电压 U2 V
					threeElec.setVoltageB(U2);
					String U3 = json2.getString("U3");//C 相电压 U3 V
					threeElec.setVoltageC(U3);
					
					/*平均相电压 Uavg V
					AB 线电压 U12 V
					BC 线电压 U23 V
					CA 线电压 U31 V
					平均线电压 Ullavg V*/
					
					String I1 = json2.getString("I1");//A 相电流 I1 A
					threeElec.setElectricityA(I1);
					String I2 = json2.getString("I2");//B 相电流 I2 A
					threeElec.setElectricityB(I2);
					String I3 = json2.getString("I3");//C 相电流 I3 A
					threeElec.setElectricityC(I3);
					
//					平均相电流 Iavb A
					
					String TC1 = json2.getString("TC1");//温度 1 TC1 ℃
					threeElec.setTempValueA(TC1);
					String TC2 = json2.getString("TC2");//温度 2 TC2 ℃
					threeElec.setTempValueB(TC2);
					String TC3 = json2.getString("TC3");//温度 3 TC3 ℃
					threeElec.setTempValueC(TC3);
					String TC4 = json2.getString("TC4");//温度 4 TC4 ℃
					threeElec.setTempValueD(TC4);
					String IR = json2.getString("IR");//剩余电流 IR IR mA
					threeElec.setSurplusElectri(IR);
					
					int ifAlarmType = 0; 
					String alarmFimally = "0";
					
					//报警
					String IRalarmStatus = json2.getString("IRalarmStatus");//剩余电流报警状态 IRalarmStatus
					
//					剩余电流报警状态，bit0表示对应状态，0：正常返回， 1：报警动作。
					
					
					int AllalarmStatus = json2.getInt("AllalarmStatus");//总故障报警状态 AllalarmStatus
					
					/*[注9] 总故障报警状态，对应的状态为1表示动作，0表示返回；当任意剩余电流通道发生探头故障时bit0=1，其他类同；当有剩余电流或温度发生故障时bit14=1；当有剩余电流或温度或电流或DI联动发生报警或预警时bit15=1。
							bit7		bit6		bit5		bit4		bit3			bit2			bit1			bit0
							电流预警	电流报警	温度预警	温度报警	剩余电流预警	剩余电流报警	温度探头故障	剩余电流探头故障

							bit15	bit14	bit13	bit12	bit11	bit10	bit9	bit8
							总报警	总故障	DI联动	保留	缺相	不平衡	欠压	过压*/
					
					/*if((AllalarmStatus&0x8000)==32768){	//总报警
						
					}if((AllalarmStatus&0x4000)==16384){//总故障
						
					}if((AllalarmStatus&0x2000)==8192){//DI联动
						
					}*/if((AllalarmStatus&0x800)==2048){//缺相
						ifAlarmType = 153;
					}if((AllalarmStatus&0x200)==512){//欠压
						ifAlarmType = 44;
						alarmFimally = U1;
					}if((AllalarmStatus&0x100)==256){//过压
						ifAlarmType = 43;
						alarmFimally = U1;
					}if((AllalarmStatus&0x40)==64){//电流报警
						ifAlarmType = 45;
						alarmFimally = I1;
					}if((AllalarmStatus&0x04)==4){//剩余电流报警--漏电流
						ifAlarmType = 46;
						alarmFimally = I1;
					}if((AllalarmStatus&0x02)==2){//温度探头故障--故障
						ifAlarmType = 36;
						alarmFimally = TC1;
					}if((AllalarmStatus&0x01)==1){//剩余电流探头故障--故障
						ifAlarmType = 36;
					}
					
					String TCalarmStatus = json2.getString("TCalarmStatus");//温度报警状态 TCalarmStatus
					
					/*[注5] 温度报警状态，bit0~ bit3表示TC1~TC4对应状态，0：正常返回， 1：报警动作。
					bit15~ bit4	bit3	…	bit0
					0	TC4报警动作	…	TC1报警动作*/
					
					String IalarmStatus = json2.getString("IalarmStatus");//电流报警/预警状态 IalarmStatus
					
					/*[注6] 电流报警/预警状态，bit 0~ bit 1分别表示电流报警~预警的状态，0：正常返回， 1：报警动作。
					bit15~ bit2	bit1	bit0
					0	电流预警	电流报警*/
					
					/*三相有功功率 P W
					三相无功功率 Q var
					三相视在功率 S VA
					总功率因数 PFavg
					频率 freq Hz*/
					if(IRalarmStatus.equals("1")||IRalarmStatus == "1"){//剩余电流报警状态
						ifAlarmType = 46;
					}else if(TCalarmStatus.equals("1")||TCalarmStatus == "1"){//温度报警状态
						ifAlarmType = 47;
					}else if(IalarmStatus.equals("1")||IalarmStatus == "1"){//过流报警状态
						ifAlarmType = 45;
					}
					
					String Pa = json2.getString("Pa");//A 相有功功率 Pa W
					eee.setActivePowerA(Pa);
					String Pb = json2.getString("Pb");//B 相有功功率 Pb W	
					eee.setActivePowerB(Pb);
					String Pc = json2.getString("Pc");//C 相有功功率 Pc W
					eee.setActivePowerC(Pc);
					String Q1 = json2.getString("Q1");//A 相无功功率 Q1 var
					eee.setReactivePowerA(Q1);
					String Q2 = json2.getString("Q2");//B 相无功功率 Q2 var	 
					eee.setReactivePowerB(Q2);
					String Q3 = json2.getString("Q3");//C 相无功功率 Q3 var	
					eee.setReactivePowerC(Q3);
					String S1 = json2.getString("S1");//A 相视在功率 S1 VA	
					eee.setApparentPowerA(S1);
					String S2 = json2.getString("S2");//B 相视在功率 S2 VA	
					eee.setApparentPowerB(S2);
					String S3 = json2.getString("S3");//C 相视在功率 S3 VA	
					eee.setApparentPowerC(S3);
					String PF1 = json2.getString("PF1");//A 相功率因数 PF1
					eee.setPowerFactorA(PF1);
					String PF2 = json2.getString("PF2");//B 相功率因数 PF2	
					eee.setPowerFactorB(PF2);
					String PF3 = json2.getString("PF3");//C 相功率因数 PF3	
					eee.setPowerFactorC(PF3);
					if(Utils.objEnergr !=null&&Utils.objEnergr.get(imeiVal)!=null){
						String activeEnergyA = Utils.objEnergr.get(imeiVal).getActiveEnergyA();//(2)有功电能A相	
						eee.setActiveEnergyA(activeEnergyA);
						String activeEnergyB = Utils.objEnergr.get(imeiVal).getActiveEnergyB();//(2)有功电能B相	
						eee.setActiveEnergyB(activeEnergyB);
						String activeEnergyC = Utils.objEnergr.get(imeiVal).getActiveEnergyC();//(2)有功电能C相	
						eee.setActiveEnergyC(activeEnergyC);
						String reactiveEnergyA = Utils.objEnergr.get(imeiVal).getReactiveEnergyA();//(2)无功电能A相	
						eee.setReactiveEnergyA(reactiveEnergyA);
						String reactiveEnergyB = Utils.objEnergr.get(imeiVal).getReactiveEnergyB();//(2)无功电能B相	
						eee.setReactiveEnergyB(reactiveEnergyB);
						String reactiveEnergyC = Utils.objEnergr.get(imeiVal).getReactiveEnergyC();//(2)无功电能C相	
						eee.setReactiveEnergyC(reactiveEnergyC);
						String apparentEnergyA = Utils.objEnergr.get(imeiVal).getApparentEnergyA();//(2)视在电能A相	
						eee.setApparentEnergyA(apparentEnergyA);
						String apparentEnergyB = Utils.objEnergr.get(imeiVal).getApparentEnergyB();//(2)视在电能B相	
						eee.setApparentEnergyB(apparentEnergyB);
						String apparentEnergyC = Utils.objEnergr.get(imeiVal).getApparentEnergyC();//(2)视在电能C相
						eee.setApparentEnergyC(apparentEnergyC);
					}
					eee.setCmd(ifAlarmType);
					eee.setAlarmFimaly(alarmFimally);
					eee.setDataTime(dataTime);
					eee.setImei(imeiVal);
					iotEntity.setEnergyEntity(eee);
					iotEntity.setElectric(threeElec);
					
					break;
				case "CustomMeasureData"://自定义测点数据 CustomMeasureData
					/*装置序列号 deviceSn
					DI 状态 DIStatus
					DO 状态 DOStatus
					告警状态 AlarmStatus
					预警状态 PreAlarmStatus
					越限状态 setpointStatus
					Ir 故障状态 IRCheckStatus
					TC 故障状态 TCCheckStatus
					Ia I1 A
					Ib I2 A
					Ic I3 A
					PMC350F 接入云平台北向规约
					 版本 V1.1
					5
					Pa Pa W
					Pb Pb W
					Pc Pc W
					测点配置详情数组 measurePointArr，具体定义参考 PMC350 规约参数定义
					参数定义 Json Key 值 单位
					参数 Id paramId
					参数值 paramValue
					参数描述 description*/
					break;
				case "BasicPowerData"://基本电脑数据 BasicPowerData
					EnergrEntity ee = new EnergrEntity();
					/*正向有功电能 kWhImp kWh
					反向有功电能 kWhExp kWh
					有功电能净值 kWhNet kWh
					有功电能总和 kWhTot kWh
					正向无功电能 kvarhImp kvarh
					反向无功电能 kvarhExp kvarh
					无功电能净值 kvarhNet kvarh
					无功电能总和 kvarhTot kvarh
					视在电能 kVAh kVAh
					第一象限无功电能 kvarhQ1 kvarh
					第二象限无功电能 kvarhQ2 kvarh
					第三象限无功电能 kvarhQ3 kvarh
					第四象限无功电能 kvarhQ4 kvarh
					A 相正向有功电能 kWhImp1 kWh
					A 相反向有功电能 kWhExp1 kWh
					A 相有功电能净值 kWhNet1 kWh
					A 相有功电能总和 kWhTot1 kWh*/
					String kWhTot1 = json2.getString("kWhTot1");
					ee.setActiveEnergyA(kWhTot1);
					/*A 相正向无功电能 kvarhImp1 kvarh
					A 相反向无功电能 kvarhExp1 kvarh
					A 相无功电能净值 kvarhNet1 kvarh
					A 相无功电能总和 kvarhTot1 kvarh*/
					String kvarhTot1 = json2.getString("kvarhTot1");
					ee.setReactiveEnergyA(kvarhTot1);
					
//					A 相视在电能 kVAh1 kVAh
					String kVAh1 = json2.getString("kVAh1");
					ee.setApparentEnergyA(kVAh1);
					
					/*A 相第一象限无功电能 kvarhQ1_1 kvarh
					A 相第二象限无功电能 kvarhQ2_1 kvarh
					A 相第三象限无功电能 kvarhQ3_1 kvarh
					A 相第四象限无功电能 kvarhQ4_1 kvarh
					B 相正向有功电能 kWhImp2 kWh
					B 相反向有功电能 kWhExp2 kWh
					PMC350F 接入云平台北向规约
					 版本 V1.1
					6
					B 相有功电能净值 kWhNet2 kWh
					B 相有功电能总和 kWhTot2 kWh*/
					String kWhTot2 = json2.getString("kWhTot2");
					ee.setActiveEnergyB(kWhTot2);
					/*B 相正向无功电能 kvarhImp2 kvarh
					B 相反向无功电能 kvarhExp2 kvarh
					B 相无功电能净值 kvarhNet2 kvarh
					B 相无功电能总和 kvarhTot2 kvarh*/
					String kvarhTot2 = json2.getString("kvarhTot2");
					ee.setReactiveEnergyB(kvarhTot2);
//					B 相视在电能 kVAh2 kVAh
					
					String kVAh2 = json2.getString("kVAh2");
					ee.setApparentEnergyB(kVAh2);
					/*B 相第一象限无功电能 kvarhQ1_2 kvarh
					B 相第二象限无功电能 kvarhQ2_2 kvarh
					B 相第三象限无功电能 kvarhQ3_2 kvarh
					B 相第四象限无功电能 kvarhQ4_2 kvarh
					C 相正向有功电能 kWhImp3 kWh
					C 相反向有功电能 kWhExp3 kWh
					C 相有功电能净值 kWhNet3 kWh
					C 相有功电能总和 kWhTot3 kWh*/
					String kWhTot3 = json2.getString("kWhTot3");
					ee.setActiveEnergyC(kWhTot3);
					/*C 相正向无功电能 kvarhImp3 kvarh
					C 相反向无功电能 kvarhExp3 kvarh
					C 相无功电能净值 kvarhNet3 kvarh
					C 相无功电能总和 kvarhTot3 kvarh*/
					String kvarhTot3 = json2.getString("kvarhTot3");
					ee.setReactiveEnergyC(kvarhTot3);
//					C 相视在电能 kVAh3 kVAh
					String kVAh3 = json2.getString("kVAh3");
					ee.setApparentEnergyC(kVAh3);
					/*C 相第一象限无功电能 kvarhQ1_3 kvarh
					C 相第二象限无功电能 kvarhQ2_3 kvarh
					C 相第三象限无功电能 kvarhQ3_3 kvarh
					C 相第四象限无功电能 kvarhQ4_3 kvarh*/
					Utils.objEnergr.put(imeiVal, ee); 
					break;
				case "IntervalPowerDataABC"://三相总分时计费数据 IntervalPowerDataABC
					/*T1 费率 正向有功电能 T1_kWhImp kWh
					T1 费率 反向有功电能 T1_kWhExp kWh
					T1 费率 正向无功电能 T1_kvarhImp kvarh
					T1 费率 反向无功电能 T1_kvarhExp kvarh
					T1 费率 视在电能 T1_kVAh kVAh
					T2 费率 正向有功电能 T2_kWhImp kWh
					T2 费率 反向有功电能 T2_kWhExp kWh
					PMC350F 接入云平台北向规约
					 版本 V1.1
					7
					T2 费率 正向无功电能 T2_kvarhImp kvarh
					T2 费率 反向无功电能 T2_kvarhExp kvarh
					T2 费率 视在电能 T2_kVAh kVAh
					T3 费率 正向有功电能 T3_kWhImp kWh
					T3 费率 反向有功电能 T3_kWhExp kWh
					T3 费率 正向无功电能 T3_kvarhImp kvarh
					T3 费率 反向无功电能 T3_kvarhExp kvarh
					T3 费率 视在电能 T3_kVAh kVAh
					T4 费率 正向有功电能 T4_kWhImp kWh
					T4 费率 反向有功电能 T4_kWhExp kWh
					T4 费率 正向无功电能 T4_kvarhImp kvarh
					T4 费率 反向无功电能 T4_kvarhExp kvarh
					T4 费率 视在电能 T4_kVAh kVAh
					T5 费率 正向有功电能 T5_kWhImp kWh
					T5 费率 反向有功电能 T5_kWhExp kWh
					T5 费率 正向无功电能 T5_kvarhImp kvarh
					T5 费率 反向无功电能 T5_kvarhExp kvarh
					T5 费率 视在电能 T5_kVAh kVAh
					T6 费率 正向有功电能 T6_kWhImp kWh
					T6 费率 反向有功电能 T6_kWhExp kWh
					T6 费率 正向无功电能 T6_kvarhImp kvarh
					T6 费率 反向无功电能 T6_kvarhExp kvarh
					T6 费率 视在电能 T6_kVAh kVAh
					T7 费率 正向有功电能 T7_kWhImp kWh
					T7 费率 反向有功电能 T7_kWhExp kWh
					T7 费率 正向无功电能 T7_kvarhImp kvarh
					T7 费率 反向无功电能 T7_kvarhExp kvarh
					T7 费率 视在电能 T7_kVAh kVAh
					T8 费率 正向有功电能 T8_kWhImp kWh
					T8 费率 反向有功电能 T8_kWhExp kWh
					T8 费率 正向无功电能 T8_kvarhImp kvarh
					T8 费率 反向无功电能 T8_kvarhExp kvarh
					T8 费率 视在电能 T8_kVAh kVAh*/
					break;
				case "IntervalPowerDataA"://A 相分时计费数据 IntervalPowerDataA
					break;
				case "IntervalPowerDataB"://B 相分时计费数据 IntervalPowerDataB
					break;
				case "IntervalPowerDataC"://C 相分时计费数据 IntervalPowerDataC
					break;
				case "PowerQualityData"://基本谐波及电能质量数据 PowerQualityData
					/*A 相电流 THD I1THD --
					B 相电流 THD I2THD --
					C 相电流 THD I3THD --
					A 相电流 TOHD I1TOHD --
					PMC350F 接入云平台北向规约
					 版本 V1.1
					8
					B 相电流 TOHD I2TOHD --
					C 相电流 TOHD I3TOHD --
					A 相电流 TEHD I1TEHD --
					B 相电流 TEHD I2TEHD --
					C 相电流 TEHD I3TEHD --
					A 相电压 THD U1THD --
					B 相电压 THD U2THD --
					C 相电压 THD U3THD --
					A 相电压 TOHD U1TOHD --
					B 相电压 TOHD U2TOHD --
					C 相电压 TOHD U3TOHD --
					A 相电压 TEHD U1TEHD --
					B 相电压 TEHD U2TEHD --
					C 相电压 TEHD U3TEHD --
					A 相电流 K 因子 KFactor1 --
					B 相电流 K 因子 KFactor2 --
					C 相电流 K 因子 KFactor3 --
					A 相电流波峰因子 CrestFactor1 --
					B 相电流波峰因子 CrestFactor2 --
					C 相电流波峰因子 CrestFactor3 --
					电压不平衡度 UUnb --
					电流不平衡度 IUnb --*/
					break;
				case "HarmonicDataCurrentA"://A 相电流分次谐波 HarmonicDataCurrentA
					break;
				case "HarmonicDataCurrentB"://B 相电流分次谐波 HarmonicDataCurrentB
					break;
				case "HarmonicDataCurrentC"://C 相电流分次谐波 HarmonicDataCurrentC
					/*分相电流 H2 I1H2 --
					分相电流 H3 I1H3 --
					分相电流 H4 I1H4 --
					... --
					PMC350F 接入云平台北向规约
					 版本 V1.1
					9
					分相电流 H31 I1H31 */
					break;
				case "HarmonicDataVoltageA"://A 相电压分次谐波 HarmonicDataVoltageA
					break;
				case "HarmonicDataVoltageB"://B 相电压分次谐波 HarmonicDataVoltageB
					break;
				case "HarmonicDataVoltageC"://C 相电压分次谐波 HarmonicDataVoltageC
					/*分相电压 H2 U1H2 --
					分相电压 H3 U1H3 --
					分相电压 H4 U1H4 --
					... --
					分相电压 H31 U1H31 --*/
					break;
				case "DemandData"://需量数据 DemandData
					/*A 相电流实时需量 I1DMD --
					B 相电流实时需量 I2DMD --
					C 相电流实时需量 I3DMD --
					总有功功率实时需量 PImpDMD --
					总无功功率实时需量 QImpDMD --
					总视在功率实时需量 SDMD --
					A 相电流预测需量 I1PredDMD --
					B 相电流预测需量 I2PredDMD --
					C 相电流预测需量 I3PredDMD --
					总有功功率预测需量 PImpPredDMD --
					总无功功率预测需量 QImpPredDMD --
					总视在功率预测需量 SPredDMD --*/
					break;
				case "SOEData"://事件 SOEData
					/*本次上送事件总条数 UpLoadSOENum --
					本次上送 SOE 起始指针 UpLoadSOEStartPtr --
					事件数组 soeDataItemArr --
					大类 broadHeadingType --
					子类 subType --
					时间 soeTime --
					双位置信息 twoPositionMessage --
					值 soeValue --
					描述 description --*/
					break;
				case "RecordData"://定时记录 RecordData
					/*本次上送定时记录变量个数 UpLoadSnapNum
					记录索引 recordIndex
					时间 recordTime
					定时记录变量数组 recordDataItemArr
					记录值 Id recordId
					记录值 recordValue
					描述 description
					 */
					break;
				case "CommandTest"://命令响应定义
//					设备响应数据 modbusAck
					break;
				case "error"://插件错误 error
					break;
				case "NBElectricEnergy"://U特电气功率电能
					eee = new ElectricEnergyEntity();
					devType = json2.getInt("DevType");//4	0x0004
					eee.setDevType(devType);
					cmd = json2.getInt("CMD");//(1)0x30	
					eee.setCmd(cmd);
					iotEntity.setAlarmType(1992);
					int shunt = json2.getInt("Shunt");//(1)	分励
					eee.setShunt(shunt);
					int shuntRelevanceTime = shunt&0x1F;//分励时间：0 - 无输出; 0x1F(31) – 常开; ’1-30’ - 分励保持时
					eee.setShuntRelevanceTime(shuntRelevanceTime);
					int shuntCuPer = (shunt&0x20)-31;//Bit5: 1 - 电压/电流分励;
					eee.setShuntCuPer(shuntCuPer);
					int shuntTemp = (shunt&0x40)-63;//Bit6: 1 - 温度/漏电分励;
					eee.setShuntTemp(shuntTemp);
					int shuntLink = shunt>>7;//Bit7: 1 – 联动分励;
					eee.setShuntLink(shuntLink);
					String activePowerA = json2.getString("ActivePowerA");//有功功率A相(2)	
					eee.setActivePowerA(activePowerA);
					String activePowerB = json2.getString("ActivePowerB");//(2)有功功率B相	
					eee.setActivePowerB(activePowerB);
					String activePowerC = json2.getString("ActivePowerC");//	有功功率C相
					eee.setActivePowerC(activePowerC);
					String reactivePowerA = json2.getString("ReactivePowerA");//(2)无功功率A相	
					eee.setReactivePowerA(reactivePowerA);
					String reactivePowerB = json2.getString("ReactivePowerB");//(2)无功功率B相	 
					eee.setReactivePowerB(reactivePowerB);
					String reactivePowerC = json2.getString("ReactivePowerC");//(2)无功功率C相	
					eee.setReactivePowerC(reactivePowerC);
					String apparentPowerA = json2.getString("ApparentPowerA");//(2)视在功率A相	
					eee.setApparentPowerA(apparentPowerA);
					String apparentPowerB = json2.getString("ApparentPowerB");//(2)视在功率B相	
					eee.setApparentPowerB(apparentPowerB);
					String apparentPowerC = json2.getString("ApparentPowerC");//(2)视在功率C相	
					eee.setApparentPowerC(apparentPowerC);
					String powerFactorA = json2.getString("PowerFactorA");//(2)功率因素A相	
					eee.setPowerFactorA(powerFactorA);
					String powerFactorB = json2.getString("PowerFactorB");//(2)功率因素B相	
					eee.setPowerFactorB(powerFactorB);
					String powerFactorC = json2.getString("PowerFactorC");//(2)功率因素C相	
					eee.setPowerFactorC(powerFactorC);
					String activeEnergyA = json2.getString("ActiveEnergyA");//(2)有功电能A相	
					eee.setActiveEnergyA(activeEnergyA);
					String activeEnergyB = json2.getString("ActiveEnergyB");//(2)有功电能B相	
					eee.setActiveEnergyB(activeEnergyB);
					String activeEnergyC = json2.getString("ActiveEnergyC");//(2)有功电能C相	
					eee.setActiveEnergyC(activeEnergyC);
					String reactiveEnergyA = json2.getString("ReactiveEnergyA");//(2)无功电能A相	
					eee.setReactiveEnergyA(reactiveEnergyA);
					String reactiveEnergyB = json2.getString("ReactiveEnergyB");//(2)无功电能B相	
					eee.setReactiveEnergyB(reactiveEnergyB);
					String reactiveEnergyC = json2.getString("ReactiveEnergyC");//(2)无功电能C相	
					eee.setReactiveEnergyC(reactiveEnergyC);
					String apparentEnergyA = json2.getString("ApparentEnergyA");//(2)视在电能A相	
					eee.setApparentEnergyA(apparentEnergyA);
					String apparentEnergyB = json2.getString("ApparentEnergyB");//(2)视在电能B相	
					eee.setApparentEnergyB(apparentEnergyB);
					String apparentEnergyC = json2.getString("ApparentEnergyC");//(2)视在电能C相
					eee.setApparentEnergyC(apparentEnergyC);
					eee.setDataTime(dataTime);
					eee.setImei(imeiVal);
					
					iotEntity.setEnergyEntity(eee);
					break;
				case "TempHumi":	//NB南京平台普通温湿度
				case "Water_Pre":	//NB南京平台普通水压
					WaterAckEntity waterEntity = new WaterAckEntity();
					switch(devType){
						case 6://普通温湿度
							String TempData = ((float)json2.getInt("TempData"))/10+"";//温度
							String HumiData = ((float)json2.getInt("HumiData"))/10+"";//湿度
							int tempHumiState = json2.getInt("Statue");
							THDevice thDevice = new THDevice();
							thDevice.setTemperature(TempData);
							thDevice.setHumidity(HumiData);
							int hight_temp_threshold = json2.getInt("hight_temp_threshold");	//高温阈值
							int low_temp_threshold = json2.getInt("low_temp_threshold");		//低温阈值
							int hight_humi_threshold = json2.getInt("hight_humi_threshold");		//高湿阈值
							int low_humi_threshold = json2.getInt("low_humi_threshold");			//低湿阈值
							int th_collect_threshold = json2.getInt("collect_threshold");		//采集时间
							int th_send_threshold = json2.getInt("send_threshold");			//上报时间
							waterEntity.setThreshold1(hight_temp_threshold);	
							waterEntity.setThreshold2(low_temp_threshold);
							waterEntity.setThreshold3(hight_humi_threshold);	
							waterEntity.setThreshold4(low_humi_threshold);
							waterEntity.setAckTimes(th_collect_threshold);
							waterEntity.setWaveValue(th_send_threshold);
							waterEntity.setWaterMac(imeiVal);
							waterEntity.setDeviceType(79);
							
							mthe = new MqttTempHumiEntity();
							mthe.setDeviceId("79");
							mthe.setHumidity(HumiData);
							mthe.setMac(imeiVal);
							mthe.setServiceType("temphumi");
							mthe.setTemperature(TempData);
							mthe.setTime(dataTime);
							
							switch(tempHumiState){	//00(基本数据)\0x01(高温报警)\0x02(低温报警)\0x03(高湿报警)\0x04(低湿报警)\0x05(低电量报警)
							case 0:	//心跳
								iotEntity.setAlarmType(1993);
								mthe.setAlarmType("0");
								break;
							case 1:	//高温报警
								iotEntity.setAlarmData(308);
								iotEntity.setMqttState(308);
								iotEntity.setAlarmType(1994);
								mthe.setAlarmType("308");
								break;
							case 2:	//低温报警
								iotEntity.setAlarmData(307);
								iotEntity.setMqttState(307);
								mthe.setAlarmType("307");
								iotEntity.setAlarmType(1994);
								break;
							case 3:	//高湿报警
								iotEntity.setAlarmType(1994);
								iotEntity.setMqttState(408);
								iotEntity.setAlarmData(408);
								mthe.setAlarmType("408");
								break;
							case 4:	//低湿报警
								iotEntity.setAlarmType(1994);
								iotEntity.setAlarmData(407);
								iotEntity.setMqttState(407);
								mthe.setAlarmType("407");
								break;
							case 5:	//低电量报警
								iotEntity.setAlarmType(1994);
								iotEntity.setAlarmData(193);
								iotEntity.setMqttState(193);
								mthe.setAlarmType("193");
								break;
							}
							obj = mthe;
							iotEntity.setThDevice(thDevice);
							break;
						case 5:	//普通水压
							int waterState = json2.getInt("statue");
							String waterValue = json2.getString("data");
							int hight_pre_threshold = json2.getInt("hight_pre_threshold");	//高压阈值
							int low_pre_threshold = json2.getInt("low_pre_threshold");		//低压阈值
							int collect_threshold = json2.getInt("collect_threshold");		//采集时间
							int send_threshold = json2.getInt("send_threshold");			//上报时间
							waterEntity.setThreshold1(hight_pre_threshold);	
							waterEntity.setThreshold2(low_pre_threshold);
							waterEntity.setAckTimes(collect_threshold);
							waterEntity.setWaveValue(send_threshold);
							waterEntity.setWaterMac(imeiVal);
							waterEntity.setDeviceType(78);
							
							
							mwe = new MqttWaterEntity();
							
							mwe.setDeviceId("78");
							mwe.setMac(imeiVal);
							mwe.setServiceType("water");
							mwe.setTime(dataTime);
							mwe.setWaterPressure(waterValue);
							
							switch(waterState){	//00(基本数据)\0x01(高压报警)\0x02(低压报警)\0x03(低电量报警)
								case 0:	//心跳
									iotEntity.setAlarmType(1995);
									mwe.setAlarmType("0");
									break;
								case 1:	//高压报警
									waterState = 218;
									iotEntity.setAlarmType(1996);
									mwe.setAlarmType("218");
									break;
								case 2:	//低压报警
									waterState = 209;
									mwe.setAlarmType("209");
									iotEntity.setAlarmType(1996);
									break;
								case 3:	//低电量报警
									iotEntity.setAlarmType(1996);
									waterState = 193;
									mwe.setAlarmType("193");
									break;
							}
							iotEntity.setAlarmData(waterState);
							iotEntity.setMqttState(waterState);
							iotEntity.setAlarmValue(waterValue);
							obj = mwe;
							break;
					}
					iotEntity.setWaterEntity(waterEntity);
					break;
				case "NBElectricData":	//NB南京平台三相电气-U特电气
					threeElec = new ThreePhaseElectricEntity();
					threeElec.setImeiStr(imeiVal);
					threeElec.setImsiStr(imsi);
					float voltageA = 0.0f;	//A相电压
	    			float voltageB = 0.0f;	//B相电压
	    			float voltageC = 0.0f;	//C相电压
	    			
	    			String runAlarmState = "0";//告警状态
	    			String runCauseState = "0";//报警类型
	    			String runGateState = "1";//闸位状态--U特电气的报警值
	    			
	    			float surplusElectri = 0.0f; //剩余电流-漏电流
	    			float electricityA = 0.0f;
	    			float electricityB = 0.0f;
	    			float electricityC = 0.0f;
	    			
	    			mee = new MqttElectricEntity();
		   			mee.setDeviceId("77");
		   			mee.setMac(imeiVal);
		   			mee.setServiceType("electric");
		   			mee.setTime(dataTime);
					switch(devType){
						case 4://U特电气
							voltageA = Float.parseFloat(json2.getString("VoltageA"));	//A相电压
			    			threeElec.setVoltageA(voltageA+"");
			    			voltageB = Float.parseFloat(json2.getString("VoltageB"));	//B相电压
			    			threeElec.setVoltageB(voltageB+"");
			    			voltageC = Float.parseFloat(json2.getString("VoltageC"));	//C相电压
			    			threeElec.setVoltageC(voltageC+"");
			    			
			    			surplusElectri = Float.parseFloat(json2.getString("residualCurrent")); //剩余电流-漏电流
			    			threeElec.setSurplusElectri(surplusElectri+"");
							
							int electricA = json2.getInt("CurrentA");	//A相电流
							int electricB = json2.getInt("CurrentB");	//B相电流
							int electricC = json2.getInt("CurrentC");	//C相电流
							threeElec.setElectricityA(Utils.getEnergyValue(electricA));
							threeElec.setElectricityB(Utils.getEnergyValue(electricB));
							threeElec.setElectricityC(Utils.getEnergyValue(electricC));
							float Temperature1 = Float.parseFloat(json2.getString("Temperature1"));
							float Temperature2 = Float.parseFloat(json2.getString("Temperature2"));
							float Temperature3 = Float.parseFloat(json2.getString("Temperature3"));
							float Temperature4 = Float.parseFloat(json2.getString("Temperature4"));
							
							threeElec.setTempValueA((Temperature1/10)+"");
							threeElec.setTempValueB((Temperature2/10)+"");
							threeElec.setTempValueC((Temperature3/10)+"");
							threeElec.setTempValueD((Temperature4/10)+"");
							mee.setTemperatureA(threeElec.getTempValueA());
							mee.setTemperatureB(threeElec.getTempValueB());
							mee.setTemperatureC(threeElec.getTempValueC());
							mee.setTemperatureN(threeElec.getTempValueD());
							int DataType = json2.getInt("DataType");
							switch(DataType){
								case 0:
									runAlarmState = "0";
									mee.setAlarmType("0");
									break;
								case 1:	//过压
									runCauseState = "43";
									runGateState = threeElec.getVoltageA();
									runAlarmState = "1";
									mee.setAlarmType("43");
									break;
								case 2:	//欠压
									runCauseState = "44";
									runGateState = threeElec.getVoltageA();
									runAlarmState = "1";
									mee.setAlarmType("44");
									break;
								case 3:	//过流
									runCauseState = "45";
									runGateState = threeElec.getElectricityA();
									runAlarmState = "1";
									mee.setAlarmType("45");
									break;
								case 4:	//漏电流
									runCauseState = "46";
									runGateState = threeElec.getElectricityA();
									runAlarmState = "1";
									mee.setAlarmType("46");
									break;
								case 5:	//温度
									runCauseState = "47";
									runGateState = threeElec.getTempValueA();
									runAlarmState = "1";
									mee.setAlarmType("47");
									break;
								case 6:	//传感器开路故障
									runCauseState = "74";
									runGateState = "1";
									runAlarmState = "1";
									mee.setAlarmType("74");
									break;
								case 7:	//传感器短路
									runCauseState = "74";
									runGateState = "1";
									runAlarmState = "1";
									mee.setAlarmType("74");
									break;
								case 8:	//错相
									runCauseState = "162";
									runGateState = "1";
									runAlarmState = "1";
									mee.setAlarmType("162");
									break;
								case 9:	//缺相
									runCauseState = "153";
									runGateState = "1";
									runAlarmState = "1";
									mee.setAlarmType("153");
									break;
								default:
									runAlarmState = "0";
									break;
							}
							
							break;
						case 3://三相电气
							voltageA = (Float.parseFloat(json2.getString("VoltageA")))/10;	//A相电压
			    			threeElec.setVoltageA(voltageA+"");
			    			voltageB = (Float.parseFloat(json2.getString("VoltageB")))/10;	//B相电压
			    			threeElec.setVoltageB(voltageB+"");
			    			voltageC = (Float.parseFloat(json2.getString("VoltageC")))/10;	//C相电压
			    			threeElec.setVoltageC(voltageC+"");
			    			
			    			surplusElectri = Float.parseFloat(json2.getString("ResidualCurrent")); //剩余电流-漏电流
			    			threeElec.setSurplusElectri(surplusElectri+"");
			    			
							electricityA = Float.parseFloat(json2.getString("CurrentA"));	//A相电流
			    			if(electricityA>10000){//修复异常数据
			    				electricityA=(int)electricityA%10000/10f;
			    			}else{
			    				electricityA=electricityA/10;
			    			}
			    			threeElec.setElectricityA(electricityA+"");
			    			electricityB = Float.parseFloat(json2.getString("CurrentB"));	//B相电流
			    			if(electricityB>10000){
			    				electricityB=(int)electricityB%10000/10f;
			    			}else{
			    				electricityB=electricityB/10;
			    			}
			    			threeElec.setElectricityB(electricityB+"");
			    			electricityC = Float.parseFloat(json2.getString("CurrentC"));	//C相电流
			    			if(electricityC>10000){
			    				electricityC=(int)electricityC%10000/10f;
			    			}else{
			    				electricityC=electricityC/10;
			    			}
			    			threeElec.setElectricityC(electricityC+"");
			    			int currentMaximum = Integer.parseInt(json2.getString("MaxResidualCurrent")); ////剩余电流最大相(1)
			    			threeElec.setCurrentMaximum(currentMaximum+"");
			    			
			    			int RunningState = Integer.parseInt(json2.getString("RunningState")); 		//运行状态
			    			
			    			runGateState = "1";//闸位状态
			    			
			    			int runState = 0;	//运行状态1
			    			
			    			
			    			if((RunningState&0x80)==128){
			    				runAlarmState = "1";
			    			}
			    			
			    			if((RunningState&0x60)==96){
			    				runGateState = "3";
			    				runGateState = "2";
			    			}else if((RunningState&0x40)==64){
			    				runGateState = "2";
			    				runGateState = "2";
			    			}else if((RunningState&0x20)==32){
			    				runGateState = "1";
			    				runGateState = "2";
			    			}
			    			
			    			runState = RunningState&0x1f;
			    			runCauseState = runState + "";	//跳闸、告警原因： 运行状态字(1)
			    			switch(runState){
			    			case 31:	//11111-485故障
			    				runCauseState = "136";
			    				mee.setAlarmType("136");
			    				break;
			    			case 19:	//10011-设置更改
			    				runCauseState = "160";
			    				mee.setAlarmType("160");
			    				break;
			    			case 18:	//10010-手动分闸
			    				runCauseState = "161";
			    				mee.setAlarmType("161");
			    				break;
			    			case 17:	//10001-合闸失败
			    				runCauseState = "48";
			    				mee.setAlarmType("48");
			    				break;
			    			case 16:	//10000-互感器故障
			    				runCauseState = "75";
			    				mee.setAlarmType("75");
			    				break;
			    			case 15:	//01111-闭锁
			    				runCauseState = "156";
			    				mee.setAlarmType("156");
			    				break;
			    			case 14:	//01110-按键试验
			    				runCauseState = "159";
			    				mee.setAlarmType("159");
			    				break;
			    			case 13:	//01101-远程
			    				runCauseState = "158";
			    				mee.setAlarmType("158");
			    				break;
			    			case 12:	//01100-定时试验
			    				runCauseState = "157";
			    				mee.setAlarmType("157");
			    				break;
			    			case 11:	//01011-停电
			    				runCauseState = "155";
			    				mee.setAlarmType("155");
			    				break;
			    			case 10:	//01010-接地
			    				runCauseState = "154";
			    				mee.setAlarmType("154");
			    				break;
			    			case 9:	//01001-过压
			    				runCauseState = "143";
			    				mee.setAlarmType("143");
			    				break;
			    			case 8:	//01000-欠压
			    				runCauseState = "144";
			    				mee.setAlarmType("144");
			    				break;
			    			case 7:	//00111-缺相
			    				runCauseState = "153";
			    				mee.setAlarmType("153");
			    				break;
			    			case 6:	//00110-短路
			    				runCauseState = "49";
			    				mee.setAlarmType("49");
			    				break;
			    			case 5:	//00101-过载-过流报警
			    				runCauseState = "45";
			    				mee.setAlarmType("45");
			    				break;
			    			case 4:	//00100-缺零
			    				runCauseState = "151";
			    				mee.setAlarmType("151");
			    				break;
			    			case 2:	//00010-剩余电流-漏电流报警
			    				runCauseState = "46";
			    				mee.setAlarmType("46");
			    				break;
			    			case 0:	//00000-正常
			    				runCauseState = "0";
			    				mee.setAlarmType("0");
			    				break;
			    			}
							break;
					}
					mee.setElectricityA(electricityA+"");
					mee.setElectricityB(electricityB+"");
		   			mee.setElectricityC(electricityC+"");
		   			mee.setSurplusElectri(surplusElectri+"");
		   			mee.setVoltageA(voltageA+"");
		   			mee.setVoltageB(voltageB+"");
		   			mee.setVoltageC(voltageC+"");
		   			
					threeElec.setRunGateState(runGateState);
					threeElec.setRunCauseState(runCauseState);
	    			threeElec.setRunAlarmState(runAlarmState);
	    			iotEntity.setMqttState(Integer.parseInt(runCauseState));
	    			iotEntity.setElectric(threeElec);
					switch(cmd){
					case 0: //心跳
		    			iotEntity.setAlarmType(1999);
		    			mee.setAlarmType("0");
						break;
					case 3: //485故障
						iotEntity.setMqttState(36);
						iotEntity.setAlarmType(1998);
						mee.setAlarmType("36");
						break;
					}
					obj = mee;
					break;
				case "NBElectricThreshold": //NB南京平台三相电气阈值处理
					ElectricThresholdBean thresholdBean = new ElectricThresholdBean();
					threeElec = new ThreePhaseElectricEntity();
					threeElec.setImeiStr(imeiVal);
					threeElec.setImsiStr(imsi);
					threeElec.setRssiVal(rssi);
					switch(devType){
						case 4://优特电气阈值处理
							String OverVoltageThreshold = json2.getString("OverVoltageThreshold");	//过压阈值
			    			thresholdBean.setOverpressure(OverVoltageThreshold);
			    			
			    			String UnderVoltageThreshold = json2.getString("UnderVoltageThreshold");	//欠压阈值
			    			thresholdBean.setUnpressure(UnderVoltageThreshold);
			    			
			    			String OverCurrentThreshold = json2.getString("OverCurrentThreshold");	 //过流阈值 
			    			thresholdBean.setOverCurrent(OverCurrentThreshold);
			    			
			    			String ResidualCurrentThreshold = json2.getString("ResidualCurrentThreshold");	 //剩余电流阈值
			    			thresholdBean.setLeakCurrent(ResidualCurrentThreshold);
			    			
			    			String TemperatureThresnhold = json2.getString("TemperatureThresnhold");	//温度阈值
			    			List<String> temperatures = new ArrayList<String>();
			    			temperatures.add(TemperatureThresnhold);
			    			temperatures.add(TemperatureThresnhold);
			    			temperatures.add(TemperatureThresnhold);
			    			temperatures.add(TemperatureThresnhold);
			    			thresholdBean.setTemperatures(temperatures);
			    			
			    			threeElec.setElectricBean(thresholdBean);
			    			iotEntity.setElectric(threeElec);
			    			iotEntity.setAlarmType(1997);
							break;
						case 3://三相电气阈值处理
						default://默认三相电气阈值处理方式
							float overpressure = (Float.parseFloat(json2.getString("OverVoltageThreshold")))/10;	//过压阈值
			    			thresholdBean.setOverpressure(overpressure+"");
			    			
			    			float unpressure = (Float.parseFloat(json2.getString("UnderVoltageThreshold")))/10;	//欠压阈值
			    			thresholdBean.setUnpressure(unpressure+"");
			    			
			    			float overCurrent = (Float.parseFloat(json2.getString("OverCurrentThreshold")))/10;	 //过流阈值 
			    			thresholdBean.setOverCurrent(overCurrent+"");
			    			
			    			float leakCurrent = Float.parseFloat(json2.getString("ResidualCurrentThreshold"));	 //剩余电流阈值
			    			thresholdBean.setLeakCurrent(leakCurrent+"");
			    			
			    			threeElec.setElectricBean(thresholdBean);
			    			iotEntity.setElectric(threeElec);
			    			iotEntity.setAlarmType(1997);
							break;
					}
					break;
				case "NBData":
					ProofGasEntity gas = new ProofGasEntity();
					String GasConcentration = json2.getString("GasConcentration");//气体浓度
					int ManipulatorState = json2.getInt("ManipulatorState");//机械手状态(0关 1开)	对应数据库里的1开2关
					int ManipulatorAuto = json2.getInt("ManipulatorAuto");//机械手关联状态(0不关联 1关联)
					gas.setProofGasMac(imeiVal);
					gas.setProofGasTemp(cmd+"");
					gas.setProofGasUnit(ManipulatorAuto+"");
					gas.setProofGasMmol(GasConcentration);
					gas.setProofGasModel("hr001");
					gas.setProofGasType("燃气探测器");
					gas.setProofGasState((ManipulatorState++)+"");
					iotEntity.setElectrState(ManipulatorState+"");
					iotEntity.setAlarmValue(GasConcentration);
					nanJingDao.updateSmokeInfo(iotEntity);
					meterInfoDao = new MeterInfoDaoImp();
					meterInfoDao.insertProofGas(gas);
					
					mge = new MqttGasEntity();
					mge.setDeviceId("72");
					mge.setGasMmol(GasConcentration);
					mge.setServiceType("gas");
					mge.setMac(imeiVal);
					mge.setTime(dataTime);
					switch(cmd){
					case 0:
						iotEntity.setAlarmType(0);
						mge.setAlarmType("0");
						break;//0x00(心跳)
					case 1:
						iotEntity.setAlarmData(1);
						iotEntity.setAlarmType(2);
						mge.setAlarmType("2");
						break;//0x01(报警)
					case 2:
						iotEntity.setAlarmData(2);
						iotEntity.setAlarmType(76);
						mge.setAlarmType("76");
						break;//0x02(传感器故障)
					case 3:
						iotEntity.setAlarmData(3);
						iotEntity.setAlarmType(136);
						mge.setAlarmType("136");
						break;//0x03(串口通讯故障)
					case 4:
						iotEntity.setAlarmData(4);
						iotEntity.setAlarmType(0);
						break;//0x04(使用主电源)
					case 5:
						iotEntity.setAlarmData(5);
						iotEntity.setAlarmType(0);
						break;//0x05(使用备用电源)
					}
					obj = mge;
					break;
				case "Electric_Data":	//NB自动重合闸漏电保护器，电压，电流，漏电流，数据类型0x10(开关闭合) 0x11(开关断开)
					ElectricThresholdBean etb = new ElectricThresholdBean();
					String nb_electric_Voltage = json2.getString("NB_electric_Voltage");//电压(V)同心跳 不解析 同心跳 同心跳
					String nb_electric_Current = json2.getDouble("NB_electric_Current")/10+"";//电流(A)同心跳 不解析 同心跳 同心跳 注意：数据需 除以10
					String nb_electric_Leakage_current = json2.getString("NB_electric_Leakage_current");//漏电流(mA) 同心跳 不解析 同心跳 同心跳
					etb.setUnpressure(nb_electric_Voltage);
					etb.setOverCurrent(nb_electric_Current);
					etb.setLeakCurrent(nb_electric_Leakage_current);
					iotEntity.setEtb(etb);
					
					mee = new MqttElectricEntity();
					mee.setDeviceId("77");
					mee.setElectricityA(nb_electric_Current);
					mee.setElectricityB(nb_electric_Current);
					mee.setElectricityC(nb_electric_Current);
					mee.setMac(imeiVal);
					mee.setServiceType("electric");
					mee.setSurplusElectri(nb_electric_Leakage_current);
					mee.setTime(dataTime);
					mee.setVoltageA(nb_electric_Voltage);
					mee.setVoltageB(nb_electric_Voltage);
					mee.setVoltageC(nb_electric_Voltage);
					switch(cmd){
						case 0:
							iotEntity.setAlarmType(2000);//电气心跳
							mee.setAlarmType("0");
							break;
						case 1:
							iotEntity.setAlarmType(43);//过压报警
							iotEntity.setAlarmData(Integer.parseInt(nb_electric_Voltage));
							iotEntity.setMqttState(43);
							mee.setAlarmType("43");
							break;
						case 2:
							iotEntity.setAlarmType(44);//欠压报警
							iotEntity.setAlarmData(Integer.parseInt(nb_electric_Voltage));
							mee.setAlarmType("44");
							break;
						case 3:
							iotEntity.setAlarmType(136);//485通讯故障
							iotEntity.setAlarmData(136);
							mee.setAlarmType("136");
							break;
						case 4:
							iotEntity.setAlarmType(45);//过流报警
							iotEntity.setAlarmData(Integer.parseInt(nb_electric_Current));
							mee.setAlarmType("45");
							break;
						case 5:
							iotEntity.setAlarmType(46);//漏电报警
							iotEntity.setAlarmData(Integer.parseInt(nb_electric_Leakage_current));
							mee.setAlarmType("46");
							break;
						case 16://开
							iotEntity.setAlarmType(2001);//电气开
							iotEntity.setAlarmData(1);
							mee.setAlarmType("0");
							break;
						case 17://关
							iotEntity.setAlarmType(2002);//电气关
							iotEntity.setAlarmData(2);
							mee.setAlarmType("0");
							break;
					}
					obj = mee;
					break;
				case "Electric_Threshold":	//NB自动重合闸漏电保护器，过压阈值、欠压阈值、过楼阈值、漏电阈值
					String nb_electric_Undervoltage_threshold = json2.getString("NB_electric_Undervoltage_threshold");//欠压阈值(V)
					String nb_electric_Overvoltage_threshold = json2.getString("NB_electric_Overvoltage_threshold");//过压阈值(V)
					String nb_electric_Overcurrent_threshold = json2.getDouble("NB_electric_Overcurrent_threshold")/10+"";//过流阈值(A) 注意：数据需 除以10
					String nb_electric_Leakage_current_threshold = json2.getString("NB_electric_Leakage_current_threshold");//漏电阈值(mA)
					
					String lc = nb_electric_Leakage_current_threshold;
					String oc = nb_electric_Overcurrent_threshold;
					String op = nb_electric_Overvoltage_threshold;
					String up = nb_electric_Undervoltage_threshold;
					AlarmThresholdValueDao mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
					mAlarmThresholdValueDao.addThresholdValue(imeiVal, lc, "", 
							"", "", imeiVal, 46);
					mAlarmThresholdValueDao.addThresholdValue(imeiVal, oc, "", 
							"", "", imeiVal, 45);
					mAlarmThresholdValueDao.addThresholdValue(imeiVal, op, "", 
							"", "", imeiVal, 43);
					mAlarmThresholdValueDao.addThresholdValue(imeiVal, up, "", 
							"", "", imeiVal, 44);
					break;
				default:
					if(json2.has("status")){
						status = json2.getString("status");	//我们自己燃气字段
					}else if(json2.has("Status")){
						status = json2.getString("Status");	//日海燃气字段
					}else if(json2.has("stDevCMD_V")){
						status = json2.getString("stDevCMD_V");	//嘉德烟感操作字段、0x00 在位、0x01 告警、0x02 低电、0x08 对码、0x0D 测试、0x11 报警恢复、0x12 低压恢复、0x13 防拆、0x14 防拆恢复、0x06 撤防(消音)
					}else if(json2.has("stDevSignal_V")){
						status = json2.getString("stDevSignal_V");	//嘉德烟感操作字段、0x00 在位、0x01 告警、0x02 低电、0x08 对码、0x0D 测试、0x11 报警恢复、0x12 低压恢复、0x13 防拆、0x14 防拆恢复、0x06 撤防(消音)
					}else if(json2.has("stDevBattery_V")){
						status = json2.getString("stDevBattery_V");	//嘉德烟感操作字段、0x00 在位、0x01 告警、0x02 低电、0x08 对码、0x0D 测试、0x11 报警恢复、0x12 低压恢复、0x13 防拆、0x14 防拆恢复、0x06 撤防(消音)
					}
					if(status.length()>15){
						status = status.substring(1);
						//代表是燃气、并做MQTT推送
						mge = new MqttGasEntity();
						mge.setDeviceId("72");
						mge.setGasMmol(null);
						mge.setServiceType("gas");
						mge.setMac(imeiVal);
						mge.setTime(dataTime);
						if(serviceId.equals("HeartBeat")){
							mge.setAlarmType("0");
						}else{
							mge.setAlarmType("2");
						}
						obj = mge;
					}
					iotEntity.setStatus(status);
					switch(serviceId){
						case "stDevBattery":
							iotEntity.setAlarmType(1001);//电池量
							//烟感MQTT推送
							mse = new MqttSmokeEntity();
							mse.setAlarmType("0");
							mse.setBatteryPower(status);
							mse.setDeviceId("61");
							mse.setMac(imeiVal);
							mse.setServiceType("smoke");
							mse.setTime(dataTime);
							obj = mse;
							break;
						case "stDevSignal":
							iotEntity.setAlarmType(1000);//设备信号
							break;
						case "HeartBeat":
							iotEntity.setAlarmType(0);//燃气心跳
							break;
						case "Alarm":
							iotEntity.setAlarmType(2);//燃气报警
							break;
						case "stDevCMD":	//NB电信烟感
							switch(status){
								case "0":	//在位
									iotEntity.setAlarmType(0);//烟感
									if(SmokeDaoImpl.ifPowerOff(imeiVal)){
										SmokeDaoImpl.setPowerState(imeiVal, 0);
										iotEntity.setAlarmType(194);
									}
									//判断是否是低电恢复
									break;
								case "1":	//告警
									iotEntity.setAlarmType(202);//烟感
									break;
								case "2":	//低电
									iotEntity.setAlarmType(193);//烟感
									//保存设备的低电状态
									SmokeDaoImpl.setPowerState(imeiVal, 1);
									break;
								case "13":	//测试 0D
									iotEntity.setAlarmType(67);//烟感
									break;
								case "17":	//0x11 报警恢复
									iotEntity.setAlarmType(69);//烟感
									break;
								case "18":	//0x12 低压恢复
									iotEntity.setAlarmType(69);//烟感
									break;
								case "19":	//0x13 防拆
									iotEntity.setAlarmType(14);//烟感
									break;
								case "20":	//0x14 防拆恢复
									iotEntity.setAlarmType(20);//烟感
									break;
								case "6":	//0x06 撤防(消音)
									iotEntity.setAlarmType(6);//烟感
									break;
							}
							if(iotEntity.getAlarmType()!=0){
								mse = new MqttSmokeEntity();
								mse.setAlarmType(iotEntity.getAlarmType()+"");
								mse.setDeviceId("61");
								mse.setMac(imeiVal);
								mse.setServiceType("smoke");
								mse.setTime(dataTime);
								obj = mse;
							}
							break;
					}
					break;
			}
			iotEntity.setDeviceId(deviceId);
			iotEntity.setGatewayId(gatewayId);
			iotEntity.setServiceId(serviceId);
			iotEntity.setServiceType(serviceType);
			if(obj!=null){
				obj = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iotEntity;
	}
}
