package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;

import com.cloudfire.dao.ToolNanJinDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.DX_NB_NJ_DataMqttEntity;
import com.cloudfire.entity.EasyIOT;
import com.cloudfire.entity.NanJing_NB_IOT_Entity;
import com.cloudfire.myservice.appAccessSecurity.Authentication;
import com.cloudfire.until.CRC16;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.HttpsUtil;
import com.cloudfire.until.IntegerTo16;
import com.cloudfire.until.JsonUtil;
import com.cloudfire.until.StreamClosedHttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ToolNanJinDaoImpl implements ToolNanJinDao {

	@SuppressWarnings({ "resource", "unchecked" })
	@Override
	public String getTokenByAppId(String appId, String secret) {
		String result = "";
		try {
			HttpsUtil httpsUtil = new HttpsUtil();
			httpsUtil.initSSLConfigForTwoWay();

			String urlLogin = Constant.APP_AUTH;

			Map<String, String> param = new HashMap<>();
			param.put("appId", appId);
			param.put("secret", secret);
			
			StreamClosedHttpResponse responseLogin = httpsUtil.doPostFormUrlEncodedGetStatusLine(urlLogin, param);
			
			Map<String, String> data = new HashMap<>();
	        data = JsonUtil.jsonString2SimpleObj(responseLogin.getContent(), data.getClass());
	        result = data.get("accessToken");
	        System.out.println("accessToken:" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public String subAccessToKen(String accessToken, String serverID,
			String callbackUrl) {
		return null;
	}

	@Override
	public String ackAccessToKen(String accessToken, String serverID,
			String devSerial, String method, String key, int value) {
		return null;
	}

	@Override
	public String ackAccessToKens(String accessToken, String serverID,
			String devSerial, String method, Map<String, Integer> param) {
		return null;
	}

	@Override
	public String connectionPlatform(String serverID, String accessToken) {
		return null;
	}

	@Override
	public String queryServiceMode(String serverID, String accessToken,
			String iotserverId) {
		return null;
	}

	@Override
	public String registeredPlant(String serverID, String accessToken,
			String devSerial, String name, String deviceType,
			String connectPointId, String serviceMode, String endUserName,
			String endUserInfo, String location, String longitude,
			String latitude, String extend_type) {
		return null;
	}

	@Override
	public String updateDevice(String serverID, String accessToken,
			String devSerial, String name, String longitude, String latitude) {
		return null;
	}

	@Override
	public String deleteDevice(String serverID, String accessToken,
			String devSerial) {
		return null;
	}

	@Override
	public String queryDevice(String serverID, String accessToken,
			String devSerial) {
		return null;
	}

	@Override
	public String queryDeviceType(String serverID, String accessToken,
			String devType) {
		return null;
	}

	@Override
	public String queryAllAvailableDeviceTypes(String serverID,
			String accessToken) {
		return null;
	}

	@Override
	public String queryUserAllDevice(String serverID, String accessToken) {
		return null;
	}

	@Override
	public String queryTheDeviceDusinessData(String serverID,
			String accessToken, String devSerial) {
		return null;
	}

	@Override
	public String unSubscribe(String serverID, String accessToken) {
		return null;
	}

	@Override
	public String querySubscribe(String serverID, String accessToken) {
		return null;
	}

	@Override
	public String deviceMessageNotification(String callbackUrl,
			String devSerial, String createTime, String lastMesssageTime,
			String iotEventTime, String serviceData) {
		return null;
	}

	@Override
	public String registrationCompleted(String serverID, String accessToken,
			List<EasyIOT> objdev) {
		return null;
	}

	@Override
	public String queryService(String hostID) {
		return null;
	}

	@Override
	public void addIotEntity(String devSerial, String createTime,
			String iotEventTime, String serviceId, String IOT_key,
			String IOT_value) {

	}

	@Override
	public String subAccessToKen(String accessToken, String serverID,
			String callbackUrl, String IotUrl) {
		return null;
	}

	@Override
	public String ackAccessToKen(String accessToken, String serverID,
			String devSerial, String method, String key, int value,
			String IotUrl) {
		return null;
	}

	@Override
	public String ackAccessToKens(String accessToken, String serverID,
			String devSerial, String method, Map<String, Integer> param,
			String IotUrl) {
		return null;
	}

	@Override
	public String connectionPlatform(String serverID, String accessToken,
			String IotUrl) {
		return null;
	}

	@Override
	public String queryServiceMode(String serverID, String accessToken,
			String iotserverId, String IotUrl) {
		return null;
	}

	@Override
	public String registeredPlant(String serverID, String accessToken,
			String devSerial, String name, String deviceType,
			String connectPointId, String serviceMode, String endUserName,
			String endUserInfo, String location, String longitude,
			String latitude, String extend_type, String IotUrl) {
		return null;
	}

	@Override
	public String updateDevice(String serverID, String accessToken,
			String devSerial, String name, String longitude, String latitude,
			String IotUrl) {
		return null;
	}

	@Override
	public String deleteDevice(String serverID, String accessToken,
			String devSerial, String IotUrl) {
		return null;
	}

	@Override
	public String queryDevice(String serverID, String accessToken,
			String devSerial, String IotUrl) {
		return null;
	}

	@Override
	public String queryDeviceType(String serverID, String accessToken,
			String devType, String IotUrl) {
		return null;
	}

	@Override
	public String queryAllAvailableDeviceTypes(String serverID,
			String accessToken, String IotUrl) {
		return null;
	}

	@Override
	public String queryUserAllDevice(String serverID, String accessToken,
			String IotUrl) {
		return null;
	}

	@Override
	public String queryTheDeviceDusinessData(String serverID,
			String accessToken, String devSerial, String IotUrl) {
		return null;
	}

	@Override
	public String unSubscribe(String serverID, String accessToken, String IotUrl) {
		return null;
	}

	@Override
	public String querySubscribe(String serverID, String accessToken,
			String IotUrl) {
		return null;
	}

	@Override
	public String deviceMessageNotification(String callbackUrl,
			String devSerial, String createTime, String lastMesssageTime,
			String iotEventTime, String serviceData, String IotUrl) {
		return null;
	}

	@Override
	public String registrationCompleted(String serverID, String accessToken,
			List<EasyIOT> objdev, String IotUrl) {
		return null;
	}

	@Override
	public String queryService(String hostID, String IotUrl) {
		return null;
	}

	@Override
	public void addIotEntity(String devSerial, String createTime,
			String iotEventTime, String serviceId, String IOT_key,
			String IOT_value, String IotUrl) {

	}

	@Override
	public void addgasbyhm(String gasMac, int gasalarm, int language,
			int alarmType, int beepsoundlevel, int handonoff) {

	}

	@Override
	public void addImeiDeviceId(String imei, String deviceId) {
		String sql = "REPLACE INTO imei_device(imei,deviceId) values(?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, imei);
			ps.setString(2, deviceId);
			ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("addImeiDeviceId Error.");
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	@Override
	public void addImeiDeviceId(String imei, String deviceId,String imsi) {
		String sql = "REPLACE INTO imei_device(imei,deviceId,imsi) values(?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, imei);
			ps.setString(2, deviceId);
			ps.setString(3, imsi);
			ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("addImeiDeviceId Error.");
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	@Override
	public String getDeviceByImei(String imei) {
		String result = "";
		String sql = "select deviceId from imei_device where imei = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, imei);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString(1);
			}
		} catch (Exception e) {
			System.out.println("addImeiDeviceId Error.");
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public int getDeviceTypeByDev(String smokeMac) {
		int result = 0;
		String sql = "SELECT deviceType from  smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println("addImeiDeviceId Error.");
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public String getImeiBydeviceId(String deviceId) {
		String result = "";
		String sql = "select imei from imei_device where deviceId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, deviceId);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString(1);
			}
		} catch (Exception e) {
			System.out.println("addImeiDeviceId Error.");
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public boolean getCancelCounld(String deviceId) {
		boolean result = false;
		try {
			HttpsUtil httpsUtil = new HttpsUtil();
			String appId = Constant.APPID;//"jPiZ2kgjJ43h33Hc3Ypyluu4h70a";
			String secret = Constant.SECRET;//"a5UFEq5BJDrkV0qC6fN9rzCnaKwa";
			String accessToken =  Authentication.getAccessToken(appId, secret);
			String urlPostAsynCmd = Constant.POST_ASYN_CMD;//"https://180.101.147.89:8743/iocm/app/cmd/v1.4.0/deviceCommands";
			String serviceId = "stCommand";	//要与profile 中定义的serviceId保持一致。
			String method = "stCmdmesg";	//命令服务下具体的命令名称，服务属性等。要与profile 中定义的命令名保持一致。
			ObjectNode paras = JsonUtil.convertObject2ObjectNode("{\"stCmdID_T\":1,\"stCmdDevN_V\":0,\"stCmdDone_T\":4,\"stCmdID_HV\":1,\"stCmd_L\":20,\"stCmdOpt_T\":3,\"stCmdDone_V\":0,\"stCmdID_L\":5,\"stCmdID_LV\":0,\"stCmdDevN_T\":2,\"stCmdOpt_L\":1,\"stCmdDevN_L\":1,\"stCmdOpt_V\":6,\"stCmd_SN\":32,\"stCmdDone_L\":1}");	//命令参数的jsonString，具体格式需要应用和设备约定。
			Map<String, Object> CommandDTOV4 = new HashMap<>();//下发命令的信息
		    CommandDTOV4.put("serviceId", serviceId);
		    CommandDTOV4.put("method", method);
		    CommandDTOV4.put("paras", paras);    
		    Map<String, Object> paramPostAsynCmd = new HashMap<>();
	        paramPostAsynCmd.put("deviceId", deviceId);
	        paramPostAsynCmd.put("command", CommandDTOV4);
	        String jsonRequest = JsonUtil.jsonObj2Sting(paramPostAsynCmd);
	        Map<String, String> header = new HashMap<>();
	        header.put(Constant.HEADER_APP_KEY, appId);
	        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
	        System.out.println(jsonRequest);
	        HttpResponse responsePostAsynCmd = httpsUtil.doPostJson(urlPostAsynCmd, header, jsonRequest);
	        
	        String responseBody = httpsUtil.getHttpResponseBody(responsePostAsynCmd);
	        
	        System.out.println("PostAsynCommand, response content:");
	        System.out.print(responsePostAsynCmd.getStatusLine().getStatusCode());
			int reint = responsePostAsynCmd.getStatusLine().getStatusCode();
			if(reint == 201){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
       
		return result;
	}
	@Override
	public boolean getCancelCounld(String deviceId,int devid,String devState) {
		boolean result = false;
		try {
			HttpsUtil httpsUtil = new HttpsUtil();
			String appId = Constant.APPID;//"jPiZ2kgjJ43h33Hc3Ypyluu4h70a";
			String secret = Constant.SECRET;//"a5UFEq5BJDrkV0qC6fN9rzCnaKwa";
			String urlPostAsynCmd = Constant.POST_ASYN_CMD;//"https://180.101.147.89:8743/iocm/app/cmd/v1.4.0/deviceCommands";
			String serviceId = "stCommand";	//要与profile 中定义的serviceId保持一致。
			String method = "stCmdmesg";	//命令服务下具体的命令名称，服务属性等。要与profile 中定义的命令名保持一致。
			String accessToken =  null;
			ObjectNode paras = null;
			switch(devid){
				case 73: //南京平台7020燃气
					appId = Constant.RQAPPID7;
					secret = Constant.RQSECRET7;
					serviceId = "NBData";	//要与profile 中定义的serviceId保持一致。
					method = "NBControl";
					accessToken =  Authentication.getAccessToken(appId, secret);
					ObjectMapper mapper = new ObjectMapper();
					ObjectNode entity = mapper.createObjectNode();
					entity.put("ControlCMD", devState);
					entity.put("DevType", 1);
					entity.put("Reserved", 1);
					paras = entity;
					break;
			}
			
			Map<String, Object> CommandDTOV4 = new HashMap<>();//下发命令的信息
		    CommandDTOV4.put("serviceId", serviceId);
		    CommandDTOV4.put("method", method);
		    CommandDTOV4.put("paras", paras);    
		    Map<String, Object> paramPostAsynCmd = new HashMap<>();
	        paramPostAsynCmd.put("deviceId", deviceId);
	        paramPostAsynCmd.put("command", CommandDTOV4);
	        String jsonRequest = JsonUtil.jsonObj2Sting(paramPostAsynCmd);
	        Map<String, String> header = new HashMap<>();
	        header.put(Constant.HEADER_APP_KEY, appId);
	        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
	        System.out.println(jsonRequest);
	        HttpResponse responsePostAsynCmd = httpsUtil.doPostJson(urlPostAsynCmd, header, jsonRequest);
	        
	        String responseBody = httpsUtil.getHttpResponseBody(responsePostAsynCmd);
	        
	        System.out.println("PostAsynCommand, response content:");
	        System.out.print(responsePostAsynCmd.getStatusLine().getStatusCode());
			int reint = responsePostAsynCmd.getStatusLine().getStatusCode();
			if(reint == 201){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
       
		return result;
	}
	
	@Override
	public boolean getCancelCounld(String deviceId,int devid) {
		boolean result = false;
		try {
			HttpsUtil httpsUtil = new HttpsUtil();
			String appId = Constant.APPID;//"jPiZ2kgjJ43h33Hc3Ypyluu4h70a";
			String secret = Constant.SECRET;//"a5UFEq5BJDrkV0qC6fN9rzCnaKwa";
			String urlPostAsynCmd = Constant.POST_ASYN_CMD;//"https://180.101.147.89:8743/iocm/app/cmd/v1.4.0/deviceCommands";
			String serviceId = "stCommand";	//要与profile 中定义的serviceId保持一致。
			String method = "stCmdmesg";	//命令服务下具体的命令名称，服务属性等。要与profile 中定义的命令名保持一致。
			String accessToken =  null;
			ObjectNode paras = null;
			switch(devid){
				case 61: //嘉德烟感消音
					appId = Constant.JDAPPID;//"jPiZ2kgjJ43h33Hc3Ypyluu4h70a";
					secret = Constant.JDSECRET;//"a5UFEq5BJDrkV0qC6fN9rzCnaKwa";
					serviceId = "stCommand";	//要与profile 中定义的serviceId保持一致。
					method = "stCmdmesg";
					accessToken =  Authentication.getAccessToken(appId, secret);
					paras = JsonUtil.convertObject2ObjectNode("{\"stCmdID_T\":1,\"stCmdDevN_V\":0,\"stCmdDone_T\":4,\"stCmdID_HV\":1,\"stCmd_L\":20,\"stCmdOpt_T\":3,\"stCmdDone_V\":0,\"stCmdID_L\":5,\"stCmdID_LV\":0,\"stCmdDevN_T\":2,\"stCmdOpt_L\":1,\"stCmdDevN_L\":1,\"stCmdOpt_V\":6,\"stCmd_SN\":32,\"stCmdDone_L\":1}");	//命令参数的jsonString，具体格式需要应用和设备约定。
					break;
				case 73: //南京平台7020燃气
					appId = Constant.RQAPPID7;
					secret = Constant.RQSECRET7;
					serviceId = "NBData";	//要与profile 中定义的serviceId保持一致。
					method = "NBControl";
					accessToken =  Authentication.getAccessToken(appId, secret);
					ObjectMapper mapper = new ObjectMapper();
					ObjectNode entity = mapper.createObjectNode();
					entity.put("ControlCMD", 1);
					entity.put("DevType", 1);
					entity.put("Reserved", devid);
					paras = entity;
					break;
			}
			
			Map<String, Object> CommandDTOV4 = new HashMap<>();//下发命令的信息
		    CommandDTOV4.put("serviceId", serviceId);
		    CommandDTOV4.put("method", method);
		    CommandDTOV4.put("paras", paras);    
		    Map<String, Object> paramPostAsynCmd = new HashMap<>();
	        paramPostAsynCmd.put("deviceId", deviceId);
	        paramPostAsynCmd.put("command", CommandDTOV4);
	        String jsonRequest = JsonUtil.jsonObj2Sting(paramPostAsynCmd);
	        Map<String, String> header = new HashMap<>();
	        header.put(Constant.HEADER_APP_KEY, appId);
	        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
	        System.out.println(jsonRequest);
	        HttpResponse responsePostAsynCmd = httpsUtil.doPostJson(urlPostAsynCmd, header, jsonRequest);
	        
	        String responseBody = httpsUtil.getHttpResponseBody(responsePostAsynCmd);
	        
	        System.out.println("PostAsynCommand, response content:");
	        System.out.print(responsePostAsynCmd.getStatusLine().getStatusCode());
			int reint = responsePostAsynCmd.getStatusLine().getStatusCode();
			if(reint == 201){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
       
		return result;
	}

	@Override
	public DX_NB_NJ_DataMqttEntity getImeiInfo(String imei) {
		String sql = "SELECT named,mac,address,principal1,principal1Phone,time from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		DX_NB_NJ_DataMqttEntity mqtt= null;
		try {
			ps.setString(1, imei);
			rs = ps.executeQuery();
			while(rs.next()){
				if(mqtt == null){
					mqtt = new DX_NB_NJ_DataMqttEntity();
				}
				mqtt.setNamed(rs.getString("named"));
				mqtt.setImei(imei);
				mqtt.setAddress(rs.getString("address"));
				mqtt.setPrincipal1(rs.getString("principal1"));
				mqtt.setPrincipal1Phone(rs.getString("principal1Phone"));
				mqtt.setTime(rs.getString("time"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return mqtt;
	}

	@Override
	public void updateSmokeInfo(NanJing_NB_IOT_Entity entity) {
		StringBuffer sqlstr = new StringBuffer();
		String heartime = GetTime.ConvertTimeByLong();
		sqlstr.append("UPDATE smoke set netState = 1, heartime ='"+heartime+"'");
		if(StringUtils.isNotBlank(entity.getRsiiVal())){
			sqlstr.append(",rssivalue = '"+entity.getRsiiVal()+"'");
		}
		if(StringUtils.isNotBlank(entity.getBatteryPower())){
			sqlstr.append(",lowVoltage = '"+entity.getBatteryPower()+"'");
		}
		if(StringUtils.isNotBlank(entity.getElectrState())){
			sqlstr.append(",electrState = '"+entity.getElectrState()+"'");
		}
		
		sqlstr.append("where mac = '"+entity.getImeiVal()+"'");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		try {
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}

	@Override
	public boolean reSetThread(String deviceId,int devid,int conCmd,String Leakage,String Unervoltage,String Overcurrent,String Overvoltage) {
		boolean result = false;
		try {
			HttpsUtil httpsUtil = new HttpsUtil();
			String appId = Constant.APPID;//"jPiZ2kgjJ43h33Hc3Ypyluu4h70a";
			String secret = Constant.SECRET;//"a5UFEq5BJDrkV0qC6fN9rzCnaKwa";
			String urlPostAsynCmd = Constant.POST_ASYN_CMD;//"https://180.101.147.89:8743/iocm/app/cmd/v1.4.0/deviceCommands";
			String serviceId = "";	//要与profile 中定义的serviceId保持一致。
			String method = "";	//命令服务下具体的命令名称，服务属性等。要与profile 中定义的命令名保持一致。
			String accessToken =  null;
			ObjectNode paras = null;
			switch(devid){
				case 75: //电气1号
					appId = Constant.DQAPPID1;//"jPiZ2kgjJ43h33Hc3Ypyluu4h70a";
					secret = Constant.DQSECRET1;//"a5UFEq5BJDrkV0qC6fN9rzCnaKwa";
					accessToken =  Authentication.getAccessToken(appId, secret);
					switch(conCmd){
						case 12://0x12(控制开关断开)
							serviceId = "Electric_Data";	//要与profile 中定义的serviceId保持一致。
							method = "ElectricControl";
							paras = JsonUtil.convertObject2ObjectNode("{\"DevType\":2,\"ThresholdCMD\":18}");	//命令参数的jsonString，具体格式需要应用和设备约定。
							break;
						case 13://0x13(控制开关闭合)
							serviceId = "Electric_Data";	//要与profile 中定义的serviceId保持一致。
							method = "ElectricControl";
							paras = JsonUtil.convertObject2ObjectNode("{\"DevType\":2,\"ThresholdCMD\":19}");	//命令参数的jsonString，具体格式需要应用和设备约定。
							break;
						case 14://0x14(设置阈值)
							serviceId = "Electric_Threshold";	//要与profile 中定义的serviceId保持一致。
							method = "SetThreshold";
							ObjectMapper mapper = new ObjectMapper();
							ObjectNode entity = mapper.createObjectNode();
							entity.put("DecType", 2);
							entity.put("ControlCMD", 20);
							entity.put("Leakage_current_threshold_value", Leakage);
							entity.put("Unervoltage_threshold_value", Unervoltage);
							entity.put("Overcurrent_threshold_value", Overcurrent);
							entity.put("Overvoltage_threshold_value", Overvoltage);
							paras = entity;//JsonUtil.convertObject2ObjectNode(para);	//命令参数的jsonString，具体格式需要应用和设备约定。
							break;
					}
					break;
				case 77: //三相电气
					appId = Constant.DQAPPID1;//"jPiZ2kgjJ43h33Hc3Ypyluu4h70a";
					secret = Constant.DQSECRET1;//"a5UFEq5BJDrkV0qC6fN9rzCnaKwa";
					accessToken =  Authentication.getAccessToken(appId, secret);
					switch(conCmd){
						case 12://0x12(控制开关断开)
							serviceId = "NBElectricData";	//要与profile 中定义的serviceId保持一致。
							method = "NBControl";
							paras = JsonUtil.convertObject2ObjectNode("{\"DevType\":3,\"cmdControl\":18}");	//命令参数的jsonString，具体格式需要应用和设备约定。
							break;
						case 13://0x13(控制开关闭合)
							serviceId = "NBElectricData";	//要与profile 中定义的serviceId保持一致。
							method = "NBControl";
							paras = JsonUtil.convertObject2ObjectNode("{\"DevType\":3,\"cmdControl\":19}");	//命令参数的jsonString，具体格式需要应用和设备约定。
							break;
						case 14://0x14(设置阈值)
							serviceId = "NBElectricThreshold";	//要与profile 中定义的serviceId保持一致。
							method = "NBSetThreshold";
							ObjectMapper mapper = new ObjectMapper();
							ObjectNode entity = mapper.createObjectNode();
							entity.put("DecType", 3);
							entity.put("cmdControl", 20);
							entity.put("ResidualCurrentThreshold", Leakage);
							entity.put("UnderVoltageThreshold", Float.parseFloat(Unervoltage)*10);
							entity.put("OverCurrentThreshold", Overcurrent);
							entity.put("OverVoltageThreshold", Float.parseFloat(Overvoltage)*10);
							paras = entity;//JsonUtil.convertObject2ObjectNode(para);	//命令参数的jsonString，具体格式需要应用和设备约定。
							break;
					}
					break;
			}
			Map<String, Object> CommandDTOV4 = new HashMap<>();//下发命令的信息
		    CommandDTOV4.put("serviceId", serviceId);
		    CommandDTOV4.put("method", method);
		    CommandDTOV4.put("paras", paras);    
		    Map<String, Object> paramPostAsynCmd = new HashMap<>();
	        paramPostAsynCmd.put("deviceId", deviceId);
	        paramPostAsynCmd.put("command", CommandDTOV4);
	        String jsonRequest = JsonUtil.jsonObj2Sting(paramPostAsynCmd);
	        Map<String, String> header = new HashMap<>();
	        header.put(Constant.HEADER_APP_KEY, appId);
	        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
	        System.out.println(jsonRequest);
	        HttpResponse responsePostAsynCmd = httpsUtil.doPostJson(urlPostAsynCmd, header, jsonRequest);
	        
	        String responseBody = httpsUtil.getHttpResponseBody(responsePostAsynCmd);
	        
	        System.out.println("PostAsynCommand, response content:");
	        System.out.print(responsePostAsynCmd.getStatusLine().getStatusCode());
			int reint = responsePostAsynCmd.getStatusLine().getStatusCode();
			if(reint == 201){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
       
		return result;
	}
	
	@Override
	public boolean reSetThread(String deviceId,int devid,int conCmd,String Leakage,String Unervoltage,String Overcurrent,String Overvoltage,String temperature,String currentMAX,String shuntState) {
		boolean result = false;
		try {
			HttpsUtil httpsUtil = new HttpsUtil();
			String appId = Constant.APPID;//"jPiZ2kgjJ43h33Hc3Ypyluu4h70a";
			String secret = Constant.SECRET;//"a5UFEq5BJDrkV0qC6fN9rzCnaKwa";
			String urlPostAsynCmd = Constant.POST_ASYN_CMD;//"https://180.101.147.89:8743/iocm/app/cmd/v1.4.0/deviceCommands";
			String serviceId = "";	//要与profile 中定义的serviceId保持一致。
			String method = "";	//命令服务下具体的命令名称，服务属性等。要与profile 中定义的命令名保持一致。
			String accessToken =  null;
			ObjectNode paras = null;
			switch(devid){
				case 80: //U特电气
					appId = Constant.UTAPPID;
					secret = Constant.UTSECRET;
					accessToken =  Authentication.getAccessToken(appId, secret);
					ObjectMapper mapper = null;
					ObjectNode entity = null;
					switch(conCmd){
						case 31://0x31(设置分励)
							serviceId = "NBElectricEnergy";	//要与profile 中定义的serviceId保持一致。
							method = "ShuntControl";
							mapper = new ObjectMapper();
							entity = mapper.createObjectNode();
							entity.put("DevType", 4);
							entity.put("CMD", 49);
							entity.put("Shunt", shuntState);
							paras = entity;
							break;
						case 32://0x32(静音)
							serviceId = "NBElectricEnergy";	//要与profile 中定义的serviceId保持一致。
							method = "ShuntControl";
							mapper = new ObjectMapper();
							entity = mapper.createObjectNode();
							entity.put("DevType", 4);
							entity.put("CMD", 50);
							entity.put("Shunt", 0);
							paras = entity;
							break;
						case 33://0x33(报警复位)
							serviceId = "NBElectricEnergy";	//要与profile 中定义的serviceId保持一致。
							method = "ShuntControl";
							mapper = new ObjectMapper();
							entity = mapper.createObjectNode();
							entity.put("DevType", 4);
							entity.put("CMD", 51);
							entity.put("Shunt", 0);
							paras = entity;
							break;
						case 14://0x14(设置阈值)
							serviceId = "NBElectricThreshold";	//要与profile 中定义的serviceId保持一致。
							method = "NBSetThreshold";
							mapper = new ObjectMapper();
							entity = mapper.createObjectNode();
							entity.put("DevType", 4);
							entity.put("CMD", 20);
							entity.put("ResidualCurrentThreshold", Leakage);
							entity.put("UnderVoltageThreshold", Unervoltage);
							entity.put("OverCurrentThreshold", Overcurrent);
							entity.put("OverVoltageThreshold", Overvoltage);
							entity.put("CurrentMAX", currentMAX);
							entity.put("TemperatureThreshold", temperature);
							entity.put("ShuntRelevance", shuntState);
							paras = entity;
							break;
					}
					break;
			}
			Map<String, Object> CommandDTOV4 = new HashMap<>();//下发命令的信息
		    CommandDTOV4.put("serviceId", serviceId);
		    CommandDTOV4.put("method", method);
		    CommandDTOV4.put("paras", paras);    
		    Map<String, Object> paramPostAsynCmd = new HashMap<>();
	        paramPostAsynCmd.put("deviceId", deviceId);
	        paramPostAsynCmd.put("command", CommandDTOV4);
	        String jsonRequest = JsonUtil.jsonObj2Sting(paramPostAsynCmd);
	        Map<String, String> header = new HashMap<>();
	        header.put(Constant.HEADER_APP_KEY, appId);
	        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
	        System.out.println(jsonRequest);
	        HttpResponse responsePostAsynCmd = httpsUtil.doPostJson(urlPostAsynCmd, header, jsonRequest);
	        
	        String responseBody = httpsUtil.getHttpResponseBody(responsePostAsynCmd);
	        
	        System.out.println("PostAsynCommand, response content:");
	        System.out.print(responsePostAsynCmd.getStatusLine().getStatusCode());
			int reint = responsePostAsynCmd.getStatusLine().getStatusCode();
			if(reint == 201){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
       
		return result;
	}
	
	@Override
	public boolean reSet_water_data(String deviceId,int devid,int conCmd,String hight_set,String low_set,String collect_time,String send_time) {
		boolean result = false;
		try {
			HttpsUtil httpsUtil = new HttpsUtil();
			String appId = Constant.PTSYAPPID;
			String secret = Constant.PTSYSECRET;
			String urlPostAsynCmd = Constant.POST_ASYN_CMD;//"https://180.101.147.89:8743/iocm/app/cmd/v1.4.0/deviceCommands";
			String serviceId = "";	//要与profile 中定义的serviceId保持一致。
			String method = "";	//命令服务下具体的命令名称，服务属性等。要与profile 中定义的命令名保持一致。
			String accessToken =  null;
			ObjectNode paras = null;
			switch(devid){
				case 78: //NB南京平台普通水压下发阈值
					appId = Constant.PTSYAPPID;
					secret = Constant.PTSYSECRET;
					accessToken =  Authentication.getAccessToken(appId, secret);
					switch(conCmd){
						case 0://0(设置阈值)
							serviceId = "Water_Pre";	//要与profile 中定义的serviceId保持一致。
							method = "AlarmSet";
							ObjectMapper mapper = new ObjectMapper();
							ObjectNode entity = mapper.createObjectNode();
							entity.put("control_cmd", conCmd);
							entity.put("devtype", 5);
							entity.put("low_set", low_set);
							entity.put("send_time", send_time);
							entity.put("hight_set", hight_set);
							entity.put("collect_time", collect_time);
							paras = entity;//JsonUtil.convertObject2ObjectNode(para);	//命令参数的jsonString，具体格式需要应用和设备约定。
							break;
					}
					break;
			}
			Map<String, Object> CommandDTOV4 = new HashMap<>();//下发命令的信息
		    CommandDTOV4.put("serviceId", serviceId);
		    CommandDTOV4.put("method", method);
		    CommandDTOV4.put("paras", paras);    
		    Map<String, Object> paramPostAsynCmd = new HashMap<>();
	        paramPostAsynCmd.put("deviceId", deviceId);
	        paramPostAsynCmd.put("command", CommandDTOV4);
	        String jsonRequest = JsonUtil.jsonObj2Sting(paramPostAsynCmd);
	        Map<String, String> header = new HashMap<>();
	        header.put(Constant.HEADER_APP_KEY, appId);
	        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
	        System.out.println(jsonRequest);
	        HttpResponse responsePostAsynCmd = httpsUtil.doPostJson(urlPostAsynCmd, header, jsonRequest);
	        
	        String responseBody = httpsUtil.getHttpResponseBody(responsePostAsynCmd);
	        
	        System.out.println("PostAsynCommand, response content:");
	        System.out.print(responsePostAsynCmd.getStatusLine().getStatusCode());
			int reint = responsePostAsynCmd.getStatusLine().getStatusCode();
			if(reint == 201){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
       
		return result;
	}
	
	@Override
	public boolean reSet_TempHumi_data(String deviceId,int devid,int conCmd,String low_TempSet,String hight_TempSet,String low_HumiSet,String Hight_HumiSet,String Tcollect_time,String Tsend_time) {
		boolean result = false;
		try {
			HttpsUtil httpsUtil = new HttpsUtil();
			String appId = Constant.PTSYAPPID;
			String secret = Constant.PTSYSECRET;
			String urlPostAsynCmd = Constant.POST_ASYN_CMD;//"https://180.101.147.89:8743/iocm/app/cmd/v1.4.0/deviceCommands";
			String serviceId = "";	//要与profile 中定义的serviceId保持一致。
			String method = "";	//命令服务下具体的命令名称，服务属性等。要与profile 中定义的命令名保持一致。
			String accessToken =  null;
			ObjectNode paras = null;
			switch(devid){
				case 79: //NB南京平台普通水压下发阈值
					appId = Constant.PTSYAPPID;
					secret = Constant.PTSYSECRET;
					accessToken =  Authentication.getAccessToken(appId, secret);
					switch(conCmd){
						case 0://0(设置阈值)
							serviceId = "TempHumi";	//要与profile 中定义的serviceId保持一致。
							method = "TempAlarmSet";
							ObjectMapper mapper = new ObjectMapper();
							ObjectNode entity = mapper.createObjectNode();
							entity.put("TcontrolCmd", conCmd);
							entity.put("Tdevtype", 6);
							entity.put("Tsend_time", Tsend_time);
							entity.put("Tcollect_time", Tcollect_time);
							entity.put("Low_TempSet", low_TempSet);
							entity.put("Hight_HumiSet", Hight_HumiSet);
							entity.put("Hight_TempSet", hight_TempSet);
							entity.put("Low_HumiSet", low_HumiSet);
							paras = entity;//JsonUtil.convertObject2ObjectNode(para);	//命令参数的jsonString，具体格式需要应用和设备约定。
							break;
					}
					break;
			}
			Map<String, Object> CommandDTOV4 = new HashMap<>();//下发命令的信息
		    CommandDTOV4.put("serviceId", serviceId);
		    CommandDTOV4.put("method", method);
		    CommandDTOV4.put("paras", paras);    
		    Map<String, Object> paramPostAsynCmd = new HashMap<>();
	        paramPostAsynCmd.put("deviceId", deviceId);
	        paramPostAsynCmd.put("command", CommandDTOV4);
	        String jsonRequest = JsonUtil.jsonObj2Sting(paramPostAsynCmd);
	        Map<String, String> header = new HashMap<>();
	        header.put(Constant.HEADER_APP_KEY, appId);
	        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
	        System.out.println(jsonRequest);
	        HttpResponse responsePostAsynCmd = httpsUtil.doPostJson(urlPostAsynCmd, header, jsonRequest);
	        
	        String responseBody = httpsUtil.getHttpResponseBody(responsePostAsynCmd);
	        
	        System.out.println("PostAsynCommand, response content:");
	        System.out.print(responsePostAsynCmd.getStatusLine().getStatusCode());
			int reint = responsePostAsynCmd.getStatusLine().getStatusCode();
			if(reint == 201){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
       
		return result;
	}

	@Override
	public boolean deleteDevFromSmoke(String smokeMac) {
		String sql = "delete from smoke where mac = ?";
		deleteAlarmInfo(smokeMac);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = -1;
		boolean result = false;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeUpdate();
			if(rs>0){
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public boolean deleteAlarmInfo(String smokeMac){
		String sql = "delete from alarm where smokeMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = -1;
		boolean result = false;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeUpdate();
			if(rs>0){
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public boolean deleteGasInfo(String imei) {
		String sql = "delete from g_proofGas where proofGasMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = -1;
		boolean result = false;
		try {
			ps.setString(1, imei);
			rs = ps.executeUpdate();
			if(rs>0){
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public byte[] ackLoraUte(String mac, byte eCmd, String repeaterMac,int dCmd,String shuntState,String Leakage,String Undervoltage,String Overcurrent,String Overvoltage,String temperature,String heartime) {
		byte[] ack = null;
		byte[] crcAck = null;
		byte[] dataLenByte = null;
		byte[] datasValues = null;
		int length = 0;
		switch(dCmd){	//0x31(设置分励) 0x32(静音) 0x33(报警复位)14(阈值) 0xE6(改变心跳时间,秒230) 0xE5(重启229)
		case 31:
			ack = new byte[20];
			length = 20;
			crcAck = new byte[16];
			ack[2] = 0x24;
			dataLenByte = IntegerTo16.toByteArray(10,2);
			ack[5] = dataLenByte[0];
			ack[6] = dataLenByte[1];
			ack[16] = (byte)Integer.parseInt(shuntState);
			break;
		case 32:
			ack = new byte[20];
			length = 20;
			crcAck = new byte[16];
			ack[2] = 0x24;
			dataLenByte = IntegerTo16.toByteArray(10,2);
			ack[5] = dataLenByte[0];
			ack[6] = dataLenByte[1];
			ack[16] = 0x00;
			break;
		case 33:
			ack = new byte[20];
			length = 20;
			crcAck = new byte[16];
			ack[2] = 0x24;
			dataLenByte = IntegerTo16.toByteArray(10,2);
			ack[5] = dataLenByte[0];
			ack[6] = dataLenByte[1];
			ack[16] = 0x00;
			break;
		case 14:
			int OverVoltageThreshold = Integer.parseInt(Overvoltage);
			int UnderVoltageThreshold = Integer.parseInt(Undervoltage);
			int OverCurrentThreshold = (int)Float.parseFloat(Overcurrent);
			int ResidualCurrentThreshold = Integer.parseInt(Leakage);
			int TemperatureThreshold = Integer.parseInt(temperature);
			int CurrentMAX = 0;
			ack = new byte[32];
			length = 32;
			crcAck = new byte[28];
			ack[2] = 0x26;
			dataLenByte = IntegerTo16.toByteArray(22,2);
			ack[5] = dataLenByte[0];
			ack[6] = dataLenByte[1];
			datasValues = IntegerTo16.toByteArray(OverVoltageThreshold, 2);//(2 过压阈值)
			ack[16] = datasValues[0];
			ack[17] = datasValues[1];
			crcAck[16] = ack[17];
			
			datasValues = IntegerTo16.toByteArray(UnderVoltageThreshold, 2);//(2 欠压阈值)
			ack[18] = datasValues[0];
			ack[19] = datasValues[1];
			crcAck[17] = ack[18];
			crcAck[18] = ack[19];
			
			datasValues = IntegerTo16.toByteArray(OverCurrentThreshold, 2);//(2 过流阈值)(2 欠压阈值)
			ack[20] = datasValues[0];
			ack[21] = datasValues[1];
			crcAck[19] = ack[20];
			crcAck[20] = ack[21];
			
			datasValues = IntegerTo16.toByteArray(ResidualCurrentThreshold, 2);//(2 漏电流阈值(2 欠压阈值)
			ack[22] = datasValues[0];
			ack[23] = datasValues[1];
			crcAck[21] = ack[22];
			crcAck[22] = ack[23];
			
			datasValues = IntegerTo16.toByteArray(TemperatureThreshold, 2);//TemperatureThreshold(2 温度阈值)
			ack[24] = datasValues[0];
			ack[25] = datasValues[1];
			crcAck[23] = ack[24];
			crcAck[24] = ack[25];
			
			datasValues = IntegerTo16.toByteArray(CurrentMAX, 2);//CurrentMAX(2 最大输入电流)(2 欠压阈值)
			ack[26] = datasValues[0];
			ack[27] = datasValues[1];
			crcAck[25] = ack[26];
			crcAck[26] = ack[27];
			
			ack[28] = (byte)Integer.parseInt(shuntState);
			crcAck[27] = ack[28];
			
			break;
		case 15://230 E6
			ack = new byte[32];
			length = 32;
			int heartTime = Integer.parseInt(heartime);
			crcAck = new byte[28];
			ack[2] = 0x26;
			dataLenByte = IntegerTo16.toByteArray(22,2);
			ack[5] = dataLenByte[0];
			ack[6] = dataLenByte[1];
			
			datasValues = IntegerTo16.toByteArray(heartTime, 2);//CurrentMAX(2 最大输入电流)(2 欠压阈值)
			ack[16] = datasValues[0];
			ack[17] = datasValues[1];
			
			break;
		case 16://229 E5
			ack = new byte[32];
			length = 32;
			crcAck = new byte[28];
			ack[2] = 0x26;
			dataLenByte = IntegerTo16.toByteArray(22,2);
			ack[5] = dataLenByte[0];
			ack[6] = dataLenByte[1];
			break;
		default :
			ack = new byte[32];
			length = 32;
			crcAck = new byte[28];
			ack[2] = 0x26;
			dataLenByte = IntegerTo16.toByteArray(22,2);
			ack[5] = dataLenByte[0];
			ack[6] = dataLenByte[1];
			break;
		}
		ack[0] = 0x7E;
		ack[1] = 0x0E;
		ack[3] = 0x00;
		ack[4] = 0x01;
		crcAck[0] = ack[1];
		crcAck[1] = ack[2];
		crcAck[2] = ack[3];
		crcAck[3] = ack[4];
		crcAck[4] = ack[5];
		crcAck[5] = ack[6];
		byte[] repeateMac = IntegerTo16.hexString2Bytes(repeaterMac);
		for(int i=0;i<4;i++){
			ack[7+i] = repeateMac[i];
			crcAck[6+i] = repeateMac[i];
		}
		byte[] smokeMac = IntegerTo16.hexString2Bytes(mac);
		for(int i=0;i<4;i++){
			ack[11+i] = smokeMac[i];
			crcAck[10+i] = smokeMac[i];
		}
		ack[15] = eCmd;
		crcAck[14] = ack[15];
		crcAck[15] = ack[16];
		
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcAck));
		ack[length-3] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ack[length-2] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ack[length-1]=0x7f;
		
		return ack;
	}
}
