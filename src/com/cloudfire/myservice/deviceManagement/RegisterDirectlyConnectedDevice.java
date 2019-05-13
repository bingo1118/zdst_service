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
			String manufacturerId = "hanrun2018";	//产商ID
			String manufacturerName = "hanrun2018";//产商名称
			String deviceType = "FuelGas";			//设备类型
			String protocolType = "CoAP";			//协议类型
			String model = "180629";				//设备型号
			String name = verifyCode;				//设备名称
			if(StringUtils.isBlank(appId)){//注册到我们自己的账号
				appId = Constant.APPID;
				secret = Constant.SECRET;
				manufacturerId = "hanrun2018";	//产商ID
				manufacturerName = "hanrun2018";//产商名称
				deviceType = "FuelGas";			//设备类型
				protocolType = "CoAP";			//协议类型
				model = "180629";				//设备型号
			}else if(StringUtils.isNotBlank(appId)&&appId.equals("89ROeDVLwgoYFzMVTKAcZCHz1Bga")){//注册到日海账号设备
				secret = "j4Ss41R345uYGqfAqglxteHwnyEa";
				manufacturerId = "20180709";	//产商ID
				manufacturerName = "FuelGas";	//产商名称
				deviceType = "FuelGas";			//设备类型
				protocolType = "CoAP";			//协议类型
				model = "RiHai2018";			//设备型号
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
			result = "注册失败";
			System.out.println("注册失败");
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
			String manufacturerId = "hanrun2018";	//产商ID
			String manufacturerName = "hanrun2018";//产商名称 
			String deviceType = "FuelGas";			//设备类型
			String protocolType = "CoAP";			//协议类型
			String model = "180629";				//设备型号
			String name = verifyCode;				//设备名称
			switch(deviceId){		//嘉德南京烟感
				case "61":
					appId = Constant.JDAPPID;
					secret = Constant.JDSECRET;
					manufacturerId = "SHDXWLNBIot";	//产商ID
					manufacturerName = "Wulian";	//产商名称
					deviceType = "Smoke";			//设备类型
					protocolType = "CoAP";			//协议类型
					model = "WLNBIotSmoke";			//设备型号
					break;
				case "73":
					appId = Constant.RQAPPID7;
					secret = Constant.RQSECRET7;
					manufacturerId = "GZHR180731";	//产商ID
					manufacturerName = "GuangZhouHanRun";	//产商名称
					deviceType = "NBHRGas";			//设备类型
					protocolType = "CoAP";			//协议类型
					model = "NBGas180731";			//设备型号
					break;
				case "75":
					appId = Constant.DQAPPID1;
					secret = Constant.DQSECRET1;
					manufacturerId = "ZD190214";	//产商ID
					manufacturerName = "ZD";	//产商名称
					deviceType = "NBelectric220V";			//设备类型
					protocolType = "CoAP";			//协议类型
					model = "220VElectric190214";			//设备型号
					break;
				case "77":
					appId = Constant.DQAPPID1;
					secret = Constant.DQSECRET1;
					manufacturerId = "GZHR180918";	//产商ID
					manufacturerName = "GuangZhouHanRun";	//产商名称
					deviceType = "NBElectric";			//设备类型
					protocolType = "CoAP";			//协议类型
					model = "NBHRElectric380V";			//设备型号
					System.out.println("7777777777777777"+appId+"   ===   "+secret);
					break;
				case "78":
				case "79":
					appId = Constant.PTSYAPPID;
					secret = Constant.PTSYSECRET;
					manufacturerId = "hrsst1102";	//产商ID
					manufacturerName = "hrsst";	//产商名称
					deviceType = "WaterMeter";			//设备类型
					protocolType = "CoAP";			//协议类型
					model = "20181102";			//设备型号
					System.out.println("7777777777777777"+appId+"   ===   "+secret);
					break;
				case "80"://U特电气
					appId = Constant.UTAPPID;
					secret = Constant.UTSECRET;
					manufacturerId = "GZHR181126";	//产商ID
					manufacturerName = "GuangZhouHanRun";	//产商名称
					deviceType = "NBElectricYouTe";			//设备类型
					protocolType = "CoAP";			//协议类型
					model = "NBElectricYouTe181126";			//设备型号
					System.out.println("NBElectricYouTe181126："+appId+"   ===   "+secret);
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
			result = "注册失败";
			System.out.println("注册失败");
			e.printStackTrace();
		}
		return result;
    }

}
