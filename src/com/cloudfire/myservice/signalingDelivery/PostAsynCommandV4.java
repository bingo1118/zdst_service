/*
 * File Name: com.huawei.service.signalingDelivery.PostAsynCommand.java
 *
 * Copyright Notice:
 *      Copyright  1998-2008, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.cloudfire.myservice.signalingDelivery;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.cloudfire.until.Constant;
import com.cloudfire.until.HttpsUtil;
import com.cloudfire.until.JsonUtil;
import com.cloudfire.until.StreamClosedHttpResponse;

/**
 * Post Asynchronous Command to a specify device:
 * This interface is used by NAs to send messages to device.
 * If a device is not online,
 * the IoT platform buffers the message and delivers the message to the device after the device is online.
 * The NA can set the maximum buffering time.
 */
public class PostAsynCommandV4 {

    public static void main(String[] args) throws Exception {
        /*
         * the device must connect to IoT platform before na post asyn command to device
         */
//      {
//      "_class" : "com.huawei.iom.iocm.domain.nsse.NsseRoute",
//      "deviceId" : "8c23b6b4-ea68-48fb-9c2f-90452a81ebb1",
//      "appId" : "pAw9x9zinQnQkYSLWbiGI_O6iBUa",
//      "nsseId" : "http://185.11.1.43:8096",
//      "edgeGwId" : "MeterCig",
//      "edgeGwType" : "CoAP",
//      "status" : "ONLINE"
//  }

        // Two-Way Authentication
        HttpsUtil httpsUtil = new HttpsUtil();
        httpsUtil.initSSLConfigForTwoWay();

        // Authentication，get token
        String accessToken = login(httpsUtil);

        //Please make sure that the following parameter values have been modified in the Constant file.
        String urlPostAsynCmd = Constant.POST_ASYN_CMD;
        String appId = Constant.APPID;

        //please replace the deviceId, when you use the demo.
        String deviceId = "8c23b6b4-ea68-48fb-9c2f-90452a81ebb1";
        String callbackUrl = Constant.REPORT_CMD_EXEC_RESULT_CALLBACK_URL;

        //please replace the following parameter values, when you use the demo.
        //And those parameter values must be consistent with the content of profile that have been preset to IoT platform.
        //The following parameter values of this demo are use the watermeter profile that already initialized to IoT platform.
        String serviceId = "WaterMeter";
        String method = "SET_TEMPERATURE_READ_PERIOD";
        ObjectNode paras = JsonUtil.convertObject2ObjectNode("{\"value\":\"12\"}");
      
        Map<String, Object> paramCommand = new HashMap<>();
        paramCommand.put("serviceId", serviceId);
        paramCommand.put("method", method);
        paramCommand.put("paras", paras);      
        
        Map<String, Object> paramPostAsynCmd = new HashMap<>();
        paramPostAsynCmd.put("deviceId", deviceId);
        paramPostAsynCmd.put("command", paramCommand);
        paramPostAsynCmd.put("callbackUrl", callbackUrl);
        
        String jsonRequest = JsonUtil.jsonObj2Sting(paramPostAsynCmd);
                
        Map<String, String> header = new HashMap<>();
        header.put(Constant.HEADER_APP_KEY, appId);
        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
        
        HttpResponse responsePostAsynCmd = httpsUtil.doPostJson(urlPostAsynCmd, header, jsonRequest);

        String responseBody = httpsUtil.getHttpResponseBody(responsePostAsynCmd);

        System.out.println("PostAsynCommand, response content:");
        System.out.print(responsePostAsynCmd.getStatusLine());
        System.out.println(responseBody);
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
