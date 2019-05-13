package com.cloudfire.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.ToolNanJinDao;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.ToolNanJinDaoImpl;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.EasyIOTCmdEntity;
import com.cloudfire.entity.EasyIOTEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.ServiceData;
import com.cloudfire.myservice.deviceManagement.DeleteDirectlyConnectedDevice;
import com.cloudfire.server.HrEasyIotServer;
import com.cloudfire.server.ServiceAlarmByHaiMan;
import com.cloudfire.until.Constant;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


public class ToolAction extends ActionSupport implements 
ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -4581514309685063419L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HrEasyIotServer hrServer;
	private final static Log log = LogFactory.getLog(ToolAction.class);
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
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
	
	/**
	 * �����ϴ��ص�
	 */
	public void uploadDevices(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("EasyIotuploadDevices:�����ϴ��ص�====����");
		String result = getRequestPostData(this.request);
		System.out.println("Onet���ݻص������"+result);
		EasyIOTEntity easyIotEntity = new EasyIOTEntity();
		ServiceAlarmByHaiMan hm = new ServiceAlarmByHaiMan();
		hrServer = new HrEasyIotServer();
		easyIotEntity = getEasyIOTEntity(result);
		
		PublicUtils utilsDao = new PublicUtilsImpl();
		utilsDao.updateDeviceMac(easyIotEntity.getDevSerial(),easyIotEntity.getRssivalue());
		String deviceTypeData = easyIotEntity.getDataType();
			switch(deviceTypeData){
				case "HR_electric_switchstate":
					hrServer.HR_electric_switchstate(easyIotEntity);//������¿���״̬
					break;
				case "10" :
					dealZJ(easyIotEntity);	//���ܼҾ�
					break;
				case "HR_electric_data":	//��������豸����������
					hrServer.HR_electric_data(easyIotEntity);
					break;
				case "HR_electric_breakdown": 	//�������485���ϱ���
					hrServer.HR_electric_breakdown(easyIotEntity);
					break;
				case "HR_electric_voltage_alarm":	 //��������豸��ѹ����
					hrServer.HR_electric_alarm(easyIotEntity);
					break;
				case "HR_electric_current_alarm":	//��������豸��������
					hrServer.HR_electric_alarm(easyIotEntity);
					break;
				case "HR_electric_creepage_alarm":	 //��������豸©��������
					hrServer.HR_electric_alarm(easyIotEntity);
					break;
				case "HR_electric_threshold":	//��������豸��ֵ
					hrServer.HR_electric_threshold(easyIotEntity);
					break;
				case "stAlarm":	 //����ε�ҵ���߼�
					hrServer.stAlarm(easyIotEntity);
					break;
				case "HR_smoke_data":		//�Լ��̸�
					hrServer.HR_smoke_data(easyIotEntity);
					break;
				case "HR_BQ_data":	//���ص����������ݴ���
					hrServer.HR_BQ_data(easyIotEntity);
					break;
				case "HR_BQ_threshold":	 //���ص�����ֵ
					hrServer.HR_BQ_threshold(easyIotEntity);
					break;
				case "HR_BQ_breakdown_data":	 //���ص���������
					hrServer.HR_BQ_breakdown_data(easyIotEntity);
					break;
				case "HR_BQ_alarm_data":	//���ص����������ݴ���
					hrServer.HR_BQ_alarm_data(easyIotEntity);
					break;
				case "NB_arc_data":		//NB���ϵ绡̽����
					hrServer.NB_arc_data(easyIotEntity);
					break;
				case "NB_arc_mute_state":		//NB�绡̽��������״̬
					hrServer.NB_arc_data(easyIotEntity);
					break;
				default:	//�������ݴ���
					hm.haiManData(easyIotEntity);
					break;
			}
	}
	
	private void dealZJ(EasyIOTEntity easyIot){
		String mac = easyIot.getDevSerial();
		String iotEventTime = easyIot.getIotEventTime();
		List<ServiceData> serviceData = easyIot.getServiceData();
		Map<String,String> serviceMap =null;
		JSONObject json = new JSONObject();
		JSONObject zjdata=new JSONObject();
		try {
			zjdata.put("datatype","alarm");
			zjdata.put("date",iotEventTime);
			if(serviceData !=null){
				for (ServiceData service:serviceData) {
					String serviceId = service.getServiceId();
					serviceMap = service.getServiceData();
					switch(serviceId){
						case "alarmType":
							String alarmType = serviceMap.get("alarmType");
							zjdata.put("alarmType",alarmType);
							break;
						case "Battery":
							String batteryLevel = serviceMap.get("batteryLevel");
							zjdata.put("battery",batteryLevel);
							break;
						case "Meter":
							String signalStrength = serviceMap.get("signalStrength");
							zjdata.put("signal",signalStrength);
							break;
					}
				}
				String callbackuUrl=getZJCallBackUrl();
				if(callbackuUrl.length()!=0){
					String charset="utf-8";
			        String contentType="application/json";
			        //�������json�ַ���
			        String reqBody=null;
			        //�����ַ
			        String url=callbackuUrl+"/report-dev-callback";
					json.put("notifyType", "deviceDataChanged");
					json.put("deviceId", mac);
					json.put("timestamp", iotEventTime);
					json.put("data", zjdata.toString());
			        reqBody=json.toString();
			        //ʹ��HttpClient���ʽӿ�
			        CloseableHttpClient httpClient = HttpClients.createDefault();
			        StringEntity bodyEntity = new StringEntity(reqBody, charset);
			        HttpPost httpPost = new HttpPost(url);
			        httpPost.setHeader("Content-type", contentType);
			        httpPost.setEntity(bodyEntity);
			        CloseableHttpResponse resp = httpClient.execute(httpPost);
			        httpClient.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getZJCallBackUrl() {
		String loginSql = "select * from easyiot_callbackurl where company = ZJ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		String url="";
		LoginEntity le = new LoginEntity();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				url=rs.getString("callbackUrl");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return url;
	}

	/**
	 * ָ��ص�
	 */
	public void getCommid(){
		this.response.setContentType("application/json");
		this.response.setCharacterEncoding("utf-8");
		System.out.println("getCommid:����ָ��ص���====����");
		String result = getRequestPostData(this.request);
		System.out.println("ָ��ص������"+result);
		EasyIOTCmdEntity easyiot = new EasyIOTCmdEntity();
		easyiot = getEasyIOTCmdEntity(result);
	}
	
	/**
	 * ע��ص�
	 */
	public void registrationCompleted(){
		this.response.setContentType("application/json");
		this.response.setCharacterEncoding("utf-8");
		String result = getRequestPostData(this.request);
		System.out.println("ע��ص������"+result);
	}
	
	/**
	 * ����ص�ʵ�����װ
	 * @param args �ص����ص�����
	 * @return
	 */
	public EasyIOTCmdEntity getEasyIOTCmdEntity(String args) {
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
	public EasyIOTEntity getEasyIOTEntity(String iotStr) {
		String str = iotStr;
		EasyIOTEntity iotEntity = new EasyIOTEntity();
		List<ServiceData> serviceList = new ArrayList<ServiceData>();
		try {
			JSONObject json = new JSONObject(str);
			JSONArray jarray = new JSONArray();
			jarray = json.getJSONArray("serviceData");
			for (int i = 0; i < jarray.length(); i++) {
				ServiceData data = new ServiceData();
				JSONObject json2 = new JSONObject(jarray.get(i).toString());
				String serviceId = json2.getString("serviceId");
				System.out.println("serviceId:"+serviceId);
				
				JSONObject json3 = new JSONObject(json2.get("serviceData").toString());
				Iterator<?> it = json3.keys(); 
				Map<String,String> servicedata = new HashMap<String,String>();
				while(it.hasNext()){
					String key = it.next().toString();
					String value = json3.getString(key);
					System.out.println(key+"="+json3.get(key));
					servicedata.put(key, value);
					if(key.equals("signalStrength")){
						iotEntity.setRssivalue(value);
					}
				}
				data.setServiceData(servicedata);
				data.setServiceId(serviceId);
				switch(serviceId){
					case "10":
						iotEntity.setDataType("10");//@@ʶ���Ǽ��豸
						break;
					case "HR_electric_switchstate":
						iotEntity.setDataType("HR_electric_switchstate");//@lzo switch ע��0 ��բ��ON��		1 ��բ��OFF��
						break;
					case "HR_electric_data":
						iotEntity.setDataType("HR_electric_data");//@lzo ���ݵ����豸��������
						break;
					case "HR_electric_breakdown":
						iotEntity.setDataType("HR_electric_breakdown");//@lzo ���ݵ���������
						break;
					case "HR_electric_voltage_alarm":
						iotEntity.setDataType("HR_electric_voltage_alarm");//@lzo ���ݵ�������ѹ
						break;
					case "HR_electric_current_alarm":
						iotEntity.setDataType("HR_electric_current_alarm");//@lzo ���ݵ���������
						break;
					case "HR_electric_creepage_alarm":
						iotEntity.setDataType("HR_electric_creepage_alarm");//@lzo ���ݵ�����©����
						break;
					case "HR_electric_threshold":
						iotEntity.setDataType("HR_electric_threshold");//@lzo ���ݵ�����ֵ
						break;
					case "stAlarm":
						iotEntity.setDataType("stAlarm");//@lzo �ԽӼε��豸����
						break;
					case "HR_smoke_data":
						iotEntity.setDataType("HR_smoke_data");//@lzo �Խ������Լ����̸�
						break;
					case "HR_BQ_data":
						iotEntity.setDataType("HR_BQ_data");	//@lzo ���ص����豸Э����������
						break;
					case "HR_BQ_breakdown_data":
						iotEntity.setDataType("HR_BQ_breakdown_data");	//@lzo ���ص���������
						break;
					case "HR_BQ_alarm_data":
						iotEntity.setDataType("HR_BQ_alarm_data");	//@lzo ���ص����������ݴ���
						break;
					case "NB_arc_data":
						iotEntity.setDataType("NB_arc_data");	//@lzo NB���ϵ绡̽���� 35
						break;
					case "NB_arc_mute_state":
						iotEntity.setDataType("NB_arc_mute_state");	//@lzo NB���ϵ绡̽���� 35
						break;
				}
				
				serviceList.add(data);
			}
			String devSerial = json.getString("devSerial");
			String createTime = json.getString("createTime");
			if(json.has("iotEventTime")){
				String iotEventTime = json.getString("iotEventTime");
				iotEntity.setIotEventTime(iotEventTime);
			}
			iotEntity.setServiceData(serviceList);
			iotEntity.setDevSerial(devSerial);
			iotEntity.setCreateTime(createTime);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return iotEntity;
	}
	
	public void deleteDeviceById(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String smokeMac = this.request.getParameter("imei");
			ToolNanJinDao td = new ToolNanJinDaoImpl();
			String deviceid = td.getDeviceByImei(smokeMac);
			int deviceType = td.getDeviceTypeByDev(smokeMac);
			String appId = "";
			if(deviceType==23){
				appId = "89ROeDVLwgoYFzMVTKAcZCHz1Bga";
			}else if(deviceType==64){
				appId = Constant.JDAPPID;
			}
			int result = DeleteDirectlyConnectedDevice.deleteByDeviceId(deviceid,appId,deviceType);
			HttpRsult hr = new HttpRsult();
			if(result == 204){
				hr.setErrorCode(0);
				td.deleteDevFromSmoke(smokeMac);
				if(deviceType==73){
					td.deleteGasInfo(smokeMac);
				}
				hr.setError("ɾ���ɹ�");
			}else{
				hr.setErrorCode(2);
				hr.setError("ɾ��ʧ��:"+result);
			}
			JSONObject jObject = new JSONObject(hr);
			this.response.getWriter().write(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
