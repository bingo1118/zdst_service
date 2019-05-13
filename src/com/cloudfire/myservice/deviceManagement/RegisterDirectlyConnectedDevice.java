package com.cloudfire.myservice.deviceManagement;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.ToolNanJinDao;
import com.cloudfire.dao.impl.ToolNanJinDaoImpl;
import com.cloudfire.myservice.appAccessSecurity.Authentication;
import com.cloudfire.until.Constant;
import com.cloudfire.until.HttpsUtil;
import com.cloudfire.until.JsonUtil;
import com.cloudfire.until.StreamClosedHttpResponse;

/**
 * Register Directly Connected Device :
 * This interface is used by NAs to register devices on the IoT platform.
 * After the registration is successful,
 * the IoT platform allocates a device ID for the device,which is used as the unique identifier of the device.
 * Unregistered devices are not allowed to access the IoT platform.
 * In NB-IoT scenarios, the Set device info interface needs to be invoked to set device information after the registration is successful.
 */
public class RegisterDirectlyConnectedDevice {

	@SuppressWarnings("unchecked")
	public static String registerDevice(String appId,String imei) {
		String result = "";
        // Two-Way Authentication
        try {
        	System.out.println("RegisterDirectlyConnectedDevice---registerDevice");
			HttpsUtil httpsUtil = new HttpsUtil();
			httpsUtil.initSSLConfigForTwoWay();
			String secret = "";
			String verifyCode = imei;
			String nodeId = verifyCode;
			Integer timeout = 0;
			String manufacturerId = "hanrun2018";	//����ID
			String manufacturerName = "hanrun2018";//��������
			String deviceType = "FuelGas";			//�豸����
			String protocolType = "CoAP";			//Э������
			String model = "180629";				//�豸�ͺ�
			String name = verifyCode;				//�豸����
			if(StringUtils.isBlank(appId)){//ע�ᵽ�����Լ����˺�
				appId = Constant.APPID;
				secret = Constant.SECRET;
				manufacturerId = "hanrun2018";	//����ID
				manufacturerName = "hanrun2018";//��������
				deviceType = "FuelGas";			//�豸����
				protocolType = "CoAP";			//Э������
				model = "180629";				//�豸�ͺ�
			}else if(StringUtils.isNotBlank(appId)&&appId.equals("89ROeDVLwgoYFzMVTKAcZCHz1Bga")){//ע�ᵽ�պ��˺��豸
				secret = "j4Ss41R345uYGqfAqglxteHwnyEa";
				manufacturerId = "20180709";	//����ID
				manufacturerName = "FuelGas";	//��������
				deviceType = "FuelGas";			//�豸����
				protocolType = "CoAP";			//Э������
				model = "RiHai2018";			//�豸�ͺ�
			}
			String accessToken = Authentication.getAccessToken(appId, secret);

			//Please make sure that the following parameter values have been modified in the Constant file.
			String urlReg = Constant.REGISTER_DEVICE+"?appId="+appId;

			//please replace the verifyCode and nodeId and timeout, when you use the demo.

			Map<String, Object> paramReg = new HashMap<>();
			paramReg.put("verifyCode", verifyCode.toUpperCase());
			paramReg.put("nodeId", nodeId.toUpperCase());
			paramReg.put("timeout", timeout);
			
//			Map<String,Object> deviceInfo = new HashMap<>();
//			deviceInfo.put("manufacturerId", manufacturerId);
//			deviceInfo.put("manufacturerName", manufacturerName);
//			deviceInfo.put("protocolType", protocolType);
//			deviceInfo.put("deviceType", deviceType);
//			deviceInfo.put("model", model);
//			deviceInfo.put("name", name);
			
//			Object[] obj = new Object[1];
//			Map<String,Object> customFields = new HashMap<>();
//			customFields.put("fieldName", nodeId);
//			customFields.put("fieldValue", nodeId);
//			customFields.put("name", nodeId);
//			obj[0] = customFields;
			
//			paramReg.put("deviceInfo", deviceInfo);
//			paramReg.put("customFields", obj);
			
			String jsonRequest = JsonUtil.jsonObj2Sting(paramReg);

			Map<String, String> header = new HashMap<>();
			header.put(Constant.HEADER_APP_KEY, appId);
			header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);

			StreamClosedHttpResponse responseReg = httpsUtil.doPostJsonGetStatusLine(urlReg, header, jsonRequest);
			System.out.println("RegisterDirectlyConnectedDevice, response content:");
			System.out.print(responseReg.getStatusLine());
			System.out.println(responseReg.getContent());
			System.out.println();
			
			Map<String, String> data = new HashMap<>();
			data = JsonUtil.jsonString2SimpleObj(responseReg.getContent(), data.getClass());
			result = data.get("resultcode");
			if(StringUtils.isBlank(result)){
				ToolNanJinDao td = new ToolNanJinDaoImpl();
				td.addImeiDeviceId(imei, data.get("deviceId"));
				ModifyDeviceInfo.ModifyDevice(data.get("deviceId"), name, manufacturerId, manufacturerName, deviceType, model, protocolType,appId);
			}
		} catch (Exception e) {
			result = "ע��ʧ��";
			System.out.println("ע��ʧ��");
			e.printStackTrace();
		}
		return result;
    }
	public static String registerDevice(String appId,String imei,String deviceId) {
		String result = "";
        // Two-Way Authentication
        try {
        	System.out.println("RegisterDirectlyConnectedDevice---registerDevice");
			HttpsUtil httpsUtil = new HttpsUtil();
			httpsUtil.initSSLConfigForTwoWay();
			String secret = "";
			String verifyCode = imei;
			String nodeId = verifyCode;
			Integer timeout = 0;
			String manufacturerId = "hanrun2018";	//����ID
			String manufacturerName = "hanrun2018";//�������� 
			String deviceType = "FuelGas";			//�豸����
			String protocolType = "CoAP";			//Э������
			String model = "180629";				//�豸�ͺ�
			String name = verifyCode;				//�豸����
			switch(deviceId){		//�ε��Ͼ��̸�
				case "61":
					appId = Constant.JDAPPID;
					secret = Constant.JDSECRET;
					manufacturerId = "SHDXWLNBIot";	//����ID
					manufacturerName = "Wulian";	//��������
					deviceType = "Smoke";			//�豸����
					protocolType = "CoAP";			//Э������
					model = "WLNBIotSmoke";			//�豸�ͺ�
					break;
				case "73":
					appId = Constant.RQAPPID7;
					secret = Constant.RQSECRET7;
					manufacturerId = "GZHR180731";	//����ID
					manufacturerName = "GuangZhouHanRun";	//��������
					deviceType = "NBHRGas";			//�豸����
					protocolType = "CoAP";			//Э������
					model = "NBGas180731";			//�豸�ͺ�
					break;
				case "75":
					appId = Constant.DQAPPID1;
					secret = Constant.DQSECRET1;
					manufacturerId = "ZD190214";	//����ID
					manufacturerName = "ZD";	//��������
					deviceType = "NBelectric220V";			//�豸����
					protocolType = "CoAP";			//Э������
					model = "220VElectric190214";			//�豸�ͺ�
					break;
				case "77":
					appId = Constant.DQAPPID1;
					secret = Constant.DQSECRET1;
					manufacturerId = "GZHR180918";	//����ID
					manufacturerName = "GuangZhouHanRun";	//��������
					deviceType = "NBElectric";			//�豸����
					protocolType = "CoAP";			//Э������
					model = "NBHRElectric380V";			//�豸�ͺ�
					System.out.println("7777777777777777"+appId+"   ===   "+secret);
					break;
				case "78":
				case "79":
					appId = Constant.PTSYAPPID;
					secret = Constant.PTSYSECRET;
					manufacturerId = "hrsst1102";	//����ID
					manufacturerName = "hrsst";	//��������
					deviceType = "WaterMeter";			//�豸����
					protocolType = "CoAP";			//Э������
					model = "20181102";			//�豸�ͺ�
					System.out.println("7777777777777777"+appId+"   ===   "+secret);
					break;
				case "80"://U�ص���
					appId = Constant.UTAPPID;
					secret = Constant.UTSECRET;
					manufacturerId = "GZHR181126";	//����ID
					manufacturerName = "GuangZhouHanRun";	//��������
					deviceType = "NBElectricYouTe";			//�豸����
					protocolType = "CoAP";			//Э������
					model = "NBElectricYouTe181126";			//�豸�ͺ�
					System.out.println("NBElectricYouTe181126��"+appId+"   ===   "+secret);
					break;
			}
			
			String accessToken = Authentication.getAccessToken(appId, secret);
			System.out.println("accessToken ============== " + accessToken);
			//Please make sure that the following parameter values have been modified in the Constant file.
			String urlReg = Constant.REGISTER_DEVICE+"?appId="+appId;

			//please replace the verifyCode and nodeId and timeout, when you use the demo.

			Map<String, Object> paramReg = new HashMap<>();
			paramReg.put("verifyCode", verifyCode.toUpperCase());
			paramReg.put("nodeId", nodeId.toUpperCase());
			paramReg.put("timeout", timeout);
			
			String jsonRequest = JsonUtil.jsonObj2Sting(paramReg);

			Map<String, String> header = new HashMap<>();
			header.put(Constant.HEADER_APP_KEY, appId);
			header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);

			StreamClosedHttpResponse responseReg = httpsUtil.doPostJsonGetStatusLine(urlReg, header, jsonRequest);
			System.out.println(urlReg+"RegisterDirectlyConnectedDevice, response content:"+responseReg.getStatusLine().getStatusCode());
			int resultCode = responseReg.getStatusLine().getStatusCode();
			Map<String, String> data = new HashMap<>();
			data = JsonUtil.jsonString2SimpleObj(responseReg.getContent(), data.getClass());
			result = data.get("resultcode");
			if(resultCode==200){
				ToolNanJinDao td = new ToolNanJinDaoImpl();
				td.addImeiDeviceId(imei, data.get("deviceId"));
				ModifyDeviceInfo.ModifyDevice(data.get("deviceId"), name, manufacturerId, manufacturerName, deviceType, model, protocolType,appId,deviceId);
			}
		} catch (Exception e) {
			result = "ע��ʧ��";
			System.out.println("ע��ʧ��");
			e.printStackTrace();
		}
		return result;
    }

}
