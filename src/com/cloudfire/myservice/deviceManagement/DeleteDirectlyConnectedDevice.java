package com.cloudfire.myservice.deviceManagement;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.myservice.appAccessSecurity.Authentication;
import com.cloudfire.until.Constant;
import com.cloudfire.until.HttpsUtil;
import com.cloudfire.until.JsonUtil;
import com.cloudfire.until.StreamClosedHttpResponse;
import com.sun.javaws.Main;

/**
 * Delete Directly Connected Device :
 * This interface is used to delete a device.
 */
public class DeleteDirectlyConnectedDevice {

	
	public static void main(String[] args) {
		String deviceId = "485c9034-1830-4f73-927b-c11818e00a82";
		deleteByDeviceId(deviceId, "89ROeDVLwgoYFzMVTKAcZCHz1Bga");
	}

	@SuppressWarnings("unchecked")
	public static String deleteByDeviceId(String deviceId,String appId){
		String result = "";
		// Two-Way Auth entication
		HttpsUtil httpsUtil = new HttpsUtil();
		try {
			httpsUtil.initSSLConfigForTwoWay();

			//Please make sure that the following parameter values have been modified in the Constant file.
			String secret = "";
			if(StringUtils.isBlank(appId)){//注册到我们自己的账号
				appId = Constant.APPID;
				secret = Constant.SECRET;
			}else if(StringUtils.isNotBlank(appId)&&appId.equals("89ROeDVLwgoYFzMVTKAcZCHz1Bga")){//注册到日海账号设备
				secret = "j4Ss41R345uYGqfAqglxteHwnyEa";
			}else if(StringUtils.isNotBlank(appId)&&appId.equals("dKQfWWBDv_1kfpLzb1gJKlBZjIUa")){//注册到我们自己
				secret = "IOotgcjky7LO0KQeCfs6I96R4JUa";
			}
			String accessToken = Authentication.getAccessToken(appId, secret);

			//please replace the deviceId, when you use the demo.
			String urlDelete = Constant.DELETE_DEVICES + "/" +deviceId+"?appId="+appId+"&cascade="+false;
			        
			Map<String, String> header = new HashMap<>();
			header.put(Constant.HEADER_APP_KEY, appId);
			header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
			
			StreamClosedHttpResponse responseDelete = httpsUtil.doDeleteGetStatusLine(urlDelete, header);

			System.out.println("DeleteDirectlyConnectedDevice, response content:");
			System.out.print(responseDelete.getStatusLine());
			System.out.println(responseDelete.getContent());
			System.out.println();
			
			Map<String, String> data = new HashMap<>();
	        data = JsonUtil.jsonString2SimpleObj(responseDelete.getContent(), data.getClass());
	        result = data.get("error_code");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
    }
	
	@SuppressWarnings("unchecked")
	public static int deleteByDeviceId(String deviceId,String appId,int devid){
		int result = 0;
		// Two-Way Auth entication
		HttpsUtil httpsUtil = new HttpsUtil();
		try {
			httpsUtil.initSSLConfigForTwoWay();

			//Please make sure that the following parameter values have been modified in the Constant file.
			String secret = "";
			/*if(StringUtils.isBlank(appId)){//注册到我们自己的账号
				appId = Constant.APPID;
				secret = Constant.SECRET;
			}else if(StringUtils.isNotBlank(appId)&&appId.equals("89ROeDVLwgoYFzMVTKAcZCHz1Bga")){//注册到日海账号设备
				secret = "j4Ss41R345uYGqfAqglxteHwnyEa";
			}else if(StringUtils.isNotBlank(appId)&&appId.equals("dKQfWWBDv_1kfpLzb1gJKlBZjIUa")){//注册到我们自己
				secret = "IOotgcjky7LO0KQeCfs6I96R4JUa";
			}*/
			switch(devid){
				case 23:	//日海
					appId = Constant.RHAPPID;
					secret = Constant.RHSECRET;
					break;
				case 61:	//嘉德烟感
					appId = Constant.JDAPPID;
					secret = Constant.JDSECRET;
					break;
				case 75:	//贵州电气
				case 77:
					appId = Constant.DQAPPID1;
					secret = Constant.DQSECRET1;
					break;
				case 78:
				case 79:	//燃气，温湿度
					appId = Constant.PTSYAPPID;
					secret = Constant.PTSYSECRET;
				default:	//我们燃气
					appId = Constant.APPID;
					secret = Constant.SECRET;
					break;
			}
			String accessToken = Authentication.getAccessToken(appId, secret);

			//please replace the deviceId, when you use the demo.
			String urlDelete = Constant.DELETE_DEVICES + "/" +deviceId+"?appId="+appId+"&cascade="+false;
			        
			Map<String, String> header = new HashMap<>();
			header.put(Constant.HEADER_APP_KEY, appId);
			header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
			
			StreamClosedHttpResponse responseDelete = httpsUtil.doDeleteGetStatusLine(urlDelete, header);

			System.out.println("DeleteDirectlyConnectedDevice, response content:");
//			System.out.print(responseDelete.getStatusLine().getStatusCode());
//			System.out.println(responseDelete.getContent());
//			System.out.println();
			
//			Map<String, String> data = new HashMap<>();
//	        data = JsonUtil.jsonString2SimpleObj(responseDelete.getContent(), data.getClass());
	        result = responseDelete.getStatusLine().getStatusCode();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
    }


}