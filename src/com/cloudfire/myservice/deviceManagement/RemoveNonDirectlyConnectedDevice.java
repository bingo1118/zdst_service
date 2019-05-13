package com.cloudfire.myservice.deviceManagement;

import com.cloudfire.myservice.appAccessSecurity.Authentication;
import com.cloudfire.until.Constant;
import com.cloudfire.until.HttpsUtil;
import com.cloudfire.until.JsonUtil;
import com.cloudfire.until.StreamClosedHttpResponse;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Remove Indirect Device :
 * This interface is used to Remove Indirect device.
 */
public class RemoveNonDirectlyConnectedDevice {

    public static void main(String args[]) throws Exception {

        // Two-Way Authentication
        HttpsUtil httpsUtil = new HttpsUtil();
        httpsUtil.initSSLConfigForTwoWay();

        // Authentication，get token
//        String accessToken = login(httpsUtil);

        //Please make sure that the following parameter values have been modified in the Constant file.
        String appId = "89ROeDVLwgoYFzMVTKAcZCHz1Bga";
        String secret = "";
		if(StringUtils.isBlank(appId)){//注册到我们自己的账号
			appId = Constant.APPID;
			secret = Constant.SECRET;
		}else if(StringUtils.isNotBlank(appId)&&appId.equals("89ROeDVLwgoYFzMVTKAcZCHz1Bga")){//注册到日海账号设备
			secret = "j4Ss41R345uYGqfAqglxteHwnyEa";
		}
		String accessToken = Authentication.getAccessToken(appId, secret);
        String deviceId = "eb790102-9282-434d-b5da-7ca437232b25";

        //please replace the following parameter values, when you use the demo.
        //And those parameter values must be consistent with the content of profile that have been preset to IoT platform.
        String serviceId = "Remover";
        String mode = "ACK";
        String method = "REMOVE";
        String toType = "GATEWAY";

        //please replace the callbackURL, when you use the demo.
        String callbackURL = "http://server:port/na/iocm/message/confirm";

        String urlRemoveIndirectDevice = Constant.REMOVE_INDIRECT_DEVICE;
        urlRemoveIndirectDevice =String.format(urlRemoveIndirectDevice, deviceId, serviceId);

        Map<String, String> commandNA2CloudHeader = new HashMap<>();
        commandNA2CloudHeader.put("mode", mode);
        commandNA2CloudHeader.put("method", method);
        commandNA2CloudHeader.put("toType", toType);
        commandNA2CloudHeader.put("callbackURL", callbackURL);

        Map<String, String> commandNA2CloudBody = new HashMap<>();
        commandNA2CloudBody.put("cmdBody", "remove indirect device cmd body content.");

        Map<String, Object> paramCommandNA2Cloud = new HashMap<>();
        paramCommandNA2Cloud.put("header", commandNA2CloudHeader);
        paramCommandNA2Cloud.put("body", commandNA2CloudBody);

        Map<String, String> header = new HashMap<>();
        header.put(Constant.HEADER_APP_KEY, appId);
        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);

        String jsonRequest = JsonUtil.jsonObj2Sting(paramCommandNA2Cloud);

        StreamClosedHttpResponse responseRemoveIndirectDevice = httpsUtil
                .doPostJsonGetStatusLine(urlRemoveIndirectDevice, header, jsonRequest);


        System.out.println("RemoveNonDirectlyConnectedDevice, response content:");
        System.out.print(responseRemoveIndirectDevice.getStatusLine());
        System.out.println(responseRemoveIndirectDevice.getContent());
        System.out.println();

    }

    /**
     * Authentication，get token
     * */
    @SuppressWarnings("unchecked")
    public static String login(HttpsUtil httpsUtil) throws Exception {

        String appId = Constant.APPID;
        String secret = Constant.SECRET;
        String urlLogin = Constant.APP_AUTH;

        Map<String, String> paramLogin = new HashMap<>();
        paramLogin.put("appId", appId);
        paramLogin.put("secret", secret);

        StreamClosedHttpResponse responseLogin = httpsUtil.doPostFormUrlEncodedGetStatusLine(urlLogin, paramLogin);

        System.out.println("app auth success,return accessToken:");
        System.out.print(responseLogin.getStatusLine());
        System.out.println(responseLogin.getContent());
        System.out.println();

        Map<String, String> data = new HashMap<>();
        data = JsonUtil.jsonString2SimpleObj(responseLogin.getContent(), data.getClass());
        return data.get("accessToken");
    }

}