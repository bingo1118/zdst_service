package com.cloudfire.myservice.deviceManagement;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.myservice.appAccessSecurity.Authentication;
import com.cloudfire.until.Constant;
import com.cloudfire.until.HttpsUtil;
import com.cloudfire.until.JsonUtil;
import com.cloudfire.until.StreamClosedHttpResponse;

/**
 * Set device information:
 * This interface is used by an NA to set or modify device information.
 */
public class ModifyDeviceInfo {

	@SuppressWarnings("unchecked")
	public static String ModifyDevice(String deviceId,String name,String manufacturerId,String manufacturerName,String deviceType,String model,String protocolType,String appId){
		String result = "";
        // Two-Way Authentication
        try {
			HttpsUtil httpsUtil = new HttpsUtil();
			httpsUtil.initSSLConfigForTwoWay();

			String secret = "";
			if(StringUtils.isBlank(appId)){//ע�ᵽ�����Լ����˺�
				appId = Constant.APPID;
				secret = Constant.SECRET;
			}else if(StringUtils.isNotBlank(appId)&&appId.equals("89ROeDVLwgoYFzMVTKAcZCHz1Bga")){//ע�ᵽ�պ��˺��豸
				secret = "j4Ss41R345uYGqfAqglxteHwnyEa";
			}else if(StringUtils.isNotBlank(appId)&&appId.equals("dKQfWWBDv_1kfpLzb1gJKlBZjIUa")){//ע�ᵽ�����Լ���ʽ����������ƽ̨
				secret = "IOotgcjky7LO0KQeCfs6I96R4JUa";
			}else if(StringUtils.isNotBlank(appId)&&appId.equals("jPiZ2kgjJ43h33Hc3Ypyluu4h70a")){//ע�ᵽ�����Լ����Է���������ƽ̨
				secret = "a5UFEq5BJDrkV0qC6fN9rzCnaKwa";
			}
			String accessToken = Authentication.getAccessToken(appId, secret);


			//please replace the deviceId, when you use the demo.
//			String deviceId = "f190281e-0c57-40fd-81fe-f2e881480899";
			String urlModifyDeviceInfo = Constant.MODIFY_DEVICE_INFO + "/" + deviceId+"?appId="+appId;

			//please replace the following parameter values, when you use the demo.
			//And those parameter values must be consistent with the content of profile that have been preset to IoT platform.
			//The following parameter values of this demo are use the watermeter profile that already initialized to IoT platform.
			if(StringUtils.isBlank(manufacturerId)){
				manufacturerId= "hanrun2018";//����ID
			}
			if(StringUtils.isBlank(manufacturerName)){
				manufacturerName = "hanrun2018";//��������
			}
			if(StringUtils.isBlank(deviceType)){
				deviceType = "FuelGas";		//�豸����
			}
			if(StringUtils.isBlank(model)){
				model = "180629";			//�豸�ͺ�
			}
			if(StringUtils.isBlank(protocolType)){
				protocolType = "CoAP";		//Э������
			}

			Map<String, Object> paramModifyDeviceInfo = new HashMap<>();
			paramModifyDeviceInfo.put("manufacturerId", manufacturerId);
			paramModifyDeviceInfo.put("manufacturerName", manufacturerName);
			paramModifyDeviceInfo.put("deviceType", deviceType);
			paramModifyDeviceInfo.put("model", model);
			paramModifyDeviceInfo.put("protocolType", protocolType);
			paramModifyDeviceInfo.put("name", name);

			
			String jsonRequest = JsonUtil.jsonObj2Sting(paramModifyDeviceInfo);

			Map<String, String> header = new HashMap<>();
			header.put(Constant.HEADER_APP_KEY, appId);
			header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);

			StreamClosedHttpResponse responseModifyDeviceInfo = httpsUtil.doPutJsonGetStatusLine(urlModifyDeviceInfo,
			        header, jsonRequest);

			System.out.println("ModifyDeviceInfo, response content:");
			System.out.println(responseModifyDeviceInfo.getStatusLine());
			System.out.println(responseModifyDeviceInfo.getContent());
			System.out.println();
			result = responseModifyDeviceInfo.getContent();
		} catch (Exception e) {
			result = "�޸ĳ���";
			e.printStackTrace();
		}
        return result;
    }
	
	@SuppressWarnings("unchecked")
	public static String ModifyDevice(String deviceId,String name,String manufacturerId,String manufacturerName,String deviceType,String model,String protocolType,String appIds,String device){
		String result = "";
        // Two-Way Authentication
        try {
			HttpsUtil httpsUtil = new HttpsUtil();
			httpsUtil.initSSLConfigForTwoWay();
			String appId = "";
			String secret = "";
			
			switch(device){
			case "23"://�պ�
				appId = Constant.RHAPPID;
				secret = Constant.RHSECRET;
				break;
			case "61"://�ε��̸�
				appId = Constant.JDAPPID;
				secret = Constant.JDSECRET;
				break;
			case "73"://ȼ��7020
				appId = Constant.RQAPPID7;
				secret = Constant.RQSECRET7;
				break;
			case "75"://���ݵ���
			case "77"://�Ͼ��������
				appId = Constant.DQAPPID1;
				secret = Constant.DQSECRET1;
				break;
			case "78"://�Ͼ���ͨˮѹ
				appId = Constant.PTSYAPPID;
				secret = Constant.PTSYSECRET;
				break;
			case "79"://�Ͼ���ͨ��ʪ��
				appId = Constant.PTSYAPPID;
				secret = Constant.PTSYSECRET;
				break;
			case "80"://�Ͼ�U�ص���
				appId = Constant.UTAPPID;
				secret = Constant.UTSECRET;
				break;
			default:
				appId = Constant.APPID;
				secret = Constant.SECRET;
				break;
			}
			
			String accessToken = Authentication.getAccessToken(appId, secret);


			//please replace the deviceId, when you use the demo.
//			String deviceId = "f190281e-0c57-40fd-81fe-f2e881480899";
			String urlModifyDeviceInfo = Constant.MODIFY_DEVICE_INFO + "/" + deviceId+"?appId="+appId;

			//please replace the following parameter values, when you use the demo.
			//And those parameter values must be consistent with the content of profile that have been preset to IoT platform.
			//The following parameter values of this demo are use the watermeter profile that already initialized to IoT platform.
			if(StringUtils.isBlank(manufacturerId)){
				manufacturerId= "hanrun2018";//����ID
			}
			if(StringUtils.isBlank(manufacturerName)){
				manufacturerName = "hanrun2018";//��������
			}
			if(StringUtils.isBlank(deviceType)){
				deviceType = "FuelGas";		//�豸����
			}
			if(StringUtils.isBlank(model)){
				model = "180629";			//�豸�ͺ�
			}
			if(StringUtils.isBlank(protocolType)){
				protocolType = "CoAP";		//Э������
			}

			Map<String, Object> paramModifyDeviceInfo = new HashMap<>();
			paramModifyDeviceInfo.put("manufacturerId", manufacturerId);
			paramModifyDeviceInfo.put("manufacturerName", manufacturerName);
			paramModifyDeviceInfo.put("deviceType", deviceType);
			paramModifyDeviceInfo.put("model", model);
			paramModifyDeviceInfo.put("protocolType", protocolType);
			paramModifyDeviceInfo.put("name", name);

			
			String jsonRequest = JsonUtil.jsonObj2Sting(paramModifyDeviceInfo);

			Map<String, String> header = new HashMap<>();
			header.put(Constant.HEADER_APP_KEY, appId);
			header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);

			StreamClosedHttpResponse responseModifyDeviceInfo = httpsUtil.doPutJsonGetStatusLine(urlModifyDeviceInfo,
			        header, jsonRequest);

			System.out.println("ModifyDeviceInfo, response content:");
			System.out.println(responseModifyDeviceInfo.getStatusLine());
			System.out.println(responseModifyDeviceInfo.getContent());
			System.out.println();
			result = responseModifyDeviceInfo.getContent();
		} catch (Exception e) {
			result = "�޸ĳ���";
			e.printStackTrace();
		}
        return result;
    }

}