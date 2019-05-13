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
		System.out.println("���ݵ���getRequestPostData����"+contentLength);
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
		System.out.println("nanjinUploadDatas���ݻص������"+result);
	}
	
	/**
	 * �Ͼ�ƽ̨�����ϴ��ص�
	 */
	public void nanjinUploadDatas(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("�Ͼ�:�����ϴ��ص�====����");
		String result = getRequestPostData(this.request);
		System.out.println("���ݻص������"+result);
		
		String hearTime = GetTime.ConvertTimeByLong();
		
		NanJing_NB_IOT_Entity IotEntity = new NanJing_NB_IOT_Entity();
		hrServer = new HrNanJingIotServer();
		IotEntity = getNanJing_IOT_Entity(result);
		IotEntity.setHearTime(hearTime);
		
		PublicUtils utilsDao = new PublicUtilsImpl();
		utilsDao.updateDeviceMac(IotEntity.getImeiVal());
		
		//���豸���������Ͼ��ε��̸�������ת��������ƽ̨����
		AreaDao ad = new AreaDaoImpl();
		
		int alarmType = IotEntity.getAlarmType();
			switch(alarmType){
				case 1990://�е�������ܵ���
				case 1991://�е��������
					hrServer.Hr_NanJing_EnergyEntity_zhongdian(IotEntity);
					break;
				case 1992:	//U�ص������ܵ�������
					hrServer.Hr_NanJing_EnergyEntity(IotEntity,0);//������������
					break;
				case 1993:	//NB��ͨ��ʪ������
					hrServer.Hr_NanJing_TempHumi(IotEntity,0);//������������
					break;
				case 1994:	//NB��ͨ��ʪ�ȱ���
					hrServer.Hr_NanJing_TempHumi(IotEntity,1);//����������
					break;
				case 1995:	//NB��ͨˮѹ����
					hrServer.Hr_NanJing_WaterData(IotEntity,0);//������������
					break;
				case 1996:	//NB��ͨˮѹ����
					hrServer.Hr_NanJing_WaterData(IotEntity,1);//����������
					break;
				case 1997://���������ֵ
					hrServer.Hr_NanJing_Three_ElectricData(IotEntity,1);//����������
					break; 
				case 1998://�����������
				case 1999://�����������
					hrServer.Hr_NanJing_Three_ElectricData(IotEntity,0);//����������
					break;
				case 2000://��������
					hrServer.Hr_NanJing_ElectricData(IotEntity);//����������
					break;
				case 43:
				case 44:
				case 45:
				case 46:
				case 76://����������
				case 77://����ͨѶ����
				case 136://��������
					hrServer.Hr_NanJing_ElectricAlarm(IotEntity);//����������
					break;
				case 2:
				case 193:
				case 194: //�͵�ָ�
				case 202:
				case 67:
				case 68:
				case 69:
				case 14: 
				case 20:
				case 6:
					hrServer.Hr_NanJing_IotServer(IotEntity);//����������
					break;
				case 1000:	//�豸�ź�ǿ��
					hrServer.Hr_NanJing_DevSignal(IotEntity);//�����ź�����
					break;
				case 1001:	//�豸�������
					hrServer.Hr_NanJing_DevBattery(IotEntity);//������������
					break;
				case 2001:	//������
				case 2002:	//������
					hrServer.Hr_Electric_Switch(IotEntity);//������������
					break;
			}
	}
	
	
	/**
	 * ����ص�ʵ�����װ
	 * @param args �ص����ص�����
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
	 * �����ϴ���װʵ�������
	 * @param iotStr �ص����ص�����
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
				jarray = new JSONObject(json.getString("service"));	//�����Լ�
			}else if(json.has("services")){
				JSONArray jary =json.getJSONArray("services");		//�պ�ȼ��
				jarray = jary.getJSONObject(0);
			}
			/**===========start===========**/
			String serviceId = jarray.getString("serviceId");
			String serviceType = jarray.getString("serviceType");
			JSONObject json2 = new JSONObject(jarray.getString("data"));
			String status = "";
			
			String rssi = "";	//rssi�ź�ǿ��ֵ
			int devType = 0;	//�豸���� 1��ȼ�� 2������
			String imsi = "0";	//imsiֵ
			String batteryPower = "";	//��ص���
			int cmd = 0;	//������ʾ�� 0x00(����)0x01(����)0x03(�豸ͨѶ����)0x10(���رպ�)0x11(���ضϿ�)0x20(��ֵ)
			
			if(json2.has("RSSI")){
				rssi = json2.getString("RSSI");	//rssi�ź�ǿ��ֵ
				iotEntity.setRsiiVal(rssi);
			}
			if(json2.has("IMSI")){
				imsi = json2.getString("IMSI");	//imsiֵ
				iotEntity.setImsiVal(imsi);
			}
			if(json2.has("BatteryPower")){
				batteryPower = json2.getString("BatteryPower");	//��ص���
				iotEntity.setBatteryPower(batteryPower);
			}
			if(json2.has("DevType")){
				devType = json2.getInt("DevType");	//�豸���� 1��ȼ�� 2��������3���������4��U�ص���
			}
			if(json2.has("cmd")){
				cmd = json2.getInt("cmd");	//������ʾ�� 0x00(����) 0x01(��ѹ����) 0x02(Ƿѹ����) 0x03(485ͨѶ����) 0x04(��������) 0x05(©�籨��) 0x10(���رպ�) 0x11(���ضϿ�)
			}
			if(json2.has("rssi")){
				rssi = json2.getString("rssi");	//rssi�ź�ǿ��ֵ
				iotEntity.setRsiiVal(rssi);
			}
			if(json2.has("imsi")){
				imsi = json2.getString("imsi");	//imsiֵ
				iotEntity.setImsiVal(imsi);
			}
			if(StringUtils.isNotBlank(deviceId)&&StringUtils.isNotBlank(imeiVal)){
				nanJingDao.addImeiDeviceId(imeiVal, deviceId, imsi);
				nanJingDao.updateSmokeInfo(iotEntity);
			}
			switch(serviceId){
//			������� serviceId
				case "FrequentlyUsedData"://�������� FrequentlyUsedData
					/*DI ״̬ DIStatus -
					DO ״̬ DOStatus -
					DI ����״̬ DILinkStatus -
					ʣ���������״̬ IRalarmStatus
					PMC350F ������ƽ̨�����Լ
					 �汾 V1.1
					2
					�¶ȱ���״̬ TCalarmStatus
					��������/Ԥ��״̬ IalarmStatus
					ʣ������Լ�״̬ IRCheckStatus
					�¶��Լ�״̬ TCCheckStatus
					�ܹ��ϱ���״̬ AllalarmStatus -
					Խ��״̬ setpointStatus
					�Լ�״̬ selfCheckStatus
					�����ź�ǿ�� wirelessSignalIntensity
					�������й����� kWhImp kWh
					A �������й����� kWhImp1 kWh
					B �������й����� kWhImp2 kWh
					C �������й����� kWhImp3 kWh
					A ����� I1 mA
					B ����� I2 mA
					C ����� I3 mA
					A ���ѹ U1 mV
					B ���ѹ U2 mV
					C ���ѹ U3 mV
					Ƶ�� freq Hz
					�ܹ������� PFavg
					A ���й����� P1 W
					B ���й����� P2 W
					C ���й����� P3 W
					A ���޹����� Q1 var
					B ���޹����� Q2 var
					C ���޹����� Q3 var
					A �����ڹ��� S1 VA
					B �����ڹ��� S2 VA
					C �����ڹ��� S3 VA
					�������й����� P W
					�������޹����� Q var
					���������ڹ��� S VA
					A/AB ��ѹ��λ U1Ang ��
					B/BC ��ѹ��λ U2Ang ��
					C/CA ��ѹ��λ U3Ang ��
					A �������λ IaAng ��
					B �������λ IbAng ��
					C �������λ IcAng ��
					��ѹ��ƽ��� UUnb %
					������ƽ��� IUnb %
					A ���ѹ�ܻ����� U1THD %
					B ���ѹ�ܻ����� U2THD %
					C ���ѹ�ܻ����� U3THD %
					PMC350F ������ƽ̨�����Լ
					 �汾 V1.1
					3
					A ������ܻ����� I1THD %
					B ������ܻ����� I2THD %
					C ������ܻ����� I3THD %
					�¶� 1 TC1 ��
					�¶� 2 TC2 ��
					�¶� 3 TC3 ��
					�¶� 4 TC4 ��
					ʣ����� IR IR mA*/
					
					break;
				case "RealTimeData"://ʵʱ�������� RealTimeData
					/*
					A/AB ��ѹ��� U1Ang ��
					PMC350F ������ƽ̨�����Լ
					 �汾 V1.1
					4
					B/BC ��ѹ��� U2Ang ��
					C/CA ��ѹ��� U3Ang ��
					A �������� IaAng ��
					B �������� IbAng ��
					C �������� IcAng ��
					���������ߵ��� Inc In A
					
					A ������������� dPF1
					B ������������� dPF2
					C ������������� dPF3
					�ܻ����������� dPF
					
					
					DI ״̬ DIStatus--DI ״̬��bit0~ bit1 �ֱ��ʾ DI1~2 ��״̬��0���򿪣�1���պ�
					DI ����״̬ DILinkStatus --- DI ����״̬��bit0~ bit1 �ֱ��ʾ D11~D12 ������״̬��0��û��������1������������
					DO ״̬ DOStatus ---   DO ״̬��bit0~ bit1 �ֱ��ʾ DO1~DO2 ��״̬��0�����أ�1��������
					
					Խ�ޱ���״̬ setpointStatus
					�����Լ�״̬ selfCheckStatus
					SOE ָ������ SOEpointer
					װ������ʱ�� OperatingTime hour
					ʣ������Լ�״̬ IRCheckStatus
					�¶��Լ�״̬ TCCheckStatus
					*/
					
					eee = new ElectricEnergyEntity();  //����
					threeElec = new ThreePhaseElectricEntity();
					threeElec.setImeiStr(imeiVal);
					eee.setImei(imeiVal);
					iotEntity.setAlarmType(1990);
					String U1 = json2.getString("U1");//A ���ѹ U1 V
					threeElec.setVoltageA(U1);
					String U2 = json2.getString("U2");//B ���ѹ U2 V
					threeElec.setVoltageB(U2);
					String U3 = json2.getString("U3");//C ���ѹ U3 V
					threeElec.setVoltageC(U3);
					
					/*ƽ�����ѹ Uavg V
					AB �ߵ�ѹ U12 V
					BC �ߵ�ѹ U23 V
					CA �ߵ�ѹ U31 V
					ƽ���ߵ�ѹ Ullavg V*/
					
					String I1 = json2.getString("I1");//A ����� I1 A
					threeElec.setElectricityA(I1);
					String I2 = json2.getString("I2");//B ����� I2 A
					threeElec.setElectricityB(I2);
					String I3 = json2.getString("I3");//C ����� I3 A
					threeElec.setElectricityC(I3);
					
//					ƽ������� Iavb A
					
					String TC1 = json2.getString("TC1");//�¶� 1 TC1 ��
					threeElec.setTempValueA(TC1);
					String TC2 = json2.getString("TC2");//�¶� 2 TC2 ��
					threeElec.setTempValueB(TC2);
					String TC3 = json2.getString("TC3");//�¶� 3 TC3 ��
					threeElec.setTempValueC(TC3);
					String TC4 = json2.getString("TC4");//�¶� 4 TC4 ��
					threeElec.setTempValueD(TC4);
					String IR = json2.getString("IR");//ʣ����� IR IR mA
					threeElec.setSurplusElectri(IR);
					
					int ifAlarmType = 0; 
					String alarmFimally = "0";
					
					//����
					String IRalarmStatus = json2.getString("IRalarmStatus");//ʣ���������״̬ IRalarmStatus
					
//					ʣ���������״̬��bit0��ʾ��Ӧ״̬��0���������أ� 1������������
					
					
					int AllalarmStatus = json2.getInt("AllalarmStatus");//�ܹ��ϱ���״̬ AllalarmStatus
					
					/*[ע9] �ܹ��ϱ���״̬����Ӧ��״̬Ϊ1��ʾ������0��ʾ���أ�������ʣ�����ͨ������̽ͷ����ʱbit0=1��������ͬ������ʣ��������¶ȷ�������ʱbit14=1������ʣ��������¶Ȼ������DI��������������Ԥ��ʱbit15=1��
							bit7		bit6		bit5		bit4		bit3			bit2			bit1			bit0
							����Ԥ��	��������	�¶�Ԥ��	�¶ȱ���	ʣ�����Ԥ��	ʣ���������	�¶�̽ͷ����	ʣ�����̽ͷ����

							bit15	bit14	bit13	bit12	bit11	bit10	bit9	bit8
							�ܱ���	�ܹ���	DI����	����	ȱ��	��ƽ��	Ƿѹ	��ѹ*/
					
					/*if((AllalarmStatus&0x8000)==32768){	//�ܱ���
						
					}if((AllalarmStatus&0x4000)==16384){//�ܹ���
						
					}if((AllalarmStatus&0x2000)==8192){//DI����
						
					}*/if((AllalarmStatus&0x800)==2048){//ȱ��
						ifAlarmType = 153;
					}if((AllalarmStatus&0x200)==512){//Ƿѹ
						ifAlarmType = 44;
						alarmFimally = U1;
					}if((AllalarmStatus&0x100)==256){//��ѹ
						ifAlarmType = 43;
						alarmFimally = U1;
					}if((AllalarmStatus&0x40)==64){//��������
						ifAlarmType = 45;
						alarmFimally = I1;
					}if((AllalarmStatus&0x04)==4){//ʣ���������--©����
						ifAlarmType = 46;
						alarmFimally = I1;
					}if((AllalarmStatus&0x02)==2){//�¶�̽ͷ����--����
						ifAlarmType = 36;
						alarmFimally = TC1;
					}if((AllalarmStatus&0x01)==1){//ʣ�����̽ͷ����--����
						ifAlarmType = 36;
					}
					
					String TCalarmStatus = json2.getString("TCalarmStatus");//�¶ȱ���״̬ TCalarmStatus
					
					/*[ע5] �¶ȱ���״̬��bit0~ bit3��ʾTC1~TC4��Ӧ״̬��0���������أ� 1������������
					bit15~ bit4	bit3	��	bit0
					0	TC4��������	��	TC1��������*/
					
					String IalarmStatus = json2.getString("IalarmStatus");//��������/Ԥ��״̬ IalarmStatus
					
					/*[ע6] ��������/Ԥ��״̬��bit 0~ bit 1�ֱ��ʾ��������~Ԥ����״̬��0���������أ� 1������������
					bit15~ bit2	bit1	bit0
					0	����Ԥ��	��������*/
					
					/*�����й����� P W
					�����޹����� Q var
					�������ڹ��� S VA
					�ܹ������� PFavg
					Ƶ�� freq Hz*/
					if(IRalarmStatus.equals("1")||IRalarmStatus == "1"){//ʣ���������״̬
						ifAlarmType = 46;
					}else if(TCalarmStatus.equals("1")||TCalarmStatus == "1"){//�¶ȱ���״̬
						ifAlarmType = 47;
					}else if(IalarmStatus.equals("1")||IalarmStatus == "1"){//��������״̬
						ifAlarmType = 45;
					}
					
					String Pa = json2.getString("Pa");//A ���й����� Pa W
					eee.setActivePowerA(Pa);
					String Pb = json2.getString("Pb");//B ���й����� Pb W	
					eee.setActivePowerB(Pb);
					String Pc = json2.getString("Pc");//C ���й����� Pc W
					eee.setActivePowerC(Pc);
					String Q1 = json2.getString("Q1");//A ���޹����� Q1 var
					eee.setReactivePowerA(Q1);
					String Q2 = json2.getString("Q2");//B ���޹����� Q2 var	 
					eee.setReactivePowerB(Q2);
					String Q3 = json2.getString("Q3");//C ���޹����� Q3 var	
					eee.setReactivePowerC(Q3);
					String S1 = json2.getString("S1");//A �����ڹ��� S1 VA	
					eee.setApparentPowerA(S1);
					String S2 = json2.getString("S2");//B �����ڹ��� S2 VA	
					eee.setApparentPowerB(S2);
					String S3 = json2.getString("S3");//C �����ڹ��� S3 VA	
					eee.setApparentPowerC(S3);
					String PF1 = json2.getString("PF1");//A �๦������ PF1
					eee.setPowerFactorA(PF1);
					String PF2 = json2.getString("PF2");//B �๦������ PF2	
					eee.setPowerFactorB(PF2);
					String PF3 = json2.getString("PF3");//C �๦������ PF3	
					eee.setPowerFactorC(PF3);
					if(Utils.objEnergr !=null&&Utils.objEnergr.get(imeiVal)!=null){
						String activeEnergyA = Utils.objEnergr.get(imeiVal).getActiveEnergyA();//(2)�й�����A��	
						eee.setActiveEnergyA(activeEnergyA);
						String activeEnergyB = Utils.objEnergr.get(imeiVal).getActiveEnergyB();//(2)�й�����B��	
						eee.setActiveEnergyB(activeEnergyB);
						String activeEnergyC = Utils.objEnergr.get(imeiVal).getActiveEnergyC();//(2)�й�����C��	
						eee.setActiveEnergyC(activeEnergyC);
						String reactiveEnergyA = Utils.objEnergr.get(imeiVal).getReactiveEnergyA();//(2)�޹�����A��	
						eee.setReactiveEnergyA(reactiveEnergyA);
						String reactiveEnergyB = Utils.objEnergr.get(imeiVal).getReactiveEnergyB();//(2)�޹�����B��	
						eee.setReactiveEnergyB(reactiveEnergyB);
						String reactiveEnergyC = Utils.objEnergr.get(imeiVal).getReactiveEnergyC();//(2)�޹�����C��	
						eee.setReactiveEnergyC(reactiveEnergyC);
						String apparentEnergyA = Utils.objEnergr.get(imeiVal).getApparentEnergyA();//(2)���ڵ���A��	
						eee.setApparentEnergyA(apparentEnergyA);
						String apparentEnergyB = Utils.objEnergr.get(imeiVal).getApparentEnergyB();//(2)���ڵ���B��	
						eee.setApparentEnergyB(apparentEnergyB);
						String apparentEnergyC = Utils.objEnergr.get(imeiVal).getApparentEnergyC();//(2)���ڵ���C��
						eee.setApparentEnergyC(apparentEnergyC);
					}
					eee.setCmd(ifAlarmType);
					eee.setAlarmFimaly(alarmFimally);
					eee.setDataTime(dataTime);
					eee.setImei(imeiVal);
					iotEntity.setEnergyEntity(eee);
					iotEntity.setElectric(threeElec);
					
					break;
				case "CustomMeasureData"://�Զ��������� CustomMeasureData
					/*װ�����к� deviceSn
					DI ״̬ DIStatus
					DO ״̬ DOStatus
					�澯״̬ AlarmStatus
					Ԥ��״̬ PreAlarmStatus
					Խ��״̬ setpointStatus
					Ir ����״̬ IRCheckStatus
					TC ����״̬ TCCheckStatus
					Ia I1 A
					Ib I2 A
					Ic I3 A
					PMC350F ������ƽ̨�����Լ
					 �汾 V1.1
					5
					Pa Pa W
					Pb Pb W
					Pc Pc W
					��������������� measurePointArr�����嶨��ο� PMC350 ��Լ��������
					�������� Json Key ֵ ��λ
					���� Id paramId
					����ֵ paramValue
					�������� description*/
					break;
				case "BasicPowerData"://������������ BasicPowerData
					EnergrEntity ee = new EnergrEntity();
					/*�����й����� kWhImp kWh
					�����й����� kWhExp kWh
					�й����ܾ�ֵ kWhNet kWh
					�й������ܺ� kWhTot kWh
					�����޹����� kvarhImp kvarh
					�����޹����� kvarhExp kvarh
					�޹����ܾ�ֵ kvarhNet kvarh
					�޹������ܺ� kvarhTot kvarh
					���ڵ��� kVAh kVAh
					��һ�����޹����� kvarhQ1 kvarh
					�ڶ������޹����� kvarhQ2 kvarh
					���������޹����� kvarhQ3 kvarh
					���������޹����� kvarhQ4 kvarh
					A �������й����� kWhImp1 kWh
					A �෴���й����� kWhExp1 kWh
					A ���й����ܾ�ֵ kWhNet1 kWh
					A ���й������ܺ� kWhTot1 kWh*/
					String kWhTot1 = json2.getString("kWhTot1");
					ee.setActiveEnergyA(kWhTot1);
					/*A �������޹����� kvarhImp1 kvarh
					A �෴���޹����� kvarhExp1 kvarh
					A ���޹����ܾ�ֵ kvarhNet1 kvarh
					A ���޹������ܺ� kvarhTot1 kvarh*/
					String kvarhTot1 = json2.getString("kvarhTot1");
					ee.setReactiveEnergyA(kvarhTot1);
					
//					A �����ڵ��� kVAh1 kVAh
					String kVAh1 = json2.getString("kVAh1");
					ee.setApparentEnergyA(kVAh1);
					
					/*A ���һ�����޹����� kvarhQ1_1 kvarh
					A ��ڶ������޹����� kvarhQ2_1 kvarh
					A ����������޹����� kvarhQ3_1 kvarh
					A ����������޹����� kvarhQ4_1 kvarh
					B �������й����� kWhImp2 kWh
					B �෴���й����� kWhExp2 kWh
					PMC350F ������ƽ̨�����Լ
					 �汾 V1.1
					6
					B ���й����ܾ�ֵ kWhNet2 kWh
					B ���й������ܺ� kWhTot2 kWh*/
					String kWhTot2 = json2.getString("kWhTot2");
					ee.setActiveEnergyB(kWhTot2);
					/*B �������޹����� kvarhImp2 kvarh
					B �෴���޹����� kvarhExp2 kvarh
					B ���޹����ܾ�ֵ kvarhNet2 kvarh
					B ���޹������ܺ� kvarhTot2 kvarh*/
					String kvarhTot2 = json2.getString("kvarhTot2");
					ee.setReactiveEnergyB(kvarhTot2);
//					B �����ڵ��� kVAh2 kVAh
					
					String kVAh2 = json2.getString("kVAh2");
					ee.setApparentEnergyB(kVAh2);
					/*B ���һ�����޹����� kvarhQ1_2 kvarh
					B ��ڶ������޹����� kvarhQ2_2 kvarh
					B ����������޹����� kvarhQ3_2 kvarh
					B ����������޹����� kvarhQ4_2 kvarh
					C �������й����� kWhImp3 kWh
					C �෴���й����� kWhExp3 kWh
					C ���й����ܾ�ֵ kWhNet3 kWh
					C ���й������ܺ� kWhTot3 kWh*/
					String kWhTot3 = json2.getString("kWhTot3");
					ee.setActiveEnergyC(kWhTot3);
					/*C �������޹����� kvarhImp3 kvarh
					C �෴���޹����� kvarhExp3 kvarh
					C ���޹����ܾ�ֵ kvarhNet3 kvarh
					C ���޹������ܺ� kvarhTot3 kvarh*/
					String kvarhTot3 = json2.getString("kvarhTot3");
					ee.setReactiveEnergyC(kvarhTot3);
//					C �����ڵ��� kVAh3 kVAh
					String kVAh3 = json2.getString("kVAh3");
					ee.setApparentEnergyC(kVAh3);
					/*C ���һ�����޹����� kvarhQ1_3 kvarh
					C ��ڶ������޹����� kvarhQ2_3 kvarh
					C ����������޹����� kvarhQ3_3 kvarh
					C ����������޹����� kvarhQ4_3 kvarh*/
					Utils.objEnergr.put(imeiVal, ee); 
					break;
				case "IntervalPowerDataABC"://�����ܷ�ʱ�Ʒ����� IntervalPowerDataABC
					/*T1 ���� �����й����� T1_kWhImp kWh
					T1 ���� �����й����� T1_kWhExp kWh
					T1 ���� �����޹����� T1_kvarhImp kvarh
					T1 ���� �����޹����� T1_kvarhExp kvarh
					T1 ���� ���ڵ��� T1_kVAh kVAh
					T2 ���� �����й����� T2_kWhImp kWh
					T2 ���� �����й����� T2_kWhExp kWh
					PMC350F ������ƽ̨�����Լ
					 �汾 V1.1
					7
					T2 ���� �����޹����� T2_kvarhImp kvarh
					T2 ���� �����޹����� T2_kvarhExp kvarh
					T2 ���� ���ڵ��� T2_kVAh kVAh
					T3 ���� �����й����� T3_kWhImp kWh
					T3 ���� �����й����� T3_kWhExp kWh
					T3 ���� �����޹����� T3_kvarhImp kvarh
					T3 ���� �����޹����� T3_kvarhExp kvarh
					T3 ���� ���ڵ��� T3_kVAh kVAh
					T4 ���� �����й����� T4_kWhImp kWh
					T4 ���� �����й����� T4_kWhExp kWh
					T4 ���� �����޹����� T4_kvarhImp kvarh
					T4 ���� �����޹����� T4_kvarhExp kvarh
					T4 ���� ���ڵ��� T4_kVAh kVAh
					T5 ���� �����й����� T5_kWhImp kWh
					T5 ���� �����й����� T5_kWhExp kWh
					T5 ���� �����޹����� T5_kvarhImp kvarh
					T5 ���� �����޹����� T5_kvarhExp kvarh
					T5 ���� ���ڵ��� T5_kVAh kVAh
					T6 ���� �����й����� T6_kWhImp kWh
					T6 ���� �����й����� T6_kWhExp kWh
					T6 ���� �����޹����� T6_kvarhImp kvarh
					T6 ���� �����޹����� T6_kvarhExp kvarh
					T6 ���� ���ڵ��� T6_kVAh kVAh
					T7 ���� �����й����� T7_kWhImp kWh
					T7 ���� �����й����� T7_kWhExp kWh
					T7 ���� �����޹����� T7_kvarhImp kvarh
					T7 ���� �����޹����� T7_kvarhExp kvarh
					T7 ���� ���ڵ��� T7_kVAh kVAh
					T8 ���� �����й����� T8_kWhImp kWh
					T8 ���� �����й����� T8_kWhExp kWh
					T8 ���� �����޹����� T8_kvarhImp kvarh
					T8 ���� �����޹����� T8_kvarhExp kvarh
					T8 ���� ���ڵ��� T8_kVAh kVAh*/
					break;
				case "IntervalPowerDataA"://A ���ʱ�Ʒ����� IntervalPowerDataA
					break;
				case "IntervalPowerDataB"://B ���ʱ�Ʒ����� IntervalPowerDataB
					break;
				case "IntervalPowerDataC"://C ���ʱ�Ʒ����� IntervalPowerDataC
					break;
				case "PowerQualityData"://����г���������������� PowerQualityData
					/*A ����� THD I1THD --
					B ����� THD I2THD --
					C ����� THD I3THD --
					A ����� TOHD I1TOHD --
					PMC350F ������ƽ̨�����Լ
					 �汾 V1.1
					8
					B ����� TOHD I2TOHD --
					C ����� TOHD I3TOHD --
					A ����� TEHD I1TEHD --
					B ����� TEHD I2TEHD --
					C ����� TEHD I3TEHD --
					A ���ѹ THD U1THD --
					B ���ѹ THD U2THD --
					C ���ѹ THD U3THD --
					A ���ѹ TOHD U1TOHD --
					B ���ѹ TOHD U2TOHD --
					C ���ѹ TOHD U3TOHD --
					A ���ѹ TEHD U1TEHD --
					B ���ѹ TEHD U2TEHD --
					C ���ѹ TEHD U3TEHD --
					A ����� K ���� KFactor1 --
					B ����� K ���� KFactor2 --
					C ����� K ���� KFactor3 --
					A ������������� CrestFactor1 --
					B ������������� CrestFactor2 --
					C ������������� CrestFactor3 --
					��ѹ��ƽ��� UUnb --
					������ƽ��� IUnb --*/
					break;
				case "HarmonicDataCurrentA"://A ������ִ�г�� HarmonicDataCurrentA
					break;
				case "HarmonicDataCurrentB"://B ������ִ�г�� HarmonicDataCurrentB
					break;
				case "HarmonicDataCurrentC"://C ������ִ�г�� HarmonicDataCurrentC
					/*������� H2 I1H2 --
					������� H3 I1H3 --
					������� H4 I1H4 --
					... --
					PMC350F ������ƽ̨�����Լ
					 �汾 V1.1
					9
					������� H31 I1H31 */
					break;
				case "HarmonicDataVoltageA"://A ���ѹ�ִ�г�� HarmonicDataVoltageA
					break;
				case "HarmonicDataVoltageB"://B ���ѹ�ִ�г�� HarmonicDataVoltageB
					break;
				case "HarmonicDataVoltageC"://C ���ѹ�ִ�г�� HarmonicDataVoltageC
					/*�����ѹ H2 U1H2 --
					�����ѹ H3 U1H3 --
					�����ѹ H4 U1H4 --
					... --
					�����ѹ H31 U1H31 --*/
					break;
				case "DemandData"://�������� DemandData
					/*A �����ʵʱ���� I1DMD --
					B �����ʵʱ���� I2DMD --
					C �����ʵʱ���� I3DMD --
					���й�����ʵʱ���� PImpDMD --
					���޹�����ʵʱ���� QImpDMD --
					�����ڹ���ʵʱ���� SDMD --
					A �����Ԥ������ I1PredDMD --
					B �����Ԥ������ I2PredDMD --
					C �����Ԥ������ I3PredDMD --
					���й�����Ԥ������ PImpPredDMD --
					���޹�����Ԥ������ QImpPredDMD --
					�����ڹ���Ԥ������ SPredDMD --*/
					break;
				case "SOEData"://�¼� SOEData
					/*���������¼������� UpLoadSOENum --
					�������� SOE ��ʼָ�� UpLoadSOEStartPtr --
					�¼����� soeDataItemArr --
					���� broadHeadingType --
					���� subType --
					ʱ�� soeTime --
					˫λ����Ϣ twoPositionMessage --
					ֵ soeValue --
					���� description --*/
					break;
				case "RecordData"://��ʱ��¼ RecordData
					/*�������Ͷ�ʱ��¼�������� UpLoadSnapNum
					��¼���� recordIndex
					ʱ�� recordTime
					��ʱ��¼�������� recordDataItemArr
					��¼ֵ Id recordId
					��¼ֵ recordValue
					���� description
					 */
					break;
				case "CommandTest"://������Ӧ����
//					�豸��Ӧ���� modbusAck
					break;
				case "error"://������� error
					break;
				case "NBElectricEnergy"://U�ص������ʵ���
					eee = new ElectricEnergyEntity();
					devType = json2.getInt("DevType");//4	0x0004
					eee.setDevType(devType);
					cmd = json2.getInt("CMD");//(1)0x30	
					eee.setCmd(cmd);
					iotEntity.setAlarmType(1992);
					int shunt = json2.getInt("Shunt");//(1)	����
					eee.setShunt(shunt);
					int shuntRelevanceTime = shunt&0x1F;//����ʱ�䣺0 - �����; 0x1F(31) �C ����; ��1-30�� - ��������ʱ
					eee.setShuntRelevanceTime(shuntRelevanceTime);
					int shuntCuPer = (shunt&0x20)-31;//Bit5: 1 - ��ѹ/��������;
					eee.setShuntCuPer(shuntCuPer);
					int shuntTemp = (shunt&0x40)-63;//Bit6: 1 - �¶�/©�����;
					eee.setShuntTemp(shuntTemp);
					int shuntLink = shunt>>7;//Bit7: 1 �C ��������;
					eee.setShuntLink(shuntLink);
					String activePowerA = json2.getString("ActivePowerA");//�й�����A��(2)	
					eee.setActivePowerA(activePowerA);
					String activePowerB = json2.getString("ActivePowerB");//(2)�й�����B��	
					eee.setActivePowerB(activePowerB);
					String activePowerC = json2.getString("ActivePowerC");//	�й�����C��
					eee.setActivePowerC(activePowerC);
					String reactivePowerA = json2.getString("ReactivePowerA");//(2)�޹�����A��	
					eee.setReactivePowerA(reactivePowerA);
					String reactivePowerB = json2.getString("ReactivePowerB");//(2)�޹�����B��	 
					eee.setReactivePowerB(reactivePowerB);
					String reactivePowerC = json2.getString("ReactivePowerC");//(2)�޹�����C��	
					eee.setReactivePowerC(reactivePowerC);
					String apparentPowerA = json2.getString("ApparentPowerA");//(2)���ڹ���A��	
					eee.setApparentPowerA(apparentPowerA);
					String apparentPowerB = json2.getString("ApparentPowerB");//(2)���ڹ���B��	
					eee.setApparentPowerB(apparentPowerB);
					String apparentPowerC = json2.getString("ApparentPowerC");//(2)���ڹ���C��	
					eee.setApparentPowerC(apparentPowerC);
					String powerFactorA = json2.getString("PowerFactorA");//(2)��������A��	
					eee.setPowerFactorA(powerFactorA);
					String powerFactorB = json2.getString("PowerFactorB");//(2)��������B��	
					eee.setPowerFactorB(powerFactorB);
					String powerFactorC = json2.getString("PowerFactorC");//(2)��������C��	
					eee.setPowerFactorC(powerFactorC);
					String activeEnergyA = json2.getString("ActiveEnergyA");//(2)�й�����A��	
					eee.setActiveEnergyA(activeEnergyA);
					String activeEnergyB = json2.getString("ActiveEnergyB");//(2)�й�����B��	
					eee.setActiveEnergyB(activeEnergyB);
					String activeEnergyC = json2.getString("ActiveEnergyC");//(2)�й�����C��	
					eee.setActiveEnergyC(activeEnergyC);
					String reactiveEnergyA = json2.getString("ReactiveEnergyA");//(2)�޹�����A��	
					eee.setReactiveEnergyA(reactiveEnergyA);
					String reactiveEnergyB = json2.getString("ReactiveEnergyB");//(2)�޹�����B��	
					eee.setReactiveEnergyB(reactiveEnergyB);
					String reactiveEnergyC = json2.getString("ReactiveEnergyC");//(2)�޹�����C��	
					eee.setReactiveEnergyC(reactiveEnergyC);
					String apparentEnergyA = json2.getString("ApparentEnergyA");//(2)���ڵ���A��	
					eee.setApparentEnergyA(apparentEnergyA);
					String apparentEnergyB = json2.getString("ApparentEnergyB");//(2)���ڵ���B��	
					eee.setApparentEnergyB(apparentEnergyB);
					String apparentEnergyC = json2.getString("ApparentEnergyC");//(2)���ڵ���C��
					eee.setApparentEnergyC(apparentEnergyC);
					eee.setDataTime(dataTime);
					eee.setImei(imeiVal);
					
					iotEntity.setEnergyEntity(eee);
					break;
				case "TempHumi":	//NB�Ͼ�ƽ̨��ͨ��ʪ��
				case "Water_Pre":	//NB�Ͼ�ƽ̨��ͨˮѹ
					WaterAckEntity waterEntity = new WaterAckEntity();
					switch(devType){
						case 6://��ͨ��ʪ��
							String TempData = ((float)json2.getInt("TempData"))/10+"";//�¶�
							String HumiData = ((float)json2.getInt("HumiData"))/10+"";//ʪ��
							int tempHumiState = json2.getInt("Statue");
							THDevice thDevice = new THDevice();
							thDevice.setTemperature(TempData);
							thDevice.setHumidity(HumiData);
							int hight_temp_threshold = json2.getInt("hight_temp_threshold");	//������ֵ
							int low_temp_threshold = json2.getInt("low_temp_threshold");		//������ֵ
							int hight_humi_threshold = json2.getInt("hight_humi_threshold");		//��ʪ��ֵ
							int low_humi_threshold = json2.getInt("low_humi_threshold");			//��ʪ��ֵ
							int th_collect_threshold = json2.getInt("collect_threshold");		//�ɼ�ʱ��
							int th_send_threshold = json2.getInt("send_threshold");			//�ϱ�ʱ��
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
							
							switch(tempHumiState){	//00(��������)\0x01(���±���)\0x02(���±���)\0x03(��ʪ����)\0x04(��ʪ����)\0x05(�͵�������)
							case 0:	//����
								iotEntity.setAlarmType(1993);
								mthe.setAlarmType("0");
								break;
							case 1:	//���±���
								iotEntity.setAlarmData(308);
								iotEntity.setMqttState(308);
								iotEntity.setAlarmType(1994);
								mthe.setAlarmType("308");
								break;
							case 2:	//���±���
								iotEntity.setAlarmData(307);
								iotEntity.setMqttState(307);
								mthe.setAlarmType("307");
								iotEntity.setAlarmType(1994);
								break;
							case 3:	//��ʪ����
								iotEntity.setAlarmType(1994);
								iotEntity.setMqttState(408);
								iotEntity.setAlarmData(408);
								mthe.setAlarmType("408");
								break;
							case 4:	//��ʪ����
								iotEntity.setAlarmType(1994);
								iotEntity.setAlarmData(407);
								iotEntity.setMqttState(407);
								mthe.setAlarmType("407");
								break;
							case 5:	//�͵�������
								iotEntity.setAlarmType(1994);
								iotEntity.setAlarmData(193);
								iotEntity.setMqttState(193);
								mthe.setAlarmType("193");
								break;
							}
							obj = mthe;
							iotEntity.setThDevice(thDevice);
							break;
						case 5:	//��ͨˮѹ
							int waterState = json2.getInt("statue");
							String waterValue = json2.getString("data");
							int hight_pre_threshold = json2.getInt("hight_pre_threshold");	//��ѹ��ֵ
							int low_pre_threshold = json2.getInt("low_pre_threshold");		//��ѹ��ֵ
							int collect_threshold = json2.getInt("collect_threshold");		//�ɼ�ʱ��
							int send_threshold = json2.getInt("send_threshold");			//�ϱ�ʱ��
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
							
							switch(waterState){	//00(��������)\0x01(��ѹ����)\0x02(��ѹ����)\0x03(�͵�������)
								case 0:	//����
									iotEntity.setAlarmType(1995);
									mwe.setAlarmType("0");
									break;
								case 1:	//��ѹ����
									waterState = 218;
									iotEntity.setAlarmType(1996);
									mwe.setAlarmType("218");
									break;
								case 2:	//��ѹ����
									waterState = 209;
									mwe.setAlarmType("209");
									iotEntity.setAlarmType(1996);
									break;
								case 3:	//�͵�������
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
				case "NBElectricData":	//NB�Ͼ�ƽ̨�������-U�ص���
					threeElec = new ThreePhaseElectricEntity();
					threeElec.setImeiStr(imeiVal);
					threeElec.setImsiStr(imsi);
					float voltageA = 0.0f;	//A���ѹ
	    			float voltageB = 0.0f;	//B���ѹ
	    			float voltageC = 0.0f;	//C���ѹ
	    			
	    			String runAlarmState = "0";//�澯״̬
	    			String runCauseState = "0";//��������
	    			String runGateState = "1";//բλ״̬--U�ص����ı���ֵ
	    			
	    			float surplusElectri = 0.0f; //ʣ�����-©����
	    			float electricityA = 0.0f;
	    			float electricityB = 0.0f;
	    			float electricityC = 0.0f;
	    			
	    			mee = new MqttElectricEntity();
		   			mee.setDeviceId("77");
		   			mee.setMac(imeiVal);
		   			mee.setServiceType("electric");
		   			mee.setTime(dataTime);
					switch(devType){
						case 4://U�ص���
							voltageA = Float.parseFloat(json2.getString("VoltageA"));	//A���ѹ
			    			threeElec.setVoltageA(voltageA+"");
			    			voltageB = Float.parseFloat(json2.getString("VoltageB"));	//B���ѹ
			    			threeElec.setVoltageB(voltageB+"");
			    			voltageC = Float.parseFloat(json2.getString("VoltageC"));	//C���ѹ
			    			threeElec.setVoltageC(voltageC+"");
			    			
			    			surplusElectri = Float.parseFloat(json2.getString("residualCurrent")); //ʣ�����-©����
			    			threeElec.setSurplusElectri(surplusElectri+"");
							
							int electricA = json2.getInt("CurrentA");	//A�����
							int electricB = json2.getInt("CurrentB");	//B�����
							int electricC = json2.getInt("CurrentC");	//C�����
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
								case 1:	//��ѹ
									runCauseState = "43";
									runGateState = threeElec.getVoltageA();
									runAlarmState = "1";
									mee.setAlarmType("43");
									break;
								case 2:	//Ƿѹ
									runCauseState = "44";
									runGateState = threeElec.getVoltageA();
									runAlarmState = "1";
									mee.setAlarmType("44");
									break;
								case 3:	//����
									runCauseState = "45";
									runGateState = threeElec.getElectricityA();
									runAlarmState = "1";
									mee.setAlarmType("45");
									break;
								case 4:	//©����
									runCauseState = "46";
									runGateState = threeElec.getElectricityA();
									runAlarmState = "1";
									mee.setAlarmType("46");
									break;
								case 5:	//�¶�
									runCauseState = "47";
									runGateState = threeElec.getTempValueA();
									runAlarmState = "1";
									mee.setAlarmType("47");
									break;
								case 6:	//��������·����
									runCauseState = "74";
									runGateState = "1";
									runAlarmState = "1";
									mee.setAlarmType("74");
									break;
								case 7:	//��������·
									runCauseState = "74";
									runGateState = "1";
									runAlarmState = "1";
									mee.setAlarmType("74");
									break;
								case 8:	//����
									runCauseState = "162";
									runGateState = "1";
									runAlarmState = "1";
									mee.setAlarmType("162");
									break;
								case 9:	//ȱ��
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
						case 3://�������
							voltageA = (Float.parseFloat(json2.getString("VoltageA")))/10;	//A���ѹ
			    			threeElec.setVoltageA(voltageA+"");
			    			voltageB = (Float.parseFloat(json2.getString("VoltageB")))/10;	//B���ѹ
			    			threeElec.setVoltageB(voltageB+"");
			    			voltageC = (Float.parseFloat(json2.getString("VoltageC")))/10;	//C���ѹ
			    			threeElec.setVoltageC(voltageC+"");
			    			
			    			surplusElectri = Float.parseFloat(json2.getString("ResidualCurrent")); //ʣ�����-©����
			    			threeElec.setSurplusElectri(surplusElectri+"");
			    			
							electricityA = Float.parseFloat(json2.getString("CurrentA"));	//A�����
			    			if(electricityA>10000){//�޸��쳣����
			    				electricityA=(int)electricityA%10000/10f;
			    			}else{
			    				electricityA=electricityA/10;
			    			}
			    			threeElec.setElectricityA(electricityA+"");
			    			electricityB = Float.parseFloat(json2.getString("CurrentB"));	//B�����
			    			if(electricityB>10000){
			    				electricityB=(int)electricityB%10000/10f;
			    			}else{
			    				electricityB=electricityB/10;
			    			}
			    			threeElec.setElectricityB(electricityB+"");
			    			electricityC = Float.parseFloat(json2.getString("CurrentC"));	//C�����
			    			if(electricityC>10000){
			    				electricityC=(int)electricityC%10000/10f;
			    			}else{
			    				electricityC=electricityC/10;
			    			}
			    			threeElec.setElectricityC(electricityC+"");
			    			int currentMaximum = Integer.parseInt(json2.getString("MaxResidualCurrent")); ////ʣ����������(1)
			    			threeElec.setCurrentMaximum(currentMaximum+"");
			    			
			    			int RunningState = Integer.parseInt(json2.getString("RunningState")); 		//����״̬
			    			
			    			runGateState = "1";//բλ״̬
			    			
			    			int runState = 0;	//����״̬1
			    			
			    			
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
			    			runCauseState = runState + "";	//��բ���澯ԭ�� ����״̬��(1)
			    			switch(runState){
			    			case 31:	//11111-485����
			    				runCauseState = "136";
			    				mee.setAlarmType("136");
			    				break;
			    			case 19:	//10011-���ø���
			    				runCauseState = "160";
			    				mee.setAlarmType("160");
			    				break;
			    			case 18:	//10010-�ֶ���բ
			    				runCauseState = "161";
			    				mee.setAlarmType("161");
			    				break;
			    			case 17:	//10001-��բʧ��
			    				runCauseState = "48";
			    				mee.setAlarmType("48");
			    				break;
			    			case 16:	//10000-����������
			    				runCauseState = "75";
			    				mee.setAlarmType("75");
			    				break;
			    			case 15:	//01111-����
			    				runCauseState = "156";
			    				mee.setAlarmType("156");
			    				break;
			    			case 14:	//01110-��������
			    				runCauseState = "159";
			    				mee.setAlarmType("159");
			    				break;
			    			case 13:	//01101-Զ��
			    				runCauseState = "158";
			    				mee.setAlarmType("158");
			    				break;
			    			case 12:	//01100-��ʱ����
			    				runCauseState = "157";
			    				mee.setAlarmType("157");
			    				break;
			    			case 11:	//01011-ͣ��
			    				runCauseState = "155";
			    				mee.setAlarmType("155");
			    				break;
			    			case 10:	//01010-�ӵ�
			    				runCauseState = "154";
			    				mee.setAlarmType("154");
			    				break;
			    			case 9:	//01001-��ѹ
			    				runCauseState = "143";
			    				mee.setAlarmType("143");
			    				break;
			    			case 8:	//01000-Ƿѹ
			    				runCauseState = "144";
			    				mee.setAlarmType("144");
			    				break;
			    			case 7:	//00111-ȱ��
			    				runCauseState = "153";
			    				mee.setAlarmType("153");
			    				break;
			    			case 6:	//00110-��·
			    				runCauseState = "49";
			    				mee.setAlarmType("49");
			    				break;
			    			case 5:	//00101-����-��������
			    				runCauseState = "45";
			    				mee.setAlarmType("45");
			    				break;
			    			case 4:	//00100-ȱ��
			    				runCauseState = "151";
			    				mee.setAlarmType("151");
			    				break;
			    			case 2:	//00010-ʣ�����-©��������
			    				runCauseState = "46";
			    				mee.setAlarmType("46");
			    				break;
			    			case 0:	//00000-����
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
					case 0: //����
		    			iotEntity.setAlarmType(1999);
		    			mee.setAlarmType("0");
						break;
					case 3: //485����
						iotEntity.setMqttState(36);
						iotEntity.setAlarmType(1998);
						mee.setAlarmType("36");
						break;
					}
					obj = mee;
					break;
				case "NBElectricThreshold": //NB�Ͼ�ƽ̨���������ֵ����
					ElectricThresholdBean thresholdBean = new ElectricThresholdBean();
					threeElec = new ThreePhaseElectricEntity();
					threeElec.setImeiStr(imeiVal);
					threeElec.setImsiStr(imsi);
					threeElec.setRssiVal(rssi);
					switch(devType){
						case 4://���ص�����ֵ����
							String OverVoltageThreshold = json2.getString("OverVoltageThreshold");	//��ѹ��ֵ
			    			thresholdBean.setOverpressure(OverVoltageThreshold);
			    			
			    			String UnderVoltageThreshold = json2.getString("UnderVoltageThreshold");	//Ƿѹ��ֵ
			    			thresholdBean.setUnpressure(UnderVoltageThreshold);
			    			
			    			String OverCurrentThreshold = json2.getString("OverCurrentThreshold");	 //������ֵ 
			    			thresholdBean.setOverCurrent(OverCurrentThreshold);
			    			
			    			String ResidualCurrentThreshold = json2.getString("ResidualCurrentThreshold");	 //ʣ�������ֵ
			    			thresholdBean.setLeakCurrent(ResidualCurrentThreshold);
			    			
			    			String TemperatureThresnhold = json2.getString("TemperatureThresnhold");	//�¶���ֵ
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
						case 3://���������ֵ����
						default://Ĭ�����������ֵ����ʽ
							float overpressure = (Float.parseFloat(json2.getString("OverVoltageThreshold")))/10;	//��ѹ��ֵ
			    			thresholdBean.setOverpressure(overpressure+"");
			    			
			    			float unpressure = (Float.parseFloat(json2.getString("UnderVoltageThreshold")))/10;	//Ƿѹ��ֵ
			    			thresholdBean.setUnpressure(unpressure+"");
			    			
			    			float overCurrent = (Float.parseFloat(json2.getString("OverCurrentThreshold")))/10;	 //������ֵ 
			    			thresholdBean.setOverCurrent(overCurrent+"");
			    			
			    			float leakCurrent = Float.parseFloat(json2.getString("ResidualCurrentThreshold"));	 //ʣ�������ֵ
			    			thresholdBean.setLeakCurrent(leakCurrent+"");
			    			
			    			threeElec.setElectricBean(thresholdBean);
			    			iotEntity.setElectric(threeElec);
			    			iotEntity.setAlarmType(1997);
							break;
					}
					break;
				case "NBData":
					ProofGasEntity gas = new ProofGasEntity();
					String GasConcentration = json2.getString("GasConcentration");//����Ũ��
					int ManipulatorState = json2.getInt("ManipulatorState");//��е��״̬(0�� 1��)	��Ӧ���ݿ����1��2��
					int ManipulatorAuto = json2.getInt("ManipulatorAuto");//��е�ֹ���״̬(0������ 1����)
					gas.setProofGasMac(imeiVal);
					gas.setProofGasTemp(cmd+"");
					gas.setProofGasUnit(ManipulatorAuto+"");
					gas.setProofGasMmol(GasConcentration);
					gas.setProofGasModel("hr001");
					gas.setProofGasType("ȼ��̽����");
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
						break;//0x00(����)
					case 1:
						iotEntity.setAlarmData(1);
						iotEntity.setAlarmType(2);
						mge.setAlarmType("2");
						break;//0x01(����)
					case 2:
						iotEntity.setAlarmData(2);
						iotEntity.setAlarmType(76);
						mge.setAlarmType("76");
						break;//0x02(����������)
					case 3:
						iotEntity.setAlarmData(3);
						iotEntity.setAlarmType(136);
						mge.setAlarmType("136");
						break;//0x03(����ͨѶ����)
					case 4:
						iotEntity.setAlarmData(4);
						iotEntity.setAlarmType(0);
						break;//0x04(ʹ������Դ)
					case 5:
						iotEntity.setAlarmData(5);
						iotEntity.setAlarmType(0);
						break;//0x05(ʹ�ñ��õ�Դ)
					}
					obj = mge;
					break;
				case "Electric_Data":	//NB�Զ��غ�բ©�籣��������ѹ��������©��������������0x10(���رպ�) 0x11(���ضϿ�)
					ElectricThresholdBean etb = new ElectricThresholdBean();
					String nb_electric_Voltage = json2.getString("NB_electric_Voltage");//��ѹ(V)ͬ���� ������ ͬ���� ͬ����
					String nb_electric_Current = json2.getDouble("NB_electric_Current")/10+"";//����(A)ͬ���� ������ ͬ���� ͬ���� ע�⣺������ ����10
					String nb_electric_Leakage_current = json2.getString("NB_electric_Leakage_current");//©����(mA) ͬ���� ������ ͬ���� ͬ����
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
							iotEntity.setAlarmType(2000);//��������
							mee.setAlarmType("0");
							break;
						case 1:
							iotEntity.setAlarmType(43);//��ѹ����
							iotEntity.setAlarmData(Integer.parseInt(nb_electric_Voltage));
							iotEntity.setMqttState(43);
							mee.setAlarmType("43");
							break;
						case 2:
							iotEntity.setAlarmType(44);//Ƿѹ����
							iotEntity.setAlarmData(Integer.parseInt(nb_electric_Voltage));
							mee.setAlarmType("44");
							break;
						case 3:
							iotEntity.setAlarmType(136);//485ͨѶ����
							iotEntity.setAlarmData(136);
							mee.setAlarmType("136");
							break;
						case 4:
							iotEntity.setAlarmType(45);//��������
							iotEntity.setAlarmData(Integer.parseInt(nb_electric_Current));
							mee.setAlarmType("45");
							break;
						case 5:
							iotEntity.setAlarmType(46);//©�籨��
							iotEntity.setAlarmData(Integer.parseInt(nb_electric_Leakage_current));
							mee.setAlarmType("46");
							break;
						case 16://��
							iotEntity.setAlarmType(2001);//������
							iotEntity.setAlarmData(1);
							mee.setAlarmType("0");
							break;
						case 17://��
							iotEntity.setAlarmType(2002);//������
							iotEntity.setAlarmData(2);
							mee.setAlarmType("0");
							break;
					}
					obj = mee;
					break;
				case "Electric_Threshold":	//NB�Զ��غ�բ©�籣��������ѹ��ֵ��Ƿѹ��ֵ����¥��ֵ��©����ֵ
					String nb_electric_Undervoltage_threshold = json2.getString("NB_electric_Undervoltage_threshold");//Ƿѹ��ֵ(V)
					String nb_electric_Overvoltage_threshold = json2.getString("NB_electric_Overvoltage_threshold");//��ѹ��ֵ(V)
					String nb_electric_Overcurrent_threshold = json2.getDouble("NB_electric_Overcurrent_threshold")/10+"";//������ֵ(A) ע�⣺������ ����10
					String nb_electric_Leakage_current_threshold = json2.getString("NB_electric_Leakage_current_threshold");//©����ֵ(mA)
					
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
						status = json2.getString("status");	//�����Լ�ȼ���ֶ�
					}else if(json2.has("Status")){
						status = json2.getString("Status");	//�պ�ȼ���ֶ�
					}else if(json2.has("stDevCMD_V")){
						status = json2.getString("stDevCMD_V");	//�ε��̸в����ֶΡ�0x00 ��λ��0x01 �澯��0x02 �͵硢0x08 ���롢0x0D ���ԡ�0x11 �����ָ���0x12 ��ѹ�ָ���0x13 ����0x14 ����ָ���0x06 ����(����)
					}else if(json2.has("stDevSignal_V")){
						status = json2.getString("stDevSignal_V");	//�ε��̸в����ֶΡ�0x00 ��λ��0x01 �澯��0x02 �͵硢0x08 ���롢0x0D ���ԡ�0x11 �����ָ���0x12 ��ѹ�ָ���0x13 ����0x14 ����ָ���0x06 ����(����)
					}else if(json2.has("stDevBattery_V")){
						status = json2.getString("stDevBattery_V");	//�ε��̸в����ֶΡ�0x00 ��λ��0x01 �澯��0x02 �͵硢0x08 ���롢0x0D ���ԡ�0x11 �����ָ���0x12 ��ѹ�ָ���0x13 ����0x14 ����ָ���0x06 ����(����)
					}
					if(status.length()>15){
						status = status.substring(1);
						//������ȼ��������MQTT����
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
							iotEntity.setAlarmType(1001);//�����
							//�̸�MQTT����
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
							iotEntity.setAlarmType(1000);//�豸�ź�
							break;
						case "HeartBeat":
							iotEntity.setAlarmType(0);//ȼ������
							break;
						case "Alarm":
							iotEntity.setAlarmType(2);//ȼ������
							break;
						case "stDevCMD":	//NB�����̸�
							switch(status){
								case "0":	//��λ
									iotEntity.setAlarmType(0);//�̸�
									if(SmokeDaoImpl.ifPowerOff(imeiVal)){
										SmokeDaoImpl.setPowerState(imeiVal, 0);
										iotEntity.setAlarmType(194);
									}
									//�ж��Ƿ��ǵ͵�ָ�
									break;
								case "1":	//�澯
									iotEntity.setAlarmType(202);//�̸�
									break;
								case "2":	//�͵�
									iotEntity.setAlarmType(193);//�̸�
									//�����豸�ĵ͵�״̬
									SmokeDaoImpl.setPowerState(imeiVal, 1);
									break;
								case "13":	//���� 0D
									iotEntity.setAlarmType(67);//�̸�
									break;
								case "17":	//0x11 �����ָ�
									iotEntity.setAlarmType(69);//�̸�
									break;
								case "18":	//0x12 ��ѹ�ָ�
									iotEntity.setAlarmType(69);//�̸�
									break;
								case "19":	//0x13 ����
									iotEntity.setAlarmType(14);//�̸�
									break;
								case "20":	//0x14 ����ָ�
									iotEntity.setAlarmType(20);//�̸�
									break;
								case "6":	//0x06 ����(����)
									iotEntity.setAlarmType(6);//�̸�
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
